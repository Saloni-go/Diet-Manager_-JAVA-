package model;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.*;
import java.util.Arrays;
import java.util.List;


public class ApiFoodSource implements FoodDataSource {

    @Override
    public BasicFood fetchFood(String name) throws Exception {
        String query = name.trim().replace(" ", "%20");
        String apiUrl = "https://world.openfoodfacts.org/cgi/search.pl?search_terms=" + query + "&search_simple=1&action=process&json=1";

        URL url = new URL(apiUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();
        conn.disconnect();

        JSONObject json = new JSONObject(response.toString());
        JSONArray products = json.getJSONArray("products");

        if (products.length() == 0) {
            throw new Exception("No results found for: " + name);
        }

        JSONObject food = products.getJSONObject(0);

        String foodName = food.optString("product_name", name);
        double calories = food.optJSONObject("nutriments").optDouble("energy-kcal_100g", 0.0);
       String keywordsStr = food.optString("categories_tags", "food");
        List<String> keywords = Arrays.asList(keywordsStr.replace("[", "").replace("]", "").split(","));
        return new BasicFood(foodName, keywords, calories);

    }
}
