package edu.ucalgary.ensf409;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * BestOrder stores a list of furniture and the total price of entire furniture
 * set
 */
public class BestOrder
{
    private ArrayList<String> furnitureIds;
    private ArrayList<Integer> furnitureRowNumbers;
    private int finalPrice; 
    private ArrayList<ArrayList<Integer>>unusedParts;

    public BestOrder(ArrayList<Integer> furnitureRowNumbers, 
                    ArrayList<String> furnitureIds, int price)
    {
        // set the furnitureIds argument to the furnitureIds data member
        this.furnitureIds = furnitureIds;  

        // set the furnitureRowNumbers to the furnitureRowNumbers data member
        this.furnitureRowNumbers = furnitureRowNumbers;

        // set the furniturePrice to the final price data member
        this.finalPrice = price;

    }

    public BestOrder ()
    {
        // instantiate the furnitureIDs array list
        this.furnitureIds = new ArrayList<String>();
        
        // instantiate the furnitureRowNumbers array list
        this.furnitureRowNumbers = new ArrayList<Integer>();
        
        // instantiate the final price to 0
        this.finalPrice = 0;

        // instantiate the size of the unused parts array
        this.unusedParts = new ArrayList<ArrayList<Integer>>();

    }

    public void addNewFurniture(ResultSet results, int rowNumber) throws SQLException
    {

        results.absolute(rowNumber+1); // move the ResultSet object to the row of
                                    // interest
        String id = results.getString("ID"); // get the ID number

        furnitureIds.add(id); // add this id to the furnitureId list

        // add this row number to the rowNumber list
        furnitureRowNumbers.add(rowNumber); 

        // get the price of the furniture
        int furniturePrice = results.getInt("Price");
        
        // add the price of the furniture to the final price
        finalPrice += furniturePrice; 
        
        // return the results cursor to before the first row
        results.beforeFirst(); 
    }

    public  void deepCopy(BestOrder toBeCopied)
    {
        BestOrder copy = new BestOrder();

        // copy the furniture ids and assign it to the copy
        this.setFurnitureId (toBeCopied.copyFurnitureId());
        // copy the furniture rowNumbers and assign it to the copy
        this.setFurnitureRowNmbers(toBeCopied.copyFurnitureRowNumber());

        // get the final price and set it to the copy
        this.setFinalPrice(toBeCopied.getFinalPrice());

    }

    public int getFinalPrice()
    {
        return this.finalPrice; // return the final price data member
    }

    public ArrayList<String> copyFurnitureId()
    {
        // create a nre array list that hold strings
        ArrayList<String> newIds = new ArrayList<String>();
        // create a string iterator that holds strings
        Iterator<String> stringIterator = this.furnitureIds.iterator();

        // while there are still strings in the list
        while (stringIterator.hasNext()) 
        {
            // make a new string
            String idCopy =new String(stringIterator.next()); 

            newIds.add(idCopy); // add the copy to the new string
        }

        return newIds; // return the newly created id array list
    }

    public ArrayList<Integer> copyFurnitureRowNumber()
    {
        // // conver the array list to an Integer array
        // Integer[] rowNumberArray = this.furnitureRowNumbers.toArray();
        
        // create a new row number array list
        ArrayList<Integer> newRowNumbers = new ArrayList<Integer>();

        // create an integer iterator
        Iterator<Integer> integerIterator = this.furnitureRowNumbers.iterator();

        // while there are still integers in the list
        while (integerIterator.hasNext())
        {
            // create a new integer
            int intCopy = integerIterator.next().intValue();
            newRowNumbers.add(intCopy); // add the copy to the new integer array
        }

        return newRowNumbers; // return the rowNumber array list
    }




    public void setFurnitureId(ArrayList<String> furnitureIds)
    {
        // set the furnitureIds argument to the furnitureIds data member
        this.furnitureIds = furnitureIds;
    }

    public void setFurnitureRowNmbers (ArrayList<Integer> furnitureRowNumbers)
    {
        // set the furnitureRowNumbers argument to the furnitureIds data member
        this.furnitureRowNumbers = furnitureRowNumbers;
    }

    public void setUnusedParts(ArrayList<ArrayList<Integer>> unusedParts)
    {
        // set the unusedParts argument to the unusedParts datamember 
        this.unusedParts = unusedParts;
    }

    public void setFinalPrice(int price)
    {
        // assign the price argument to the final price of the data member
        this.finalPrice = price; 
    }

    public void removeFurniture(ResultSet results, int rowNumber)
                                                    throws SQLException
    {   
        // get the size of the rowNumberList
        int sizeOfRowNumberList = this.furnitureRowNumbers.size();

        // decrease by 1 to get the last row index
        int lastRowIndex = sizeOfRowNumberList - 1; 
        // remove the last id in the list
        this.furnitureIds.remove(this.furnitureIds.size() - 1);

        // remove the last row number in the list
        this.furnitureRowNumbers.remove(lastRowIndex);

        results.absolute(rowNumber+1); // navigate to the row in rhe results table

        // get the price of the furniture
        int furniturePrice = results.getInt("Price");

        // update the final price of the furniture list
        this.finalPrice -= furniturePrice; 

        results.beforeFirst(); // reset the ResultSet cursor
    }

    public ArrayList<Integer> getFurnitureRowNumbers()
    {
        return this.furnitureRowNumbers; // return the furniture row numbers
    }

    public ArrayList<String> getFurnitureIds()
    {
        return this.furnitureIds; // return the furniture ids
    }



    


    
}