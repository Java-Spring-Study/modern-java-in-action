

public class Apple {
    private Color color;

    private int weight;

    public int getWeight() {
        return weight;
    }

    public Color getColor() {
        return color;
    }

    public static boolean isGreenApple(Apple apple){
        return Color.GREEN.equals(apple.getColor());
    }
    public static boolean isHeavyApple(Apple apple){
        return apple.getWeight() > 150;
    }
}
