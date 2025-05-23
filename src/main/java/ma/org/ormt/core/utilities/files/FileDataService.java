package ma.org.ormt.core.utilities.files;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class FileDataService {
    private final ObjectMapper objectMapper;

    public FileDataService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T readJsonFile(File file, Class<T> clazz) throws IOException {
        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            return objectMapper.readValue(inputStream, clazz);
        }
    }

    public <T> List<T> readJsonFileAsList(File file, TypeReference<List<T>> typeRef) throws IOException {
        try (InputStream inputStream = Files.newInputStream(file.toPath())) {
            return objectMapper.readValue(inputStream, typeRef);
        }
    }

    public boolean fileExists(File file) {
        return file != null && file.exists() && file.isFile();
    }
}
