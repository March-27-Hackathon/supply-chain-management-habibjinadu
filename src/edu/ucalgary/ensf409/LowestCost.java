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

            createItemTable(results); // creates the item table

            //The following just prints the query results to the screen, for testing purposes
        /*    while(results.next()) {
                System.out.println(results.getString("ID")+" "+results.getString("Type")+" "+results.getString("Legs")+" "+results.getString("Arms")+" "+results.getString("Seat")+" "+
                        results.getString("Cushion")+" "+results.getInt("Price")+" "+results.getString("ManuID"));
            } */

            System.out.println("Calculated desk lamp price: " + lampPrice(results, itemTable));
            stmt.close();
            results.close();
        } catch (SQLException e) {
            System.err.println("An SQLException occurred while selecting from "
                    + furnitureCategory + " with the Type " + furnitureType);
            e.printStackTrace();
        }
    }

    /**
     * printTableItem prints the item table onto the screen
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
     * printTableItem prints the 2D boolean array onto the screen
     */
    private void printTableItem(boolean [][] itemTable)
    {
        // for each row in the 2D array
        for (boolean[] boolArray: itemTable) 
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

    /**
     * checkNewPart checks to see if tempParts has a specific entry where it is 
     * true and where that entry is false in parts. for example is parts is
     * [true false true] and tempParts is [false true false] the method will 
     * return true.
     */
    private boolean checkNewPart(boolean[] parts, boolean[] tempParts)
    {
        // for all entries in the parts
        for (int i = 0; i < parts.length; i++)
        {
            // if temp parts and the entry in parts is false and the entry in
            // temp parts is true, return true. This means there is a new part
            if (parts[i] == false && tempParts[i] == true)
            {
                return true; // return true
            }
        }

        return false;
    }

    private boolean[][] copyItemTable()
    {
        // initalize the size of the copy
        boolean[][] copy = new 
                    boolean[this.itemTable.length][this.itemTable[0].length];

        // for each row in the itemTable
        for (int i = 0; i < this.itemTable.length; i++)
        {
            // make a copy of the row
            copy[i] = Arrays.copyOf(this.itemTable[i], this.itemTable[i].length);
        }

        return copy;
    }

    private boolean[][] copyItemTable(boolean[][] itemTable)
    {
        // initalize the size of the copy
        boolean[][] copy = new 
                    boolean[itemTable.length][itemTable[0].length];

        // for each row in the itemTable
        for (int i = 0; i < itemTable.length; i++)
        {
            // make a copy of the row
            copy[i] = Arrays.copyOf(itemTable[i], itemTable[i].length);
        }

        return copy;
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

    private int lampPrice(ResultSet results, boolean[][] parts) throws SQLException{
        
        return calculateLampPrice (results, parts);
    }
    private int calculateLampPrice(ResultSet results, boolean[][] parts) throws SQLException{
        
        // if (!checkSolutionPossibility(parts, this.numberOfItems))
        // {
        //     return -1; // no solution is possible
        // }

        // get the number of parts that the furniture has
        int numberOfParts = parts[0].length; 

        BestOrder totalOrder = new BestOrder();
        BestOrder cheapestOption  = new BestOrder();
        BestOrder currentOption = new BestOrder();
        
        // initialize the rows to be skipped.
        ArrayList<Integer> rowsToSkip = new ArrayList<Integer>();
        
        //initialize the current row to 0
        int currentRow = 0;

        // makes of copy of the itemTable
        boolean[][] partsCopy = copyItemTable();

        // create an empty boolean array filled with false
        boolean [] currentlyFulfilled = new boolean[partsCopy[0].length];

        //find the cheapest for one order
        boolean value = findCheapestForOne(results, partsCopy,cheapestOption, 
                                            currentOption, currentlyFulfilled);
        
        System.out.println("Cheapest Combination: " + cheapestOption.getFurnitureIds().toString());
        System.out.println("Price for Cheapest Combination: " + cheapestOption.getFinalPrice());
        return cheapestOption.getFinalPrice();
    }

    /**
     * findCheapestForOne tries to find the cheapest combination of items for 
     * one piece of furniture. It does this by calculating the possible 
     * alternative options at each build step so it can compare the price of
     * all of the alternatives.
     * @param results
     * @param parts
     * @param cheapest
     * @param currentRow
     * @param rowsToSkip
     * @param currentlyFulfilled
     * @return
     */
    public boolean findCheapestForOne(ResultSet results, boolean[][] parts,
                                        BestOrder cheapestOption, 
                                        BestOrder currentOption,
                                        boolean[] currentlyFulfilled)
                                        throws SQLException
    {

        // calculate the other rows that can fill the item
        int [] alternatives = calculateAlternatives(currentlyFulfilled);
        // for all the furniture in the table
        for (int i = 0; i < alternatives.length; i ++)
        {

            
                // add both arrays
                addArrays(currentlyFulfilled, parts[alternatives[i]]); 
                // add the furniture to the list
                currentOption.addNewFurniture(results, alternatives[i]);
                

                // if we have found all the parts
                if (containsAllTrue(currentlyFulfilled))
                {
                    
                    
                    // if the cheapestPrice is zero
                    if (cheapestOption.getFinalPrice() == 0)
                    {
                        cheapestOption.deepCopy(currentOption);
                        // set the cheapestOption to the currrent option
                        // cheapestOption = currentOption.deepCopy();
                    }else
                    {
                        // if the current calculated price is less than the
                        // cheapest, update the cheapest price
                        if 
                        (currentOption.getFinalPrice() < 
                            cheapestOption.getFinalPrice())
                        {
                            cheapestOption.deepCopy(currentOption);
                            // set the cheapest option to be the current 
                            // option
                            // cheapestOption = currentOption.deepCopy();
                        }
                    }

                    // remove the most recently added furniture item and 
                    // look at the next alternative
                    currentOption.removeFurniture(results, alternatives[i]);

                    // remove the parts that were added to the current 
                    // fulfilled by this furniture and try again
                    repopulateCurrentlyFilled(currentlyFulfilled, parts,
                                    currentOption.getFurnitureRowNumbers());
                    
                    
                }
                else
                {
                    

                // find the next cheapest
                findCheapestForOne(results, parts, cheapestOption, 
                    currentOption, currentlyFulfilled);
                
                // remove the most recently added furniture item and 
                // look at the next alternative
                currentOption.removeFurniture(results, alternatives[i]);

                // remove the parts that were added to the current 
                // fulfilled by this furniture and try again
                repopulateCurrentlyFilled(currentlyFulfilled, parts,
                                currentOption.getFurnitureRowNumbers());
                    
                }


        }
        
        return false; 
    }

    /**
     * repoplulateCurrentFilled repolulates the currently fulfilled boolean 
     * array with the available parts from the current order
     */
    public void repopulateCurrentlyFilled(boolean[] currentlyFulfilled,
                                        boolean[][] parts,
                                        ArrayList<Integer> rowNumbers)
    {
        
        // clear the currentlyFulfilled array and fill it with false
        Arrays.fill(currentlyFulfilled, false);

        for (int i = 0; i < rowNumbers.size(); i++)
        {
            // add the currently fulfilled array with the parts array
            addArrays(currentlyFulfilled, parts[rowNumbers.get(i)]);
        }
    }

    /**
     * calculateAlternatives will find the rows that can add more parts to the 
     * currently fulfilled parts list
     * @param currentlyFulfilled
     * @return
     */
    private int[] calculateAlternatives(boolean[] currentlyFulfilled)
    {
        // copy the item table
        boolean[][] itemTableCopy = copyItemTable();
        
        // remove the filled columns for the itemTableCopy
        boolean[][] removedColumnsTable = removeFilledColumns(
                                            currentlyFulfilled, itemTableCopy);

        // printTableItem(removedColumnsTable);
        // return the list of rows numbers that can provide more parts
        return findAlternativeRows(removedColumnsTable);

    }

    /**
     * findAlternative rows returns an int array containing row numbers that 
     * have working parts in the removedColumnsTable
     * @param removedColumnsTable
     * @return
     */
    private int[] findAlternativeRows(boolean[][] removedColumnsTable)
    {
        // create a new arrayList to hold the row numbers
        ArrayList<Integer> rowList = new ArrayList<Integer>();
        // create an empty boolean array filled with false
        boolean [] emptyBooleanArray = new boolean[removedColumnsTable[0].length];
        for (int row = 0; row < itemTable.length; row ++)
        {
            // if a new part exists
            if (checkNewPart(emptyBooleanArray,removedColumnsTable[row]))
            {
                rowList.add(row); // add the row number to the rowList
            }
        }
        // return the int array representation of the rowList
        return convertToIntArray(rowList); 
    }
    
    private boolean[][] removeFilledColumns(boolean [] currentlyFulfilled, 
                                    boolean[][] itemTableCopy)
    {
        // find the true columns in the currently fulfilled boolean array
        int [] columns = findTrueColumns(currentlyFulfilled);

        // create newTable that that is the copy of the itemTavleCopt
        boolean[][] newTable = copyItemTable(itemTableCopy);

        if (columns.length > 0) // if true columns exist
        {
        // remove the true columns from the item table boolean array
        newTable = removeTrueColumns(itemTableCopy, columns);
        }

        return newTable; // return the new table

    }

    private int [] findTrueColumns(boolean [] currentlyFulfilled)
    {
        // make a new array list to hold the list of columns fulfilled
        ArrayList<Integer> columnList = new ArrayList<Integer>();
        for(int i = 0; i < currentlyFulfilled.length; i++)
        {
            // if this column has an entry that is true
            if(currentlyFulfilled[i] == true)
            {
                columnList.add(i); // add the column number to the columnList
            }
        }

        // return the array list
        return convertToIntArray(columnList);
    }

    private int [] convertToIntArray(ArrayList<Integer> columnList)
    {
        // get the size of the arrayList
        int [] intArray = new int[columnList.size()];

        // for each element in the intArray
        for (int i = 0; i < intArray.length; i++)
        {
            // set the i'th element of columnList to the ith element in the
            // int array
            intArray[i] = columnList.get(i); 
        }

        return intArray; // return the int array
    }

    /**
     * removeColumns removes the column of itemTableCopy
     * @param itemTableCopy
     * @param columnNumbers
     */
    private boolean[][] removeTrueColumns(boolean[][] itemTableCopy, 
                                int[]columnNumbers)
    {
        
        boolean[][] newItemTable = new boolean[itemTableCopy.length]
                            [itemTableCopy[0].length - columnNumbers.length];
        for (int i = 0; i < newItemTable.length; i++)
        {
            for (int j = 0, k = 0; j < itemTableCopy[0].length; j++)
            {
                // if the current column is not part of the true columns
                if (!containsArrayValue(j,columnNumbers))
                {
                    // copy the j+offset column entry of itemTableCopy
                    // to the j column entry of newItemTable
                    newItemTable[i][k] = itemTableCopy[i][j];
                    k++;
                }



            }

        }
        return newItemTable; // return the newItemTable 
    }

    private boolean containsArrayValue(int number, int[] intArray)
    {
        // for all values in the int array.
        for(int value : intArray)
        {
            // if an element in the intArray is equal to the number
            if (value == number)
            {
                return true; // return true
            }
        }

        return false;
    }
}

