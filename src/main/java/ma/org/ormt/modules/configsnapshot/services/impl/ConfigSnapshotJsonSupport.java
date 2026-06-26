package ma.org.ormt.modules.configsnapshot.services.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Component
public class ConfigSnapshotJsonSupport {

    private final ObjectMapper snapshotMapper;

    public ConfigSnapshotJsonSupport(ObjectMapper objectMapper) {
        this.snapshotMapper = objectMapper.copy()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
    }

    public byte[] toJsonBytes(Object value) throws JsonProcessingException {
        return snapshotMapper.writeValueAsBytes(value);
    }

    public <T> T fromJsonBytes(byte[] value, Class<T> targetClass) throws IOException {
        return snapshotMapper.readValue(value, targetClass);
    }

    public String normalizeJson(String payload) {
        if (payload == null || payload.isBlank()) {
            return null;
        }

        try {
            JsonNode node = snapshotMapper.readTree(payload);
            return snapshotMapper.writeValueAsString(node);
        } catch (Exception exception) {
            return payload.trim();
        }
    }

    public byte[] utf8(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }

    public <T> List<T> sort(Collection<T> values, Comparator<T> comparator) {
        List<T> sorted = new ArrayList<>(values == null ? List.of() : values);
        sorted.sort(comparator);
        return sorted;
    }

    public <K, V> Map<K, V> linkedMap() {
        return new LinkedHashMap<>();
    }

    public boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    public String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public String coalesce(String... values) {
        for (String value : values) {
            if (hasText(value)) {
                return value;
            }
        }
        return null;
    }

    public String safeFileSegment(String value) {
        String normalized = Objects.toString(trimToNull(value), "item");
        return normalized.replaceAll("[\\\\/:*?\"<>|]", "_");
    }
}
