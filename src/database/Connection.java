package database;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connection {
    private static final String URL="jdbc:oracle:thin:@//localhost:1521/XE";
    private static final String USERNAME="system";
    private static final String PASSWORD="oracle";
    
    public static java.sql.Connection getConnection() throws SQLException{
        return DriverManager.getConnection(URL,USERNAME,PASSWORD);
    }

    
}
