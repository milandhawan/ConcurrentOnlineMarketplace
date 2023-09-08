/**
 * Stores
 * This class works with Stores objects 
 * that can hold products that the seller wants to sell
 * 
 *
 * @author Vanamali Vemparala, Milan Dhawan, Elliot Bode, Stephen Kruse, lab sec 26
 *
 * @version December 12, 2022
 *
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Stores extends Object {

    private String storeName;
    private ArrayList<Product> products;

    public Stores(String storeName) {
        this.storeName = storeName;
        products = new ArrayList<>();
    }

    public void setName(String storeName) {
        this.storeName = storeName;
    }

    public String getName() {
        return storeName;
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public void removeProduct(Product product) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).equals(product)) {
                products.remove(i);
            }
        }
    }

    public void editProduct(String name, String newName, String newDescription, int newPrice, int newQuantity) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getName().equals(name)) {
                products.get(i).setName(newName);
                products.get(i).setDescription(newDescription);
                products.get(i).setPrice(newPrice);
                products.get(i).setQuantity(newQuantity);
            }
        }
    }


    public ArrayList<Product> getProducts() {
        return products;
    }

    @Override
    public boolean equals(Object o) {
        Stores store = (Stores) o;

        return (this.storeName.equals(store.getName()));
    }
    
}
