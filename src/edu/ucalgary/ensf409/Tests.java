/**
@author Logan Jones <a href="mailto:logan.jones1@ucalgary.ca">
	Logan.Jones1@ucalgary.ca</a>
@version	1.0
@since		1.0
 */

package edu.ucalgary.ensf409;

import static org.junit.Assert.*;
import org.junit.*;
import java.sql.*;

public class Tests {
	public static Connection dbConnection;
	
	public static void initializeConnection() {
		try {
		 Tests.dbConnection = DriverManager.getConnection("jdbc:mysql://localhost/inventory","Jones","ensf409");
		} 
		catch(SQLException e) {
			System.out.println("Cannot connect to the database");
            e.printStackTrace();
            System.exit(1);
		}
	}
	
	@Test
	public void testFindBestCombinationMeshChair() {
		Tests.initializeConnection();
		LowestCost test = new LowestCost(Tests.dbConnection, "Chair", "Mesh", 1);
		FurnitureOrder testResult = test.findBestCombination();
		
		assertEquals("Failure on LowestCost with 1 mesh chair", 150, testResult.getPrice());
	}
	
	@Test
	public void testFindBestCombinationTaskChair() {
		Tests.initializeConnection();
		LowestCost test = new LowestCost(Tests.dbConnection, "Chair", "Task", 1);
		FurnitureOrder testResult = test.findBestCombination();
		
		assertEquals("Failure on LowestCost with 1 task chair", 150, testResult.getPrice());
	}
	
	@Test
	public void testFindBestCombinationKneelingChair() {
		Tests.initializeConnection();
		LowestCost test = new LowestCost(Tests.dbConnection, "Chair", "Kneeling", 1);
		FurnitureOrder testResult = test.findBestCombination();
		
		assertEquals("Failure on LowestCost with 1 task chair", 0, testResult.getPrice());
	}
	
}
