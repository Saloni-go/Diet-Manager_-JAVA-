package model;

public class DietEvaluator {
    public static String evaluateDiet(double consumed, double target) {
        double accuracy = (consumed / target) * 100;
        if (accuracy >= 95 && accuracy <= 105) {
            return "✅ Diet is correct (" + (int)accuracy + "% of target)";
        } else if (accuracy < 95) {
            return "⚠️ Under-eating (" + (int)accuracy + "% of target)";
        } else {
            return "❌ Over-eating (" + (int)accuracy + "% of target)";
        }
    }
}
