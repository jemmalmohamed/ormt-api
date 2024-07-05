package ma.org.ormt.core.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.core.commun.rest.responses.MessageResponse;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateUtils {

    /**
     * Tries to parse a date string which could be in different formats and return a
     * LocalDateTime.
     * 
     * @param dateString The date string to be parsed.
     * @return A LocalDateTime object or null if parsing fails.
     */
    public static LocalDateTime parseDateFromString(String dateString, String format) {
        LocalDate date = parseLocalDate(dateString, format);
        return date != null ? date.atStartOfDay() : null;
    }

    // Method to parse string into Date
    public static Date parseDate(String dateStr) {
        try {
            // Adjust the format as necessary
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            return formatter.parse(dateStr);
        } catch (ParseException e) {

            String message = MessageResponse.builder()
                    .title("Erreur de format de date")
                    .mainMessage("Le format de date doit être yyyy-MM-dd'T'HH:mm:ss")
                    .build()
                    .format();

            throw new RuntimeException(message);
        }
    }

    /**
     * Tries to parse a date string which could be in different formats and return a
     * LocalDate.
     * 
     * @param dateString The date string to be parsed.
     * @return A LocalDate object or null if parsing fails.
     */
    public static LocalDate parseLocalDate(String dateString, String format) {
        DateTimeFormatter primaryFormatter = DateTimeFormatter.ofPattern(format);
        DateTimeFormatter secondaryFormatter = DateTimeFormatter.ofPattern(format);

        try {
            return LocalDate.parse(dateString, primaryFormatter);
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(dateString, secondaryFormatter);
            } catch (DateTimeParseException ex) {
                log.warn("Failed to parse date string: " + dateString);
                return null;
            }
        }
    }

    /**
     * Parses a verbose date string which could be in a verbose datetime format or
     * simpler date formats and return a LocalDateTime.
     * 
     * @param dateString The date string to be parsed.
     * @return A LocalDateTime object or null if parsing fails.
     */
    public static LocalDateTime parseVerboseDateString(String dateString) {
        ZonedDateTime zonedDateTime = parseVerboseToDate(dateString);
        return zonedDateTime != null ? zonedDateTime.toLocalDateTime() : null;
    }

    /**
     * Parses a verbose date string which could be in a verbose datetime format or
     * simpler date formats and return a LocalDate.
     * 
     * @param dateString The date string to be parsed.
     * @return A LocalDate object or null if parsing fails.
     */
    public static LocalDate parseVerboseToLocalDate(String dateString) {
        ZonedDateTime zonedDateTime = parseVerboseToDate(dateString);
        return zonedDateTime != null ? zonedDateTime.toLocalDate() : null;
    }

    private static ZonedDateTime parseVerboseToDate(String dateString) {
        DateTimeFormatter verboseFormatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy");

        try {
            return ZonedDateTime.parse(dateString, verboseFormatter);
        } catch (DateTimeParseException e) {
            log.warn("Failed to parse verbose date string: " + dateString);
            return null;
        }
    }
}
