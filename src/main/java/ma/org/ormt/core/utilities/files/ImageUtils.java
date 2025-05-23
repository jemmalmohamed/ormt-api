package ma.org.ormt.core.utilities.files;

import net.coobird.thumbnailator.Thumbnails;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;

public class ImageUtils {
    private ImageUtils() {
    }

    /**
     * Optimize (resize/compress) an image MultipartFile and return as File (JPEG).
     * 
     * @param file      the original MultipartFile
     * @param maxWidth  max width in pixels
     * @param maxHeight max height in pixels
     * @param quality   JPEG quality (0.0-1.0)
     * @return optimized File (JPEG)
     * @throws IOException
     */
    public static File optimizeImageToFile(MultipartFile file, int maxWidth, int maxHeight, double quality)
            throws IOException {
        if (file == null || file.isEmpty())
            return null;
        File tempFile = File.createTempFile("optimized-", ".jpg");
        tempFile.deleteOnExit();
        Thumbnails.of(file.getInputStream())
                .size(maxWidth, maxHeight)
                .outputQuality(quality)
                .outputFormat("jpg")
                .toFile(tempFile);
        return tempFile;
    }

    /**
     * Optimize an image and convert to MultipartFile using
     * FileToMultipartFileConverter.
     * 
     * @param file      the original MultipartFile
     * @param maxWidth  max width in pixels
     * @param maxHeight max height in pixels
     * @param quality   JPEG quality (0.0-1.0)
     * @return optimized MultipartFile (JPEG)
     * @throws IOException
     */
    public static MultipartFile optimizeImageWithConverter(MultipartFile file, int maxWidth, int maxHeight,
            double quality) throws IOException {
        File optimizedFile = optimizeImageToFile(file, maxWidth, maxHeight, quality);
        if (optimizedFile == null)
            return file;
        return FileToMultipartFileConverter.toMultipartFile(optimizedFile);
    }
}
