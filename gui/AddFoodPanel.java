package gui;

import javax.swing.*;
import java.awt.*;
import model.*;

public class AddFoodPanel extends JPanel {
    private JTextField nameField, keywordsField, caloriesField;
    private JButton addButton, undoButton;

    private JComboBox<String> sourceBox;
    private JTextField apiSearchField;
    private JButton fetchButton;

    private FoodDatabase foodDatabase;

    public AddFoodPanel(FoodDatabase foodDatabase) {
        this.foodDatabase = foodDatabase;
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Add New Food"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Source selection
        sourceBox = new JComboBox<>(new String[]{"Search your Food", "Try searching Online"});
        apiSearchField = new JTextField(20);
        fetchButton = new JButton("Fetch Food");

        // Manual fields
        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField(20);
        JLabel keywordsLabel = new JLabel("Keywords (comma separated):");
        keywordsField = new JTextField(20);
        JLabel caloriesLabel = new JLabel("Calories per serving:");
        caloriesField = new JTextField(10);
        addButton = new JButton("Add Food");
        undoButton = new JButton("Undo");
        undoButton.setEnabled(false); // Start as disabled

        // Row 0 - Data Source Selector
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Data Source:"), gbc);
        gbc.gridx = 1;
        add(sourceBox, gbc);

        // Row 1 - API search field
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Search Food (API):"), gbc);
        gbc.gridx = 1;
        add(apiSearchField, gbc);

        // Row 2 - Fetch button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        add(fetchButton, gbc);

        // Row 3 - Name
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(nameLabel, gbc);
        gbc.gridx = 1;
        add(nameField, gbc);

        // Row 4 - Keywords
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(keywordsLabel, gbc);
        gbc.gridx = 1;
        add(keywordsField, gbc);

        // Row 5 - Calories
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(caloriesLabel, gbc);
        gbc.gridx = 1;
        add(caloriesField, gbc);

        // Row 6 - Add button
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(undoButton);
        add(buttonPanel, gbc);

        // Default: hide API components
        apiSearchField.setVisible(false);
        fetchButton.setVisible(false);

        // Toggle between Manual/API mode
        sourceBox.addActionListener(e -> {
            boolean isApi = sourceBox.getSelectedIndex() == 1; // "Try searching Online"
            apiSearchField.setVisible(isApi);
            fetchButton.setVisible(isApi);

            nameField.setVisible(!isApi);
            keywordsField.setVisible(!isApi);
            caloriesField.setVisible(!isApi);
            addButton.setVisible(!isApi);
            undoButton.setVisible(!isApi);

            revalidate();
            repaint();
        });

        // Manual Add Food with Undo
        addButton.addActionListener(e -> {
            try {
                String name = nameField.getText().trim();
                String keywordsStr = keywordsField.getText().trim();
                double calories = Double.parseDouble(caloriesField.getText().trim());

                FoodDataSource dataSource = new ManualEntryFoodSource(name, keywordsStr, calories);
                BasicFood newFood = dataSource.fetchFood(name);

                CommandManager.getInstance().executeCommand(new AddFoodCommand(foodDatabase, newFood));
                FoodIO.saveFood(newFood, "data/foods.txt");

                JOptionPane.showMessageDialog(this, "Food Added: " + name + " (" + calories + " cal)\nKeywords: " + keywordsStr);
                nameField.setText("");
                keywordsField.setText("");
                caloriesField.setText("");

                undoButton.setEnabled(CommandManager.getInstance().canUndo());
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for calories.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding food: " + ex.getMessage());
            }
        });

        // API Fetch with Undo
        fetchButton.addActionListener(e -> {
            try {
                String query = apiSearchField.getText().trim();
                if (query.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter a food name to search.");
                    return;
                }

                FoodDataSource apiSource = new ApiFoodSource(); // Replace with actual API source
                BasicFood fetched = apiSource.fetchFood(query);

                CommandManager.getInstance().executeCommand(new AddFoodCommand(foodDatabase, fetched));
                FoodIO.saveFood(fetched, "data/foods.txt");

                JOptionPane.showMessageDialog(this, "Food fetched and added: " + fetched.getName());
                apiSearchField.setText("");

                undoButton.setEnabled(CommandManager.getInstance().canUndo());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "API Error: " + ex.getMessage());
            }
        });

        // Undo Action
        undoButton.addActionListener(e -> {
            CommandManager.getInstance().undo();
            undoButton.setEnabled(CommandManager.getInstance().canUndo());
            JOptionPane.showMessageDialog(this, "Undo successful.");
        });
    }
}
