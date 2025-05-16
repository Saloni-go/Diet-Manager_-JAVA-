package gui;

import javax.swing.*;
import java.awt.*;
import model.*;
import model.FoodEntry;
import model.Food;

public class SummaryPanel extends JPanel {
    private JLabel lblBMRValue;
    private JLabel lblTDEEValue;
    private JLabel lblRecommendedValue;
    private JLabel lblConsumedValue;
    private JLabel lblEvaluation;
    private JLabel lblWeightGoal;
    private JButton refreshButton;
    private DailyLog dailyLog;
    private UserProfile userProfile;
    private JTextArea foodBreakdownArea;

    private JRadioButton mifflinRadio;
    private JRadioButton katchRadio;

    public SummaryPanel(DailyLog dailyLog, UserProfile userProfile) {
        this.dailyLog = dailyLog;
        this.userProfile = userProfile;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Summary"));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Calculation method panel
        JPanel methodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        methodPanel.setBorder(BorderFactory.createTitledBorder("Calorie Calculation Method"));
        mifflinRadio = new JRadioButton("Mifflin-St Jeor", true);
        katchRadio = new JRadioButton("Katch-McArdle (auto from measurements)");
        ButtonGroup methodGroup = new ButtonGroup();
        methodGroup.add(mifflinRadio);
        methodGroup.add(katchRadio);
        methodPanel.add(mifflinRadio);
        methodPanel.add(katchRadio);
        mainPanel.add(methodPanel);

        JPanel goalPanel = new JPanel();
        goalPanel.setLayout(new BorderLayout());
        lblWeightGoal = new JLabel("Weight Goal: " + userProfile.getWeightGoal());
        goalPanel.setBorder(BorderFactory.createTitledBorder("Weight Goal"));
        goalPanel.add(lblWeightGoal, BorderLayout.CENTER);

        JPanel bmrPanel = createInfoPanel("BMR (Basal Metabolic Rate)", "", "");
        JPanel tdeePanel = createInfoPanel("Target Calories (TDEE)", "", "");
        JPanel recommendedPanel = createInfoPanel("Recommended Calories", "", "");
        JPanel consumedPanel = createInfoPanel("Calories Consumed", "", "");

        JPanel evalPanel = new JPanel();
        evalPanel.setLayout(new BorderLayout());
        JLabel evalLabel = new JLabel("Evaluation: ");
        lblEvaluation = new JLabel("");
        evalPanel.setBorder(BorderFactory.createTitledBorder("Diet Evaluation"));
        evalPanel.add(evalLabel, BorderLayout.WEST);
        evalPanel.add(lblEvaluation, BorderLayout.CENTER);

        mainPanel.add(goalPanel);
        mainPanel.add(bmrPanel);
        mainPanel.add(tdeePanel);
        mainPanel.add(recommendedPanel);
        mainPanel.add(consumedPanel);
        mainPanel.add(evalPanel);

        foodBreakdownArea = new JTextArea(5, 30);
        foodBreakdownArea.setEditable(false);
        foodBreakdownArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        foodBreakdownArea.setBorder(BorderFactory.createTitledBorder("Per-Food Calorie Breakdown"));
        mainPanel.add(new JScrollPane(foodBreakdownArea));

        add(mainPanel, BorderLayout.CENTER);

        refreshButton = new JButton("Refresh");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> updateSummary());

        updateSummary();
    }

    private JPanel createInfoPanel(String title, String value, String formula) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.setMaximumSize(new Dimension(300, 100));

        JLabel valueLabel = new JLabel(value);
        JLabel formulaLabel = new JLabel(formula);

        panel.add(valueLabel);
        if (!formula.isEmpty()) {
            panel.add(formulaLabel);
        }

        switch (title) {
            case "BMR (Basal Metabolic Rate)":
                lblBMRValue = valueLabel;
                break;
            case "Target Calories (TDEE)":
                lblTDEEValue = valueLabel;
                break;
            case "Recommended Calories":
                lblRecommendedValue = valueLabel;
                break;
            case "Calories Consumed":
                lblConsumedValue = valueLabel;
                break;
        }

        return panel;
    }

    public SummaryPanel(UserProfile userProfile) {
        setLayout(new BorderLayout());
        
        double bmr = userProfile.getBMR();
        double tdee = userProfile.getTDEE();
        double bodyFat = userProfile.getBodyFatPercentage();
        double recommendedCalories = userProfile.getRecommendedCalories();

        String summaryText = String.format(
            "<html><h2>Summary</h2>" +
            "BMR: %.2f<br>" +
            "TDEE: %.2f<br>" +
            "Body Fat: %.2f%%<br>" +
            "Recommended Daily Calorie Intake: %.2f cal</html>",
            bmr, tdee, bodyFat, recommendedCalories
        );

        add(new JLabel(summaryText), BorderLayout.CENTER);
    }

    private void updateSummary() {
        double totalConsumed = dailyLog.getTotalCalories();
        double bmr;

        // Select method
        if (katchRadio.isSelected()) {
            double bodyFat = userProfile.calculateBodyFatPercentage();  // Navy method
            double leanBodyMass = userProfile.getWeight() * (1 - bodyFat / 100);
            bmr = 370 + (21.6 * leanBodyMass);
        } else {
            bmr = userProfile.getBMR();  // Mifflin-St Jeor
        }

        double activityFactor = userProfile.getActivityLevel();
        double tdee = bmr * activityFactor;
        double recommendedCalories = tdee;

        String weightGoal = userProfile.getWeightGoal();
        if ("gain".equals(weightGoal)) {
            recommendedCalories += 500;
        } else if ("lose".equals(weightGoal)) {
            recommendedCalories -= 500;
        }

        if (lblWeightGoal != null) {
            String goal = userProfile.getWeightGoal();
            if (goal == null || goal.isEmpty()) {
                goal = "Not Set";
            }
            lblWeightGoal.setText("Weight Goal: " + goal);
        }

        if (lblBMRValue != null) {
            lblBMRValue.setText(String.format("%.2f kcal", bmr));
        }
        if (lblTDEEValue != null) {
            lblTDEEValue.setText(String.format("%.2f kcal", tdee));
        }
        if (lblRecommendedValue != null) {
            lblRecommendedValue.setText(String.format("%.2f kcal", recommendedCalories));
        }
        if (lblConsumedValue != null) {
            lblConsumedValue.setText(String.format("%.2f kcal", totalConsumed));
        }

        String evaluation = DietEvaluator.evaluateDiet(totalConsumed, tdee);
        if (lblEvaluation != null) {
            lblEvaluation.setText(evaluation);
        }

        if (foodBreakdownArea != null) {
            StringBuilder breakdown = new StringBuilder();
            for (FoodEntry entry : dailyLog.getFoodEntries()) {
                Food food = entry.getFood();
                double totalCals = food.getCalories(entry.getQuantity());
                breakdown.append(String.format("%-20s: %.2f kcal\n", food.getName(), totalCals));
            }
            foodBreakdownArea.setText(breakdown.toString());
        }
    }
}
