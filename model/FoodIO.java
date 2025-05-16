package model;
import java.io.*;
import java.util.*;

public class FoodIO {
    public static void loadFoods(FoodDatabase db, String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("Foods file not found at: " + filePath);
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while((line = reader.readLine()) != null) {
                // Expected formats:
                // Basic: B;Name;CaloriesPerServing;keyword1,keyword2,...
                // Composite: C;Name;keyword1,keyword2;comp1Name:servings,comp2Name:servings,...
                String[] parts = line.split(";");
                if(parts.length < 4) continue;
                String type = parts[0];
                if(type.equalsIgnoreCase("B")) {
                    String name = parts[1];
                    double calories = Double.parseDouble(parts[2]);
                    java.util.List<String> keywords = Arrays.asList(parts[3].split(","));
                    db.addFood(new BasicFood(name, keywords, calories));
                } else if(type.equalsIgnoreCase("C")) {
                    String name = parts[1];
                    java.util.List<String> keywords = Arrays.asList(parts[2].split(","));
                    String[] compTokens = parts[3].split(",");
                    Map<Food, Integer> components = new HashMap<>();
                    for(String token : compTokens) {
                        String[] compParts = token.split(":");
                        if(compParts.length < 2) continue;
                        String compName = compParts[0];
                        int compServings = Integer.parseInt(compParts[1]);
                        // Look up the component food from the database
                        Food compFood = db.getFood(compName.toLowerCase());
                        if(compFood != null) {
                            components.put(compFood, compServings);
                        }
                    }
                    db.addFood(new CompositeFood(name, keywords, components));
                }
            }
        }
    }
    
    public static void saveFood(Food food, String filePath) throws IOException {
        File file = new File(filePath);
        if (file.getParentFile() != null && !file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            if(food instanceof BasicFood) {
                BasicFood bf = (BasicFood) food;
                writer.write("B;" + bf.getName() + ";" + bf.getCaloriesPerServing() + ";" + String.join(",", bf.getKeywords()));
            } else if(food instanceof CompositeFood) {
                CompositeFood cf = (CompositeFood) food;
                StringBuilder compStr = new StringBuilder();
                for(Map.Entry<Food, Integer> entry : cf.getComponents().entrySet()) {
                    if(compStr.length() > 0) {
                        compStr.append(",");
                    }
                    compStr.append(entry.getKey().getName()).append(":").append(entry.getValue());
                }
                writer.write("C;" + food.getName() + ";" + String.join(",", food.getKeywords()) + ";" + compStr.toString());
            }
            writer.newLine();
        }
    }
}
