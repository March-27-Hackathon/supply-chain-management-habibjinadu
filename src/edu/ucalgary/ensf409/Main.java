package edu.ucalgary.ensf409;
import java.sql.*;
import java.util.Arrays;
import java.util.LinkedList;
import javax.swing.*;

public class Main {
    // List of furniture types of each category
    private final static String[] CHAIR_TYPES = {"Ergonomic", "Executive", "Kneeling", "Mesh", "Task"};
    private final static String[] DESK_TYPES = {"Adjustable", "Standing", "Traditional"};
    private final static String[] FILING_TYPES = {"Small", "Medium", "Large"};
    private final static String[] LAMP_TYPES = {"Desk", "Study", "Swing Arm"};

    // Header extension for GUI
    private final static String HEADER_EXT = " - Faculty FrankenFurniture Finder";

    // Image assets for GUI
    private final static ImageIcon icon = new ImageIcon("frankenchair.png");
    private final static ImageIcon errorIcon = new ImageIcon("frankenchairerror.png");

    // Member variables
    private String category = null;
    private String type = null;
    private int quantity = Integer.MIN_VALUE;

    public static void main(String[] args) {
        Main main = new Main(); // instantiates Main object
        boolean gui = false; // flag for either GUI or console interface

        if (args.length < 3) {
            gui = true; // sets GUI flag to true
            main.graphicalUserInterfaceInput(); // either launches GUI
        }
        else {
            main.commandLineInput(args); // or accepts command line arguments
        }        

        //The order form should be created and used here, using orderResult to get all the needed information
        // // Habib's Test Code
        FurnitureData database = new FurnitureData("jdbc:mysql://localhost/inventory","carter","3nsf409*");

        database.initializeConnection(); // initialize the connection

        Connection databaseConnection = database.getDatabaseConnection();

        //Carter's test code
        LowestCost calculation = new LowestCost(databaseConnection, main.category, main.type, main.quantity);
        FurnitureOrder orderResult = calculation.findBestCombination(); //findBestCombination will create and return an
        //order with all of the relevant information

        // remove all of the ordered furniture in the inventory database
        database.removeOrderFromDatabase(orderResult.getFurnitureIDList(),
                                         orderResult.getCATEGORY());
        
        if (orderResult.isFulfilled()) {
            if (gui) {
                graphicalUserInterfaceOutput(orderResult.getFurnitureIDList(), orderResult.getPrice()); // Outputs order details to GUI
            }
            else {
                commandLineOutput(orderResult.getFurnitureIDList(), orderResult.getPrice()); // Outputs order details to console
            }
            
            // Creates order form using orderResult to get all the needed information
            OrderForm.writeOrderForm(orderResult.getTYPE(), orderResult.getCATEGORY(), orderResult.getNUMITEMS(), orderResult.getFurnitureIDList(), orderResult.getPrice());
        }
        else {
            if (gui) {
                graphicalUserInterfaceOutput(orderResult.getManufacturerNameList()); // Outputs manufacturers to GUI
            }
            else {
                commandLineOutput(orderResult.getManufacturerNameList()); // Outputs manufacturers to console
            }
        }
    }

    // JOptionPane Interface

	/**
     * Receives input via a JOptionPane GUI
     */
    public void graphicalUserInterfaceInput() {
        try {
            String[] categories = {"Chair", "Desk", "Filing", "Lamp"}; // String array containing categories
            this.category = (String)JOptionPane.showInputDialog(null, "I am looking for (a)...", "Category" + HEADER_EXT, JOptionPane.INFORMATION_MESSAGE, icon, categories, categories[0]);
            if (this.category.equals("Chair")) { // Chair is selected
                this.type = (String)JOptionPane.showInputDialog(null, "Select a type of chair:", "Type (Chair)" + HEADER_EXT, JOptionPane.INFORMATION_MESSAGE, icon, CHAIR_TYPES, CHAIR_TYPES[0]);
            }
            else if (this.category.equals("Desk")) { // Desk is selected
                this.type = (String)JOptionPane.showInputDialog(null, "Select a type of desk:", "Type (Desk)" + HEADER_EXT, JOptionPane.INFORMATION_MESSAGE, icon, DESK_TYPES, DESK_TYPES[0]);
            }
            else if (this.category.equals("Filing")) { // Filing is selected
                this.type = (String)JOptionPane.showInputDialog(null, "Select a type of filing:", "Type (FilingO" + HEADER_EXT, JOptionPane.INFORMATION_MESSAGE, icon, FILING_TYPES, FILING_TYPES[0]);
            }
            else { // Lamp is selected
                this.type = (String)JOptionPane.showInputDialog(null, "Select a type of lamp:", "Type (Lamp)" + HEADER_EXT, JOptionPane.INFORMATION_MESSAGE, icon, LAMP_TYPES, LAMP_TYPES[0]);
            }
        }
        catch (NullPointerException e) {
            System.exit(0); // Exits if user clicks "Cancel" button
        }
        if (this.type == null){
            System.exit(0); // Exits if user clicks "Cancel" button
        }
        String quantityString = "Input";
        while (this.quantity < 1) {
            quantityString = (String)JOptionPane.showInputDialog(null, "How many would you like?", "Quantity" + HEADER_EXT, JOptionPane.INFORMATION_MESSAGE, icon, null, "1");
            if (quantityString == null) {
                System.exit(1); // Exits if user clicks "Cancel" button
            }
            try {
                this.quantity = Integer.parseUnsignedInt(quantityString); // parses inputted String to int
            }
            catch (NumberFormatException e){
                if (quantityString.length() == 0) {
                    // Error handling for no input
                    JOptionPane.showMessageDialog(null, "At least one item must be requested.", "Error" + HEADER_EXT, JOptionPane.ERROR_MESSAGE, errorIcon);
                }
                else {
                    // Error handling for invalid number
                    JOptionPane.showMessageDialog(null, quantityString + " is not a valid number of items.", "Error" + HEADER_EXT, JOptionPane.ERROR_MESSAGE, errorIcon);
                }
            }
            if (quantity == 0) {
                // Error handling for input of 0
                JOptionPane.showMessageDialog(null, "At least one item must be requested.", "Error" + HEADER_EXT, JOptionPane.ERROR_MESSAGE, errorIcon);
            }
        }
    }

    /**
     * Outputs order information for a fulfilled order via a JOptionPane GUI
     */
    public static void graphicalUserInterfaceOutput(LinkedList<String> furnitureIDList, int price) {
        String message = "";
        message = message + "Purchase ";
        if (furnitureIDList.size() == 1) {
            message = message + furnitureIDList.get(0) + " for $" + price + ".";
        }
        else if (furnitureIDList.size() == 2) {
            message = message + furnitureIDList.get(0) + " and "  + furnitureIDList.get(1) + " for $" + price + ".";
        }
        else if (furnitureIDList.size() > 2) {
            for (int i = 0; i < furnitureIDList.size() - 1; i++) {
                message = message + furnitureIDList.get(i) + ", ";
            }
            message = message + "and "  + furnitureIDList.get(furnitureIDList.size() - 1) + " for $" + price + ".";
        }
        // Displays message
        JOptionPane.showMessageDialog(null, message, "Item(s) Found" + HEADER_EXT, JOptionPane.INFORMATION_MESSAGE, errorIcon);
    }

    /**
     * Outputs order information for an unfulfilled order via a JOptionPane GUI
     */
    public static void graphicalUserInterfaceOutput(LinkedList<String> manufacturerList) {
        String message = "";
        message = message + "Order cannot be fulfilled based on current inventory. Suggested manufacturer";
        if (manufacturerList.size() == 1) {
            message = message + " is " + manufacturerList.get(0) + ".";
        }
        else if (manufacturerList.size() == 2) {
            message = message + "s are " + manufacturerList.get(0) + " and "  + manufacturerList.get(1) + ".";
        }
        else if (manufacturerList.size() > 2) {
            message = message + "s are ";
            for (int i = 0; i < manufacturerList.size() - 1; i++) {
                message = message + manufacturerList.get(i) + ", ";
            }
            message = message + "and " + manufacturerList.get(manufacturerList.size() - 1) + ".";
        }
        // Displays message
        JOptionPane.showMessageDialog(null, message, "Item(s) Not Found" + HEADER_EXT, JOptionPane.ERROR_MESSAGE, errorIcon);
    }

    // Command Line Interface

    /**
     * Receives input via the console
     * @param args command line arguments from main()
     */
    public void commandLineInput(String args[]) {
        // Titlecases inputted Strings
        category = args[0].substring(0, 1).toUpperCase() + args[0].substring(1).toLowerCase();
        type = args[1].substring(0, 1).toUpperCase();
        boolean upperCaseNext = false;
        for (int i = 1; i < args[1].length(); i++) {
            if (args[1].charAt(i) == ' ')  {
                type = type + String.valueOf(args[1].charAt(i));
                upperCaseNext = true;
            }
            else if (upperCaseNext) {
                type = type + String.valueOf(args[1].charAt(i)).toUpperCase();
                upperCaseNext = false;
            }
            else {
                type = type + String.valueOf(args[1].charAt(i)).toLowerCase();
            }
        }
        if (args.length == 4) { // handles possible space when inputting type (e.g. Swing Arm)
            type = type + " " + args[2].substring(0, 1).toUpperCase();
            for (int i = 1; i < args[2].length(); i++) {
                if (args[2].charAt(i) == ' ')  {
                    type = type + String.valueOf(args[2].charAt(i));
                    upperCaseNext = true;
                }
                else if (upperCaseNext) {
                    type = type + String.valueOf(args[2].charAt(i)).toUpperCase();
                    upperCaseNext = false;
                }
                else {
                    type = type + String.valueOf(args[2].charAt(i)).toLowerCase();
                }
            }
        }

        boolean validType = false; // flag for valid input

        // Checks if furniture category and type combination is valid
        if (category.equals("Chair")) {
            for (int i = 0; i < CHAIR_TYPES.length; i++) {
                if (type.equals(CHAIR_TYPES[i])) {
                    validType = true;
                }
            }
        }
        else if (category.equals("Desk")) {
            for (int i = 0; i < DESK_TYPES.length; i++) {
                if (type.equals(DESK_TYPES[i])) {
                    validType = true;
                }
            }
        }
        else if (category.equals("Filing")) {
            for (int i = 0; i < FILING_TYPES.length; i++) {
                if (type.equals(FILING_TYPES[i])) {
                    validType = true;
                }
            }
        }
        else if (category.equals("Lamp")) {
            for (int i = 0; i < LAMP_TYPES.length; i++) {
                if (type.equals(LAMP_TYPES[i])) {
                    validType = true;
                }
            }
        }
        else {
            // Error handling for invalid category
            System.out.println(category + " is not a valid furniture category. Please try again.");
        }
        if (!validType) {
            // Error handling for invalid type
            System.out.println(type + " is not a valid " + category.toLowerCase() + " type. Please try again.");
        }
        try {
            quantity = Integer.parseUnsignedInt(args[args.length - 1]); // parses inputted String to int
        }
        catch (NumberFormatException e) {
            // Error handling for invalid number
            System.out.println(args[args.length - 1] + " is not a valid number of items. Please try again.");
        }
        if (quantity == 0) {
            // Error handling for input of zero
            System.out.println("At least one item must be requested. Please try again.");
        }
    }

    /**
     * Outputs order information for a fulfilled order via the console
     */
    public static void commandLineOutput(LinkedList<String> furnitureIDList, int price) {
        System.out.print("Purchase ");
        if (furnitureIDList.size() == 1) {
            System.out.println(furnitureIDList.get(0) + " for $" + price + ".");
        }
        else if (furnitureIDList.size() == 2) {
            System.out.println(furnitureIDList.get(0) + " and "  + furnitureIDList.get(1) + " for $" + price + ".");
        }
        else if (furnitureIDList.size() > 2) {
            for (int i = 0; i < furnitureIDList.size() - 1; i++) {
                System.out.print(furnitureIDList.get(i) + ", ");
            }
            System.out.println("and "  + furnitureIDList.get(furnitureIDList.size() - 1) + " for $" + price + ".");
        }
    }

    /**
     * Outputs order information for an unfulfilled order via the console
     */
    public static void commandLineOutput(LinkedList<String> manufacturerList) {
        System.out.print("Order cannot be fulfilled based on current inventory. Suggested manufacturer");
        if (manufacturerList.size() == 1) {
            System.out.println(" is " + manufacturerList.get(0) + ".");
        }
        else if (manufacturerList.size() == 2) {
            System.out.println("s are " + manufacturerList.get(0) + " and "  + manufacturerList.get(1) + ".");
        }
        else if (manufacturerList.size() > 2) {
            System.out.print("s are ");
            for (int i = 0; i < manufacturerList.size() - 1; i++) {
                System.out.print(manufacturerList.get(i) + ", ");
            }
            System.out.println("and "  + manufacturerList.get(manufacturerList.size() - 1) + ".");
        }
    }
}