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
     * Tries to parse a date string which could be in different formats.
     * 
     * @param dateString The date string to be parsed.
     * @return A LocalDateTime object or null if parsing fails.
     */
    public static LocalDateTime parseDateString(String dateString) {
        // Primary format (with century)
        DateTimeFormatter primaryFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        // Secondary format (without century)
        DateTimeFormatter secondaryFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");

        // Try with the primary format
        try {
            LocalDate date = LocalDate.parse(dateString, primaryFormatter);
            return date.atStartOfDay(); // Convert to LocalDateTime at start of the day
        } catch (DateTimeParseException e) {
            // If primary format fails, try with the secondary format
            try {
                LocalDate date = LocalDate.parse(dateString, secondaryFormatter);
                return date.atStartOfDay(); // Convert to LocalDateTime at start of the day
            } catch (DateTimeParseException ex) {
                // If both formats fail, handle the error (e.g., log an error or return null)
                log.warn("Failed to parse date string: " + dateString);
                return null; // Or handle differently
            }
        }
    }

    /**
     * Parses a date string which could be in a verbose datetime format or simpler
     * date formats.
     * 
     * @param dateString The date string to be parsed.
     * @return A LocalDateTime object or null if parsing fails.
     */
    public static LocalDateTime parseVerboseDateString(String dateString) {
        // Verbose datetime format including day of week, month in text, time, time
        // zone, and year
        DateTimeFormatter verboseFormatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy");

        // Attempt to parse the verbose datetime format
        try {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateString, verboseFormatter);
            return zonedDateTime.toLocalDateTime();
        } catch (DateTimeParseException e) {
            log.warn("Failed to parse verbose date string: " + dateString);
            return null; // Or handle differently
        }
    }
}
