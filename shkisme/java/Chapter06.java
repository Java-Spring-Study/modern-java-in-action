import static java.util.Arrays.asList;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collector.Characteristics.CONCURRENT;
import static java.util.stream.Collector.Characteristics.IDENTITY_FINISH;
import static java.util.stream.Collectors.averagingInt;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.filtering;
import static java.util.stream.Collectors.flatMapping;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.maxBy;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.summarizingInt;
import static java.util.stream.Collectors.summingInt;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Currency;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Chapter06 {

  public static void main(String[] args) {
    List<Dish> menu = asList(
        new Dish("pork", false, 800, Type.MEAT),
        new Dish("beef", false, 700, Type.MEAT),
        new Dish("chicken", false, 400, Type.MEAT),
        new Dish("french fries", true, 530, Type.OTHER),
        new Dish("rice", true, 350, Type.OTHER),
        new Dish("season fruit", true, 120, Type.OTHER),
        new Dish("pizza", true, 550, Type.OTHER),
        new Dish("prawns", false, 300, Type.FISH),
        new Dish("salmon", false, 450, Type.FISH)
    );
    Map<Currency, List<Transaction>> transactionsByCurrencies = new HashMap<>();
    List<Transaction> transactions = new ArrayList<>();

    // 기존의 쉽지 않은 그룹화 코드
    for (Transaction transaction : transactions) {
      Currency currency = transaction.getCurrency();
      List<Transaction> transactionsForCurrency = transactionsByCurrencies.get(currency);
      if (transactionsForCurrency == null) {
        transactionsForCurrency = new ArrayList<>();
        transactionsByCurrencies.put(currency, transactionsForCurrency);
      }
      transactionsForCurrency.add(transaction);
    }

    // 대신 범용적인 컬렉터 파라미터를 collect 메서드에 전달하는 방식으로 구현
    Map<Currency, List<Transaction>> transactionsByCurrencies2 = transactions.stream().collect(
        groupingBy(Transaction::getCurrency));

    // counting() 메서드
    long howManyDishes = menu.stream().collect(counting());
    howManyDishes = menu.stream().count();

    // 6.2.1 스트림값에서 최댓값과 최솟값 검색
    Comparator<Dish> dishCaloriesComparator = comparingInt(Dish::getCalories);
    Optional<Dish> mostCalorieDish = menu.stream().collect(maxBy(dishCaloriesComparator));

    // 6.2.2 요약 연산 - summingInt
    int totalCalories = menu.stream()
        .collect(
            summingInt(Dish::getCalories) // int 로 매핑한 컬렉터를 반환한다.
        ); // 요약 작업을 수행한다.

    // summingDouble - 같은 기능을 수행한다!
    /*int totalCalories2 = menu.stream()
        .collect(
            summingDouble(Dish::getDoubleCalories)
        );*/

    // 평균값 계산도 가능!
    double avgCalories = menu.stream().collect(averagingInt(Dish::getCalories));

    // 두 개 이상의 연산을 한번에 수행하고 싶다면 ! summarizingInt
    IntSummaryStatistics menuStatistics = menu.stream().collect(summarizingInt(Dish::getCalories));
    System.out.println("menuStatistics = " + menuStatistics); // 이런게 있누..
    System.out.println("menuStatistics.getSum() = " + menuStatistics.getSum());

    String shortMenu = menu.stream().map(Dish::getName).collect(joining()); // 모든 요리 명 연결
    System.out.println("shortMenu = " + shortMenu);
    String shortMenu2 = menu.stream().map(Dish::getName).collect(joining(","));
    System.out.println("shortMenu2 = " + shortMenu2);

    // 6.2.4 범용 리듀싱 요약 연산 - 위의 연산은 모두 reduce 로 가능하다!
    int totalCalories2 = menu.stream()
        .collect(reducing(0, Dish::getCalories, (i, j) -> i + j));

    Optional<Dish> mostCalorieDish2 = menu.stream().collect(reducing((d1, d2) ->
        d1.getCalories() > d2.getCalories() ? d1 : d2));

    // collect 와 reduce
    Stream<Integer> stream = asList(1, 2, 3, 4, 5, 6).stream();
    List<Integer> numbers = stream.reduce(new ArrayList<Integer>(),
        (List<Integer> l, Integer e) -> {
          l.add(e);
          return l;
        }, (List<Integer> l1, List<Integer> l2) -> {
          l1.addAll(l2);
          return l1;
        });

    // 컬렉션 프레임워크의 유연성 : 같은 연산도 다양한 방식으로
    int totalCalories3 = menu.stream().collect(reducing(0, Dish::getCalories, Integer::sum));
    Optional<Integer> totalCalories4 = menu.stream().map(Dish::getCalories).reduce(Integer::sum);
    int totalCalories5 = menu.stream().map(Dish::getCalories).reduce(Integer::sum).get();
    int totalCalories6 = menu.stream().mapToInt(Dish::getCalories).sum();

    // 퀴즈 6-1 , reducing 은 BinaryOperator<T>를 인수로 받는다. 즉, BiFunction<T,T,T> 를 인수로 받는다.

    // 6.3 그룹화
    Map<Type, List<Dish>> dishesByType = menu.stream().collect(groupingBy(Dish::getType));
    System.out.println(dishesByType);

    // 메서드 참조 대신 람다 표현식으로도 필요한 분류 로직을 구현할 수 있다.
    Map<CaloricLevel, List<Dish>> dishesByCaloricLevel = menu.stream().collect(groupingBy(
        dish -> {
          if (dish.getCalories() <= 400) {
            return CaloricLevel.DIET;
          } else if (dish.getCalories() <= 700) {
            return CaloricLevel.NORMAL;
          } else {
            return CaloricLevel.FAT;
          }
        }
    ));

    // 6.3.1 그룹화된 요소 조작
    Map<Type, List<Dish>> caloricDishesByType = menu.stream()
        .filter(dish -> dish.getCalories() > 500)
        .collect(groupingBy(Dish::getType));
    System.out.println(caloricDishesByType); // FISH 종류 요리 자체가 키에서 사라져버리는 문제.
    // 해결 - 두 번째 인수에 필터 프레디케이트를 받아서 해결하는 Collectors 클래스.
    Map<Type, List<Dish>> caloricDishesByType2 = menu.stream()
        .collect(groupingBy(Dish::getType, filtering(dish -> dish.getCalories() > 500, toList())));
    System.out.println(caloricDishesByType2);

    // 또 다른 유용한 기능 - 매핑 함수를 이용해 요소를 변환하는 작업
    Map<Type, List<String>> dishNamesByType = menu.stream()
        .collect(groupingBy(Dish::getType, mapping(Dish::getName, toList())));
    System.out.println(dishNamesByType);

    // flatMapping 컬렉터를 이용

    Map<String, List<String>> dishTags = new HashMap<>();
    dishTags.put("pork", asList("greasy", "salty"));
    dishTags.put("beef", asList("salty", "roasted"));
    dishTags.put("chicken", asList("fried", "crisp"));
    dishTags.put("french fries", asList("greasy", "fried"));
    dishTags.put("rice", asList("light", "natural"));
    dishTags.put("season fruit", asList("fresh", "natural"));
    dishTags.put("pizza", asList("tasty", "salty"));
    dishTags.put("prawns", asList("tasty", "roasted"));
    dishTags.put("salmon", asList("delicious", "fresh"));

    Map<Type, Set<String>> dishNamesByType2 = menu.stream()
        .collect(groupingBy(Dish::getType,
            flatMapping(dish -> dishTags.get(dish.getName()).stream(), toSet())));
    System.out.println(dishNamesByType2);

    // 6.3.2 다수준 그룹화
    Map<Type, Map<CaloricLevel, List<Dish>>> dishesByTypeCaloricLevel = menu.stream()
        .collect(groupingBy(Dish::getType,
            groupingBy(dish -> {
              if (dish.getCalories() <= 400) {
                return CaloricLevel.DIET;
              } else if (dish.getCalories() <= 700) {
                return CaloricLevel.NORMAL;
              } else {
                return CaloricLevel.FAT;
              }
            })
        ));
    System.out.println(dishesByTypeCaloricLevel);

    // 6.3.3 서브그룹으로 데이터 수집 - groupingBy로 넘겨주는 컬렉터의 형식은 제한이 없다.
    Map<Type, Long> typesCount = menu.stream().collect(groupingBy(Dish::getType, counting()));
    System.out.println(typesCount);

    // 요리의 종류를 분류하는 컬렉터로 메뉴에서 가장 높은 칼로리를 가진 요리를 찾는 프로그램도 다시 구현
    Map<Type, Optional<Dish>> mostCaloricByType = menu.stream()
        .collect(groupingBy(Dish::getType, maxBy(comparingInt(Dish::getCalories))));

    // 컬렉터 결과를 다른 형식에 적용하기
    Map<Type, Dish> mostCaloricByType2 = menu.stream().collect(groupingBy(Dish::getType,
        collectingAndThen(maxBy(comparingInt(Dish::getCalories))
            // 적용할 컬렉터와, 변환 함수를 인수로 받아서 다른 컬렉터를 반환한다.
            , Optional::get)));

    // groupingBy 와 함께 사용하는 다른 컬렉터 예제
    Map<Type, Integer> totalCaloriesByType3 = menu.stream()
        .collect(groupingBy(Dish::getType, summingInt(Dish::getCalories)));

    Map<Type, Set<CaloricLevel>> caloricLevelsByType = menu.stream()
        .collect(groupingBy(Dish::getType, mapping(dish -> {
              if (dish.getCalories() <= 400) {
                return CaloricLevel.DIET;
              } else if (dish.getCalories() <= 700) {
                return CaloricLevel.NORMAL;
              } else {
                return CaloricLevel.FAT;
              }
            }, toSet()
        )));

    Map<Type, HashSet<CaloricLevel>> caloricLevelsByType2 = menu.stream()
        .collect(groupingBy(Dish::getType, mapping(dish -> {
          if (dish.getCalories() <= 400) {
            return CaloricLevel.DIET;
          } else if (dish.getCalories() <= 700) {
            return CaloricLevel.NORMAL;
          } else {
            return CaloricLevel.FAT;
          }
        }, toCollection(HashSet::new))));

    // 6.4
    Map<Boolean, List<Dish>> partitionedMenu = menu.stream()
        .collect(partitioningBy(Dish::isVegetarian));
    System.out.println(partitionedMenu);
    // 분할의 장점 - 참, 거짓 두 가지 요소의 스트림 리스트를 모두 유지한다.
    Map<Boolean, Map<Type, List<Dish>>> vegetarianDishesByType = menu.stream()
        .collect(partitioningBy(Dish::isVegetarian, groupingBy(Dish::getType)));
    System.out.println(vegetarianDishesByType);

    Map<Boolean, Dish> mostCaloricPartitionedByVegetarian = menu.stream()
        .collect(partitioningBy(Dish::isVegetarian,
            collectingAndThen(maxBy(comparingInt(Dish::getCalories)), Optional::get)));
    System.out.println(mostCaloricPartitionedByVegetarian);

    // 퀴즈 6 - 2
    Map<Boolean, Map<Boolean, List<Dish>>> collect1 = menu.stream().collect(
        partitioningBy(Dish::isVegetarian, partitioningBy(dish -> dish.getCalories() > 500)));

    //menu.stream().collect(partitioningBy(Dish::isVegetarian, partitioningBy(Dish::getType)));

    Map<Boolean, Long> collect = menu.stream()
        .collect(partitioningBy(Dish::isVegetarian, counting()));

    // 직접 만든 컬렉터를 toList() 대신 사용할 수 있다.
    List<Dish> dishes = menu.stream().collect(new ToListCollector<>());

    // 컬렉터를 구현하지 않고도 커스텀 수집을 수행할 수 있다.
    List<Dish> dishes2 = menu.stream().collect(
        ArrayList::new, // 발행
        List::add, // 누적
        List::addAll); // 합침

    // 직접 만든 커스텀 컬렉터로 예제 고쳐보기
    Map<Boolean, List<Integer>> collect2 = IntStream.rangeClosed(2, 100).boxed()
        .collect(new PrimeNumbersCollector());

    // 6.6.2 컬렉터 성능 비교
    long fastest = Long.MAX_VALUE;
    for (int i = 0; i < 10; ++i) {
      long start = System.nanoTime();
      partitionPrimes(1_000_000);
      long duration = (System.nanoTime() - start) / 1_000_000;
      if (duration < fastest) {
        fastest = duration;
      }
    }
    System.out.println("[partitionPrimes] Fastest execution done in " + fastest + " msecs");

    long fastest2 = Long.MAX_VALUE;
    for (int i = 0; i < 10; ++i) {
      long start2 = System.nanoTime();
      partitionPrimesWithCustomCollector(1_000_000);
      long duration2 = (System.nanoTime() - start2) / 1_000_000;
      if (duration2 < fastest2) {
        fastest2 = duration2;
      }
    }
    System.out.println("[partitionPrimesWithCustomCollector] Fastest execution done in " + fastest2
        + " msecs"); // 성능이 약 30퍼센트 향상되었따!

    // 가독성은 매우 떨어지지만, collect 세 함수를 전달해서 같은 결과를 얻을 수 있다.
    IntStream.rangeClosed(2, 100).boxed().collect(
        () -> new HashMap<Boolean, List<Integer>>() {
          {
            put(true, new ArrayList<Integer>());
            put(false, new ArrayList<Integer>());
          }
        },
        (acc, candidate) -> {
          acc.get(isPrime_conveyList(acc.get(true), candidate))
              .add(candidate);
        },
        (map1, map2) -> {
          map1.get(true).addAll(map2.get(true));
          map2.get(false).addAll(map2.get(false));
        }
    );
  }

  // 6.4.2 숫자를 소수와 비소수로 분할하기
  public static boolean isPrime(int candidate) {
    return IntStream.range(2, candidate)
        .noneMatch(i -> candidate % i == 0);
  }

  public static boolean isPrime2(int candidate) {
    int candidateRoot = (int) Math.sqrt(candidate); // 주어진 수의 제곱근 이하로 제한
    return IntStream.rangeClosed(2, candidateRoot)
        .noneMatch(i -> candidate % i == 0);
  }

  public static Map<Boolean, List<Integer>> partitionPrimes(int n) {
    return IntStream.rangeClosed(2, n).boxed()
        .collect(partitioningBy(Chapter06::isPrime2)); // 구현한 isPrime2 메서드를 프레디케이트로 이용하기!
  }

  // 6.6 커스텀 컬렉터를 구현해서 성능 개선하기 - 중간 결과 리스트를 전달하도록 메서드 생성
  public static boolean isPrime_conveyList(List<Integer> primes, int candidate) {
    int candidateRoot = (int) Math.sqrt(candidate);
    return primes.stream()
        .takeWhile(i -> i
            <= candidateRoot) // filter 는 전체 스트림을 처리한 다음에 결과를 반환하므로 성능 이슈가 있을 수 있음. takeWhile로 처리.
        .noneMatch(i -> candidate % i == 0);
  }

  public static class PrimeNumbersCollector implements
      Collector<Integer, Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> {

    @Override
    public Supplier<Map<Boolean, List<Integer>>> supplier() {
      return () -> new HashMap<>() {
        {
          put(true, new ArrayList<>());
          put(false, new ArrayList<>());
        }
      };
    }

    @Override
    public BiConsumer<Map<Boolean, List<Integer>>, Integer> accumulator() {
      return (Map<Boolean, List<Integer>> acc, Integer candidate) -> {
        acc.get(isPrime_conveyList(acc.get(true), candidate))
            .add(candidate);
      };
    }

    @Override
    public BinaryOperator<Map<Boolean, List<Integer>>> combiner() {
      return (Map<Boolean, List<Integer>> map1, Map<Boolean, List<Integer>> map2) -> {
        map1.get(true).addAll(map2.get(true));
        map1.get(false).addAll(map2.get(false));
        return map1;
      };
    }

    @Override
    public Function<Map<Boolean, List<Integer>>, Map<Boolean, List<Integer>>> finisher() {
      return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
      return Collections.unmodifiableSet(EnumSet.of(IDENTITY_FINISH));
    }
  }

  public static Map<Boolean, List<Integer>> partitionPrimesWithCustomCollector(int n) {
    return IntStream.rangeClosed(2, n).boxed()
        .collect(new PrimeNumbersCollector());
  }

  // counting 컬렉터를 reducing 을 이용해 구현할 수 있다.
  public static <T> Collector<T, ?, Long> counting() {
    return reducing(0L, e -> 1L, Long::sum);
  }

  // Stream<T> 의 모든 요소를 List<T> 로 수집하는 클래스 구현
  public static class ToListCollector<T> implements Collector<T, List<T>, List<T>> {

    @Override
    public Supplier<List<T>> supplier() {
      return ArrayList::new;
    }

    @Override
    public BiConsumer<List<T>, T> accumulator() {
      return List::add;
    }

    @Override
    public BinaryOperator<List<T>> combiner() {
      return (list1, list2) -> {
        list1.addAll(list2);
        return list1;
      };
    }

    @Override
    public Function<List<T>, List<T>> finisher() {
      return Function.identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
      return Collections.unmodifiableSet(EnumSet.of(IDENTITY_FINISH, CONCURRENT));
    }
  }

}
