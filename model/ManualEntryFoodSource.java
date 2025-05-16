package model;

import java.util.Arrays;

public class ManualEntryFoodSource implements FoodDataSource {
    private String name;
    private String keywords;
    private double calories;

    public ManualEntryFoodSource(String name, String keywords, double calories) {
        this.name = name;
        this.keywords = keywords;
        this.calories = calories;
    }

    @Override
    public BasicFood fetchFood(String query) {
        return new BasicFood(name, Arrays.asList(keywords.split(",")), calories);
    }
}
