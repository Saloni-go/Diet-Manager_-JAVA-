package gui;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import model.*;

public class ProfilePanel extends JPanel {
    private JTextField nameField, ageField, heightField, weightField;
    private JComboBox<String> genderBox, activityLevelBox, goalBox;
    private UserProfile userProfile;

    // New fields for body fat calculation
    private JTextField waistField, neckField, hipField;

    public ProfilePanel(UserProfile userProfile) {
        this.userProfile = userProfile;

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        nameField = new JTextField(15);
        ageField = new JTextField(5);
        heightField = new JTextField(5);
        weightField = new JTextField(5);
        waistField = new JTextField(5);
        neckField = new JTextField(5);
        hipField = new JTextField(5); // only used for females

        genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        activityLevelBox = new JComboBox<>(new String[]{
            "Sedentary (little or no exercise)",
            "Lightly active (1-3 days/week)",
            "Moderately active (3-5 days/week)",
            "Very active (6-7 days/week)",
            "Super active (twice/day)"
        });
        goalBox = new JComboBox<>(new String[]{
            "Maintain Weight",
            "Lose Weight",
            "Gain Weight"
        });

        int row = 0;
        addField(gbc, row++, "Name:", nameField);
        addField(gbc, row++, "Gender:", genderBox);
        addField(gbc, row++, "Age:", ageField);
        addField(gbc, row++, "Height (cm):", heightField);
        addField(gbc, row++, "Weight (kg):", weightField);
        addField(gbc, row++, "Waist (cm):", waistField);
        addField(gbc, row++, "Neck (cm):", neckField);
        addField(gbc, row++, "Hip (cm):", hipField);
        addField(gbc, row++, "Activity Level:", activityLevelBox);
        addField(gbc, row++, "Fitness Goal:", goalBox);

        JButton saveButton = new JButton("Save");
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        add(saveButton, gbc);

        saveButton.addActionListener(e -> saveProfile());
    }

    private void addField(GridBagConstraints gbc, int y, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = y;
        gbc.gridwidth = 1;
        add(new JLabel(label), gbc);

        gbc.gridx = 1;
        add(field, gbc);
    }

    private void saveProfile() {
        System.out.println("Save button clicked");

        try {
            String name = nameField.getText().trim();
            String gender = (String) genderBox.getSelectedItem();
            int age = Integer.parseInt(ageField.getText().trim());
            double height = Double.parseDouble(heightField.getText().trim());
            double weight = Double.parseDouble(weightField.getText().trim());
            double waist = Double.parseDouble(waistField.getText().trim());
            double neck = Double.parseDouble(neckField.getText().trim());
            double hip = gender.equalsIgnoreCase("Female") ? Double.parseDouble(hipField.getText().trim()) : 0.0;
            double activityLevel = getActivityLevelMultiplier((String) activityLevelBox.getSelectedItem());
            String goal = (String) goalBox.getSelectedItem();

            // Set base profile fields
            userProfile.setUsername(name);
            userProfile.setGender(gender);
            userProfile.setAge(age);
            userProfile.setHeightCm(height);
            userProfile.setWeightKg(weight);
            userProfile.setActivityLevel(activityLevel);
            userProfile.setWaistCm(waist);
            userProfile.setNeckCm(neck);
            userProfile.setHipCm(hip);
            userProfile.setWeightGoal(goal);

            // Set strategy based on goal
            switch (goal) {
                case "Lose Weight" -> userProfile.setCalorieStrategy(new LoseWeightStrategy());
                case "Gain Weight" -> userProfile.setCalorieStrategy(new GainWeightStrategy());
                default -> userProfile.setCalorieStrategy(new MaintainWeightStrategy());
            }

            double bmr = userProfile.getBMR();
            double tdee = userProfile.getTDEE();
            double bodyFat = userProfile.getBodyFatPercentage();
            double recommendedCalories = userProfile.getRecommendedCalories();

            System.out.printf("DEBUG: Waist = %.2f, Neck = %.2f, Hip = %.2f, Height = %.2f, Gender = %s%n",
                    waist, neck, hip, height, gender);
            System.out.printf("DEBUG: Body Fat Calculated = %.2f%%%n", bodyFat);

            if (Double.isNaN(bodyFat) || Double.isInfinite(bodyFat) || bodyFat <= 0) {
                JOptionPane.showMessageDialog(this, "Body fat calculation failed. Please check your inputs.");
                return;
            }

            // Save to file
            java.io.File directory = new java.io.File("data");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            try (PrintWriter writer = new PrintWriter(new FileWriter("data/users.txt", true))) {
                writer.printf("%s,%s,%d,%.2f,%.2f,%.2f,%s,%.2f%n",
                        name, gender, age, height, weight, activityLevel, goal, bodyFat);
            }

            JOptionPane.showMessageDialog(this, String.format(
                    "<html>Profile saved successfully!<br>Recommended Calories: %.0f cal/day<br>Estimated Body Fat: %.2f%%</html>",
                    recommendedCalories, bodyFat));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Please enter valid values.");
            e.printStackTrace();
        }
    }

    private double getActivityLevelMultiplier(String level) {
        return switch (level) {
            case "Sedentary (little or no exercise)" -> 1.2;
            case "Lightly active (1-3 days/week)" -> 1.375;
            case "Moderately active (3-5 days/week)" -> 1.55;
            case "Very active (6-7 days/week)" -> 1.725;
            case "Super active (twice/day)" -> 1.9;
            default -> 1.2;
        };
    }
}
