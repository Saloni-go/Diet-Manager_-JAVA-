package model;

import java.util.*;

public class FoodDatabase {
    private Map<String, Food> foodMap = new HashMap<>();

    // Add a food item to the database
    public void addFood(Food food) {
        foodMap.put(food.getName().toLowerCase(), food);
    }

    // Retrieve a food item by name
    public Food getFood(String name) {
        return foodMap.get(name.toLowerCase());
    }

    // Remove a food item by name
    public void removeFood(String name) {
        foodMap.remove(name.toLowerCase());
    }

    // Get all food items
    public Collection<Food> getAllFoods() {
        return foodMap.values();
    }

    // Get all food names
    public List<String> getFoodNames() {
        return new ArrayList<>(foodMap.keySet());
    }
}
