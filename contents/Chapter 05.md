## 스트림 슬라이싱

### takeWhile

```java
default java.util.stream.Stream<T> takeWhile(java.util.function.Predicate<? super T> predicate) { /* compiled code */ }
```

```java
Stream.of(1,2,3,4,5,6,7,8,9)
        .filter(n -> n%2 == 0)
        forEach(n -> System.out.print(n + ","));
System.out.println();
Stream.of(2,4,6,8,5,10,12,14)
        .takeWhile(n -> n%2 == 0)
        forEach(n -> System.out.print(n + ","));
// 2,4,6,8,
// 2,4,6,8,
```

`filter`를 사용하면 전체 스트림을 순회하면서 각각의 요소에 `Predicate`를 적용하여 슬라이싱을 진행하지만,

`takeWhile`은 모든 스트림에 `Predicate`를 적용하여 슬라이싱한다.

위 예제 코드 결과를 보면 알 수 있듯이 `takeWhile`은 *정렬*되어 있다는 가정이 존재한다.

첫 번째 결과와 두 번째 결과가 같은데, 그 이유는 `2,4,6,8`의 다음 값인 5에서 `false`가 나왔기 때문에 멈추게 된다.

### dropWhile

```java
default java.util.stream.Stream<T> dropWhile(java.util.function.Predicate<? super T> predicate) { /* compiled code */ }
```

```java
Stream.of(1,2,3,4,5,6,7,8,9)
        .filter(n -> n%2 == 0)
        .forEach(n -> System.out.print(n + ","));
System.out.println();
Stream.of(2,4,6,8,5,10,12,14)
        .dropWhile(n -> n%2 == 0)
        .forEach(n -> System.out.print(n + ","));
System.out.println();
Stream.of(5,6,8,10,2,3,5,12,11)
        .dropWhile(n -> n%2 == 0)
        .forEach(n -> System.out.print(n + ","));

// 2,4,6,8,
// 5,10,12,14,
// 5,6,8,10,2,3,5,12,11,
```

`dropWhile`은 `Predicate`에 의해 `false`가 나왔을 때 중단 후 남은 요소를 반환한다.

즉 `takeWhile`과 동일하게 정렬이 되어있다는 가정이 있어야 한다. 3번째 출력문을 보면 첫 번째 값인 5에서 거짓이 나왔기 때문에 모든 값이 출력되었다.

### limit

```java
Stream<T> limit(long var1);
```

```java
Stream.of(1,2,3,4,5,6,7,8,9)
        .filter(n -> n%2 == 0)
        .limit(3)
        .forEach(n -> System.out.print(n + ","));
// 2,4,6,
```

`limit`을 사용하면 필터링된 데이터 중에서 반환 받을 요소 개수를 지정할 수 있다.

`limit`을 사용하지 않았을 때 `2,4,6,8,`이 나왔지만, `limit(3)`을 사용하여 앞에서부터 3개(`2,4,6,`)가 출력된 것을 볼 수 있다.

### skip

```java
Stream<T> skip(long var1);
```

```java
Stream.of(1,2,3,4,5,6,7,8,9)
        .filter(n -> n%2 == 0)
        .skip(2)
        .forEach(n -> System.out.print(n + ","));
// 6,8,
```

`skip`은 필터링된 데이터 중에서 몇 개를 스킵할 것인지 설정할 수 있다.

`skip`을 사용하지 않았을 때 `2,4,6,8,`이 나왔지만 `skip(2)`을 사용한 결과, `2,4`가 스킵된 결과가 출력된 것을 볼 수 있다.

## 매핑

### map

```java
<R> Stream<R> map(Function<? super T, ? extends R> var1);
```

쉽게 말해 스트림 전체를 순회하면서 객체에서 구현된 메서드를 `map`을 사용해 매핑할 수 있다. (기존의 값을 고친다는 개념보다 새로운 버전을 만든다(메서드를 호출해서 새로운 결과물을 만드는 것이기 때문이다.)는 개념에 가까우므로 *매핑*이라는 단어를 사용한다고 한다.)

```java
List<Integer> result = Stream.of("test", "test2", "test3")
        .map(String::length)
        .toList();
System.out.println(result);
// [4, 5, 5]
```

### flatMap

```java
<R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> var1);
```

스트림 평탄화에서 `flatMap`이 사용된다고 한다. 처음에는 무슨 말인지 이해가 잘 안되지만 예제를 보면 쉽게 설명할 수 있다.

```java
List<Stream<String>> result = Stream.of("test", "test2", "test3")
        .map(w -> w.split(""))
        .map(Arrays::stream)
        //.flatMap(Arrays::stream)
        .distinct().toList()
result.forEach(i -> i.forEach(System.out::print));
// testtest2test3
```

위 코드는 `["test", "test2", "test3"]` 리스트에서 `split("")` 메서드를 사용하여 문자 각각의 리스트로 만들었으며, 이를 스트림으로 감싼 후 중복을 제거하려고 한다. 

하지만 감싸는 과정에서 `평탄화`가 이루어지지 않았기 때문에 `List<Stream<String>>`이 반환된다.

그리고 `Stream`이 감싸진 형태이기 때문에 `distinct()`도 적용되지 않은 채 저장된다.

결과적으로 `tes23`이 나와야하지만 `testtest2test3`이 출력된 것을 확인할 수 있다.

```java
List<String> result = Stream.of("test", "test2", "test3")
        .map(w -> w.split(""))
        //.map(Arrays::stream)
        .flatMap(Arrays::stream)
        .distinct().toList()
result.forEach(System.out::print);
// tes23
```

하지만 `flatMap`을 사용하면 이를 해결할 수 있다. 결과 또한 원하는 값인 `tes23`이 나왔으며, 반환 타입은 `List<String>`이다.

```java
String[][] arr = {{"test", "test2"}, {"test3", "test4"}, {"test5","test6"}};
List<Integer> result = Arrays.stream(arr)
        .flatMap(i -> Arrays.stream(i))
        .map(String::length)
        .toList();

System.out.println(result);
// [4, 5, 5, 5, 5, 5]
```

위 예제를 보면 직관적으로 와닿는다. 2차원 배열을 1차원 배열의 stream으로 만들어서 각각의 문자열 길이를 출력하고 있다.

즉 평탄화는 감싸고 있는 것을 지워준다고 생각하면 되겠다. (ex: 2차원에서 1차원 배열, `List<Stream<String>>`에서 `List<String>`) 

## 검색과 매칭

### anyMatch

```java
boolean anyMatch(Predicate<? super T> var1);
```

```java
List<String> result = Arrays.asList("test", "test2", "test33");

if (result.stream().anyMatch(i -> i.length() > 4)) {
    System.out.println("적어도 하나의 값의 길이가 4보다 큽니다.");
}
// 적어도 하나의 값의 길이가 4보다 큽니다.
```

`["test", "test2", "test33"]` 리스트에서 `anyMatch`를 사용하여 길이가 4보다 큰지 비교하고 있다.

`anyMatch`는 적어도 하나의 요소와 일치하는지 확인하기 때문에 `test`와 `test2`에서 `false`가 나오더라도, `test33`이 4보다 크기 때문에 `true`를 반환하며 결과적으로 `true`를 반환한다.

### allMatch

```java
boolean allMatch(Predicate<? super T> var1);
```

```java
List<String> result = Arrays.asList("test11", "test22", "test33");

if (result.stream().allMatch(i -> i.length() > 4)) {
    System.out.println("모든 값의 길이가 4보다 큽니다.");
}
// 모든 값의 길이가 4보다 큽니다.
```

`allMatch`는 모든 요소의 조건에서 `true`가 나와야하며, 하나라도 `false`가 나오면 결과적으로 `false`를 반환한다.

### noneMatch

```java
boolean noneMatch(Predicate<? super T> var1);
```

```java
List<String> result = Arrays.asList("test11", "test22", "test33");

if (result.stream().noneMatch(i -> i.length() <= 4)) {
    System.out.println("모든 값의 길이가 4보다 큽니다.");
}
// 모든 값의 길이가 4보다 큽니다.
```

`noneMatch`는 `allMatch`의 반대 연산을 수행한다. 모든 요소의 조건에서 `false`가 나와야하며, 하나라도 `true`가 나오면 결과적으로 `false`를 반환한다.

위 결과를 보면 `["test11","test2","test33"]` 모두 4보다 크기 때문에 `true`를 반환한다.

*쇼트서킷* : 지금까지 사용했던 `allMatch`, `noneMatch`, `limit` 등 모든 요소를 처리하지 않고도 중간에 결과를 반환할 수 있기 때문에 쇼트서킷 연산이라고 부른다.

## 리듀싱

### reduce

스트림 요소를 값으로 도출하는 연산을 *리듀싱 연산*이라고 부른다. 리듀싱 또한 코드를 보면 쉽게 이해할 수 있다.

```java
int sum = 0;
for (int x : numbers) {
    sum += x;
}
```

위 코드는 `numbers`를 순회하면서 `sum` 변수에 값을 추가한다.

```java
// Integer.class
public static int sum(int a, int b) {
    return a + b;
}
```

```java
List<Integer> numbers = Arrays.asList(1, 2, 3, 4);
Optional<Integer> sum0 = numbers.stream().reduce((a, b) -> a + b);
int sum1 = numbers.stream().reduce(0, (a, b) -> a + b);
int sum2 = numbers.stream().reduce(0, Integer::sum);
```

하지만 `reduce`를 사용하면 한 줄로 해결할 수 있다. `sum1`과 `sum2`는 같은 결과가 나오며, 특히 `sum2` 변수를 보면 메서드 참조를 사용하여 코드를 더욱 간결하게 만들 수 있었다.

위 코드에서 `reduce`의 첫 번째 값인 0은 초깃값이며, 두 번째 값은 함수형 인터페이스인 `BinaryOperator`을 사용하여 a에 0, b에 1을 넣어서 더한 값인 1이라는 새로운 누적값이 만들어진다. (<a href="https://johngrib.github.io/wiki/java-functional-interface/#binaryoperator">BinaryOperator 설명</a>)

즉 `sum` 변수에 값이 누적되는 것과 똑같은 메커니즘이다.

```java
Optional<T> reduce(BinaryOperator<T> accumulator);

T reduce(T identity, BinaryOperator<T> accumulator);

<U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner);
```

`reduce`의 인터페이스를 보면 오버로딩이 되어 있다.

*파라미터가 1개일 때*: 초기 값이 없는 상태이며, 리턴 타입이 `Optional<T>`인 것이 핵심이다. 즉 스트림에 요소가 무조건 있다고 보장받지 못하기 때문에 `Optional`을 감싼 것이다. (요소가 없다면 합계를 반환할 수 없다.)

*파라미터가 2개일 때*: 지금까지 설명했던 예제의 경우이다.

*파라미터가 3개일 때*: `combiner`가 추가되어 병렬 처리를 할 때 다른 쓰레드의 결과를 합쳐준다고 합니다.. ~~더 정리해오겠습니다 .. ㅠㅠ~~

### findAny, findFirst

```java
Optional<T> findFirst();
Optional<T> findAny();
```

필터링된 요소들 중에서 하나를 선택하는 방법으로 `findAny`, `findFirst`가 존재한다.

`findFirst`는 순서를 고려하기 때문에 첫 번째 요소를 반환한다.

`findAny`는 가장 먼저 찾은 요소를 반환하기 때문에, 순서를 보장받지 못할 수 있는 병렬 처리에서 하나를 반환하고 싶을 때 사용된다.

책에서 언급되었던 `findFirst`, `findAny`의 반환 타입도 `Optional`이 감싸진 형태인데, 스트림에 요소가 무조건 있다고 보장받을 수 없기 때문이다.

## 숫자형 스트림

```java
int calories = menu.stream() // Stream<Dish>
            .map(Dish::getCalories) // Stream<Integer>
            .reduce(0, Integer::sum);
```

위와 같은 코드를 사용했을 때 `map`을 사용할 때 스트림으로 감싸는 *박싱 비용*이 존재한다. 이를 피하기 위해 기본형 특화 스트림을 제공한다. 또한 자주 사용하는 숫자 관련 연산 수행 메서드를 제공하고 있다. (`max()`, `min()`, `sum()` 등)

### 기본형 특화 스트림

각 타입에 따른 기본형 특화 스트림들을 제공하고 있으며, (`IntStream`, `DoubleStream`, `LongStream`)

매핑 메서드를 사용하면 위의 스트림 타입으로 변환된다. (`mapToInt`, `mapToDouble`, `mapToLong`)

```java
// IntStream.class
OptionalInt reduce(IntBinaryOperator var1);
int sum();

// IntPipeline.class - IntStream 구현체
public final int sum() {
    return this.reduce(0, Integer::sum);
}
```

```java
int calories = menu.stream() // Stream<Dish>
            .mapToInt(Dish::getCalories) // IntStream
            .sum();
```

이전 예시와 위 예시를 보면 `Stream<Integer>`와 `IntStream`의 차이가 있음을 확인할 수 있다. 또한 `IntStream`에 구현되어 있는 `sum()` 메서드를 호출하여 합계를 얻어낼 수 있었다.

```java
IntStream intStream = menu.stream().mapToInt(Dish::getCalories);
Stream<Integer> stream = intStream.boxed();
```

위와 같이 기본형 특화 스트림을 박싱할 수도 있다. (Stream의 기능들을 사용할 수 있다.)

## 스트림 만들기

### Stream.of

```java
static <T> Stream<T> of(T... values) {
    return Arrays.stream(values);
}
```

```java
List<Integer> result = Stream.of("test", "test2", "test3")
        .map(String::length)
        .toList();
System.out.println(result);
// [4, 5, 5]
```

매핑(`map`)에 대해 설명할 때 사용했던 `Stream.of`가 값으로 스트림을 만들 때 사용한다. `Stream.empty()`로 빈 스트림을 만들 수도 있다.

### Stream.ofNullable

```java
static <T> Stream<T> ofNullable(T t) {
    return t == null ? empty() : StreamSupport.stream(new Streams.StreamBuilderImpl(t), false);
}
```

`ofNullable`의 구현 코드를 보면 값이 null인지 체크하여 empty()를 반환하거나, 그에 맞는 stream을 반환하고 있다.

스트림에 요소가 무조건 있다고 보장받을 수 없을 때 사용하면 된다.

### Arrays.stream

```java
String[][] arr = {{"test", "test2"}, {"test3", "test4"}, {"test5","test6"}};
List<Integer> result = Arrays.stream(arr)
        .flatMap(i -> Arrays.stream(i))
        .map(String::length)
        .toList();

System.out.println(result);
// [4, 5, 5, 5, 5, 5]
```

`flatMap` 설명할 때 사용했던 `Arrays.stream`이 배열로 스트림을 만들 때 사용한다.

## 무한 스트림 만들기

### Stream.iterate

```java
static <T> Stream<T> iterate(final T seed, final UnaryOperator<T> f) {}

static <T> Stream<T> iterate(final T seed, final Predicate<? super T> hasNext, final UnaryOperator<T> next) {}
```

```java
Stream.iterate(0, n -> n + 2)
        .limit(10)
        .forEach(System.out::println);
// 0
// 2
// 4
// ...
// 14
// 16
// 18
```

`iterate`를 사용하면 무한 스트림을 만들 수 있으며, `UnaryOperator`(<a href="https://johngrib.github.io/wiki/java-functional-interface/#unaryoperator">UnaryOperator 설명</a>)를 사용하여 값 하나를 받아서 계산된 같은 타입의 값을 리턴하고 있다. (이전에 언급했던 `BinaryOperator`는 값 2개를 받는다.)

또한 자바 9부터는 `Predicate`를 지원하여 아래 코드와 같이 언제까지 작업을 수행할 것인지 범위를 지정할 수 있다.

```java
IntStream.iterate(0, n -> n < 100, n -> n + 4)
                .forEach(System.out::println);
// 0
// 4
// 8
// ...
// 88
// 92
// 96
```

### Stream.generate

```java
static <T> Stream<T> generate(Supplier<? extends T> s)
```

```java
Stream.generate(Math::random)
        .filter(i -> i > 0.6)
        .limit(5)
        .forEach(System.out::println);

// 0.8325703952291411
// 0.6957692449861237
// 0.6538480384933207
// 0.7582579396084951
// 0.639961709703448
```

위 코드는 무한 스트림 상태에서 랜덤 난수를 계속 생성하며, 0.6보다 큰 경우의 값을 5개 뽑아낸다.

`generate`는 난수 생성과 같이 연속적으로 계산하지 않을 때 사용한다. (난수는 `iterate`에서 사용된 예시 코드처럼 값을 연속적으로 계산하지 않는다. - 상태가 없는 메서드, 나중에 계산으로 사용할 어떤 값도 저장해두지 않는다.)

* `Supplier`는 매개변수를 받지 않고 반환하는 함수형 인터페이스이다.
