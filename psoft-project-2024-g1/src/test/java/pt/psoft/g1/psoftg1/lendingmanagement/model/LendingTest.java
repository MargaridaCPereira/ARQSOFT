package pt.psoft.g1.psoftg1.lendingmanagement.model;

import org.hibernate.StaleObjectStateException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.mockito.Mockito.when;
import org.springframework.web.context.WebApplicationContext;

import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.repositories.BookRepository;
import pt.psoft.g1.psoftg1.exceptions.NotFoundException;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.lendingmanagement.api.LendingController;
import pt.psoft.g1.psoftg1.lendingmanagement.api.LendingView;
import pt.psoft.g1.psoftg1.lendingmanagement.api.LendingViewMapper;
import pt.psoft.g1.psoftg1.lendingmanagement.repositories.LendingRepository;
import pt.psoft.g1.psoftg1.lendingmanagement.services.CreateLendingRequest;
import pt.psoft.g1.psoftg1.lendingmanagement.services.LendingServiceImpl;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.repositories.ReaderRepository;
import pt.psoft.g1.psoftg1.readermanagement.services.ReaderService;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@PropertySource({ "classpath:config/library.properties" })
class LendingTest {

    private static final ArrayList<Author> authors = new ArrayList<>();

    private Book book;
    private ReaderDetails readerDetails;

    @Value("${lendingDurationInDays}")
    private int lendingDurationInDays = 14; // Set default value for testing

    @Value("${fineValuePerDayInCents}")
    private int fineValuePerDayInCents = 100; // Set default value for testing

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ReaderRepository readerRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ReaderService readerService;

    @Mock
    private LendingRepository lendingRepository;

    @Mock
    private LendingViewMapper lendingViewMapper;

    @InjectMocks
    private LendingController lendingController;

    @Mock
    private LendingServiceImpl lendingService;

    private CreateLendingRequest createLendingRequest;
    private Lending lending;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {

        objectMapper = new ObjectMapper(); // Criação da instância do ObjectMapper

        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(lendingController).build();

        // Initialize the author and book
        Author author = new Author("Manuel Antonio Pina",
                "Manuel António Pina foi um jornalista e escritor português, premiado em 2011 com o Prémio Camões",
                null);
        authors.add(author);

        book = new Book("9782826012092",
                "O Inspetor Max",
                "conhecido pastor-alemão que trabalha para a Judiciária, vai ser fundamental para resolver um importante caso de uma rede de malfeitores que quer colocar uma bomba num megaconcerto de uma ilustre cantora",
                new Genre("Romance"),
                authors,
                null);

        readerDetails = new ReaderDetails(1,
                Reader.newReader("manuel@gmail.com", "Manuelino123!", "Manuel Sarapinto das Coives"),
                "2000-01-01",
                "919191919",
                true,
                true,
                true,
                null,
                null);

        // Setup for creating a new lending request
        createLendingRequest = new CreateLendingRequest();
        createLendingRequest.setIsbn(book.getIsbn());
        createLendingRequest.setReaderNumber(readerDetails.getReaderNumber());

        // Instance of Lending to use in tests
        lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents,
                "79810b4efe785523d4fd16b9");

        when(bookRepository.findByIsbn(book.getIsbn())).thenReturn(Optional.of(book));
        when(readerRepository.findByReaderNumber(readerDetails.getReaderNumber()))
                .thenReturn(Optional.of(readerDetails));
    }

    @Test
    void ensureBookNotNull() {
        assertThrows(IllegalArgumentException.class, () -> new Lending(null, readerDetails, 1, lendingDurationInDays,
                fineValuePerDayInCents, "79810b4efe785523d4fd16b9"));
    }

    @Test
    void ensureReaderNotNull() {
        assertThrows(IllegalArgumentException.class, () -> new Lending(book, null, 1, lendingDurationInDays,
                fineValuePerDayInCents, "79810b4efe785523d4fd16b9"));
    }

    @Test
    void ensureValidReaderNumber() {
        assertThrows(IllegalArgumentException.class, () -> new Lending(book, readerDetails, -1, lendingDurationInDays,
                fineValuePerDayInCents, "79810b4efe785523d4fd16b9"));
    }

    @Test
    void testSetReturned() {
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents,
                "79810b4efe785523d4fd16b9");
        lending.setReturned(0, null);
        assertEquals(LocalDate.now(), lending.getReturnedDate());
    }

    @Test
    void testGetDaysDelayed() {
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents,
                "79810b4efe785523d4fd16b9");
        assertEquals(0, lending.getDaysDelayed());
    }

    @Test
    void testGetDaysUntilReturn() {
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents,
                "79810b4efe785523d4fd16b9");
        assertEquals(Optional.of(lendingDurationInDays), lending.getDaysUntilReturn());
    }

    @Test
    void testGetDaysOverDue() {
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents,
                "79810b4efe785523d4fd16b9");
        assertEquals(Optional.empty(), lending.getDaysOverdue());
    }

    @Test
    void testGetTitle() {
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents,
                "79810b4efe785523d4fd16b9");
        assertEquals("O Inspetor Max", lending.getTitle());
    }

    @Test
    void testGetLendingNumber() {
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents,
                "79810b4efe785523d4fd16b9");
        assertEquals(LocalDate.now().getYear() + "/1", lending.getLendingNumber());
    }

    @Test
    void testGetBook() {
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents,
                "79810b4efe785523d4fd16b9");
        assertEquals(book, lending.getBook());
    }

    @Test
    void testGetReaderDetails() {
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents,
                "79810b4efe785523d4fd16b9");
        assertEquals(readerDetails, lending.getReaderDetails());
    }

    @Test
    void testGetStartDate() {
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents,
                "79810b4efe785523d4fd16b9");
        assertEquals(LocalDate.now(), lending.getStartDate());
    }

    @Test
    void testGetLimitDate() {
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents,
                "79810b4efe785523d4fd16b9");
        assertEquals(LocalDate.now().plusDays(lendingDurationInDays), lending.getLimitDate());
    }

    @Test
    void testGetReturnedDate() {
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents,
                "79810b4efe785523d4fd16b9");
        assertNull(lending.getReturnedDate());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateLendingSuccessfully() throws Exception {
        // Create the CreateLendingRequest
        CreateLendingRequest createLendingRequest = new CreateLendingRequest();
        createLendingRequest.setReaderNumber(readerDetails.getReaderNumber());
        createLendingRequest.setIsbn(book.getIsbn());

        // Create the expected Lending object
        Lending expectedLending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents,
                "79810b4efe785523d4fd16b9");

        // Mock the behavior of the repositories and services
        when(bookRepository.findByIsbn(createLendingRequest.getIsbn())).thenReturn(Optional.of(book));
        when(readerRepository.findByReaderNumber(createLendingRequest.getReaderNumber()))
                .thenReturn(Optional.of(readerDetails));
        when(lendingService.create(any(CreateLendingRequest.class))).thenReturn(expectedLending);

        // Prepare LendingView
        LendingView lendingView = lendingViewMapper.toLendingView(expectedLending);

        // Perform the request
        mockMvc.perform(post("/api/lendings/create") // Ensure the URL matches your endpoint
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createLendingRequest))) // Convert to JSON
                .andExpect(status().isCreated()); // Check response body
    }

    @Test
    void testSetReturnedWithConcurrentUpdate() {
        // Simulação de erro de concorrência usando versão desatualizada
        Lending lending = new Lending(book, readerDetails, 1, 14, 100, "79810b4efe785523d4fd16b9");
        lending.setReturned(lending.getVersion(), null); // First call to mark as returned

        // Attempt to return the book again should throw IllegalArgumentException
        assertThrows(IllegalArgumentException.class,
                () -> lending.setReturned(lending.getVersion(), "Outro comentário"));
    }
}
