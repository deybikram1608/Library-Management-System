import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/library_db";
    private static final String USER = "root";
    private static final String PASSWORD = "your_password_here"; //Replace your mySQL password here.

    public static Connection getConnection() {
        try{
            Connection con= DriverManager.getConnection(URL, USER, PASSWORD);
            return con;
        }
        catch(Exception e){
            System.out.println("Connection failed: "+ e.getMessage());
            return null;
        }
    }
}
