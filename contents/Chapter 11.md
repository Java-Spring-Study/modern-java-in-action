# Chapter11. null 대신 Optional 클래스

- 자바를 포함한 대부분의 언어 설계에는 null 참조 개념을 포함한다.

## **1. 값이 없는 상황을 어떻게 처리할까?**

### **1. 보수적인 자세로 NullPointerException 줄이기**

```java
public String getCarInsuranceName (Person person) {
    if (person != null) {
        Car car = person.getCar();
        if (car != null) {
            Insurance insurance = car.getInsurance();
            if (insurance != null) {
                return insurance.getName();
            }
        }
    }
    return "Unknown";
}
```

- 변수를 참조할 때마다 null을 확인하면 들여쓰기 수준이 증가한다.<br>
⇒ 이와 같은 반복 패턴 코드를 ‘깊은 의심’ 이라고 부른다. (…)
- 코드의 구조가 엉망이 되고, 가독성도 떨어진다.

```java
public String getCarInsuranceName (Person person) {
    if(Person == null) {
        return "Unknown";
    }
    Car car = person.getCar();
    if(car == null) {
        return "Unknown";
    }
    Insurance insurance = car.getInsurance();
    if(insurance == null) {
        return "Unknown";
    }
    return insurance.getName();
}
```

- 메서드에 네 개의 출구가 생기므로 유지보수가 어려워진다.

⇒ null로 값이 없다는 사실을 표현하는 것은 좋은 방법이 아니다!<br>
따라서 값이 있거나 없음을 표현할 좋은 방법이 필요하다.

<br>

### **2. null 때문에 발생하는 문제**

- 에러의 근원이다
- 코드를 어지럽힌다 : null 확인 코드를 추가해야 하므로 가독성이 떨어짐
- 아무 의미가 없다 : 정적 형식 언어에서 값이 없음을 표현하는 방법으로 적절하지 않다.
- 자바 철학에 위배된다 : 자바는 모든 포인터를 숨겼는데, null 포인터가 예외로 남아있다.
- 형식 시스템에 구멍을 만든다 :null은 무형식이며 정보를 포함하지 않으므로 모든 참조형식에 null 할당이 가능한데, 다른 부분으로 퍼졌을 때 어떤 의미로 사용되었는지 알 수 없다.

---

<br>

## **2. Optional 클래스 소개**

- 자바 8은 `java.util.Optional<T>` 라는 새로운 클래스를 제공한다.
- `Optional` 은 선택형 값을 캡슐화하는 클래스다.
- `Optional` 클래스는 값이 있으면 값을 감싸고, 값이 없으면 `Optional.empty` 메서드로 `Optional`을 반환한다.
- `Optional.empty`: 싱글턴 인스턴스를 반환하는 정적 팩토리 메서드
- null 참조와 `Optional.empty` 차이
    - null을 참조하려 하면 `NullPointerException` 이 발생하지만, `Optional.empty()` 는 `Optional` 객체이므로 다양한 방식으로 활용 가능하다.
- `Optional` 의 역할은 더 이해하기 쉬운 API를 설계하도록 돕는 것, 언랩해서 값이 없을 수 있는 상황에 적절하게 대응하도록 강제하는 효과가 있다.

---

<br>

## **3. Optional 적용 패턴**

### **1. Optional 객체 만들기**

- **빈 `Optional`**
    
    정적 팩토리 메서드 `Optional.empty`로 빈 `Optional` 객체 생성
    
    ```java
    Optional<Car> optCar = Optional.empty();
    ```    

- **null이 아닌 값으로 Optional 만들기**
    
    정적 팩토리 메서드 `Optional.of`로 null이 아닌 값을 포함하는 `Optional` 객체 생성
    
    ```java
    Optional<Car> optCar = Optional.of(car);
    ```
    
    car가 null이라면 `NullPointException`이 발생한다.
    
- **null 값으로 Optional 만들기**
    
    정적 팩토리 메서드 `Optional.ofNullable`로 null 값을 저장할 수 있는 `Optional` 객체 생성
    
    ```java
    Optional<Car> optCar = Optional.ofNullable(car);
    ```
    
    car가 null이면 빈 `Optional` 객체가 반환된다.
    
<br>

### **2. 맵으로 Optional의 값을 추출하고 변환하기**

- 객체의 정보를 추출할 때 `Optional` 사용하기

```java
// 이름 정보에 접근하기전에 insurance가 null인지 확인해야 한다.
String name = null;
if(insurance != null) {
    name = insurance.getName();
}

// map의 인수로 제공된 함수가 값을 바꾼다. (스트림 연산과 유사)
// Optional이 비어있으면 아무 일도 일어나지 않는다.
Optional<Insurance> optInsurance = Optional.ofNullable(insurance);
Optional<String> name = optInsurance.map(Insurance::getName);
```

<br>

### **3. flatMap으로 Optional 객체 연결**

```java
Optional<Person> optPerson = Optional.of(person);
Optional<String> name =
							// getCar는 Optional<Car> 객체를 반환함 => Optional<Optional<Car>>
    optPerson.map(Person::getCar)
							// getInsurance는 Optional<Insurance> 객체를 반환함
             .map(Car::getInsurance)
             .map(Insurance::getName);
```

- 위 코드는 컴파일되지 않는다. 중첩 `Optional` 객체 구조를 가진다.

```java
public String getCarInsuranceName(Optional<Person> person) {
    return person.flatMap(Person::getCar)
                 .flatMap(Car::getInsurance)
                 .map(Insurance::getName)
                 .orElse("Unknown");
}
```

- 스트림의 `flatMap`은 함수를 인수로 받아서 다른 스트림을 반환하는 메서드로,
인수로 받은 함수를 적용해서 생성된 각각의 스트림에서 콘텐츠만 남긴다.
    
    ⇒ 함수를 적용해서 생성된 모든 스트림이 하나의 스트림으로 병합되어 평준화된다.
    
- `Optional` 의 `flatMap` 메서드로 이차원 `Optional` 을 일차원 `Optional` 로 평준화할 수 있다.

```java
public String getCarInsuranceName(Optional<Person> person) {
    return person.flatMap(Person::getCar)
                 .flatMap(Car::getInsurance)
                 .map(Insurance::getName)
                 // 결과 Optional이 비어있으면 기본값 Unknown
                 .orElse("Unknown");
}
```

- `Optional` 을 이용해서 코드를 복잡하게 만들지 않으면서 값이 없는 상황을 처리한다.

![Untitled](https://user-images.githubusercontent.com/78337522/214845519-128e912c-84dd-48e5-972d-51784fdeca00.png)

- 평준화 과정이란 두 `Optional` 을 합치는 기능을 수행하면서, 둘 중 하나라도 null이면 빈 `Optional` 을 생성하는 연산이다.
- `flatMap` 을 빈 `Optional` 에 호출하면 아무 일도 일어나지 않고 그대로 반환되지만,
`Person` 을 감싸고 있다면 `flatMap` 에 전달된 Function이 `Person` 에 적용된다.
- `Optional` 이 비어있을 때 기본값을 제공하는 `orElse` 메서드를 사용한다.

<br>

### **4. Optional 스트림 조작**

- 자바 9에서는 `Optional`을 포함하는 스트림을 쉽게 처리하도록 `stream()` 메서드를 추가했다.

```java
public Set<String> getCarInauranceNames(List<Person> persons) {
    return persons.stream()
                  .map(Person::getCar)
                  .map(optCar -> optCat.flatMap(Car::getInsurance))
                  .map(optIns -> optIns.map(Insurance::getName))
                  .flatMap(Optional::stream)
                  .collect(toSet());
}
```

- 각 `Optional` 이 비어있는지에 따라 0개 이상의 항목을 포함하는 스트림으로 변환한다.
- 스트림의 요소를 두 수준(스트림의 스트림)으로 변환하고, 다시 한 수준(평면 스트림)으로 바꾼다.
- 한 단계의 연산으로 값을 포함하는 `Optional`을 언랩하고, 비어있는 `Optional`은 건너뛴다.

<br>

### **5. 디폴트 액션과 Optional 언랩**

`orElse` 메서드 외에도 다양한 방법으로 `Optional` 값을 읽을 수 있다.

- **`get()`**<br>
: 값을 읽는 간단한 메서드지만 안전하지 않다. 래핑된 값이 있으면 반환하고, 값이 없으면 `NoSuchElementException`을 발생시킨다.
- **`orElse()`**<br>
: 값을 포함하지 않을 때 기본값을 제공한다.
- **`orElseGet(Supplier<? extends T> other)`**<br>
: 값이 없을 때만 Supplier가 실행된다. 비어있을 때만 기본값을 생성하게 된다.
  - `orElse()` 는 null이든 아니든 호출되고, `orElseGet()` 은 null일 때만 호출된다.
- **`orElseThrow(Supplier<? extends X> exceptionSupplier)`**<br>
: `Optional`이 비어있을 때 지정한 예외를 발생시킨다.
- **`ifPresent(Consumer<? super T> consumer)`**<br>
: 값이 존재할 때 인수로 넘겨준 동작을 실행할 수 있고, 값이 없으면 아무 일도 일어나지 않는다.
- **`ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction)`**<br>
: `Optional`이 비어있을 때 실행할 수 있는 `Runnable`을 인수로 받는다. (자바 9에서 추가)

<br>

### **6. 두 Optional 합치기**

- 두 `Optional`을 인수로 받아 `Optional<Insurance>`를 반환하는 `nullsafe` 메서드를 구현한다.
- 인수로 전달한 값 중 하나라도 비어있으면 빈 `Optional<Insurance>`를 반환한다.

```java
public Optional<Insurance> nullSafeFindCheapestInsurance(
            Optional<Person> person, Optional<Car> car) {
    // isPresent 메서드는 Optional이 값을 포함하는지 여부를 알려준다
    if(person.isPresent() && car.isPresent()){
        return Optional.of(findCheapestInsurance(person.get(), car.get()));
    } else {
        return Optional.empty();
    }
}
```

- `map` 과 `flatMap` 메서드로 구현해보기

```java
public Optional<Insurance> nullSafeFindCheapestInsurance(
            Optional<Person> person, Optional<Car> car) {
    return person.flatMap(p -> car.map(c -> findCheapestInsurance(p,c)));
}
```
<br>

### **7. 필터로 특정값 거르기**

- 객체의 메서드를 호출해서 어떤 프로퍼티를 확인해야 할 때 사용한다.

```java
// Optional 객체에 filter 메서드 사용
Optional<Insurance> optInsurance = ...;
optInsurance.filter(ins -> "CambridgeInsurance".equals(insurance.getName()))
            .ifPresent(x -> System.out.println("ok"););
```

- `Optional`은 최대 한 개의 요소를 포함할 수 있는 스트림과 같으므로, `Optional`이 비어있으면 `filter` 연산은 아무 동작도 하지 않고, 값이 있으면 프레디케이트를 적용한다.
- 프레디케이트 적용 결과가 true면 아무 변화도 일어나지 않고, false면 `Optional`은 빈 상태가 된다.

---

<br>

## **4. Optional을 사용한 실용 예제**

- 기존 API에 `Optional` 기능을 활용할 수 있도록, 코드에 작은 유틸리티 메서드를 추가한다.

<br>

### **1. 잠재적으로 null이 될 수 있는 대상을 Optional로 감싸기**

- `Map`의 `get` 메서드는 요청한 키에 대응하는 값을 찾지 못하면 null을 반환한다.
- map에서 반환하는 값을 Optional로 감싸고, if-then-else를 추가하거나 `ofNullable`을 이용한다.

```java
Optional<Object> value = Optional.ofNullable(map.get("key"));
```

<br>

### **2. 예외와 Optional 클래스**

- `paserInt` 와 같이 null을 반환하는 대신 예외를 발생시키는 경우가 있다.
- `parseInt` 를 감싸는 유틸리티 메서드를 구현해서 `Optional` 을 반환한다.

```java
public static Optional<Integer> stringToInt(String s) {
    try{
        return Optional.of(Integer.parseInt(s));
    } catch (NumberFormatException e) {
        // 예외 발생 시 빈 Optional을 반환한다
        return Optional.empty();
    }
}
```

⇒ 위와 같은 메서드를 포함하는 유틸리티 클래스 `OptionalUtility` 를 만들어보자!

<br>

### **3. 기본형 Optional을 사용하지 말아야 하는 이유**

- `Optional`도 스트림처럼 기본형 특화 클래스를 제공한다.
- 스트림이 많은 요소를 가질 때는 기본형 특화 스트림으로 성능을 향상시킬 수 있는데,
`Optional`의 최대 요소 수는 한 개이므로 기본형 특화 클래스로 성능을 개선할 수 없다.
- 기본형 특화 `Optional` 은 유용한 메서드 `map`, `flatMap`, `filter` 등을 지원하지 않는다.
- 기본형 특화 `Optional` 로 생성한 결과는 다른 일반 `Optional`과 혼용할 수 없다.

<br>

### **4. 응용**

- `Optional`로 프로퍼티에서 지속 시간 읽기

```java
Properties props = new Properties();
props.setProperty("a", "5");
props.setProperty("b", "true");
props.setProperty("c", "-3");

// 메서드 시그니처
// 프로퍼티를 읽어 값을 초 단위 지속 시간으로 해석한다.
public int readDuration(Properties props, String name)

// JUnit assertion
// 지속 시간은 양수여야 하므로 문자열이 양수일 때 해당 값을 반환하고, 그 외에는 0을 반환한다.
assertEqual(5, readDuration(param, "a"));
assertEqual(0, readDuration(param, "b"));
assertEqual(0, readDuration(param, "c"));
assertEqual(0, readDuration(param, "d"));

// Optional로 프로퍼티에서 지속 시간을 읽는다.
// 이전 예제의 OptionalUtility 메서드를 이용해 Optional<Integer>를 반환한다.
public int readDuration(Properties props, String name) {
    return Optioinal.ofNullable(props.getProperty(name))
                    .flatMap(OptionalUtility::stringToInt)
                    .filter(x -> x > 0)
                    .orElse(0)
}
```