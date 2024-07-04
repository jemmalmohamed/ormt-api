package ma.org.ancfcc.pva.modules.mission.scan.service;

import java.util.UUID;

import ma.org.ancfcc.pva.core.commun.base.service.BaseService;
import ma.org.ancfcc.pva.modules.mission.scan.ScanExecution;
import ma.org.ancfcc.pva.modules.mission.scan.dto.request.ScanExecutionRequestDto;

public interface ScanExecutionService extends BaseService<ScanExecution> {

    ScanExecution create(ScanExecutionRequestDto requestDto);

    ScanExecution update(UUID id, ScanExecutionRequestDto photoRequestDto);

    Long countByBandeId(UUID bandeId);
}