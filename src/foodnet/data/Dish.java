package foodnet.data;

import java.io.Serializable;

public class Dish implements Serializable {
    private String name;
    private double price;

    public Dish(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        // Keeping "руб." as it is a unit of currency, not conversational text
        return name + " - " + price + " RUB."; 
    }
}