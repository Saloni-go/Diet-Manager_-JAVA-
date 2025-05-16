package model;

public class AddFoodCommand implements Command {
    private FoodDatabase foodDatabase;
    private Food food;

    public AddFoodCommand(FoodDatabase foodDatabase, Food food) {
        this.foodDatabase = foodDatabase;
        this.food = food;
    }

    @Override
    public void execute() {
        foodDatabase.addFood(food);
    }

    @Override
    public void undo() {
        foodDatabase.removeFood(food.getName());
    }
}
