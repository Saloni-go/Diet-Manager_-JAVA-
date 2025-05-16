package gui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.*;
import model.*;

public class CompositeFoodPanel extends JPanel {
    private JTextField nameField, keywordsField, searchField;
    private JPanel componentsPanel;
    private JButton addComponentButton, addCompositeButton;
    private FoodDatabase foodDatabase;

    private static class ComponentEntry {
        JComboBox<String> foodCombo;
        JTextField servingsField;
    }

    private java.util.List<ComponentEntry> componentEntries;

    public CompositeFoodPanel(FoodDatabase foodDatabase) {
        this.foodDatabase = foodDatabase;
        componentEntries = new ArrayList<>();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Create Composite Food"));

        // Top input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Composite Food Name:");
        nameField = new JTextField(20);
        JLabel keywordsLabel = new JLabel("Keywords (comma separated):");
        keywordsField = new JTextField(20);
        JLabel searchLabel = new JLabel("Search by keyword:");
        searchField = new JTextField(20);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateCombos(); }
            public void removeUpdate(DocumentEvent e) { updateCombos(); }
            public void changedUpdate(DocumentEvent e) { updateCombos(); }
        });

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(keywordsLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(keywordsField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(searchLabel, gbc);
        gbc.gridx = 1;
        inputPanel.add(searchField, gbc);

        add(inputPanel, BorderLayout.NORTH);

        // Components area
        componentsPanel = new JPanel();
        componentsPanel.setLayout(new BoxLayout(componentsPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(componentsPanel);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        addComponentButton = new JButton("Add Component");
        addCompositeButton = new JButton("Create Composite Food");
        buttonPanel.add(addComponentButton);
        buttonPanel.add(addCompositeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        addComponentButton.addActionListener(e -> addComponentEntry());
        addCompositeButton.addActionListener(e -> createCompositeFood());

        addComponentEntry(); // Start with one entry
    }

    private void addComponentEntry() {
        ComponentEntry entry = new ComponentEntry();
        entry.foodCombo = new JComboBox<>(foodDatabase.getFoodNames().toArray(new String[0]));
        entry.servingsField = new JTextField(5);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.add(new JLabel("Component:"));
        panel.add(entry.foodCombo);
        panel.add(new JLabel("Servings:"));
        panel.add(entry.servingsField);

        componentsPanel.add(panel);
        componentsPanel.revalidate();
        componentsPanel.repaint();

        componentEntries.add(entry);
    }

    private void createCompositeFood() {
        try {
            String compName = nameField.getText().trim();
            String keywordsStr = keywordsField.getText().trim();
            if (compName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Composite food name cannot be empty.");
                return;
            }
            java.util.List<String> keywords = Arrays.asList(keywordsStr.split("\\s*,\\s*"));

            Map<Food, Integer> components = new HashMap<>();
            for (ComponentEntry entry : componentEntries) {
                String foodName = (String) entry.foodCombo.getSelectedItem();
                int servings = Integer.parseInt(entry.servingsField.getText().trim());
                Food food = foodDatabase.getFood(foodName);
                if (food != null) {
                    components.put(food, servings);
                }
            }

            if (components.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please add at least one component.");
                return;
            }

            CompositeFood compositeFood = new CompositeFood(compName, keywords, components);
            foodDatabase.addFood(compositeFood);
            FoodIO.saveFood(compositeFood, "data/foods.txt");
            JOptionPane.showMessageDialog(this, "Composite Food Created: " + compName);

            // Reset UI
            nameField.setText("");
            keywordsField.setText("");
            componentsPanel.removeAll();
            componentEntries.clear();
            addComponentEntry();
            componentsPanel.revalidate();
            componentsPanel.repaint();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for servings.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error creating composite food: " + ex.getMessage());
        }
    }

    private void updateCombos() {
        String query = searchField.getText().toLowerCase();
        java.util.List<String> filtered = foodDatabase.getAllFoods().stream()
            .filter(f -> f.getKeywords().stream().anyMatch(k -> k.toLowerCase().contains(query)))
            .map(Food::getName)
            .toList();

        for (ComponentEntry entry : componentEntries) {
            entry.foodCombo.setModel(new DefaultComboBoxModel<>(filtered.toArray(new String[0])));
        }
    }
}
