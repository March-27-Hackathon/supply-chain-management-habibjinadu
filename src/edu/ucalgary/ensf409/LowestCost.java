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
            Statement stmt = dbConnect.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                                                    ResultSet.CONCUR_UPDATABLE);
            ResultSet results = stmt.executeQuery("SELECT * FROM " +
                    furnitureCategory + " WHERE Type = '" + furnitureType +
                    "'");

            createItemTable(results);
            for (boolean[] boolArray: this.itemTable)
            {
                for (boolean bool: boolArray)
                {
                    System.out.print(bool);
                    System.out.print("            ");

                }

                System.out.println();
            }

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

    private void createItemTable (ResultSet results) throws SQLException
    {
        int numberOfRows = getNumberOfRows(results);
        int numberOfParts = getNumberOfParts(results);
        this.itemTable = new boolean [numberOfRows][numberOfParts];
        
        for (int i = 0; i < this.itemTable.length; i++)
        {
            results.next();
            fill(results, this.itemTable[i]);

        }

        results.beforeFirst();

    }

    private int getNumberOfRows(ResultSet results)
    {
        int lastRow = -1;
        try
        {
            results.last();
            lastRow = results.getRow();
            results.absolute(0);
            
        }
        catch (SQLException e)
        {
            System.err.println("Could not move the cursor In the ResultSet");
            e.printStackTrace();
        }
        return lastRow;
    }

    private int getNumberOfParts (ResultSet results)
    {
        int numberOfParts = -1;
        try
        {
            numberOfParts = results.findColumn("Price") - 3;
        }
        catch (SQLException e)
        {
            System.err.println("Could not find the column named 'Price'");
            e.printStackTrace();
        }
        return numberOfParts;
    }

    private int getRowPrice(ResultSet results, int rowIndex) throws SQLException{
        //rowIndex starts at 1
        int savedRow = results.getRow();
        results.absolute(rowIndex);
        int result = results.getInt("Price");
        results.absolute(savedRow);
        return result;
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

    public int calculateLampPrice(ResultSet results, boolean[] foundParts, boolean[][] parts, int currentRow) throws SQLException{
        //base case is when parts.length == 1 AND parts[0] doesn't satisfy the requirements
        //also currentRow should start at 1, eg, when calling the function, currentRow should be 1

        addArrays(foundParts, parts[0]);
        if(!containsAllTrue(foundParts)) {
            if(parts.length == 1) {
                return -1; //no combination found
            } else {
                return getRowPrice(results, currentRow) +
                        calculateLampPrice(results, foundParts, Arrays.copyOfRange(parts, 1, parts.length), currentRow+1);
            }
        }

        return getRowPrice(results, currentRow);
    }
}
