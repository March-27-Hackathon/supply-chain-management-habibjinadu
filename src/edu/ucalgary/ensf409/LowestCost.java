package edu.ucalgary.ensf409;

import java.sql.*;
import java.util.*;

/**
 * Uses a connection to a database to calculate the lowest cost of creating a furniture item, given the desired furniture
 * item and its type.
 */
public class LowestCost {
    private Connection dbConnect;
    private ResultSet results;
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

    /**
     * This method will use the database connection, the furniture category and type, and the number of items to be
     * ordered to determine the most cost-efficient combination of furniture items to use from the database. Once it
     * does, it will update the database to remove the furniture items it's using, and will also create and return a
     * FurnitureOrder object consisting of all of the relevant data for the order.
     * @return The FurnitureOrder object, which contains all data relevant to the furniture order.
     */
    public FurnitureOrder findBestCombination() {
        FurnitureOrder order = new FurnitureOrder(furnitureCategory, furnitureType, numberOfItems);
        try {
            
            Statement stmt = dbConnect.createStatement(
                                            ResultSet.TYPE_SCROLL_INSENSITIVE,
                                                    ResultSet.CONCUR_UPDATABLE);
            results = stmt.executeQuery("SELECT * FROM " +
                    furnitureCategory + " WHERE Type = '" + furnitureType +
                    "'");

            createItemTable(); // creates the item table

            //The following just prints the query results to the screen, for testing purposes
        /*  while(results.next()) {
                System.out.println(results.getString("ID")+" "+results.getString("Type")+" "+results.getString("Legs")+" "+results.getString("Arms")+" "+results.getString("Seat")+" "+
                        results.getString("Cushion")+" "+results.getInt("Price")+" "+results.getString("ManuID"));
            } */

            printTableItem();
            ArrayList<ArrayList<Integer>> potentialCombos = filterCombos(generateAllCombos());
            ArrayList<Integer> lowestCombo = null;
            finalPrice = calculatePrice(potentialCombos.get(0));
            for (ArrayList<Integer> potentialCombo : potentialCombos) {
                int tempPrice = calculatePrice(potentialCombo);
                System.out.println(tempPrice);
                if (tempPrice <= finalPrice) {
                    lowestCombo = potentialCombo;
                    finalPrice = tempPrice;
                }
            }
            if(lowestCombo != null) {
                System.out.println("FINAL PRICE: " + finalPrice);
                System.out.println("ROWS USED:");
                for (int row : lowestCombo) {
                    System.out.println(row + 1); //starts at 1
                }
            } else {
                System.out.println("No combinations exist");
            }

        } catch (SQLException e) {
            System.err.println("An SQLException occurred while selecting from "
                    + furnitureCategory + " with the Type " + furnitureType);
            e.printStackTrace();
        }
        return order;
    }

    /**
     * printTableItem prints the table which shows the working and broken parts
     * for each furniture item in the itemTable
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

    /**
     * createItemTable creates a 2D boolean array that is populated with 
     * the working and broken parts for each furniture item in the ResultSet
     * for example if a furniture item in the Result set has a Y Y N for it's 
     * parts. The corresponding row in the itemTable will contain 
     * true true false
     * @throws SQLException if the database cannot be accessed, an SQL exception
     * is thrown
     */
    private void createItemTable () throws SQLException
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
            fill(this.itemTable[i]); // fill each row with true if
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
        int numberOfParts = -1; // default value if the last row is not found
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

    private int getRowPrice(int rowIndex) throws SQLException{
        //rowIndex starts at 1
        int savedRow = results.getRow();
        results.absolute(rowIndex);
        int result = results.getInt("Price");
        results.absolute(savedRow);
        return result;
    }

    public int calculateChairPrice() throws SQLException {
        ArrayList<ArrayList<String>> combinations = new ArrayList<>();
        ResultSet original = results;
        results.next();
        boolean[] parts = new boolean[4];
        fill(parts);
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
            fill(tempParts);
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
    private void fill(boolean[] parts) throws SQLException{
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

    /*
    public int calculateDeskPrice() {
        return 0;
    }

    public int calculateFilingPrice() {
        return 0;
    }

    private int lampPrice() throws SQLException{
        return calculateLampPrice(itemTable[0], itemTable, 1);
    }
    private int calculateLampPrice(boolean[] foundParts, boolean[][] parts, int currentRow) throws SQLException{
        if(containsAllTrue(foundParts)) {
            return getRowPrice(currentRow);
            //combination found
        }
        if(parts.length == 1) {
            if(checkNewPart(foundParts, parts[0])) {
                addArrays(foundParts, parts[0]);
                if(containsAllTrue(foundParts)) {
                    return getRowPrice(currentRow);
                    //success!
                } else {
                    return -1;
                    //failure
                }
            } else {
                return -1;
                //failure
            }
        }
        //if there are new parts to be added, the price should include the current row
        if(checkNewPart(foundParts, parts[0])) {
            addArrays(foundParts, parts[0]);
            return getRowPrice(currentRow) + calculateLampPrice(foundParts, Arrays.copyOfRange(parts, 1, parts.length), currentRow+1);
        }
        //otherwise it should not include the current row
        return calculateLampPrice(foundParts, Arrays.copyOfRange(parts, 1, parts.length), currentRow+1);
    }
    */

    /**
     * Checks if the given furniture item combination results in a full piece of furniture, with at least one of each
     * part intact
     * @param combo The combination of furniture items
     * @return whether or not the combination creates a full piece of furniture
     */
    private boolean isValidCombo(ArrayList<Integer> combo) {
        boolean [] parts = new boolean[itemTable[0].length];
        for (Integer row : combo) {
            addArrays(parts, itemTable[row]);
        }
        return containsAllTrue(parts);
    }

    /**
     * Uses the resultSet to get the price of a combination of furniture items
     * @param combo A combination of furniture items
     * @return The price of all furniture items in the combination
     * @throws SQLException if the resultSet is unable to get the price of one of the rows
     */
    private int calculatePrice(ArrayList<Integer> combo) throws SQLException{
        int price = 0;
        int savedIndex = results.getRow();
        for(int rowIndex : combo) {
            results.absolute(rowIndex+1);
            price += results.getInt("Price");
        }
        results.absolute(savedIndex);
        return price;
    }

    /**
     * Receives a list of combinations of furniture items, and returns a list that only contains combinations where all
     * parts are found
     * @param allCombos A list of any combination of furniture items
     * @return A list of combinations of furniture items containing all parts
     */
    private ArrayList<ArrayList<Integer>> filterCombos(ArrayList<ArrayList<Integer>> allCombos) {
        ArrayList<ArrayList<Integer>> result = new ArrayList<>();
        for (ArrayList<Integer> current : allCombos) {
            if (isValidCombo(current)) {
                result.add(current);
            }
        }
        return result;
    }

    /**
     * This method generates all possible combinations of itemTable rows (corresponding to a furniture item in the
     * database). It picks each row using its itemTable index. For example, the first index will be 0, the next will be
     * 1, etc. Each element in the returned ArrayList is itself an ArrayList, containing all of the indexes of the rows
     * used. After calling this function, the calculation method should determine which of the combinations result in
     * furniture items that have at least one of each functional part.
     * @return A list that contains all possible combinations of furniture items.
     */
    private ArrayList<ArrayList<Integer>> generateAllCombos() {
        ArrayList<ArrayList<Integer>> list = new ArrayList<>();
        //total array length is 2^itemTable.length
        for(int i = 0; i < Math.pow(2, itemTable.length); i++) {
            //this next part uses the binary representation of i to translate i to a combination of choices whether or
            //not to include each row of itemTable in the combination
            String s = Integer.toBinaryString(i);
            //pad s with zeros
            StringBuilder newStr = new StringBuilder();
            if(s.length() < itemTable.length) {
                for(int j = 0; j < itemTable.length-s.length(); j++) {
                    newStr.append('0');
                }
            }
            newStr.append(s);
            //newS is now a String that contains i in a binary representation, padded with zeros to achieve a length of
            //itemTable.length
            ArrayList<Integer> combination = new ArrayList<>();
            for(int j = 0; j < itemTable.length; j++) {
                if(newStr.charAt(j) == '1') {
                    //include the row of itemTable with index j as part of the combination
                    combination.add(j);
                }
            }
            list.add(combination); //add this combination of itemTable rows to the list of all combinations
        }
        return list;
    }
}

