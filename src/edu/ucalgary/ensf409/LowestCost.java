package edu.ucalgary.ensf409;

import java.sql.*;

/**
 * Uses a connection to a database to calculate the lowest cost of creating a furniture item, given the desired furniture
 * item and its type.
 */
public class LowestCost {
    private Connection dbConnect;
    private String furnitureType;
    private int numberOfItems;

    public LowestCost(Connection dbConnect, String fType, int numItems) {
        this.dbConnect = dbConnect;
        this.furnitureType = fType;
        this.numberOfItems = numItems;
    }
    
}
