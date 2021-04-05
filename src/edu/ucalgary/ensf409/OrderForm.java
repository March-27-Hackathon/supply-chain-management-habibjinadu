package edu.ucalgary.ensf409;

import java.io.*;
import java.util.LinkedList;

public class OrderForm {

    /**
     Writes Order Form
    */
	public static void writeOrderForm(String type, String category, int quantity, LinkedList<String> furnitureIDList, int price) {
		String fileName = "orderform.text";
		try {
			FileWriter fileWriter = new FileWriter(fileName, false);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write("Furniture Order Form\n\nFaculty Name:\nContact:\nDate:\n\nOriginal Request: ");
            bufferedWriter.write(type.toLowerCase() + " " + category.toLowerCase() + ", " + String.valueOf(quantity));
            bufferedWriter.write("\n\nItems Ordered\n");
            for (int i = 0; i < furnitureIDList.size(); i++) {
                bufferedWriter.write("ID: " + furnitureIDList.get(i) + "\n");
            }
            bufferedWriter.write("\nTotal Price: $" + String.valueOf(price + "\n"));
            bufferedWriter.close();
		}
		catch(IOException ex) {
			System.out.println("Error writing to file '" + fileName + "'");
		}
	}
}
