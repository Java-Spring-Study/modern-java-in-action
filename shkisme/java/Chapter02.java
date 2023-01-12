
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class Chapter02 {

  // 2.1.1 첫번째 시도
  public static List<Apple> filterGreenApples(List<Apple> inventory) {
    List<Apple> result = new ArrayList<>();

    for (Apple apple : inventory) {
      if (Color.GREEN.equals(apple.getColor())) {
        result.add(apple);
      }
    }
    return result;
  }

  // 2.1.2 두번째 시도 - 색을 파라미터화
  public static List<Apple> filterApplesByColor(List<Apple> inventory, Color color) {
    List<Apple> result = new ArrayList<>();

    for (Apple apple : inventory) {
      if (color.equals(apple.getColor())) {
        result.add(apple);
      }
    }
    return result;
  }

  // 농부가 무게에 대한 기준도 바뀌게 해달라고 했다면
  public static List<Apple> filterApplesByWeight(List<Apple> inventory, int weight) {
    List<Apple> result = new ArrayList<>();

    for (Apple apple : inventory) {
      if (apple.getWeight() > weight) {
        result.add(apple);
      }
    }
    return result;
  }

  // 2.2 선택 조건을 결정하는 인터페이스 만들기
  public interface ApplePredicate {

    boolean test(Apple apple);
  }

  // 다양한 선택 조건을 대표하는 여러 버전의 Apple Predicate 정의
  public class AppleHeavyWeightPredicate implements ApplePredicate {

    @Override
    public boolean test(Apple apple) {
      return apple.getWeight() > 150;
    }
  }

  public class AppleGreenColorPredicate implements ApplePredicate {

    @Override
    public boolean test(Apple apple) {
      return Color.GREEN.equals(apple.getColor());
    }
  }

  // 2.2.1 Apple Predicate 를 이용한 필터 메서드
  public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple : inventory) {
      if (p.test(apple)) {
        result.add(apple);
      }
    }
    return result;
  }

  // 퀴즈 2-1
  public interface ApplePrint {

    String print(Apple apple);
  }

  public class AppleWeightPrint implements ApplePrint {

    @Override
    public String print(Apple apple) {
      return "Apple Weight : " + String.valueOf(apple.getWeight());
    }
  }

  public class AppleHeavyOrLightPrint implements ApplePrint {

    @Override
    public String print(Apple apple) {
      return apple.getWeight() > 150 ? "Heavy Apple" : "Light Apple";
    }
  }

  public static void prettyPrintApple(List<Apple> inventory, ApplePrint applePrint) {
    for (Apple apple : inventory) {
      String output = applePrint.print(apple);
      System.out.println(output);
    }
  }

  public static void main(String[] args) {
    // 2.3.2 익명 클래스 사용
    List<Apple> inventory = new ArrayList<>();
    List<Apple> redApples1 = filterApples(inventory, new ApplePredicate() {
      @Override
      public boolean test(Apple apple) {
        return Color.RED.equals(apple.getColor());
      }
    });

    // 퀴즈 2-2
    MeaningOfThis m = new MeaningOfThis();
    m.doIt();

    // 2.3.3 람다 표현식 사용!
    List<Apple> result = filterApples(inventory,
        (Apple apple) -> Color.RED.equals(apple.getColor()));
    // 2.3.4 리스트 형식으로 추상화
    List<Apple> redApples2 = filter(inventory, (Apple apple) -> Color.RED.equals(apple.getColor()));
    List<Integer> numbers = new ArrayList<>();
    List<Integer> evenNumbers = filter(numbers, integer -> integer % 2 == 0);
  }

  // 퀴즈 2-2
  public static class MeaningOfThis {

    public final int value = 4;

    public void doIt() {
      int value = 6;
      Runnable r = new Runnable() {
        public final int value = 5;

        @Override
        public void run() {
          int value = 10;
          System.out.println(this.value); // this 는 Runnable 객체를 가리키고 있음
        }
      };
      r.run();
    }
  }

  // 2.3.4 리스트 형식으로 추상화
  public interface Predicate<T> {

    boolean test(T t);
  }

  public static <T> List<T> filter(List<T> list, Predicate<T> p) {
    List<T> result = new ArrayList<>();
    for (T l : list) {
      if (p.test(l)) {
        result.add(l);
      }
    }
    return result;
  }
}
