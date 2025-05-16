package gui;
import model.LogEntry;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.IOException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.*;
import java.util.stream.Collectors;
import model.*;

public class LogPanel extends JPanel {
    private JComboBox<String> foodList;
    private JTextField servingsField;
    private JTextArea logArea;
    private JTextField keywordSearchField;
    private JTextField foodNameSearchField;
    private FoodDatabase foodDatabase;
    private DailyLog dailyLog;

    private DefaultListModel<String> logListModel;
    private JList<String> logList;
    private java.util.List<LogEntry> logEntries; // Track actual objects

    private void removeEntryFromLogFile(LogEntry toRemove) {
        File originalFile = new File("data/log.txt");
        File tempFile = new File("data/log_temp.txt");

        try (
            BufferedReader reader = new BufferedReader(new FileReader(originalFile));
            PrintWriter writer = new PrintWriter(new FileWriter(tempFile))
        ) {
            String line;
            boolean removed = false;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String foodName = parts[0].trim();
                    int servings = Integer.parseInt(parts[1].trim());

                    // Match based on food and servings — skip first match only
                    if (!removed && foodName.equals(toRemove.getFood().getName()) && servings == toRemove.getServings()) {
                        removed = true; // skip this line (delete it)
                        continue;
                    }
                }
                writer.println(line); // keep this line
            }

            writer.flush();

            // Replace old file with new one
            if (!originalFile.delete() || !tempFile.renameTo(originalFile)) {
                throw new IOException("Failed to replace original log file.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error deleting log entry from file: " + e.getMessage());
        }
    }


    public LogPanel(FoodDatabase foodDatabase, DailyLog dailyLog) {
        this.foodDatabase = foodDatabase;
        this.dailyLog = dailyLog;
        this.logEntries = new ArrayList<>();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Daily Log"));

        // Search Panels
        JPanel searchPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        JPanel foodSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        foodNameSearchField = new JTextField(15);
        JButton foodSearchButton = new JButton("Search by Food Name");
        foodSearchPanel.add(new JLabel("Search by food name:"));
        foodSearchPanel.add(foodNameSearchField);
        foodSearchPanel.add(foodSearchButton);

        JPanel keywordSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        keywordSearchField = new JTextField(15);
        JButton keywordSearchButton = new JButton("Search by Keywords");
        keywordSearchPanel.add(new JLabel("Search by keywords:"));
        keywordSearchPanel.add(keywordSearchField);
        keywordSearchPanel.add(keywordSearchButton);

        searchPanel.add(foodSearchPanel);
        searchPanel.add(keywordSearchPanel);

        // Food Selection Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        foodList = new JComboBox<>(foodDatabase.getFoodNames().toArray(new String[0]));
        servingsField = new JTextField(5);
        JButton addButton = new JButton("Add Entry");

        topPanel.add(new JLabel("Food:"));
        topPanel.add(foodList);
        topPanel.add(new JLabel("Servings:"));
        topPanel.add(servingsField);
        topPanel.add(addButton);

        // Combine panels
        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(searchPanel, BorderLayout.NORTH);
        northPanel.add(topPanel, BorderLayout.SOUTH);
        add(northPanel, BorderLayout.NORTH);

        // Log Display Area
        logListModel = new DefaultListModel<>();
        logList = new JList<>(logListModel);
        JScrollPane listScrollPane = new JScrollPane(logList);

        JButton deleteButton = new JButton("Delete Selected");
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        centerPanel.add(listScrollPane, BorderLayout.CENTER);
        centerPanel.add(deleteButton, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        // === Actions ===

        foodSearchButton.addActionListener((ActionEvent e) -> {
            String text = foodNameSearchField.getText().trim().toLowerCase();
            if (text.isEmpty()) {
                refreshFoodList();
            } else {
                var results = foodDatabase.getAllFoods().stream()
                    .filter(f -> f.getName().toLowerCase().contains(text))
                    .map(Food::getName)
                    .collect(Collectors.toList());
                foodList.setModel(new DefaultComboBoxModel<>(results.toArray(new String[0])));
            }
        });

        keywordSearchButton.addActionListener((ActionEvent e) -> {
            String text = keywordSearchField.getText().trim().toLowerCase();
            if (text.isEmpty()) {
                refreshFoodList();
            } else {
                var results = foodDatabase.getAllFoods().stream()
                    .filter(f -> f.getKeywords().stream()
                        .anyMatch(k -> k.toLowerCase().contains(text)))
                    .map(Food::getName)
                    .collect(Collectors.toList());
                foodList.setModel(new DefaultComboBoxModel<>(results.toArray(new String[0])));
            }
        });

        addButton.addActionListener(e -> {
            try {
                String foodName = (String) foodList.getSelectedItem();
                int servings = Integer.parseInt(servingsField.getText().trim());
                Food food = foodDatabase.getFood(foodName);

                dailyLog.addEntry(food, servings);
                LogIO.saveLogEntry(food, servings, "data/log.txt");

                LogEntry entry = new LogEntry(food, servings);
                logEntries.add(entry);
                logListModel.addElement(servings + " serving(s) of " + food.getName());

                servingsField.setText("");
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for servings.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error logging food: " + ex.getMessage());
            }
        });

        deleteButton.addActionListener(e -> {
            int selectedIndex = logList.getSelectedIndex();
            if (selectedIndex != -1) {
                LogEntry entryToRemove = logEntries.get(selectedIndex);

                // 1. Remove from memory (UI and internal list)
                logEntries.remove(selectedIndex);
                logListModel.remove(selectedIndex);

                // 2. Remove only this entry from the file
                removeEntryFromLogFile(entryToRemove);

                JOptionPane.showMessageDialog(this, "Entry deleted.");
            } else {
                JOptionPane.showMessageDialog(this, "Please select an entry to delete.");
            }
        });

    }

    public void refreshFoodList() {
        foodList.setModel(new DefaultComboBoxModel<>(foodDatabase.getFoodNames().toArray(new String[0])));
    }
}
