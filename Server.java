/**
 * Server
 * This class sends the correct data
 * to the client(s) and stores/reads 
 * necessary csv and txt files.
 *
 *
 * @author Vanamali Vemparala, Milan Dhawan, Elliot Bode, Stephen Kruse, lab sec 26
 *
 * @version December 12, 2022
 *
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.nio.channels.OverlappingFileLockException;
import java.util.*;

public class Server {

    public void saveAccount(String filename, String username, String password) {
        //writes a username and a password to a file separated by a comma
        //format ensures compatibility with findAccount method
        username = username.toLowerCase();
        try {
            FileOutputStream fos = new FileOutputStream(filename, true);
            PrintWriter pw = new PrintWriter(fos);
            pw.println(username + "," + password);
            pw.close();
            File f = new File(username + ".txt");
            f.createNewFile();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveEdited(String filename, ArrayList<String> overwrite, String newUsername,
                           String username, String acctType) {
        //writes a username and a password to a file separated by a comma
        //format ensures compatibility with findAccount method
        newUsername = newUsername.toLowerCase();
        try {
            FileOutputStream fos = new FileOutputStream(filename, false);
            PrintWriter pw = new PrintWriter(fos);
            for (int i = 0; i < overwrite.size(); i++) {
                pw.println(overwrite.get(i));
            }
            pw.close();
            File f = new File(newUsername + ".txt");
            f.createNewFile();
            File g = new File(username + ".txt");
            ArrayList<String> changeFile = readFile(username + ".txt");
            ArrayList<String> changeMarket = readFile("Marketplace.txt");
            ArrayList<String> changePurchases = readFile("customerPurchases.txt");
            if (acctType.matches("seller")) {
                for (int i = 0; i < changeFile.size(); i++) {
                    String[] temp = changeFile.get(i).split(";");
                    temp[3] = newUsername;
                    changeFile.set(i, temp[0] + ";" + temp[1] + ";" + temp[2] + ";" + temp[3]);
                }
                for (int i = 0; i < changeMarket.size(); i++) {
                    String[] temp = changeMarket.get(i).split(";");
                    if (temp[3].matches(username)) {
                        temp[3] = newUsername;
                        changeMarket.set(i, temp[0] + ";" + temp[1] + ";" + temp[2] + ";" + temp[3]);
                    }
                }
            }
            if (acctType.matches("customer")) {
                for (int i = 0; i < changePurchases.size(); i++) {
                    String[] temp = changePurchases.get(i).split(";");
                    if (temp[1].matches(username)) {
                        changePurchases.set(i, temp[0] + ";" + newUsername + ";" + temp[2] + ";" + temp[3]);
                    }
                }
            }
            g.delete();
            FileOutputStream newFos = new FileOutputStream(newUsername + ".txt", false);
            PrintWriter newPw = new PrintWriter(newFos);
            for (int i = 0; i < changeFile.size(); i++) {
                newPw.println(changeFile.get(i));
            }
            newPw.close();
            FileOutputStream marketFos = new FileOutputStream("Marketplace.txt", false);
            PrintWriter marketPw = new PrintWriter(marketFos);
            for (int i = 0; i < changeMarket.size(); i++) {
                marketPw.println(changeMarket.get(i));
            }
            marketPw.close();
            FileOutputStream customerFos = new FileOutputStream("customerPurchases.txt", false);
            PrintWriter customerPw = new PrintWriter(customerFos);
            for (int i = 0; i < changePurchases.size(); i++) {
                customerPw.println(changePurchases.get(i));
            }
            customerPw.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ArrayList<String> readFile(String filename) {
        /*
         * A readFile method. Can be used for all necessary file reading.
         * Returns an ArrayList with each line in the file read.
         * Used in "Accounts" class to verify unique username and that
         * password matches (called in findAccount method and logIn method)
         */
        ArrayList<String> usernames = new ArrayList<>();
        try {
            BufferedReader file = new BufferedReader(new FileReader(filename));
            StringBuffer inputBuffer = new StringBuffer();
            String line;
            while ((line = file.readLine()) != null) {
                usernames.add(line);
            }
            file.close();
        } catch (IOException e) {
        }
        return usernames;
    }

    public boolean findAccount(String filename, String username) {
        //if a username exists in a certain file, findAccount returns true
        //username must be first thing on file line an followed by a comma
        username.toLowerCase();
        ArrayList<String> usernames = readFile(filename);
        for (int i = 0; i < usernames.size(); i++) {
            String[] usernameExist = usernames.get(i).split(",");
            if (usernameExist[0].equals(username)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> searchProduct(String search) {
        ArrayList<String> products = readFile("Marketplace.txt");
        ArrayList<String> requested = new ArrayList<>();
        for (int i = 0; i < products.size(); i++) {
            String[] info = products.get(i).split(";");
            String storeName = info[0].toLowerCase();
            String productName = info[1].toLowerCase();
            search = search.toLowerCase();
            if (productName.contains(search) || storeName.contains(search)) {
                requested.add(products.get(i));
            }
        }
        if (requested == null) {
            return null;
        }
        return requested;
    }

    public void viewProductInfo(String productFilename, ArrayList<String> productsDisp,
                                int index, PrintWriter writer, ArrayList<String> marketContents,
                                BufferedReader reader) throws IOException {
        ArrayList<String> seeking = readFile(productFilename);
        String match = productsDisp.get(index);
        String[] displaySplit = match.split(";");
        String productName = displaySplit[1];
        String availability = "0";
        String description = null;
        String[] infoStoreFile = new String[4];
        int var = 0;
        for (int i = 0; i < seeking.size(); i++) {
            if (seeking.get(i).substring(0, seeking.get(i).indexOf(";")).toLowerCase().
                    matches(productName.toLowerCase())) {
                //need to verify that index of seeking matches the index of an overall arraylist
                infoStoreFile = seeking.get(i).split(";");
                availability = infoStoreFile[2];
                description = infoStoreFile[1];
                var = i;
                if (!productsDisp.get(index).matches(marketContents.get(index))) {
                    for (int j = 0; j < marketContents.size(); j++) {
                        if (marketContents.get(j).matches(productsDisp.get(index))) {
                            index = j;
                            break;
                        }
                    }
                }
            }
        }
        writer.write(description);
        writer.println();
        writer.flush();
        writer.write(availability);
        writer.println();
        writer.flush();
        String purchase = reader.readLine();
        if (purchase.matches("true")) {
            String username = reader.readLine();
            String customerFilename = username + ".txt";
            logPurchase(productFilename, customerFilename, index, username, var);
        } else if (purchase.matches("false")) {
            String username = reader.readLine();
            afterLogIn(writer, reader);

        }
    }

    public void logPurchase(String productInfoFile, String customerFilename, int index,
                            String customerName, int var) {
        ArrayList<String> product = readFile("Marketplace.txt");
        ArrayList<String> overwrite = readFile(productInfoFile);
        try {
            FileOutputStream fos = new FileOutputStream(customerFilename, true);
            PrintWriter pw = new PrintWriter(fos);
            String[] productInfo = product.get(index).split(";");
            pw.println(productInfo[0] + ";" + productInfo[1] + ";" + productInfo[2]);
            pw.close();
        } catch (FileNotFoundException e) {
        }
        //decrease availibility by one and write change to file
        try {
            FileOutputStream fos = new FileOutputStream(productInfoFile, false);
            PrintWriter pw = new PrintWriter(fos);
            String[] productInfo = overwrite.get(var).split(";");
            int availibility = 0;
            try {
                availibility = Integer.parseInt(productInfo[2]) - 1;
            } catch (NumberFormatException e) {
            }
            String[] overwriteSplit = overwrite.get(var).split(";");
            overwrite.set(var, overwriteSplit[0] + ";" + overwriteSplit[1] +
                    ";" + availibility + ";" + overwriteSplit[3]);
            for (int i = 0; i < overwrite.size(); i++) {
                pw.println(overwrite.get(i));
            }
            pw.close();
        } catch (FileNotFoundException e) {
        }
        try {
            FileOutputStream fos = new FileOutputStream("customerPurchases.txt", true);
            PrintWriter pw = new PrintWriter(fos);
            String[] productInfo = product.get(index).split(";");
            pw.println(productInfo[0] + ";" + customerName + ";" + productInfo[1] + ";" + productInfo[2]);
            pw.close();
        } catch (FileNotFoundException e) {
        }
    }

    public void exportPurchase(String username, BufferedReader reader, PrintWriter writer) {
        try {
            FileReader fr = new FileReader(username + ".txt");
            BufferedReader br = new BufferedReader(fr);
            FileWriter fw = new FileWriter(username + "Purchases.txt", false);
            while (true) {
                String s = br.readLine();
                if (s == null) {
                    fw.close();
                    break;
                }
                String[] exportSplit = s.split(";");
                fw.write(exportSplit[1] + " from " + exportSplit[0] + " for $" + exportSplit[2] + "\n");

            }

        } catch (IOException e) {
        }

        String exportMessage = "Purchase history exported to " + username + "Purchases.txt!";
        writer.write(exportMessage);
        writer.println();
        writer.flush();
        //System.out.println("Purchase History exported to " + username + "Purchases.txt!\n");
    }

    public void checkStats(String response, String customerName, PrintWriter writer, BufferedReader reader) throws IOException {
        String line;
        ArrayList<String> allPurchases = new ArrayList<>();
        ArrayList<String> customerPurchases = new ArrayList<>();
        // reads customerPurchases.txt and stores into arrayList
        try {
            FileReader fileReader = new FileReader("customerPurchases.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);


            line = bufferedReader.readLine();
            while (line != null) {
                allPurchases.add(line);
                String[] purchasesArray = line.split(";");
                if (purchasesArray[1].equals(customerName)) {
                    customerPurchases.add(line);
                }
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (IOException e) {
        }

        //sets to alphabetical order
        customerPurchases.sort(String::compareToIgnoreCase);
        allPurchases.sort(String::compareToIgnoreCase);

        ArrayList<String> justStoreNames = new ArrayList<>();
        ArrayList<String> justConsumerStoreNames = new ArrayList<>();

        for (int i = 0; i < allPurchases.size(); i++) {
            String[] separatedArray = allPurchases.get(i).split(";");
            justStoreNames.add(separatedArray[0]);
        }

        for (int i = 0; i < customerPurchases.size(); i++) {
            String[] separatedArray = customerPurchases.get(i).split(";");
            justConsumerStoreNames.add(separatedArray[0]);
        }

        Set<String> uniqueStoreNames = new HashSet<String>(justStoreNames);
        Object[] uniqueStoreNameArray = uniqueStoreNames.toArray();

        int totalCounter = 0;
        int consumerCounter = 0;

        ArrayList<String> finalPrint = new ArrayList<>();

        for (int i = 0; i < uniqueStoreNames.size(); i++) {
            for (int j = 0; j < justStoreNames.size(); j++) {
                if (justStoreNames.get(j).equals(uniqueStoreNameArray[i])) {
                    totalCounter++;
                }
            }
            finalPrint.add(uniqueStoreNameArray[i] + " - Total Purchases: " + totalCounter);
            totalCounter = 0;
        }

        // puts all the purchases organized by store in a list

        for (int i = 0; i < finalPrint.size(); i++) {
            String[] tempArray = finalPrint.get(i).split(" - ");
            for (int j = 0; j < justConsumerStoreNames.size(); j++) {
                if (tempArray[0].equals(justConsumerStoreNames.get(j))) {
                    consumerCounter++;
                }
            }
            finalPrint.set(i, finalPrint.get(i) + " - Your Purchases: " + consumerCounter);
            consumerCounter = 0;
        }

        // alphabetical order

        if (response.matches("1")) {
            finalPrint.sort(String::compareToIgnoreCase);
            for (int i = 0; i < finalPrint.size(); i++) {
                writer.write(finalPrint.get(i));
                writer.println();
                writer.flush();
            }
            writer.println();
            writer.flush();
        }

        // by total purchases

        if (response.matches("2")) {
            ArrayList<Integer> isolateTotalPurchases = new ArrayList<>();
            for (int i = 0; i < finalPrint.size(); i++) {
                String tempArray[] = finalPrint.get(i).split(" - ");
                String isolateNumber = tempArray[1].substring(tempArray[1].indexOf(": ") + 2, tempArray[1].length());
                isolateTotalPurchases.add(Integer.parseInt(isolateNumber));
            }

            Collections.sort(isolateTotalPurchases);
            Collections.reverse(isolateTotalPurchases);

            ArrayList<Integer> realUnqiue = new ArrayList<>();
            for (int i = 0; i < isolateTotalPurchases.size(); i++) {
                if (!(realUnqiue.contains(isolateTotalPurchases.get(i)))) {
                    realUnqiue.add(isolateTotalPurchases.get(i));
                }
            }

            ArrayList<String> sortedArrayList = new ArrayList<>();

            for (int i = 0; i < realUnqiue.size(); i++) {
                for (int j = 0; j < finalPrint.size(); j++) {
                    String[] tempArray = finalPrint.get(j).split(" - ");
                    int isolateNumber = Integer.parseInt(tempArray[1].substring(tempArray[1].indexOf(": ") + 2,
                            tempArray[1].length()));
                    if (isolateNumber == realUnqiue.get(i)) {
                        sortedArrayList.add(finalPrint.get(j));
                    }
                }
            }

            for (int i = 0; i < sortedArrayList.size(); i++) {
                writer.write(sortedArrayList.get(i));
                writer.println();
                writer.flush();
            }
            writer.println();
            writer.flush();
        }

        // by consumer purchases

        if (response.matches("3")) {
            ArrayList<Integer> isolateTotalPurchases = new ArrayList<>();
            for (int i = 0; i < finalPrint.size(); i++) {
                String tempArray[] = finalPrint.get(i).split(" - ");
                String isolateNumber = tempArray[2].substring(tempArray[2].indexOf(": ") + 2, tempArray[2].length());
                isolateTotalPurchases.add(Integer.parseInt(isolateNumber));
            }

            Collections.sort(isolateTotalPurchases);
            Collections.reverse(isolateTotalPurchases);

            ArrayList<Integer> realUnqiue = new ArrayList<>();
            for (int i = 0; i < isolateTotalPurchases.size(); i++) {
                if (!(realUnqiue.contains(isolateTotalPurchases.get(i)))) {
                    realUnqiue.add(isolateTotalPurchases.get(i));
                }
            }

            ArrayList<String> sortedArrayList = new ArrayList<>();

            for (int i = 0; i < realUnqiue.size(); i++) {
                for (int j = 0; j < finalPrint.size(); j++) {
                    String[] tempArray = finalPrint.get(j).split(" - ");
                    int isolateNumber = Integer.parseInt(tempArray[2].substring(tempArray[2].indexOf(": ") + 2,
                            tempArray[2].length()));
                    if (isolateNumber == realUnqiue.get(i)) {
                        sortedArrayList.add(finalPrint.get(j));
                    }
                }
            }

            for (int i = 0; i < sortedArrayList.size(); i++) {
                writer.write(sortedArrayList.get(i));
                writer.println();
                writer.flush();
            }
            writer.println();
            writer.flush();
        }
        afterLogIn(writer, reader);
    }

    public void editStore(BufferedReader reader, PrintWriter writer) throws IOException {
        String username = reader.readLine();
        ArrayList<String> readMarket = readFile("Marketplace.txt");
        ArrayList<String> readSeller = readFile(username + ".txt");
        ArrayList<String> readPurchases = readFile("customerPurchases.txt");
        for (int i = 0; i < readSeller.size(); i++) {
            writer.write(readSeller.get(i));
            writer.println();
            writer.flush();
            if (i + 1 == readSeller.size()) {
                writer.write("a");
                writer.println();
                writer.flush();
                break;
            } else {
                writer.write("this code sucks");
                writer.println();
                writer.flush();
            }
        }
//        boolean storeExists = Boolean.parseBoolean(reader.readLine());
//        boolean ownStore = Boolean.parseBoolean(reader.readLine());
        String store = null;
        store = reader.readLine();
        String newName = store;


//        do {
//            for (int i = 0; i < readMarket.size(); i++) {
//                if (readMarket.get(i).contains(store)) {
//                    storeExists = true;
//                    writer.write("y");
//                    writer.println();
//                    writer.flush();
//                    break;
//                }
//            }
//            for (int i = 0; i < readSeller.size(); i++) {
//                if (readSeller.get(i).contains(store)) {
//                    ownStore = true;
//                    writer.write("y");
//                    writer.println();
//                    writer.flush();
//                    break;
//                }
//            }
//            if (!storeExists) {
//                writer.write("There is no store with that name!");
//                writer.println();
//                writer.flush();
//            } else if (!ownStore) {
//                writer.write("You don't own that store!");
//                writer.println();
//                writer.flush();
//            }
//        } while (!storeExists && !ownStore);

        String selection = reader.readLine();
        if (selection.matches("y")) {
            newName = reader.readLine();
            try {
                File f = new File("Marketplace.txt");
                f.createNewFile();
                BufferedReader bfr = new BufferedReader(new FileReader(f));
                String file = "Marketplace.txt";
                ArrayList<String> info = new ArrayList<>();
                String toys = bfr.readLine();
                while (toys != null) {
                    info.add(toys);
                    toys = bfr.readLine();
                }

                for (int i = 0; i < readPurchases.size(); i++) {
                    String[] temp = readPurchases.get(i).split(";");
                    if (temp[0].matches(store)) {
                        readPurchases.set(i, newName + ";" + temp[1] + ";" + temp[2] + ";" + temp[3]);
                    }
                }

                FileOutputStream changeNameFos = new FileOutputStream("customerPurchases.txt", false);
                PrintWriter changeNamePw = new PrintWriter(changeNameFos);
                for (int i = 0; i < readPurchases.size(); i++) {
                    changeNamePw.println(readPurchases.get(i));
                }
                changeNamePw.close();

                for (int i = 0; i < info.size(); i++) {
                    if (info.get(i).toLowerCase().contains(store.toLowerCase())) {
                        String a = info.get(i);
                        a = a.replaceAll(store, newName);
                        info.set(i, a);
                    }
                }

                FileOutputStream fos = new FileOutputStream(file, false);
                PrintWriter pw = new PrintWriter(fos);


                for (int i = 0; i < info.size(); i++) {
                    pw.println(info.get(i));
                }

                pw.close();

                f = new File(username + ".txt");
                f.createNewFile();
                bfr = new BufferedReader(new FileReader(f));
                file = username + ".txt";
                info = new ArrayList<>();
                toys = bfr.readLine();
                while (toys != null) {
                    info.add(toys);
                    toys = bfr.readLine();
                }
                for (int i = 0; i < info.size(); i++) {
                    if (info.get(i).toLowerCase().contains(store.toLowerCase())) {
                        String a = info.get(i);
                        a = a.replaceAll(store, newName);
                        info.set(i, a);
                    }
                }

                fos = new FileOutputStream(file, false);
                pw = new PrintWriter(fos);


                for (int i = 0; i < info.size(); i++) {
                    pw.println(info.get(i));
                }

                pw.close();

                bfr = new BufferedReader(new FileReader(
                        store.toLowerCase().replaceAll(" ", "") + ".txt"));
                f = new File(newName.toLowerCase().replaceAll(" ", ""));
                file = newName.toLowerCase().replaceAll(" ", "") + ".txt";
                info = new ArrayList<>();
                toys = bfr.readLine();
                while (toys != null) {
                    info.add(toys);
                    toys = bfr.readLine();
                }

                fos = new FileOutputStream(file, false);
                pw = new PrintWriter(fos);

                for (int i = 0; i < info.size(); i++) {
                    pw.println(info.get(i));
                }

                pw.close();
            } catch (IOException e) {
            }
        }

        String product = null;
        selection = reader.readLine();
        if (selection.matches("y")) {
//            boolean productExists = false;
//            do {
            for (int i = 0; i < readSeller.size(); i++) {
                if (readSeller.get(i).contains(store)) {
                    writer.write(readSeller.get(i));
                    writer.println();
                    writer.flush();
                } else {
                    writer.write("no");
                    writer.println();
                    writer.flush();
                }
                if (i + 1 == readSeller.size()) {
                    writer.write("a");
                    writer.println();
                    writer.flush();
                    break;
                }
//                    } else {
//                        writer.write("this code sucks");
//                        writer.println();
//                        writer.flush();
//                    }
            }
            product = reader.readLine();
            for (int i = 0; i < readMarket.size(); i++) {
                if (readMarket.get(i).contains(product)) {
//                        productExists = true;
                    writer.write("y");
                    writer.println();
                    writer.flush();
                    break;
                }
            }
//                if (!productExists) {
//                    writer.write("There is no product with that name!");
//                    writer.println();
//                    writer.flush();
//                }
//            } while (!productExists);
        }

        if (selection.matches("y")) {
            String nProduct = reader.readLine();
            String desc = reader.readLine();
            String quant = (reader.readLine());
            String price = (reader.readLine());

            String newLine = nProduct + ";" + desc + ";" + quant + ";" + price;
            String mk = nProduct + ";" + price + ";" + username;

            try {
                File f = new File("Marketplace.txt");
                f.createNewFile();
                BufferedReader bfr = new BufferedReader(new FileReader(f));
                String file = "Marketplace.txt";
                ArrayList<String> info = new ArrayList<>();
                String toys = bfr.readLine();
                while (toys != null) {
                    info.add(toys);
                    toys = bfr.readLine();
                }
                for (int i = 0; i < info.size(); i++) {
                    if (info.get(i).toLowerCase().contains(product.toLowerCase())) {
                        info.set(i, newName + ";" + mk);
                    }
                }

                FileOutputStream fos = new FileOutputStream(file, false);
                PrintWriter pw = new PrintWriter(fos);


                for (int i = 0; i < info.size(); i++) {
                    pw.println(info.get(i));
                }

                pw.close();

                f = new File(username + ".txt");
                f.createNewFile();
                bfr = new BufferedReader(new FileReader(f));
                file = username + ".txt";
                info = new ArrayList<>();
                toys = bfr.readLine();
                while (toys != null) {
                    info.add(toys);
                    toys = bfr.readLine();
                }
                for (int i = 0; i < info.size(); i++) {
                    if (info.get(i).toLowerCase().contains(product.toLowerCase())) {
                        info.set(i, newName + ";" + mk);
                    }
                }

                fos = new FileOutputStream(file, false);
                pw = new PrintWriter(fos);


                for (int i = 0; i < info.size(); i++) {
                    pw.println(info.get(i));
                }

                pw.close();

                f = new File(newName.toLowerCase().replaceAll(" ", "") + ".txt");
                f.createNewFile();
                bfr = new BufferedReader(new FileReader(f));
                file = newName.toLowerCase().replaceAll(" ", "") + ".txt";
                info = new ArrayList<>();
                toys = bfr.readLine();
                while (toys != null) {
                    info.add(toys);
                    toys = bfr.readLine();
                }
                for (int i = 0; i < info.size(); i++) {
                    if (info.get(i).toLowerCase().contains(product.toLowerCase())) {
                        info.set(i, newLine);
                    }
                }

                fos = new FileOutputStream(file, false);
                pw = new PrintWriter(fos);


                for (int i = 0; i < info.size(); i++) {
                    pw.println(info.get(i));
                }

                pw.close();


            } catch (IOException e) {
            }
        }

        selection = reader.readLine();

        if (selection.matches("y")) {
            boolean productExists = false;
            product = null;

            do {
                product = reader.readLine();

                for (int i = 0; i < readMarket.size(); i++) {
                    if (readMarket.get(i).contains(product)) {
                        productExists = true;
                        writer.write("a");
                        writer.println();
                        writer.flush();
                        break;
                    }
                }
                if (!productExists) {
                    writer.write("There is no product with that name!");
                    writer.println();
                    writer.flush();
                }
            } while (!productExists);

            try {
                File f = new File("Marketplace.txt");
                f.createNewFile();
                BufferedReader bfr = new BufferedReader(new FileReader(f));
                String file = "Marketplace.txt";
                ArrayList<String> info = new ArrayList<>();
                String toys = bfr.readLine();
                while (toys != null) {
                    info.add(toys);
                    toys = bfr.readLine();
                }
                for (int i = 0; i < info.size(); i++) {
                    if (info.get(i).toLowerCase().contains(product.toLowerCase())) {
                        info.remove(i);
                        break;
                    }
                }

                FileOutputStream fos = new FileOutputStream(file, false);
                PrintWriter pw = new PrintWriter(fos);


                for (int i = 0; i < info.size(); i++) {
                    pw.println(info.get(i));
                }

                pw.close();

                f = new File(username + ".txt");
                f.createNewFile();
                bfr = new BufferedReader(new FileReader(f));
                file = username + ".txt";
                info = new ArrayList<>();
                toys = bfr.readLine();
                while (toys != null) {
                    info.add(toys);
                    toys = bfr.readLine();
                }
                for (int i = 0; i < info.size(); i++) {
                    if (info.get(i).toLowerCase().contains(product.toLowerCase())) {
                        info.remove(i);
                        break;
                    }
                }

                fos = new FileOutputStream(file, false);
                pw = new PrintWriter(fos);


                for (int i = 0; i < info.size(); i++) {
                    pw.println(info.get(i));
                }

                pw.close();

                f = new File(newName.toLowerCase().replaceAll(" ", "") + ".txt");
                f.createNewFile();
                bfr = new BufferedReader(new FileReader(f));
                file = newName.toLowerCase().replaceAll(" ", "") + ".txt";
                info = new ArrayList<>();
                toys = bfr.readLine();
                while (toys != null) {
                    info.add(toys);
                    toys = bfr.readLine();
                }
                for (int i = 0; i < info.size(); i++) {
                    if (info.get(i).toLowerCase().contains(product.toLowerCase())) {
                        info.remove(i);
                        break;
                    }
                }

                fos = new FileOutputStream(file, false);
                pw = new PrintWriter(fos);


                for (int i = 0; i < info.size(); i++) {
                    pw.println(info.get(i));
                }

                pw.close();


            } catch (IOException e) {
            }
        }

    }


    public void addStore(BufferedReader reader, PrintWriter writer) throws IOException {
        //System.out.println("HELLO");
        String username = reader.readLine();
        String storeName = reader.readLine();
        Seller seller = new Seller(username.toLowerCase());
        Stores store = new Stores(storeName);
        seller.addStore(store);
        String num = reader.readLine();
        if (num.matches("1.|1")) {
            importStore(username, storeName, writer, reader);
        } else if (num.matches("2.|2")) {
            //boolean addAnother = Boolean.parseBoolean(reader.readLine());
            do {
                String response = reader.readLine();
                if (response.matches("n")) {
                    break;
                }
                String proName = reader.readLine();
                String desc = reader.readLine();
                int price = Integer.parseInt(reader.readLine());
                int ava = Integer.parseInt(reader.readLine());
                Product product = new Product(proName, desc, price, ava);
                store.addProduct(product);
            } while (true);
            ArrayList<Product> products = store.getProducts();
            ArrayList<Stores> stores = seller.getStores();
            storeWrite(products, storeName);
            sellerWrite(stores, username);
        }
    }

    public void deleteStore(BufferedReader reader, PrintWriter writer) throws IOException {
        String username = reader.readLine();
        ArrayList<String> readMarket = readFile("Marketplace.txt");
        ArrayList<String> readSeller = readFile(username + ".txt");
        for (int i = 0; i < readSeller.size(); i++) {
            writer.write(readSeller.get(i));
            writer.println();
            writer.flush();
            if (i + 1 == readSeller.size()) {
                writer.write("a");
                writer.println();
                writer.flush();
                break;
            } else {
                writer.write("this code sucks");
                writer.println();
                writer.flush();
            }
        }
        writer.println();
        writer.flush();
        writer.write(String.valueOf(readSeller));
        writer.println();
        writer.flush();

        boolean storeExists = Boolean.parseBoolean(reader.readLine());
        String store;
        store = reader.readLine();
        //System.out.println(store);

        if (store == null) {
            return;
        }

        try {
            File f = new File("Marketplace.txt");
            f.createNewFile();
            BufferedReader bfr = new BufferedReader(new FileReader(f));
            String file = "Marketplace.txt";
            ArrayList<String> info = new ArrayList<>();
            String toys = bfr.readLine();
            while (toys != null) {
                info.add(toys);
                toys = bfr.readLine();
            }
            for (int i = 0; i < info.size(); i++) {
                if (info.get(i).toLowerCase().contains(store.toLowerCase())) {
                    info.remove(i);
                    i--;
                }
            }

            FileOutputStream fos = new FileOutputStream(file, false);
            PrintWriter pw = new PrintWriter(fos);


            for (int i = 0; i < info.size(); i++) {
                pw.println(info.get(i));
            }

            pw.close();

            f = new File(username + ".txt");
            f.createNewFile();
            bfr = new BufferedReader(new FileReader(f));
            file = username + ".txt";
            info = new ArrayList<>();
            toys = bfr.readLine();
            while (toys != null) {
                info.add(toys);
                toys = bfr.readLine();
            }
            for (int i = 0; i < info.size(); i++) {
                if (info.get(i).toLowerCase().contains(store.toLowerCase())) {
                    info.remove(i);
                    i--;
                }
            }

            fos = new FileOutputStream(file, false);
            pw = new PrintWriter(fos);


            for (int i = 0; i < info.size(); i++) {
                pw.println(info.get(i));
            }

            pw.close();

            File myObj = new File(store.toLowerCase().replaceAll(" ", "")
                    + ".txt");
            if (myObj.delete()) {
                writer.write("Store has been deleted");
                writer.println();
                writer.flush();
            }

        } catch (IOException e) {
        }
    }

    public void checkSellerStats(BufferedReader reader, PrintWriter writer, String sellerName) throws IOException {
        String line;
        ArrayList<String> customerPurchases = new ArrayList<>();
        ArrayList<String> storesOwned = new ArrayList<>();
        sellerName = sellerName.toLowerCase();
        // reads customerPurchases.txt and stores into an ArrayList
        try {
            FileReader fileReader = new FileReader("customerPurchases.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            line = bufferedReader.readLine();
            while (line != null) {
                customerPurchases.add(line);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (IOException e) {
        }
        // puts stores owned by the seller into an ArrayList
        try {
            FileReader fileReader = new FileReader("Marketplace.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            line = bufferedReader.readLine();
            while (line != null) {
                String[] tempArray = line.split(";");
                if (sellerName.equals(tempArray[3])) {
                    storesOwned.add(tempArray[0]);
                }
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (IOException e) {
        }

        // stores all the stores owned into unique arrayList (ensures no doubles)
        ArrayList<String> uniqueStoresOwned = new ArrayList<>();
        for (int i = 0; i < storesOwned.size(); i++) {
            if (!(uniqueStoresOwned.contains(storesOwned.get(i)))) {
                uniqueStoresOwned.add(storesOwned.get(i));
            }
        }
        ArrayList<String> specificTransactions = new ArrayList<>();

        // stores any transactions made by customers into an arrayList
        for (int i = 0; i < customerPurchases.size(); i++) {
            String[] tempArray = customerPurchases.get(i).split(";");
            String storeName = tempArray[0];
            for (int j = 0; j < uniqueStoresOwned.size(); j++) {
                if (Objects.equals(storeName, uniqueStoresOwned.get(j))) {
                    specificTransactions.add(customerPurchases.get(i));
                }
            }
        }

        String response = reader.readLine();


        // ranks by customer purchase
        if (response.matches("1")) {
            ArrayList<String> customers = new ArrayList<>();

            for (int i = 0; i < specificTransactions.size(); i++) {
                String[] tempArray = specificTransactions.get(i).split(";");
                customers.add(tempArray[1]);
            }
            customers.sort(String::compareToIgnoreCase);

            ArrayList<String> uniqueCustomers = new ArrayList<>();
            for (int i = 0; i < customers.size(); i++) {
                if (!(uniqueCustomers.contains(customers.get(i)))) {
                    uniqueCustomers.add(customers.get(i));
                }
            }

            ArrayList<String> nonSorted = new ArrayList<>();


            for (int i = 0; i < uniqueCustomers.size(); i++) {
                String customer = uniqueCustomers.get(i);
                int purchases = 0;
                for (int j = 0; j < specificTransactions.size(); j++) {
                    String[] tempArray = specificTransactions.get(j).split(";");
                    if (tempArray[1].equals(customer)) {
                        purchases++;
                    }
                }
                nonSorted.add(customer + " - Purchases: " + purchases);
                purchases = 0;
            }

            ArrayList<Integer> purchaseTally = new ArrayList<>();

            for (int i = 0; i < nonSorted.size(); i++) {
                String[] tempArray = nonSorted.get(i).split(": ");
                purchaseTally.add(Integer.parseInt(tempArray[1]));
            }

            Collections.sort(purchaseTally);
            Collections.reverse(purchaseTally);

            ArrayList<Integer> sortedPurchaseTally = new ArrayList<>();

            for (int i = 0; i < purchaseTally.size(); i++) {
                if (!(sortedPurchaseTally.contains(purchaseTally.get(i)))) {
                    sortedPurchaseTally.add(purchaseTally.get(i));
                }
            }

            ArrayList<String> finalCustomerRanked = new ArrayList<>();
            finalCustomerRanked.clear();
            int counter = 0;

            for (int i = 0; i < sortedPurchaseTally.size(); i++) {
                for (int j = 0; j < nonSorted.size(); j++) {
                    String[] tempArray = nonSorted.get(j).split("Purchases: ");
                    if (Integer.parseInt(tempArray[1]) == sortedPurchaseTally.get(i)) {
                        counter++;
                        finalCustomerRanked.add(counter + ". " + nonSorted.get(j));
                    }
                }
            }
            if (counter == 0) {
                finalCustomerRanked.set(0, "No purchases!");
                //JOptionPane.showMessageDialog(null, "No customers have purchased any of your " +
                //"products yet", "Ranked By Customers", JOptionPane.INFORMATION_MESSAGE);
            }
            for (int i = 0; i < finalCustomerRanked.size(); i++) {
                writer.write(finalCustomerRanked.get(i));
                writer.println();
                writer.flush();
            }
            writer.println();
            writer.flush();
            //need to send finalCustomerRanked list
            //JOptionPane.showMessageDialog(null, finalCustomerRanked, "Ranked By Customers",
            //JOptionPane.INFORMATION_MESSAGE);
        }

        // ranks by product sales
        if (response.matches("2")) {
            ArrayList<String> products = new ArrayList<>();

            for (int i = 0; i < specificTransactions.size(); i++) {
                String[] tempArray = specificTransactions.get(i).split(";");
                products.add(tempArray[2]);
            }
            products.sort(String::compareToIgnoreCase);

            ArrayList<String> uniqueProducts = new ArrayList<>();
            for (int i = 0; i < products.size(); i++) {
                if (!(uniqueProducts.contains(products.get(i)))) {
                    uniqueProducts.add(products.get(i));
                }
            }


            ArrayList<String> nonSorted = new ArrayList<>();


            for (int i = 0; i < uniqueProducts.size(); i++) {
                String product = uniqueProducts.get(i);
                int purchases = 0;
                for (int j = 0; j < specificTransactions.size(); j++) {
                    String[] tempArray = specificTransactions.get(j).split(";");
                    if (tempArray[2].equals(product)) {
                        purchases++;
                    }
                }
                nonSorted.add(product + " - Purchases: " + purchases);
                purchases = 0;
            }

            ArrayList<Integer> purchaseTally = new ArrayList<>();

            for (int i = 0; i < nonSorted.size(); i++) {
                String[] tempArray = nonSorted.get(i).split(": ");
                purchaseTally.add(Integer.parseInt(tempArray[1]));
            }

            Collections.sort(purchaseTally);
            Collections.reverse(purchaseTally);

            ArrayList<Integer> sortedPurchaseTally = new ArrayList<>();

            for (int i = 0; i < purchaseTally.size(); i++) {
                if (!(sortedPurchaseTally.contains(purchaseTally.get(i)))) {
                    sortedPurchaseTally.add(purchaseTally.get(i));
                }
            }

            int counter = 0;
            ArrayList<String> finalProductsRanked = new ArrayList<>();
            finalProductsRanked.clear();
            for (int i = 0; i < sortedPurchaseTally.size(); i++) {
                for (int j = 0; j < nonSorted.size(); j++) {
                    String[] tempArray = nonSorted.get(j).split("Purchases: ");
                    if (Integer.parseInt(tempArray[1]) == sortedPurchaseTally.get(i)) {
                        counter++;
                        finalProductsRanked.add(counter + ". " + nonSorted.get(j));
                    }
                }
            }
            if (counter == 0) {
                finalProductsRanked.set(0, "No purchases!");
                //JOptionPane.showMessageDialog(null, "No customers have purchased any of your " +
                //  "products yet", "Ranked By Customers", JOptionPane.INFORMATION_MESSAGE);
            }
            //need to send finalProductsRanked list
            for (int i = 0; i < finalProductsRanked.size(); i++) {
                writer.write(finalProductsRanked.get(i));
                writer.println();
                writer.flush();
            }
            writer.println();
            writer.flush();
            //JOptionPane.showMessageDialog(null, finalProductsRanked, "Ranked By Customers",
            //JOptionPane.INFORMATION_MESSAGE);
        }
        if (response.matches("3")) {
            String storeExportName = reader.readLine();
            if (storeExportName != null) {
                exportStore(storeExportName, writer);
            }
            //send no lists, just message
        }
        afterLogIn(writer, reader);
    }

    public void exportStore(String storeName, PrintWriter writer) {
        try {
            FileReader fr = new FileReader(storeName + ".txt");
            BufferedReader br = new BufferedReader(fr);
            FileWriter fw = new FileWriter(storeName + "Products.txt", false);
            fw.write("Product, Description, Price, Quantity");
            while (true) {
                fw.write("\n");
                String s = br.readLine();
                if (s == null) {
                    fw.close();
                    break;
                }
                String[] exportSplit = s.split(";");
                fw.write(exportSplit[0] + ", " + exportSplit[1] + ", " + exportSplit[2]
                        + ", " + exportSplit[3]);

            }
            String exportMessage = "Purchase history exported to " + storeName + "Products.txt!";
            writer.write(exportMessage);
            writer.println();
            writer.flush();
            //JOptionPane.showMessageDialog(null, "Purchase History exported to " + storeName +
            //"Products.txt!\n", "Purchase History Exported", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            writer.write("Not owned!");
            writer.println();
            writer.flush();
            //JOptionPane.showMessageDialog(null, "That is not a store you own!",
            //"Purchase History Exported", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void importStore(String username, String storeName, PrintWriter writer, BufferedReader reader) {
        String formatStore;
        formatStore = storeName.replaceAll(" ", "");
        formatStore = formatStore.toLowerCase();
        try {
            FileReader fr = new FileReader(formatStore + "import.txt");
            BufferedReader bfr = new BufferedReader(fr);
            FileWriter gg = new FileWriter(formatStore + ".txt", false);
            File w = new File("temp.txt");
            FileWriter fw = new FileWriter(w);
            while (true) {
                String s = bfr.readLine();
                if (s == null) {
                    gg.close();
                    fw.close();
                    break;
                }
                String[] importSplit = s.split(",");
                gg.write(importSplit[0] + ";" + importSplit[1] + ";" + importSplit[2] + ";" +
                        importSplit[3] + "\n");
                fw.write(storeName + ";" + importSplit[0] + ";" + importSplit[3] + ";sell\n");
            }

            try {
                File f = new File(username + ".txt");
                f.createNewFile();
                String file = username + ".txt";
                FileOutputStream fos = new FileOutputStream(file, true);
                PrintWriter pw = new PrintWriter(fos);
                BufferedReader bfr2 = new BufferedReader(new FileReader(w));
                String line = bfr2.readLine();
                ArrayList<String> info = new ArrayList<>();
                while (line != null) {
                    info.add(line);
                    line = bfr2.readLine();
                }

                for (int i = 0; i < info.size(); i++) {
                    pw.println(info.get(i));
                }

                pw.close();
            } catch (IOException e) {
            }

            try {
                File f = new File("Marketplace.txt");
                f.createNewFile();
                String file = "Marketplace.txt";
                FileOutputStream fos = new FileOutputStream(file, true);
                PrintWriter pw = new PrintWriter(fos);
                BufferedReader bfr2 = new BufferedReader(new FileReader(w));
                String line = bfr2.readLine();
                ArrayList<String> info = new ArrayList<>();
                while (line != null) {
                    info.add(line);
                    line = bfr2.readLine();
                }

                for (int i = 0; i < info.size(); i++) {
                    pw.println(info.get(i));
                }

                pw.close();
                pw.close();
            } catch (IOException e) {
            }

            writer.write("Successfully imported store");
            writer.println();
            writer.flush();
        } catch (IOException e) {
            writer.write("Error importing the file");
            writer.println();
            writer.flush();
        } catch (ArrayIndexOutOfBoundsException e) {
            writer.write("Error importing the file");
            writer.println();
            writer.flush();
        }
    }

    public void storeWrite(ArrayList<Product> products, String storeName) {
        try {
            String store = storeName.toLowerCase().replaceAll(" ", "");
            File f = new File(store + ".txt");
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f, false);
            PrintWriter pw = new PrintWriter(fos);
            for (Product product : products) {
                pw.println(product.getName() + ";" + product.getDescription() + ";"
                        + product.getQuantity() + ";" + product.getPrice());
            }
            pw.close();
        } catch (IOException e) {
        }
    }

    public void sellerWrite(ArrayList<Stores> stores, String sellerName) {
        try {
            File f = new File(sellerName + ".txt");
            f.createNewFile();
            String file = sellerName + ".txt";
            FileOutputStream fos = new FileOutputStream(file, true);
            PrintWriter pw = new PrintWriter(fos);
            for (Stores store : stores) {
                for (int j = 0; j < store.getProducts().size(); j++) {
                    pw.println(store.getName() + ";" + store.getProducts().get(j).getName() + ";" +
                            store.getProducts().get(j).getPrice() + ";" + sellerName);
                }
            }
            pw.close();
        } catch (IOException e) {
        }

        try {
            File f = new File("Marketplace.txt");
            f.createNewFile();
            String file = "Marketplace.txt";
            FileOutputStream fos = new FileOutputStream(file, true);
            PrintWriter pw = new PrintWriter(fos);
            for (Stores store : stores) {
                for (int j = 0; j < store.getProducts().size(); j++) {
                    pw.println(store.getName() + ";" + store.getProducts().get(j).getName() + ";" +
                            store.getProducts().get(j).getPrice() + ";" + sellerName);
                }
            }
            pw.close();
        } catch (IOException e) {
        }
    }

    public void afterLogIn(PrintWriter writer, BufferedReader reader) throws IOException {
        String acctType = reader.readLine();
        String choice = reader.readLine();
        int choiceInt = Integer.parseInt(choice);
        int index = -1;
        if (acctType.matches("seller")) {
            switch (choiceInt) {
                case 1:
                    ArrayList<String> overwrite = readFile(acctType + ".txt");
                    String username = reader.readLine();
                    String newUsername = reader.readLine();
                    for (int i = 0; i < overwrite.size(); i++) {
                        if (overwrite.get(i).substring(0, overwrite.get(i).indexOf(",")).matches(username)) {
                            index = i;
                        }
                    }
                    if (newUsername.matches(username)) {
                        writer.write("false");
                        writer.println();
                        writer.flush();
                        String password = reader.readLine();
                        newUsername = newUsername.toLowerCase();
                        overwrite.set(index, newUsername + "," + password);
                        saveEdited("seller.txt", overwrite, newUsername, username, acctType);
                        if (acctType.matches("seller")) {

                        }
                        username = newUsername;
                        //overwrite the file now with the password (probably use method)
                    } else if (!findAccount("customer.txt", newUsername) || !findAccount("seller.txt",
                            newUsername)) {
                        writer.write("false");
                        writer.println();
                        writer.flush();
                        //String password = "password";
                        String password = reader.readLine();
                        newUsername = newUsername.toLowerCase();
                        overwrite.set(index, newUsername + "," + password);
                        saveEdited("seller.txt", overwrite, newUsername, username, acctType);
                    } else {
                        writer.write("true");
                        writer.println();
                        writer.flush();
                    }
                    //code for case 1 seller
                    afterLogIn(writer, reader);
                    break;
                case 2:
                    editStore(reader, writer);
                    afterLogIn(writer, reader);
                    break;
                case 3:
                    addStore(reader, writer);
                    afterLogIn(writer, reader);
                    break;
                case 4:
                    deleteStore(reader, writer);
                    afterLogIn(writer, reader);
                    //code for case 4 seller
                    break;
                case 5:
                    username = reader.readLine();
                    checkSellerStats(reader, writer, username);
                    afterLogIn(writer, reader);
                    //code for case 5 seller
                    break;
                case 6:
                    break;
            }
        } else if (acctType.matches("customer")) {
            switch (choiceInt) {
                case 1:
                    ArrayList<String> overwrite = readFile(acctType + ".txt");
                    String username = reader.readLine();
                    String newUsername = reader.readLine();
                    for (int i = 0; i < overwrite.size(); i++) {
                        if (overwrite.get(i).substring(0, overwrite.get(i).indexOf(",")).matches(username)) {
                            index = i;
                        }
                    }
                    if (newUsername.matches(username)) {
                        writer.write("false");
                        writer.println();
                        writer.flush();
                        String password = reader.readLine();
                        newUsername = newUsername.toLowerCase();
                        overwrite.set(index, newUsername + "," + password);
                        saveEdited("customer.txt", overwrite, newUsername, username, acctType);
                        //overwrite the file now with the password (probably use method)
                    } else if (!findAccount("customer.txt", newUsername) || !findAccount("seller.txt",
                            newUsername)) {
                        writer.write("false");
                        writer.println();
                        writer.flush();
                        //String password = "password";
                        String password = reader.readLine();
                        newUsername = newUsername.toLowerCase();
                        overwrite.set(index, newUsername + "," + password);
                        saveEdited("customer.txt", overwrite, newUsername, username, acctType);
                    } else {
                        writer.write("true");
                        writer.println();
                        writer.flush();
                    }
                    //code for case 1 customer
                    afterLogIn(writer, reader);
                    break;
                case 2:
                    String account = reader.readLine();
                    String viewMarketplace = reader.readLine();
                    ArrayList<String> marketContents = readFile("Marketplace.txt");
                    if (marketContents.size() > 0) {
                        for (int i = 0; i < marketContents.size(); i++) {
                            writer.write(marketContents.get(i));
                            writer.println();
                            writer.flush();
                        }
                        writer.println();
                        writer.flush();
                    }
                    String productFilename = reader.readLine();
                    ArrayList<String> productsDisp = new ArrayList<>();
                    for (String line = reader.readLine(); line != null && line.length() > 0; line =
                            reader.readLine()) {
                        productsDisp.add(line);
                    }
                    index = reader.read();
                    reader.readLine();
                    viewProductInfo(productFilename, productsDisp, index, writer, marketContents, reader);
                    //code for case 2 customer
                    afterLogIn(writer, reader);
                    break;
                case 3:
                    boolean found;

                    do {
                        found = true;
                        String query = reader.readLine();
                        ArrayList<String> searchResult = searchProduct(query);
                        if (searchResult.size() == 0) {
                            found = false;
                            writer.write("false");
                            writer.println();
                            writer.flush();
                        } else {
                            for (int i = 0; i < searchResult.size(); i++) {
                                writer.write(searchResult.get(i));
                                writer.println();
                                writer.flush();
                            }
                            writer.println();
                            writer.flush();
                        }
                        marketContents = readFile("Marketplace.txt");
                        if (marketContents.size() > 0) {
                            for (int i = 0; i < marketContents.size(); i++) {
                                writer.write(marketContents.get(i));
                                writer.println();
                                writer.flush();
                            }
                            writer.println();
                            writer.flush();
                        }
                    } while (found == false);
                    marketContents = readFile("Marketplace.txt");
                    productFilename = reader.readLine();
                    productsDisp = new ArrayList<>();
                    for (String line = reader.readLine(); line != null && line.length() > 0; line =
                            reader.readLine()) {
                        productsDisp.add(line);
                    }
                    index = reader.read();
                    reader.readLine();
                    viewProductInfo(productFilename, productsDisp, index, writer, marketContents, reader);
                    //code for case 3 customer
                    afterLogIn(writer, reader);
                    break;
                case 4:
                    String export = reader.readLine();
                    if (export.matches("1")) {
                        username = reader.readLine();
                        exportPurchase(username, reader, writer);
                    }
                    afterLogIn(writer, reader);
                    //code for case 4 customer
                    break;
                case 5:
                    String response = reader.readLine();
                    username = reader.readLine();
                    checkStats(response, username, writer, reader);
                    //code for case 5 customer
                    break;
                case 6:
                    break;
            }
        }
    }

    public void createAccount(BufferedReader reader, PrintWriter writer) throws IOException {
        try {
            File f = new File("customer.txt");
            //customer info written to "customer.txt"
            File g = new File("seller.txt");
            //seller info written to "seller.txt"
            f.createNewFile();
            g.createNewFile();
        } catch (IOException e) {
        }
        Server server = new Server();
        String username;
        String password;
        do {
            String acctType = reader.readLine();
            username = reader.readLine();
            if (server.findAccount("customer.txt", username) || server.findAccount("seller.txt",
                    username)) {
                writer.write("true");
                writer.println();
                writer.flush();
            } else {
                writer.write("false");
                writer.println();
                writer.flush();
                password(reader, username, acctType);
                afterLogIn(writer, reader);
            }

        } while (server.findAccount("customer.txt", username) || server.findAccount("seller.txt",
                username));
    }

    public void password(BufferedReader reader, String username, String acctType) throws IOException {
        String password = reader.readLine();
        //password = reader.readLine();
        if (acctType.matches("1")) {
            saveAccount("customer.txt", username, password);
        } else if (acctType.matches("2")) {
            saveAccount("seller.txt", username, password);
        }
    }

    public void logIn(BufferedReader reader, PrintWriter writer) throws IOException {
        /*
         * If a user tries to log in, this method verifies that the username
         * exists and that the password matches the given username
         */
        String verified;
        do {
            String acctType = reader.readLine();
            String username = reader.readLine();
            String password = reader.readLine();
            String comparePass = "";
            String filename = "";
            verified = "false";
            if (acctType.matches("customer")) {
                filename = "customer.txt";
            } else if (acctType.matches("seller")) {
                filename = "seller.txt";
            }
            int index = -1;
            ArrayList<String> usernameList = readFile(filename);
            for (int i = 0; i < usernameList.size(); i++) {
                String[] array = usernameList.get(i).split(",");
                if ((array[0]).equalsIgnoreCase(username)) {
                    index = i;
                    comparePass = array[1];
                }
            }
            if (index == -1) {
                //Send DNE error code to client
                writer.write(verified);
                writer.println();
                writer.flush();
            } else {
                verified = "false";
                comparePass =
                        usernameList.get(index).substring(usernameList.get(index).indexOf(",") + 1);
                if (comparePass.matches(password)) {
                    verified = "true";
                    writer.write(verified);
                    writer.println();
                    writer.flush();
                } else {
                    verified = "false";
                    writer.write(verified);
                    writer.println();
                    writer.flush();
                }
            }
            if (verified.matches("true")) {
                afterLogIn(writer, reader);
            }
        } while (verified.matches("false"));
    }


    // ClientHandler class
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        // Constructor
        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        public void run() {
            PrintWriter writer = null; //out
            BufferedReader reader = null; //in
            try {

                // get the outputstream of client

                // get the inputstream of client


                String line;
                Server sev = new Server();
                //Socket socket = serverSocket.accept();
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer = new PrintWriter(clientSocket.getOutputStream());
                //user initial choice should be sent to server for proper method call
                //ie user wants to create an account, send "1"  from client
                //user wants to log in, send "2" from client
                String initial = reader.readLine();
                if (initial.matches("1")) {
                    sev.createAccount(reader, writer);
                } else if (initial.matches("2")) {
                    sev.logIn(reader, writer);
                }
            } catch (Exception e) {
            } finally {
                try {
                    if (writer != null) {
                        writer.close();
                    }
                    if (reader != null) {
                        reader.close();
                        clientSocket.close();
                    }
                } catch (IOException e) {
                }
            }
        }
    }


    public static void main(String[] args) throws IOException {


        ServerSocket server = null;

        try {

            // server is listening on port 1234
            server = new ServerSocket(4242);
            server.setReuseAddress(true);

            // running infinite loop for getting
            // client request
            while (true) {

                // socket object to receive incoming client
                // requests
                Socket client = server.accept();

                // Displaying that new client is connected
                // to server

                // create a new thread object
                ClientHandler clientSock
                        = new ClientHandler(client);

                // This thread will handle the client
                // separately
                new Thread(clientSock).start();
            }
        } catch (IOException e) {
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                }
            }
        }
    }

    //ServerSocket serverSocket = new ServerSocket(4242);
}
