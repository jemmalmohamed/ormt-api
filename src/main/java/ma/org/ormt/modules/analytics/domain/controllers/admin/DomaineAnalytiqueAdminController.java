package ma.org.ormt.modules.analytics.domain.controllers.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ma.org.ormt.core.commun.rest.responses.RestResponse;
import ma.org.ormt.modules.analytics.association.espace.EspaceDomaineAnalytique;
import ma.org.ormt.modules.analytics.association.espace.dtos.EspaceDomaineAnalytiqueLinkDto;
import ma.org.ormt.modules.analytics.association.tbgroup.TbGroupDomaineAnalytique;
import ma.org.ormt.modules.analytics.association.tbgroup.dtos.TbGroupDomaineAnalytiqueLinkDto;
import ma.org.ormt.modules.analytics.category.dtos.CategorieAnalytiqueDto;
import ma.org.ormt.modules.analytics.category.dtos.CategorieAnalytiqueSectionDto;
import ma.org.ormt.modules.analytics.category.dtos.request.CategorieAnalytiqueRequestDto;
import ma.org.ormt.modules.analytics.category.dtos.request.CategorieAnalytiqueSectionRequestDto;
import ma.org.ormt.modules.analytics.domain.DomaineAnalytiqueMapper;
import ma.org.ormt.modules.analytics.domain.dtos.DomaineAnalytiqueDto;
import ma.org.ormt.modules.analytics.domain.dtos.DomaineAnalytiqueSectionDto;
import ma.org.ormt.modules.analytics.domain.dtos.PortailAnalytiqueTransitionSummaryDto;
import ma.org.ormt.modules.analytics.domain.dtos.request.DomaineAnalytiqueLinkRequestDto;
import ma.org.ormt.modules.analytics.domain.dtos.request.DomaineAnalytiqueRequestDto;
import ma.org.ormt.modules.analytics.domain.dtos.request.DomaineAnalytiqueSectionRequestDto;
import ma.org.ormt.modules.analytics.domain.dtos.request.ReorderItemsRequest;
import ma.org.ormt.modules.analytics.domain.services.DomaineAnalytiqueService;
import ma.org.ormt.modules.roleacces.dtos.summary.RoleAccesSummaryDto;

@Validated
@RestController
@RequestMapping("/api/v1/admin/domaines-analytiques")
@RequiredArgsConstructor
public class DomaineAnalytiqueAdminController {

    private final DomaineAnalytiqueService service;
    private final DomaineAnalytiqueMapper mapper;

    @GetMapping
    @PreAuthorize("hasAuthority('dashboard:list')")
    public ResponseEntity<RestResponse<List<DomaineAnalytiqueDto>>> findAll() {
        List<DomaineAnalytiqueDto> dtos = service.findAll().stream()
                .map(domain -> mapper.toDto(domain, List.<RoleAccesSummaryDto>of()))
                .toList();
        return response(dtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('dashboard:read')")
    public ResponseEntity<RestResponse<DomaineAnalytiqueDto>> findById(@PathVariable Long id) {
        DomaineAnalytiqueDto dto = mapper.toDto(service.findById(id).orElseThrow(), List.of());
        return response(dto, HttpStatus.OK);
    }

    @GetMapping("/{id}/sections")
    @PreAuthorize("hasAuthority('dashboard:read')")
    public ResponseEntity<RestResponse<List<DomaineAnalytiqueSectionDto>>> findSectionsByDomaine(@PathVariable Long id) {
        List<DomaineAnalytiqueSectionDto> dtos = service.findSectionsByDomaineAnalytique(id).stream()
                .map(mapper::toDomainSectionDto)
                .toList();
        return response(dtos, HttpStatus.OK);
    }

    @GetMapping("/sections/{sectionId}")
    @PreAuthorize("hasAuthority('dashboard:read')")
    public ResponseEntity<RestResponse<DomaineAnalytiqueSectionDto>> findSectionById(@PathVariable Long sectionId) {
        DomaineAnalytiqueSectionDto dto = mapper.toDomainSectionDto(service.findSectionById(sectionId).orElseThrow());
        return response(dto, HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('dashboard:create')")
    public ResponseEntity<RestResponse<DomaineAnalytiqueDto>> create(
            @Validated @ModelAttribute DomaineAnalytiqueRequestDto requestDto) throws Exception {
        return response(mapper.toDto(service.create(requestDto), List.of()), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('dashboard:edit')")
    public ResponseEntity<RestResponse<DomaineAnalytiqueDto>> update(
            @PathVariable Long id,
            @Validated @ModelAttribute DomaineAnalytiqueRequestDto requestDto) throws Exception {
        return response(mapper.toDto(service.update(id, requestDto), List.of()), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('dashboard:delete')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sections")
    @PreAuthorize("hasAuthority('dashboard:edit')")
    public ResponseEntity<RestResponse<DomaineAnalytiqueDto>> createSection(
            @PathVariable Long id,
            @Validated @RequestBody DomaineAnalytiqueSectionRequestDto requestDto) {
        service.createSection(id, requestDto);
        return response(mapper.toDto(service.findById(id).orElseThrow(), List.of()), HttpStatus.OK);
    }

    @PutMapping("/sections/{sectionId}")
    @PreAuthorize("hasAuthority('dashboard:edit')")
    public ResponseEntity<Void> updateSection(
            @PathVariable Long sectionId,
            @Validated @RequestBody DomaineAnalytiqueSectionRequestDto requestDto) {
        service.updateSection(sectionId, requestDto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/sections/reorder")
    @PreAuthorize("hasAuthority('dashboard:edit')")
    public ResponseEntity<Void> reorderSections(@PathVariable Long id,
            @Validated @RequestBody ReorderItemsRequest request) {
        service.reorderSections(id, request.getItems());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/sections/{sectionId}")
    @PreAuthorize("hasAuthority('dashboard:delete')")
    public ResponseEntity<Void> deleteSection(@PathVariable Long sectionId) {
        service.deleteSection(sectionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/categories")
    @PreAuthorize("hasAuthority('dashboard:edit')")
    public ResponseEntity<RestResponse<DomaineAnalytiqueDto>> createCategory(
            @PathVariable Long id,
            @Validated @RequestBody CategorieAnalytiqueRequestDto requestDto) {
        requestDto.setDomaineAnalytiqueId(id);
        service.createCategory(requestDto);
        return response(mapper.toDto(service.findById(id).orElseThrow(), List.of()), HttpStatus.OK);
    }

    @GetMapping("/{id}/categories")
    @PreAuthorize("hasAuthority('dashboard:read')")
    public ResponseEntity<RestResponse<List<CategorieAnalytiqueDto>>> findCategoriesByDomaine(@PathVariable Long id) {
        List<CategorieAnalytiqueDto> dtos = service.findCategoriesByDomaineAnalytique(id).stream()
                .map(mapper::toCategoryDto)
                .toList();
        return response(dtos, HttpStatus.OK);
    }

    @GetMapping("/categories/{categoryId}")
    @PreAuthorize("hasAuthority('dashboard:read')")
    public ResponseEntity<RestResponse<CategorieAnalytiqueDto>> findCategoryById(@PathVariable Long categoryId) {
        CategorieAnalytiqueDto dto = mapper.toCategoryDto(service.findCategoryById(categoryId).orElseThrow());
        return response(dto, HttpStatus.OK);
    }

    @PutMapping("/categories/{categoryId}")
    @PreAuthorize("hasAuthority('dashboard:edit')")
    public ResponseEntity<Void> updateCategory(
            @PathVariable Long categoryId,
            @Validated @RequestBody CategorieAnalytiqueRequestDto requestDto) {
        service.updateCategory(categoryId, requestDto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/categories/reorder")
    @PreAuthorize("hasAuthority('dashboard:edit')")
    public ResponseEntity<Void> reorderCategories(@PathVariable Long id,
            @Validated @RequestBody ReorderItemsRequest request) {
        service.reorderCategories(id, request.getItems());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/categories/{categoryId}")
    @PreAuthorize("hasAuthority('dashboard:delete')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        service.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/categories/{categoryId}/sections")
    @PreAuthorize("hasAuthority('dashboard:edit')")
    public ResponseEntity<Void> createCategorySection(
            @PathVariable Long categoryId,
            @Validated @RequestBody CategorieAnalytiqueSectionRequestDto requestDto) {
        service.createCategorySection(categoryId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/categories/{categoryId}/sections")
    @PreAuthorize("hasAuthority('dashboard:read')")
    public ResponseEntity<RestResponse<List<CategorieAnalytiqueSectionDto>>> findCategorySections(
            @PathVariable Long categoryId) {
        List<CategorieAnalytiqueSectionDto> dtos = service.findSectionsByCategory(categoryId).stream()
                .map(mapper::toCategorySectionDto)
                .toList();
        return response(dtos, HttpStatus.OK);
    }

    @GetMapping("/categories/sections/{sectionId}")
    @PreAuthorize("hasAuthority('dashboard:read')")
    public ResponseEntity<RestResponse<CategorieAnalytiqueSectionDto>> findCategorySectionById(
            @PathVariable Long sectionId) {
        CategorieAnalytiqueSectionDto dto = mapper
                .toCategorySectionDto(service.findCategorySectionById(sectionId).orElseThrow());
        return response(dto, HttpStatus.OK);
    }

    @PutMapping("/categories/sections/{sectionId}")
    @PreAuthorize("hasAuthority('dashboard:edit')")
    public ResponseEntity<Void> updateCategorySection(
            @PathVariable Long sectionId,
            @Validated @RequestBody CategorieAnalytiqueSectionRequestDto requestDto) {
        service.updateCategorySection(sectionId, requestDto);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/categories/{categoryId}/sections/reorder")
    @PreAuthorize("hasAuthority('dashboard:edit')")
    public ResponseEntity<Void> reorderCategorySections(@PathVariable Long categoryId,
            @Validated @RequestBody ReorderItemsRequest request) {
        service.reorderCategorySections(categoryId, request.getItems());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/categories/sections/{sectionId}")
    @PreAuthorize("hasAuthority('dashboard:delete')")
    public ResponseEntity<Void> deleteCategorySection(@PathVariable Long sectionId) {
        service.deleteCategorySection(sectionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/espaces/{espaceId}")
    @PreAuthorize("hasAuthority('espace:read')")
    public ResponseEntity<RestResponse<List<DomaineAnalytiqueDto>>> getEspaceLinks(@PathVariable Long espaceId) {
        List<DomaineAnalytiqueDto> dtos = service.findEspaceLinks(espaceId).stream()
                .map(EspaceDomaineAnalytique::getDomaineAnalytique)
                .map(domain -> mapper.toDto(service.findById(domain.getId()).orElseThrow(), List.of()))
                .toList();
        return response(dtos, HttpStatus.OK);
    }

    @GetMapping("/espaces/{espaceId}/associations")
    @PreAuthorize("hasAuthority('espace:read')")
    public ResponseEntity<RestResponse<List<EspaceDomaineAnalytiqueLinkDto>>> getEspaceAssociationLinks(
            @PathVariable Long espaceId) {
        List<EspaceDomaineAnalytiqueLinkDto> dtos = service.findEspaceLinks(espaceId).stream()
                .map(mapper::toEspaceLinkDto)
                .toList();
        return response(dtos, HttpStatus.OK);
    }

    @PostMapping("/espaces/{espaceId}")
    @PreAuthorize("hasAuthority('espace:edit')")
    public ResponseEntity<Void> attachToEspace(
            @PathVariable Long espaceId,
            @Validated @RequestBody DomaineAnalytiqueLinkRequestDto requestDto) {
        service.attachToEspace(espaceId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/espaces/{espaceId}/reorder")
    @PreAuthorize("hasAuthority('espace:edit')")
    public ResponseEntity<Void> reorderEspaceLinks(@PathVariable Long espaceId,
            @Validated @RequestBody ReorderItemsRequest request) {
        service.reorderEspaceLinks(espaceId, request.getItems());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/espaces/{espaceId}/{domaineAnalytiqueId}")
    @PreAuthorize("hasAuthority('espace:edit')")
    public ResponseEntity<Void> detachFromEspace(@PathVariable Long espaceId, @PathVariable Long domaineAnalytiqueId) {
        service.detachFromEspace(espaceId, domaineAnalytiqueId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/tb-groups/{tbGroupId}")
    @PreAuthorize("hasAuthority('dashboard:read')")
    public ResponseEntity<RestResponse<List<DomaineAnalytiqueDto>>> getTbGroupLinks(@PathVariable Long tbGroupId) {
        List<DomaineAnalytiqueDto> dtos = service.findTbGroupLinks(tbGroupId).stream()
                .map(TbGroupDomaineAnalytique::getDomaineAnalytique)
                .map(domain -> mapper.toDto(service.findById(domain.getId()).orElseThrow(), List.of()))
                .toList();
        return response(dtos, HttpStatus.OK);
    }

    @GetMapping("/tb-groups/{tbGroupId}/associations")
    @PreAuthorize("hasAuthority('dashboard:read')")
    public ResponseEntity<RestResponse<List<TbGroupDomaineAnalytiqueLinkDto>>> getTbGroupAssociationLinks(
            @PathVariable Long tbGroupId) {
        List<TbGroupDomaineAnalytiqueLinkDto> dtos = service.findTbGroupLinks(tbGroupId).stream()
                .map(mapper::toTbGroupLinkDto)
                .toList();
        return response(dtos, HttpStatus.OK);
    }

    @PostMapping("/tb-groups/{tbGroupId}")
    @PreAuthorize("hasAuthority('dashboard:edit')")
    public ResponseEntity<Void> attachToTbGroup(
            @PathVariable Long tbGroupId,
            @Validated @RequestBody DomaineAnalytiqueLinkRequestDto requestDto) {
        service.attachToTbGroup(tbGroupId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/tb-groups/{tbGroupId}/reorder")
    @PreAuthorize("hasAuthority('dashboard:edit')")
    public ResponseEntity<Void> reorderTbGroupLinks(@PathVariable Long tbGroupId,
            @Validated @RequestBody ReorderItemsRequest request) {
        service.reorderTbGroupLinks(tbGroupId, request.getItems());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/tb-groups/{tbGroupId}/{domaineAnalytiqueId}")
    @PreAuthorize("hasAuthority('dashboard:edit')")
    public ResponseEntity<Void> detachFromTbGroup(@PathVariable Long tbGroupId,
            @PathVariable Long domaineAnalytiqueId) {
        service.detachFromTbGroup(tbGroupId, domaineAnalytiqueId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/transition/summary")
    @PreAuthorize("hasAuthority('dashboard:read')")
    public ResponseEntity<RestResponse<PortailAnalytiqueTransitionSummaryDto>> getTransitionSummary() {
        return response(service.getTransitionSummary(), HttpStatus.OK);
    }

    private <T> ResponseEntity<RestResponse<T>> response(T data, HttpStatus status) {
        return ResponseEntity.status(status).body(RestResponse.<T>builder()
                .status(status)
                .success(true)
                .data(data)
                .build());
    }
}
