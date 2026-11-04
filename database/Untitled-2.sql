USE book_reading_db;

ALTER TABLE books
ADD COLUMN start_date VARCHAR(30),
ADD COLUMN rating VARCHAR(10),
ADD COLUMN notes TEXT;
DESCRIBE books;

USE book_reading_db;

SELECT book_id, title, author
FROM books
WHERE title = 'Atomic Habits';
START TRANSACTION;

DELETE FROM books
WHERE title = 'Atomic Habits'
AND book_id <> 36;
COMMIT;

USE book_reading_db;
DESCRIBE books;

SELECT COLUMN_NAME, COLUMN_TYPE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'book_reading_db'
AND TABLE_NAME = 'books';