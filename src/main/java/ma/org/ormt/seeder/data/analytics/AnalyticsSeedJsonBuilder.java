package ma.org.ormt.seeder.data.analytics;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AnalyticsSeedJsonBuilder {

    private final ObjectMapper objectMapper;

    public String metadata(String seedSource, Map<String, Object> extraValues) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("seedSource", seedSource);
        if (extraValues != null) {
            extraValues.forEach((key, value) -> {
                if (key != null && value != null) {
                    payload.put(key, value);
                }
            });
        }
        return toJson(payload);
    }

    public String heroContent(String imageUrl, String apropos) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("imageUrl", imageUrl == null ? "" : imageUrl);
        payload.put("apropos", apropos == null ? "" : apropos);
        return toJson(payload);
    }

    public String tbdEmbedContent(Long tbdDashboardId) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("tbdDashboardId", tbdDashboardId);
        return toJson(payload);
    }

    public String editorContent(String html, String backgroundColor, String color) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("html", html == null ? "" : html);

        Map<String, Object> widgetStyle = new LinkedHashMap<>();
        widgetStyle.put("backgroundColor", backgroundColor == null ? "" : backgroundColor);
        widgetStyle.put("color", color == null ? "" : color);
        payload.put("widgetStyle", widgetStyle);

        return toJson(payload);
    }

    private String toJson(Map<String, Object> payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Impossible de construire le JSON de seed analytique.", exception);
        }
    }
}
