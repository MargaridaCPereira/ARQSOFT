package pt.psoft.g1.psoftg1.algorithms.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import pt.psoft.g1.psoftg1.bookmanagement.repositories.BookRepository;
import pt.psoft.g1.psoftg1.bookmanagement.services.GenreBookCountDTO;
import pt.psoft.g1.psoftg1.exceptions.LendingForbiddenException;
import pt.psoft.g1.psoftg1.exceptions.NotFoundException;
import pt.psoft.g1.psoftg1.genremanagement.model.Genre;
import pt.psoft.g1.psoftg1.genremanagement.repositories.GenreRepository;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Fine;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.lendingmanagement.repositories.FineRepository;
import pt.psoft.g1.psoftg1.lendingmanagement.repositories.LendingRepository;
import pt.psoft.g1.psoftg1.readermanagement.model.ReaderDetails;
import pt.psoft.g1.psoftg1.readermanagement.repositories.ReaderRepository;
import pt.psoft.g1.psoftg1.readermanagement.services.ReaderService;
import pt.psoft.g1.psoftg1.shared.services.Page;
import java.util.ArrayList;
import java.util.stream.Collectors;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlgorithmServiceImpl implements AlgorithmService {
    private final BookRepository bookRepository;
    private final ReaderService readerService;
    private final GenreRepository genreRepository;


    public List<Book> getTopLentBooksByGenre(String genre, int limit) {
        return bookRepository.getTopLentBooksByGenre(genre, limit);
    }

    public List<Genre> getTopGenres(int limit) {
        return genreRepository.getTopGenres(limit);
    }

    @Override
    public List<Book> recommendBooks(long userId, int X) {
        Optional<ReaderDetails> optionalReaderDetails = readerService.findByUserId(userId);

        if (optionalReaderDetails.isEmpty()) {
            throw new IllegalArgumentException("Reader not found with user ID: " + userId);
        }

        ReaderDetails readerDetails = optionalReaderDetails.get();
        List<Book> lendings = new ArrayList<>();

        int age = readerDetails.getBirthDate().getAge(); 

        // Lógica de recomendação baseada na idade
        if (age < 10) {
            lendings = getTopLentBooksByGenre("children", X);
        } else if (age >= 10 && age < 18) {
            lendings = getTopLentBooksByGenre("juvenile", X);
        } else {
            List<Genre> topGenres = readerDetails.getInterestList();
            if(!topGenres.isEmpty()){
                for (Genre genre : topGenres) {
                    lendings.addAll(getTopLentBooksByGenre(genre.getGenre(), X));
                }
            }
        }
        return lendings;
    }

    @Override
    public List<Book> recommendMostLentBooks(int X, int Y) {
        // Passo 1: Obter os Y gêneros mais emprestados
        List<Genre> topGenres = getTopGenres(Y);

        // Passo 2: Obter os X livros mais emprestados de cada um dos gêneros
        List<Book> lendings = new ArrayList<>();

        if(!topGenres.isEmpty()){
            for (Genre genre : topGenres) {
                lendings.addAll(getTopLentBooksByGenre(genre.getGenre(), X));
            }
        }

        return lendings;
    }

}
