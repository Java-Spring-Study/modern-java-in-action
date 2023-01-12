import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.IntSupplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Chapter05 {

  public static void main(String[] args) {
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
    List<Dish> specialMenu = Arrays.asList( // 칼로리 오름차순으로 정렬된 리스트
        new Dish("season fruit", true, 120, Type.OTHER),
        new Dish("prawns", false, 300, Type.FISH),
        new Dish("rice", true, 350, Type.OTHER),
        new Dish("chicken", false, 400, Type.MEAT),
        new Dish("french fires", true, 530, Type.OTHER)
    );
    // 5.1.1 프레디케이트로 필터링 - filter
    List<Dish> vegetarianMenu = menu.stream()
        .filter(Dish::isVegetarian)
        .collect(toList());

    // 5.1.2 고유 요소 필터링 - distinct
    List<Integer> numbers = Arrays.asList(1, 2, 1, 3, 3, 2, 4);
    numbers.stream()
        .filter(integer -> integer % 2 == 0)
        .distinct() // 고유 여부는 스트림에서 만든 객체의 hashCode, equals 로 결정된다.
        .forEach(System.out::println);

    // 5.2.1 프레디케이트를 이용한 슬라이싱 - takeWhile (리스트는 미리 정렬되어 있어야 한다)
    List<Dish> sliceMenu = specialMenu.stream()
        .takeWhile(dish -> dish.getCalories() < 320)
        .collect(toList());
    System.out.println(sliceMenu);
    // 5.2.1 프레디케이트를 이용한 슬라이싱 - dropWhile (리스트는 미리 정렬되어 있어야 한다)
    List<Dish> sliceMenu2 = specialMenu.stream()
        .dropWhile(dish -> dish.getCalories() < 320)
        .collect(toList());
    System.out.println(sliceMenu2);
    // 5.2.2 스트림 축소 - limit
    List<Dish> dishes = specialMenu.stream()
        .filter(dish -> dish.getCalories() > 300)
        .limit(3)
        .collect(toList());
    System.out.println(dishes);
    // 5.2.3 요소 건너뛰기 - skip
    List<Dish> dishes1 = specialMenu.stream()
        .filter(dish -> dish.getCalories() > 300)
        .skip(2)
        .collect(toList());
    System.out.println(dishes1);
    // 퀴즈 5-1 처음 등장하는 두 고기 요리를 필터링하라!
    List<Dish> collect = menu.stream()
        .filter(dish -> dish.getType() == Type.MEAT)
        .limit(2)
        .collect(toList());
    // 5.3.1 - 스트림의 각 요소에 함수 적용하기
    List<String> dishNames = menu.stream()
        .map(Dish::getName)
        .collect(toList());
    List<Integer> dishNameLengths = menu.stream()
        .map(Dish::getName)
        .map(String::length)
        .collect(toList());
    List<String> words = Arrays.asList("Modern", "Java", "In", "KEEPER");
    List<Integer> wordLengths = words.stream()
        .map(String::length)
        .collect(toList());
    // 5.3.2 - 스트림 평면화 - 중복 요소 제거 X
    List<String[]> collect1 = words.stream()
        .map(word -> word.split(""))
        .distinct()
        .collect(toList());
    // Arrays.stream - 문자열을 받아서 문자열 스트림을 반환해준다.
    String[] arrayOfWords = {"Goodbye", "World"};
    Stream<String> stream = Arrays.stream(arrayOfWords);

    // 5.3.2 해결 - 그러나 실패
    List<Stream<String>> collect2 = words.stream()
        .map(word -> word.split(""))
        .map(Arrays::stream)
        .distinct()
        .collect(toList());
    // 5.3.2 해결 - flatMap 사용
    List<String> collect3 = words.stream()
        .map(word -> word.split(""))
        .flatMap(Arrays::stream)
        .distinct()
        .collect(toList());
    System.out.println(collect3);

    // 퀴즈 5-2-1번
    List<Integer> integers = List.of(1, 2, 3, 4, 5);
    List<Integer> collect4 = integers.stream()
        .map(integer -> Math.pow(integer, 2))
        .map(Double::intValue)
        .collect(toList());
    System.out.println(collect4);
    // 퀴즈 5-2-2번
    List<Integer> integers1 = List.of(1, 2, 3);
    List<Integer> integers2 = List.of(3, 4);
    List<Pair> collect5 = integers1.stream()
        .flatMap(integer -> integers2.stream().map(integer1 -> new Pair(integer, integer1)))
        .collect(toList());
    System.out.println(collect5);
    // 퀴즈 5-3-3번
    List<Pair> collect6 = integers1.stream()
        .flatMap(integer -> integers2.stream().map(integer1 -> new Pair(integer, integer1)))
        .filter(pair -> (pair.getX() + pair.getY()) % 3 == 0)
        .collect(toList());
    System.out.println(collect6);

    // 5.4.1 anyMatch
    if (menu.stream().anyMatch(Dish::isVegetarian)) {
      System.out.println("채식이 포함되어 있어용");
    }
    // 5.4.2 allMatch
    boolean b = menu.stream().allMatch(dish -> dish.getCalories() < 1000);
    // noneMatch
    boolean b1 = menu.stream().noneMatch(dish -> dish.getCalories() >= 1000);

    // 5.4.3 요소 검색
    Optional<Dish> any = menu.stream()
        .filter(Dish::isVegetarian)
        .findAny();
    System.out.println(any);

    // 값이 없을 때 어떻게 처리할지 강제하는 기능을 제공하는 Optional.
    boolean any1 = menu.stream()
        .filter(Dish::isVegetarian)
        .findAny().isPresent(); // 값이 있으면 true, 없으면 false
    menu.stream()
        .filter(Dish::isVegetarian)
        .findAny().ifPresent(dish -> System.out.println(dish.getName())); // 값이 있으면 블록 실행
    menu.stream()
        .filter(Dish::isVegetarian)
        .findAny().get(); // 값이 존재하면 값을 반환, 아니면 NoSuchElementException
    menu.stream()
        .filter(Dish::isVegetarian)
        .findAny().orElse(null); // 값이 있으면 값을 반환, 아니면 괄호 안의 기본값을 반환.

    // 5.4.4 첫 번째 요소 찾기 - findFirst
    List<Integer> integers3 = Arrays.asList(1, 2, 3, 4, 5);
    integers3.stream()
        .map(n -> n * n)
        .filter(n -> n % 3 == 0)
        .findFirst();

    // 5.5.1 요소의 합 - 이전에는.. 이렇게 짰었다.
    int sum = 0;
    List<Integer> numbers2 = List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    for (int x : numbers2) {
      sum += x;
    }
    // 5.5.1 요소의 합 - 이제는 reduce 를 이용하자!
    int sum2 = numbers2.stream().reduce(0, (a, c) -> a + c);

    // 퀴즈 5-3
    int reduce = menu.stream().map(dish -> 1).reduce(0, Integer::sum);
    System.out.println("reduce = " + reduce);
    long cnt = menu.stream().count();
    System.out.println("cnt = " + cnt);
    System.out.println("menu.size() = " + menu.size());

    // 5.6 실전 연습
    System.out.println("======5.6 실전 예제=====");
    Trader raoul = new Trader("Raoul", "Cambridge");
    Trader mario = new Trader("Mario", "Milan");
    Trader alan = new Trader("Alan", "Cambridge");
    Trader brian = new Trader("Brian", "Cambridge");
    List<Transaction> transactions = Arrays.asList(
        new Transaction(brian, 2011, 300),
        new Transaction(raoul, 2012, 1000),
        new Transaction(raoul, 2011, 400),
        new Transaction(mario, 2012, 710),
        new Transaction(mario, 2012, 700),
        new Transaction(alan, 2012, 950)
    );
    System.out.print("1번. 2011년에 일어난 모든 트랜잭션을 찾아 값을 오름차순으로 정리하기 : ");
    List<Transaction> collect7 = transactions.stream()
        .filter(transaction -> transaction.getYear() == 2011)
        .sorted(comparing(Transaction::getValue)).collect(toList());
    //.map(Transaction::getValue).sorted().collect(toList());
    System.out.println(collect7);

    System.out.print("2번. 거래자가 근무하는 모든 도시를 중복 없이 나열하기 : ");
    List<String> collect8 = transactions.stream().map(Transaction::getTrader).map(Trader::getCity)
        .distinct()
        .collect(toList());
    System.out.println(collect8);

    System.out.print("3번. 케임브리지에서 근무하는 모든 거래자를 찾아서 이름순으로 정렬하기 : ");
    /*List<String> collect9 = transactions.stream()
        .map(Transaction::getTrader)
        .filter(trader -> trader.getCity().equals("Cambridge"))
        .map(Trader::getName)
        .distinct()
        .sorted()
        .collect(toList());*/
    List<Trader> collect9 = transactions.stream().map(Transaction::getTrader)
        .filter(trader -> trader.getCity().equals("Cambridge"))
        .distinct()
        .sorted(comparing(Trader::getName))
        .collect(toList());
    System.out.println(collect9);

    System.out.print("4번. 모든 거래자의 이름을 알파벳 순으로 정렬해서 반환하기 : ");
    List<String> collect10 = transactions.stream()
        .map(Transaction::getTrader)
        .map(Trader::getName)
        .distinct()
        .sorted()
        .collect(toList());
    System.out.println(collect10);

    System.out.print("5번. 밀라노에 거래자가 있는가? : ");
    boolean milan = transactions.stream().map(Transaction::getTrader)
        .anyMatch(trader -> trader.getCity().equals("Milan"));
    System.out.println(milan);

    System.out.print("6번. 케임브리지에 거주하는 거래자의 모든 트랜잭션 값을 출력하시오 : ");
    List<Integer> cambridge = transactions.stream()
        .filter(transaction -> transaction.getTrader().getCity().equals("Cambridge"))
        .map(Transaction::getValue).collect(toList());
    System.out.println(cambridge);

    System.out.print("7번. 전체 트랜잭션 중 최댓값은 얼마인가? : ");
    int max = transactions.stream().map(Transaction::getValue).max(new Comparator<Integer>() {
      @Override
      public int compare(Integer o1, Integer o2) {
        return o1 - o2;
      }
    }).orElse(0);

    transactions.stream().map(Transaction::getValue).reduce(Integer::max);

    System.out.println(max);

    System.out.print("8번. 전체 트랜잭션 중 최솟값은 얼마인가? : ");
    int min = transactions.stream().map(Transaction::getValue).min(new Comparator<Integer>() {
      @Override
      public int compare(Integer o1, Integer o2) {
        return o1 - o2;
      }
    }).orElse(0);
    // 다양한 방법이 있군...
    transactions.stream().map(Transaction::getValue).reduce(Integer::min);
    transactions.stream().reduce((t1, t2) -> t1.getValue() < t2.getValue() ? t1 : t2);
    transactions.stream().min(comparing(Transaction::getValue));

    System.out.println(min);
    System.out.println("======5.6 실전 예제=====");

    // 5.7 오토박싱 비용이 숨어 있는 !
    int calories = menu.stream().map(Dish::getCalories).reduce(0, Integer::sum);

    // 5.7.1 기본형 스트림
    int sum1 = menu.stream()
        .mapToInt(Dish::getCalories)
        .sum();
    Stream<Integer> boxed = menu.stream().mapToInt(Dish::getCalories).boxed();

    // 기본값 : OptionalInt
    int max1 = menu.stream()
        .mapToInt(Dish::getCalories)
        .max().orElse(1); // 기본값을 명시.

    // 5.7.2 숫자 범위
    long count1 = IntStream.range(1, 100).filter(n -> n % 2 == 0).count();
    long count2 = IntStream.rangeClosed(1, 100).filter(n -> n % 2 == 0).count();
    System.out.println("count1 = " + count1);
    System.out.println("count2 = " + count2);

    // 5.7.3 - 피타고라스 수 스트림 만들기
    Stream<int[]> pythagoreanTriples = IntStream.rangeClosed(1, 100).boxed() // 1~100 까지 만듦
        .flatMap(a -> IntStream.rangeClosed(a,
                100) // 세 수의 스트림을 만듬. 스트림 a 값을 매핑하면 스트림의 스트림이 만들어지므로, flatMap
            .filter(d -> Math.sqrt(a * a + d * d) % 1 == 0) // ~의 제곱근이 정수인지 확인.
            .mapToObj(d -> new int[]{a, d, (int) Math.sqrt(a * a + d * d)}));

    // 개선한 피타고라스 수 스트림 만들기 - 제곱근을 두 번 계산하는 문제 해결
    Stream<double[]> pythagoreanTriples2 = IntStream.rangeClosed(1, 100).boxed()
        .flatMap(a -> IntStream.rangeClosed(a, 100)
            .mapToObj(e -> new double[]{a, e, Math.sqrt(a * a + e * e)})
            .filter(t -> t[2] % 1 == 0)); // 세 번째 수가 정수인지 확인

    // 5.8.1 값으로 스트림 만들기
    Stream<String> modern = Stream.of("Modern", "Java", "In", "Action");
    stream.map(String::toUpperCase).forEach(System.out::println);
    Stream<String> emptyStream = Stream.empty(); // empty 메서드로 스트림을 비울 수 있다.

    // 5.8.2 null 이 될 수 있는 객체로 스트림 만들기
    String home = System.getProperty("home");
    Stream<String> homeValueStream = home == null ? Stream.empty() : Stream.of(home);
    Stream<String> homeValueStream2 = Stream.ofNullable(System.getProperty("home"));
    Stream<String> values = Stream.of("config", "home", "user")
        .flatMap(key -> Stream.ofNullable(System.getProperty(key)));

    // 5.8.3 배열로 스트림 만들기.
    int[] num = {2, 3, 4, 5};
    int sum3 = Arrays.stream(num) // IntStream 으로 변환하기
        .sum();

    // 5.8.4 파일로 스트림 만들기
    long uniqueWorlds = 0;
    try (Stream<String> lines = Files.lines(Paths.get("data.txt"), Charset.defaultCharset())) {
      uniqueWorlds = lines.flatMap(line -> Arrays.stream(line.split(" ")))
          .distinct()
          .count();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // 5.8.5 함수로 무한 스트림 만들기
    Stream.iterate(0, n -> n + 2) // 초깃값 0
        .limit(10)
        .forEach(System.out::println);

    // 퀴즈 5-4 피보나치 수열 집합 - 직접 메서드를 써보면서 익혀보잡!
    Stream.iterate(new int[]{0, 1}, (num1) -> new int[]{num1[1], num1[0] + num1[1]})
        .limit(20)
        .forEach(t -> System.out.println("(" + t[0] + ","));

    // 100보다 크면 숫자 생성을 중단하는 코드를 iterate 로 구현해보기!
    IntStream.iterate(0, n -> n < 100, n -> n + 4)
        .forEach(System.out::println);

    IntStream.iterate(0, n -> n + 4)
        // .filter(n -> n < 100) // 이건 안된다! 언제 이 작업을 중단하는지 filter 메서드는 모름.
        .takeWhile(n -> n < 100) // 쇼트서킷 연산을 이용해야 한다.
        .forEach(System.out::println);

    // generate 메서드
    Stream.generate(Math::random)
        .limit(5)
        .forEach(System.out::println);

    IntStream ones = IntStream.generate(() -> 1); // IntSupplier 를 인수로 받는다.

    // 안전하지 않은 예제
    IntStream twos = IntStream.generate(new IntSupplier() {
      @Override
      public int getAsInt() { // getAsInt 메서드의 연산을 커스터마이즈할 수 있는 상태 필드를 정의할 수 있다. 부작용이 생길 수 있다.
        return 2;
      }
    });

    IntSupplier fib = new IntSupplier() {
      private int previous = 0;
      private int current = 1;

      @Override
      public int getAsInt() {
        int oldPrevious = this.previous;
        int nextValue = this.previous + this.current;
        this.previous = this.current;
        this.current = nextValue;
        return oldPrevious;
      }
    };
    IntStream.generate(fib).limit(10).forEach(System.out::println);
  }
}
