package ma.org.ancfcc.pva.modules.mission.service.exports.xls.single.informations;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import ma.org.ancfcc.pva.modules.mission.models.Mission;

public interface MissionInformationsXlsGenerator {

    void createMissionInformationTable(Sheet sheet, Mission mission, Workbook workbook);
}
