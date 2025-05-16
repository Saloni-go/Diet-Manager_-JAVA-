package model;

public interface FoodDataSource {
    BasicFood fetchFood(String query) throws Exception;
}
