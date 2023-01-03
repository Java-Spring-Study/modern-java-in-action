

public class Apple {
    private Color color;

    private int weight;

    private String country;

    public Apple(int weight) {
        this.weight = weight;
    }

    public Apple() {

    }

    public Apple(Color color) {
        this.color = color;
    }

    public Apple(Color color, Integer weight) {
        this.color = color;
        this.weight = weight;
    }

    public int getWeight() {
        return weight;
    }

    public Color getColor() {
        return color;
    }

    public String getCountry() {
        return country;
    }

    public static boolean isGreenApple(Apple apple){
        return Color.GREEN.equals(apple.getColor());
    }
    public static boolean isHeavyApple(Apple apple){
        return apple.getWeight() > 150;
    }

    @Override
    public String toString() {
        return "Apple{" +
            "color=" + color +
            ", weight=" + weight +
            '}';
    }
}
