# [Chapter 02] 동작 파라미터화 코드 전달하기

어떻게 시시각각 변하는 사용자 요구사항에 대응해야 할까?

답은 **동작 파라미터화**에 있다. 이번 챕터에서는 동작 파라미터화에 대해 자세히 다룬다.

# 2.1 변화하는 요구사항에 대응하기

농작 재고목 애플리케이션에 리스트에서 녹색 사과만 필터링하는 기능을 추가한다고 가정하자.

## 2.1.1 첫번째 시도 : 녹색 사과 필터링

```java
public static List<Apple> filterGreenApples(List<Apple> inventory){
    List<Apple> result = new ArrayList<>();

    for(Apple apple: inventory){
        if (GREEN.equals(apple.getColor())){
            result.add(apple);
        }
    }
    return result;
}
```

이 상황에서 농부가 변심하여 빨간 사과도 필터링하고 싶어졌다면 어떻게 해야할까? 물론 복사 붙여넣기 해서, RED로 조건을 바꿔주면 되겠지만, 나중에 더 다양한 색으로 필터링하는 변화에도 과연 그렇게 대응해야 할까?

<aside>
💡 거의 비슷한 코드가 반복 존재한다면, 그 코드를 추상화한다.

</aside>

## 2.1.2 두번째 시도 : 색을 파라미터화

```java
public static List<Apple> filterApplesByColor(List<Apple> inventory, Color color){
  List<Apple> result = new ArrayList<>();

  for(Apple apple: inventory){
      if (color.equals(apple.getColor())){
          result.add(apple);
      }
  }
  return result;
}
```

여기서 농부가 무게의 기준도 바뀔 수 있도록 추가해달라고 했다면, 코드를 다음 처럼 작성할 수 있다.

```java
public static List<Apple> filterApplesByWeight(List<Apple> inventory, int weight){
  List<Apple> result = new ArrayList<>();

  for(Apple apple: inventory){
      if (apple.getWeight() > weight){
          result.add(apple);
      }
  }
  return result;
}
```

대부분의 코드와 중복되는 것 같지 않은가? 이는 DRY(같은 것을 반복하지 말 것)의 원칙을 어기는 것이다.

# 2.2 동작 파라미터화

먼저 선택 조건을 결정하는 인터페이스를 정의하자. 프레디케이트는 1장에서 간략히 설명했다.

```java
public interface ApplePredicate{
    boolean test (Apple apple);
}
```

다양한 선택 조건을 대표하는 ApplePredicate 클래스 정의

```java
public class AppleHeavyWeightPredicate implements ApplePredicate{
    @Override
    public boolean test(Apple apple){
        return apple.getWeight() > 150;
    }
}

public class AppleGreenColorPredicate implements ApplePredicate{
    @Override
    public boolean test(Apple apple) {
        return Color.GREEN.equals(apple.getColor());
    }
}
```

이제는 filterApples 메서드가 ApplePredicate 객체를 인수로 받도록 고치면 된다.

## 2.2.1 네번째 시도 : 추상적 조건으로 필터링

```java
public static List<Apple> filterApples(List<Apple> inventory, ApplePredicate p){
  List<Apple> result = new ArrayList<>();
  for (Apple apple : inventory){
      if (p.test(apple)){
          result.add(apple);
      }
  }
  return result;
}
```

이제 필요한대로 다양한 ApplePredicate를 만들어서 filterApples 메서드로 전달할 수 있다.

## 퀴즈 2-1 유연한 prettyPrintApple 메서드 구현하기

```java
public interface ApplePrint{
    String print(Apple apple);
}
public class AppleWeightPrint implements ApplePrint{
    @Override
    public String print(Apple apple) {
        return "Apple Weight : " + String.valueOf(apple.getWeight());
    }
}
public class AppleHeavyOrLightPrint implements ApplePrint{
    @Override
    public String print(Apple apple) {
        return apple.getWeight() > 150 ? "Heavy Apple" : "Light Apple";
    }
}
public static void prettyPrintApple(List<Apple> inventory, ApplePrint applePrint){
    for (Apple apple : inventory){
        String output = applePrint.print(apple);
        System.out.println(output);
    }
}
```

# 2.3 복잡한 과정 간소화

인터페이스를 구현하는 여러 클래스를 정의한 다음에 인스턴스화 하는 번거러운 작업을 줄일 수는 없을까?

## 2.3.1 익명 클래스

자바의 지역 클래스와 비슷한 개념이다. 클래스 선언과 인스턴스화를 동시에 할 수 있고, 즉석에서 필요한 구현을 만들어서 사용할 수 있다.

## 2.3.2 다섯번째 시도 : 익명 클래스 사용

```java
List<Apple> redApples = filterApples(inventory, new ApplePredicate() {
    @Override
    public boolean test(Apple apple) {
        return Color.RED.equals(apple.getColor());
    }
});
```

하지만 여전히, 익명 클래스는 많은 공간을 차지한다. 그리고 많은 프로그래머가 익명 클래스의 사용에 익숙하지 않다.

## 퀴즈 2-2 익명 클래스 문제

대충 익명 클래스 사용하기 어렵다는 뜻

```java
intelliJ
```

## 2.3.3 여섯 번째 시도 : 람다 표현식 사용

```java
List<Apple> result = 
		filterApples(inventory, (Apple apple) -> Color.RED.equals(apple.getColor()));
```

> 훨씬 간단해진 코드 ! ! ! 심지어 문제를 더 잘 설명하고 있다.
> 

## 2.3.4 일곱 번째 시도 : 리스트 형식으로 추상화

```java
public interface Predicate<T>{
    boolean test(T t);
}

public static <T> List<T> filter(List<T> list, Predicate<T> p){
    List<T> result = new ArrayList<>();
    for (T l : list){
        if (p.test(l)){
            result.add(l);
        }
    }
    return result;
}
```

이제는 사과뿐 아니라 바나나, 정수, 문자열 전부 필터할 수 있다.

# 2.4 실전 예제

다음은 코드 전달 개념을 익힐 수 있는 예제들이다.

- 2.4.1 Comparator로 정렬하기
- 2.4.2 Runnable로 코드 블록 실행하기
- 2.4.3 Callable을 결과로 반환하기
- 2.4.4 GUI 이벤트 처리하기
