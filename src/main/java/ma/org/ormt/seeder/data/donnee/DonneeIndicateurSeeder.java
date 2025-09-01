package ma.org.ormt.seeder.data.donnee;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.modules.indicateurs.donnee.dtos.request.DonneeIndicateurRequestDto;
import ma.org.ormt.modules.indicateurs.donnee.services.DonneeIndicateurService;
import ma.org.ormt.core.threads.ThreadService;
import ma.org.ormt.modules.indicateurs.dimension.dtos.DomaineCreateRequestDto.IndicateurCreateRequestDto.IndicateurDonneeRequestDto;
import ma.org.ormt.modules.indicateurs.dimension.models.Dimension;
import ma.org.ormt.modules.indicateurs.dimension.services.DimensionService;
import ma.org.ormt.modules.indicateurs.indicateur.models.Indicateur;
import ma.org.ormt.modules.indicateurs.indicateur.services.indicateur.IndicateurService;
import ma.org.ormt.modules.indicateurs.valeurdimension.dtos.request.ValeurDimensionRequestDto;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class DonneeIndicateurSeeder {
    private final IndicateurService indicateurService;
    private final DonneeIndicateurService donneeIndicateurService;
    private final DimensionService dimensionService;
    private final ObjectMapper objectMapper;

    public void createIndicateurDonnee(File file) {
        File parentFolder = file.getParentFile();
        File dataFolder = new File(parentFolder, "data");

        if (dataFolder.exists() && dataFolder.isDirectory()) {
            File[] dataFiles = dataFolder
                    .listFiles((dir, name) -> name.toLowerCase().endsWith(".json") && new File(dir, name).isFile());

            if (dataFiles != null && dataFiles.length > 0) {
                log.info("Found {} data JSON files in folder: {}", dataFiles.length, dataFolder.getAbsolutePath());

                for (File dataFile : dataFiles) {
                    try (InputStream dataInputStream = Files.newInputStream(dataFile.toPath())) {
                        log.info("Processing data file: {}", dataFile.getName());
                        IndicateurDonneeRequestDto dataIndicareur = objectMapper
                                .readValue(dataInputStream, IndicateurDonneeRequestDto.class);
                        proccessDonneeIndicateur(dataIndicareur);
                    } catch (Exception e) {
                        log.error("Failed to process data file {}: {}", dataFile.getName(), e.getMessage(), e);
                    }
                }
            } else {
                log.debug("No data JSON files found in folder: {}", dataFolder.getAbsolutePath());
            }
        } else {
            log.debug("Data folder does not exist or is not a directory: {}", dataFolder.getAbsolutePath());
        }
    }

    public void proccessDonneeIndicateur(
            IndicateurDonneeRequestDto dataIndicareur) {
        try {
            Indicateur indicateur = indicateurService.findByNom(dataIndicareur.getIndicateur().toLowerCase())
                    .orElseThrow(() -> new RuntimeException("Indicateur not found: " + dataIndicareur.getIndicateur()));

            List<Object> dataList = dataIndicareur.getData();
            log.info("Processing {} data entries for indicator: {}", dataList.size(), indicateur.getNom());

            int threadPoolSize = 20; // You can adjust this value as needed
            ThreadService.executeInThreadPool(threadPoolSize, dataList, dataItem -> {
                try {
                    JsonNode jsonNode = objectMapper.valueToTree(dataItem);
                    DonneeIndicateurRequestDto donneeRequest = new DonneeIndicateurRequestDto();
                    if (jsonNode.has("valeur")) {
                        donneeRequest.setValeur(jsonNode.get("valeur").asText());
                    } else {
                        log.warn("Data item for indicator {} doesn't have 'valeur' property, skipping",
                                indicateur.getNom());
                        return;
                    }
                    List<ValeurDimensionRequestDto> dimensionValues = new ArrayList<>();
                    Iterator<Map.Entry<String, JsonNode>> fields = jsonNode.fields();
                    while (fields.hasNext()) {
                        Map.Entry<String, JsonNode> field = fields.next();
                        String dimensionName = field.getKey();
                        if (!dimensionName.equals("valeur")) {
                            String dimensionValue = field.getValue().asText();
                            Dimension dimension = dimensionService.findByNom(dimensionName)
                                    .orElseThrow(() -> new RuntimeException("Dimension not found: " + dimensionName));
                            ValeurDimensionRequestDto valeurDimensionDto = new ValeurDimensionRequestDto();
                            valeurDimensionDto.setDimension(dimension);
                            valeurDimensionDto.setValeur(dimensionValue);
                            dimensionValues.add(valeurDimensionDto);
                        }
                    }
                    donneeRequest.setValeurDimensions(dimensionValues);
                    donneeIndicateurService.create(indicateur.getId(), donneeRequest);
                } catch (Exception e) {
                    log.error("Error processing data item for indicator {}: {}", indicateur.getNom(), e.getMessage(),
                            e);
                }
            });
            log.info("Successfully processed {} data entries for indicator: {}", dataList.size(), indicateur.getNom());
        } catch (Exception e) {
            log.error("Failed to process data for indicator {}: {}", dataIndicareur.getIndicateur(), e.getMessage(), e);
        }
    }
}
