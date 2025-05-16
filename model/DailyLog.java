package model;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import model.FoodEntry;


public class DailyLog {
    private Map<Food, Integer> log = new LinkedHashMap<>();
    private List<FoodEntry> foodEntries = new ArrayList<>();

    public void addEntry(Food food, int servings) {
        log.put(food, log.getOrDefault(food, 0) + servings);
    }
    
    public double getTotalCalories() {
        double total = 0;
        for (Map.Entry<Food, Integer> entry : log.entrySet()) {
            total += entry.getKey().getCalories(entry.getValue());
        }
        return total;
    }

    // model/DailyLog.java
    public void addEntry(FoodEntry entry) {
        foodEntries.add(entry);
    }

    public void removeEntry(FoodEntry entry) {
        foodEntries.remove(entry);
    }


    public void deleteEntry(Food food) {
    log.remove(food);
    }

    
    public String getLogDetails() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Food, Integer> entry : log.entrySet()) {
            sb.append(entry.getValue()).append(" x ").append(entry.getKey().getName())
              .append(" = ").append(entry.getKey().getCalories(entry.getValue())).append(" kcal\n");
        }
        return sb.toString();
    }

    public List<FoodEntry> getFoodEntries() {
    return foodEntries; // or whatever list you are using internally
    }

    
    public void clear() {
        log.clear();
    }
}
