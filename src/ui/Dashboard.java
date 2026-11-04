
package ui;

import dao.BookDAO;
import models.Book;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class Dashboard extends JFrame {

    private BookDAO dao = new BookDAO();

    private JTable bookTable;
    private DefaultTableModel tableModel;

    private JTextField searchField;
    private JComboBox<String> filterBox;

    private JLabel totalBooksLabel;
    private JLabel readingLabel;
    private JLabel completedLabel;

    public Dashboard() {

        setTitle("LibraryHub - Book Reading Tracker");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        createHeader();
        createSidebar();
        createMainPanel();

        refreshBooks();

    }

    // HEADER
    private void createHeader() {

        JPanel header = new JPanel(new BorderLayout());

        header.setBackground(new Color(35, 45, 65));
        header.setPreferredSize(new Dimension(1100, 65));

        JLabel title = new JLabel("  LibraryHub");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 25));

        JLabel subtitle = new JLabel("Book Reading Tracker  ");
        subtitle.setForeground(Color.WHITE);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));

        header.add(title, BorderLayout.WEST);
        header.add(subtitle, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

    }

    // SIDEBAR
    private void createSidebar() {

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new GridLayout(6, 1, 5, 12));

        sidebar.setBackground(new Color(35, 45, 65));
        sidebar.setPreferredSize(new Dimension(200, 600));

        JButton dashboardBtn = createButton("Dashboard");
        JButton addBtn = createButton("Add Book");
        JButton viewBtn = createButton("View Books");
        JButton updateBtn = createButton("Update Progress");
        JButton deleteBtn = createButton("Delete Book");
        JButton refreshBtn = createButton("Refresh");

        sidebar.add(dashboardBtn);
        sidebar.add(addBtn);
        sidebar.add(viewBtn);
        sidebar.add(updateBtn);
        sidebar.add(deleteBtn);
        sidebar.add(refreshBtn);

        add(dashboardBtn, BorderLayout.WEST);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(35, 45, 65));
        wrapper.add(sidebar, BorderLayout.NORTH);

        add(wrapper, BorderLayout.WEST);

        dashboardBtn.addActionListener(e -> refreshBooks());
        addBtn.addActionListener(e -> addBook());
        viewBtn.addActionListener(e -> refreshBooks());
        updateBtn.addActionListener(e -> updateBook());
        deleteBtn.addActionListener(e -> deleteBook());
        refreshBtn.addActionListener(e -> refreshBooks());

    }

    // BUTTON DESIGN
    private JButton createButton(String text) {

        JButton button = new JButton(text);

        button.setBackground(new Color(55, 75, 105));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));

        button.setFocusPainted(false);

        return button;

    }

    // MAIN PANEL
    private void createMainPanel() {

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));

        mainPanel.setBackground(new Color(240, 243, 248));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // STATISTICS

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setOpaque(false);

        totalBooksLabel = new JLabel("Total Books: 0");
        readingLabel = new JLabel("Currently Reading: 0");
        completedLabel = new JLabel("Completed: 0");

        statsPanel.add(createCard(totalBooksLabel));
        statsPanel.add(createCard(readingLabel));
        statsPanel.add(createCard(completedLabel));

        // SEARCH AND FILTER

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        searchField = new JTextField(20);

        JButton searchBtn = new JButton("Search");
        JButton clearBtn = new JButton("Clear");

        filterBox = new JComboBox<>(new String[] {
                "ALL",
                "NOT_STARTED",
                "CURRENTLY_READING",
                "COMPLETED",
                "ON_HOLD"
        });

        searchPanel.add(new JLabel("Search Book:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(clearBtn);
        searchPanel.add(new JLabel("Filter:"));
        searchPanel.add(filterBox);

        searchBtn.addActionListener(e -> searchBooks());

        searchField.addActionListener(e -> searchBooks());

        clearBtn.addActionListener(e -> {
            searchField.setText("");
            filterBox.setSelectedItem("ALL");
            refreshBooks();
        });

        filterBox.addActionListener(e -> searchBooks());

        // BOOK TABLE

        String[] columns = {
                "ID",
                "Title",
                "Author",
                "Genre",
                "Total Pages",
                "Current Page",
                "Status",
                "ISBN"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

        };

        bookTable = new JTable(tableModel);

        bookTable.setRowHeight(30);
        bookTable.setFont(new Font("Arial", Font.PLAIN, 13));

        bookTable.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13));

        JScrollPane scrollPane = new JScrollPane(bookTable);

        // COMBINE PANELS

        JPanel topPanel = new JPanel(new BorderLayout(10, 15));

        topPanel.setOpaque(false);
        topPanel.add(statsPanel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

    }

    // STATISTICS CARD
    private JPanel createCard(JLabel label) {

        JPanel card = new JPanel(new GridBagLayout());

        card.setBackground(Color.WHITE);

        label.setFont(new Font("Arial", Font.BOLD, 17));

        card.add(label);

        return card;

    }

    // ADD BOOK
    private void addBook() {

        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField genreField = new JTextField();
        JTextField isbnField = new JTextField();
        JTextField pagesField = new JTextField();

        String[] fields = {
                "Title", "Author", "Genre", "ISBN", "Total Pages"
        };

        JTextField[] inputs = {
                titleField, authorField, genreField,
                isbnField, pagesField
        };

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));

        for (int i = 0; i < fields.length; i++) {

            panel.add(new JLabel(fields[i]));
            panel.add(inputs[i]);

        }

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Add New Book",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {

            try {

                String title = titleField.getText().trim();
                String author = authorField.getText().trim();
                String genre = genreField.getText().trim();
                String isbn = isbnField.getText().trim();

                int pages = Integer.parseInt(pagesField.getText());

                if (title.isEmpty() || author.isEmpty() || pages <= 0) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Please enter valid book details.");

                    return;
                }

                Book book = new Book(
                        title,
                        author,
                        genre,
                        isbn,
                        pages,
                        0,
                        "NOT_STARTED");

                boolean added = dao.addBook(book);

                if (added) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Book Added Successfully!");

                    refreshBooks();

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            "Unable to add book. Check the details and ISBN.");

                }

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                        this,
                        "Total pages must be a valid number.");

            }

        }

    }

    // VIEW AND REFRESH BOOKS
    private void refreshBooks() {

        List<Book> books = dao.getAllBooks();

        displayBooks(books);

    }

    // DISPLAY BOOKS IN TABLE
    private void displayBooks(List<Book> books) {

        tableModel.setRowCount(0);

        for (Book book : books) {

            Object[] row = {
                    book.getBookId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getGenre(),
                    book.getTotalPages(),
                    book.getCurrentPage(),
                    book.getReadingStatus(),
                    book.getIsbn()
            };

            tableModel.addRow(row);

        }

        updateStatistics();

    }

    // UPDATE STATISTICS
    private void updateStatistics() {

        List<Book> books = dao.getAllBooks();

        int total = books.size();
        int reading = 0;
        int completed = 0;

        for (Book book : books) {

            if ("CURRENTLY_READING".equals(book.getReadingStatus())) {
                reading++;
            }

            if ("COMPLETED".equals(book.getReadingStatus())) {
                completed++;
            }

        }

        totalBooksLabel.setText("Total Books: " + total);
        readingLabel.setText("Currently Reading: " + reading);
        completedLabel.setText("Completed: " + completed);

    }

    // SEARCH AND FILTER
    private void searchBooks() {

        String keyword = searchField.getText().trim();

        String status = (String) filterBox.getSelectedItem();

        List<Book> books;

        if (!keyword.isEmpty()) {

            books = dao.searchBooksByTitle(keyword);

            if (!"ALL".equals(status)) {

                books.removeIf(book -> !status.equals(book.getReadingStatus()));

            }

        } else if (!"ALL".equals(status)) {

            books = dao.getBooksByStatus(status);

        } else {

            books = dao.getAllBooks();

        }

        displayBooks(books);

    }

    // GET SELECTED BOOK ID
    private int getSelectedBookId() {

        int row = bookTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a book first.");

            return -1;

        }

        return (int) tableModel.getValueAt(row, 0);

    }

    // UPDATE READING PROGRESS
    private void updateBook() {

        int bookId = getSelectedBookId();

        if (bookId == -1) {
            return;
        }

        Book book = dao.searchBook(bookId);

        if (book == null) {
            JOptionPane.showMessageDialog(this, "Book not found!");
            return;
        }

        JTextField pageField = new JTextField(
                String.valueOf(book.getCurrentPage()));

        String[] statuses = {
                "NOT_STARTED",
                "CURRENTLY_READING",
                "COMPLETED",
                "ON_HOLD"
        };

        JComboBox<String> statusBox = new JComboBox<>(statuses);

        statusBox.setSelectedItem(book.getReadingStatus());

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));

        panel.add(new JLabel("Current Page:"));
        panel.add(pageField);

        panel.add(new JLabel("Reading Status:"));
        panel.add(statusBox);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Update Reading Progress",
                JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {

            try {

                int currentPage = Integer.parseInt(
                        pageField.getText());

                if (currentPage < 0 ||
                        currentPage > book.getTotalPages()) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Enter a page between 0 and "
                                    + book.getTotalPages());

                    return;

                }

                book.setCurrentPage(currentPage);

                book.setReadingStatus(
                        (String) statusBox.getSelectedItem());

                if (currentPage == book.getTotalPages()
                        && !"COMPLETED".equals(book.getReadingStatus())) {

                    JOptionPane.showMessageDialog(
                            this,
                            "A book at its final page must be marked COMPLETED.");

                    return;

                }

                boolean updated = dao.updateBook(book);

                if (updated) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Reading Progress Updated!");

                    refreshBooks();

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            "Update Failed!");

                }

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                        this,
                        "Enter a valid page number.");

            }

        }

    }

    // DELETE BOOK
    private void deleteBook() {

        int bookId = getSelectedBookId();

        if (bookId == -1) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this book?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {

            boolean deleted = dao.deleteBook(bookId);

            if (deleted) {

                JOptionPane.showMessageDialog(
                        this,
                        "Book Deleted Successfully!");

                refreshBooks();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Unable to delete book.");

            }

        }

    }

    // MAIN METHOD
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            Dashboard dashboard = new Dashboard();

            dashboard.setVisible(true);

        });

    }

}
