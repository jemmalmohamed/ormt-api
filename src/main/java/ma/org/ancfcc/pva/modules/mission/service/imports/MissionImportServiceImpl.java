package ma.org.ancfcc.pva.modules.mission.service.imports;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.log4j.Log4j2;
import ma.org.ancfcc.pva.core.commun.base.service.BaseServiceImpl;
import ma.org.ancfcc.pva.core.commun.base.service.SpecificationService;
import ma.org.ancfcc.pva.core.commun.rest.responses.MessageResponse;
import ma.org.ancfcc.pva.core.exceptions.handlers.ShapefileProcessingException;
import ma.org.ancfcc.pva.core.gis.shapefile.ShpFileService;
import ma.org.ancfcc.pva.core.gis.shapefile.ShpSimpleFeatureService;
import ma.org.ancfcc.pva.core.gis.utils.GeometryConversion;
import ma.org.ancfcc.pva.core.gis.utils.GeometryUtils;
import ma.org.ancfcc.pva.core.utilities.DateUtils;
import ma.org.ancfcc.pva.core.utilities.FileUtils;
import ma.org.ancfcc.pva.core.utilities.StringHelper;
import ma.org.ancfcc.pva.modules.capteur.Capteur;
import ma.org.ancfcc.pva.modules.capteur.enums.CapteurName;
import ma.org.ancfcc.pva.modules.capteur.enums.CapteurSubName;
import ma.org.ancfcc.pva.modules.capteur.service.CapteurService;
import ma.org.ancfcc.pva.modules.mission.Mission;
import ma.org.ancfcc.pva.modules.mission.repository.MissionRepository;
import ma.org.ancfcc.pva.modules.objet.Objet;
import ma.org.ancfcc.pva.modules.objet.service.ObjetService;
import ma.org.ancfcc.pva.modules.organisme.Organisme;
import ma.org.ancfcc.pva.modules.organisme.service.OrganismeService;
import ma.org.ancfcc.pva.modules.planaction.PlanAction;
import ma.org.ancfcc.pva.modules.planaction.service.PlanActionService;

@Log4j2
@Service
public class MissionImportServiceImpl extends BaseServiceImpl<Mission>
        implements MissionImportService {

    @Value("${geoserver.workspace}")
    private String gsWorkspace;

    @Value("${geoserver.postgis-datastore}")
    private String gsPostgisDatastore;

    @Autowired
    private MissionRepository missionRepository;

    @Autowired
    private CapteurService capteurService;
    @Autowired
    private PlanActionService planActionService;
    @Autowired
    private OrganismeService organismeService;

    @Autowired
    private ObjetService objetService;

    public MissionImportServiceImpl(MissionRepository missionRepository,
            SpecificationService specificationService) {
        super(missionRepository, specificationService);
    }

    @Override
    // @Transactional
    public void importMissionFromShapefile(List<File> shapefileComponents, Integer srid) throws IOException {

        File prjFile = FileUtils.getFileByExtFromFileListComponents(shapefileComponents, "prj");

        ShpFileService.validatePrjFile(prjFile, srid);

        File shpFile = FileUtils.getFileByExtFromFileListComponents(shapefileComponents, "shp");

        SimpleFeatureCollection featureCollection = ShpFileService.getFeatureCollectionFromShapefile(shpFile);

        try (SimpleFeatureIterator features = featureCollection.features()) {
            while (features.hasNext()) {
                SimpleFeature feature = features.next();
                try {

                    Mission mission = createMissionShapefileFromSimpleFeature(feature, srid);

                    if (missionRepository.existsByCode(mission.getCode())) {
                        log.info("Mission with code: " + mission.getCode() + " already exists");

                    } else {
                        create(mission);

                    }
                } catch (Exception e) {
                    log.error("Error creating mission from shapefile", e);
                    String message = MessageResponse.builder()
                            .title("Invalide Shapefile")
                            .mainMessage("Le shapefile ne contient pas les attributs d'une mission")
                            .subMessage("Veuillez vérifier les attributs de la mission")
                            .build()
                            .format();
                    throw new ShapefileProcessingException(message);
                }

            }

        }

    }

    private Mission createMissionShapefileFromSimpleFeature(SimpleFeature feature, Integer srid) {
        log.info("Creating mission from shapefile");

        Mission mission = new Mission();

        setMissionAttributs(feature, mission);
        setMissionObjets(feature, mission);
        setMissionSuperficie(feature, mission);
        setMissionDate(feature, mission);
        setMissionPLanAction(feature, mission);
        setMissionOrganisme(feature, mission);
        setMissionCapteurAttributs(feature, mission);
        setMissionGeometry(feature, mission, srid);

        return mission;
    }

    private void setMissionGeometry(SimpleFeature feature, Mission mission, Integer srid) {
        if (feature.getDefaultGeometry() instanceof Geometry) {
            Geometry geometry = (Geometry) feature.getDefaultGeometry();
            geometry = GeometryUtils.geometryIsPolygonOrMultiPolygon(geometry);
            geometry = GeometryConversion.convertTo2D(geometry);
            if (geometry instanceof MultiPolygon) {
                MultiPolygon delimitation = (MultiPolygon) geometry;
                delimitation.setSRID(srid);
                if (delimitation.isValid()) {
                    mission.setDelimitation(delimitation);
                } else {
                    throw new IllegalArgumentException(
                            "Delimitation de la mission " + mission.getCode() + " n'est pas valide");
                }

            }

        }

    }

    private void setMissionDate(SimpleFeature feature, Mission mission) {
        String dateAttributName = ShpSimpleFeatureService.findFeatureAttributIgnoreCase(feature, "date");
        String dateStr = StringHelper.getSafeString(feature.getAttribute(dateAttributName)); // Make sure this is the
        if (!dateStr.equals("")) {
            LocalDate date = DateUtils.parseVerboseToLocalDate(dateStr);
            mission.setDatePva(date);
        }
    }

    private void setMissionOrganisme(SimpleFeature feature, Mission mission) {
        Organisme organisme = organismeService.findByNom("ancfcc")
                .orElseThrow(() -> new EntityNotFoundException("Organisme not found"));
        mission.setOrganisme(organisme);
    }

    private void setMissionSuperficie(SimpleFeature feature, Mission mission) {
        String superficieAttributName = ShpSimpleFeatureService.findFeatureAttributIgnoreCase(feature, "superficie");
        String superficieString = StringHelper.getSafeString(feature.getAttribute(superficieAttributName));
        if (!superficieString.equals("")) {
            mission.setSuperficie(
                    Long.parseLong(StringHelper.getSafeString(feature.getAttribute(superficieAttributName))));
        }
    }

    private void setMissionPLanAction(SimpleFeature feature, Mission mission) {
        String exerciceAttributName = ShpSimpleFeatureService.findFeatureAttributIgnoreCase(feature, "exercice");
        String planActionName = StringHelper.getSafeString(feature.getAttribute(exerciceAttributName)).toLowerCase();

        PlanAction planAction = planActionService.findByNom(planActionName).orElseThrow(
                () -> new EntityNotFoundException("PlanAction not found"));
        mission.setPlanAction(planAction);
    }

    @Transactional
    private void setMissionObjets(SimpleFeature feature, Mission mission) {
        List<String> objetAttributNameList = ShpSimpleFeatureService.findFeatureListWithPatternIgnoreCase(feature,
                "objet");

        for (String objetAttributName : objetAttributNameList) {
            String objetName = StringHelper.getSafeString(feature.getAttribute(objetAttributName)).toLowerCase();
            if (objetName != null && !objetName.isEmpty()) {
                Objet objet = objetService.findByNom(objetName)
                        .orElseThrow(() -> new EntityNotFoundException("Objet not found: " + objetName));

                mission.getObjets().add(objet); // Add objet to the mission's objets collection

                // Add the mission to the objet's missions collection to maintain bidirectional
                // relationship
                // objet.getMissions().add(mission);
            }
        }
    }

    private void setMissionAttributs(SimpleFeature feature, Mission mission) {
        String codeAttributName = ShpSimpleFeatureService.findFeatureAttributIgnoreCase(feature, "code");
        String missionAttributName = ShpSimpleFeatureService.findFeatureAttributIgnoreCase(feature, "mission");
        mission.setCode(StringHelper.getSafeString(feature.getAttribute(codeAttributName)).toLowerCase());
        mission.setNom(StringHelper.getSafeString(feature.getAttribute(missionAttributName)).toLowerCase());
    }

    private Mission setMissionCapteurAttributs(SimpleFeature feature, Mission mission) {
        String capteurAttributName = ShpSimpleFeatureService.findFeatureAttributIgnoreCase(feature, "camera");
        String capteurName = StringHelper.getSafeString(feature.getAttribute(capteurAttributName)).toLowerCase();
        String gsdAttributName = ShpSimpleFeatureService.findFeatureAttributIgnoreCase(feature, "gsd");
        String gsdEchelle = StringHelper.getSafeString(feature.getAttribute(gsdAttributName));
        String capteurNom = getCapteurNameFromString(capteurName);

        Optional<Capteur> capteur = capteurService.findByNom(capteurNom);
        if (capteur.isPresent()) {
            String mode = capteur.get().getMode();
            switch (mode) {
                // case "analogique":
                // AnalogiqueAttribut analogiqueAttribut = new AnalogiqueAttribut();
                // analogiqueAttribut.setEchelle(Long.parseLong(gsdEchelle));
                // analogiqueAttribut.setMission(mission);
                // mission.setAnalogiqueAttributs(analogiqueAttribut);
                // break;
                // case "numérique":
                // NumeriqueAttribut numeriqueAttribut = new NumeriqueAttribut();
                // numeriqueAttribut.setResolution(Integer.parseInt(gsdEchelle));
                // numeriqueAttribut.setMission(mission);
                // mission.setNumeriqueAttributs(numeriqueAttribut);
                // break;
                // case "lidar":
                // LidarAttribut lidarAttribut = new LidarAttribut();
                // lidarAttribut.setDensite(Float.parseFloat(gsdEchelle));
                // lidarAttribut.setMission(mission);
                // mission.setLidarAttributs(lidarAttribut);
                default:
                    break;
            }
        } else {

            log.error("Capteur with name: " + capteurName + " not found");

        }
        return mission;
    }

    public String getCapteurNameFromString(String nom) {
        if (nom.contains(CapteurSubName.ADS.getDescription())) {
            return CapteurName.ADS40_80.getDescription();
        } else if (nom.contains(CapteurSubName.ALS.getDescription())) {
            return CapteurName.ALS70.getDescription();
        } else if (nom.contains(CapteurSubName.DMC.getDescription())) {
            return CapteurName.DMC_II_230.getDescription();
        } else if (nom.contains(CapteurSubName.RC30.getDescription())) {
            return CapteurName.RC30.getDescription();
        } else if (nom.contains(CapteurSubName.RMK.getDescription())) {
            return CapteurName.RMK_TOP_15.getDescription();
        } else {
            throw new EntityNotFoundException("Capteur with name '" + nom + "' not found");
        }
    }

}