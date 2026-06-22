package ma.org.ormt.modules.analytics.domain.controllers;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ma.org.ormt.core.commun.base.controller.BaseController;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.modules.analytics.category.dtos.CategorieAnalytiqueDto;
import ma.org.ormt.modules.analytics.category.models.CategorieAnalytique;
import ma.org.ormt.modules.analytics.domain.DomaineAnalytiqueMapper;
import ma.org.ormt.modules.analytics.domain.dtos.DomaineAnalytiqueDto;
import ma.org.ormt.modules.analytics.domain.models.DomaineAnalytique;
import ma.org.ormt.modules.analytics.domain.services.DomaineAnalytiqueService;
import ma.org.ormt.modules.dashboard.tbd.dtos.TbdDashboardFullDto;
import ma.org.ormt.modules.dashboard.tbd.services.TbdDashboardService;
import ma.org.ormt.modules.roleacces.utils.RoleAccesMappingUtil;

@RestController
@RequestMapping("/api/v1/public")
@RequiredArgsConstructor
public class PortailAnalytiquePublicController extends BaseController<Object> {

    private final DomaineAnalytiqueService service;
    private final DomaineAnalytiqueMapper mapper;
    private final TbdDashboardService tbdDashboardService;

    @GetMapping("/espaces/{espaceId}/domaines-analytiques")
    public ResponseEntity<RestResponse<List<DomaineAnalytiqueDto>>> getByEspace(@PathVariable Long espaceId) {
        if (!hasResourceAccess(espaceId, "espace", "lecture")) {
            return createForbiddenResponse();
        }
        List<DomaineAnalytiqueDto> dtos = service.findByEspace(espaceId).stream()
                .filter(this::isActif)
                .map(domain -> mapper.toDto(domain, RoleAccesMappingUtil.mapForRessource(roleAccesService, "espace", espaceId)))
                .toList();
        return buildListResponse(dtos, HttpStatus.OK);
    }

    @GetMapping("/espaces/{espaceId}/domaines-analytiques/{id}")
    public ResponseEntity<RestResponse<DomaineAnalytiqueDto>> getDomainInEspace(@PathVariable Long espaceId,
            @PathVariable Long id) {
        Optional<DomaineAnalytiqueDto> dto = findAccessibleDomainInEspace(espaceId, id)
                .map(domain -> mapper.toDto(domain,
                        RoleAccesMappingUtil.mapForRessource(roleAccesService, "espace", espaceId)));
        if (dto.isEmpty()) {
            return createForbiddenResponse();
        }
        return buildItemResponse(dto.get(), HttpStatus.OK);
    }

    @GetMapping("/espaces/{espaceId}/domaines-analytiques/{id}/categories")
    public ResponseEntity<RestResponse<DomaineAnalytiqueDto>> getCategoriesInEspace(@PathVariable Long espaceId,
            @PathVariable Long id) {
        return getDomainInEspace(espaceId, id);
    }

    @GetMapping("/espaces/{espaceId}/domaines-analytiques/{id}/categories/{categoryId}")
    public ResponseEntity<RestResponse<CategorieAnalytiqueDto>> getCategoryInEspace(@PathVariable Long espaceId,
            @PathVariable Long id,
            @PathVariable Long categoryId) {
        Optional<CategorieAnalytiqueDto> dto = findAccessibleCategoryInEspace(espaceId, id, categoryId)
                .map(mapper::toCategoryDto);
        if (dto.isEmpty()) {
            return createForbiddenResponse();
        }
        return buildItemResponse(dto.get(), HttpStatus.OK);
    }

    @GetMapping("/tb-groups/{tbGroupId}/domaines-analytiques")
    public ResponseEntity<RestResponse<List<DomaineAnalytiqueDto>>> getByTbGroup(@PathVariable Long tbGroupId) {
        if (!hasResourceAccess(tbGroupId, "tableauBord", "lecture")) {
            return createForbiddenResponse();
        }
        List<DomaineAnalytiqueDto> dtos = service.findByTbGroup(tbGroupId).stream()
                .filter(this::isActif)
                .map(domain -> mapper.toDto(domain,
                        RoleAccesMappingUtil.mapForRessource(roleAccesService, "tableauBord", tbGroupId)))
                .toList();
        return buildListResponse(dtos, HttpStatus.OK);
    }

    @GetMapping("/tb-groups/{tbGroupId}/domaines-analytiques/{id}")
    public ResponseEntity<RestResponse<DomaineAnalytiqueDto>> getDomainInTbGroup(@PathVariable Long tbGroupId,
            @PathVariable Long id) {
        Optional<DomaineAnalytiqueDto> dto = findAccessibleDomainInTbGroup(tbGroupId, id)
                .map(domain -> mapper.toDto(domain,
                        RoleAccesMappingUtil.mapForRessource(roleAccesService, "tableauBord", tbGroupId)));
        if (dto.isEmpty()) {
            return createForbiddenResponse();
        }
        return buildItemResponse(dto.get(), HttpStatus.OK);
    }

    @GetMapping("/tb-groups/{tbGroupId}/domaines-analytiques/{id}/categories")
    public ResponseEntity<RestResponse<DomaineAnalytiqueDto>> getCategoriesInTbGroup(@PathVariable Long tbGroupId,
            @PathVariable Long id) {
        return getDomainInTbGroup(tbGroupId, id);
    }

    @GetMapping("/tb-groups/{tbGroupId}/domaines-analytiques/{id}/categories/{categoryId}")
    public ResponseEntity<RestResponse<CategorieAnalytiqueDto>> getCategoryInTbGroup(@PathVariable Long tbGroupId,
            @PathVariable Long id,
            @PathVariable Long categoryId) {
        Optional<CategorieAnalytiqueDto> dto = findAccessibleCategoryInTbGroup(tbGroupId, id, categoryId)
                .map(mapper::toCategoryDto);
        if (dto.isEmpty()) {
            return createForbiddenResponse();
        }
        return buildItemResponse(dto.get(), HttpStatus.OK);
    }

    @GetMapping("/tb-groups/{tbGroupId}/domaines-analytiques/{id}/categories/{categoryId}/tbd")
    public ResponseEntity<RestResponse<TbdDashboardFullDto>> getCategoryTbd(@PathVariable Long tbGroupId,
            @PathVariable Long id,
            @PathVariable Long categoryId) {
        Optional<CategorieAnalytique> categoryOpt = findAccessibleCategoryInTbGroup(tbGroupId, id, categoryId);
        Long tbdId = categoryOpt
                .map(CategorieAnalytique::getTbdDashboard)
                .filter(Objects::nonNull)
                .map(tbd -> tbd.getId())
                .orElse(null);
        if (tbdId == null) {
            return createForbiddenResponse();
        }
        TbdDashboardFullDto dto = tbdDashboardService.findById(tbdId);
        return ResponseEntity.ok(RestResponse.<TbdDashboardFullDto>builder()
                .status(HttpStatus.OK)
                .success(true)
                .data(dto)
                .build());
    }

    @Override
    protected <DTO> DTO mapToDto(Object entity, Class<DTO> dtoClass) {
        throw new UnsupportedOperationException("Not used by PortailAnalytiquePublicController");
    }

    private <T> ResponseEntity<RestResponse<T>> buildItemResponse(T data, HttpStatus status) {
        return ResponseEntity.status(status).body(RestResponse.<T>builder()
                .status(status)
                .success(true)
                .data(data)
                .build());
    }

    private <T> ResponseEntity<RestResponse<List<T>>> buildListResponse(List<T> data, HttpStatus status) {
        return ResponseEntity.status(status).body(RestResponse.<List<T>>builder()
                .status(status)
                .success(true)
                .data(data)
                .build());
    }

    private Optional<DomaineAnalytique> findAccessibleDomainInEspace(Long espaceId, Long domaineAnalytiqueId) {
        if (!hasResourceAccess(espaceId, "espace", "lecture")) {
            return Optional.empty();
        }
        return service.findByEspace(espaceId).stream()
                .filter(this::isActif)
                .filter(domain -> Objects.equals(domain.getId(), domaineAnalytiqueId))
                .findFirst();
    }

    private Optional<DomaineAnalytique> findAccessibleDomainInTbGroup(Long tbGroupId, Long domaineAnalytiqueId) {
        if (!hasResourceAccess(tbGroupId, "tableauBord", "lecture")) {
            return Optional.empty();
        }
        return service.findByTbGroup(tbGroupId).stream()
                .filter(this::isActif)
                .filter(domain -> Objects.equals(domain.getId(), domaineAnalytiqueId))
                .findFirst();
    }

    private Optional<CategorieAnalytique> findAccessibleCategoryInEspace(Long espaceId, Long domaineAnalytiqueId,
            Long categoryId) {
        Optional<DomaineAnalytique> domain = findAccessibleDomainInEspace(espaceId, domaineAnalytiqueId);
        if (domain.isEmpty()) {
            return Optional.empty();
        }
        return service.findCategoryById(categoryId)
                .filter(category -> isActif(category)
                        && category.getDomaineAnalytique() != null
                        && Objects.equals(category.getDomaineAnalytique().getId(), domain.get().getId()));
    }

    private Optional<CategorieAnalytique> findAccessibleCategoryInTbGroup(Long tbGroupId, Long domaineAnalytiqueId,
            Long categoryId) {
        Optional<DomaineAnalytique> domain = findAccessibleDomainInTbGroup(tbGroupId, domaineAnalytiqueId);
        if (domain.isEmpty()) {
            return Optional.empty();
        }
        return service.findCategoryById(categoryId)
                .filter(category -> isActif(category)
                        && category.getDomaineAnalytique() != null
                        && Objects.equals(category.getDomaineAnalytique().getId(), domain.get().getId()));
    }

    private boolean isActif(DomaineAnalytique domain) {
        return Boolean.TRUE.equals(domain.getActif());
    }

    private boolean isActif(CategorieAnalytique category) {
        return Boolean.TRUE.equals(category.getActif());
    }
}
