package ma.org.ormt.modules.mission.service.imports;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.geotools.api.feature.simple.SimpleFeature;
import org.geotools.api.geometry.MismatchedDimensionException;
import org.geotools.api.referencing.FactoryException;
import org.geotools.api.referencing.operation.TransformException;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.MultiPolygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.core.commun.base.service.BaseServiceImpl;
import ma.org.ormt.core.commun.base.service.SpecificationService;
import ma.org.ormt.core.commun.rest.responses.MessageResponse;
import ma.org.ormt.core.exceptions.handlers.ShapefileProcessingException;
import ma.org.ormt.core.gis.crs.ITRFCrsTransformer;
import ma.org.ormt.core.gis.shapefile.ShpFileService;
import ma.org.ormt.core.gis.shapefile.ShpSimpleFeatureService;
import ma.org.ormt.core.utilities.DateUtils;
import ma.org.ormt.core.utilities.FileUtils;
import ma.org.ormt.core.utilities.StringHelper;
import ma.org.ormt.modules.capteur.Capteur;
import ma.org.ormt.modules.capteur.service.CapteurService;
import ma.org.ormt.modules.mission.models.AnalogiqueAttribut;
import ma.org.ormt.modules.mission.models.LidarAttribut;
import ma.org.ormt.modules.mission.models.Mission;
import ma.org.ormt.modules.mission.models.NumeriqueAttribut;
import ma.org.ormt.modules.mission.repository.MissionRepository;
import ma.org.ormt.modules.objet.Objet;
import ma.org.ormt.modules.objet.service.ObjetService;
import ma.org.ormt.modules.organisme.Organisme;
import ma.org.ormt.modules.organisme.service.OrganismeService;
import ma.org.ormt.modules.planaction.PlanAction;
import ma.org.ormt.modules.planaction.service.PlanActionService;

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

        ShpFileService.validatePrjFileIsWgs(prjFile, srid);

        File shpFile = FileUtils.getFileByExtFromFileListComponents(shapefileComponents, "shp");

        SimpleFeatureCollection featureCollection = ShpFileService.getFeatureCollectionFromShapefile(shpFile);

        try (SimpleFeatureIterator features = featureCollection.features()) {
            while (features.hasNext()) {
                SimpleFeature feature = features.next();
                Mission mission = null;
                try {

                    mission = createMissionShapefileFromSimpleFeature(feature, srid);

                    if (missionRepository.existsByCode(mission.getCode())) {
                        log.info("Mission with code: " + mission.getCode() + " already exists");

                    } else {
                        create(mission);

                    }
                } catch (Exception e) {
                    log.error("Error creating mission from shapefile", e);

                    String message = MessageResponse.builder()
                            .title("Invalide Shapefile")
                            .mainMessage("Le shapefile ne contient pas les attributs d'une mission + " + e.getMessage())
                            .subMessage("Veuillez vérifier les attributs de la mission")
                            .build()
                            .format();
                    throw new ShapefileProcessingException(message);
                }

            }

        }

    }

    private Mission createMissionShapefileFromSimpleFeature(SimpleFeature feature, Integer srid)
            throws MismatchedDimensionException, FactoryException, TransformException {
        log.info("Creating mission from shapefile");

        Mission mission = new Mission();

        setMissionGeometry(feature, mission, srid);
        setMissionAttributs(feature, mission);
        setMissionObjets(feature, mission);
        setMissionDate(feature, mission);
        setMissionPLanAction(feature, mission);
        setMissionOrganisme(feature, mission);
        setMissionCapteurAttributs(feature, mission);
        setMissionSuperficie(feature, mission, srid, 900918);

        return mission;
    }

    private void setMissionGeometry(SimpleFeature feature, Mission mission, Integer srid)
            throws MismatchedDimensionException {

        Geometry geometry = ShpSimpleFeatureService.get2DGeometryFromFeature(feature, srid);

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

    private void setMissionDate(SimpleFeature feature, Mission mission) {
        String dateStr = ShpSimpleFeatureService.getValueFromFeature(feature, "date");

        if (!dateStr.equals("")) {
            LocalDate date = DateUtils.parseVerboseToLocalDate(dateStr);
            mission.setDatePva(date);
        }
    }

    private void setMissionOrganisme(SimpleFeature feature, Mission mission) {
        String organismeName = ShpSimpleFeatureService.getValueFromFeature(feature, "organisme");

        Organisme organisme = organismeService.findByNom(organismeName)
                .orElseGet(() -> organismeService.findByNom("ancfcc")
                        .orElseThrow(() -> new EntityNotFoundException("Organisme not found")));
        mission.setOrganisme(organisme);
    }

    private void setMissionSuperficie(SimpleFeature feature, Mission mission, Integer sourceSrid,
            Integer projectionSrid)
            throws MismatchedDimensionException {
        String superficieString = ShpSimpleFeatureService.getValueFromFeature(feature, "superficie");

        if (!superficieString.equals("")) {
            Double superficie = Double.parseDouble(superficieString);
            mission.setSuperficie(
                    superficie);
        } else {
            Geometry geometry = ShpSimpleFeatureService.get2DGeometryFromFeature(feature, sourceSrid);
            Geometry transformerGeom = ITRFCrsTransformer.transformFromTo(geometry, sourceSrid, projectionSrid);
            double areaInSquareMeters = transformerGeom.getArea();
            double areaInHectares = areaInSquareMeters / 10000;
            double roundedArea = Math.round(areaInHectares);
            mission.setSuperficie(roundedArea);
        }

    }

    private void setMissionPLanAction(SimpleFeature feature, Mission mission) {
        String planActionName = ShpSimpleFeatureService.getValueFromFeature(feature, "exercice");

        PlanAction planAction = planActionService.findByNom(planActionName).orElseThrow(
                () -> new EntityNotFoundException("PlanAction not found"));
        mission.setPlanAction(planAction);
    }

    private void setMissionObjets(SimpleFeature feature, Mission mission) {
        List<String> objetAttributNameList = ShpSimpleFeatureService.findFeatureListWithPatternIgnoreCase(feature,
                "objet");

        for (String objetAttributName : objetAttributNameList) {
            String objetName = StringHelper.getSafeString(feature.getAttribute(objetAttributName)).toLowerCase();
            if (objetName != null && !objetName.isEmpty()) {
                Objet objet = objetService.findByNom(objetName)
                        .orElseThrow(() -> new EntityNotFoundException("Objet not found: " + objetName));
                mission.getObjets().add(objet);
            }
        }
    }

    private void setMissionAttributs(SimpleFeature feature, Mission mission) {
        String missionCode = ShpSimpleFeatureService.getValueFromFeature(feature, "code");
        String missionMission = ShpSimpleFeatureService.getValueFromFeature(feature, "mission");
        mission.setCode(missionCode);
        mission.setNom(missionMission);
    }

    private Mission setMissionCapteurAttributs(SimpleFeature feature, Mission mission) {
        String capteurName = ShpSimpleFeatureService.getValueFromFeature(feature, "camera");
        String capteurCode = capteurService.getCapteurCodeFromString(capteurName.toLowerCase());
        String gsdEchelle = ShpSimpleFeatureService.getValueFromFeature(feature, "gsd");

        Optional<Capteur> capteur = capteurService.findByCode(capteurCode);

        if (capteur.isPresent()) {
            mission.setCapteur(capteur.get());
            String mode = capteur.get().getMode();
            switch (mode) {
                case "analogique":
                    AnalogiqueAttribut analogiqueAttribut = new AnalogiqueAttribut();
                    analogiqueAttribut.setEchelle(Integer.parseInt(gsdEchelle));
                    analogiqueAttribut.setMission(mission);
                    mission.setAnalogiqueAttributs(analogiqueAttribut);
                    break;
                case "numérique":
                    NumeriqueAttribut numeriqueAttribut = new NumeriqueAttribut();
                    numeriqueAttribut.setResolution(Integer.parseInt(gsdEchelle));
                    numeriqueAttribut.setMission(mission);
                    mission.setNumeriqueAttributs(numeriqueAttribut);
                    break;
                case "lidar":
                    LidarAttribut lidarAttribut = new LidarAttribut();
                    lidarAttribut.setDensite(Float.parseFloat(gsdEchelle));
                    lidarAttribut.setMission(mission);
                    mission.setLidarAttributs(lidarAttribut);
                    break;
                default:
                    break;
            }
        } else {

            log.error("Capteur with name: " + capteurName + " not found");

        }
        return mission;
    }

}