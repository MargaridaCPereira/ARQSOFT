package pt.psoft.g1.psoftg1.algorithmmanagement.Service;

import org.hibernate.StaleObjectStateException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import pt.psoft.g1.psoftg1.algorithmmanagement.service.AlgorithmServiceImpl;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.repositories.AuthorRepository;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.repositories.BookRepository;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.repositories.GenreRepository;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.lendingmanagement.repositories.LendingRepository;
import pt.psoft.g1.psoftg1.readermanagement.model.BirthDate;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.repositories.ReaderRepository;
import pt.psoft.g1.psoftg1.readermanagement.services.ReaderService;
import pt.psoft.g1.psoftg1.usermanagement.model.Reader;
import pt.psoft.g1.psoftg1.usermanagement.repositories.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
class AlgorithmServiceImplTest {

        private final String validIsbn = "9782826012092";
        private final String validIsbn2 = "9782722203402";


        @InjectMocks
        private AlgorithmServiceImpl algorithmServiceImpl; // Mocks serão injetados aqui

        @Mock
        private ReaderRepository readerRepository;

        @Mock
        private UserRepository userRepository;

        @Mock
        private BookRepository bookRepository;

        @Mock
        private GenreRepository genreRepository;

        @Mock
        private AuthorRepository authorRepository;

        @Mock
        private ReaderService readerService;

        private Lending lending;
        private ReaderDetails readerDetails;
        private Reader reader;
        private Book book;
        private Author author;
        private Genre genre;

        @BeforeEach
        void setUp() {
                // Create and save the author
                author = new Author("Manuel Antonio Pina",
                                "Manuel António Pina foi um jornalista e escritor português, premiado em 2011 com o Prémio Camões",
                                null);
                // Simulating save operation
                when(authorRepository.save(author)).thenReturn(author);

                // Create and save the genre
                genre = new Genre("Género");
                when(genreRepository.save(genre)).thenReturn(genre);

                // Create and save the book with the saved author and genre using a valid ISBN
                book = new Book(validIsbn, // Use a valid ISBN-13 format
                                "O Inspetor Max",
                                "conhecido pastor-alemão que trabalha para a Judiciária, vai ser fundamental para resolver um importante caso de uma rede de malfeitores que quer colocar uma bomba num megaconcerto de uma ilustre cantora",
                                genre,
                                List.of(author), // Ensure authors is a valid list
                                null);
                when(bookRepository.save(book)).thenReturn(book);

                // Create and save the book with the saved author and genre using a valid ISBN
                book = new Book(validIsbn2, // Use a valid ISBN-13 format
                                "O Inspetor Maxi",
                                "conhecidissimo pastor-alemão que trabalha para a Judiciária, vai ser fundamental para resolver um importante caso de uma rede de malfeitores que quer colocar uma bomba num megaconcerto de uma ilustre cantora",
                                genre,
                                List.of(author), // Ensure authors is a valid list
                                null);
                when(bookRepository.save(book)).thenReturn(book);

                // Create and save the reader
                reader = Reader.newReader("manuel@gmail.com", "Manuelino123!", "Manuel Sarapinto das Coives");
                when(userRepository.save(reader)).thenReturn(reader);

                // Create and save reader details
                readerDetails = new ReaderDetails(1,
                                reader,
                                "2000-01-01",
                                "919191919",
                                true,
                                true,
                                true,
                                null, null);
                when(readerRepository.save(readerDetails)).thenReturn(readerDetails);
        }

        @AfterEach
        void tearDown() {
                // Clear mocks after each test if needed
                lending = null;
                readerDetails = null;
                reader = null;
                book = null;
                author = null;
                genre = null;
        }


        @Test
        void testRecommendBooksForChild() {
                // Mocking ReaderDetails
                ReaderDetails childReader = mock(ReaderDetails.class);
                when(childReader.getBirthDate()).thenReturn(new BirthDate(LocalDate.now().minusYears(8).toString()));
                when(readerService.findByUserId(1L)).thenReturn(Optional.of(childReader));

                // Simulando livros infantis com ISBNs válidos
                List<Book> childrenBooks = List.of(
                                new Book(validIsbn2, "Livro Infantil 1", "Descrição 1", genre, List.of(author), "") // ISBN-10
                );
                // Mock para retornar apenas um livro
                when(bookRepository.getTopLentBooksByGenre("children", 1)).thenReturn(childrenBooks);

                // Execute recommendation method
                List<Book> recommendedBooks = algorithmServiceImpl.recommendBooks(1L, 1); // Mudei para 1 aqui

                // Verify results
                assertEquals(1, recommendedBooks.size()); // Espera-se que retorne 1 livro
                verify(bookRepository).getTopLentBooksByGenre("children", 1); // Verifica que o repositório foi chamado
                                                                              // corretamente
        }

        @Test
        void testRecommendBooksForTeen() {
                // Mockando os detalhes do leitor
                ReaderDetails teenReader = mock(ReaderDetails.class);
                when(teenReader.getBirthDate()).thenReturn(new BirthDate(LocalDate.now().minusYears(15).toString()));
                when(readerService.findByUserId(1L)).thenReturn(Optional.of(teenReader));

                // Simulando livros juvenis com ISBNs válidos
                String validIsbn13 = "9780136091813"; // ISBN-13 válido
                List<Book> teenBooks = List.of(
                                new Book(validIsbn13, "Livro Juvenil 1", "Descrição 1", genre, List.of(author), "") // ISBN-13
                );

                // Mock para retornar apenas um livro
                when(bookRepository.getTopLentBooksByGenre("juvenile", 1)).thenReturn(teenBooks);

                // Execute o método de recomendação
                List<Book> recommendedBooks = algorithmServiceImpl.recommendBooks(1L, 1); // Recomendando 1 livro

                // Verificar resultados
                assertEquals(1, recommendedBooks.size()); // Espera-se que retorne 1 livro
                verify(bookRepository).getTopLentBooksByGenre("juvenile", 1); // Verifica que o repositório foi chamado
                                                                              // corretamente
        }

                @Test
                void testRecommendBooksForAdult() {
                        // Mockando os detalhes do leitor
                        ReaderDetails adultReader = mock(ReaderDetails.class);
                        when(adultReader.getBirthDate()).thenReturn(new BirthDate(LocalDate.now().minusYears(25).toString()));
                        when(readerService.findByUserId(1L)).thenReturn(Optional.of(adultReader));

                        // Simulando livros com base em gêneros de interesse
                        Genre genre1 = new Genre("fiction");
                        List<Book> adultBooks = List.of(new Book("9782826012092", "Livro Adulto 1", "Descrição 1", genre1, List.of(author), null));


                        // Mock para retornar livros de um gênero
                        when(adultReader.getInterestList()).thenReturn(List.of(genre1)); // Interesses do leitor
                        when(bookRepository.getTopLentBooksByGenre("fiction", 1)).thenReturn(adultBooks);

                        // Execute o método de recomendação
                        List<Book> recommendedBooks = algorithmServiceImpl.recommendBooks(1L, 1); // Recomendando 1 livro

                        // Verificar resultados
                        assertEquals(1, recommendedBooks.size()); // Espera-se que retorne 1 livro
                        verify(bookRepository).getTopLentBooksByGenre("fiction", 1); // Verifica que o repositório foi chamado
                                                                                // corretamente
                }

        @Test
        void testRecommendMostLentBooks() {
                int X = 2; // Número de livros por gênero
                int Y = 3; // Número de gêneros a serem considerados

                // Mockando gêneros mais emprestados
                Genre genre1 = new Genre("fiction");
                Genre genre2 = new Genre("non-fiction");
                Genre genre3 = new Genre("children");

                List<Genre> topGenres = List.of(genre1, genre2, genre3);
                when(algorithmServiceImpl.getTopGenres(Y)).thenReturn(topGenres);

                // Mockando livros para cada gênero
                String validIsbn1 = "9780134685991"; // ISBN-13 válido
                String validIsbn2 = "9780134685984"; // ISBN-13 válido
                List<Book> fictionBooks = List.of(
                                new Book(validIsbn1, "Fiction Book 1", "Descrição 1", genre1, List.of(author), ""),
                                new Book(validIsbn2, "Fiction Book 2", "Descrição 2", genre1, List.of(author), ""));

                List<Book> nonFictionBooks = List.of(
                                new Book(validIsbn, "Non-Fiction Book 1", "Descrição 1", genre2, List.of(author), ""),
                                new Book("9782826012092", "Non-Fiction Book 2", "Descrição 2", genre2, List.of(author),
                                                "") // Adicionado um segundo livro
                );

                String validIsbn4 = "9780345391803"; // ISBN-13 válido
                List<Book> childrenBooks = List.of(
                                new Book(validIsbn4, "Children Book 1", "Descrição 1", genre3, List.of(author), ""),
                                new Book("9780345391810", "Children Book 2", "Descrição 2", genre3, List.of(author), "") // Adicionado
                                                                                                                         // um
                                                                                                                         // segundo
                                                                                                                         // livro
                );

                // Mockando retorno de livros mais emprestados por gênero
                when(algorithmServiceImpl.getTopLentBooksByGenre("fiction", X)).thenReturn(fictionBooks);
                when(algorithmServiceImpl.getTopLentBooksByGenre("non-fiction", X)).thenReturn(nonFictionBooks);
                when(algorithmServiceImpl.getTopLentBooksByGenre("children", X)).thenReturn(childrenBooks);

                // Executando o método de recomendação
                List<Book> recommendedBooks = algorithmServiceImpl.recommendMostLentBooks(X, Y);

                // Verificando resultados
                assertEquals(6, recommendedBooks.size()); // Espera-se que retorne 6 livros no total (2 de ficção, 2 de
                                                          // não-ficção, 2 de infantil)
                assertTrue(recommendedBooks.containsAll(fictionBooks));
                assertTrue(recommendedBooks.containsAll(nonFictionBooks));
                assertTrue(recommendedBooks.containsAll(childrenBooks));
        }

        @Test
        void testRecommendMostLentBooksFewerBooksThanRequested() {
                int X = 3; // Número de livros por gênero
                int Y = 2; // Número de gêneros a serem considerados

                // Mockando gêneros mais emprestados
                Genre genre1 = new Genre("fiction");
                Genre genre2 = new Genre("non-fiction");

                List<Genre> topGenres = List.of(genre1, genre2);
                when(algorithmServiceImpl.getTopGenres(Y)).thenReturn(topGenres);

                // Mockando retorno de apenas 1 livro de cada gênero
                String validIsbn1 = "9780134685991"; // ISBN-13 válido
                List<Book> fictionBooks = List.of(
                                new Book(validIsbn1, "Fiction Book 1", "Descrição 1", genre1, List.of(author), ""));

                String validIsbn2 = "9780134685984"; // ISBN-13 válido
                List<Book> nonFictionBooks = List.of(
                                new Book(validIsbn2, "Non-Fiction Book 1", "Descrição 1", genre2, List.of(author), ""));

                when(algorithmServiceImpl.getTopLentBooksByGenre("fiction", X)).thenReturn(fictionBooks);
                when(algorithmServiceImpl.getTopLentBooksByGenre("non-fiction", X)).thenReturn(nonFictionBooks);

                // Executando o método de recomendação
                List<Book> recommendedBooks = algorithmServiceImpl.recommendMostLentBooks(X, Y);

                // Verificando resultados
                assertEquals(2, recommendedBooks.size()); // Espera-se que retorne 2 livros (1 de ficção e 1 de
                                                          // não-ficção)
                assertTrue(recommendedBooks.containsAll(fictionBooks));
                assertTrue(recommendedBooks.containsAll(nonFictionBooks));
        }

}
