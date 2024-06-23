package ma.org.ancfcc.pva.core.utilities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

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
    public static LocalDateTime parseDateString(String dateString) {
        LocalDate date = parseToDate(dateString);
        return date != null ? date.atStartOfDay() : null;
    }

    /**
     * Tries to parse a date string which could be in different formats and return a
     * LocalDate.
     * 
     * @param dateString The date string to be parsed.
     * @return A LocalDate object or null if parsing fails.
     */
    public static LocalDate parseToDate(String dateString) {
        DateTimeFormatter primaryFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter secondaryFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");

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
