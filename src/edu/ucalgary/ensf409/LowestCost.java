package edu.ucalgary.ensf409;

import java.sql.*;
import java.util.*;

/**
 * Uses a connection to a database to calculate the lowest cost of creating a furniture item, given the desired furniture
 * item and its type.
 */
public class LowestCost {
    private Connection dbConnect;
    private String furnitureCategory;
    private String furnitureType;
    private int numberOfItems;
    private int finalPrice;

    public LowestCost(Connection dbConnect, String category, String fType, int numItems) {
        this.dbConnect = dbConnect;
        this.furnitureCategory = category;
        this.furnitureType = fType;
        this.numberOfItems = numItems;
    }

    public void findBestCombination() {
        try {
            Statement stmt = dbConnect.createStatement();
            ResultSet results = stmt.executeQuery("SELECT * FROM " +
                    furnitureCategory + " WHERE Type = '" + furnitureType +
                    "'");

            //The following just prints the query results to the screen, for testing purposes
            while(results.next()) {
                System.out.println(results.getString("ID")+" "+results.getString("Type")+" "+results.getString("Legs")+" "+results.getString("Arms")+" "+results.getString("Seat")+" "+
                        results.getString("Cushion")+" "+results.getInt("Price")+" "+results.getString("ManuID"));
            }

            stmt.close();
            results.close();
        } catch (SQLException e) {
            System.err.println("An SQLException occurred while selecting from "
                    + furnitureCategory + " with the Type " + furnitureType);
        }
    }

    public int calculateChairPrice(ResultSet results) throws SQLException {
        return 0;
    }

    private void checkFilled(ResultSet results, boolean[] parts) throws SQLException{
        for(int i = 3; i < 7; i++) {
            if (results.getString(i).equals("Y")) {
                parts[i-3] = true;
            }
        }
    }

    public int calculateDeskPrice(ResultSet results) {
        return 0;
    }

    public int calculateFilingPrice(ResultSet results) {
        return 0;
    }

    public int calculateLampPrice(ResultSet results) {
        return 0;
    }
}
