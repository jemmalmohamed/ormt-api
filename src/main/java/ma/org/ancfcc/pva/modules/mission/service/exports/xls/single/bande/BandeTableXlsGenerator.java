package ma.org.ancfcc.pva.modules.mission.service.exports.xls.single.bande;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import ma.org.ancfcc.pva.modules.mission.models.Mission;

public interface BandeTableXlsGenerator {

    void createBandesTable(Sheet sheet, Mission mission, Workbook workbook);
}
