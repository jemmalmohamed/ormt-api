package ma.org.ancfcc.pva.modules.mission.service.planification.xml;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ancfcc.pva.core.commun.rest.responses.MessageResponse;
import ma.org.ancfcc.pva.core.exceptions.handlers.XMLfileProcessingException;
import ma.org.ancfcc.pva.modules.capteur.Capteur;
import ma.org.ancfcc.pva.modules.capteur.enums.CapteurCode;
import ma.org.ancfcc.pva.modules.mission.bande.Bande;
import ma.org.ancfcc.pva.modules.mission.bande.service.BandeService;
import ma.org.ancfcc.pva.modules.mission.models.Mission;
import ma.org.ancfcc.pva.modules.mission.photo.planification.service.PhotoPlanificationService;
import ma.org.ancfcc.pva.modules.mission.service.planification.xml.parser.ListViewTable;
import ma.org.ancfcc.pva.modules.mission.service.planification.xml.parser.PlanEventInfo;
import ma.org.ancfcc.pva.modules.mission.service.planification.xml.parser.PlanLineXmlInfo;

@Log4j2
@Service
@RequiredArgsConstructor
public class UploadXmlPlanificationServiceImpl implements UploadXmlPlanificationService {

    private final BandeService bandeService;

    private final PhotoPlanificationService photoPlanificationService;

    @Override
    public void uploadPlanificationXmlFile(Mission mission, File file) throws JAXBException {

        ListViewTable listViewTable = parsePlanificationXml(file);
        String sensorType = listViewTable.getSensorType();

        checkMissionCapteur(sensorType, mission);

        if (sensorType.equals(CapteurCode.ADS40_80.getDescription())) {
            processPlanification(listViewTable, mission, this::createADS80Planification);

        }
        if (sensorType.equals(CapteurCode.DMC_II_230.getDescription())) {
            processPlanification(listViewTable, mission, this::createDMCII230Planification);
        }

    }

    public ListViewTable parsePlanificationXml(File xmlFile) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(ListViewTable.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return (ListViewTable) jaxbUnmarshaller.unmarshal(xmlFile);

    }

    private void processPlanification(ListViewTable listViewTable, Mission mission,
            BiConsumer<ListViewTable, Mission> planificationProcessor) {
        planificationProcessor.accept(listViewTable, mission);
    }

    private void createADS80Planification(ListViewTable listViewTable, Mission mission) {
        processPlanificationLines(listViewTable.extractPlanLineInfo(), mission, 4326);
    }

    private void createDMCII230Planification(ListViewTable listViewTable, Mission mission) {

        List<PlanLineXmlInfo> planLinesXml = listViewTable.extractPlanLineInfo();
        Map<String, List<PlanEventInfo>> planEventsGroup = listViewTable.extractPlanEventInfo();

        // Process plan lines in parallel using CompletableFuture
        List<CompletableFuture<Void>> futures = planLinesXml.stream()
                .map(planLineXml -> CompletableFuture.runAsync(() -> {
                    Bande bande = bandeService.saveBandePlanificationFromXml(planLineXml, mission, 4326);
                    List<PlanEventInfo> planEvents = planEventsGroup.get(planLineXml.getPlanLineLabel());
                    processPlanEvents(planEvents, bande, 4326);
                }))
                .collect(Collectors.toList());

        // Wait for all futures to complete
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Error processing planification", e);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void processPlanEvents(List<PlanEventInfo> planEventsListInfo, Bande bande, Integer srid) {
        photoPlanificationService.savePhotoPlanificationFromXml(planEventsListInfo, bande, srid);

    }

    private void processPlanificationLines(List<PlanLineXmlInfo> planLinesInfo, Mission mission, Integer srid) {
        for (PlanLineXmlInfo planLineInfo : planLinesInfo) {
            Optional<Bande> bande = bandeService.findBandeByLabelAndMissionId(planLineInfo.getPlanLineLabel(),
                    mission.getId());

            if (bande.isPresent()) {
                bandeService.delete(bande.get().getId());
            }

            bandeService.saveBandePlanificationFromXml(planLineInfo, mission, srid);

        }
    }

    private void checkMissionCapteur(String sensorType, Mission mission) {
        Capteur capteur = mission.getCapteur();
        if (!capteur.getCode().equals(sensorType)) {
            String message = MessageResponse.builder()
                    .title("Erreur traitement XML")
                    .mainMessage("Capteur de la mission + " + mission.getCode()
                            + " ne correspond pas au capteur du fichier XML")
                    .build().format();
            throw new XMLfileProcessingException(message);
        }
    }

}