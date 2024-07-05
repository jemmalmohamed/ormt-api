package ma.org.ancfcc.pva.core.gis.shapefile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.geotools.api.data.DataStore;
import org.geotools.api.data.SimpleFeatureSource;
import org.geotools.api.data.SimpleFeatureStore;
import org.geotools.api.data.Transaction;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.shapefile.ShapefileDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.springframework.web.multipart.MultipartFile;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ancfcc.pva.core.commun.rest.responses.MessageResponse;
import ma.org.ancfcc.pva.core.exceptions.handlers.ShapefileProcessingException;
import ma.org.ancfcc.pva.core.exceptions.handlers.ShapefileUploadException;
import ma.org.ancfcc.pva.core.gis.crs.CRSHelper;
import ma.org.ancfcc.pva.core.utilities.FileUtils;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ShpFileService {

    private static final List<String> SHAPEFILE_COMPONENT_EXTENSIONS = Arrays.asList(".shp", ".shx", ".dbf", ".prj");

    public static SimpleFeatureCollection getFeatureCollectionFromShapefile(File shapefile) throws IOException {
        SimpleFeatureCollection collection = null;
        ShapefileDataStore dataStore = null;
        try {
            dataStore = new ShapefileDataStore(shapefile.toURI().toURL());
            dataStore.setCharset(StandardCharsets.UTF_8);
            String typeName = dataStore.getTypeNames()[0];
            SimpleFeatureSource featureSource = dataStore.getFeatureSource(typeName);
            collection = featureSource.getFeatures();
        } catch (Exception e) {
            log.error("Error reading shapefile: {}", shapefile.getName(), e);
            throw new IOException("Failed to read shapefile", e);
        } finally {
            if (dataStore != null) {
                dataStore.dispose();
            }
        }
        return collection;
    }

    public static void validateShapefileComponents(List<File> shapefileComponents) {
        for (String ext : SHAPEFILE_COMPONENT_EXTENSIONS) {
            boolean hasComponent = shapefileComponents.stream().anyMatch(file -> file.getName().endsWith(ext));
            if (!hasComponent) {
                String message = MessageResponse.builder()
                        .mainMessage("Le fichier avec l'extension " + ext + " est manquant.")
                        .build()
                        .format();
                throw new ShapefileUploadException(message);
            }
        }
    }

    public static List<File> findShapefileComponents(Path tempDir, List<MultipartFile> files) {
        return SHAPEFILE_COMPONENT_EXTENSIONS.stream()
                .map(ext -> FileUtils.findFileWithExtension(tempDir, files, ext))
                .collect(Collectors.toList());
    }

    public static boolean prjHasProjection(File prjFile, Integer srid) {
        Integer prjSrid = getSridFromPrjFile(prjFile);
        return prjSrid != null && prjSrid.equals(srid);
    }

    public static Integer getSridFromPrjFile(File prjFile) {
        Integer srid;
        String wkt = readWKTFromPrjFile(prjFile);
        CoordinateReferenceSystem crs = CRSHelper.getCrsFromWkt(wkt);
        srid = CRSHelper.getOfficialCodeFromCrs(crs);
        if (srid == null) {

            String code = crs.getName().getCode();
            if (code.contains("GCS_WGS_1984")
                    || code.contains("WGS_1984")
                    || code.contains("WGS84")
                    || code.contains("WGS 1984")) {
                srid = 4326;
            }

        }
        return srid;
    }

    public static String readWKTFromPrjFile(File prjFile) {
        try {
            String wkt = new String(Files.readAllBytes(prjFile.toPath()), StandardCharsets.UTF_8);
            return CRSHelper.wktIsWGS(wkt) ? CRSHelper.normalizeWKT(wkt) : wkt;
        } catch (Exception e) {
            log.error("Error reading WKT from file", e);
        }
        return null;
    }

    public static void validatePrjFileIsWgs(File prjFile, Integer srid) {
        if (!srid.equals(4326)) {
            String message = MessageResponse.builder()
                    .title("Erreur de projection")
                    .mainMessage("Veuillez sélectionnez la projection WGS84 SRID 4326!")
                    .build()
                    .format();
            throw new ShapefileProcessingException(message);
        }
        boolean hasProjection = prjHasProjection(prjFile, srid);
        if (!hasProjection) {
            String message = MessageResponse.builder()
                    .title("Erreur de projection")
                    .mainMessage("Le  shapefile n'a pas la même projection sélectionnée !")
                    .build()
                    .format();
            throw new ShapefileProcessingException(message);
        }
    }

    public static File addShapefileToZip(File shapefileDirectory, String typeName) throws IOException {
        File zipFile = new File(shapefileDirectory, typeName + ".zip");
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            String[] extensions = new String[] { ".shp", ".shx", ".dbf", ".prj" };
            for (String extension : extensions) {
                File componentFile = new File(shapefileDirectory, typeName + extension);
                if (componentFile.exists()) {
                    zos.putNextEntry(new ZipEntry(componentFile.getName()));
                    try (FileInputStream fis = new FileInputStream(componentFile)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = fis.read(buffer)) > 0) {
                            zos.write(buffer, 0, length);
                        }
                        zos.closeEntry();
                    }
                }
            }
        }
        return zipFile;
    }

    /**
     * Creates a shapefile from a given SimpleFeatureCollection and saves it to the
     * specified directory.
     *
     * @param featureCollection The collection of features to be included in the
     *                          shapefile.
     * @param typeName          The name of the shapefile type.
     * @param directory         The directory where the shapefile will be saved.
     * @return The created shapefile as a File object.
     * @throws IOException If an I/O error occurs during the shapefile creation
     *                     process.
     */
    public File createShapefile(SimpleFeatureCollection featureCollection, String typeName, File directory)
            throws IOException {
        File fileDirectory = new File(directory, typeName + ".shp");
        ShapefileDataStoreFactory dataStoreFactory = new ShapefileDataStoreFactory();

        Map<String, Serializable> params = new HashMap<>();
        params.put("url", fileDirectory.toURI().toURL());
        params.put("create spatial index", Boolean.TRUE);
        params.put("charset", StandardCharsets.ISO_8859_1.name());
        params.put("enable cpg switch", Boolean.TRUE);

        DataStore newDataStore = dataStoreFactory.createNewDataStore(params);

        newDataStore.createSchema(featureCollection.getSchema());

        try (Transaction transaction = new DefaultTransaction("create")) {
            SimpleFeatureStore featureStore = (SimpleFeatureStore) newDataStore.getFeatureSource(typeName);
            featureStore.setTransaction(transaction);
            featureStore.addFeatures(featureCollection);
            transaction.commit();
        } catch (IOException e) {
            throw new IOException("Failed to create shapefile", e);
        }
        newDataStore.dispose();
        return fileDirectory;
    }

    public File ensureDirectory(String directoryName) throws IOException {
        File fileDirectory = new File(System.getProperty("java.io.tmpdir"), directoryName);
        if (!fileDirectory.exists()) {
            boolean created = fileDirectory.mkdirs();
            if (!created) {
                throw new IOException("Failed to create directory: " + fileDirectory.getAbsolutePath());
            }
        }
        return fileDirectory;
    }

    public static boolean isValidShapefile(File dir, String name) {
        if (dir == null || !dir.isDirectory()) {
            return false;
        }
        return SHAPEFILE_COMPONENT_EXTENSIONS.stream().anyMatch(ext -> name.toLowerCase().endsWith(ext));
    }

}
