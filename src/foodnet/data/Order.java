package foodnet.data;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    private List<Dish> dishes;
    private String address;

    public Order(List<Dish> dishes, String address) {
        this.dishes = dishes;
        this.address = address;
    }

    public List<Dish> getDishes() { return dishes; }
    public String getAddress() { return address; }
}