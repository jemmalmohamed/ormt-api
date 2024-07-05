package ma.org.ormt.seeder.data;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.core.gis.shapefile.ShpFileService;
import ma.org.ormt.modules.province.service.ProvinceService;

@Log4j2
@Component
@RequiredArgsConstructor
@Order(11)
public class ProvinceShapefileSeeder implements CommandLineRunner {

    @Value("${starter.database.seed}")
    private String seeding;

    @Value("${data.external.data_path}")
    private String externalDataPath;

    private final ProvinceService provinceService;

    @Override
    public void run(String... args) throws IOException {

        if (!seeding.equals("true"))
            return;

        // ExecutorService executor = Executors.newFixedThreadPool(30); // Adjust the
        // number of threads as needed

        List<String> folders = Arrays.asList("province");

        for (String folder : folders) {
            // executor.execute(() -> {
            try {
                uploadShapefile(externalDataPath + "shapefiles/" + folder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // });
        }

    }

    private void uploadShapefile(String folder) throws IOException {
        log.info("### uploading region shapefiles...");

        log.info("### uploading {}..." + folder);

        File resourceDir = new File(folder);

        if (!resourceDir.exists()) {
            log.info("folder {} does not exist", folder);
            return;
        }

        if (resourceDir.isDirectory()) {
            File[] filesArray = resourceDir.listFiles(); // get all the files inside the directory

            List<File> shapefileComponents = Arrays.asList(filesArray);

            ShpFileService.validateShapefileComponents(shapefileComponents);

            provinceService.createProvinceFromShapefile(shapefileComponents, 4326);

        } else {
            throw new IOException("Expected a directory for shapefiles but found a   file.");
        }

        log.info("### uploading PROVINCE shapefiles... done");
    }

}
