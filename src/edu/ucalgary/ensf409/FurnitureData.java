package edu.ucalgary.ensf409;

import java.sql.*;
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
     * removeFurniture removes the furniture with the specified id, from the
     * table specified by the category argument
     * @param id
     * @param category
     */
    public void removeFurniture(String id, String category)
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
    public Connection getDatabaseConnection()
    {
        return this.databaseConnection; // return the database connection
    }
       

}
