package edu.ucalgary.ensf409;
import java.sql.*;

public class Main {
    private final static String[] CHAIR_TYPES = {"Ergonomic", "Executive", "Kneeling", "Mesh", "Task"};
    private final static String[] DESK_TYPES = {"Adjustable", "Standing", "Traditional"};
    private final static String[] FILING_TYPES = {"Small", "Medium", "Large"};
    private final static String[] LAMP_TYPES = {"Desk", "Study", "Swing Arm"};
    public static void main(String[] args) throws IllegalArgumentException {

        // Argument handling
        if (args.length < 3) {
            throw new IllegalArgumentException("A furniture category, its type, and the number of items must be requested.");
        }
        String category = args[0].substring(0, 1).toUpperCase() + args[0].substring(1).toLowerCase();
        String type = args[1].substring(0, 1).toUpperCase();
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
        int quantity = 0;
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

        System.out.println("Test");

        // Habib's Test Code
        FurnitureData database = new FurnitureData("jdbc:mysql://localhost/inventory","habib","password");

        database.initializeConnection(); // initialize the connection
        
        Connection databaseConnection = database.getDatabaseConnection();
    }
}