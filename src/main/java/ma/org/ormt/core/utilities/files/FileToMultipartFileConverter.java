package ma.org.ormt.core.utilities.files;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.springframework.web.multipart.MultipartFile;

public class FileToMultipartFileConverter implements MultipartFile {
    private final File file;
    private final String name;
    private final String originalFilename;
    private final String contentType;
    private final byte[] content;

    public FileToMultipartFileConverter(File file) throws IOException {
        this.file = file;
        this.name = file.getName();
        this.originalFilename = file.getName();
        this.contentType = Files.probeContentType(file.toPath());
        this.content = Files.readAllBytes(file.toPath());
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return content.length == 0;
    }

    @Override
    public long getSize() {
        return content.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return content;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new FileInputStream(file);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        Files.write(dest.toPath(), content);
    }

    public static MultipartFile toMultipartFile(File file) throws IOException {
        return new FileToMultipartFileConverter(file);
    }
}
