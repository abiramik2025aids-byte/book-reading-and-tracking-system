package dao;

import models.Book;
import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BookDAO {

    private static final List<String> VALID_STATUSES = Arrays.asList(
            "NOT_STARTED",
            "CURRENTLY_READING",
            "COMPLETED",
            "ON_HOLD");

    // CREATE: Add a new book.
    public boolean addBook(Book book) {
        if (!isValidBook(book, false)) {
            System.out.println("Invalid book details or reading status!");
            return false;
        }

        String sql = "INSERT INTO books " +
                "(title, author, genre, isbn, total_pages, current_page, reading_status, start_date, rating, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            setBookDetails(ps, book);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.out.println("Could not add book: " + e.getMessage());
            return false;
        }
    }

    // READ: Get all books.
    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY book_id";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                books.add(mapBook(rs));
            }

        } catch (SQLException e) {
            System.out.println("Could not retrieve books: " + e.getMessage());
        }

        return books;
    }

    // READ: Search by book ID.
    public Book searchBook(int bookId) {
        if (bookId <= 0) {
            System.out.println("Invalid Book ID!");
            return null;
        }

        String sql = "SELECT * FROM books WHERE book_id = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, bookId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapBook(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Search by ID failed: " + e.getMessage());
        }

        return null;
    }

    // READ: Search by part or all of a book title.
    public List<Book> searchBooksByTitle(String title) {
        List<Book> books = new ArrayList<>();

        if (title == null || title.trim().isEmpty()) {
            return books;
        }

        String sql = "SELECT * FROM books WHERE title LIKE ? ORDER BY title";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, "%" + title.trim() + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(mapBook(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Search by title failed: " + e.getMessage());
        }

        return books;
    }

    // UPDATE: Update an existing book.
    public boolean updateBook(Book book) {
        if (!isValidBook(book, true)) {
            System.out.println("Invalid book details or reading status!");
            return false;
        }

        String sql = "UPDATE books SET title = ?, author = ?, genre = ?, isbn = ?, " +
                "total_pages = ?, current_page = ?, reading_status = ?, start_date = ?, rating = ?, notes = ? " +
                "WHERE book_id = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            setBookDetails(ps, book);
            ps.setInt(11, book.getBookId());

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
            return false;
        }
    }

    // DELETE: Delete a book using its ID.
    public boolean deleteBook(int bookId) {
        if (bookId <= 0) {
            System.out.println("Invalid Book ID!");
            return false;
        }

        String sql = "DELETE FROM books WHERE book_id = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, bookId);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.out.println("Delete failed: " + e.getMessage());
            return false;
        }
    }

    // CREATE: Save a reading session.
    public boolean saveReadingSession(
            int bookId,
            java.time.LocalDate sessionDate,
            int pagesRead,
            int totalAfterReading,
            String notes) {

        String sql = "INSERT INTO reading_sessions "
                + "(book_id, session_date, pages_read, total_after_reading, notes) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, bookId);
            ps.setDate(2, java.sql.Date.valueOf(sessionDate));
            ps.setInt(3, pagesRead);
            ps.setInt(4, totalAfterReading);
            ps.setString(5, notes);

            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            System.out.println("Could not save reading session: " + e.getMessage());
            return false;
        }
    }
    
    // READ: Retrieve reading sessions for a book.
    public List<String[]> getReadingSessions(int bookId) {

        List<String[]> sessions = new ArrayList<>();

        String sql = "SELECT session_date, pages_read, "
                + "total_after_reading, notes "
                + "FROM reading_sessions "
                + "WHERE book_id = ? ORDER BY session_date";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, bookId);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    String[] session = {
                            rs.getDate("session_date").toString(),
                            String.valueOf(rs.getInt("pages_read")),
                            String.valueOf(rs.getInt("total_after_reading")),
                            rs.getString("notes")
                    };

                    sessions.add(session);
                }

            }

        } catch (SQLException e) {
            System.out.println("Could not retrieve reading sessions: "
                    + e.getMessage());
        }

        return sessions;
    }

    // FILTER: Get books by reading status.
    public List<Book> getBooksByStatus(String status) {
        List<Book> books = new ArrayList<>();

        if (!isValidStatus(status)) {
            System.out.println("Invalid reading status!");
            return books;
        }

        String sql = "SELECT * FROM books WHERE reading_status = ? ORDER BY title";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(mapBook(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Could not filter books: " + e.getMessage());
        }

        return books;
    }

    // Bind all book fields in the same order as the INSERT/UPDATE query.
     private void setBookDetails(PreparedStatement ps, Book book) throws SQLException {
        ps.setString(1, book.getTitle());
        ps.setString(2, book.getAuthor());
        ps.setString(3, book.getGenre());
        ps.setString(4, book.getIsbn());
        ps.setInt(5, book.getTotalPages());
        ps.setInt(6, book.getCurrentPage());
        ps.setString(7, book.getReadingStatus());

        if (book.getStartDate() != null) {
            ps.setString(8, book.getStartDate());
        } else {
            ps.setNull(8, Types.VARCHAR);
        }

        if (book.getRating() != null) {
            ps.setString(9, book.getRating());
        } else {
            ps.setNull(9, Types.VARCHAR);
        }

        if (book.getNotes() != null) {
            ps.setString(10, book.getNotes());
        } else {
            ps.setNull(10, Types.VARCHAR);
        }
    }

    // Convert a database row into a Book object.
    private Book mapBook(ResultSet rs) throws SQLException {
        Timestamp dateAdded = rs.getTimestamp("date_added");

        Book book = new Book(
                rs.getInt("book_id"),
                rs.getString("title"),
                rs.getString("author"),
                rs.getString("genre"),
                rs.getString("isbn"),
                rs.getInt("total_pages"),
                rs.getInt("current_page"),
                rs.getString("reading_status"),
                dateAdded == null ? null : dateAdded.toLocalDateTime());

        book.setStartDate(rs.getString("start_date"));
        book.setRating(rs.getString("rating"));
        book.setNotes(rs.getString("notes"));

        return book;
    }

    // Validate fields before sending data to MySQL.
    private boolean isValidBook(Book book, boolean requireBookId) {
        if (book == null) {
            return false;
        }

        if (requireBookId && book.getBookId() <= 0) {
            return false;
        }

        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) {
            return false;
        }

        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            return false;
        }

        if (book.getTotalPages() <= 0 ||
                book.getCurrentPage() < 0 ||
                book.getCurrentPage() > book.getTotalPages()) {
            return false;
        }

        if (!isValidStatus(book.getReadingStatus())) {
            return false;
        }

        // A book at its final page must be marked completed.
        if (book.getCurrentPage() == book.getTotalPages()
                && !"COMPLETED".equals(book.getReadingStatus())) {
            return false;
        }

        return true;
    }

    private boolean isValidStatus(String status) {
        return status != null && VALID_STATUSES.contains(status);
    }
}