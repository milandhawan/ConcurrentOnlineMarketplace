/**
 * Product
 * This class creates product objects 
 * that can be listed by the seller or bought by a customer
 * 
 *
 * @author Vanamali Vemparala, Milan Dhawan, Elliot Bode, Stephen Kruse, lab sec 26
 *
 * @version December 12, 2022
 *
 */
public class Product extends Object {

    private String name;
    private String description;
    private int price;
    private int quantity;

    public Product(String name, String description, int price, int quantity) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        Product product = (Product) o;

        return (this.name.equals(product.getName())
                && this.description.equals(product.getDescription())
                && this.price == product.getPrice()
                && this.quantity == product.getQuantity());

    }

}
