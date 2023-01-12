public class Pair {

  int x, y;

  public Pair(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  @Override
  public String toString() {
    return "Pair{" +
        "x=" + x +
        ", y=" + y +
        '}';
  }
}
