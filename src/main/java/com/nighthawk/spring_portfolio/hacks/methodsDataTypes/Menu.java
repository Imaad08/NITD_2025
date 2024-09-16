package com.nighthawk.spring_portfolio.hacks.methodsDataTypes;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Menu: custom implementation
 * @author     John Mortensen
 *
 * Uses String to contain Title for an Option
 * Uses Runnable to store Class-Method to be run when Title is selected
 */

// The Menu Class has a HashMap of Menu Rows
public class Menu {
    // Format
    // Key {0, 1, 2, ...} created based on order of input menu
    // Value {MenuRow0, MenuRow1, MenuRow2,...} each corresponds to key
    // MenuRow  {<Exit,Noop>, Option1, Option2, ...}
    Map<Integer, MenuRow> menu = new HashMap<>();

    /**
     *  Constructor for Menu,
     *
     * @param  rows,  is the row data for menu.
     */
    public Menu(MenuRow[] rows) {
        int i = 0;
        for (MenuRow row : rows) {
            // Build HashMap for lookup convenience
            menu.put(i++, new MenuRow(row.getTitle(), row.getAction()));
        }
    }

    /**
     *  Get Row from Menu,
     *
     * @param  i,  HashMap key (k)
     *
     * @return  MenuRow, the selected menu
     */
    public MenuRow get(int i) {
        return menu.get(i);
    }

    /**
     *  Iterate through and print rows in HashMap
     */
    public void print() {
        for (Map.Entry<Integer, MenuRow> pair : menu.entrySet()) {
            System.out.println(pair.getKey() + " ==> " + pair.getValue().getTitle());
        }
    }

    /**
     *  To test run Driver
     */
    public static void main(String[] args) {
        Driver.main(args);
    }

}

// The MenuRow Class has title and action for individual line item in menu
class MenuRow {
    String title;       // menu item title
    Runnable action;    // menu item action, using Runnable

    /**
     *  Constructor for MenuRow,
     *
     * @param  title,  is the description of the menu item
     * @param  action, is the run-able action for the menu item
     */
    public MenuRow(String title, Runnable action) {
        this.title = title;
        this.action = action;
    }

    /**
     *  Getters
     */
    public String getTitle() {
        return this.title;
    }
    public Runnable getAction() {
        return this.action;
    }

    /**
     *  Runs the action using Runnable (.run)
     */
    public void run() {
        action.run();
    }
}

// The Main Class illustrates initializing and using Menu with Runnable action
class Driver {
    /**
     *  Menu Control Example
     */
    public static void main(String[] args) {
        // Row initialize
        MenuRow[] rows = new MenuRow[]{
            // lambda style, () -> to point to Class.Method
            new MenuRow("Exit", () -> main(null)),
            new MenuRow("Do Nothing", () -> DoNothingByValue.main(null)),
            new MenuRow("Swap if Hi-Low", () -> IntByReference.main(null)),
            new MenuRow("Matrix Reverse", () -> Matrix.main(null)),
            new MenuRow("Diverse Array", () -> DiverseArray.main(null)),
            new MenuRow("Random Squirrels", () -> Number.main(null))
        };

        // Menu construction
        Menu menu = new Menu(rows);

        // Run menu forever, exit condition contained in loop
        while (true) {
            System.out.println("Hacks Menu:");
            // print rows
            menu.print();

            // Scan for input
            try {
                Scanner scan = new Scanner(System.in);
                int selection = scan.nextInt();

                // menu action
                try {
                    MenuRow row = menu.get(selection);
                    // stop menu
                    if (row.getTitle().equals("Exit")) {
                        if (scan != null) 
                            scan.close();  // scanner resource requires release
                        return;
                    }
                    // run option
                    row.run();
                } catch (Exception e) {
                    System.out.printf("Invalid selection %d\n", selection);
                }

            } catch (Exception e) {
                System.out.println("Not a number");
            }
        }
    }
}
