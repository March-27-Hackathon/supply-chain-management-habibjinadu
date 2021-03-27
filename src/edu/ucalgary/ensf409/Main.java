package edu.ucalgary.ensf409;
import java.sql.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Test");

        // Habib's Test Code
        FurnitureData database = new FurnitureData("jdbc:mysql://localhost/inventory","habib","password");

        database.initializeConnection(); // initialize the connection
        
        Connection databaseConnection = database.getDatabaseConnection();
    }
}