package edu.ucalgary.ensf409;
import java.sql.*;
import java.util.Arrays;
import java.util.LinkedList;
import javax.swing.*;

public class Main {
    private final static String HEADER_EXT = " - Faculty FrankenFurniture Finder";
    private final static String[] CHAIR_TYPES = {"Ergonomic", "Executive", "Kneeling", "Mesh", "Task"};
    private final static String[] DESK_TYPES = {"Adjustable", "Standing", "Traditional"};
    private final static String[] FILING_TYPES = {"Small", "Medium", "Large"};
    private final static String[] LAMP_TYPES = {"Desk", "Study", "Swing Arm"};
    private String category = null;
    private String type = null;
    private int quantity = Integer.MIN_VALUE;
    private final static ImageIcon icon = new ImageIcon("frankenchair.png");
    private final static ImageIcon erroricon = new ImageIcon("frankenchairerror.png");


    public static void main(String[] args) {
        Main main = new Main(); // instantiates Main object
        boolean gui = false;

        if (args.length < 3) {
            gui = true;
            main.graphicalUserInterfaceInput(); // either launches GUI
        }
        else {
            main.commandLineInput(args); // or accepts command line arguments
        }        

        //The order form should be created and used here, using orderResult to get all the needed information
        // // Habib's Test Code
        FurnitureData database = new FurnitureData("jdbc:mysql://localhost/inventory","habib","password");

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
                graphicalUserInterfaceOutput(orderResult.getFurnitureIDList(), orderResult.getPrice());
            }
            else {
                commandLineOutput(orderResult.getFurnitureIDList(), orderResult.getPrice());
            }
            
            //The order form should be created and used here, using orderResult to get all the needed information
            OrderForm.writeOrderForm(orderResult.getTYPE(), orderResult.getCATEGORY(), orderResult.getNUMITEMS(), orderResult.getFurnitureIDList(), orderResult.getPrice());
        }
        else {
            if (gui) {
                graphicalUserInterfaceOutput(orderResult.getManufacturerNameList()); // THIS SHOULD BE MANUFACTURER NAMES NOT IDs !!
            }
            else {
                commandLineOutput(orderResult.getManufacturerNameList()); // THIS SHOULD BE MANUFACTURER NAMES NOT IDs !!
            }
        }
    }

    // JOptionPane Interface
    public void graphicalUserInterfaceInput() {
        try {
            String[] categories = {"Chair", "Desk", "Filing", "Lamp"};
            this.category = (String)JOptionPane.showInputDialog(null, "I am looking for (a)...", "Category" + HEADER_EXT, JOptionPane.INFORMATION_MESSAGE, icon, categories, categories[0]);
            if (this.category.equals("Chair")) {
                this.type = (String)JOptionPane.showInputDialog(null, "Select a type of chair", "Type (Chair)" + HEADER_EXT, JOptionPane.INFORMATION_MESSAGE, icon, CHAIR_TYPES, CHAIR_TYPES[0]);
            }
            else if (this.category.equals("Desk")) {
                this.type = (String)JOptionPane.showInputDialog(null, "Select a type of desk", "Type (Desk)" + HEADER_EXT, JOptionPane.INFORMATION_MESSAGE, icon, DESK_TYPES, DESK_TYPES[0]);
            }
            else if (this.category.equals("Filing")) {
                this.type = (String)JOptionPane.showInputDialog(null, "Select a type of filing", "Type (FilingO" + HEADER_EXT, JOptionPane.INFORMATION_MESSAGE, icon, FILING_TYPES, FILING_TYPES[0]);
            }
            else {
                this.type = (String)JOptionPane.showInputDialog(null, "Select a type of lamp", "Type (Lamp)" + HEADER_EXT, JOptionPane.INFORMATION_MESSAGE, icon, LAMP_TYPES, LAMP_TYPES[0]);
            }
        }
        catch (NullPointerException e) {
            System.exit(0);
        }
        if (this.type == null){
            System.exit(0);
        }
        String quantityString = "Input";
        while (this.quantity < 1) {
            quantityString = (String)JOptionPane.showInputDialog(null, "How many would you like?", "Quantity" + HEADER_EXT, JOptionPane.INFORMATION_MESSAGE, icon, null, "1");
            if (quantityString == null) {
                System.exit(1);
            }
            try {
                this.quantity = Integer.parseUnsignedInt(quantityString);
            }
            catch (NumberFormatException e){
                if (quantityString.length() == 0) {
                    JOptionPane.showMessageDialog(null, "At least one item must be requested.", "Error" + HEADER_EXT, JOptionPane.ERROR_MESSAGE, errorIcon);
                }
                else {
                    JOptionPane.showMessageDialog(null, quantityString + " is not a valid number of items.", "Error" + HEADER_EXT, JOptionPane.ERROR_MESSAGE, errorIcon);
                }
            }
            if (quantity == 0) {
                JOptionPane.showMessageDialog(null, "At least one item must be requested.", "Error" + HEADER_EXT, JOptionPane.ERROR_MESSAGE, errorIcon);
            }
        }
    }

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
        JOptionPane.showMessageDialog(null, message, "Item(s) Found" + HEADER_EXT, JOptionPane.INFORMATION_MESSAGE, errorIcon);
    }
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
        JOptionPane.showMessageDialog(null, message, "Item(s) Not Found" + HEADER_EXT, JOptionPane.ERROR_MESSAGE, errorIcon);
    }

    // Command Line Interface
    public void commandLineInput(String args[]) {
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
        if (args.length == 4) { 
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
        boolean validType = false;
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
            System.out.println(category + " is not a valid furniture category. Please try again.");
        }
        if (!validType) {
            System.out.println(type + " is not a valid " + category.toLowerCase() + " type. Please try again.");
        }
        try {
            quantity = Integer.parseUnsignedInt(args[args.length - 1]);
        }
        catch (NumberFormatException e) {
            System.out.println(args[args.length - 1] + " is not a valid number of items. Please try again.");
        }
        if (quantity == 0) {
            System.out.println("At least one item must be requested. Please try again.");
        }
    }

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