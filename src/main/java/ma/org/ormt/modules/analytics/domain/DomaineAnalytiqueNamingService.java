package ma.org.ormt.modules.analytics.domain;

import java.text.Normalizer;
import java.util.Locale;

import org.springframework.stereotype.Service;

@Service
public class DomaineAnalytiqueNamingService {

    public String normalizeThemeKey(String rawValue) {
        if (rawValue == null) {
            return "";
        }
        String base = rawValue.trim().toLowerCase(Locale.ROOT)
                .replace(" - public", "")
                .replace(" - décisionnel", "")
                .replace(" - decisionnel", "");
        String normalized = Normalizer.normalize(base, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return normalized;
    }

    public String normalizeSlug(String rawValue) {
        return normalizeThemeKey(rawValue);
    }
}
