package pt.psoft.g1.psoftg1.authormanagement.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pt.psoft.g1.psoftg1.authormanagement.model.Author;
import pt.psoft.g1.psoftg1.authormanagement.repositories.AuthorRepository;
import pt.psoft.g1.psoftg1.idgeneratormanagement.IdGeneratorType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AuthorServiceImplIntegrationTest {

    @Autowired
    private AuthorService authorService;

    @MockBean
    private AuthorRepository authorRepository;

    @MockBean
    private IdGeneratorType idGeneratorType;

    @BeforeEach
    public void setUp() {
        Author alex = new Author("Alex", "O Alex escreveu livros", null);
        List<Author> list = new ArrayList<>();
        list.add(alex);

        Mockito.when(authorRepository.searchByNameName(alex.getName()))
                .thenReturn(list);
    }

    @Test
    public void whenValidId_thenAuthorShouldBeFound() {
        // Arrange
        String authorId = "5f8d0f9e3c6b2a000f1c2d3e4";
        Author mockAuthor = new Author("Alex", "O Alex escreveu livros", null);
        mockAuthor.setAuthorId(authorId); // Setando um ID para o autor simulado

        when(authorRepository.findByAuthorId(authorId)).thenReturn(Optional.of(mockAuthor));

        // Act
        Optional<Author> found = authorService.findByAuthorId(authorId); // Mudando para findByAuthorId

        // Assert
        assertThat(found).isPresent();
        found.ifPresent(author -> assertThat(author.getAuthorId()).isEqualTo(authorId));

        // Verifica se o repositório foi consultado pelo ID
        verify(authorRepository).findByAuthorId(authorId); // Verificando o ID correto
    }

    @Test
    public void whenCreateAuthor_thenAuthorShouldBeCreated() {
        // Arrange
        CreateAuthorRequest createAuthorRequest = new CreateAuthorRequest();
        createAuthorRequest.setName("John Doe");
        createAuthorRequest.setBio("A new author");
        createAuthorRequest.setPhoto(null); // Assuming photo is optional
        createAuthorRequest.setPhotoURI(null); // Assuming photoURI is optional

        String generatedId = "5f8d0f9e3c6b2a000f1c2d3e4"; // Exemplo de ID gerado

        // Mock do gerador de ID
        when(idGeneratorType.generateId()).thenReturn(generatedId);

        // Mock do método save do repositório
        when(authorRepository.save(any(Author.class))).thenAnswer(invocation -> {
            Author savedAuthor = invocation.getArgument(0);
            savedAuthor.setAuthorId(generatedId); // Simula a definição do ID gerado
            return savedAuthor;
        });

        // Act
        Author actualAuthor = authorService.create(createAuthorRequest);

        // Assert
        assertThat(actualAuthor).isNotNull();
        assertThat(actualAuthor.getAuthorId()).isEqualTo(generatedId);
        assertThat(actualAuthor.getName()).isEqualTo("John Doe");
        assertThat(actualAuthor.getBio()).isEqualTo("A new author");

        // Verifica se o método save foi chamado
        verify(authorRepository).save(any(Author.class));
        // Verifica se o gerador de ID foi chamado
        verify(idGeneratorType).generateId();
    }
}
