package model;

import java.util.List;

public abstract class Food {
    protected String name;
    protected List<String> keywords;

    public Food(String name, List<String> keywords) {
        this.name = name;
        this.keywords = keywords;
    }

    public String getName() {
        return name;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    // Abstract method to be implemented by subclasses
    public abstract double getCalories(int quantity);
}
