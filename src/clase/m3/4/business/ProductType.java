package UF4.business;

public class ProductType {
    
    private String name;
    private int id;
    private double price;
    private int stock;
    private boolean isOnSale;

    public ProductType(String name, int id, double price, int stock, boolean isOnSale) {
        this.name = name;
        this.id = id;
        this.price = price;
        this.stock = stock;
        this.isOnSale = isOnSale;
    }

    // Getters & Setters.
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public boolean isOnSale() {
        return isOnSale;
    }

    public void setOnSale(boolean isOnSale) {
        this.isOnSale = isOnSale;
    }
}
