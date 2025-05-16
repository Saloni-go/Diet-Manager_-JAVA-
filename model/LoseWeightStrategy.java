package model;

public class LoseWeightStrategy implements CalorieStrategy {
    @Override
    public double computeCalories(double tdee) {
        return tdee - 500;
    }
}
