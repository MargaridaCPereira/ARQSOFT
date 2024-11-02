package pt.psoft.g1.psoftg1.algorithms.service;

import pt.psoft.g1.psoftg1.bookmanagement.model.Book;

import java.util.List;

public interface AlgorithmService {

    /**
     * Recommends books to a reader based on age and popular genres.
     *
     * @param readerId - ID of the reader to base recommendations on
     * @param X - Number of books to recommend
     * @param Y - Number of top genres to consider
     * @return List of recommended books
     */
    List<Book> recommendBooks(long readerId, int X);


    /**
     * Recommends books 
     *
     * @param X - Number of books to recommend
     * @param Y - Number of top genres to consider
     * @return List of recommended books
     */
    List<Book> recommendMostLentBooks(int X, int Y);


}
