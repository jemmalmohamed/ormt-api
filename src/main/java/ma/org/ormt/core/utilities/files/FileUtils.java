package ma.org.ormt.core.utilities.files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.core.commun.rest.responses.MessageResponse;
import ma.org.ormt.core.exceptions.handlers.ShapefileUploadException;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {

    public static File findFileWithExtension(Path tempDir, List<MultipartFile> files, String extension) {
        return new File(tempDir.toFile(), files.stream()
                .filter(f -> {
                    String fileName = f.getOriginalFilename();
                    return fileName != null && fileName.endsWith(extension);
                })
                .findFirst()
                .orElseThrow(() -> new ShapefileUploadException(MessageResponse.builder()
                        .mainMessage("Le fichier avec l'extension " + extension + " est manquant.")
                        .build()
                        .format()))
                .getOriginalFilename());
    }

    public static Path saveFilesTemporarilyDirectory(List<MultipartFile> files) throws IOException {
        Path tempDir = Files.createTempDirectory("uploaded-shapefiles");
        for (MultipartFile file : files) {
            File tempFile = new File(tempDir.toFile(), file.getOriginalFilename());
            file.transferTo(tempFile);
        }
        return tempDir;
    }

    public static void cleanTemporaryFiles(Path tempDir) {
        try (java.util.stream.Stream<Path> paths = Files.walk(tempDir)) {
            paths.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            log.error("Error while deleting temporary files", e);
            throw new ShapefileUploadException(
                    MessageResponse.builder()
                            .mainMessage("Erreur lors de la suppression des fichiers temporaires")
                            .build()
                            .format());
        }
    }

    public static void checkFileIsXml(MultipartFile file) {
        String fileExtension = getFileExtension(file);

        if (!("xml".equalsIgnoreCase(fileExtension))) {
            throw new ShapefileUploadException(MessageResponse.builder()
                    .mainMessage("Le fichier " + file.getOriginalFilename() + " n'est pas un fichier Excel ou Xml.")
                    .build()
                    .format());
        }

    }

    public static void checkFileIsXls(MultipartFile file) {

        String fileExtension = getFileExtension(file);

        if (!("xls".equalsIgnoreCase(fileExtension)
                || "xlsx".equalsIgnoreCase(fileExtension))) {
            throw new ShapefileUploadException(MessageResponse.builder()
                    .mainMessage("Le fichier " + file.getOriginalFilename() + " n'est pas un fichier Excel ou Xml.")
                    .build()
                    .format());
        }

    }

    public static String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            return originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        }
        return null;
    }

    public static String getFileExtension(File file) {
        String originalFilename = file.getName();
        if (originalFilename != null && originalFilename.lastIndexOf(".") > 0) {
            return originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
        }
        return null;
    }

    public static File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
        File tempFile = File.createTempFile("temp", null);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            byte[] bytes = multipartFile.getBytes();
            fos.write(bytes);
        }
        return tempFile;
    }

    public static File getFileByExtFromFileListComponents(List<File> fileList, String extension) {
        return fileList.stream()
                .filter(file -> file.getName().endsWith("." + extension))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No " + extension + " file provided in components."));
    }

    public static void validateDirectory(File dir) throws IOException {
        if (!dir.exists()) {
            log.info("### DATA: folder {} does not exist", dir.getPath());
            throw new IOException("Directory does not exist: " + dir.getPath());
        }
        if (!dir.isDirectory()) {
            throw new IOException("Expected a directory but found a file: " + dir.getPath());
        }
    }
}
