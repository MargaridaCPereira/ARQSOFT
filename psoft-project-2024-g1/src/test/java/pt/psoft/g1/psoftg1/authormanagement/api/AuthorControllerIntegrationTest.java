package pt.psoft.g1.psoftg1.authormanagement.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.services.AuthorService;
import pt.psoft.g1.psoftg1.authormanagement.services.CreateAuthorRequest;
import pt.psoft.g1.psoftg1.shared.services.FileStorageService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private AuthorController authorController;

    @Mock
    private AuthorService authorService;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private AuthorViewMapper authorViewMapper;

    private CreateAuthorRequest createAuthorRequest;
    private Author mockAuthor;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        MockitoAnnotations.openMocks(this);

        // Initialize CreateAuthorRequest with test data
        createAuthorRequest = new CreateAuthorRequest();
        createAuthorRequest.setName("John Doe"); // Nome válido
        createAuthorRequest.setBio("Author bio"); // Biografia válida
        createAuthorRequest.setPhoto(null); // Supondo que a foto pode ser nula
        createAuthorRequest.setIdType("hexadecimal"); // Defina um valor válido para idType

        // Create the expected Author object
        mockAuthor = new Author("John Doe", "Author bio", null);
    }

    @Test
    @WithMockUser(roles = "LIBRARIAN")
    void testCreateAuthorSuccessfully() throws Exception {
        // Mock the behavior of the services
        when(authorService.create(any(CreateAuthorRequest.class))).thenReturn(mockAuthor);

        // Perform the request
        mockMvc.perform(post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAuthorRequest)))
                .andExpect(status().isCreated()); // Check response status is 201
    }
}
