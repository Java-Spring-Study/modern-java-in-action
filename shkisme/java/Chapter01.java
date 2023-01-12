import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

public class Chapter01 {

  public static void main(String[] args) {
    File[] hiddenFiles1 = new File(".").listFiles(new FileFilter() { // FileFilter로 감싸고, 전달
      @Override
      public boolean accept(File file) {
        return file.isHidden();
      }
    });
    File[] hiddenFiles2 = new File(".").listFiles(File::isHidden);

    // 1.3.2 코드 넘겨주기 : 예제
    List<Apple> inventory = new ArrayList<>();
    filterApples(inventory, Apple::isGreenApple);
    filterApples(inventory, (Apple a) -> a.getWeight() > 150);
    filterApples(inventory, Apple::isHeavyApple);

    // 1.4 스트림
    List<Transaction> transactions = new ArrayList<>();
        /*Map<Currency, List<Transaction>> transactionsByCurrencies =
                transactions.stream()
                        .filter((Transaction t) -> t.getValue() > 1000)
                        .collect(groupingBy(Transaction::getCurrency));*/

    // 1.4.1
    // 순차 처리 방식의 코드
    List<Apple> heavyApples1 =
        inventory.stream() // 컬렉션을 스트림으로 변환
            .filter((Apple a) -> a.getWeight() > 150) // 병렬로 처리
            .collect(toList()); // 리스트로 다시 복원
    // 병렬 처리 방식의 코드
    List<Apple> heavyApples2 =
        inventory.parallelStream() // 컬렉션을 스트림으로 변환
            .filter((Apple a) -> a.getWeight() > 150) // 병렬로 처리
            .collect(toList()); // 리스트로 다시 복원
  }

  // 1.3.2 코드 넘겨주기 : 예제
  public static List<Apple> filterGreenApples(List<Apple> inventory) {
    List<Apple> result = new ArrayList<>();

    for (Apple apple : inventory) {
      if (Color.GREEN.equals(apple.getColor())) {
        result.add(apple);
      }
    }
    return result;
  }

  public static List<Apple> filterHeavyApples(List<Apple> inventory) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple : inventory) {
      if (apple.getWeight() > 150) {
        result.add(apple);
      }
    }
    return result;
  }

  public interface Predicate<T> {

    boolean test(T t);
  }

  static List<Apple> filterApples(List<Apple> inventory, Predicate<Apple> p) {
    List<Apple> result = new ArrayList<>();
    for (Apple apple : inventory) {
      if (p.test(apple)) {
        result.add(apple);
      }
    }
    return result;
  }
}
