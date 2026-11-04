import dao.BookDAO;
import models.Book;
import java.util.List;

public class DAOTest {

    public static void main(String[] args) {

        BookDAO dao = new BookDAO();

        // CREATE: Add a new book
        String uniqueISBN = "DAO-TEST-" + System.currentTimeMillis();

        Book book = new Book(
                "The Alchemist",
                "Paulo Coelho",
                "Fiction",
                uniqueISBN,
                200,
                0,
                "NOT_STARTED");

        boolean added = dao.addBook(book);

        System.out.println("\n--- ADD BOOK TEST ---");
        System.out.println("Book Added: " + added);

        // UPDATE: Update reading progress
        System.out.println("\n--- UPDATE BOOK TEST ---");

        List<Book> searchResults = dao.searchBooksByTitle("The Alchemist");

        Book updateBook = null;

        for (Book b : searchResults) {
            if (uniqueISBN.equals(b.getIsbn())) {
                updateBook = b;
                break;
            }
        }

        if (updateBook != null) {

            updateBook.setCurrentPage(50);
            updateBook.setReadingStatus("CURRENTLY_READING");

            boolean updated = dao.updateBook(updateBook);

            System.out.println("Book Updated: " + updated);

            System.out.println("Updated Details: "
                    + dao.searchBook(updateBook.getBookId()));

        } else {
            System.out.println("Book not found!");
        }

        // READ: Display all books
        System.out.println("\n--- DISPLAY ALL BOOKS ---");

        List<Book> books = dao.getAllBooks();

        if (books.isEmpty()) {
            System.out.println("No books found!");
        } else {
            for (Book b : books) {
                System.out.println(b);
            }
        }

        // READ: Search book by ID
        System.out.println("\n--- SEARCH BOOK TEST ---");

        if (updateBook != null) {

            Book searchedBook = dao.searchBook(updateBook.getBookId());

            if (searchedBook != null) {
                System.out.println("Book Found: " + searchedBook);
            } else {
                System.out.println("Book Not Found!");
            }
        }

        // FILTER: Display books by reading status
        System.out.println("\n--- FILTER BOOKS TEST ---");

        List<Book> filteredBooks = dao.getBooksByStatus("CURRENTLY_READING");

        if (filteredBooks.isEmpty()) {
            System.out.println("No currently reading books!");
        } else {
            for (Book b : filteredBooks) {
                System.out.println(b);
            }
        }

        // DELETE: Test deleting a book

        System.out.println("\n--- DELETE BOOK TEST ---");

        if (updateBook != null) {

            boolean deleted = dao.deleteBook(updateBook.getBookId());

            System.out.println("Book Deleted: " + deleted);

            Book checkBook = dao.searchBook(updateBook.getBookId());

            if (checkBook == null) {
                System.out.println("Book successfully removed from database!");
            } else {
                System.out.println("Book still exists!");
            }
        }
        System.out.println("\n--- DAO TEST COMPLETED ---");
    }
}