package model;

import java.util.Map;

public class CompositeFood extends Food {
    private Map<Food, Integer> components;
    
    public CompositeFood(String name, java.util.List<String> keywords, Map<Food, Integer> components) {
        super(name, keywords);
        this.components = components;
    }
    
    @Override
    public double getCalories(int servings) {
        double total = 0;
        for (Map.Entry<Food, Integer> entry : components.entrySet()) {
            total += entry.getKey().getCalories(entry.getValue());
        }
        return total * servings;
    }
    
    public Map<Food, Integer> getComponents() {
        return components;
    }
}
