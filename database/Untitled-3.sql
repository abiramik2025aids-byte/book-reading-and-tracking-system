USE book_reading_db;

DESCRIBE books;

USE book_reading_db;

ALTER TABLE books
MODIFY rating VARCHAR(20);

USE book_reading_db;

SELECT 
    title,
    author,
    COUNT(*) AS duplicate_count
FROM books
GROUP BY title, author
HAVING COUNT(*) > 1;

SELECT *
FROM books
WHERE LOWER(title) = 'the alchemist'
AND LOWER(author) = 'paulo coelho';

SELECT *
FROM books
ORDER BY book_id;

USE book_reading_db;

DELETE FROM books
WHERE book_id IN (35, 48);

SELECT
    book_id,
    title,
    author,
    total_pages,
    current_page,
    reading_status,
    start_date,
    rating,
    notes
FROM books
ORDER BY book_id;

DELETE FROM books
WHERE book_id = 46;

UPDATE books
SET reading_status = 'CURRENTLY_READING'
WHERE book_id = 1;

SELECT book_id, title, current_page, total_pages, reading_status
FROM books
ORDER BY book_id;

USE book_reading_db;

SELECT
    book_id,
    title,
    current_page,
    total_pages,
    reading_status
FROM books
ORDER BY book_id;