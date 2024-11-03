package pt.psoft.g1.psoftg1.lendingmanagement.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.repositories.BookRepository;
import pt.psoft.g1.psoftg1.exceptions.LendingForbiddenException;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.idgeneratormanagement.IdGeneratorType;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.lendingmanagement.repositories.LendingRepository;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.repositories.ReaderRepository;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class LendingServiceImplIntegrationTest {

    @Autowired
    private LendingService lendingService;

    @MockBean
    private LendingRepository lendingRepository;

    @MockBean
    private BookRepository bookRepository;

    @MockBean
    private ReaderRepository readerRepository;

    @MockBean
    private IdGeneratorType idGeneratorType;

    private Book book;
    private ReaderDetails readerDetails; // Change from Reader to ReaderDetails

    @BeforeEach
    public void setUp() {
        // Create a mock author
        Author author = new Author("Author Name", "Author Bio", null);
        List<Author> authors = new ArrayList<>();
        authors.add(author);

        // Create a mock genre
        Genre genre = new Genre("Fiction");

        // Configure a mock book using the constructor
        book = new Book("9789720706386", "Book Title", "Book Description", genre, authors, null);

        // Configure a mock reader using ReaderDetails
        Reader mockReader = Reader.newReader("reader_username", "Reader_password1", "Reader Name");
        readerDetails = new ReaderDetails(2024 / 10, mockReader, "2000-01-01", "912345678", true, true, true, null,
                null);

        new Lending(book, readerDetails, 1, 14, 100, "b0db4fc9194bacc6b461dc85");

    }

    @Test
    public void whenCreateLending_thenLendingShouldBeCreated() {
        // Arrange
        CreateLendingRequest createLendingRequest = new CreateLendingRequest();
        createLendingRequest.setIsbn(book.getIsbn());
        createLendingRequest.setReaderNumber(readerDetails.getReaderNumber()); // Use readerDetails

        String generatedId = "5f8d0f9e3c6b2a000f1c2d3e4"; // Example hexadecimal ID

        // Mocking the behavior of repositories
        when(lendingRepository.listOutstandingByReaderNumber(readerDetails.getReaderNumber())).thenReturn(List.of());
        when(bookRepository.findByIsbn(book.getIsbn())).thenReturn(Optional.of(book));
        when(readerRepository.findByReaderNumber(readerDetails.getReaderNumber()))
                .thenReturn(Optional.of(readerDetails)); // Use readerDetails
        when(idGeneratorType.generateId()).thenReturn(generatedId);
        when(lendingRepository.getCountFromCurrentYear()).thenReturn(0);

        Lending expectedLending = new Lending(book, readerDetails, 1, 14, 10, generatedId); // Use readerDetails
        when(lendingRepository.save(any(Lending.class))).thenReturn(expectedLending);

        // Act
        Lending actualLending = lendingService.create(createLendingRequest);

        // Assert
        assertThat(actualLending).isNotNull();
        assertThat(actualLending.getLendingId()).isEqualTo(generatedId);
        assertThat(actualLending.getBook()).isEqualTo(book);
        assertThat(actualLending.getReaderDetails()).isEqualTo(readerDetails); // Use readerDetails
    }

    @Test
    public void whenReaderHasThreeOutstandingBooks_thenLendingShouldThrowException() {
        // Arrange
        CreateLendingRequest createLendingRequest = new CreateLendingRequest();
        createLendingRequest.setIsbn(book.getIsbn());
        createLendingRequest.setReaderNumber(readerDetails.getReaderNumber());

        // Mocking the behavior to simulate three outstanding books
        Lending outstandingLending1 = new Lending(book, readerDetails, 1, 14, 100, "lendingId1");
        Lending outstandingLending2 = new Lending(book, readerDetails, 2, 14, 100, "lendingId2");
        Lending outstandingLending3 = new Lending(book, readerDetails, 3, 14, 100, "lendingId3");

        // Mock do repositório
        when(lendingRepository.listOutstandingByReaderNumber(readerDetails.getReaderNumber()))
                .thenReturn(List.of(outstandingLending1, outstandingLending2, outstandingLending3));

        // Act & Assert
        assertThatExceptionOfType(LendingForbiddenException.class)
                .isThrownBy(() -> lendingService.create(createLendingRequest))
                .withMessage("Reader has three books outstanding already");
    }
}
