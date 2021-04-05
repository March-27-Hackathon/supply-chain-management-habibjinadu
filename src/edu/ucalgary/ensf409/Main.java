package edu.ucalgary.ensf409;
import java.sql.*;
import javax.swing.JOptionPane;


public class Main {
    private final static String HEADER_EXT = " - Faculty FrankenFurniture Finder";
    private final static String[] CHAIR_TYPES = {"Ergonomic", "Executive", "Kneeling", "Mesh", "Task"};
    private final static String[] DESK_TYPES = {"Adjustable", "Standing", "Traditional"};
    private final static String[] FILING_TYPES = {"Small", "Medium", "Large"};
    private final static String[] LAMP_TYPES = {"Desk", "Study", "Swing Arm"};
    public static void main(String[] args) throws IllegalArgumentException {
        String category = null;
        String type = null;
        int quantity = Integer.MIN_VALUE;
        // JOptionPane Interface
        if (args.length < 3) {
            try {
                String[] categories = {"Chair", "Desk", "Filing", "Lamp"};
                category = (String)JOptionPane.showInputDialog(null, "I am looking for (a)...", "Category" + HEADER_EXT, JOptionPane.INFORMATION_MESSAGE, null, categories, categories[0]);
                if (category.equals("Chair")) {
                    type = (String)JOptionPane.showInputDialog(null, "Select a type of chair", "Type (Chair)" + HEADER_EXT, JOptionPane.INFORMATION_MESSAGE, null, CHAIR_TYPES, CHAIR_TYPES[0]);
                }
                else if (category.equals("Desk")) {
                    type = (String)JOptionPane.showInputDialog(null, "Select a type of desk", "Type (Desk)" + HEADER_EXT, JOptionPane.INFORMATION_MESSAGE, null, DESK_TYPES, DESK_TYPES[0]);
                }
                else if (category.equals("Filing")) {
                    type = (String)JOptionPane.showInputDialog(null, "Select a type of filing", "Type (FilingO" + HEADER_EXT, JOptionPane.INFORMATION_MESSAGE, null, FILING_TYPES, FILING_TYPES[0]);
                }
                else {
                    type = (String)JOptionPane.showInputDialog(null, "Select a type of lamp", "Type (Lamp)" + HEADER_EXT, JOptionPane.INFORMATION_MESSAGE, null, LAMP_TYPES, LAMP_TYPES[0]);
                }
            }
            catch (NullPointerException e) {
                System.exit(0);
            }
            if (type == null){
                System.exit(0);
            }
            String quantityString = "Input";
            while (quantity < 1) {
                quantityString = (String)JOptionPane.showInputDialog(null, "How many would you like?", "Quantity" + HEADER_EXT, JOptionPane.INFORMATION_MESSAGE, null, null, null);
                if (quantityString == null) {
                    System.exit(1);
                }
                try {
                    quantity = Integer.parseUnsignedInt(quantityString);
                }
                catch (NumberFormatException e){
                    JOptionPane.showMessageDialog(null, quantityString + " is not a valid number of items.", "Error" + HEADER_EXT, JOptionPane.ERROR_MESSAGE);
                }
                if (quantity == 0) {
                    JOptionPane.showMessageDialog(null, "At least one item must be requested.", "Error" + HEADER_EXT, JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        // Commandline argument handling
        else {
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
                throw new IllegalArgumentException(category + " is not a valid furniture category.");
            }
            if (!validType) {
                throw new IllegalArgumentException(type + " is not a valid " + category.toLowerCase() + " type.");
            }
            try {
                quantity = Integer.parseUnsignedInt(args[args.length - 1]);
            }
            catch (NumberFormatException e) {
                throw new IllegalArgumentException(args[args.length - 1] + " is not a valid number of items.");
            }
            if (quantity == 0) {
                throw new IllegalArgumentException("At least one item must be requested.");
            }
        }

        // System.out.println("Category: " + category);
        // System.out.println("Type: " + type);
        // System.out.println("Quantity: " + String.valueOf(quantity));

        // Habib's Test Code
        FurnitureData database = new FurnitureData("jdbc:mysql://localhost/inventory","habib","password");

        database.initializeConnection(); // initialize the connection

        Connection databaseConnection = database.getDatabaseConnection();

        //Carter's test code
        LowestCost calculation = new LowestCost(databaseConnection, category, type, quantity);
        FurnitureOrder orderResult = calculation.findBestCombination(); //findBestCombination will create and return an
        //order with all of the relevant information

        //The order form should be created and used here, using orderResult to get all the needed information
    }
}