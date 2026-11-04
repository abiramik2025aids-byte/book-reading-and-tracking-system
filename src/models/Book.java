package models;

import java.time.LocalDateTime;

public class Book {

    private int bookId;
    private String title;
    private String author;
    private String genre;
    private String isbn;
    private int totalPages;
    private int currentPage;
    private String readingStatus;
    private LocalDateTime dateAdded;
    private String startDate;
    private String rating;
    private String notes;

    public Book() {
    }

    // For creating a new book; the database generates bookId and dateAdded.
    public Book(String title, String author, String genre, String isbn,
            int totalPages, int currentPage, String readingStatus) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.isbn = isbn;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.readingStatus = readingStatus;
    }

    // For a complete book record retrieved from the database.
    public Book(int bookId, String title, String author, String genre,
            String isbn, int totalPages, int currentPage,
            String readingStatus, LocalDateTime dateAdded) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.isbn = isbn;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.readingStatus = readingStatus;
        this.dateAdded = dateAdded;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public String getReadingStatus() {
        return readingStatus;
    }

    public void setReadingStatus(String readingStatus) {
        this.readingStatus = readingStatus;
    }

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDateTime dateAdded) {
        this.dateAdded = dateAdded;
    }
    
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", genre='" + genre + '\'' +
                ", isbn='" + isbn + '\'' +
                ", totalPages=" + totalPages +
                ", currentPage=" + currentPage +
                ", readingStatus='" + readingStatus + '\'' +
                ", dateAdded=" + dateAdded +
                '}';
    }
}