package ma.org.ormt.modules.observatoire.controllers.admin;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ma.org.ormt.core.commun.base.controller.BaseController;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.core.validators.groups.OnCreate;
import ma.org.ormt.core.validators.groups.OnUpdate;
import ma.org.ormt.modules.observatoire.dtos.ObservatoirePageContentDto;
import ma.org.ormt.modules.observatoire.dtos.ObservatoirePageContentMapper;
import ma.org.ormt.modules.observatoire.dtos.request.ObservatoirePageContentRequestDto;
import ma.org.ormt.modules.observatoire.models.ObservatoirePageContent;
import ma.org.ormt.modules.observatoire.services.ObservatoirePageContentService;

@RestController
@RequestMapping("api/v1/admin/observatoire-content")
@RequiredArgsConstructor
public class ObservatoirePageContentAdminController extends BaseController<ObservatoirePageContent> {

    private static final String ADMIN_ACCESS = "hasAuthority('ROLE_ADMIN') or hasAuthority('ROLE_MASTER')";

    private final ObservatoirePageContentService service;
    private final ObservatoirePageContentMapper mapper;

    @GetMapping("/current")
    @PreAuthorize(ADMIN_ACCESS)
    public ResponseEntity<RestResponse<ObservatoirePageContentDto>> getCurrentContent() {
        Optional<ObservatoirePageContent> entity = service.findCurrent();
        if (entity.isEmpty()) {
            return createNotFoundResponse("Contenu Observatoire non trouve");
        }

        return buildResponseEntity(entity.get(), ObservatoirePageContentDto.class, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize(ADMIN_ACCESS)
    public ResponseEntity<RestResponse<ObservatoirePageContentDto>> create(
            @Validated(OnCreate.class) @RequestBody ObservatoirePageContentRequestDto requestDto) {
        ObservatoirePageContent created = service.create(requestDto);
        return buildResponseEntity(created, ObservatoirePageContentDto.class, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize(ADMIN_ACCESS)
    public ResponseEntity<RestResponse<ObservatoirePageContentDto>> update(@PathVariable Long id,
            @Validated(OnUpdate.class) @RequestBody ObservatoirePageContentRequestDto requestDto) {
        ObservatoirePageContent updated = service.update(id, requestDto);
        return buildResponseEntity(updated, ObservatoirePageContentDto.class, HttpStatus.OK);
    }

    @Override
    protected <DTO> DTO mapToDto(ObservatoirePageContent entity, Class<DTO> dtoClass) {
        if (dtoClass == ObservatoirePageContentDto.class) {
            return dtoClass.cast(mapper.mapToDto(entity));
        }

        throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
    }
}