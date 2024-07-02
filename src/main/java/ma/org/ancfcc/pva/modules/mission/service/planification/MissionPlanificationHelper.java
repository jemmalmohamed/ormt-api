package ma.org.ancfcc.pva.modules.mission.service.planification;

import org.springframework.stereotype.Service;

@Service
public class MissionPlanificationHelper {

    public String formatBandeLabel(String label) {
        StringBuilder numericalPart = new StringBuilder();

        // Conserver uniquement les chiffres
        for (char ch : label.toCharArray()) {
            if (Character.isDigit(ch)) {
                numericalPart.append(ch);
            }
        }

        // Vérifier la longueur de la partie numérique et formater correctement
        if (numericalPart.length() > 0) {
            String digits = numericalPart.toString();
            int number = Integer.parseInt(digits);
            if (number < 100) {
                // Formater pour avoir toujours deux chiffres si le nombre est inférieur à 100
                return String.format("%02d", number);
            } else {
                // Garder le nombre tel quel s'il est supérieur ou égal à 100
                return digits;
            }
        }

        // Retourner "00" si aucun chiffre n'est présent pour maintenir un format
        // cohérent
        return "00";
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
