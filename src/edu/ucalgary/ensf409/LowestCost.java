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
        ArrayList<ArrayList<String>> combinations = new ArrayList<>();
        ResultSet original = results;
        results.next();
        boolean[] parts = new boolean[4];
        fill(results, parts);
        while(results.next()) {
            if(containsAllTrue(parts)) {

            }
            boolean[] tempParts = new boolean[4];
            fill(results, tempParts);
            addArrays(parts, tempParts);
        }

    }

    private static void fill(ResultSet results, boolean[] parts) throws SQLException{
        for(int i = 3; i < 7; i++) {
            if (results.getString(i).equals("Y")) {
                parts[i-3] = true;
            }
        }
    }

    private static void addArrays(boolean[] a, boolean[] b) {
        for(int i = 0; i < a.length; i++) {
            if(!a[i]) {
                a[i] = b[i];
            }
        }
    }

    private static boolean containsAllTrue(boolean[] src) {
        for(int i = 0; i < src.length; i++) {
            if(!src[i]) {
                return false;
            }
        }
        return true;
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
