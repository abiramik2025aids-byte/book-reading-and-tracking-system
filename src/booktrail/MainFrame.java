package booktrail;


import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;



public class MainFrame extends JFrame {
    private final dao.BookDAO bookDAO = new dao.BookDAO();
    private JLabel booksReadValue;
    private JLabel currentlyReadingValue;
    private JLabel goalInfo;
    private JProgressBar goalProgress;
    private JLabel pagesThisWeekValue;
    private Book editingBook = null;
    private JTextField bookTitleField, authorField, totalPagesField, startDateField;
    private JComboBox<String> genreField, ratingField;
    private JTextArea notesField;
    private JButton saveBookButton;
    private JLabel addBookHeading, addBookSubtitle;

    private final Color bg = new Color(235, 244, 253);
    private final Color navy = new Color(39, 52, 66);
    private final Color navyLight = new Color(66, 83, 101);
    private final Color text = new Color(40, 49, 61);
    private final Color blue = new Color(63, 139, 207);
    private final Color muted = new Color(105, 116, 128);
    private final Color white = Color.WHITE;
    private final Color borderColor = new Color(222, 232, 242);

    private JPanel pages;
    private CardLayout layout;

    private final List<Book> savedBooks = new ArrayList<>();

    private DefaultTableModel bookTableModel;
    private JTable bookTable;
    private TableRowSorter<DefaultTableModel> sorter;

    private JComboBox<Book> trackerBookSelector;
    private JLabel trackerCover;
    private JLabel trackerTitle;
    private JLabel trackerAuthor;
    private JLabel trackerPages;
    private JLabel trackerPercentage;
    private JLabel trackerStatus;
    private JProgressBar trackerProgress;

    private JTextField sessionPagesField;
    private JTextField sessionDateField;
    private JTextField sessionNotesField;

    private DefaultTableModel sessionTableModel;
    private WeeklyChart weeklyChart;

    private final DateTimeFormatter dateFormat =
            DateTimeFormatter.ofPattern("MMM dd, yyyy");

    // BOOK DATA
    private static class Book {

        String title;
        int bookId;
        String author;
        String genre;
        int totalPages;
        String startDate;
        String rating;
        String notes;
        String coverPath;
        
        public int getBookId() {
            return bookId;
        }
        

        int pagesRead = 0;

        List<ReadingSession> sessions = new ArrayList<>();

        Book(String title, String author, String genre,
             int totalPages, String startDate,
             String rating, String notes) {

            this.title = title;
            this.author = author;
            this.genre = genre;
            this.totalPages = totalPages;
            this.startDate = startDate;
            this.rating = rating;
            this.notes = notes;

            // Automatically find cover using the book title
            this.coverPath = findCoverPath(title);
        }

        @Override
        public String toString() {
            return title;
        }
    }

    // READING SESSION DATA
    private static class ReadingSession {

        LocalDate date;
        int pages;
        int totalAfterReading;
        String notes;

        ReadingSession(LocalDate date, int pages,
                       int totalAfterReading, String notes) {

            this.date = date;
            this.pages = pages;
            this.totalAfterReading = totalAfterReading;
            this.notes = notes;
        }
    }

    // CONSTRUCTOR
    public MainFrame() {

        savedBooks.clear();

        for (models.Book dbBook : bookDAO.getAllBooks()) {
            Book guiBook = new Book(
                    dbBook.getTitle(),
                    dbBook.getAuthor(),
                    dbBook.getGenre(),
                    dbBook.getTotalPages(),
                    dbBook.getStartDate(),
                    dbBook.getRating(),
                    dbBook.getNotes());



            guiBook.bookId = dbBook.getBookId();
            guiBook.pagesRead = dbBook.getCurrentPage();

            // Load saved reading sessions from MySQL.
            List<String[]> savedSessions = bookDAO.getReadingSessions(guiBook.bookId);

            for (String[] savedSession : savedSessions) {

                ReadingSession session = new ReadingSession(
                        LocalDate.parse(savedSession[0]),
                        Integer.parseInt(savedSession[1]),
                        Integer.parseInt(savedSession[2]),
                        savedSession[3] == null ? "" : savedSession[3]);

                guiBook.sessions.add(session);
            }

            savedBooks.add(guiBook);
        }



        setTitle("BookTrail - Your Reading Journey");
        setSize(1440, 850);
        setMinimumSize(new Dimension(1100, 700));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel background = new JPanel(new BorderLayout(25, 0));
        background.setBackground(bg);
        background.setBorder(new EmptyBorder(25, 28, 25, 28));

        // LEFT PROMOTIONAL SECTION
        JPanel promo = new JPanel();
        promo.setBackground(bg);
        promo.setLayout(new BoxLayout(promo, BoxLayout.Y_AXIS));
        promo.setPreferredSize(new Dimension(335, 700));

        JLabel brand = new JLabel("BookTrail");
        brand.setFont(new Font("Arial", Font.BOLD, 44));
        brand.setForeground(text);

        JLabel tagline = new JLabel(
                "<html>Your reading journey,<br>all in one place.</html>"
        );
        tagline.setFont(new Font("Arial", Font.BOLD, 25));
        tagline.setForeground(text);

        promo.add(Box.createVerticalStrut(100));
        promo.add(brand);
        promo.add(Box.createVerticalStrut(20));
        promo.add(tagline);
        promo.add(Box.createVerticalStrut(45));

        addFeature(promo, "Track books and progress with ease");
        addFeature(promo, "Set goals and stay motivated");
        addFeature(promo, "Discover insights about your reading");
        addFeature(promo, "A clean, friendly space for every reader");

        promo.add(Box.createVerticalGlue());

        // APPLICATION WINDOW
        JPanel app = new JPanel(new BorderLayout());
        app.setBackground(white);
        app.setBorder(BorderFactory.createLineBorder(borderColor, 1));

        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(new Color(218, 235, 250));
        titleBar.setPreferredSize(new Dimension(1000, 48));

        JLabel appLogo = new JLabel("BookTrail");
        appLogo.setFont(new Font("Arial", Font.BOLD, 17));
        appLogo.setForeground(text);
        appLogo.setBorder(new EmptyBorder(0, 18, 0, 0));

        JLabel windowButtons = new JLabel("  -     □     ×  ");
        windowButtons.setFont(new Font("Arial", Font.PLAIN, 17));
        windowButtons.setForeground(navy);

        titleBar.add(appLogo, BorderLayout.WEST);
        titleBar.add(windowButtons, BorderLayout.EAST);
        app.add(titleBar, BorderLayout.NORTH);

        // BODY
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(bg);

        // SIDEBAR
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(150, 700));
        sidebar.setBackground(navy);

        JPanel menu = new JPanel(new GridLayout(4, 1, 0, 12));
        menu.setBackground(navy);
        menu.setBorder(new EmptyBorder(25, 9, 10, 9));

        String[] items = {
            "⌂  Dashboard",
            "⊞  Add Book",
            "▣  View Books",
            "▥  Reading Tracker"
        };

        for (int i = 0; i < items.length; i++) {

            JButton button = new JButton(items[i]);
            button.setFont(new Font("Arial", Font.PLAIN, 12));
            button.setForeground(white);
            button.setBackground(i == 0 ? navyLight : navy);
            button.setHorizontalAlignment(SwingConstants.LEFT);
            button.setFocusPainted(false);
            button.setBorder(new EmptyBorder(12, 8, 12, 2));

            menu.add(button);

            final int index = i;

            button.addActionListener(e -> {

                if (index == 0) {
                    pages.remove(0);
                    pages.add(createDashboard(), "Dashboard");
                    pages.revalidate();
                    pages.repaint();
                    layout.show(pages, "Dashboard");

                } else if (index == 1) {
                    layout.show(pages, "Add Book");

                } else if (index == 2) {
                    refreshBookTable();
                    layout.show(pages, "View Books");

                } else {
                    refreshTracker();
                    layout.show(pages, "Reading Tracker");
                }
            });
        }

        sidebar.add(menu, BorderLayout.NORTH);
        body.add(sidebar, BorderLayout.WEST);

        // PAGE MANAGEMENT
        layout = new CardLayout();
        pages = new JPanel(layout);
        pages.setBackground(bg);

        pages.add(createDashboard(), "Dashboard");
        pages.add(createAddBook(), "Add Book");
        pages.add(createViewBooks(), "View Books");
        pages.add(createReadingTracker(), "Reading Tracker");

        body.add(pages, BorderLayout.CENTER);
        app.add(body, BorderLayout.CENTER);

        background.add(promo, BorderLayout.WEST);
        background.add(app, BorderLayout.CENTER);

        setContentPane(background);
        setVisible(true);
    }

    // AUTOMATIC BOOK COVER MATCHING
    private static String findCoverPath(String title) {

        String normalized = title.toLowerCase()
                .replaceAll("^the\\s+", "")
                .replaceAll("[^a-z0-9]+", "_")
                .replaceAll("^_|_$", "");

        // Match the actual filenames in your images folder
        switch (normalized) {

            case "hidden_path":
                normalized = "hidden_path";
                break;

            case "atomic_habits":
                normalized = "atomic";
                break;

            case "midnight_library":
                normalized = "midnight";
                break;

            case "paper_palace":
                normalized = "paperpalace";
                break;

            case "java_programming":
                normalized = "java_programming";
                break;

            case "c_programming":
                normalized = "c_programming";
                break;

            case "basic_data_structures":
                normalized = "basic_data_structures";
                break;

            default:
                break;
        }

        String[] extensions = {
            ".jpg", ".jpeg", ".png"
        };

        for (String extension : extensions) {

            File image = new File(
                    "images/" + normalized + extension
            );

            if (image.exists()) {
                return image.getAbsolutePath();
            }
        }

        return null;
    }

    // PROMOTIONAL FEATURES
    private void addFeature(JPanel panel, String feature) {

        JPanel featurePanel = new JPanel(new BorderLayout());
        featurePanel.setBackground(bg);
        featurePanel.setMaximumSize(new Dimension(330, 58));
        featurePanel.setPreferredSize(new Dimension(330, 58));

        JLabel label = new JLabel(feature);
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        label.setForeground(muted);

        featurePanel.add(label, BorderLayout.CENTER);
        featurePanel.setBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 1, 0, new Color(196, 219, 242)
                )
        );

        panel.add(featurePanel);
    }

    // DASHBOARD
    private JPanel createDashboard() {

        JPanel dashboard = new JPanel(new BorderLayout(12, 15));
        dashboard.setBackground(bg);
        dashboard.setBorder(new EmptyBorder(25, 23, 22, 23));

        JPanel heading = new JPanel();
        heading.setBackground(bg);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Welcome to BookTrail");
        title.setFont(new Font("Arial", Font.BOLD, 29));
        title.setForeground(text);

        JLabel subtitle = new JLabel("Here's your reading overview");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(muted);

        heading.add(title);
        heading.add(Box.createVerticalStrut(5));
        heading.add(subtitle);

        dashboard.add(heading, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setBackground(bg);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JPanel stats = new JPanel(new GridLayout(1, 3, 14, 0));
        stats.setBackground(bg);
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 85));

        long booksReadCount = savedBooks.stream()
                .filter(book -> book.pagesRead >= book.totalPages)
                .count();

        long currentlyReadingCount = savedBooks.stream()
                .filter(book -> book.pagesRead > 0 && book.pagesRead < book.totalPages)
                .count();

        stats.add(statCard("Books Read", String.valueOf(booksReadCount)));
        stats.add(statCard("Currently Reading", String.valueOf(currentlyReadingCount)));
        LocalDate weekStart = LocalDate.now().minusDays(6);

        int pagesThisWeek = savedBooks.stream()
                .flatMap(book -> book.sessions.stream())
                .filter(session -> !session.date.isBefore(weekStart)
                        && !session.date.isAfter(LocalDate.now()))
                .mapToInt(session -> session.pages)
                .sum();

        stats.add(statCard("Pages This Week", String.valueOf(pagesThisWeek)));
        content.add(stats);
        content.add(Box.createVerticalStrut(15));

        JPanel goal = card();
        goal.setLayout(new BoxLayout(goal, BoxLayout.Y_AXIS));
        goal.setMaximumSize(new Dimension(Integer.MAX_VALUE, 105));

        JLabel goalTitle = new JLabel("Reading Goal");
        goalTitle.setFont(new Font("Arial", Font.BOLD, 14));

        int totalPagesRead = 0;

        List<models.Book> goalBooks = bookDAO.getAllBooks();

        for (models.Book book : goalBooks) {

            List<String[]> sessions = bookDAO.getReadingSessions(book.getBookId());

            for (String[] session : sessions) {
                totalPagesRead += Integer.parseInt(session[1]);
            }
        }

        int readingTarget = 500;

        int percentage = Math.min(
                100,
                (totalPagesRead * 100) / readingTarget);

        goalInfo = new JLabel(
                totalPagesRead + " of " + readingTarget + " pages");

        goalInfo.setFont(new Font("Arial", Font.PLAIN, 12));
        goalInfo.setForeground(muted);

        goalProgress = new JProgressBar(0, 100);
        goalProgress.setValue(percentage);
        goalProgress.setString(percentage + "%");
        goalProgress.setStringPainted(true);
        goalProgress.setForeground(blue);
        goalProgress.setBackground(new Color(225, 235, 245));
        goalProgress.setBorderPainted(false);
        goalProgress.setMaximumSize(new Dimension(Integer.MAX_VALUE, 19));

        goal.add(goalTitle);
        goal.add(Box.createVerticalStrut(5));
        goal.add(goalInfo);
        goal.add(Box.createVerticalStrut(9));
        goal.add(goalProgress);

        content.add(goal);
        content.add(Box.createVerticalStrut(15));

        JPanel bottom = new JPanel(new GridLayout(1, 2, 14, 0));
        bottom.setBackground(bg);

        JPanel recent = card();
        recent.setLayout(new BoxLayout(recent, BoxLayout.Y_AXIS));

        JLabel recentTitle = new JLabel("Recently Read");
        recentTitle.setFont(new Font("Arial", Font.BOLD, 15));

        recent.add(recentTitle);
        recent.add(Box.createVerticalStrut(12));

        List<models.Book> allBooks = bookDAO.getAllBooks();

        boolean hasRecentBooks = false;

        for (models.Book book : allBooks) {

            List<String[]> sessions = bookDAO.getReadingSessions(book.getBookId());

            if (!sessions.isEmpty()) {

                String[] lastSession = sessions.get(sessions.size() - 1);

                String sessionDate = lastSession[0];

                recent.add(bookRow(
                        book.getTitle(),
                        book.getAuthor(),
                        book.getReadingStatus(),
                        sessionDate,
                        ""));

                recent.add(Box.createVerticalStrut(8));

                hasRecentBooks = true;
            }
        }

        if (!hasRecentBooks) {
            JLabel emptyMessage = new JLabel(
                    "No reading activity yet. Start reading a book!");

            emptyMessage.setFont(new Font("Arial", Font.PLAIN, 12));
            emptyMessage.setForeground(muted);

            recent.add(emptyMessage);
        }

        JPanel quick = card();
        quick.setLayout(new BoxLayout(quick, BoxLayout.Y_AXIS));

        JLabel quickTitle = new JLabel("Quick Add");
        quickTitle.setFont(new Font("Arial", Font.BOLD, 15));

        JLabel quickText = new JLabel(
                "<html>Add a new book<br>to keep your<br>library growing.</html>"
        );
        quickText.setFont(new Font("Arial", Font.PLAIN, 12));
        quickText.setForeground(muted);

        JButton add = new JButton("Add Book");
        add.setBackground(navy);
        add.setForeground(white);
        add.setFocusPainted(false);

        quick.add(quickTitle);
        quick.add(Box.createVerticalStrut(15));
        quick.add(quickText);
        quick.add(Box.createVerticalStrut(15));
        quick.add(add);

        add.addActionListener(e -> layout.show(pages, "Add Book"));

        JLabel illustration = loadImage(
                "booktrail_open_book.png", 155, 165
        );

        if (illustration != null) {
            quick.add(Box.createVerticalStrut(8));
            quick.add(illustration);
        }

        JScrollPane recentScroll = new JScrollPane(recent);
        recentScroll.setPreferredSize(new Dimension(350, 300));
        recentScroll.getVerticalScrollBar().setUnitIncrement(16);
        recentScroll.getViewport().setBackground(white);
        recentScroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        recentScroll.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        recentScroll.setBorder(null);

        bottom.add(recentScroll);
        bottom.add(quick);

        content.add(bottom);
        dashboard.add(content, BorderLayout.CENTER);

        return dashboard;
    }

    // ADD BOOK SCREEN - ORIGINAL DESIGN, NO COVER BUTTON
    private JPanel createAddBook() {

        JPanel screen = new JPanel(new BorderLayout(15, 18));
        screen.setBackground(bg);
        screen.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel heading = new JPanel();
        heading.setBackground(bg);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Add a New Book");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(text);

        JLabel subtitle = new JLabel(
                "Add a new book to your reading collection"
        );
        addBookHeading = title;
        addBookSubtitle = subtitle;
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(muted);

        heading.add(title);
        heading.add(Box.createVerticalStrut(5));
        heading.add(subtitle);

        screen.add(heading, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(18, 0));
        content.setBackground(bg);

        JPanel form = card();
        form.setLayout(new BorderLayout(10, 15));

        JLabel formTitle = new JLabel("Book Information");
        formTitle.setFont(new Font("Arial", Font.BOLD, 16));
        form.add(formTitle, BorderLayout.NORTH);

        JTextField bookTitle = new JTextField();
        JTextField author = new JTextField();

        JComboBox<String> genre = new JComboBox<>(
                new String[]{
                    "Select Genre", "Fiction", "Fantasy", "Romance",
                    "Mystery", "Thriller", "Science Fiction",
                    "Non-Fiction", "Biography", "Self-Help", "Other"
                }
        );

        JTextField totalPages = new JTextField();
        JTextField startDate = new JTextField();

        JComboBox<String> rating = new JComboBox<>(
                new String[]{
                    "Select Rating", "1 Star", "2 Stars",
                    "3 Stars", "4 Stars", "5 Stars"
                }
        );

        JPanel fields = new JPanel(new GridLayout(3, 2, 15, 18));
        fields.setBackground(white);

        fields.add(createFormField("Book Title", bookTitle));
        fields.add(createFormField("Author", author));
        fields.add(createFormField("Genre", genre));
        fields.add(createFormField("Total Pages", totalPages));
        fields.add(createFormField("Start Date", startDate));
        fields.add(createFormField("Rating", rating));

        JTextArea notes = new JTextArea(5, 20);
        notes.setFont(new Font("Arial", Font.PLAIN, 12));
        notes.setLineWrap(true);
        notes.setWrapStyleWord(true);
        notes.setBorder(new EmptyBorder(8, 8, 8, 8));

        // Make form fields accessible for editing
        bookTitleField = bookTitle;
        authorField = author;
        genreField = genre;
        totalPagesField = totalPages;
        startDateField = startDate;
        ratingField = rating;
        notesField = notes;
        addBookHeading = title;
        addBookSubtitle = subtitle;

        JScrollPane notesScroll = new JScrollPane(notes);
        notesScroll.setBorder(
                BorderFactory.createLineBorder(borderColor)
        );

        JLabel notesLabel = new JLabel("Notes");
        notesLabel.setFont(new Font("Arial", Font.BOLD, 12));

        JPanel notesPanel = new JPanel(new BorderLayout(0, 7));
        notesPanel.setBackground(white);
        notesPanel.add(notesLabel, BorderLayout.NORTH);
        notesPanel.add(notesScroll, BorderLayout.CENTER);

        JPanel formCenter = new JPanel(new BorderLayout(0, 18));
        formCenter.setBackground(white);
        formCenter.add(fields, BorderLayout.NORTH);
        formCenter.add(notesPanel, BorderLayout.CENTER);

        form.add(formCenter, BorderLayout.CENTER);

        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );
        buttons.setBackground(white);

        JButton cancel = new JButton("Cancel");
        JButton save = new JButton("Save Book");
        saveBookButton = save;

        cancel.setBackground(new Color(238, 243, 248));
        cancel.setForeground(text);
        save.setBackground(navy);
        save.setForeground(white);

        cancel.setFocusPainted(false);
        save.setFocusPainted(false);

        buttons.add(cancel);
        buttons.add(save);
        form.add(buttons, BorderLayout.SOUTH);

        cancel.addActionListener(e -> {

            bookTitle.setText("");
            author.setText("");
            genre.setSelectedIndex(0);
            totalPages.setText("");
            startDate.setText("");
            rating.setSelectedIndex(0);
            notes.setText("");

            editingBook = null;

            saveBookButton.setText("Save Book");
            addBookHeading.setText("Add a New Book");
            addBookSubtitle.setText("Add a new book to your reading collection");

            layout.show(pages, "Dashboard");
        });

        save.addActionListener(e -> {

            String titleValue = bookTitle.getText().trim();
            String authorValue = author.getText().trim();
            String genreValue = (String) genre.getSelectedItem();
            String pagesValue = totalPages.getText().trim();
            String dateValue = startDate.getText().trim();
            String ratingValue = (String) rating.getSelectedItem();
            String notesValue = notes.getText().trim();

            if (titleValue.isEmpty() || authorValue.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please enter the book title and author."
                );
                return;
            }

            int pageCount = 0;

            if (!pagesValue.isEmpty()) {
                try {
                    pageCount = Integer.parseInt(pagesValue);

                    if (pageCount <= 0) {
                        throw new NumberFormatException();
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                            this,
                            "Please enter a valid positive number of pages."
                    );
                    return;
                }
            }

            if (editingBook != null) {

                models.Book dbBook = bookDAO.searchBook(editingBook.getBookId());

                if (dbBook == null) {
                    JOptionPane.showMessageDialog(this, "Book not found in database.");
                    return;
                }

                dbBook.setTitle(titleValue);
                dbBook.setAuthor(authorValue);
                dbBook.setGenre(genreValue);
                dbBook.setTotalPages(pageCount);
                dbBook.setStartDate(dateValue);
                dbBook.setRating(ratingValue);
                dbBook.setNotes(notesValue);

                if (dbBook.getCurrentPage() > pageCount) {
                    dbBook.setCurrentPage(pageCount);
                    dbBook.setReadingStatus("COMPLETED");
                }

                if (!bookDAO.updateBook(dbBook)) {
                    JOptionPane.showMessageDialog(this, "Could not update book.");
                    return;
                }

                editingBook.title = titleValue;
                editingBook.author = authorValue;
                editingBook.genre = genreValue;
                editingBook.totalPages = pageCount;
                editingBook.startDate = dateValue;
                editingBook.rating = ratingValue;
                editingBook.notes = notesValue;
                editingBook.pagesRead = dbBook.getCurrentPage();

            } else {

                Book newBook = new Book(
                        titleValue,
                        authorValue,
                        genreValue,
                        pageCount,
                        dateValue,
                        ratingValue,
                        notesValue);

                models.Book dbBook = new models.Book(
                        newBook.title,
                        newBook.author,
                        newBook.genre,
                        null,
                        newBook.totalPages,
                        newBook.pagesRead,
                        "NOT_STARTED");

                dbBook.setStartDate(dateValue);
                dbBook.setRating(ratingValue);
                dbBook.setNotes(notesValue);

                if (bookDAO.addBook(dbBook)) {
                    savedBooks.add(newBook);
                } else {
                    JOptionPane.showMessageDialog(this, "Could not save book to database.");
                    return;
                }
            }

            refreshBookTable();
            refreshTracker();

            JOptionPane.showMessageDialog(
                    this,
                    "Your book has been saved successfully!\n"
                    + "Total books in your library: " + savedBooks.size()
            );

            bookTitle.setText("");
            author.setText("");
            genre.setSelectedIndex(0);
            totalPages.setText("");
            startDate.setText("");
            rating.setSelectedIndex(0);
            notes.setText("");

            editingBook = null;
            saveBookButton.setText("Save Book");
            title.setText("Add a New Book");
            subtitle.setText("Add a new book to your reading collection");

            refreshBookTable();
            refreshTracker();
            

            layout.show(pages, "View Books");
        });

        content.add(form, BorderLayout.CENTER);

        JPanel illustrationPanel = card();
        illustrationPanel.setLayout(
                new BoxLayout(illustrationPanel, BoxLayout.Y_AXIS)
        );
        illustrationPanel.setPreferredSize(new Dimension(190, 300));

        JLabel illustrationTitle = new JLabel("Happy Reading!");
        illustrationTitle.setFont(new Font("Arial", Font.BOLD, 15));
        illustrationTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        illustrationPanel.add(illustrationTitle);
        illustrationPanel.add(Box.createVerticalStrut(20));

        JLabel illustration = loadImage(
                "booktrail_open_book.png", 145, 165
        );

        if (illustration != null) {
            illustration.setAlignmentX(Component.CENTER_ALIGNMENT);
            illustrationPanel.add(illustration);
        }

        content.add(illustrationPanel, BorderLayout.EAST);
        screen.add(content, BorderLayout.CENTER);

        return screen;
    }

    // VIEW BOOKS SCREEN
    private JPanel createViewBooks() {

        JPanel screen = new JPanel(new BorderLayout(15, 18));
        screen.setBackground(bg);
        screen.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel heading = new JPanel();
        heading.setBackground(bg);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Your Library");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(text);

        JLabel subtitle = new JLabel(
                "Explore and manage your reading collection"
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(muted);

        heading.add(title);
        heading.add(Box.createVerticalStrut(5));
        heading.add(subtitle);

        screen.add(heading, BorderLayout.NORTH);

        JPanel library = card();
        library.setLayout(new BorderLayout(10, 15));

        JPanel controls = new JPanel(new BorderLayout(12, 0));
        controls.setBackground(white);

        JTextField search = new JTextField();
        search.setFont(new Font("Arial", Font.PLAIN, 13));
        search.setPreferredSize(new Dimension(250, 38));
        search.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(borderColor),
                        new EmptyBorder(5, 10, 5, 10)
                )
        );
        search.setToolTipText("Search by book title or author");

        JComboBox<String> filter = new JComboBox<>(
                new String[]{
                    "All Genres", "Fiction", "Fantasy", "Romance",
                    "Mystery", "Thriller", "Science Fiction",
                    "Non-Fiction", "Biography", "Self-Help", "Other"
                }
        );

        filter.setPreferredSize(new Dimension(150, 38));

        controls.add(search, BorderLayout.CENTER);
        controls.add(filter, BorderLayout.EAST);

        library.add(controls, BorderLayout.NORTH);

        String[] columns = {
                "Book Title", "Author", "Genre",
                "Pages", "Start Date", "Rating"
        };

        bookTableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int column) {
                return Object.class;
            }
        };

        bookTable = new JTable(bookTableModel);
        bookTable.setRowHeight(72);
        bookTable.setFont(new Font("Arial", Font.PLAIN, 12));
        bookTable.setForeground(text);
        bookTable.setBackground(white);
        bookTable.setSelectionBackground(new Color(218, 235, 250));
        bookTable.setSelectionForeground(text);
        bookTable.setGridColor(borderColor);
        bookTable.setShowVerticalLines(false);

        JTableHeader header = bookTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 12));
        header.setBackground(new Color(230, 240, 250));
        header.setForeground(navy);
        header.setPreferredSize(new Dimension(100, 40));
        header.setReorderingAllowed(false);

        

        

        sorter = new TableRowSorter<>(bookTableModel);
        bookTable.setRowSorter(sorter);

        JScrollPane tableScroll = new JScrollPane(bookTable);
        tableScroll.setBorder(
                BorderFactory.createLineBorder(borderColor)
        );

        library.add(tableScroll, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(white);

        JLabel countLabel = new JLabel("Total Books: 0");
        countLabel.setFont(new Font("Arial", Font.BOLD, 12));
        countLabel.setForeground(muted);

        JPanel actions = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );
        actions.setBackground(white);

        JButton update = new JButton("✏ Update Selected");
        JButton delete = new JButton("Delete Selected");
        JButton addBook = new JButton("+ Add Book");
        update.setBackground(new Color(225, 238, 250));
        update.setForeground(navy);

        update.setFocusPainted(false);

        delete.setBackground(new Color(245, 230, 230));
        delete.setForeground(new Color(150, 55, 55));
        addBook.setBackground(navy);
        addBook.setForeground(white);

        delete.setFocusPainted(false);
        addBook.setFocusPainted(false);

        actions.add(update);
        update.addActionListener(e -> {

            int selectedRow = bookTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Please select a book to update.");
                return;
            }

            int modelRow = bookTable.convertRowIndexToModel(selectedRow);
            editingBook = savedBooks.get(modelRow);

            bookTitleField.setText(editingBook.title);
            authorField.setText(editingBook.author);
            genreField.setSelectedItem(editingBook.genre);
            totalPagesField.setText(String.valueOf(editingBook.totalPages));
            startDateField.setText(editingBook.startDate);
            ratingField.setSelectedItem(editingBook.rating);
            notesField.setText(editingBook.notes);

            addBookHeading.setText("Update Book Details");
            addBookSubtitle.setText("Edit your book information");
            saveBookButton.setText("Update Book");

            layout.show(pages, "Add Book");
            
        });
        actions.add(delete);
        actions.add(addBook);

        bottom.add(countLabel, BorderLayout.WEST);
        bottom.add(actions, BorderLayout.EAST);

        library.add(bottom, BorderLayout.SOUTH);

        search.getDocument().addDocumentListener(
                new DocumentListener() {

            private void updateSearch() {

                String query = search.getText().trim();

                if (query.isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(
                            RowFilter.regexFilter(
                                    "(?i)" + java.util.regex.Pattern.quote(query),
                                    1, 2
                            )
                    );
                }
            }

            public void insertUpdate(DocumentEvent e) {
                updateSearch();
            }

            public void removeUpdate(DocumentEvent e) {
                updateSearch();
            }

            public void changedUpdate(DocumentEvent e) {
                updateSearch();
            }
        });

        filter.addActionListener(e -> {

            String selected = (String) filter.getSelectedItem();

            if (selected.equals("All Genres")) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(
                        RowFilter.regexFilter(
                                "(?i)^" + java.util.regex.Pattern.quote(selected) + "$",
                                3
                        )
                );
            }
        });

        delete.addActionListener(e -> {

            int selectedRow = bookTable.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please select a book to delete.");
                return;
            }

            int modelRow = bookTable.convertRowIndexToModel(selectedRow);

            Book selectedBook = savedBooks.get(modelRow);

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this book?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {

                boolean deleted = bookDAO.deleteBook(selectedBook.getBookId());

                if (deleted) {

                    savedBooks.remove(modelRow);

                    refreshBookTable();
                    refreshTracker();

                    

                    JOptionPane.showMessageDialog(
                            this,
                            "Book deleted successfully!");

                } else {

                    JOptionPane.showMessageDialog(
                            this,
                            "Failed to delete book from database.");
                }
            }
        });

        addBook.addActionListener(e -> layout.show(pages, "Add Book"));

        screen.add(library, BorderLayout.CENTER);
        screen.putClientProperty("countLabel", countLabel);

        return screen;
    }

    // READING TRACKER SCREEN
    private JPanel createReadingTracker() {

        JPanel screen = new JPanel(new BorderLayout(12, 15));
        screen.setBackground(bg);
        screen.setBorder(new EmptyBorder(22, 22, 20, 22));

        JPanel heading = new JPanel();
        heading.setBackground(bg);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Reading Progress");
        title.setFont(new Font("Arial", Font.BOLD, 29));
        title.setForeground(text);

        JLabel subtitle = new JLabel(
                "Your reading journey at a glance"
        );
        subtitle.setFont(new Font("Arial", Font.PLAIN, 15));
        subtitle.setForeground(muted);

        heading.add(title);
        heading.add(Box.createVerticalStrut(4));
        heading.add(subtitle);

        screen.add(heading, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setBackground(bg);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JPanel top = new JPanel(new GridLayout(1, 3, 12, 0));
        top.setBackground(bg);
        top.setPreferredSize(new Dimension(900, 225));
        top.setMaximumSize(new Dimension(Integer.MAX_VALUE, 225));

        // CURRENT BOOK CARD
        JPanel currentCard = card();
        currentCard.setLayout(new BorderLayout(8, 10));

        JLabel currentHeading = new JLabel("Current Book");
        currentHeading.setFont(new Font("Arial", Font.BOLD, 14));
        currentCard.add(currentHeading, BorderLayout.NORTH);

        JPanel currentInfo = new JPanel(new BorderLayout(12, 0));
        currentInfo.setBackground(white);

        trackerCover = new JLabel();
        trackerCover.setPreferredSize(new Dimension(95, 145));
        trackerCover.setHorizontalAlignment(SwingConstants.CENTER);

        trackerTitle = new JLabel("No book selected");
        trackerTitle.setFont(new Font("Arial", Font.BOLD, 16));
        trackerTitle.setForeground(text);

        trackerAuthor = new JLabel("Add a book to your library");
        trackerAuthor.setFont(new Font("Arial", Font.PLAIN, 11));
        trackerAuthor.setForeground(muted);

        trackerPages = new JLabel("Pages Read: 0 / 0");
        trackerPages.setFont(new Font("Arial", Font.PLAIN, 11));
        trackerPages.setForeground(muted);

        trackerProgress = new JProgressBar(0, 100);
        trackerProgress.setValue(0);
        trackerProgress.setStringPainted(false);
        trackerProgress.setForeground(blue);
        trackerProgress.setBackground(new Color(225, 235, 245));
        trackerProgress.setBorderPainted(false);

        trackerPercentage = new JLabel("0%");
        trackerPercentage.setFont(new Font("Arial", Font.BOLD, 19));
        trackerPercentage.setForeground(blue);

        trackerStatus = new JLabel("Not Started");
        trackerStatus.setFont(new Font("Arial", Font.PLAIN, 11));
        trackerStatus.setForeground(muted);

        JPanel details = new JPanel();
        details.setBackground(white);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));

        details.add(trackerTitle);
        details.add(Box.createVerticalStrut(5));
        details.add(trackerAuthor);
        details.add(Box.createVerticalStrut(17));
        details.add(trackerPages);
        details.add(Box.createVerticalStrut(7));
        details.add(trackerProgress);
        details.add(Box.createVerticalStrut(9));

        JPanel progressBottom = new JPanel(new BorderLayout());
        progressBottom.setBackground(white);
        progressBottom.add(trackerPercentage, BorderLayout.WEST);
        progressBottom.add(trackerStatus, BorderLayout.EAST);

        details.add(progressBottom);

        currentInfo.add(trackerCover, BorderLayout.WEST);
        currentInfo.add(details, BorderLayout.CENTER);

        currentCard.add(currentInfo, BorderLayout.CENTER);

        // LOG READING SESSION CARD
        JPanel logCard = card();
        logCard.setLayout(new BorderLayout(5, 8));

        JLabel logHeading = new JLabel("Log Reading Session");
        logHeading.setFont(new Font("Arial", Font.BOLD, 14));

        logCard.add(logHeading, BorderLayout.NORTH);

        JPanel logFields = new JPanel();
        logFields.setBackground(white);
        logFields.setLayout(new BoxLayout(logFields, BoxLayout.Y_AXIS));

        trackerBookSelector = new JComboBox<>();
        trackerBookSelector.setFont(new Font("Arial", Font.PLAIN, 12));
        trackerBookSelector.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 32)
        );

        sessionPagesField = new JTextField();
        sessionDateField = new JTextField(LocalDate.now().toString());
        sessionNotesField = new JTextField();

        logFields.add(fieldWithLabel("Choose Book", trackerBookSelector));
        logFields.add(Box.createVerticalStrut(7));
        logFields.add(fieldWithLabel("Pages Read", sessionPagesField));
        logFields.add(Box.createVerticalStrut(7));
        logFields.add(fieldWithLabel("Date (YYYY-MM-DD)", sessionDateField));
        logFields.add(Box.createVerticalStrut(7));
        logFields.add(fieldWithLabel("Notes", sessionNotesField));

        logCard.add(logFields, BorderLayout.CENTER);

        JButton logButton = new JButton("Save Reading Session");
        logButton.setBackground(navy);
        logButton.setForeground(white);
        logButton.setFocusPainted(false);

        logCard.add(logButton, BorderLayout.SOUTH);

        // CUTE BOOK ILLUSTRATION
        JPanel cutePanel = new JPanel(new GridBagLayout());
        cutePanel.setBackground(bg);

        JLabel cuteBook = loadImage(
                "images/cute_book.jpg", 190, 190
        );

        if (cuteBook != null) {
            cutePanel.add(cuteBook);
        } else {
            JLabel fallback = new JLabel(
                    "<html><center>Keep reading!<br>You're doing great!</center></html>"
            );
            fallback.setFont(new Font("Arial", Font.BOLD, 16));
            fallback.setForeground(blue);
            cutePanel.add(fallback);
        }

        top.add(currentCard);
        top.add(logCard);
        top.add(cutePanel);

        content.add(top);
        content.add(Box.createVerticalStrut(13));

        // RECENT READING SESSIONS
        JPanel bottom = new JPanel(new GridLayout(1, 2, 12, 0));
        bottom.setBackground(bg);
        bottom.setPreferredSize(new Dimension(900, 260));
        bottom.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        JPanel recentCard = card();
        recentCard.setLayout(new BorderLayout(5, 10));

        JLabel recentHeading = new JLabel("Recent Reading Sessions");
        recentHeading.setFont(new Font("Arial", Font.BOLD, 14));

        recentCard.add(recentHeading, BorderLayout.NORTH);

        String[] sessionColumns = {
            "Date", "Pages Read", "Total Pages", "Notes"
        };

        sessionTableModel = new DefaultTableModel(
                sessionColumns, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable sessionTable = new JTable(sessionTableModel);
        sessionTable.setFont(new Font("Arial", Font.PLAIN, 11));
        sessionTable.setRowHeight(29);
        sessionTable.setForeground(text);
        sessionTable.setBackground(white);
        sessionTable.setGridColor(borderColor);
        sessionTable.setShowVerticalLines(false);

        JTableHeader sessionHeader = sessionTable.getTableHeader();
        sessionHeader.setFont(new Font("Arial", Font.BOLD, 11));
        sessionHeader.setBackground(new Color(230, 240, 250));
        sessionHeader.setForeground(navy);
        sessionHeader.setReorderingAllowed(false);

        JScrollPane sessionScroll = new JScrollPane(sessionTable);
        sessionScroll.setBorder(
                BorderFactory.createLineBorder(borderColor)
        );

        recentCard.add(sessionScroll, BorderLayout.CENTER);

        // WEEKLY READING OVERVIEW
        JPanel chartCard = card();
        chartCard.setLayout(new BorderLayout(5, 8));

        JLabel chartHeading = new JLabel("Weekly Reading Overview");
        chartHeading.setFont(new Font("Arial", Font.BOLD, 14));

        chartCard.add(chartHeading, BorderLayout.NORTH);

        weeklyChart = new WeeklyChart();
        chartCard.add(weeklyChart, BorderLayout.CENTER);

        bottom.add(recentCard);
        bottom.add(chartCard);

        content.add(bottom);
        screen.add(content, BorderLayout.CENTER);

        trackerBookSelector.addActionListener(e -> updateTrackerDisplay());
        logButton.addActionListener(e -> saveReadingSession());

        refreshTracker();

        return screen;
    }

    // TRACKER FORM FIELD
    private JPanel fieldWithLabel(String labelText, JComponent input) {

        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setBackground(white);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 11));
        label.setForeground(text);

        input.setFont(new Font("Arial", Font.PLAIN, 11));
        input.setPreferredSize(new Dimension(150, 29));

        if (input instanceof JTextField) {
            input.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(borderColor),
                            new EmptyBorder(4, 7, 4, 7)
                    )
            );
        }

        panel.add(label, BorderLayout.NORTH);
        panel.add(input, BorderLayout.CENTER);

        return panel;
    }

    // REFRESH TRACKER BOOK LIST
    private void refreshTracker() {

        if (trackerBookSelector == null) {
            return;
        }

        Book selectedBook =
                (Book) trackerBookSelector.getSelectedItem();

        trackerBookSelector.removeAllItems();

        for (Book book : savedBooks) {
            trackerBookSelector.addItem(book);
        }

        if (selectedBook != null && savedBooks.contains(selectedBook)) {
            trackerBookSelector.setSelectedItem(selectedBook);
        } else if (trackerBookSelector.getItemCount() > 0) {
            trackerBookSelector.setSelectedIndex(0);
        }

        updateTrackerDisplay();
    }

    // UPDATE CURRENT BOOK DISPLAY
    private void updateTrackerDisplay() {

        if (trackerBookSelector == null) {
            return;
        }

        Book book = (Book) trackerBookSelector.getSelectedItem();

        if (book == null) {

            trackerTitle.setText("No book selected");
            trackerAuthor.setText("Add a book to your library");
            trackerPages.setText("Pages Read: 0 / 0");
            trackerPercentage.setText("0%");
            trackerStatus.setText("Not Started");
            trackerProgress.setValue(0);
            trackerCover.setIcon(null);
            trackerCover.setText("No cover");

            refreshSessionTable(null);

            if (weeklyChart != null) {
                weeklyChart.setBook(null);
            }

            return;
        }

        trackerTitle.setText(book.title);
        trackerAuthor.setText(book.author);

        trackerPages.setText(
                "Pages Read: " + book.pagesRead + " / " + book.totalPages
        );

        int percentage = 0;

        if (book.totalPages > 0) {
            percentage = (int) (
                    (book.pagesRead * 100.0) / book.totalPages
            );
        }

        trackerProgress.setValue(percentage);
        trackerPercentage.setText(percentage + "%");

        if (book.pagesRead == 0) {
            trackerStatus.setText("Not Started");
        } else if (book.pagesRead >= book.totalPages
                && book.totalPages > 0) {
            trackerStatus.setText("Completed");
        } else {
            trackerStatus.setText("In Progress");
        }

        // AUTOMATICALLY DISPLAY MATCHING COVER
        trackerCover.setText("");
        trackerCover.setIcon(null);

        if (book.coverPath != null) {

            JLabel coverImage = loadImage(
                    book.coverPath, 95, 145
            );

            if (coverImage != null) {
                trackerCover.setIcon(coverImage.getIcon());
            } else {
                trackerCover.setText("Cover unavailable");
            }

        } else {
            trackerCover.setText(
                    "<html><center>No<br>cover found</center></html>"
            );
        }

        refreshSessionTable(book);

        if (weeklyChart != null) {
            weeklyChart.setBook(book);
        }
    }

    // SAVE A READING SESSION
    private void saveReadingSession() {

        Book book = (Book) trackerBookSelector.getSelectedItem();

        if (book == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please add a book in View Books first."
            );
            return;
        }

        if (book.totalPages <= 0) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please add the total number of pages for this book."
            );
            return;
        }

        int pagesToAdd;

        try {
            pagesToAdd = Integer.parseInt(
                    sessionPagesField.getText().trim()
            );

            if (pagesToAdd <= 0) {
                throw new NumberFormatException();
            }

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid positive number of pages."
            );
            return;
        }

        if (book.pagesRead + pagesToAdd > book.totalPages) {

            JOptionPane.showMessageDialog(
                    this,
                    "You cannot read more pages than the book contains.\n"
                    + "Remaining pages: "
                    + (book.totalPages - book.pagesRead)
            );
            return;
        }

        LocalDate readingDate;

        try {
            readingDate = LocalDate.parse(
                    sessionDateField.getText().trim()
            );

            if (readingDate.isAfter(LocalDate.now())) {
                JOptionPane.showMessageDialog(
                        this,
                        "Please select today or an earlier date."
                );
                return;
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Enter the date in YYYY-MM-DD format.\n"
                    + "Example: 2026-11-02"
            );
            return;
        }

        String notes = sessionNotesField.getText().trim();

        if (notes.isEmpty()) {
            notes = "Reading session";
        }

        int updatedPages = book.pagesRead + pagesToAdd;

        // Get the existing book from MySQL.
        models.Book dbBook = bookDAO.searchBook(book.getBookId());

        if (dbBook == null) {
            JOptionPane.showMessageDialog(this,
                    "Could not find this book in the database.");
            return;
        }

        // Update reading progress and status.
        dbBook.setCurrentPage(updatedPages);

        if (updatedPages >= book.totalPages) {
            dbBook.setReadingStatus("COMPLETED");
        } else {
            dbBook.setReadingStatus("CURRENTLY_READING");
        }

        // Save updated book details.
        if (!bookDAO.updateBook(dbBook)) {
            JOptionPane.showMessageDialog(this,
                    "Could not update reading progress in the database.");
            return;
        }

        // Save the reading session.
        if (!bookDAO.saveReadingSession(
                book.getBookId(),
                readingDate,
                pagesToAdd,
                updatedPages,
                notes)) {

            JOptionPane.showMessageDialog(this,
                    "Progress was updated, but the reading session could not be saved.");
            return;
        }

        // Update application memory only after database operations succeed.
        book.pagesRead = updatedPages;

        ReadingSession session = new ReadingSession(
                readingDate,
                pagesToAdd,
                book.pagesRead,
                notes);

        book.sessions.add(session);

        updateTrackerDisplay();
        refreshDashboardStats();
        

        sessionPagesField.setText("");
        sessionNotesField.setText("");
        sessionDateField.setText(LocalDate.now().toString());

        JOptionPane.showMessageDialog(
                this,
                "Reading session saved successfully!\n"
                + "You have read " + book.pagesRead
                + " out of " + book.totalPages + " pages."
        );
    }

    // REFRESH RECENT SESSION TABLE
    private void refreshSessionTable(Book book) {

        if (sessionTableModel == null) {
            return;
        }

        sessionTableModel.setRowCount(0);

        if (book == null) {
            return;
        }

        for (int i = book.sessions.size() - 1;
                i >= 0 && i >= book.sessions.size() - 5; i--) {

            ReadingSession session = book.sessions.get(i);

            sessionTableModel.addRow(new Object[]{
                session.date.format(dateFormat),
                session.pages,
                session.totalAfterReading,
                session.notes
            });
        }
    }

    // WEEKLY READING CHART
    private class WeeklyChart extends JPanel {

        private Book selectedBook;

        WeeklyChart() {
            setBackground(white);
            setPreferredSize(new Dimension(300, 180));
        }

        void setBook(Book book) {
            selectedBook = book;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int left = 35;
            int right = getWidth() - 15;
            int top = 18;
            int bottom = getHeight() - 32;

            int chartHeight = bottom - top;

            int[] dailyPages = new int[7];

            if (selectedBook != null) {

                LocalDate today = LocalDate.now();

                LocalDate monday = today.with(
                        java.time.DayOfWeek.MONDAY
                );

                for (ReadingSession session : selectedBook.sessions) {

                    if (!session.date.isBefore(monday)
                            && !session.date.isAfter(today)) {

                        int dayIndex =
                                session.date.getDayOfWeek().getValue() - 1;

                        dailyPages[dayIndex] += session.pages;
                    }
                }
            }

            int maxPages = 100;

            for (int value : dailyPages) {
                if (value > maxPages) {
                    maxPages = ((value / 25) + 1) * 25;
                }
            }

            g2.setFont(new Font("Arial", Font.PLAIN, 10));
            g2.setColor(muted);

            for (int i = 0; i <= 4; i++) {

                int y = bottom - (i * chartHeight / 4);
                int value = maxPages * i / 4;

                g2.setColor(new Color(222, 232, 242));
                g2.drawLine(left, y, right, y);

                g2.setColor(muted);
                g2.drawString(String.valueOf(value), 3, y + 4);
            }

            String[] days = {
                "Mon", "Tue", "Wed", "Thu",
                "Fri", "Sat", "Sun"
            };

            int availableWidth = right - left;
            int slotWidth = availableWidth / 7;
            int barWidth = Math.max(10, slotWidth / 2);

            for (int i = 0; i < 7; i++) {

                int barHeight =
                        dailyPages[i] * chartHeight / maxPages;

                int x = left + i * slotWidth
                        + (slotWidth - barWidth) / 2;

                int y = bottom - barHeight;

                g2.setColor(blue);
                g2.fillRoundRect(
                        x, y, barWidth,
                        Math.max(0, barHeight),
                        6, 6
                );

                g2.setColor(text);
                g2.drawString(
                        days[i],
                        x - 2,
                        bottom + 17
                );
            }

            g2.dispose();
        }
    }

    // REFRESH TABLE WITH SAVED BOOKS
    private void refreshBookTable() {

        if (bookTableModel == null) {
            return;
        }

        bookTableModel.setRowCount(0);

        for (Book book : savedBooks) {

            

            bookTableModel.addRow(new Object[]{
                
                book.title,
                book.author,
                book.genre,
                book.totalPages == 0 ? "-" : book.totalPages,
                (book.startDate == null || book.startDate.isEmpty()) ? "-" : book.startDate,
                (book.rating == null || book.rating.equals("Select Rating")) ? "-" : book.rating
            });
        }

        JPanel viewPage = (JPanel) pages.getComponent(2);
        JLabel countLabel = findCountLabel(viewPage);

        if (countLabel != null) {
            countLabel.setText("Total Books: " + savedBooks.size());
        }
    }

    // FIND BOOK COUNT LABEL
    private JLabel findCountLabel(Container container) {

        for (Component component : container.getComponents()) {

            if (component instanceof JLabel) {

                JLabel label = (JLabel) component;

                if (label.getText().startsWith("Total Books:")) {
                    return label;
                }
            }

            if (component instanceof Container) {

                JLabel found = findCountLabel((Container) component);

                if (found != null) {
                    return found;
                }
            }
        }

        return null;
    }

    // FORM FIELD
    private JPanel createFormField(
            String labelText,
            JComponent input
    ) {

        JPanel panel = new JPanel(new BorderLayout(0, 7));
        panel.setBackground(white);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(text);

        input.setFont(new Font("Arial", Font.PLAIN, 12));
        input.setPreferredSize(new Dimension(150, 36));

        if (input instanceof JTextField) {
            input.setBorder(
                    BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(borderColor),
                            new EmptyBorder(5, 8, 5, 8)
                    )
            );
        }

        panel.add(label, BorderLayout.NORTH);
        panel.add(input, BorderLayout.CENTER);

        return panel;
    }

    // COMMON CARD PANEL
    private JPanel card() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        return panel;
    }

    // STATISTIC CARD
    private JPanel statCard(String name, String number) {

        JPanel p = card();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel label = new JLabel(name);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setForeground(muted);

        JLabel value = new JLabel(number);
        value.setFont(new Font("Arial", Font.BOLD, 26));
        value.setForeground(text);

        if (name.equals("Books Read")) {
            booksReadValue = value;
        } else if (name.equals("Currently Reading")) {
            currentlyReadingValue = value;
        } else if (name.equals("Pages This Week")) {
            pagesThisWeekValue = value;
        }

        p.add(label);
        p.add(Box.createVerticalStrut(7));
        p.add(value);

        return p;
    }

    
private void refreshDashboardStats() {

    long booksReadCount = savedBooks.stream()
            .filter(book -> book.pagesRead >= book.totalPages)
            .count();

    long currentlyReadingCount = savedBooks.stream()
            .filter(book -> book.pagesRead > 0
                    && book.pagesRead < book.totalPages)
            .count();

    LocalDate weekStart = LocalDate.now()
            .with(java.time.DayOfWeek.MONDAY);

    int pagesThisWeek = savedBooks.stream()
            .flatMap(book -> book.sessions.stream())
            .filter(session -> !session.date.isBefore(weekStart)
                    && !session.date.isAfter(LocalDate.now()))
            .mapToInt(session -> session.pages)
            .sum();

    int totalPagesRead = savedBooks.stream()
            .flatMap(book -> book.sessions.stream())
            .mapToInt(session -> session.pages)
            .sum();

    int readingTarget = 500;

    int percentage = Math.min(
            100,
            (totalPagesRead * 100) / readingTarget
    );

    booksReadValue.setText(String.valueOf(booksReadCount));
    currentlyReadingValue.setText(String.valueOf(currentlyReadingCount));
    pagesThisWeekValue.setText(String.valueOf(pagesThisWeek));

    if (goalInfo != null && goalProgress != null) {
        goalInfo.setText(totalPagesRead + " of " + readingTarget + " pages");
        goalProgress.setValue(percentage);
        goalProgress.setString(percentage + "%");
    }
}

    // BOOK ROW
    private JPanel bookRow(
            String title,
            String author,
            String status,
            String date,
            String imagePath
    ) {

        JPanel row = new JPanel(new BorderLayout(12, 2));
        
        row.setBackground(white);
        row.setPreferredSize(new Dimension(390, 90));
        row.setMinimumSize(new Dimension(200, 90));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        JLabel cover = loadImage(imagePath, 50, 70);

        if (cover != null) {
            row.add(cover, BorderLayout.WEST);
        }

        JPanel info = new JPanel();
        info.setBackground(white);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel name = new JLabel(title);
        name.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel writer = new JLabel(author);
        writer.setFont(new Font("Arial", Font.PLAIN, 11));
        writer.setForeground(muted);

        info.add(Box.createVerticalGlue());
        info.add(name);
        info.add(Box.createVerticalStrut(4));
        info.add(writer);
        info.add(Box.createVerticalGlue());

        JPanel right = new JPanel();
        right.setBackground(white);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        String displayStatus = status;

        if ("CURRENTLY_READING".equals(status)) {
            displayStatus = "Reading";
        } else if ("NOT_STARTED".equals(status)) {
            displayStatus = "Not Started";
        } else if ("ON_HOLD".equals(status)) {
            displayStatus = "On Hold";
        } else if ("COMPLETED".equals(status)) {
            displayStatus = "Completed";
        }

        JLabel state = new JLabel(displayStatus);
        state.setPreferredSize(new Dimension(130, 20));
        state.setFont(new Font("Arial", Font.PLAIN, 10));

        JLabel dateLabel = new JLabel(date);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 10));

        right.add(Box.createVerticalGlue());
        right.add(state);
        right.add(dateLabel);
        right.add(Box.createVerticalGlue());

        row.add(info, BorderLayout.CENTER);
        row.add(right, BorderLayout.EAST);

        return row;
    }

    // IMAGE LOADING
    private JLabel loadImage(String path, int width, int height) {

        File imageFile = new File(path);

        if (!imageFile.exists()) {
            System.out.println(
                    "Image not found: " + imageFile.getAbsolutePath()
            );
            return null;
        }

        ImageIcon original = new ImageIcon(imageFile.getAbsolutePath());

        if (original.getIconWidth() <= 0
                || original.getIconHeight() <= 0) {
            return null;
        }

        Image scaled = original.getImage().getScaledInstance(
                width, height, Image.SCALE_SMOOTH
        );

        JLabel imageLabel = new JLabel(new ImageIcon(scaled));
        imageLabel.setPreferredSize(new Dimension(width, height));

        return imageLabel;
    }

    // MAIN METHOD
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}