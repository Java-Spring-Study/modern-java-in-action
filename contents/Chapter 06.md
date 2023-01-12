## 컬렉터

```java
Map<Currency, List<Transaction>> transactionsByCurrencies = new HashMap<>();

for (Transaction transaction : transactions) {
    Currency currency = transaction.getCurrency();
    List<Transaction> transactionsForCurrency = 
            transactionsByCurrencies.get(currency);
    if (transactionsForCurrency == null) {
        transactionsForCurrency = new ArrayList<>();
        transactionsByCurrencies.put(currency, transactionsForCurrency);
    }
    transactionsForCurrency.add(transaction);
}
```

```java
Map<Currency, List<Transaction>> transactionsByCurrencies = 
    transactions.stream().collect(groupingBy(Transaction::getCurrency));
```

다수준 그룹화를 할 때 컬렉터를 사용하여 위 코드처럼 *간결하고 유연하게* 작성할 수 있다.

위 예시를 보면 직접 구현한 코드들이 `collect`으로 리듀싱 연산을 이용해서 컬렉터가 작업을 처리한다.

`Collectors` 클래스에는 다양한 유틸리티 메서드들을 제공한다.

데이터 자체를 변환하는 것보다는 데이터 저장 구조를 변환할 때가 많다. 위 예제 코드도 `transactions`를 `Map`으로 구조를 변환하고 있다.

컬렉터를 사용하면 스트림의 모든 항목을 하나의 결과로 합칠 수 있다.

## `Collectors` 클래스

### counting()

```java
public static <T> Collector<T, ?, Long> counting() {
    return summingLong((e) -> {
        return 1L;
    });
}
```

```java
Stream<String> num = Stream.of("1", "2", "3", "4");
  
long cnt = num.collect(Collectors.counting());
// displaying the required count
System.out.println(cnt);

// 4
```

`counting()`을 사용하면 위 코드와 같이 요소의 개수를 얻어낼 수 있다. 위 코드는 간단한 예시이지만, `counting()`은 다른 컬렉터와 같이 사용할 때 위력을 발휘한다.

### maxBy, minBy

```java
public static <T> Collector<T, ?, Optional<T>> minBy(Comparator<? super T> comparator) {
    return reducing(BinaryOperator.minBy(comparator));
}

public static <T> Collector<T, ?, Optional<T>> maxBy(Comparator<? super T> comparator) {
    return reducing(BinaryOperator.maxBy(comparator));
}
```

```java
Comparator<Dish> dishCaloriesComparator = 
    Comparator.comparingInt(Dish::getCalories);

Optional<Dish> mostCalorieDish = 
    menu.stream()
        .collect(maxBy(dishCaloriesComparator));
```

책에 나온 위 예제 코드를 보면 알 수 있듯이 `minBy`, `maxBy`는 `Comparator`를 구현하여 어떤 방식으로 스트림의 요소를 비교할 것인지 정의한다.

또한 `minBy`, `maxBy`의 반환 타입이 `Optional<T>`임을 확인할 수 있는데, 이는 5장에서 언급했던 이유와 동일하게 스트림에 데이터가 무조건 있다고 가정할 수 없기 때문이다.

### summingInt, summingLong, summingDouble

```java
public static <T> Collector<T, ?, Integer> summingInt(ToIntFunction<? super T> mapper) {}

public static <T> Collector<T, ?, Long> summingLong(ToLongFunction<? super T> mapper) {}

public static <T> Collector<T, ?, Double> summingDouble(ToDoubleFunction<? super T> mapper) {}
```

```java
public static void main(String[] args) {
    List<Dish> menu = new ArrayList<>();
    menu.add(new Dish("test", 80));
    menu.add(new Dish("test2", 90));
    menu.add(new Dish("test3", 100));
    int result = menu.stream().collect(summingInt(Dish::getCalories));
    System.out.println("result = " + result);
}

static public class Dish {
    private String name;
    private int cal;

    public Dish(String name, int cal) {
        this.name = name;
        this.cal = cal;
    }

    public String getName() {
        return name;
    }

    public int getCalories() {
        return cal;
    }
}

// result = 270
```

`summingXXX` 메서드는 요약 연산에 사용되며, 위 코드는 클래스에 구현되어 있는 `getCalories` 메서드로 칼로리를 받아와서 총 칼로리를 계산하고 있다. 

이전에 `reduce` 관련 예제 코드에서는 `a + b` 연산으로 숫자 값이 누적되었는데, `getCalories` 메서드를 활용하여 누적 계산하는 방법을 `reduce`로 만들어보려고 생각하면 좋은 방법이 떠오르지 않는다.

확실히 위 예제를 통해 *간결하고 유연하게* 코드를 작성할 수 있다는게 무슨 뜻인지 이해할 수 있었다.

### summarizingInt, summarizingLong, summarizingDouble

```java
public static <T> Collector<T, ?, IntSummaryStatistics> summarizingInt(ToIntFunction<? super T> mapper) {}

public static <T> Collector<T, ?, LongSummaryStatistics> summarizingLong(ToLongFunction<? super T> mapper) {}

public static <T> Collector<T, ?, DoubleSummaryStatistics> summarizingDouble(ToDoubleFunction<? super T> mapper) {}
```

```java
public static void main(String[] args) {
    List<Dish> menu = new ArrayList<>();
    menu.add(new Dish("test", 80));
    menu.add(new Dish("test2", 90));
    menu.add(new Dish("test3", 100));
    IntSummaryStatistics result = menu.stream().collect(summarizingInt(Dish::getCalories));
    System.out.println("result = " + result);
}

static public class Dish {
    private String name;
    private int cal;

    public Dish(String name, int cal) {
        this.name = name;
        this.cal = cal;
    }

    public String getName() {
        return name;
    }

    public int getCalories() {
        return cal;
    }
}

// result = IntSummaryStatistics{count=3, sum=270, min=80, average=90.000000, max=100}
```

일반적인 수학 계산에서 필요한 합계, 평균 등을 모두 구하려면 스트림을 여러 번 사용해야 할 것 같지만, `summarizingXXX`를 사용하면 된다. 

합계, 평균, 개수, 최댓값, 최솟값이 담겨진 객체를 반환 받으며, 메서드로 원하는 값만 가져올 수도 있다. (`getCount()`, `getSum()`, `getMin()` 등)

### joining()

```java
public static Collector<CharSequence, ?, String> joining() {
    return new CollectorImpl(StringBuilder::new, StringBuilder::append, (r1, r2) -> {
        r1.append(r2);
        return r1;
    }, StringBuilder::toString, CH_NOID);
}

public static Collector<CharSequence, ?, String> joining(CharSequence delimiter) {
    return joining(delimiter, "", "");
}

public static Collector<CharSequence, ?, String> joining(CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
    return new CollectorImpl(() -> {
        return new StringJoiner(delimiter, prefix, suffix);
    }, StringJoiner::add, StringJoiner::merge, StringJoiner::toString, CH_NOID);
}
```

`joining` 메서드는 문자열을 연결할 때 사용할 수 있는 메서드이다.

구현체 코드를 보면 인자 값을 입력하지 않았을 때 내부적으로 `StringBuilder`를 사용하여 문자를 조합하며, delimiter 값을 넣었을 때 `StringJoiner`를 사용하여 문자를 조합하고 있다.

```java
public static void main(String[] args) {
    List<Dish> menu = new ArrayList<>();
    menu.add(new Dish("test", 80));
    menu.add(new Dish("test2", 90));
    menu.add(new Dish("test3", 100));
    System.out.println("result = " + menu.stream().map(Dish::getName).collect(joining()));
    System.out.println("result = " + menu.stream().map(Dish::getName).collect(joining(", ")));
}

static public class Dish {

    private String name;
    private int cal;

    public Dish(String name, int cal) {
        this.name = name;
        this.cal = cal;
    }

    public String getName() {
        return name;
    }

    public int getCalories() {
        return cal;
    }
}

// result = testtest2test3
// result = test, test2, test3
```

위 예제 코드에서 `joining` 메서드에 인자 값을 넣지 않았을 때, 넣었을 때의 결과를 확인할 수 있다.

### reducing()

```java
public static <T> Collector<T, ?, Optional<T>> reducing(BinaryOperator<T> op) {}

public static <T, U> Collector<T, ?, U> reducing(U identity, Function<? super T, ? extends U> mapper, BinaryOperator<U> op) {}
```

지금까지 사용한 메서드들은 `요약 팩터리 메서드`, `특화된 컬렉터`라고 불리는데,

즉 자주 사용되는 기능들을 메서드로 구현되어 있어 편리하게 코드를 작성할 수 있었다.

`reducing()`은 범용 팩터리 메서드이며, 이전에 사용했던 `reduce`처럼 범용적으로 코드를 작성할 수 있다.

`minBy`, `maxBy`의 구현 코드를 보면 `reducing`을 사용하고 있는데 범용적으로 사용되고 있음을 알 수 있다.

```java
public static void main(String[] args) {

    List<Dish> menu = new ArrayList<>();

    menu.add(new Dish("test", 80));
    menu.add(new Dish("test2", 90));
    menu.add(new Dish("test34", 100));

    System.out.println("result = " + menu.stream().map(Dish::getName).collect(joining()));
    System.out.println("result = " + menu.stream().collect(reducing("", Dish::getName, (i, j) -> i + j)));

    System.out.println(menu.stream().collect(reducing(0, Dish::getCalories, (i, j) -> i + j)));
    System.out.println(menu.stream().collect(reducing(0, Dish::getCalories, Integer::sum)));

    Optional<Dish> longDish = menu.stream().collect(reducing((i, j) -> i.getName().length() > j.getName().length() ? i : j));
    System.out.println("longDishName = " + longDish.get().getName());

    Optional<Dish> mostCalorieDish = menu.stream().collect(reducing((d1, d2) -> d1.getCalories() > d2.getCalories() ? d1 : d2));
    System.out.println(mostCalorieDish.get().getCalories());
}

static public class Dish {

    private String name;
    private int cal;

    public Dish(String name, int cal) {
        this.name = name;
        this.cal = cal;
    }

    public String getName() {
        return name;
    }

    public int getCalories() {
        return cal;
    }
}

// result = testtest2test34
// result = testtest2test34
// 270
// 270
// longDishName = test34
// 100
```

첫 번째 인수는 초기값이며, 두 번째는 반환 함수, 세 번쨰는 `BinaryOperator` 함수형 인터페이스를 통해 기존 값과 추가되는 값을 인자로 받아서 결과적으로 문자가 합쳐진 형태로 반환한다. 비슷한 형식으로 칼로리 합계도 계산할 수 있다.

`joining()`을 사용했을 때보다 코드가 길어졌으며, `joining()`에 비해 코드가 직관적이지 않다고 볼 수 있다.

또한 `reducing`에 인수를 하나만 넣어서(람다식) 제일 긴 이름, 높은 칼로리를 찾을 수도 있다.

```java
System.out.println(menu.stream().collect(reducing(0, Dish::getCalories, Integer::sum)));
System.out.println(menu.stream().map(Dish::getCalories).reduce(Integer::sum).get());

// 270
// 270
```
두 코드는 동일한 결과가 나오며, 다양한 방식으로 같은 연산을 할 수 있음을 책에서 언급하며 컬렉션의 유연성을 설명하고 있다. 그렇기 때문에 상황에 맞는 최적의 방법을 선택하는 것이 중요하다.

*collect와 reduce의 차이점*

ㅠㅠ...

### groupingBy

```java
public static <T, K> Collector<T, ?, Map<K, List<T>>> groupingBy(Function<? super T, ? extends K> classifier) {}
public static <T, K, A, D> Collector<T, ?, Map<K, D>> groupingBy(Function<? super T, ? extends K> classifier, Collector<? super T, A, D> downstream) {}
public static <T, K, D, A, M extends Map<K, D>> Collector<T, ?, M> groupingBy(Function<? super T, ? extends K> classifier, Supplier<M> mapFactory, Collector<? super T, A, D> downstream) {}
```

```java
public static void main(String[] args) {

    List<Dish> menu = new ArrayList<>();

    menu.add(new Dish("test", 80));
    menu.add(new Dish("test", 110));
    menu.add(new Dish("test", 120));
    menu.add(new Dish("test2", 90));
    menu.add(new Dish("test34", 100));

    Map<String, List<Dish>> DishMap = menu.stream().collect(groupingBy(Dish::getName));

    DishMap.entrySet().stream().forEach(i -> {
        System.out.println(i.getKey() + " " + i.getValue());
        i.getValue().stream().forEach(j -> System.out.println(j.getName() + " " + j.getCalories()));
    });
}

static public class Dish {

    private String name;
    private int cal;

    public Dish(String name, int cal) {
        this.name = name;
        this.cal = cal;
    }

    public String getName() {
        return name;
    }

    public int getCalories() {
        return cal;
    }
}

// test2 [Main$Dish@39ba5a14]
// test2 90
// test34 [Main$Dish@7a0ac6e3]
// test34 100
// test [Main$Dish@71be98f5, Main$Dish@6fadae5d, Main$Dish@17f6480]
// test 80
// test 110
// test 120
```

`groupingBy`는 데이터 집합을 하나 이상의 특성으로 그룹화하는 연산에서 사용된다. 

결과를 보면 이름을 기준으로 분류가 정상적으로 되었음을 확인할 수 있다. 이는 6장 도입부에서 설명했던 예시 코드인 `groupingBy`를 사용하여 한 줄로 그룹화를 할 수 있었다.

```java
// 이전에 선언했던 List<Dish> menu 변수 사용
Map<String, List<Dish>> DishMap = menu.stream().collect(groupingBy(i -> {
    if (i.getCalories() >= 100) return "big";
    else return "small";
}))

DishMap.entrySet().stream().forEach(i -> {
    System.out.println(i.getKey() + " " + i.getValue());
    i.getValue().stream().forEach(j -> System.out.println(j.getName() + " " + j.getCalories()));
});

// small [Main$Dish@3d24753a, Main$Dish@3f3afe78]
// test 80
// test2 90
// big [Main$Dish@59a6e353, Main$Dish@7a0ac6e3, Main$Dish@7e0ea639]
// test 110
// test 120
// test34 100
```

`groupingBy`에서 조건문을 만들어서 분류할 수도 있다. 위 코드는 100을 기준으로 작으면 `small`, 크면 `big`이라는 key 값으로 분류했고 value는 그에 해당하는 `List<Dish>` 타입의 데이터가 저장된다.

그룹화된 요소를 조작할 수 있는 방법도 존재한다. `filtering`과 `mapping`을 사용하여 조작할 수 있다.

```java
// 이전에 선언했던 List<Dish> menu 변수 사용
Map<String, List<Dish>> DishMap = menu.stream().collect(groupingBy(i -> {
    if (i.getCalories() >= 100) return "big";
    else return "small";
}, filtering(i -> i.getName() == "test", toList())))

DishMap.entrySet().stream().forEach(i -> {
    System.out.println(i.getKey() + " " + i.getValue());
    i.getValue().stream().forEach(j -> System.out.println(j.getName() + " " + j.getCalories()));
});

// small [Main$Dish@39ba5a14]
// test 80
// big [Main$Dish@7a0ac6e3, Main$Dish@71be98f5]
// test 110
// test 120
```

위 예시는 이전 예제에서 봤듯이 `big`, `small`로 분류 후에 `filtering`을 사용하여 이름이 `test`이어야 한다는 조건을 추가로 적용하였다. 결과를 보면 `small`과 `big`에 저장된 데이터가 3개이며, 이름은 모두 `test`임을 확인할 수 있다.

```java
// 이전에 선언했던 List<Dish> menu 변수 사용
Map<String, List<String>> DishMap = menu.stream().collect(groupingBy(i -> {
    if (i.getCalories() >= 100) return "big";
    else return "small";
}, mapping(Dish::getName, toList())))

DishMap.entrySet().stream().forEach(i -> {
    System.out.println(i.getKey() + " " + i.getValue());
    i.getValue().stream().forEach(j -> System.out.println(j));
});

// small [test, test2]
// test
// test2
// big [test, test, test34]
// test
// test
// test34
```

`mapping`을 사용하면 `map`처럼 매핑을 할 수 있다. 특히 `Map`이 감싸고 있는 상황에서 요소를 조작할 수 있다는 것이 큰 장점이라고 생각한다.

~~`flatMapping`~~ : 아직 제대로 이해를 못해서 정리를 못했습니다...

```java
// 이전에 선언했던 List<Dish> menu 변수 사용
Map<String, Long> DishMap = menu.stream().collect(groupingBy(Dish::getName, counting()));

DishMap.entrySet().stream().forEach(i -> {
    System.out.println(i.getKey() + " " + i.getValue());
});

// test2 1
// test34 1
// test 3
```

`List<Dish>` 타입에서 `groupingBy`를 사용하여 이름을 카운팅할 수 있는 `Map<String, Long>` 타입으로 변경할 수도 있다.

### collectingAndThen

```java
// 이전에 선언했던 List<Dish> menu 변수 사용
Map<String, Dish> DishMap = menu.stream()
        .collect(groupingBy(Dish::getName,
                collectingAndThen(
                        maxBy(comparingInt(Dish::getCalories)),
                Optional::get)))

DishMap.entrySet().stream().forEach(i -> {
    System.out.println(i.getKey() + " " + i.getValue().getCalories());
});

// test2 90
// test34 100
// test 120
```

컬렉팅 결과를 다른 형식에 적용하는 방법으로 `collectingAndThen`이 있다. 위 코드는 비효율적이긴 합니다..

`groupingBy`에서 `Dish::getName`을 사용하여 이름을 기준으로 그룹핑을 한 후에 `collectingAndThen`을 사용하여 칼로리가 최대인 하나를 선택하는 방식으로 변경되었다 (`List<Dish>` -> `Dish`)

동작 방식을 보면 이전에 설명했던 `filtering`, `mapping`과 같이 이전 결과를 바탕으로 새로운 결과를 만들어낸다.

마지막으로 `groupingBy`를 사용하는 예제가 책에 정말 많은데, 그만큼 유연하다고 생각하면 될 것 같다. (구현체를 확인하면 엄청 복잡하게 되어있다..)

### partitioningBy

```java
public static <T> Collector<T, ?, Map<Boolean, List<T>>> partitioningBy(Predicate<? super T> predicate) {}
public static <T, D, A> Collector<T, ?, Map<Boolean, D>> partitioningBy(Predicate<? super T> predicate, Collector<? super T, A, D> downstream) {}
```

```java
// 이전에 선언했던 List<Dish> menu 변수 사용
Map<Boolean, List<Dish>> DishMap = menu.stream().collect(partitioningBy(i -> i.getName() == "test"));

DishMap.entrySet().stream().forEach(i -> {
    System.out.println(i.getKey() + " " + i.getValue());
    i.getValue().stream().forEach(j -> System.out.println(j.getCalories()));
});

// false [Main$Dish@340f438e, Main$Dish@30c7da1e]
// 90
// 100
// true [Main$Dish@57829d67, Main$Dish@19dfb72a, Main$Dish@17c68925]
// 80
// 110
// 120
```

`List<Dish>`에서 `Map`으로 재정의가 된다는 점에서 `groupingBy`의 기능와 뭔가 비슷해보인다. 하지만 자세히 보면 차이점을 확인할 수 있다.

```java
Map<String, List<Dish>> DishMap1 = menu.stream().collect(groupingBy(Dish::getName));
Map<Boolean, List<Dish>> DishMap2 = menu.stream().collect(partitioningBy(i -> i.getName() == "test"));
```

`DishMap1`은 `Dish::getName`을 사용하여 이름을 기준으로 *그룹핑*을 한 것이지만, `DishMap2`는 이름이 `test`인지 아닌지를 기준으로 *파티셔닝*했다고 볼 수 있다.

## Collector 인터페이스