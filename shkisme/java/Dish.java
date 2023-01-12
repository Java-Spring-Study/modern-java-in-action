

public class Dish {

  private final String name;
  private final boolean vegetarian;
  private final int calories;
  private final Type type;

  public Dish(String name, boolean vegetarian, int calories, Type type) {
    this.name = name;
    this.calories = calories;
    this.vegetarian = vegetarian;
    this.type = type;
  }

  public int getCalories() {
    return calories;
  }

  public String getName() {
    return name;
  }

  public Type getType() {
    return type;
  }

  public boolean isVegetarian() {
    return this.vegetarian;
  }

  @Override
  public String toString() {
    return "Dish{" +
        "name='" + name + '\'' +
        ", vegetarian=" + vegetarian +
        ", calories=" + calories +
        ", type=" + type +
        '}';
  }
}
