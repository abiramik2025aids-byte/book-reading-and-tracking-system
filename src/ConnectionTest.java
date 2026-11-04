import java.sql.Connection;
import java.sql.SQLException;
import database.DBConnection;

public class ConnectionTest {

    public static void main(String[] args) {

        try {
            Connection con = DBConnection.getConnection();

            if (con != null) {
                System.out.println("MySQL Connected Successfully!");
            }

            con.close();

        } catch (SQLException e) {
            System.out.println("Connection Failed!");
            e.printStackTrace();
        }
    }
}