import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class Chapter04 {

  public static void main(String[] args) {
    // 4.1 스트림 예제 - 기존의 방식
    List<Dish> menu1 = new ArrayList<>(); // menu 들을 담은 리스트
    List<Dish> lowCaloricDishes = new ArrayList<>();
    for (Dish dish : menu1) {
      if (dish.getCalories() < 400) {
        lowCaloricDishes.add(dish);
      }
    }
    Collections.sort(lowCaloricDishes, new Comparator<Dish>() {
      public int compare(Dish d1, Dish d2) {
        return Integer.compare(d1.getCalories(), d2.getCalories());
      }
    });
    List<String> lowCaloricDishesName = new ArrayList<>();
    for (Dish dish : lowCaloricDishes) {
      lowCaloricDishesName.add(dish.getName());
    }
    // 최신 코드로 바꿔보기 - 스트림 사용
    List<String> lowCaloricDishesName_stream = menu1.stream()
        .filter(dish -> dish.getCalories() < 400)
        .sorted(comparing(Dish::getCalories))
        .map(Dish::getName)
        .collect(toList());
    // parallelStream - 멀티코어 아키텍처에서 병렬로 실행 가능
    List<String> lowCaloricDishesName_parallelStream = menu1.parallelStream()
        .filter(dish -> dish.getCalories() < 400)
        .sorted(comparing(Dish::getCalories))
        .map(Dish::getName)
        .collect(toList());

    // p. 139 예제
    List<Dish> menu = Arrays.asList(
        new Dish("pork", false, 800, Type.MEAT),
        new Dish("beef", false, 700, Type.MEAT),
        new Dish("chicken", false, 400, Type.MEAT),
        new Dish("french fires", true, 530, Type.OTHER),
        new Dish("rice", true, 350, Type.OTHER),
        new Dish("season fruit", true, 120, Type.OTHER),
        new Dish("pizza", true, 550, Type.OTHER),
        new Dish("prawns", false, 300, Type.FISH),
        new Dish("salmon", false, 450, Type.FISH)
    );
    // p. 142 실험 - 데이터 처리 연산은 서로 파이프라인을 형성할 수 있도록 스트림을 반환한다고 한다.
    Stream<Dish> dishStream = menu.stream().filter(dish -> dish.getCalories() < 300);

    // 4.3.1 스트림은 단 한 번만 소비할 수 있다!
    List<String> title = Arrays.asList("JAVA 8", "IN", "PNU");
    Stream<String> s = title.stream();
    s.forEach(System.out::println);
    //s.forEach(System.out::println); // java.lang.IllegalStateException

    // 퀴즈 4_1 - 리팩토링 헤보기
    List<String> highCaloricDishes = new ArrayList<>();
    Iterator<Dish> iterator = menu.iterator();
    while (iterator.hasNext()) {
      Dish dish = iterator.next();
      if (dish.getCalories() > 300) {
        highCaloricDishes.add(dish.getName());
      }
    }
    List<String> answer = menu.stream()
        .filter(dish -> dish.getCalories() > 300)
        .map(Dish::getName)
        .collect(toList());

    // p.151 예제 코드
    List<String> names = menu.stream()
        .filter(dish -> {
          System.out.println("dish.getName() = " + dish.getName());
          return dish.getCalories() > 300;
        })
        .map(dish -> {
          System.out.println("dish.getName() = " + dish.getName());
          return dish.getName();
        })
        .limit(3)
        .collect(toList());
  }

}
