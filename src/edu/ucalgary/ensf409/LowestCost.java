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

            //printTableItem();

            ArrayList<Integer> furnitureOrderList = makeOrderList();

            
            LinkedList<String> furnitureIds = new LinkedList<>();

            // for each row in the furniture orderList
            for (Integer row: furnitureOrderList)
            {
                // add the order ID to the furnitureOrder
                order.addID(getRowId(row.intValue()+1));
            }
            // set the price of the order
            order.setPrice(calculatePrice(furnitureOrderList)); 
            // if there are orders in the funiture order list
            if (!(furnitureOrderList.size() == 0))
            {
                order.setFulfilled(); // set the fulfilled flag to true
            }else
            {
                // else get the list of manufacturers that can provide this type
                // and store it in the order variable.
                order.setManufacturerNameList(
                            generateManufacturerNames(this.furnitureCategory));
                
            }

            
            

            
            
        } catch (SQLException e) {
            System.err.println("An SQLException occurred while selecting from "
                    + furnitureCategory + " with the Type " + furnitureType);
            e.printStackTrace();
        }
        return order;
    }

    /**
     * Make list string creates a formatted string using a list of ids.
     * The formatted string is in the form ('id1', 'id2', ...)
     * @return
     */
    private String makeListString(LinkedList<String> ids)
    {
        // create the string builder
        StringBuilder formattedString = new StringBuilder();
        formattedString.append("("); // append the opening bracket
        
        for (String id: ids)
        {
            formattedString.append("'"); // append an apostrophe
            formattedString.append(id); // append the id in the string
            formattedString.append("'"); // append the closing apostrophe
            formattedString.append(","); // append the comma
        }

        // delete the last comma
        formattedString.deleteCharAt(formattedString.length()-1);
        formattedString.append(")"); // append the closing bracket

        return formattedString.toString(); // return the formatted string
    }
    /**
     * generateManufactureNames returns a list of the manufacturer names for
     * from the Manufacturer table 
     * @param category
     * @return
     */
    private LinkedList<String> generateManufacturerNames(String category)
    {
        // get the manufacturer ids from the category table
        LinkedList<String> manufacturerIds = 
                                  generateManufacturerIDList(category);
        
        // make the linked list that will store the manufacturer names
        LinkedList<String> manufacturerNames = new LinkedList<String>();
        // make the list of IDs
        String listOfIds = makeListString(manufacturerIds);
        // make the queryString
        String query = "SELECT * FROM manufacturer WHERE ManuID IN " + listOfIds;
        // make the statement and the query database
        try (Statement queryStatement = this.dbConnect.createStatement();
            ResultSet queryResult = queryStatement.executeQuery(query))
        {
            while (queryResult.next()) // while there are still rows
            {
                // add the name of the maufacturer
                manufacturerNames.add(queryResult.getString("Name"));
            }
        }
        catch (SQLException e)
        {
            // print a message
            System.out.println("Could not query the manufacturer database");
            e.printStackTrace(); // print stack trace
        }

        return manufacturerNames; // return the manufacturer names
    }

    /**
     * generateManufacturerIDList returns a list of the different manufacturer
     * Ids for the specific category
     * @param category
     * @return
     */
    private LinkedList<String> generateManufacturerIDList(String category)
    {
        LinkedList<String> manufacturerIds = new LinkedList<String>();
        // make the query string to retrieve the Manufacturer IDs from the 
        // table specified by Category
        String query = "SELECT DISTINCT ManuID FROM " + category;
        // make a statement and query the database
        try(Statement queryStatement = this.dbConnect.createStatement();
            ResultSet queryResult = queryStatement.executeQuery(query))
        {
            // while there are still rows left in the result
            while(queryResult.next()) 
            {
                // add the manufacturer ids to the linkedlist
                manufacturerIds.add(queryResult.getString("ManuID"));
            }
        }
        catch (SQLException e)
        {
            // print a message
            System.out.println("Could not query the database");
            e.printStackTrace(); // print stack trace
        }

        return manufacturerIds; // return the manufacturer Ids
    }

    /**
     * make orderList returns an integer array list containing the lowest cost
     * furniture combinations for the client order
     * @return
     */
    private ArrayList<Integer> makeOrderList() throws SQLException
    {
        // make an integer array to hold the row numbers of lowest cost order
        ArrayList<Integer> furnitureOrderList = new ArrayList<Integer>();

            // find the lowest cost furniture one item in the client order
            // and assign it to lowestCombo
            ArrayList<Integer> lowestCombo = findLowestCost();
            // if the lowestCombo array list is empty
            if (lowestCombo.size() == 0)
            {
                return new ArrayList<Integer>(0); // return an empty arrayList
            }
            // add the row numbers from the lowestCombo to the furniture
            // order list
            furnitureOrderList.addAll(lowestCombo);
        
        return furnitureOrderList; // return the order list
    }

    /**
     * trimItemTable trims the columns that already we already have extra parts
     * for in the current order
     * @param unusedParts
     */
    private void trimItemTable (ArrayList<Integer> unusedParts)
    {
        // create a new array list to hold the column numbers that have extra 
        // parts
        ArrayList<Integer> columnNumbers = new ArrayList<Integer>();

        // for each element in the unusedParts List
        for (int i = 0; i < unusedParts.size(); i++)
        {
            // if the column at least 1 extra part
            if (unusedParts.get(i).intValue() > 0)
            {
                // add that column to the columnNumbers array
                columnNumbers.add(Integer.valueOf(i));
            }
        }

        // remove the unnecessary columns in the itemTable data member
        removeTrueColumns(columnNumbers); 
    }
    
    /**
     * removeTrueColumns removes the columns in the item table that already
     * have extra parts
     * @param columnNumbers
     */
    private void removeTrueColumns(ArrayList<Integer> columnNumbers)
    {
        
        boolean[][] newItemTable = new boolean[this.itemTable.length]
                            [this.itemTable[0].length - columnNumbers.size()];
        for (int i = 0; i < newItemTable.length; i++)
        {
            for (int j = 0, k = 0; j < this.itemTable[0].length; j++)
            {
                // if the current column is not part of the true columns
                if (!columnNumbers.contains(Integer.valueOf(j)))
                {
                    // copy the j+offset column entry of this.itemTable
                    // to the j column entry of newItemTable
                    newItemTable[i][k] = this.itemTable[i][j];
                    k++;
                }

            }

        }
        // set the itemTable data member to the newItemTable
        this.itemTable = newItemTable; 
    }

    /**
     * deactivateFurnitureInventory modifies the itemTable data member by 
     * setting all the parts that are currently used for an order to false in
     * the orderList
     * @param orderList
     */
    private void deactivateFurnitureInInventory (ArrayList<Integer> orderList)
    {
        // for each row in the itemTable
        for (int row = 0; row < this.itemTable.length; row++)
        {
            // if this row number is current in the order list
            if (orderList.contains(Integer.valueOf(row)))
            {
                // fill this row with false
                Arrays.fill(this.itemTable[row], false);
            }
        }
    }
    /**
     * findUnusedParts looks for any parts extra parts in the furniture order 
     * list
     * @param furnitureOrderList
     * @return
     */
    private ArrayList<Integer>findUnusedParts(
                                        ArrayList<Integer> furnitureOrderList,
                                        int numberOfItems)
    {
        // instantiate the array list that will hold the amount of unused parts
        // in each column
        ArrayList<Integer> unusedParts =  
                            new ArrayList<Integer>(this.itemTable[0].length);

        // for each column in the itemTable
        for (int column = 0; column < this.itemTable[0].length; column++)
        {
             // set the initial value in the column to zero
            unusedParts.add(0);

            // for each row in the itemTable
            for (int row = 0; row < this.itemTable.length; row++)
            {
                
                if (this.itemTable[row][column] == true && 
                furnitureOrderList.contains(Integer.valueOf(row)))
                {
                    // get the currentAmount of parts in this column
                    int currentAmount = unusedParts.get(column);

                    
                    // increment the currentAmount in this column by 1
                    unusedParts.set(column, currentAmount + 1);
                }
            }
            // get the final amount of parts in the column
            int finalAmount = unusedParts.get(column);
            // subtract the final amount in the column by the number of
            // furniture that is currently needed
            unusedParts.set(column, finalAmount - numberOfItems);
        }

        return unusedParts;
    }

    /**
     * findLowestCost looks for the lowest combination for furniture to build 
     * one complete furniture. 
     */
    private ArrayList<Integer> findLowestCost() throws SQLException
    {
        ArrayList<ArrayList<Integer>> potentialCombos = filterCombos(generateAllCombos());
        // if there are no potential combinations
        if (potentialCombos.size() == 0)
        {
            return new ArrayList<Integer>(0); // return an arrayList with a size of zero
        }

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
        // if(lowestCombo != null) {
        //     System.out.println("FINAL PRICE: " + finalPrice);
        //     System.out.println("ROWS USED:");
        //     for (int row : lowestCombo) {
        //         System.out.println(row + 1); //starts at 1
        //     }
        // } else {
        //     System.out.println("No combinations exist");
        // }

        return lowestCombo; // return the lowest combo

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

    /**
     * get rowId gets the id of a row
     * @param rowIndex
     * @return
     * @throws SQLException
     */
    private String getRowId(int rowIndex) throws SQLException
    {
        //rowIndex starts at 1
        int savedRow = results.getRow();
        results.absolute(rowIndex);
        String result = results.getString("ID");
        results.absolute(savedRow);
        return result;
    }

    /*
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
     */

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

    /**
     * Converts the Y and N characters from a row in the database to an array of true and false boolean values, where Y
     * is converted to true and N is converted to false.
     * @param parts An array to be filled with true/false values.
     * @throws SQLException if results encountered a problem while trying to read values from the database.
     */
    private void fill(boolean[] parts) throws SQLException{
        int numberOfParts = getNumberOfParts(results);
        for(int i = 3; i < numberOfParts + 3 ; i++) {
            if (results.getString(i).equals("Y")) {
                parts[i-3] = true;
            }
        }
    }

    /**
     * Sets each false element in array a to true, if the same element in array b is true. In other words, it does
     * (a element) || (b element) for each element of a.
     * @param a The destination array
     * @param b The source array
     */
    private static void addArrays(boolean[] a, boolean[] b) {
        for(int i = 0; i < a.length; i++) {
            if(!a[i]) {
                a[i] = b[i];
            }
        }
    }

    /**
     * Returns true if each element of src is true
     * @param src The source array
     * @return Whether or not each element of src is true
     */
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
     * Checks if the current combination of furniture represented by combo
     * has all the parts for the amount of items that the client has ordered
     * 
     * @param combo The combination of furniture items
     * @return whether or not the combination has all the parts needed for 
     * the requested amount of furniture
     */
    private boolean isValidCombo(ArrayList<Integer> combo) {
        // calculate the amount of unused parts in each column
        ArrayList<Integer> unusedParts = findUnusedParts(combo,
                                                            this.numberOfItems);
        int counter = 0; // set a counter to zero
        // for each column get the amount of unused parts
        for (Integer unusedPart:unusedParts)
        {
            // if there are zero or more unused parts in this column
            if (unusedPart >= 0) 
            {
                counter++; // increment the counter
            }
        }

        // return true if we have the required number of parts for all items 
        // in the order
        return counter == this.itemTable[0].length; 
    }

    /**
     * Uses the resultSet to get the price of a combination of furniture items
     * @param combo A combination of furniture items
     * @return The price of all furniture items in the combination
     * @throws SQLException if the resultSet is unable to get the price of one of the rows
     */
    private int calculatePrice(ArrayList<Integer> combo) throws SQLException{
        int price = 0;
        int savedIndex = results.getRow(); //saves the current index of results
        for(int rowIndex : combo) {
            results.absolute(rowIndex+1); //point results towards the row at rowIndex (the +1 is because the first
            // resultSet row is at index 1)
            price += results.getInt("Price"); //add the price of the row to the total price
        }
        results.absolute(savedIndex); //goes back to the saved index of results
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
        //creates the ArrayList to return
        for (ArrayList<Integer> current : allCombos) {
            //calls isValidCombo for each element to determine if it should be added to result
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

