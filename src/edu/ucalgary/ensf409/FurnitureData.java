package edu.ucalgary.ensf409;

import java.sql.*;
import java.util.LinkedList;
public class FurnitureData {

    public final String DBURL; //store the database url information
    public final String USERNAME; //store the user's account username
    public final String PASSWORD; //store the user's account password

    public Connection databaseConnection; // variable to store the database 
    // connection

    public FurnitureData(String url, String userName, String password)
    {
        this.DBURL = url; // initialize the database url with the url argument
        // initialize USERNAME with the USERNAME argument
        this.USERNAME = userName; 
        // initialize PASSWORD with the password argument
        this.PASSWORD = password;
    }

    /**
     * creates a connection with the database
     */
    public void initializeConnection()
    {
        try 
        {
            // connect to the database
            this.databaseConnection = DriverManager.getConnection(this.DBURL, 
                                                this. USERNAME,this.PASSWORD);
        }
        catch(SQLException e)
        {
            // print error message
            System.out.println("Cannot connect to the database");
            e.printStackTrace(); // print stack trace
        }
        
    }

    /**
     * removeOrderFromDatabase removes each id specified by ids, from the table
     * specified by category in the inventory database
     * @param ids list of ids to be removed
     * @param category the table where the ids will be removed
     */
    public void removeOrderFromDatabase(LinkedList<String> ids, String category)
    {
        // for each individual id in the list of furniture ids
        for (String id: ids)
        {
            // remove the furniture from the database
            removeFurniture(id, category); 
        }
    }
    /**
     * removeFurniture removes the furniture with the specified id, from the
     * table specified by the category argument
     * @param id id of the row to be removed
     * @param category table where the id will be removed
     */
    private void removeFurniture(String id, String category)
    {
        // make the query string to delete an id from the table specified by
        // category
        String queryString = "DELETE FROM " + category + " WHERE ID=?";
        // alter the database with the following queryString and id
        alterDatabase(queryString, id);
    }

    /**
     * alters the database as specified by the prepared Statement query
     * @param queryString queryString contaning the SQL statement with input
     * parameters
     * @param id parameter to be used as the 1st parameter in the query string
     */
    private void alterDatabase(String queryString, String id)
    {   
        // use try-with-resources to auto-close the resources after use
        try
        (   
            // create a prepared statement the the string query
            PreparedStatement customStatement = 
            databaseConnection.prepareStatement(queryString)
        )
        {
            // set the first question mark as ID
            customStatement.setString(1, id); 


            customStatement.executeUpdate(); // execute the update
        }
        catch (SQLException e)
        {
            // print a message
            System.out.println("Could not remove from the database");
            e.printStackTrace(); // print stack trace
        }
    }
    /**
     * returns the Connection object that connects to the inventory database
     * @return a Connection object that is connected to the database
     */
    public Connection getDatabaseConnection()
    {
        return this.databaseConnection;
        // return the database connection
    }
    
    /**
     * closeConnection closes the connection to the inventory database.
     */
    public void closeConnection()
    {
        try
        {        // close the database
        databaseConnection.close();
        }
        catch (SQLException e)
        {
            // print a message
            System.out.println("Could not close the database");
            e.printStackTrace(); // print stack trace
        }
    }
}
