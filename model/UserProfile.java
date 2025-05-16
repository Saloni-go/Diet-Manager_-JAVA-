package model;

public class UserProfile {
    private String username;
    private int age;
    private double weightKg;
    private double heightCm;
    private double activityLevel;
    private String gender;
    private String weightGoal; // "maintain", "gain", "lose"
    private double waistCm;
    private double neckCm;
    private double hipCm; // used for females
    private CalorieStrategy calorieStrategy;


    public UserProfile(String username, int age, String gender, double weightKg, double heightCm, double activityLevel, String weightGoal) {
        this.username = username;
        this.age = age;
        this.gender = gender;
        this.weightKg = weightKg;
        this.heightCm = heightCm;
        this.activityLevel = activityLevel;
        this.weightGoal = weightGoal;
    }

    // Getter and setter for weightGoal
    public String getWeightGoal() {
        return weightGoal;
    }

    public void setWeightGoal(String weightGoal) {
        this.weightGoal = weightGoal;
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
    }

    public void setHeightCm(double heightCm) {
        this.heightCm = heightCm;
    }

    public void setActivityLevel(double activityLevel) {
        this.activityLevel = activityLevel;
    }

    public void setWaistCm(double waistCm) {
        this.waistCm = waistCm;
    }

    public void setNeckCm(double neckCm) {
        this.neckCm = neckCm;
    }

    public void setHipCm(double hipCm) {
        this.hipCm = hipCm;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public double getHeightCm() {
        return heightCm;
    }

    public double getActivityLevel() {
        return activityLevel;
    }

    public double getWaistCm() {
        return waistCm;
    }

    public double getNeckCm() {
        return neckCm;
    }

    public double getHipCm() {
        return hipCm;
    }

    public double getWeight() {
        return weightKg;
    }

    // Calculate BMR
    public double getBMR() {
        if (gender.equalsIgnoreCase("Male")) {
            return 10 * weightKg + 6.25 * heightCm - 5 * age + 5;
        } else {
            return 10 * weightKg + 6.25 * heightCm - 5 * age - 161;
        }
    }

    // Calculate TDEE
    public double getTDEE() {
        return getBMR() * activityLevel;
    }

    // Calculate Body Fat % using U.S. Navy method
    public double calculateBodyFatPercentage() {
        if (gender.equalsIgnoreCase("male")) {
            return 495 / (1.0324 - 0.19077 * Math.log10(waistCm - neckCm)
                          + 0.15456 * Math.log10(heightCm)) - 450;
        } else {
            return 495 / (1.29579 - 0.35004 * Math.log10(waistCm + hipCm - neckCm)
                          + 0.22100 * Math.log10(heightCm)) - 450;
        }
    }

    public void setCalorieStrategy(CalorieStrategy strategy) {
    this.calorieStrategy = strategy;
    }

    public double getRecommendedCalories() {
        if (calorieStrategy == null) {
            return getTDEE(); // Fallback if strategy isn't set
        }
        return calorieStrategy.computeCalories(getTDEE());
    }


    // Optional duplicate getter if needed by other parts of the code
    public double getBodyFatPercentage() {
        return calculateBodyFatPercentage();
    }
}
