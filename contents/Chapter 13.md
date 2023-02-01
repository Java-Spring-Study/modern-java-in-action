자바 8에서는 기본 구현을 포함하는 인터페이스를 정의하는 두 가지 방법을 제공한다.

- 인터페이스 내부에 정적 메서드(static method)를 사용
- 디폴트 메서드 사용

### 디폴트 메서드를 이용해서 좋은 점이 뭘까 ?

디폴트 메서드를 이용하면, 인터페이스의 기본 구현을 그대로 상속하므로 인터페이스에 자유롭게 새로운 메서드를 추가할 수 있게 된다.

# 13.1 변화하는 API

우리가 라이브러리 설계자가 되었다고 가정하고, API를 설계했다고 생각하자. 릴리즈된 인터페이스를 고치면 어떤 문제가 발생할까?

```java
public interface Resizable extends Drawable {
  int getWidth();
  int getHeight();
  void setWidth(int width);
  void setHeight(int height);
  void setAbsoluteSize(int width, int height);
}
```

이때 우리가 만든 라이브러리를 즐겨 사용하는 사용자가, Resizable 을 구현하는 Ellipse 클래스를 만들었다고 하자.

```java
public class Ellipse implements Resizable{
	...
}
```

## 13.1.2 API 버전 2

하지만 몇 개월 후, Resizable 에 메서드를 추가해서 인터페이스가 바뀌었다면, 컴파일 에러가 발생할 것이다.

```java
public interface Resizable extends Drawable {
  int getWidth();
  int getHeight();
  void setWidth(int width);
  void setHeight(int height);
  void setAbsoluteSize(int width, int height);
	void setRelativeSize(int wFactor, int hFactor); // 추가
}
```

이렇듯 공개된 API를 고치면 기존 버저과 호환성 문제가 발생할 것이다.

### 바이너리 호환성

뭔가 바꾼 이후에도 에러 없이 기존 바이너리가 실행될 수 있는 상황.

### 소스 호환성

코드를 고쳐도 기존 프로그램을 성공적으로 재컴파일할 수 있음

### 동작 호환성

코드를 바꿔도 같은 입력값이 주어지면, 같은 동작을 실행함.

# 13.2 디폴트 메서드란 무엇인가?

디폴트 메서드는 인터페이스 자체에서 구현하는 것이다.

### 추상 클래스(abstract)와 인터페이스(interface)

1. 클래스는 하나의 추상 클래스만 상속받을 수 있다. 하지만 인터페이스는 여러개 구현할 수 있다.
2. 추상 클래스는 필드로 공통 상태를 가질 수 있지만 인터페이스는 그렇지 않다.

# 13.3 디폴트 메서드 활용 패턴

디폴트 메서드를 이용하는 두 가지 방식이 있다.

## 13.3.1 선택형 메서드

```java
interface Iterator<T>{
  boolean hasNext();
  T next();
  default void remove() {
    throw new UnsupportedOperationException();
  }
}
```

이제는 빈 remove 메서드를 구현할 필요가 없어졌다.

## 13.3.2 동작 다중 상속

### 다중 상속 형식

```java
public class ArrayList<E> extends AbstractList<E> implements List<E>, RandomAccess, Cloneable, Serializable {
}
```

이제는 인터페이스가 구현을 포함할 수 있으므로, 클래스는 여러 인터페이스에서 동작을 상속받을 수 있다.

### 인터페이스 조합

이제는 디폴트 메서드를 활용할 수 있기 때문에, 코드를 복사 & 붙여넣기 할 필요가 없다.

더 나아가, 이제는 인터페이스를 직접 고칠 수 있으니, 모든 클래스도 자동으로 변경한 코드를 상속받는다.

# 13.4 해석 규칙

```java
interface A {
  default void hello() {
    System.out.println("Hello form A");
  }
}

interface B extends A {
  default void hello() {
    System.out.println("Hello from B");
  }
}

class C implements B, A {
  public static void main(String[] args) {
    new C().hello(); // 무엇이 출력될까?
  }
}
```

C++의 다이아몬드 문제

## 13.4.1 알아야 할 세가지 해결 규칙

다른 클래스나 인터페이스로부터 같은 시그니처를 갖는 메서드를 상속받을 때는 세 가지 규칙을 따라야 한다.

1. 클래스가 항상 이긴다.
2. 1번 이외의 상황에서는 서브인터페이스가 이긴다. 즉 B가 A를 상속받는다면 B가 A를 이긴다.
3. 여전히 우선순위가 결정되지 않았다면 명시적으로 디폴트 메서드를 오버라이드하고 호출해야 한다.

## 13.4.2 디폴트 메서드를 제공하는 서브인터페이스가 이긴다.

### 1. 하지만 다음 같은 경우는 어떨까?

![image](https://user-images.githubusercontent.com/92802207/215994482-b50a2687-830b-4d7e-9647-d0f57358d810.png)

```java
interface A {
  default void hello() {
    System.out.println("Hello form A");
  }
}

interface B extends A {
  default void hello() {
    System.out.println("Hello from B");
  }
}

class D implements A {
}

class C extends D implements B, A {
  public static void main(String[] args) {
    new C().hello(); // 무엇이 출력될까?
  }
}
```

클래스나 슈퍼클래스에 메서드 정의가 없으므로, 디폴트 메서드를 정의하는 서브 인터페이스가 선택된다. 따라서 여전히 B 가 선택된다.

### 2. 하지만 다음과 같은 경우는 어떨까?

```java
interface A {
  default void hello() {
    System.out.println("Hello form A");
  }
}

interface B extends A {
  default void hello() {
    System.out.println("Hello from B");
  }
}

class D implements A {
  @Override
  public void hello() {
    System.out.println("Hello from D");
  }
}

class C extends D implements B, A {
  public static void main(String[] args) {
    new C().hello(); // 무엇이 출력될까?
  }
}
```

D가 출력된다. 슈퍼클래스의 메서드 정의가 우선권을 갖기 때문이다.

### 3. 하지만 다음과 같은 경우는 어떨까?

```java
interface A {
  default void hello() {
    System.out.println("Hello form A");
  }
}

interface B extends A {
  default void hello() {
    System.out.println("Hello from B");
  }
}

abstract class D implements A {
  public abstract void hello();
}
```

A에서 디폴트 메서드가 제공됨에도 불구하고, C 는 hello 를 구현해야 한다.

## 13.4.3 충돌 그리고 명시적인 문제 해결

```java
interface A {
  default void hello() {
    System.out.println("Hello form A");
  }
}

interface B {
  default void hello() {
    System.out.println("Hello from B");
  }
}

public class C implements B, A { // 컴파일 에러

}
```

A, B 인터페이스 간에 상속관계가 없으므로, A 와 B 의 hello 메서드를 구별할 방법이 없다. 자바 컴파일러는 어떤 메서드를 호출해야 할 지 알 수 없어서 에러가 발생한다.

### 충돌 해결

해결 방법은 클래스 C 에서 hello 메서드를 오버라이드 한 다음에 호출하려는 메서드를 명시적으로 선택해야 한다.

```java
class C implements B, A {
  public void hello() {
    B.super.hello();
  }
}
```

### 다른 재미있는 문제

```java
interface A {
  default Number getNumber() {
    return 10;
  }
}

interface B {
  default Integer getNumber() {
    return 42;
  }
}

class C implements B, A {
  public static void main(String[] args) {
    System.out.println(new C().getNumber());
  }
}
```

C는 A 와 B 의 메서드를 구별할 수 없다. 컴파일 에러가 발생한다.

## 13.4.4 다이아몬드 문제

![image](https://user-images.githubusercontent.com/92802207/215994410-d9c4e188-31ef-47c9-bb01-af5fb136bc29.png)

```java
interface A {
  default void hello() {
    System.out.println("Hello from A");
  }
}

interface B extends A {
}

interface C extends A {
}

class D implements B, C {
  public static void main(String[] args) {
    new D().hello(); // 무엇이 출력될까?
  }
}
```

A만 디폴트 메서드를 정의하고 있으므로 A가 출력된다.

### 다음 세 가지 규칙만 기억하면 모든 충돌 문제를 해결할 수 있다.

1. 클래스가 항상 이긴다. 클래스에서 정의한 메서드가 항상 우선권을 갖는다.
2. 1번 이외에는 서브인터페이스가 이긴다.
3. 여전히 우선순위가 결정되지 않았다면, 명시적으로 디폴트 메서드를 오버라이드하고 호출해야 한다.
