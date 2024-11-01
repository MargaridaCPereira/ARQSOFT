package pt.psoft.g1.psoftg1.lendingmanagement.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.lendingmanagement.repositories.LendingRepository;
import pt.psoft.g1.psoftg1.lendingmanagement.services.LendingServiceImpl;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.services.ReaderService;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@PropertySource({"classpath:config/library.properties"})
class LendingTest {

    private static final ArrayList<Author> authors = new ArrayList<>();
    private Book book;
    private ReaderDetails readerDetails;

    @Value("${lendingDurationInDays}")
    private int lendingDurationInDays;
    
    @Value("${fineValuePerDayInCents}")
    private int fineValuePerDayInCents;

    
    @Mock
    private ReaderService readerService;

    @Mock
    private LendingRepository lendingRepository;

    @InjectMocks
    private LendingServiceImpl lendingService;


    @BeforeEach
    public void setup() {

        MockitoAnnotations.openMocks(this);
        // Inicializar o autor e o livro
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

    }

    @Test
    void ensureBookNotNull() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Lending(null, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents, "79810b4efe785523d4fd16b9"));
    }

    @Test
    void ensureReaderNotNull() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Lending(book, null, 1, lendingDurationInDays, fineValuePerDayInCents, "79810b4efe785523d4fd16b9"));
    }

    @Test
    void ensureValidReaderNumber() {
        assertThrows(IllegalArgumentException.class, () -> 
            new Lending(book, readerDetails, -1, lendingDurationInDays, fineValuePerDayInCents, "79810b4efe785523d4fd16b9"));
    }

    @Test
    void testSetReturned() {
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents, "79810b4efe785523d4fd16b9");
        lending.setReturned(0, null);
        assertEquals(LocalDate.now(), lending.getReturnedDate());
    }

    @Test
    void testGetDaysDelayed() {
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents, "79810b4efe785523d4fd16b9");
        assertEquals(0, lending.getDaysDelayed());
    }

    @Test
    void testGetDaysUntilReturn() {
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents, "79810b4efe785523d4fd16b9");
        assertEquals(Optional.of(lendingDurationInDays), lending.getDaysUntilReturn());
    }

    @Test
    void testGetDaysOverDue() {
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents, "79810b4efe785523d4fd16b9");
        assertEquals(Optional.empty(), lending.getDaysOverdue());
    }

    @Test
    void testGetTitle() {
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents, "79810b4efe785523d4fd16b9");
        assertEquals("O Inspetor Max", lending.getTitle());
    }

    @Test
    void testGetLendingNumber() {
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents, "79810b4efe785523d4fd16b9");
        assertEquals(LocalDate.now().getYear() + "/1", lending.getLendingNumber());
    }

    @Test
    void testGetBook() {
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents, "79810b4efe785523d4fd16b9");
        assertEquals(book, lending.getBook());
    }

    @Test
    void testGetReaderDetails() {
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents, "79810b4efe785523d4fd16b9");
        assertEquals(readerDetails, lending.getReaderDetails());
    }

    @Test
    void testGetStartDate() {
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents, "79810b4efe785523d4fd16b9");
        assertEquals(LocalDate.now(), lending.getStartDate());
    }

    @Test
    void testGetLimitDate() {
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents, "79810b4efe785523d4fd16b9");
        assertEquals(LocalDate.now().plusDays(lendingDurationInDays), lending.getLimitDate());
    }

    @Test
    void testGetReturnedDate() {
        Lending lending = new Lending(book, readerDetails, 1, lendingDurationInDays, fineValuePerDayInCents, "79810b4efe785523d4fd16b9");
        assertNull(lending.getReturnedDate());
    }



    
    
    
}
