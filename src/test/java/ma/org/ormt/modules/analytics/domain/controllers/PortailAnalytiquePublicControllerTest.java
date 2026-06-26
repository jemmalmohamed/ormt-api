package ma.org.ormt.modules.analytics.domain.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ma.org.ormt.modules.analytics.category.models.CategorieAnalytique;
import ma.org.ormt.modules.analytics.domain.DomaineAnalytiqueMapper;
import ma.org.ormt.modules.analytics.domain.dtos.DomaineAnalytiqueDto;
import ma.org.ormt.modules.analytics.domain.models.DomaineAnalytique;
import ma.org.ormt.modules.analytics.domain.services.DomaineAnalytiqueService;
import ma.org.ormt.modules.dashboard.tbd.dtos.TbdDashboardFullDto;
import ma.org.ormt.modules.dashboard.tbd.models.TbdDashboard;
import ma.org.ormt.modules.dashboard.tbd.services.TbdDashboardService;
import ma.org.ormt.modules.roleacces.services.RoleAccesService;
import ma.org.ormt.security.authentication.services.AuthService;

@ExtendWith(MockitoExtension.class)
class PortailAnalytiquePublicControllerTest {

    @Mock
    private DomaineAnalytiqueService domaineAnalytiqueService;

    @Mock
    private DomaineAnalytiqueMapper domaineAnalytiqueMapper;

    @Mock
    private TbdDashboardService tbdDashboardService;

    @Mock
    private RoleAccesService roleAccesService;

    @Mock
    private AuthService authService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        PortailAnalytiquePublicController controller = new PortailAnalytiquePublicController(
                domaineAnalytiqueService,
                domaineAnalytiqueMapper,
                tbdDashboardService);
        ReflectionTestUtils.setField(controller, "roleAccesService", roleAccesService);
        ReflectionTestUtils.setField(controller, "authService", authService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void getDomainInEspaceReturnsForbiddenWhenDomainIsInactive() throws Exception {
        when(roleAccesService.hasAccessToResource(5L, "espace", "lecture")).thenReturn(true);
        when(domaineAnalytiqueService.findByEspace(5L)).thenReturn(List.of(domain(11L, false)));

        mockMvc.perform(get("/api/v1/public/espaces/5/domaines-analytiques/11"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Permission denied"));
    }

    @Test
    void getCategoryTbdReturnsForbiddenWhenCategoryIsOutsideAllowedChain() throws Exception {
        when(roleAccesService.hasAccessToResource(10L, "tableauBord", "lecture")).thenReturn(true);
        when(domaineAnalytiqueService.findByTbGroup(10L)).thenReturn(List.of(domain(1L, true)));
        when(domaineAnalytiqueService.findCategoryById(30L)).thenReturn(Optional.of(category(30L, 2L, 99L, true)));

        mockMvc.perform(get("/api/v1/public/tb-groups/10/domaines-analytiques/1/categories/30/tbd"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Permission denied"));

        verify(tbdDashboardService, never()).findById(99L);
    }

    @Test
    void getCategoryTbdReturnsDashboardWhenChainIsValid() throws Exception {
        when(roleAccesService.hasAccessToResource(10L, "tableauBord", "lecture")).thenReturn(true);
        when(domaineAnalytiqueService.findByTbGroup(10L)).thenReturn(List.of(domain(1L, true)));
        when(domaineAnalytiqueService.findCategoryById(30L)).thenReturn(Optional.of(category(30L, 1L, 77L, true)));
        when(tbdDashboardService.findById(77L)).thenReturn(TbdDashboardFullDto.builder()
                .id(77L)
                .nom("tbd-77")
                .titre("TBD 77")
                .build());

        mockMvc.perform(get("/api/v1/public/tb-groups/10/domaines-analytiques/1/categories/30/tbd"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(77))
                .andExpect(jsonPath("$.data.titre").value("TBD 77"));
    }

    @Test
    void getCategoryInEspaceReturnsCategoryWhenChainIsValid() throws Exception {
        when(roleAccesService.hasAccessToResource(5L, "espace", "lecture")).thenReturn(true);
        when(domaineAnalytiqueService.findByEspace(5L)).thenReturn(List.of(domain(11L, true)));
        when(domaineAnalytiqueService.findCategoryById(30L)).thenReturn(Optional.of(category(30L, 11L, 77L, true)));
        when(domaineAnalytiqueMapper.toCategoryDto(any(CategorieAnalytique.class)))
                .thenReturn(categoryDto(30L, "Population"));

        mockMvc.perform(get("/api/v1/public/espaces/5/domaines-analytiques/11/categories/30"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(30))
                .andExpect(jsonPath("$.data.libelle").value("Population"));
    }

    @Test
    void getByTbGroupFiltersInactiveDomains() throws Exception {
        when(roleAccesService.hasAccessToResource(8L, "tableauBord", "lecture")).thenReturn(true);
        DomaineAnalytique active = domain(1L, true);
        DomaineAnalytique inactive = domain(2L, false);
        when(domaineAnalytiqueService.findByTbGroup(8L)).thenReturn(List.of(active, inactive));
        when(domaineAnalytiqueMapper.toDto(active, List.of())).thenReturn(dto(1L, "actif"));

        mockMvc.perform(get("/api/v1/public/tb-groups/8/domaines-analytiques"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].titre").value("actif"));
    }

    private DomaineAnalytique domain(Long id, boolean actif) {
        DomaineAnalytique domain = DomaineAnalytique.builder()
                .nom("d-" + id)
                .titre("Domain " + id)
                .slug("domain-" + id)
                .actif(actif)
                .build();
        domain.setId(id);
        return domain;
    }

    private CategorieAnalytique category(Long id, Long domainId, Long tbdId, boolean actif) {
        TbdDashboard dashboard = null;
        if (tbdId != null) {
            dashboard = TbdDashboard.builder()
                    .nom("tbd-" + tbdId)
                    .titre("TBD " + tbdId)
                    .build();
            dashboard.setId(tbdId);
        }
        CategorieAnalytique category = CategorieAnalytique.builder()
                .domaineAnalytique(domain(domainId, true))
                .tbdDashboard(dashboard)
                .nom("cat-" + id)
                .libelle("Categorie " + id)
                .slug("categorie-" + id)
                .actif(actif)
                .build();
        category.setId(id);
        return category;
    }

    private DomaineAnalytiqueDto dto(Long id, String titre) {
        DomaineAnalytiqueDto dto = new DomaineAnalytiqueDto();
        dto.setId(id);
        dto.setTitre(titre);
        return dto;
    }

    private ma.org.ormt.modules.analytics.category.dtos.CategorieAnalytiqueDto categoryDto(Long id, String libelle) {
        ma.org.ormt.modules.analytics.category.dtos.CategorieAnalytiqueDto dto = new ma.org.ormt.modules.analytics.category.dtos.CategorieAnalytiqueDto();
        dto.setId(id);
        dto.setLibelle(libelle);
        return dto;
    }
}
