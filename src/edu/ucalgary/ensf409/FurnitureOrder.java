/**
 * @author Habib Jinadu <a href = "mailto:habib.jinadu@ucalgary.ca">
 * habib.jinadu@ucalgary.ca</a>
 * @author Cheyenne Goh <a href = "mailto:cheyenne.goh1@ucalgary.ca">
 * cheyenne.goh1@ucalgary.ca</a>
 * @author Carter Fuchs <a href = "mailto:carter.fuchs@ucalgary.ca">
 * carter.fuchs@ucalgary.ca</a>
 * @author Logan Jones  <a href = "mailto:logan.jones1@ucalgary.ca">
 * logan.jones1@ucalgary.ca</a>
 * @version     1.8
 * @since       1.0
 */
package edu.ucalgary.ensf409;
import java.util.LinkedList;

/**
 * The purpose of this class is to collect all the data for the furniture order
 * in one place, so that it can later be used by the OrderForm class. This class
 * doesn't use the database connection in any way.
 */
public class FurnitureOrder {
    private LinkedList<String> furnitureIDList; //stores IDs of all of the
    // selected furniture items (in no particular order)
    private LinkedList<String> manufacturerNameList; //stores the IDs of
    // suggested manufacturers, in the event that there
    // isn't enough furniture in the database to fulfill the order. If there is,
    // this field will be null.
    private int price;
    private final String CATEGORY; //eg, chair, desk, filing, lamp
    private final String TYPE; //eg, mesh, ergonimic, executive, kneeling,
    // small, ...
    private final int NUMITEMS; //the number of items to be built
    private boolean fulfilled; //whether the order can be fulfilled or not: true
    // for yes, false for no

    public FurnitureOrder(String category, String type, int numItems) {
        furnitureIDList = new LinkedList<>(); // make new linkedList to hold ids
        manufacturerNameList = null; // set the manufacturer name list to null
        price = 0; // set the price of the order to zero
        CATEGORY = category; // assign the category argument to CATEGORY
        TYPE = type; // assign the type argument to TYPE
        NUMITEMS = numItems; // assign numItems to NUMITEMS
        fulfilled = false; // set fulfilled to false
    }

    /**
     * Get the furnitureIDList data member
     * @return a linkedList that contains the id of the furniture list as a 
     * string
     */
    public LinkedList<String> getFurnitureIDList() {
        return furnitureIDList; // return the furnitureID list data member
    }

    /**
     * get the manufacturerNameList
     * @return
     */
    public LinkedList<String> getManufacturerNameList() {
        // return the manufacturerNameList data member
        return manufacturerNameList; 
    }

    /**
     * set the ManufacturerNameList 
     * @param list LinkedList of strings that contain the manufacturer names 
     * for the order
     */
    public void setManufacturerNameList(LinkedList<String> list) {
        // assign the manufacturerNameList data member to the list LinkedList
        manufacturerNameList = list; 
    }

    /**
     * Adds the given id to the list of furniture IDs.
     * @param id The ID to add to the list.
     */
    public void addID(String id) {
        furnitureIDList.add(id); // add the id to the furnitureIDList
    }

    /**
     * get the price data member
     * @return an int representing the price of the furniture order. It will be
     * 0 is there are no items in the furniture order
     */
    public int getPrice() {
        return price; // return the price data member
    }

    /**
     * set the price data member in the furnitureOrder
     * @param price int representing the price of the items in the 
     * furnitureIDList
     */
    public void setPrice(int price) {
        // assign the price data member to the price argument
        this.price = price; 
    }

    /**
     * get the CATEGORY data member
     * @return a String representing the category of the items that are stored
     * in the FurnitureOrder
     */
    public String getCATEGORY() {
        return CATEGORY; // return the CATEGORY data member
    }

    /**
     * get the TYPE data member
     * @return a String representing the type of items that are stored
     * in the FurnitureOrder
     */
    public String getTYPE() {
        return TYPE; // return the TYPE data member
    }

    /**
     * get the NUMITEMS data member
     * @return an int representing the number of items that are stored
     * in the FurnitureOrder
     */
    public int getNUMITEMS() {
        return NUMITEMS; // return the NUMITEMS data member
    }

    /**
     * checks to see if the order has been fulfilled.
     * @return a boolean representing of the items in the furniture list is the
     * lowest cost combination of parts that will fulfill the client order
     */
    public boolean isFulfilled() {
        return fulfilled; // return the fulfilled data member
    }

    /**
     * Marks the order as fulfilled.
     */
    public void setFulfilled() {
        fulfilled = true; // ser fulfilled to true
    }
}
