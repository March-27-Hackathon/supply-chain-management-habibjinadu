package edu.ucalgary.ensf409;

import java.io.*;

public class OrderForm {

    /**
     Writes Order Form
    */
	public static void writeOrderForm(String type, String category, int quantity, String[] itemsOrdered, int totalPrice) {
		String fileName = "orderform.text";
		try {
			FileWriter fileWriter = new FileWriter(fileName, false);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write("Furniture Order Form\n\nFaculty Name:\nContact:\nDate:\n\nOriginal Request: ");
            bufferedWriter.write(type + " " + category + ", " + String.valueOf(quantity));
            bufferedWriter.write("\n\nItems Ordered\n");
            for (int i = 0; i < itemsOrdered.length; i++) {
                bufferedWriter.write("ID: " + itemsOrdered[i] + "\n");
            }
            bufferedWriter.write("\nTotal Price: $" + String.valueOf(totalPrice + "\n"));
            bufferedWriter.close();
		}
		catch(IOException ex) {
			System.out.println("Error writing to file '" + fileName + "'");
		}
	}
}
