CREATE DATABASE IF NOT EXISTS book_reading_db;

USE book_reading_db;

CREATE TABLE IF NOT EXISTS books (
    book_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    author VARCHAR(100) NOT NULL,
    genre VARCHAR(50),
    isbn VARCHAR(20) UNIQUE,
    total_pages INT NOT NULL,
    current_page INT DEFAULT 0,

    reading_status ENUM(
        'NOT_STARTED',
        'CURRENTLY_READING',
        'COMPLETED',
        'ON_HOLD'
    ) DEFAULT 'NOT_STARTED',

    date_added TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CHECK (total_pages > 0),
    CHECK (current_page >= 0),
    CHECK (current_page <= total_pages)
);

SHOW TABLES;

DESCRIBE books;
INSERT INTO books
(title, author, genre, isbn, total_pages, current_page, reading_status)
VALUES
('Wings of Fire', 'APJ Abdul Kalam', 'Autobiography',
 '9788173711466', 180, 50, 'CURRENTLY_READING'),

('The Alchemist', 'Paulo Coelho', 'Fiction',
 '9780061122415', 208, 208, 'COMPLETED'),

('Harry Potter', 'JK Rowling', 'Fantasy',
 '9780747532699', 350, 0, 'NOT_STARTED'),

('Atomic Habits', 'James Clear', 'Self Help',
 '9781847941831', 320, 100, 'CURRENTLY_READING');

 SELECT * FROM books;

 SELECT
    title,
    current_page,
    total_pages,
    ROUND((current_page * 100.0) / total_pages, 2) AS progress_percentage
FROM books;

SELECT title, reading_status
FROM books;
UPDATE books
SET current_page = 75,
    reading_status = 'CURRENTLY_READING'
WHERE book_id = 1;
SELECT * FROM books WHERE book_id = 1;

DELETE FROM books
WHERE book_id = 4;
SELECT * FROM books;

USE book_reading_db;

SELECT * FROM books;
