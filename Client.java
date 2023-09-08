/**
 * Client
 * This class contains the GUI and
 * recieves data from the server that
 * is used to function the program
 *
 *
 * @author Vanamali Vemparala, Milan Dhawan, Elliot Bode, Stephen Kruse, lab sec 26
 *
 * @version December 12, 2022
 *
 */

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.text.NumberFormat;
import java.util.*;

public class Client extends Thread {

    private static Object obj = new Object();

    public String convertName(String storeName) {
        String[] store = storeName.split(";");
        storeName = store[0];
        storeName = storeName.replaceAll("\\s", "");
        return storeName;
    }

    public void createAccount(PrintWriter writer, BufferedReader reader) throws IOException {
        //creates customer and seller accounts
        String username;
        String[] accountInfo = new String[2];

        String[] accountTypes = new String[2];
        accountTypes[0] = "Customer";
        accountTypes[1] = "Seller";
        String acctType = (String) JOptionPane.showInputDialog(null, "What type of " +
                        "account would you like to create?", "Choose account", JOptionPane.QUESTION_MESSAGE,
                null, accountTypes, null);

        acctType = acctType.toLowerCase();
        //System.out.println("What type of account would you like to create?\n1. Customer\n2. Seller");
        //String acctType = scanner.nextLine().toLowerCase();
        do { //loops if the customer does not enter valid account creation info
            try {
                if (!acctType.matches("customer") & !acctType.matches("seller")) {
                    JOptionPane.showMessageDialog(null, "Error: Please select a valid account type",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    acctType = (String) JOptionPane.showInputDialog(null, "What type of " +
                                    "account would you like to create?", "Choose account",
                            JOptionPane.QUESTION_MESSAGE, null, accountTypes, null);
                }
            } catch (NullPointerException e) {
                return;
            }
            /*
            if (!acctType.matches("1.|1|customer|2.|2|seller")) {
                //1., 1, and customer (case insensitive) are valid input for customer account
                System.out.println("Please select a valid account type.");
                System.out.println("1. Customer\n2. Seller");
                acctType = scanner.nextLine().toLowerCase();
            }

             */
            String type = "";
            if (acctType.matches("1.|1|customer")) {
                type = "1";
                accountInfo[0] = "customer";
            } else if (acctType.matches("2|2.|seller")) {
                type = "2";
                accountInfo[0] = "seller";
            }
            boolean incorrect;
            boolean exists = false;
            do {
                incorrect = false;
                if (exists == true) {
                    JOptionPane.showMessageDialog(null, "That " +
                            "username is already taken! " +
                            "Please select another one", "Error", JOptionPane.ERROR_MESSAGE);
                }
                //if the username already exists, the program loops until the user inputs
                //a username that does not exist
                try {
                    username = JOptionPane.showInputDialog(null, "Enter a username/email:",
                            "Username Creation", JOptionPane.PLAIN_MESSAGE);
                    if (!username.contains("@") && !username.contains(".")) {
                        JOptionPane.showMessageDialog(null, "Enter a valid email!",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        incorrect = true;
                    } else {
                        //send username to the server
                        //run server findaccount method
                        //send boolean back to client, if true, proceed, if false, display error
                        synchronized (obj) {
                            writer.write(type);
                            writer.println();
                            writer.flush();
                            writer.write(username);
                            writer.println();
                            writer.flush();
                        }
                        exists = Boolean.parseBoolean(reader.readLine());
                        if (exists == false) {
                            String password = JOptionPane.showInputDialog(null, "Enter a password:",
                                    "Password Creation", JOptionPane.PLAIN_MESSAGE);
                            if (password == null) {
                                return;
                            }
                            /*
                            try {
                                if (password == null) {
                                    return;
                                }
                                if (Integer.parseInt(password) == JOptionPane.CLOSED_OPTION ||
                                        Integer.parseInt(password) == JOptionPane.CANCEL_OPTION) {
                                    return;
                                }
                            } catch (NumberFormatException e) {
                                //continue;
                            }

                             */


                            //System.out.println("Enter a password: ");
                            //String password = scanner.nextLine();
                            synchronized (obj) {
                                System.out.println(password);
                                writer.write(password);
                                writer.println();
                                writer.flush();
                            }
                        }
                    }
                } catch (NullPointerException e) {
                    return;
                }
            } while (exists == true || incorrect == true);
            accountInfo[1] = username;
        } while (!acctType.matches("1.|1|customer|2.|2|seller"));
        afterLogIn(accountInfo, writer, reader, 0, null);
        return;
    }

    public String[] logIn(PrintWriter writer, BufferedReader reader) throws IOException {
        //ORDER:
        //send acctType to server
        //send username to server
        //send password to server
        //server runs findAccount- if DNE, either username/ pass incorrect, repeat
        //server compare pass to comparePass
        String acctType;
        String[] accounts = new String[2];
        accounts[0] = "Customer";
        accounts[1] = "Seller";

        do {
            acctType = (String) JOptionPane.showInputDialog(null, "Welcome back! What type of account do " +
                    "you have?", "Account Type", JOptionPane.QUESTION_MESSAGE, null, accounts, null);
            try {
                acctType = acctType.toLowerCase();
            } catch (NullPointerException e) {
                return null;
            }
            //System.out.println("Welcome back! What type of account do you have?\n1. Customer\n2. Seller");
            //acctType = scanner.nextLine(); //send to server, dictates filereading
            //acctType = acctType.toLowerCase();
            if (!acctType.matches("1.|1|customer|2.|2|seller")) {
                JOptionPane.showMessageDialog(null, "Please enter a valid account type",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } while (!acctType.matches("1.|1|customer|2.|2|seller"));
        if (acctType.matches("1|1.|customer")) {
            acctType = "customer";
        } else if (acctType.matches("2|2.|seller")) {
            acctType = "seller";
        }
        String username; //input must be sent to server
        String password; //input must be sent to server
        String comparePass; //get from readfile (server side)
        boolean verified = false;
        do {

            try {
                username = JOptionPane.showInputDialog(null, "Please enter your username",
                        "Login", JOptionPane.PLAIN_MESSAGE);

                if (username == null) {
                    return null;
                }
            } catch (NullPointerException e) {
                return null;
            }
            //System.out.println("Please enter your username.");
            //username = scanner.nextLine();
            if (!username.contains("@") || !username.contains(".")) {
                JOptionPane.showMessageDialog(null, "That is not a valid username! Please enter an email.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                //System.out.println("That is not a valid username! Please enter an email.");
                continue;
            }

            try {
                //loops until password matches given username
                password = JOptionPane.showInputDialog(null, "Enter your password",
                        "Login", JOptionPane.PLAIN_MESSAGE);
            } catch (NullPointerException e) {
                return null;
            }
            //System.out.println("Enter your password.");
            //password = scanner.nextLine();
            synchronized (obj) {
                writer.write(acctType);
                writer.println();
                writer.flush();
                writer.write(username);
                writer.println();
                writer.flush();
                writer.write(password);
                writer.println();
                writer.flush();
            }
            verified = Boolean.parseBoolean(reader.readLine());
            if (!verified) {
                JOptionPane.showMessageDialog(null, "Either your username or password is incorrect!",
                        "Error", JOptionPane.ERROR_MESSAGE);
                //System.out.println("Either your username or password is incorrect!");
            }
        } while (!verified);
        if (acctType.matches("seller")) {
            return new String[]{"seller", username};
        } else if (acctType.matches("customer")) {
            return new String[]{"customer", username};
        } else {
            return null;
        }
    }

    int hello = 0;

    public void afterLogIn(String[] accountInfo, PrintWriter writer,
                           BufferedReader reader, int called, String search) throws IOException { // fix line
        // 157
        // (local field issue)

        if (hello == 1) {
            hello = 0;
            return;
        }
        int line = -1;
        int repeated = 0;
        String choice;
        if (called == 2) {
            choice = "b0";
        } else {
            choice = "b" + called;
            if (choice.matches("b3")) {
                repeated += 1;
            }
        }
        String accountType = accountInfo[0];
        String username = accountInfo[1];

        String[] sellerOptions = new String[6];
        sellerOptions[0] = "Edit account";
        sellerOptions[1] = "Edit store";
        sellerOptions[2] = "Add store";
        sellerOptions[3] = "Delete store";
        sellerOptions[4] = "Check statistics";
        sellerOptions[5] = "Log out";

        String[] customerOptions = new String[6];
        customerOptions[0] = "Edit account";
        customerOptions[1] = "Browse all products";
        customerOptions[2] = "Search for a product or store";
        customerOptions[3] = "Review purchase history";
        customerOptions[4] = "Check statistics";
        customerOptions[5] = "Log out";

        String[] yesOrNo = new String[2];
        yesOrNo[0] = "Yes";
        yesOrNo[1] = "No";


        if (called == 1) {
            accountType = "customer";
        }
        if (accountType.equals("seller")) {
            do {
                //System.out.println("2. Edit store\n3. Add store\n4. Delete store\n5. Check statistics\n6.
                // Log out");
                //choice = scanner.nextLine();
                choice = (String) JOptionPane.showInputDialog(null,
                        "What would you like to do?", "Seller Menu", JOptionPane.QUESTION_MESSAGE,
                        null, sellerOptions, null);
                if (choice.matches("Edit account")) {
                    editAccount(accountInfo, reader, writer, accountType, username);
                }
                if (choice.matches(("Edit store"))) {
                    synchronized (obj) {
                        writer.write(accountType);
                        writer.println();
                        writer.flush();
                        writer.write("2");
                        writer.println();
                        writer.flush();
                    }
                    editStore(accountInfo, reader, writer, username);
                }
                if (choice.matches(("Add store"))) {
                    synchronized (obj) {
                        writer.write(accountType);
                        writer.println();
                        writer.flush();
                        writer.write("3");
                        writer.println();
                        writer.flush();
                    }
                    addStore(accountInfo, reader, writer, username);
                }
                if (choice.matches(("Delete store"))) {
                    synchronized (obj) {
                        writer.write(accountType);
                        writer.println();
                        writer.flush();
                        writer.write("4");
                        writer.println();
                        writer.flush();
                    }
                    deleteStore(accountInfo, reader, writer, username);
                }
                if (choice.matches("Check statistics")) {
                    String response;
                    do {
                        String[] options = new String[3];
                        options[0] = "By customer purchases";
                        options[1] = "By the product sales";
                        options[2] = "As a CSV file";

                        // sees how the user would want to organize the data
                        String ArrangeSelect;
                        ArrangeSelect = (String) JOptionPane.showInputDialog(null,
                                "How would you like to view/export the data?",
                                "Seller Statistics", JOptionPane.QUESTION_MESSAGE, null, options, null);
                        if (ArrangeSelect == null) {
                            return;
                        }
                        String arrange = null;
                        if (ArrangeSelect.equals("By customer purchases")) {
                            arrange = "1";
                        } else if (ArrangeSelect.equals("By the product sales")) {
                            arrange = "2";
                        } else if (ArrangeSelect.equals("As a CSV file")) {
                            arrange = "3";
                        }
                        response = arrange;
                    } while (!response.matches("1|2|3"));

                    String storeExportName = null;
                    if (response.matches("3")) {
                        storeExportName = JOptionPane.showInputDialog(null, "Which store would " +
                                "you like to export?", "Export Store", JOptionPane.QUESTION_MESSAGE);
                        storeExportName = storeExportName.replaceAll(" ", "");
                        storeExportName = storeExportName.toLowerCase();
                    }

                    synchronized (obj) {
                        writer.write(accountType);
                        writer.println();
                        writer.flush();
                        writer.write("5");
                        writer.println();
                        writer.flush();
                        writer.write(username);
                        writer.println();
                        writer.flush();
                        writer.write(response);
                        writer.println();
                        writer.flush();
                        if (response.matches("3")) {
                            writer.write(storeExportName);
                            writer.println();
                            writer.flush();
                        }
                    }

                    if (!response.matches("3")) {
                        ArrayList<String> display = new ArrayList<>();
                        for (String dispLine = reader.readLine(); dispLine != null && dispLine.length() > 0; dispLine =
                                reader.readLine()) {
                            display.add(dispLine);
                        }
                        if (display.get(0).matches("No purchases!")) {
                            JOptionPane.showMessageDialog(null, "No customers have purchased any of your " +
                                    "products yet", "Ranked By Customers", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            String[] displayArray = display.toArray(new String[0]);

                            JOptionPane.showMessageDialog(null, displayArray, "Sorted Statistics",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        String exportMessage = reader.readLine();
                        if (exportMessage.matches("Not owned!")) {
                            JOptionPane.showMessageDialog(null, "That is not a store you own!",
                                    "Purchase History Exported", JOptionPane.ERROR_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, exportMessage, "Purchase History Exported",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }

                if (choice.matches("Log out")) {
                    synchronized (obj) {
                        writer.write(accountType);
                        writer.println();
                        writer.flush();
                        writer.write("6");
                        writer.println();
                        writer.flush();
                    }
                    JOptionPane.showMessageDialog(null, "Goodbye!", "Log Out",
                            JOptionPane.INFORMATION_MESSAGE);
                    break;
                }
            } while (!Arrays.asList(sellerOptions).contains(choice));
            if (!choice.matches("Log out")) {
                afterLogIn(accountInfo, writer, reader, 0, null);
                return;
            }
            /*
            writer.write(accountType);
            writer.println();
            writer.flush();
            writer.write(choice);
            writer.println();
            writer.flush();
             */
        }
                /*
                if (choice.matches("1.|1")) {
                    editAccount(scanner, accountInfo);
                } else if (choice.matches("2.|2")) {
                    editStore(scanner, username, accountInfo);
                } else if (choice.matches("3.|3")) {
                    addStoreChoice(scanner, accountInfo, username);
                } else if (choice.matches("4.|4")) {
                    deleteStore(scanner, username, accountInfo);
                    //call method to implement Vanamali delete store logic
                } else if (choice.matches("5.|5")) {
                    Seller seller = new Seller(accountInfo[1]);
                    seller.checkSellerStatistics(scanner, accountInfo[1]);
                    afterLogIn(scanner, accountInfo, writer, reader);
                    //call method to implement Milan statistics logic

                 */

        if (accountType.equals("customer")) {
            do {
                if (called == 1 && search != null && !choice.matches("b3")) {
                    choice = "Search for a product or store";
                } else if (called == 1 && search == null && !choice.matches("b3")) {
                    choice = "Browse all products";
                } else if (!choice.matches("b3")) {
                    choice = (String) JOptionPane.showInputDialog(null, "What would you like " +
                                    "to do?", "Customer Menu", JOptionPane.QUESTION_MESSAGE,
                            null, customerOptions, null);
                } else if (choice.matches("b3") && repeated == 1) {
                    choice = (String) JOptionPane.showInputDialog(null, "What would you like " +
                                    "to do?", "Customer Menu", JOptionPane.QUESTION_MESSAGE,
                            null, customerOptions, null);
                    if (choice.matches("Log out")) {
                        hello = 1;
                        JOptionPane.showMessageDialog(null, "Goodbye!", "Log Out",
                                JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                } else if (choice.matches("b3") && repeated != 1) {
                    return;
                }
                if (choice == null) {
                    return;
                }
                if (choice.matches("Edit account")) {
                    editAccount(accountInfo, reader, writer, accountType, username);
                    break;
                }
                if (choice.matches("Browse all products")) {
                    int selection = 2;
                    synchronized (obj) {
                        writer.write(accountType);
                        writer.println();
                        writer.flush();
                        writer.write("2");
                        writer.println();
                        writer.flush();
                    }
                    viewMarketplace(null, accountInfo, writer, reader, selection, null);
                    afterLogIn(accountInfo, writer, reader, 3, null);
                    return;
                }
                if (choice.matches("Search for a product or store")) {
                    int selection = 3;
                    ArrayList<String> displayList = new ArrayList<>();
                    boolean hits = true;
                    String query;
                    synchronized (obj) {
                        writer.write(accountType);
                        writer.println();
                        writer.flush();
                        writer.write("3");
                        writer.println();
                        writer.flush();
                    }
                    do {
                        hits = true;
                        if (called != 1) {
                            query = JOptionPane.showInputDialog(null, "What would you like" +
                                    " to search?", "Search Product/Store", JOptionPane.QUESTION_MESSAGE);
                            //System.out.println("What would you like to search?");
                            //query = scanner.nextLine();
                        } else {
                            query = search;
                        }
                        synchronized (obj) {
                            writer.write(query);
                            writer.println();
                            writer.flush();
                        }
                        displayList.clear();
                        for (String read = reader.readLine(); read != null && read.length() > 0; read =
                                reader.readLine()) {
                            displayList.add(read);
                        }
                        //System.out.println("size = " + displayList.get(0));
                        if (displayList.get(0).matches("false")) {
                            hits = false;
                            JOptionPane.showMessageDialog(null, "There were no products " +
                                    "matching your search!", "Error", JOptionPane.ERROR_MESSAGE);
                            //System.out.println("There were no products matching your search! Try again.");
                        }
                    } while (!hits);
                    //System.out.println("Success!");
                    viewMarketplace(displayList, accountInfo, writer, reader, selection, query);
                    afterLogIn(accountInfo, writer, reader, 1, query);
                    return;
                }
                if (choice.matches("Review purchase history")) {
                    //System.out.println("Would you like to export your purchase history?\n1. Yes\n2. No");
                    String historyResponse;
                    synchronized (obj) {
                        writer.write(accountType);
                        writer.println();
                        writer.flush();
                        writer.write("4");
                        writer.println();
                        writer.flush();
                    }
                    do {
                        historyResponse = (String) JOptionPane.showInputDialog(null,
                                "Would you like to export your purchase history?", "Review Purchase History",
                                JOptionPane.QUESTION_MESSAGE, null, yesOrNo, null);
                        if (historyResponse == null) {
                            return;
                        }
                        //System.out.println(historyResponse);
                        //historyResponse = scanner.nextLine();
                        //send appropriate data to server
                        //customer.exportPurchases(accountInfo[1]);
                    } while (!historyResponse.matches("Yes|No"));
                    if (historyResponse.matches("Yes")) {
                        synchronized (obj) {
                            writer.write("1");
                            writer.println();
                            writer.flush();
                            writer.write(username);
                            writer.println();
                            writer.flush();
                        }
                        String exportMessage = reader.readLine();
                        JOptionPane.showMessageDialog(null, exportMessage, "Success", JOptionPane.INFORMATION_MESSAGE);
                        //System.out.println(exportMessage);
                    } else if (historyResponse.matches("No")) {
                        writer.write("2");
                        writer.println();
                        writer.flush();
                    }
                    //afterLogIn(scanner, accountInfo, writer, reader, 0, null);
                }
                if (choice.matches("Check statistics")) {
                    String response;
                    do {
                        String[] ArrangeList = new String[3];
                        ArrangeList[0] = "Alphabetical Order";
                        ArrangeList[1] = "By total sold products";
                        ArrangeList[2] = "By products you bought";
                        String ArrangeSelect;
                        ArrangeSelect = (String) JOptionPane.showInputDialog(null, "How would you like to arrange the store data?", "Marketplace",
                                JOptionPane.QUESTION_MESSAGE, null, ArrangeList, null);
                        if (ArrangeSelect == null) {
                            return;
                        }
                        String arrange = null;
                        if (ArrangeSelect.equals("Alphabetical Order")) {
                            arrange = "1";
                        } else if (ArrangeSelect.equals("By total sold products")) {
                            arrange = "2";
                        } else if (ArrangeSelect.equals("By products you bought")) {
                            arrange = "3";
                        }
                        response = arrange;
                    } while (!response.matches("1|2|3"));

                    synchronized (obj) {
                        writer.write(accountType);
                        writer.println();
                        writer.flush();
                        writer.write("5");
                        writer.println();
                        writer.flush();
                        writer.write(response);
                        writer.println();
                        writer.flush();
                        writer.write(username);
                        writer.println();
                        writer.flush();
                    }
                    ArrayList<String> display = new ArrayList<>();
                    for (String dispLine = reader.readLine(); dispLine != null && dispLine.length() > 0; dispLine =
                            reader.readLine()) {
                        display.add(dispLine);
                    }
                    String[] displayArray = display.toArray(new String[0]);

                    JOptionPane.showMessageDialog(null, displayArray, "Sorted Statistics",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                if (choice.matches("Log out")) {
                    synchronized (obj) {
                        writer.write(accountType);
                        writer.println();
                        writer.flush();
                        writer.write("6");
                        writer.println();
                        writer.flush();
                    }
                    JOptionPane.showMessageDialog(null, "Goodbye!", "Log Out",
                            JOptionPane.INFORMATION_MESSAGE);
                    //System.out.println("Goodbye!");
                    //break;
                    choice = "Log out";
                    break;
                }
            } while (!Arrays.asList(customerOptions).contains(choice));
            if (choice.matches("Log out")) {
                choice = "Log out";
                return;
            } else if (!choice.matches("Log out")) {
                afterLogIn(accountInfo, writer, reader, 0, null);
                return;
            }
        }
    }

    public void editAccount(String[] accountInfo, BufferedReader reader,
                            PrintWriter writer, String accountType, String username) throws IOException {
        boolean incorrect;
        boolean exists = false;
        String newUsername;
        do {
            incorrect = false;
            if (exists == true) {
                JOptionPane.showMessageDialog(null, "That username already exists, please " +
                        "try again!", "Error", JOptionPane.ERROR_MESSAGE);
            }
            //if the username already exists, the program loops until the user inputs
            //a username that does not exist
            newUsername = JOptionPane.showInputDialog(null, "What is your new username?",
                    "Edit Account", JOptionPane.QUESTION_MESSAGE);
            if (newUsername == null) {
                return;
            }
            //System.out.println("What is your new username?");
            //newUsername = scanner.nextLine();
            if (!newUsername.contains("@") && !newUsername.contains(".")) {
                JOptionPane.showMessageDialog(null, "Enter a valid email!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                incorrect = true;
            } else {
                //send username to the server
                //run server findaccount method
                //send boolean back to client, if true, proceed, if false, display error
                synchronized (obj) {
                    writer.write(accountType);
                    writer.println();
                    writer.flush();
                    writer.write("1");
                    writer.println();
                    writer.flush();
                    writer.write(username);
                    writer.println();
                    writer.flush();
                    writer.write(newUsername);
                    writer.println();
                    writer.flush();
                }
                exists = Boolean.parseBoolean(reader.readLine());
                if (exists == false) {
                    String password = JOptionPane.showInputDialog(null, "What is your new password?",
                            "Edit Account", JOptionPane.QUESTION_MESSAGE);
                    if (password == null) {
                        return;
                    }
                    //System.out.println("Enter a password: ");
                    //String password = scanner.nextLine();
                    synchronized (obj) {
                        writer.write(password);
                        writer.println();
                        writer.flush();
                    }
                }
            }
        } while (exists == true || incorrect == true);
        accountInfo[1] = newUsername;
    }

    public void editStore(String[] accountInfo, BufferedReader reader,
                          PrintWriter writer, String username) throws IOException {
        writer.write(username);
        writer.println();
        writer.flush();
        ArrayList<String> display = new ArrayList<>();
        do {
            String disp = reader.readLine();
            display.add(disp);
            String ans = reader.readLine();
            if (ans.matches("a")) {
                break;
            }
        } while (true);

        for (int i = 0; i < display.size(); i++) {
            display.set(i, display.get(i).substring(0, display.get(i).indexOf(";")));
        }


        HashSet<String> hset = new HashSet<String>(display);


//        boolean storeExists = false;
//        boolean ownStore = false;
//        writer.write(String.valueOf(storeExists));
//        writer.println();
//        writer.flush();
//        writer.write(String.valueOf(ownStore));
//        writer.println();
//        writer.flush();
        String store = null;

        store = (String) JOptionPane.showInputDialog(null, "Which store would you like to Edit?", "Edit Store",
                JOptionPane.QUESTION_MESSAGE, null, hset.toArray(), null);
        if (store == null) {
            return;
        }
        synchronized (obj) {
            writer.write(store);
            writer.println();
            writer.flush();
        }

//            String ans1 = reader.readLine();
//            String ans2 = reader.readLine();
//            if (ans1.matches("y") && ans2.matches("y")) {
//                break;
//            } else {
//                JOptionPane.showMessageDialog(null, "Give an existing store that you " +
//                        "own!", "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        } while (true);


        String selection;
        String[] yesOrNo = new String[]{"Yes", "No"};
        do {
            selection = (String) JOptionPane.showInputDialog(null, "Would you like to change" +
                            " the name?", "Edit Store", JOptionPane.QUESTION_MESSAGE, null, yesOrNo,
                    null);
            if (selection == null) {
                return;
            }
            if (selection.equals("Yes")) {
                selection = "y";
            } else if (selection.equals("No")) {
                selection = "n";
            }
        } while (!selection.equals("n") && !selection.equals("y"));


        synchronized (obj) {
            writer.write(selection);
            writer.println();
            writer.flush();
        }

        if (selection.matches("y")) {
            String newName = JOptionPane.showInputDialog(null, "What is the new name of " +
                    "the store?", "Edit Store", JOptionPane.QUESTION_MESSAGE);
            if (newName == null) {
                return;
            }
            synchronized (obj) {
                writer.write(newName);
                writer.println();
                writer.flush();
            }
        }


        //TODO this bs

        selection = (String) JOptionPane.showInputDialog(null, "Would you like to change a " +
                "product?", "Edit Store", JOptionPane.QUESTION_MESSAGE, null, yesOrNo, null);
        if (selection == null) {
            return;
        }
        if (selection.equals("Yes")) {
            selection = "y";
        }
        if (selection.equals("No")) {
            selection = "n";
        }

        synchronized (obj) {
            writer.write(selection);
            writer.println();
            writer.flush();
        }

        if (selection.equals("y")) {
//            boolean productExists = false;
            String product = null;
            ArrayList<String> bruh = new ArrayList<>();
            String dispLine = reader.readLine();

            while (dispLine != null) {
                if (dispLine.matches("a")) {
                    break;
                }
                if (dispLine.matches("no")) {

                } else {
                    String[] format = dispLine.split(";");
                    bruh.add(format[1]);
                }
                dispLine = reader.readLine();
            }
            product = (String) JOptionPane.showInputDialog(null, "Which product?", "Edit Product", JOptionPane.QUESTION_MESSAGE,
                    null, bruh.toArray(), null); //doesnt get here

            synchronized (obj) {
                writer.write(product);
                writer.println();
                writer.flush();
            }

//                String ans = reader.readLine();
//                if (ans.matches("y")) {
//                    break;
//                } else {
//                    JOptionPane.showMessageDialog(null, "That product does not exist!",
//                            "Error", JOptionPane.ERROR_MESSAGE);
//                }

        }

        if (selection.matches("y")) {
            boolean repeat = false;
            String quant = null;
            String price = null;
            String nProduct = JOptionPane.showInputDialog(null, "What is the new name?",
                    "Edit Product", JOptionPane.QUESTION_MESSAGE);
            if (nProduct == null) {
                return;
            }

            String desc = JOptionPane.showInputDialog(null, "What is the new description?",
                    "Edit Product", JOptionPane.QUESTION_MESSAGE);
            if (desc == null) {
                return;
            }

            int quantInt = 0;
            do {
                quant = (JOptionPane.showInputDialog(null, "How many " +
                        "would you like to sell?", "Edit Product", JOptionPane.QUESTION_MESSAGE));
                try {
                    quantInt = Integer.parseInt(quant);
                } catch (NumberFormatException e) {
                    repeat = true;
                    JOptionPane.showMessageDialog(null, "Please enter a valid integer!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
                if (quant == null) {
                    return;
                }
                if (quantInt <= 0) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid integer!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    repeat = true;
                } else {
                    repeat = false;
                }
            } while (repeat);

            int priceInt = 0;
            do {
                price = (JOptionPane.showInputDialog(null, "What is the " +
                        "price?", "Edit Product", JOptionPane.QUESTION_MESSAGE));
                try {
                    priceInt = Integer.parseInt(price);
                } catch (NumberFormatException e) {
                    repeat = true;
                    JOptionPane.showMessageDialog(null, "Please enter a valid integer!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
                if (price == null) {
                    return;
                }
                if (priceInt <= 0) {
                    JOptionPane.showMessageDialog(null, "Please enter a valid integer!",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    repeat = true;
                } else {
                    repeat = false;
                }
            } while (repeat);

            synchronized (obj) {
                writer.write(nProduct);
                writer.println();
                writer.flush();
                writer.write(desc);
                writer.println();
                writer.flush();
                writer.write(quant);
                writer.println();
                writer.flush();
                writer.write(price);
                writer.println();
                writer.flush();
            }
        }


        selection = (String) JOptionPane.showInputDialog(null, "Would you like to delete a " +
                "product?", "Edit Store", JOptionPane.QUESTION_MESSAGE, null, yesOrNo, null);

        if (selection == null) {
            return;
        }
        if (selection.equals("Yes")) {
            selection = "y";
        }
        if (selection.equals("No")) {
            selection = "n";
        }

        synchronized (obj) {
            writer.write(selection);
            writer.println();
            writer.flush();
        }

        if (selection.equals("y")) {
//            boolean productExists = false;
            String product = null;
            ArrayList<String> idk = new ArrayList<>();
            product = (String) JOptionPane.showInputDialog(null,
                    "Which item would you like to delete?", "Edit Store", JOptionPane.WARNING_MESSAGE, null, idk.toArray(), JOptionPane.INFORMATION_MESSAGE);
            synchronized (obj) {
                writer.write(product);
                writer.println();
                writer.flush();
            }

        }
    }


    public void deleteStore(String[] accountInfo, BufferedReader reader,
                            PrintWriter writer, String username) throws IOException {
        writer.write(username);
        writer.println();
        writer.flush();
        boolean storeExists = false;
        ArrayList<String> display = new ArrayList<>();
        do {
            String disp = reader.readLine();
            display.add(disp);
            String ans = reader.readLine();
            if (ans.matches("a")) {
                break;
            }
        } while (true);

        for (int i = 0; i < display.size(); i++) {
            display.set(i, display.get(i).substring(0, display.get(i).indexOf(";")));
        }


        HashSet<String> hset = new HashSet<String>(display);

        writer.write(String.valueOf(storeExists));
        writer.println();
        writer.flush();
        String store = null;


        store = (String) JOptionPane.showInputDialog(null, "Which store would you like to Delete?", "Delete Store",
                JOptionPane.QUESTION_MESSAGE, null, hset.toArray(), null);

        //System.out.println(store);

        if (store == null) {
            JOptionPane.showMessageDialog(null, "You currently have no stores!",
                    "Delete Store", JOptionPane.ERROR_MESSAGE);
            return;
        }

        synchronized (obj) {
            writer.write(store);
            writer.println();
            writer.flush();
        }


        String result = reader.readLine();

        JOptionPane.showMessageDialog(null, "Store has been deleted!",
                "Delete Store", JOptionPane.INFORMATION_MESSAGE);
    }


    public void addStore(String[] accountInfo, BufferedReader reader,
                         PrintWriter writer, String username) throws IOException {
        String[] importOrCreate = {"Import", "Create"};

        String storeName = JOptionPane.showInputDialog(null, "What would you like to name " +
                "your store?", "Add Store", JOptionPane.QUESTION_MESSAGE);

        if (storeName == null) {
            return;
        }

        synchronized (obj) {
            writer.write(username);
            writer.println();
            writer.flush();
            writer.write(storeName);
            writer.println();
            writer.flush();
        }

        String importQuestion = (String) JOptionPane.showInputDialog(null,
                "Would you like to import a store, or create from scratch?", "Add Store",
                JOptionPane.QUESTION_MESSAGE, null, importOrCreate, null);

        if (importQuestion == null) {
            return;
        }
        if (importQuestion.equals(importOrCreate[0])) {
            importQuestion = "1";
        }
        if (importQuestion.equals(importOrCreate[1])) {
            importQuestion = "2";
        }
        synchronized (obj) {
            writer.write(importQuestion);
            writer.println();
            writer.flush();
        }

        boolean addAnother = false;
        if (importQuestion.matches("1.|1")) {
            String message = reader.readLine();
            if (message.matches("Successfully imported store")) {
                JOptionPane.showMessageDialog(null, "Successfully imported store",
                        "Successful Import", JOptionPane.INFORMATION_MESSAGE);
            } else if (message.matches("Error importing the file")) {
                JOptionPane.showMessageDialog(null, "Error importing the file, make sure " +
                                "it is in the correct format: product, description, quantity price",
                        "Error!", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (importQuestion.matches("2.|2")) {
            /*writer.write(String.valueOf(addAnother));
            writer.println();
            writer.flush();*/


            int count = 0;
            do {
                String[] yesOrNo = new String[]{"Yes", "No"};
                String response = (String) JOptionPane.showInputDialog(null, "Do you want to " +
                                "add a product?", "Add Store", JOptionPane.QUESTION_MESSAGE, null, yesOrNo,
                        null);

                if (response == null) {
                    return;
                }
                if (response.equals("Yes")) {
                    response = "y";
                }
                if (response.equals("No")) {
                    response = "n";
                    count = 0;
                }
                synchronized (obj) {
                    writer.write(response);
                    writer.println();
                    writer.flush();
                }
                String productName;
                String description;
                String strPrice;
                if (response.matches("n")) {
//                    addAnother = false;
//                    writer.write(String.valueOf(addAnother));
//                    writer.println();
//                    writer.flush();
                    break;
                } else {
                    productName = JOptionPane.showInputDialog(null, "What is the name of " +
                            "your product?", "Add Product", JOptionPane.QUESTION_MESSAGE);
                    if (productName == null) {
                        return;
                    }

                    description = JOptionPane.showInputDialog(null, "Please give a description " +
                            "of your product", "Add Product", JOptionPane.QUESTION_MESSAGE);
                    if (description == null) {
                        return;
                    }
                    int price = 0;
                    boolean repeat;
                    do {
                        repeat = false;
                        strPrice = (JOptionPane.showInputDialog(null,
                                "How much do you want to charge?", "Add Product",
                                JOptionPane.QUESTION_MESSAGE));
                        try {
                            price = Integer.parseInt(strPrice);
                        } catch (NumberFormatException e) {
                            repeat = true;
                            JOptionPane.showMessageDialog(null, "Please enter a valid integer!",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                        if (strPrice == null) {
                            return;
                        }
                        if (price <= 0) {
                            JOptionPane.showMessageDialog(null, "Please enter a valid integer!",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            repeat = true;
                        } else {
                            repeat = false;
                        }

                    } while (repeat);
                }
                //System.out.println("How many are available?");
                String strAva; //scanner.nextLine();
                int available = 0;
                boolean repeat;
                do {
                    repeat = false;
                    strAva = (JOptionPane.showInputDialog(null,
                            "How many are available?", "Add Product",
                            JOptionPane.QUESTION_MESSAGE));
                    try {
                        available = Integer.parseInt(strAva);
                    } catch (NumberFormatException e) {
                        repeat = true;
                        JOptionPane.showMessageDialog(null, "Please enter a valid integer!",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    if (strAva == null) {
                        return;
                    }
                    if (available <= 0) {
                        JOptionPane.showMessageDialog(null, "Please enter a valid integer!",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        repeat = true;
                    } else {
                        repeat = false;
                    }

                } while (repeat);
                synchronized (obj) {
                    writer.write(productName);
                    writer.println();
                    writer.flush();
                    writer.write(description);
                    writer.println();
                    writer.flush();
                    writer.write(strPrice);
                    writer.println();
                    writer.flush();
                    writer.write(strAva);
                    writer.println();
                    writer.flush();
                }
                addAnother = response.matches("y");
                if (addAnother) {
                    count = 1;
                }
                //System.out.println("Would you like to add another product?");
                //addAnother = scanner.nextLine().matches("y");
            } while (addAnother);
        }
    }

    public void viewMarketplace(ArrayList<String> storeList, String[] accountInfo,
                                PrintWriter writer, BufferedReader reader, int selection, String query) throws IOException {
        //initial params are scanner, null, accountInfo
        //begin serverside
        String accountType = accountInfo[0];
        if (selection == 2) {
            synchronized (obj) {
                writer.write(accountType);
                writer.println();
                writer.flush();
                writer.write("2");
                writer.println();
                writer.flush();
            }
        }
        ArrayList<String> marketContents = new ArrayList<>();
        for (String line = reader.readLine(); line != null && line.length() > 0; line =
                reader.readLine()) {
            marketContents.add(line);
        }
        if (storeList == null || storeList.size() == 0) { //if nothing is searched, display everything
            storeList = marketContents;
        }
        //System.out.println(storeList);
        //end serverside

        //send arraylist to client
        //probably have to do with individual strings and put the string into a string array
        //add all the readlines into an Arraylist called storeList
        //ArrayList<String> storeList = new ArrayList<>();
        String formatArray[] = new String[storeList.size()];
        for (int i = 0; i < storeList.size(); i++) {
            try {
                String[] display;
                display = storeList.get(i).split(";");
                String first = (i + 1 + ". " + display[0]);
                String second = (": " + display[1]);
                //System.out.printf(i + 1 + ". " + display[0]);
                //System.out.printf(": " + display[1]);
                String price = display[2];
                int priceF = Integer.parseInt(price);
                //System.out.printf(" - $" + NumberFormat.getNumberInstance(Locale.US).format(priceF) + "\n");
                String third = (" - $" + NumberFormat.getNumberInstance(Locale.US).format(priceF) + "\n");
                formatArray[i] = first + second + third;
            } catch (ArrayIndexOutOfBoundsException e) {
                return;
            }
        }
        //formatting done clientside

        String[] MarketArray = new String[storeList.size()];
        for (int j = 0; j < storeList.size(); j++) {
            MarketArray[j] = formatArray[j];
        }
        String searchSelect;
        do {
            searchSelect = (String) JOptionPane.showInputDialog(null, "Select Item", "Marketplace",
                    JOptionPane.QUESTION_MESSAGE, null, MarketArray, null);

            if (searchSelect == null) {
                return;
            }
        } while (searchSelect == null);

        String[] marketSplit = searchSelect.split(". ");
        int storeResponse = Integer.parseInt(marketSplit[0]);

        //System.out.println("Please select a product number. Product numbers are on the far left of the " +
        //"display");
        //int storeResponse = -1;
        do {
            try {
                //String storeSelect = scanner.nextLine();
                //storeResponse = Integer.parseInt(storeSelect);
                if (storeResponse < 1 || storeResponse > storeList.size()) {
                    //System.out.println("That is not a recognized product! Please select a product number.
                    //Product numbers are on the far left of the display");
                    JOptionPane.showMessageDialog(null, "That is not a recognized product! Please select a product number. " +
                            "Product numbers are on the far left of the display", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                //System.out.println("That is not a recognized product! Please select a product number. " +
                //"Product numbers are on the far left of the display");
                JOptionPane.showMessageDialog(null, "That is not a recognized product! Please select a product number. " +
                        "Product numbers are on the far left of the display", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        } while (storeResponse < 1 || storeResponse > storeList.size());
        //deal with viewProductInfo later

        viewProductInfo(storeList.get(storeResponse - 1), accountInfo, storeResponse - 1,
                storeList, marketContents, reader, writer, selection, storeList, query);
        return;
    }

    public void viewProductInfo(String s, String[] accountInfo, int index,
                                ArrayList<String> productsDisp, ArrayList<String> marketContents,
                                BufferedReader reader, PrintWriter writer, int selection,
                                ArrayList<String> displayList, String query) throws IOException {
        boolean purchased = false;
        ArrayList<String> seeking = new ArrayList<>();
        String[] infoStoreFile = new String[4];
        //have to do convertName method
        String productFilename = convertName(s).toLowerCase() + ".txt";
        synchronized (obj) {
            writer.write(productFilename);
            writer.println();
            writer.flush();
        }
        String username = accountInfo[1];
        synchronized (obj) {
            for (int i = 0; i < productsDisp.size(); i++) {
                writer.write(productsDisp.get(i));
                writer.println();
                writer.flush();
            }
        }
        synchronized (obj) {
            writer.println();
            writer.flush();
            writer.write(index);
            writer.println();
            writer.flush();
        }

        //should be done serverside

        //send availability and description to client

        String description = reader.readLine();
        String availability = reader.readLine();
        //start client display
        JOptionPane.showMessageDialog(null, description, "Description", JOptionPane.INFORMATION_MESSAGE);
        JOptionPane.showMessageDialog(null, "There are " + availability + " available.", "Availability", JOptionPane.INFORMATION_MESSAGE);
        //System.out.println(description);
        //System.out.println("There are " + availability + " available.");
        String browsingSelect = null;
        if (availability.equals("0")) {
            JOptionPane.showMessageDialog(null, "Sorry! This item is out of stock.", "OUT OF STOCK", JOptionPane.ERROR_MESSAGE);
            //System.out.println("Sorry! This item is out of stock. Press 2 to continue browsing.");
            browsingSelect = "Continue Browsing";
        } else {
            String[] PurchaseBrowsing = new String[2];
            PurchaseBrowsing[0] = "Purchase";
            PurchaseBrowsing[1] = "Continue Browsing";

            browsingSelect = (String) JOptionPane.showInputDialog(null, "What would you like to do?", "Marketplace",
                    JOptionPane.PLAIN_MESSAGE, null, PurchaseBrowsing, null);


            if (browsingSelect == null) {
                return;
            }
            //System.out.println("Press 1 to purchase. Press 2 to continue browsing.");
        }
        String choice = browsingSelect;
        do {
            if (choice.matches("Purchase")) {
                synchronized (obj) {
                    writer.write("true");
                    writer.println();
                    writer.flush();
                    writer.write(username);
                    writer.println();
                    writer.flush();
                }
                choice = "Purchase";
                JOptionPane.showMessageDialog(null, "Purchase Successful!", "Success!", JOptionPane.INFORMATION_MESSAGE);
                //return;
            } else if (choice.matches("Continue Browsing")) {
                synchronized (obj) {
                    writer.write("false");
                    writer.println();
                    writer.flush();
                    writer.write(username);
                    writer.println();
                    writer.flush();
                }
                //afterLogIn(scanner, accountInfo, writer, reader, 1, query);
                return;
            }
        } while (!choice.matches("Purchase|Continue Browsing"));
        afterLogIn(accountInfo, writer, reader, 3, null);
        return;
    }

    public void welcome(PrintWriter writer, BufferedReader reader) throws IOException {
        //determines whether user wants to create an account of log in
        //then calls appropriate methods. It loops until the user chooses
        //one of the above options
        String[] options = new String[2];
        options[0] = "Create an account";
        options[1] = "Log in";
        String choice = (String) JOptionPane.showInputDialog(null, "Hello! What would you " +
                        "like to do?", "Welcome", JOptionPane.QUESTION_MESSAGE,
                null, options, null);
        try {
            choice = choice.toLowerCase();
        } catch (NullPointerException e) {
            return;
        }
        if (choice.matches("create an account")) {
            choice = "1";
        } else if (choice.matches("log in")) {
            choice = "2";
        }
        //System.out.println("Hello! What would you like to do?\n1. Create an account\n2. Log in");
        //String choice = scanner.nextLine().toLowerCase();

        do {
            if (!choice.matches("1.|1|create an account|2.|2|log in")) {
                JOptionPane.showMessageDialog(null, "Please choose a valid action",
                        "Error", JOptionPane.ERROR_MESSAGE);
                choice = (String) JOptionPane.showInputDialog(null, "Hello! What would you " +
                                "like to do?", "Welcome", JOptionPane.QUESTION_MESSAGE,
                        null, options, null);
                choice = choice.toLowerCase();
                //System.out.println("Please choose a valid action.");
                //System.out.println("What would you like to do?\n1. Create an account\n2. Log in");
                //choice = scanner.nextLine();
            }
            if (choice.matches("1.|1|create an account")) {
                synchronized (obj) {
                    writer.write(choice);
                    writer.println();
                    writer.flush();
                }
                createAccount(writer, reader);
            } else if (choice.matches("2.|2|log in")) {
                synchronized (obj) {
                    writer.write(choice);
                    writer.println();
                    writer.flush();
                }
                String[] accountInfo = logIn(writer, reader);
                if (accountInfo == null) {
                    return;
                }
                afterLogIn(accountInfo, writer, reader, 2, null);
                return;
            }
        } while (!choice.matches("1.|1|create an account|2.|2|log in"));
    }

    public void run() {
        try {
            //below is connection to server
            Socket socket = new Socket("localhost", 4242);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            String response;

            Client client = new Client();
            client.welcome(writer, reader);

            //client.createAccount(scanner, writer, reader);
        } catch (Exception e) {
        }
    }

    public static void main(String[] args) {
        try {
            Client client = new Client();
            client.start();
            client.join();
            /*
            Client[] stockTraders = {new Client(),
                    new Client()};

            Client client1 = new Client();
            Client client2 = new Client();
            client1.start();
            client2.start();
            //client1.join();
            //client2.join();
             */
        } catch (Exception ex) {
            return;
        }
    }
}
