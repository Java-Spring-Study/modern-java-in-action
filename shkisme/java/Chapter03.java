import static java.util.Comparator.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream.Filter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntBinaryOperator;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Chapter03 {

  public static void main(String[] args) throws IOException {
    //String result = processFile((BufferedReader br) -> br.readLine() + br.readLine());
    foreach(
        Arrays.asList(1, 2, 3, 4, 5),
        (Integer i) -> System.out.println(i)
    );
    List<Integer> i = map(
        Arrays.asList("lambdas", "in", "action"),
        (String s) -> s.length()
    );

    IntPredicate evenNumbers = (int i2) -> i2 % 2 == 0;
    evenNumbers.test(1000); // 박싱하지 않음
    Predicate<Integer> oddNumbers = (Integer i3) -> i3 % 2 != 0;
    oddNumbers.test(1000); // 박싱

    // 3.5.4 람다 캡쳐링 예제
    int portNumber = 1337;
    Runnable r = () -> System.out.println(portNumber);

    // List에 포함된 문자열을 대소문자를 구분하지 않고 정렬하는 프로그램을 구현하려 한다.
    // List의 sort 메서드는 인수로 Comaparator를 기대한다.
    List<String> str = Arrays.asList("a", "b", "A", "B");
    str.sort(String::compareToIgnoreCase);

    // 3.6.2 생성자 참조
    Supplier<Apple> c1 = Apple::new;
    Apple a1 = c1.get();
    Function<Integer, Apple> c2 = Apple::new;
    Apple a2 = c2.apply(110);
    System.out.println(a2);
    BiFunction<Color, Integer, Apple> c3 = Apple::new;
    Apple a3 = c3.apply(Color.GREEN, 110); // 다양한 인수를 가진 생성자는 이렇게 하면 되구나!
    System.out.println(a3);
  }

  // 람다 예제
  public void ex3_1() {
    Filter<List<String>> listFilter = (List<String> list) -> list.isEmpty();
    Runnable runnable = () -> new Apple(10);
    Consumer<Apple> appleConsumer = (Apple a) -> {
      System.out.println(a.getWeight());
    };
    Function<String, Integer> stringIntegerFunction = (String s) -> s.length();
    IntBinaryOperator intBinaryOperator = (int a, int b) -> a * b;
  }

  // p. 93 예제
  public static void p_93() {
    Runnable r1 = () -> System.out.println("HELLO 1");
    Runnable r2 = new Runnable() {
      public void run() {
        System.out.println("HELLO 2");
      }
    };
    process(r1);
    process(r2);
    process(() -> System.out.println("HELLO 3"));
  }

  public static void process(Runnable r) {
    r.run();
  }

  // 퀴즈 3-3 - easy
  public Callable<String> fetch() {
    return () -> "Tricky example ; -)";
  }

  // 3.3 람다 활용 : 실행 어라운드 패턴 - 최종적으로 동작이 유연해진 코드
  public static String processFile(BufferedReaderProcessor p) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader("data.txt"));
    return p.process(br);
  }

  @FunctionalInterface // 함수형 인터페이스 예제
  // 3.3 실행 어라운드 패턴 3단계? : 함수형 인터페이스를 정의하라.
  public interface BufferedReaderProcessor {

    String process(BufferedReader b) throws IOException;
  }

  // Consumer 예제
  public static <T> void foreach(List<T> list, Consumer<T> c) {
    for (T t : list) {
      c.accept(t);
    }
  }

  // Function 예제
  public static <T, R> List<R> map(List<T> list, Function<T, R> f) {
    List<R> result = new ArrayList<>();
    for (T t : list) {
      result.add(f.apply(t));
    }
    return result;
  }

  // 오토박싱 피하기 - 함수형 인터페이스로 피하기
  public interface IntPredicate {

    boolean test(int t);
  }

  // 3.8 람다 표현식을 조합할 수 있는 유용한 메서드
  public static void p_124() {
    List<Apple> inventory = new ArrayList<>();
    // Comparator 조합
    Comparator<Apple> c = comparing(Apple::getWeight); // Function 기반의 Comparator
    inventory.sort(comparing(Apple::getWeight)
        .reversed()
        .thenComparing(Apple::getCountry)); // 무게가 같으면 국가별로 정리

    // Predicate 조합
    Predicate<Apple> redApple = (Apple a) -> a.getColor().equals(Color.RED);
    Predicate<Apple> notRedApple = redApple.negate(); // 기존 결과를 반전시킨 객체를 만든다.
    Predicate<Apple> redAndHeavyApple = redApple
        .and(apple -> apple.getWeight() > 150); // 빨간색이면서, 무게를 필터링해준다.
    Predicate<Apple> redAndHeavyAppleOrGreen = redApple
        .and(apple -> apple.getWeight() > 150)
        .or(apple -> apple.getColor().equals(Color.GREEN)); // 더 복잡한 프레디케이드 만들기

    // Function 조합
    Function<Integer, Integer> f = x -> x + 1;
    Function<Integer, Integer> g = x -> x * 2;
    Function<Integer, Integer> h = f.andThen(g); // g(f) function
    int result = h.apply(1); // 4 가 반환된다.
    System.out.println("result = " + result);

    Function<Integer, Integer> f2 = x -> x + 1;
    Function<Integer, Integer> g2 = x -> x * 2;
    Function<Integer, Integer> h2 = f2.andThen(g2); // f(g) function
    int result2 = h.apply(1); // 3 이 반환된다.
    System.out.println("result2 = " + result2);

    /// 그래서 이걸 어디에 쓸까? 예제 ㄱㄱ
    Function<String, String> addHeader = Letter::addHeader;
    Function<String, String> transformationPipeline = addHeader
        .andThen(Letter::checkSpelling) // 헤더 추가 -> 철자 검사
        .andThen(Letter::addFooter); // 마지막 푸터 추가
  }

  // 예제용 클래스 : 문자열로 구성된 편지 내용을 반환하는 메서드 존재
  public static class Letter {

    public static String addHeader(String text) {
      return "From Raoul.... : " + text;
    }

    public static String addFooter(String text) {
      return text + "Kind regards";
    }

    public static String checkSpelling(String text) {
      return text.replaceAll("labda", "lambda");
    }
  }
}
