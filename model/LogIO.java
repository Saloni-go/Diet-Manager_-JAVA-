package model;
import java.io.*;

public class LogIO {
    public static void saveLogEntry(Food food, int servings, String filePath) throws IOException {
        File file = new File(filePath);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            String date = java.time.LocalDate.now().toString();
            writer.write(food.getName() + ";" + servings + ";" + date);
            writer.newLine();
        }
    }
    
}
