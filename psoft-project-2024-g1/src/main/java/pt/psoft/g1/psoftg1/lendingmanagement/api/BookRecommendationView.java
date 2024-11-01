package pt.psoft.g1.psoftg1.lendingmanagement.api;

public class BookRecommendationView {
    private final Long id;
    private final String title;
    private final String genre;

    public BookRecommendationView(Long id, String title, String genre) {
        this.id = id;
        this.title = title;
        this.genre = genre;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getGenre() {
        return genre;
    }
}
