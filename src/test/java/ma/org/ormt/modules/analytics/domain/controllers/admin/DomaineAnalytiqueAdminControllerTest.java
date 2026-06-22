package ma.org.ormt.modules.analytics.domain.controllers.admin;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import ma.org.ormt.core.exceptions.GlobalExceptionHandler;
import ma.org.ormt.core.validators.ObjectsValidator;
import ma.org.ormt.modules.analytics.domain.DomaineAnalytiqueMapper;
import ma.org.ormt.modules.analytics.domain.services.DomaineAnalytiqueService;

@ExtendWith(MockitoExtension.class)
class DomaineAnalytiqueAdminControllerTest {

    @Mock
    private DomaineAnalytiqueService domaineAnalytiqueService;

    @Mock
    private DomaineAnalytiqueMapper domaineAnalytiqueMapper;

    @Mock
    private ObjectsValidator<Object> objectsValidator;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        DomaineAnalytiqueAdminController controller = new DomaineAnalytiqueAdminController(
                domaineAnalytiqueService,
                domaineAnalytiqueMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler(objectsValidator))
                .build();
    }

    @Test
    void createReturnsUnprocessableEntityWhenBusinessConflictOccurs() throws Exception {
        doThrow(new IllegalStateException("Un domaine analytique avec ce slug existe déjà."))
                .when(domaineAnalytiqueService)
                .create(any());

        mockMvc.perform(post("/api/v1/admin/domaines-analytiques")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CreateDomainPayload(
                        "climat",
                        "Climat",
                        true))))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value("Un domaine analytique avec ce slug existe déjà."))
                .andExpect(jsonPath("$.status").value("422 UNPROCESSABLE_ENTITY"));
    }

    private record CreateDomainPayload(String nom, String titre, Boolean actif) {
    }
}
