package ma.org.ormt.modules.mission.service.planification.helper;

import org.springframework.stereotype.Service;

@Service
public class MissionPlanificationHelper {

    public String formaLabel(String label) {
        StringBuilder numericalPart = new StringBuilder();

        // Collect only the digits
        for (char ch : label.toCharArray()) {
            if (Character.isDigit(ch)) {
                numericalPart.append(ch);
            }
        }

        // Remove leading zeros by converting to an integer and back to a string
        if (numericalPart.length() > 0) {
            return String.valueOf(Integer.parseInt(numericalPart.toString()));
        }

        // Return "0" if no digits are present to maintain a consistent format
        return "0";
    }

    public String formatScanLabel(String label) {
        String scanLabel = label;
        if (label.length() == 3) {
            scanLabel = label.substring(1);
        }
        return scanLabel;
    }

    public String formatEventLabel(String label) {
        String eventLabel = label;
        if (label.length() == 3) {
            eventLabel = label.substring(1);
        }
        return eventLabel;
    }
}
