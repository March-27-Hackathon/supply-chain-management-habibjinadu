package edu.ucalgary.ensf409;

import java.io.*;

public class OrderForm {
    // testing
    // public static void main(String[] args) {
    //     writeOrderForm();
    // }
    // public static String getType() {
    //     return "mesh";
    // }
    // public static String getCategory() {
    //     return "chair";
    // }
    // public static int getQuantity() {
    //     return 1;
    // }
    // public static String[] getItemsOrdered() {
    //     String[] arr = {"C9890", "C0942"};
    //     return arr;
    // }
    // public static int getTotalPrice() {
    //     return 150;
    // }


    /**
     Writes Order Form
    */
	public static void writeOrderForm() {
		String fileName = "orderform.text";
		try {
			FileWriter fileWriter = new FileWriter(fileName, false);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write("Furniture Order Form\n\nFaculty Name:\nContact:\nDate:\n\nOriginal Request: ");
            bufferedWriter.write(getType() + " " + getCategory() + ", " + String.valueOf(getQuantity())); // replace with actual function calls
            bufferedWriter.write("\n\nItems Ordered\n");
            for (int i = 0; i < getItemsOrdered().length; i++) { // replace with actual array and length call
                bufferedWriter.write("ID: " + getItemsOrdered()[i] + "\n"); // here too
            }
            bufferedWriter.write("\nTotal Price: $" + String.valueOf(getTotalPrice()) + "\n"); // and getTotalPrice
            bufferedWriter.close();
		}
		catch(IOException ex) {
			System.out.println("Error writing to file '" + fileName + "'");
		}
	}
}
