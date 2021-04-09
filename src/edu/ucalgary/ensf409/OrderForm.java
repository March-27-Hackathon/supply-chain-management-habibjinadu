/**
 * @author Habib Jinadu <a href = "mailto:habib.jinadu@ucalgary.ca">
 * habib.jinadu@ucalgary.ca</a>
 * @author Cheyenne Goh <a href = "cheyenne.goh1@ucalgary.ca">
 * cheyenne.goh1@ucalgary.ca</a>
 * @author Carter Fuchs <a href = "mailto:carter.fuchs@ucalgary.ca">
 * carter.fuchs@ucalgary.ca</a>
 * @author Logan Jones  <a href = "mailto:logan.jones1@ucalgary.ca">
 * logan.jones1@ucalgary.ca</a>
 * @version     1.8
 * @since       1.0
 */
package edu.ucalgary.ensf409;

import java.io.*;
import java.util.LinkedList;

public class OrderForm {

	/**
     * Receives information and writes an order form with file name
	 * "orderform.text" in the root directory
     * @param type furniture type as a String
	 * @param category furniture category as a String
	 * @param quantity number of items requested as an int
	 * @param furnitureIDList LinkedList containing IDs of requested furniture
	 *                        items
	 * @param price calculated total price as an int
     */
	public static void writeOrderForm(String type, String category,
									  int quantity,
									  LinkedList<String> furnitureIDList,
									  int price) {
		String fileName = "orderform.txt";
		try {
			FileWriter fileWriter = new FileWriter(fileName, false);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write("Furniture Order Form\n\nFaculty Name:" +
					"\nContact:\nDate:\n\nOriginal Request: ");
            bufferedWriter.write(type.toLowerCase() + " " +
					category.toLowerCase() + ", " + String.valueOf(quantity));
            bufferedWriter.write("\n\nItems Ordered\n");
            for (int i = 0; i < furnitureIDList.size(); i++) {
                bufferedWriter.write("ID: " + furnitureIDList.get(i) + "\n");
            }
            bufferedWriter.write("\nTotal Price: $" + String.valueOf(price
					+ "\n"));
            bufferedWriter.close();
		}
		catch(IOException ex) {
			System.out.println("Error writing to file '" + fileName + "'");
		}
	}
}
