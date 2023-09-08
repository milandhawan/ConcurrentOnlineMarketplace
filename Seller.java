
/**
 * Seller
 * This class works with all the logic
 * creating a seller object and all the stores
 *
 * @author Vanamali Vemparala, Milan Dhawan, Elliot Bode, Stephen Kruse, lab sec 26
 * @version December 12, 2022
 */
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.Scanner;

public class Seller extends Object {

    private String sellerName;
    private ArrayList<Stores> stores;

    public Seller(String sellerName) {
        this.sellerName = sellerName;
        stores = new ArrayList<>();
    }

    public void createStore(String storeName) {
        stores.add(new Stores(storeName));
    }

    public void addStore(Stores store) {
        stores.add(store);
    }

    public void editStore(String name, String newName) {
        for (Stores store : stores) {
            if (store.getName().equalsIgnoreCase(name)) {
                store.setName(newName);
                break;
            }
        }
    }

    public void removeStore(Stores store) {
        for (int i = 0; i < stores.size(); i++) {
            if (stores.get(i).equals(store)) {
                stores.remove(i);
                break;
            }
        }
    }



    public ArrayList<Stores> getStores() {
        return stores;
    }
}
