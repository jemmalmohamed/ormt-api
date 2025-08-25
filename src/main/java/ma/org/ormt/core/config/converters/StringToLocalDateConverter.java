package ma.org.ormt.core.config.converters;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.core.convert.converter.Converter;

/**
 * Converts various ISO-8601 date/time string formats to a LocalDate.
 * Accepts:
 * - yyyy-MM-dd
 * - yyyy-MM-ddTHH:mm:ss
 * - yyyy-MM-ddTHH:mm:ss.SSSX (e.g., with timezone Z or offset)
 * - Instant strings like 2025-08-17T23:00:00.000Z
 *
 * Strategy: prefer the leading date portion when present; otherwise try common
 * ISO parsers and convert to LocalDate.
 */
public class StringToLocalDateConverter implements Converter<String, LocalDate> {

    @Override
    public LocalDate convert(String source) {
        if (source == null) {
            return null;
        }
        String s = source.trim();
        if (s.isEmpty()) {
            return null;
        }

        // Fast-path: if the string starts with an ISO date, use the first 10 chars
        if (s.length() >= 10 && Character.isDigit(s.charAt(0)) && s.charAt(4) == '-' && s.charAt(7) == '-') {
            String datePart = s.substring(0, 10);
            try {
                return LocalDate.parse(datePart, DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (Exception ignored) {
                // fall through
            }
        }

        // Try pure ISO local date
        try {
            return LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception ignored) {
        }

        // Try ISO local date-time then drop time
        try {
            return LocalDateTime.parse(s, DateTimeFormatter.ISO_LOCAL_DATE_TIME).toLocalDate();
        } catch (Exception ignored) {
        }

        // Try offset date-time (e.g., ...Z or +01:00) then drop time
        try {
            return OffsetDateTime.parse(s, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDate();
        } catch (Exception ignored) {
        }

        // Try instant then convert using system default zone
        try {
            Instant instant = Instant.parse(s);
            return instant.atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (Exception ignored) {
        }

        // Could not parse; return null to allow validation (@NotNull) to handle it
        return null;
    }
}
