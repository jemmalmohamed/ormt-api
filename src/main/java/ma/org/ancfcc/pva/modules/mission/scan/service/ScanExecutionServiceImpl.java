package ma.org.ancfcc.pva.modules.mission.scan.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import ma.org.ancfcc.pva.core.commun.base.service.BaseServiceImpl;
import ma.org.ancfcc.pva.core.commun.base.service.SpecificationService;
import ma.org.ancfcc.pva.core.validators.ObjectsValidator;
import ma.org.ancfcc.pva.modules.mission.scan.ScanExecution;
import ma.org.ancfcc.pva.modules.mission.scan.dto.request.ScanExecutionRequestDto;
import ma.org.ancfcc.pva.modules.mission.scan.dto.request.ScanExecutionRequestDtoMapper;
import ma.org.ancfcc.pva.modules.mission.scan.repository.ScanExecutionRepository;

@Service
public class ScanExecutionServiceImpl extends BaseServiceImpl<ScanExecution>
        implements ScanExecutionService {

    @Autowired
    private ScanExecutionRepository scanExecutionRepository;

    @Autowired
    private ObjectsValidator<ScanExecutionRequestDto> validator;

    @Autowired
    private ScanExecutionRequestDtoMapper scanExecutionRequestMapper;

    public ScanExecutionServiceImpl(ScanExecutionRepository scanExecutionRepository,
            SpecificationService specificationService) {
        super(scanExecutionRepository, specificationService);
    }

    /**
     * Updates an Photo with new values
     *
     * @param id           the ID of the Photo to update
     * @param updatedPhoto the new Photo data
     * @return the updated Photo
     */
    @Override
    // @Transactional
    public ScanExecution update(Long id, ScanExecutionRequestDto scanExecutionRequestDto) {
        validator.validate(scanExecutionRequestDto);

        ScanExecution scanExecutionToUpdate = scanExecutionRequestMapper
                .mapToEntity(scanExecutionRequestDto);

        checkPathId(id, scanExecutionToUpdate.getId());

        ScanExecution scanExecution = scanExecutionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Photo not found"));

        return scanExecutionRepository.save(scanExecution);
    }

    @Override
    public ScanExecution create(ScanExecutionRequestDto requestDto) {

        validator.validate(requestDto);

        ScanExecution scanExecutionToCreate = scanExecutionRequestMapper.mapToEntity(requestDto);

        ScanExecution scanExecution = create(scanExecutionToCreate);

        return scanExecutionRepository.save(scanExecution);
    }

    @Override
    public Long countByBandeId(Long bandeId) {
        return scanExecutionRepository.countByBandeId(bandeId);
    }

}