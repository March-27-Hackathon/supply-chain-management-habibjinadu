package edu.ucalgary.ensf409;
import java.sql.*;

public class Main {
    public static void main(String[] args) {


        // Habib's Test Code
        FurnitureData database = new FurnitureData("jdbc:mysql://localhost/inventory","carter","3nsf409*");

        database.initializeConnection(); // initialize the connection

        Connection databaseConnection = database.getDatabaseConnection();

        //Carter's test code
        LowestCost calculation = new LowestCost(databaseConnection, "Chair", "Ergonomic", 3);
        calculation.findBestCombination();
    }
}