package model;
import java.util.List;

public class BasicFood extends Food {
    private double caloriesPerServing;
    
    public BasicFood(String name, List<String> keywords, double caloriesPerServing) {
        super(name, keywords);
        this.caloriesPerServing = caloriesPerServing;
    }
    
    public double getCaloriesPerServing() {
        return caloriesPerServing;
    }
    
    @Override
    public double getCalories(int servings) {
        return servings * caloriesPerServing;
    }
}

