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
    
    public Connection getDatabaseConnection()
    {
        return this.databaseConnection; // return the database connection
    }
       

}
