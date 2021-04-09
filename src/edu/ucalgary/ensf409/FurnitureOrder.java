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
        furnitureIDList = new LinkedList<>();
        manufacturerNameList = null;
        price = 0;
        CATEGORY = category;
        TYPE = type;
        NUMITEMS = numItems;
        fulfilled = false;
    }

    public LinkedList<String> getFurnitureIDList() {
        return furnitureIDList;
    }

    public LinkedList<String> getManufacturerNameList() {
        return manufacturerNameList;
    }

    public void setManufacturerNameList(LinkedList<String> list) {
        manufacturerNameList = list;
    }

    /**
     * Adds the given id to the list of furniture IDs.
     * @param id The ID to add to the list.
     */
    public void addID(String id) {
        furnitureIDList.add(id);
    }
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCATEGORY() {
        return CATEGORY;
    }

    public String getTYPE() {
        return TYPE;
    }

    public int getNUMITEMS() {
        return NUMITEMS;
    }

    public boolean isFulfilled() {
        return fulfilled;
    }

    /**
     * Marks the order as fulfilled.
     */
    public void setFulfilled() {
        fulfilled = true;
    }
}
