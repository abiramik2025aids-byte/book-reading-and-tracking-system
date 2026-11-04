package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/book_reading_db";

    private static final String USER = "root";

    private static String loadPassword() {
        try {
            List<String> lines = Files.readAllLines(
                    Paths.get(".env"));

            for (String line : lines) {
                if (line.startsWith("DB_PASSWORD=")) {
                    return line.substring("DB_PASSWORD=".length()).trim();
                }
            }

            throw new IllegalStateException(
                    "DB_PASSWORD not found in .env");

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Unable to read .env file", e);
        }
    }

    private static final String PASSWORD = loadPassword();

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}