package ma.org.ormt.modules.mission.service.exports.xls.single.bande;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import ma.org.ormt.modules.mission.models.Mission;

public interface BandeTableXlsGenerator {

    void createBandesTable(Sheet sheet, Mission mission, Workbook workbook);
}
