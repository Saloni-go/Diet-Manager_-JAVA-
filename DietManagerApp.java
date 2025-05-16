import model.*;
import gui.MainWindow;
import javax.swing.*;
import java.io.IOException;

public class DietManagerApp {
    public static void main(String[] args) {
        // Set Nimbus Look and Feel for a modern UI
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, fall back to default.
            e.printStackTrace();
        }
        
        // Create backend instances
        FoodDatabase foodDatabase = new FoodDatabase();
        DailyLog dailyLog = new DailyLog();
        
        // Load foods from the text file
        try {
            FoodIO.loadFoods(foodDatabase, "data/foods.txt");
        } catch (IOException e) {
            System.out.println("Failed to load foods: " + e.getMessage());
        }
        
        // Create a default user profile (customize as needed)
        UserProfile userProfile = new UserProfile("JohnDoe", 25, "Male", 70.0, 175.0, 1.55, "gain"); // Use a valid goal
        
        // Launch the Swing GUI, passing the backend objects
        SwingUtilities.invokeLater(() -> {
            try {
                MainWindow mainWindow = new MainWindow(foodDatabase, dailyLog, userProfile);
                mainWindow.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error initializing GUI: " + e.getMessage());
            }
        });
    }
}
