USE book_reading_db;

SELECT * FROM reading_sessions;

USE book_reading_db;

SELECT
    book_id,
    title,
    current_page,
    total_pages,
    reading_status
FROM books
WHERE reading_status = 'COMPLETED';

USE book_reading_db;

SELECT
    reading_status,
    COUNT(*) AS book_count
FROM books
GROUP BY reading_status;

USE book_reading_db;

SELECT
    SUM(pages_read) AS total_pages_read,
    COUNT(*) AS total_sessions
FROM reading_sessions;

USE book_reading_db;

SELECT
    book_id,
    SUM(pages_read) AS total_session_pages,
    MAX(total_after_reading) AS latest_page
FROM reading_sessions
GROUP BY book_id;

USE book_reading_db;

SHOW TABLES;

 USE book_reading_db;

SELECT * FROM books;

SELECT * FROM reading_sessions;

SELECT 
    b.title,
    r.session_id,
    r.session_date,
    r.pages_read,
    r.total_after_reading
FROM books b
JOIN reading_sessions r
ON b.book_id = r.book_id
ORDER BY r.session_id DESC;