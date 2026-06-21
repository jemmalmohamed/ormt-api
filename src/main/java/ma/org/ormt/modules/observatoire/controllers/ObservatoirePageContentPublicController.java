package ma.org.ormt.modules.observatoire.controllers;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ma.org.ormt.core.commun.base.controller.BaseController;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.modules.observatoire.dtos.ObservatoirePageContentDto;
import ma.org.ormt.modules.observatoire.dtos.ObservatoirePageContentMapper;
import ma.org.ormt.modules.observatoire.models.ObservatoirePageContent;
import ma.org.ormt.modules.observatoire.services.ObservatoirePageContentService;

@RestController
@RequestMapping("api/v1/public/observatoire-content")
@RequiredArgsConstructor
public class ObservatoirePageContentPublicController extends BaseController<ObservatoirePageContent> {

    private final ObservatoirePageContentService service;
    private final ObservatoirePageContentMapper mapper;

    @GetMapping("/current")
    public ResponseEntity<RestResponse<ObservatoirePageContentDto>> getCurrentPublishedContent() {
        Optional<ObservatoirePageContent> entity = service.findPublished();
        if (entity.isEmpty()) {
            return createNotFoundResponse("Contenu Observatoire publie non trouve");
        }

        return buildResponseEntity(entity.get(), ObservatoirePageContentDto.class, HttpStatus.OK);
    }

    @Override
    protected <DTO> DTO mapToDto(ObservatoirePageContent entity, Class<DTO> dtoClass) {
        if (dtoClass == ObservatoirePageContentDto.class) {
            return dtoClass.cast(mapper.mapToDto(entity));
        }

        throw new IllegalArgumentException("Unsupported DTO type: " + dtoClass.getName());
    }
}