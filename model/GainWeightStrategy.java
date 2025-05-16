package model;

public class GainWeightStrategy implements CalorieStrategy {
    @Override
    public double computeCalories(double tdee) {
        return tdee + 500;
    }
}
