package model;

public class MaintainWeightStrategy implements CalorieStrategy {
    @Override
    public double computeCalories(double tdee) {
        return tdee;
    }
}
