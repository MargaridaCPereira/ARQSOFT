package pt.psoft.g1.psoftg1.lendingmanagement.services;

import pt.psoft.g1.psoftg1.lendingmanagement.api.BookRecommendationView;
import pt.psoft.g1.psoftg1.lendingmanagement.model.Lending;
import pt.psoft.g1.psoftg1.shared.services.Page;

import java.util.List;
import java.util.Optional;

public interface LendingService {
    /**
     * @param lendingNumber
     * @return {@code Optional<Lending>}
     */
    Optional<Lending> findByLendingNumber(String lendingNumber);
    /**
     * @param readerNumber - Reader Number of the Reader associated with the lending
     * @param isbn         - ISBN of the book associated with the lending
     * @param returned     - Wether it's intended to filter by the return status of a lending
     * @return {@code Iterable<Lending>}
     */
    List<Lending> listByReaderNumberAndIsbn(String readerNumber, String isbn, Optional<Boolean> returned);
    Lending create(CreateLendingRequest resource); //No ID passed, as it is auto generated
    Lending setReturned(String id, SetLendingReturnedRequest resource, long desiredVersion);
    Double getAverageDuration();
    List<Lending> getOverdue(Page page);
    Double getAvgLendingDurationByIsbn(String isbn);
    List<Lending> searchLendings(Page page, SearchLendingQuery request);

    /**
     * Recommends books to a reader based on age and popular genres.
     *
     * @param readerId - ID of the reader to base recommendations on
     * @param X - Number of books to recommend
     * @param Y - Number of top genres to consider
     * @return List of recommended books
     */
    List<BookRecommendationView> recommendBooks(long readerId, int X);


    /**
     * Recommends books 
     *
     * @param X - Number of books to recommend
     * @param Y - Number of top genres to consider
     * @return List of recommended books
     */
    List<BookRecommendationView> recommendMostLentBooks(int X, int Y);


}
