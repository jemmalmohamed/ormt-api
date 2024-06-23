package ma.org.ancfcc.pva.seeder.data;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.github.javafaker.Faker;

import lombok.RequiredArgsConstructor;
import ma.org.ancfcc.pva.modules.avion.Avion;
import ma.org.ancfcc.pva.modules.avion.service.AvionService;
import ma.org.ancfcc.pva.modules.capteur.Capteur;
import ma.org.ancfcc.pva.modules.capteur.enums.CapteurCategorie;
import ma.org.ancfcc.pva.modules.capteur.enums.CapteurFormat;
import ma.org.ancfcc.pva.modules.capteur.enums.CapteurMode;
import ma.org.ancfcc.pva.modules.capteur.enums.CapteurName;
import ma.org.ancfcc.pva.modules.capteur.service.CapteurService;

@Component
@Order(2)
@RequiredArgsConstructor
public class AvionCapteurSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private String seed;

    private final CapteurService capteurService;

    private final AvionService avionService;

    private static final String ADS = CapteurName.ADS40_80.getDescription();
    private static final String ADS_DESC = "Leica ADS40/80 Airborne Digital Sensor";
    private static final String ALS = CapteurName.ALS70.getDescription();
    private static final String ALS_DESC = "Leica ALS Airborne Laser Scanner";
    private static final String DMC = CapteurName.DMC_II_230.getDescription();
    private static final String DMC_DESC = "Z/I Digital Mapping Camera II 230 Megapixel";
    private static final String RMK = CapteurName.RMK_TOP_15.getDescription();
    private static final String RMK_DESC = "Zeiss RMK TOP 153 mm focal length";
    private static final String RC30 = CapteurName.RC30.getDescription();
    private static final String RC30_DESC = "Leica RC30 153 mm focal length";

    private static final String FCC = "cn-fcc";
    private static final String TWY = "cn-twy";
    private static final String TWL = "cn-twl";

    private static final String LEICA = "Leica geosystems";
    private static final String ZI_IMAGING = "ZI Imaging";
    private static final String ZEISS = "Zeiss";
    private static final Faker faker = new Faker(Locale.FRENCH);

    @Override
    public void run(String... args) throws Exception {
        if (!seed.equals("true"))
            return;

        seedAvions();
        seedCapteurs();
        seedCapteurDrones();

        // attachCapteurToAvion(FCC, ADS, true);
        // attachCapteurToAvion(FCC, ALS, true);
        // attachCapteurToAvion(TWY, DMC, true);
        // attachCapteurToAvion(TWY, RC30, false);
        // attachCapteurToAvion(TWL, RMK, false);
    }

    // private void attachCapteurToAvion(String avionMatricule, String capteurName,
    // boolean isInstalled) {
    // Avion avion = avionService.findByMatricule(avionMatricule).orElseThrow();
    // Capteur capteur = capteurService.findByNom(capteurName).orElseThrow();

    // AvionCapteur avionCapteur = new AvionCapteur();
    // avionCapteur.setAvion(avion);
    // avionCapteur.setCapteur(capteur);
    // avionCapteur.setIsInstalled(isInstalled);

    // avionCapteurRepository.save(avionCapteur);
    // }

    private void seedCapteurDrones() {
        for (int i = 0; i < 6; i++) {
            Capteur capteur = createCapteur(
                    "drone-capteur-" + i,
                    "drone-capteur-" + i,
                    CapteurCategorie.DRONE.getDescription(),
                    CapteurFormat.MATRICIELLE.getDescription(),
                    CapteurMode.NUMERIQUE.getDescription(),
                    LEICA);
            capteurService.create(capteur);
        }
    }

    private void seedAvions() {
        createAndSaveAvion(FCC, "king air 350 er", "beachcraft");
        createAndSaveAvion(TWY, "king air 350", "beachcraft");
        createAndSaveAvion(TWL, "defender", "Britten-Norman");
    }

    private void createAndSaveAvion(String matricule, String modele, String marque) {
        if (avionService.findByMatricule(matricule).isEmpty()) {
            Avion avion = new Avion();
            avion.setMatricule(matricule);
            avion.setModele(modele);
            avion.setMarque(marque);
            avionService.create(avion);
        }
    }

    private void seedCapteurs() {
        createAndSaveCapteur(ADS, ADS_DESC, CapteurCategorie.AVION.getDescription(),
                CapteurFormat.LINEAIRE.getDescription(), CapteurMode.NUMERIQUE.getDescription(), LEICA);
        createAndSaveCapteur(ALS, ALS_DESC, CapteurCategorie.AVION.getDescription(),
                CapteurFormat.LINEAIRE.getDescription(), CapteurMode.LIDAR.getDescription(), LEICA);
        createAndSaveCapteur(DMC, DMC_DESC, CapteurCategorie.AVION.getDescription(),
                CapteurFormat.MATRICIELLE.getDescription(), CapteurMode.NUMERIQUE.getDescription(), ZI_IMAGING);
        createAndSaveCapteur(RMK, RMK_DESC, CapteurCategorie.AVION.getDescription(),
                CapteurFormat.MATRICIELLE.getDescription(), CapteurMode.ANALOGIQUE.getDescription(), ZEISS);
        createAndSaveCapteur(RC30, RC30_DESC, CapteurCategorie.AVION.getDescription(),
                CapteurFormat.MATRICIELLE.getDescription(), CapteurMode.ANALOGIQUE.getDescription(), LEICA);
    }

    private void createAndSaveCapteur(String nom, String description, String categorie, String format, String mode,
            String constructeur) {
        if (capteurService.findByNom(nom).isEmpty()) {
            Capteur capteur = createCapteur(nom, description, categorie, format, mode, constructeur);
            capteurService.create(capteur);
        }
    }

    private Capteur createCapteur(String nom, String description, String categorie, String format, String mode,
            String constructeur) {
        Capteur capteur = new Capteur();
        capteur.setNom(nom);
        capteur.setDescription(description);
        capteur.setCategorie(categorie);
        capteur.setFormat(format);
        capteur.setMode(mode);
        capteur.setSerial(faker.number().digits(5));
        capteur.setConstructeur(constructeur);
        return capteur;
    }

}