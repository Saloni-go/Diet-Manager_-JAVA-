package model;

public class LogEntry {
    private Food food;
    private int servings;

    public LogEntry(Food food, int servings) {
        this.food = food;
        this.servings = servings;
    }

    public Food getFood() {
        return food;
    }

    public int getServings() {
        return servings;
    }

    @Override
    public String toString() {
        return servings + " serving(s) of " + food.getName();
    }
}
