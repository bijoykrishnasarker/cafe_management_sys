package cafemanagementsystem;

import java.sql.*;

public class Conn {
    Connection c;
    Statement s;

    public Conn(){ 
        try {
            // Optional: Load MySQL driver (good practice)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Use correct password and full JDBC URL
            c = DriverManager.getConnection(
                "jdbc:mysql://localhost:3307/cafe_management", 
                "root", 
                "Bijoy1219"
            );

            s = c.createStatement();
            System.out.println("Database connected successfully.");
        } catch(Exception e){
            System.out.println("Database connection error: " + e);
        }
    }
}
