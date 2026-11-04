USE book_reading_db;

SELECT * FROM reading_sessions;

SELECT book_id, title, total_pages, current_page, reading_status
FROM books
WHERE book_id = 35;

USE book_reading_db;

SELECT book_id, title, total_pages, current_page, reading_status
FROM books
WHERE title LIKE '%Alchemist%';

SELECT * FROM reading_sessions
WHERE book_id = 2;

SELECT book_id, title, current_page, total_pages, reading_status
FROM books
WHERE book_id = 2;