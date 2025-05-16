//for diet evaluation section on logs page

package model;

import java.io.Serializable;

public class FoodEntry implements Serializable {
    private Food food;
    private int quantity;

    public FoodEntry(Food food, int quantity) {
        this.food = food;
        this.quantity = quantity;
    }

    public Food getFood() {
        return food;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
