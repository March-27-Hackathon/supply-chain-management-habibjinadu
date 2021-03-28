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
    private boolean[][] itemTable;

    public LowestCost(Connection dbConnect, String category, String fType, int numItems) {
        this.dbConnect = dbConnect;
        this.furnitureCategory = category;
        this.furnitureType = fType;
        this.numberOfItems = numItems;
    }

    public void findBestCombination() {
        try {
            
            Statement stmt = dbConnect.createStatement(
                                            ResultSet.TYPE_SCROLL_INSENSITIVE,
                                                    ResultSet.CONCUR_UPDATABLE);
            ResultSet results = stmt.executeQuery("SELECT * FROM " +
                    furnitureCategory + " WHERE Type = '" + furnitureType +
                    "'");

            createItemTable(results);
            

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

    /**
     * printTableItem prints the 
     */
    private void printTableItem()
    {
        // for each row in the 2D array
        for (boolean[] boolArray: this.itemTable) 
            {
                for (boolean bool: boolArray)   // for each boolean
                {
                    System.out.print(bool); // print the boolean value
                    System.out.print("            "); // print a large space

                }

                System.out.println(); // print a new line
            }
    }
    private void createItemTable (ResultSet results) throws SQLException
    {
        // get the number of rows.
        int numberOfRows = getNumberOfRows(results);
        // get the number of parts
        int numberOfParts = getNumberOfParts(results);
        // instantiate the 2D item table array
        this.itemTable = new boolean [numberOfRows][numberOfParts];
        
        // interate through the itemTable
        for (int i = 0; i < this.itemTable.length; i++)
        {
            results.next(); // navigate to the next result
            fill(results, this.itemTable[i]); // fill each row with true if
                                            // the item is Y, false if the item
                                            // N
        }

        results.beforeFirst(); // set the results cursor back to row 0

    }

    private int getNumberOfRows(ResultSet results)
    {
        int lastRow = -1; // default value for the last row
        try
        {
            results.last(); // nagivate to the last row of the table
            lastRow = results.getRow(); // get the row number in the last row
            results.absolute(0); // return the cursor to row 0
            
        }
        catch (SQLException e)
        {
            System.err.println("Could not move the cursor In the ResultSet");
            e.printStackTrace();
        }
        return lastRow; // return the index of the last row
    }

    private int getNumberOfParts (ResultSet results)
    {
        int numberOfParts = -1; // default value if the last row is not foound
        try
        {
            // find the number of parts for the table
            numberOfParts = results.findColumn("Price") - 3;
        }
        catch (SQLException e)
        {
            System.err.println("Could not find the column named 'Price'");
            e.printStackTrace();
        }
        return numberOfParts; // return the number of parts for the table
    }
    public int calculateChairPrice(ResultSet results) throws SQLException {
        ArrayList<ArrayList<String>> combinations = new ArrayList<>();
        ResultSet original = results;
        results.next();
        boolean[] parts = new boolean[4];
        fill(results, parts);
        // for (int i = 0; i < )
        int index = 0;
        int savedIndex = 0;
        ArrayList<String> newCombination = new ArrayList<>();
        newCombination.add(results.getString("ID"));

        while(results.next()) {
            if(containsAllTrue(parts)) {
                break;
            }
            boolean[] tempParts = new boolean[4];
            fill(results, tempParts);
            if (checkNewPart(parts,tempParts)) {
                addArrays(parts, tempParts);
                newCombination.add(results.getString("ID"));
                if(savedIndex != 0) {
                    savedIndex = index;
                }
            }    
            index++;
        }
        combinations.add(newCombination);
        return 0;
    }

    private boolean checkNewPart(boolean[] parts, boolean[] tempParts)
    {
        for (int i = 0; i < parts.length; i++)
        {
            if (parts[i] == false && tempParts[i] == true)
            {
                return true;
            }
        }

        return false;
    }
    private void fill(ResultSet results, boolean[] parts) throws SQLException{
        int numberOfParts = getNumberOfParts(results);
        for(int i = 3; i < numberOfParts + 3 ; i++) {
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
