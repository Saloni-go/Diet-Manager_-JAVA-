package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import model.*;
import gui.ProfilePanel;

public class MainWindow extends JFrame {
    private FoodDatabase foodDatabase;
    private DailyLog dailyLog;
    private UserProfile userProfile;
    private CommandManager commandManager; // Manager for undo functionality

    public MainWindow(FoodDatabase foodDatabase, DailyLog dailyLog, UserProfile userProfile) {
        this.foodDatabase = foodDatabase;
        this.dailyLog = dailyLog;
        this.userProfile = userProfile;
        this.commandManager = CommandManager.getInstance(); // Assuming singleton pattern

        setTitle("YADA - Yet Another Diet Assistant");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 550);
        setLocationRelativeTo(null);

        // Create tabbed interface
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Add Food", new AddFoodPanel(foodDatabase));
        tabs.addTab("Composite Food", new CompositeFoodPanel(foodDatabase));
        tabs.addTab("Daily Log", new LogPanel(foodDatabase, dailyLog));
        tabs.addTab("Summary", new SummaryPanel(dailyLog, userProfile));
        tabs.addTab("Profile", new ProfilePanel(userProfile));

        // Refresh food list in LogPanel when switching tabs
        tabs.addChangeListener(e -> {
            int selectedIndex = tabs.getSelectedIndex();
            if (tabs.getTitleAt(selectedIndex).equals("Daily Log")) {
                ((LogPanel) tabs.getComponentAt(selectedIndex)).refreshFoodList();
            }
        });

        // Create Undo button
        JButton undoButton = new JButton("Undo");
        undoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                commandManager.undo();
            }
        });

        // Panel for button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.add(undoButton);

        // Main layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tabs, BorderLayout.CENTER);

        add(mainPanel);
    }
}
