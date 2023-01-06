# Chapter 3 람다 표현식



# 3.1 람다란 무엇인가?



**익명클래스를 이용한 동적 파라미터화**

```java
appleFilter(inventory, new PredicateApple() {
            @Override
            public boolean test(Apple apple) {
                return apple.getColor() > Color.GREEN;
            }
        });
```



**람다를 이용한 동적 파라미터화** 

```java
appleFilter(inventory, (Apple apple) -> apple.getColor() == Color.GREEN);
```



`람다 표현식` 

- 메서드로 전달 할 수 있는 **익명 함수를 단순화 한 것**
  
    [ 특징 ]
    
    - **익명** :  보통의 메서드와 달리 이름이 없음.
    - **함수** :  람다는 메서드처럼 클래스에 종속되지 않으므로 함수라고 한다.
    - **전달** :  람다 표현식을 메서드 인수로 전달하거나 변수로 저장 가능하다.
    - **간결성** : 익명클래스처럼 자질구레한 코드를 구현할 필요가 없다.
    
    [ 효과 ]
    
    1. 코드가 간결하고 유연해진다.
    2. 개발자의 의도가 더 명확해진다.
    3. 익명클래스의 메서드를 만드는 과정이 사라져서 생산성이 증가한다.
    
    

`람다 문법`

1. 표현식 스타일 람다( Expression Style Lambda )

    ```java
    // (parameter) -> expression
    
    // ex)
    ( ) -> "Iron Man"
    ```

    

1. 블록 스타일 람다( Block Style Lambda )

    ```java
    //(parameter) -> {statement;}
    
    //ex)
    
    ( ) -> {return "Iron Man";}
    ```


자세한 예제 (p91) 표3-1 확인.

---

# 3.2 어디에 어떻게 람다를 사용하는가?

람다를 그럼 어떻게 쓸 수 있을까 ? 동적  파라미터화에서 ??



## 3.2.1 함수형 인터페이스

`함수형 인터페이스`

- 정확히 하나의 추상메서드를 지정하는 인터페이스이다.
  
    [ 헷갈리지 말자 ]
    
    1. 디폴트 메서드가 많이 있어도 추상메서드가 하나면 함수형 인터페이스이다..!
    2. 부모 인터페이스로부터 추상메서드를 하나 상속받으면 함수형 인터페이스가 아니다..!!
    
    (p93) 퀴즈 3-2 참고
    
    

 **람다 표현식으로 함수형 인터페이스의 추상 메서드 구현을 직접 전달 할 수 있으므로 전체 표현식을 함수형 인터페이스의 인스턴스로 취급한다.**

 즉, 구현부 클래스의 인스턴스의 역할을 하는데, 구현부 클래스(익명 클래스) 생성과정을 생략한 것이다. 정확히는 생략이 아니라 컴파일러가 익명클래스를 생성해줌(?)



## 3.2.2 함수 디스크립터

`함수 디스크립터`

함수형 인터페이스의 추상 메서드 시그니처

람다 표현식의 시그니처를 서술하는 메서드. ex ) ( ) → void( ) : Runable

 

동적 파라미터화를 위해, 메서드에 함수를 파라미터로 넘길 때는

함수형 인터페이스의 추상메서드와 람다의 함수 디스크립터가 일치하도록 람다를 작성해야한다.

```java
public void process(Runable r){
	r.run();
}

process( () -> System.out.println("Difficult JAVA"));
```

1. Runable 인터페이스의 run메서드 시그니처
2. ( ) → System.out.println( ) 람다의 함수 디스크립터

1, 2 전부 ( ) → void( )로 동일한 시그니처를 가진다.

---

# 3.3 실행 어라운드 패턴

예시 문제..!

`실행 어라운드 패턴`

자원처리에 사용하는 순환패턴은 자원을 열고, 처리한 다음에, 자원을 닫는 순서로 이루어진다.

위의 패턴을 processFile이라는 메서드로 작성하길 원한다.

변화에 유연한 메서드로 작성하기 위하여 동적 파라미터화를 하려고한다.



**예시**

```java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static String processFile() throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("애국가.txt"));) {
            return br.readLine();
        }
    }

    public static String processFile(BufferedReaderProcessor p) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("애국가.txt"));) {
            return p.process(br);
        }
    }


    public static void main(String[] args) throws IOException {

        String oneLine = processFile();
        System.out.println("oneLine = \n" + oneLine + '\n');


        String oneLine2 = processFile((BufferedReader br) -> br.readLine());
        System.out.println("oneLine2 = \n" + oneLine2 + '\n');


        String fourLine = processFile((BufferedReader br)
                -> br.readLine() + "\n"
                + br.readLine() + "\n"
                + br.readLine() + "\n"
                + br.readLine() + "\n");
        System.out.println("fourLine = \n" + fourLine);

    }
}

//>> 출력 결과
//oneLine = 
//동해물과 백두산이 마르고 닳도록

//oneLine2 = 
//동해물과 백두산이 마르고 닳도록

//fourLine = 
//동해물과 백두산이 마르고 닳도록
//하느님이 보우하사 우리나라 만세
//무궁화 삼천리 화려강한 산
//대한사람 대한으로 길이 보전하세

```

**애국가.txt**

```txt
동해물과 백두산이 마르고 닳도록
하느님이 보우하사 우리나라 만세
무궁화 삼천리 화려강한 산
대한사람 대한으로 길이 보전하세
```



---

# 3.4 함수형 인터페이스

`java.util.function`

다양한 람다 표현식을 사용하려면 공통의 함수 디스크립터를 기술하는 **함수형 인터페이스 집합**이 필요하다. 

자바8 설계자들이 java.util.function 패키지에 여러가지 함수형 인터페이스를 작성해놓음.

ex) Predicate<T>, Consumer<T>, Function<T, R> 제네릭 함수형 인터페이스



`기본형 특화`

제네릭 함수형 인터페이스 Predicate<T>, Consumer<T>, Function<T, R>는 제네릭 파라미터에는

참조형만 사용할 수 있다.



**Q .    그렇다면 기본형을 파라미터로 넣을 수 없는 것인가 ?** 

**Ans . NO. 넣을 수 있다!!**



자바에서는 기본형 ⇒ 참조형 변환하는 기능을 제공 ⇒ `박싱(boxing)`

(반대로, 참조형 ⇒ 기본형 변환하는 기능을 제공 ⇒ `언박싱(unboxing)` )

프로그래머의 편의를 위해 박싱과 언박싱이 자동으로 이루어지는 `오토박싱(autoboxing)` 기능 제공.

```java
//**오토 박싱 예시**

List<Interger> list = new ArrayList<>();
for (int i=300; i<400; i++){
	list.add(i)
}
```

자바8 에서는 기본형을 입출력으로 사용하는 상황에서 오토박싱 동작을 피할 수 있도록 특별한 버전의 함수형 인터페이스를 제공한다.

ex) IntPredicate, DoublePredicate, IntConsumer, IntFunction 등등..

```java
public interface IntPredicate{
	boolean test(int t);
}

IntPredicate isEvenNumbers = (int i) -> i%2 == 0;
isEvenNumbers.test(1000)

Predicate<Integer> isOddNumners = (Integer i) -> i%2 != 0;
isOddNumbers.test(1000);
```

| 함수형 인터페이스   | 함수 디스크립터  | 기본형 특화                                                  |
| ------------------- | --------------------------------- | ------------------------------------------- |
| Predicate<T>        | T → boolean     | IntPredicate, LongPredicate, DoublePredicate                 |
| Consumer<T>         | T → void         | IntConsumer,  LongConsumer, DoubleConsumer                   |
| Function<T, R>      | T → R            | IntFunction<R>, IntToDoubleFunction, <br />IntToLongFunction, LongFunction<R>,<br /> LongToDoubleFunction, LongToIntFunction,<br />DoubleFunction<R>, DoubleToIntFunction,<br />DoubleToLongFunction, ToIntFunction<T>,<br />ToDoubleFunction<T>, ToLongFunction<T> |
| Supplier<T>         | ( ) → T          | BooleanSupplier, IntSupplier,<br />LongSupplier, DoubleSupplier |
| UnaryOperator<T>    | T → T            | IntUnaryOperator, LongUnaryOperator,<br />DoubleUnaryOperator |
| BinaryOperator<T>   | (T, T) → T       | IntBinaryOperator, LongBinaryOperator,<br />DoubleBinaryOperator |
| BiPredicate<L, R>   | (T, U) → boolean |                                                              |
| BiConsumer<T, U>    | (T, U) → void    | objIntConsumer<T>, objLongConsumer<T>, <br />objDoubleConsumer<T> |
| BiFunction<T, U, R> | (T, U) → R       | ToIntBiFunction<T, U>, ToLongBiFunction<T, U>, <br />ToDoubleBiFunction<T, U> |

자세한 예제 (p106~107)



**[ 참고 ]**

> **기본형(Primitive Type)    : int, double, byte, char 등등** 
**참조형(Reference Type)  : Object, Interger, Byte, List, 등등 인스턴스들**
> 

---



# 3.5 형식 검사, 형식 추론, 제약

람다가 사용되는 콘텍스트context를 이용해서 람다의 형식을 추론할 수 있다.



## 3.5.1 형식 검사.

`대상형식(target typing)`

콘텍스트에서 기대되는 람다 표현식의 형식.

콘텍스트 : 람다가 전달될 메서드 파라미터나 람다가 할당되는 변수.



**형식 확인 과정 예시**

```java
List<Apple> heavierThan150g =
filter(inventory, (Apple apple) -> apple.getWeight() > 150);
```

1. filter 메서드를 선언을 확인한다.
2. filter 메서드는 두 번째 파라미터로 Predicate<Apple> 형식(대상형식)을 기대한다.
3. Predicate<Apple>은 test라는 한개의 추상 메서드를 정의하는 함수형 인터페이스이다.
4. test메서드는 Apple을 인수로 받아서 boolean을 반환하는 함수 디스크립터를 묘사한다.
5. filter 메서드로 전달된 인수는 이와 같은 요구사항을 만족해야한다.



**주의**

람다 표현식이 예외를 던질 수 있으면 추상메서드도 같은 예외를 던질 수 있도록 throws로 선언해야한다.



## 3.5.2 같은 람다, 다른 함수형 인터페이스

대상형식이라는 특징 때문에 같은 람다 표현식이더라도 호환되는 추상메서드를 가진 다른 함수형 인터페이스로 사용될 수 있다.



**예시** 

같은 함수 디스크립터 를 가진 함수형 인터페이스들

```java
//( ) -> T
Callable<Integer> c = () -> 42;
PrevilegedAction<Integer> p = () -> 42;

//(T, T) -> R
Comparator<Apple> c1 = (Apple a1, Apple a2) -> a1.getWeight( ).compareTo(a2.getWeight());
BiFunction<Apple> c2 = (Apple a1, Apple a2) -> a1.getWeight( ).compareTo(a2.getWeight());
ToIntBiFunction<Apple> c3 = (Apple a1, Apple a2) -> a1.getWeight( ).compareTo(a2.getWeight());
```



**확인문제**

```java
//컴파일 오류가 나는 이유는 ?
Object o = ( ) -> {System.out.println("Tricky example");};

//Ans. 람다 표현식의 대상형식은 Object가 아닌 함수형 인터페이스이다.
//아래와 같은 2가지 방식중 하나로 리팩터링을 해야한다.
Runnabel r = ( ) -> {System.out.println("Tricky example");};
Object o = (Runnable) ( ) -> {System.out.println("Tricky example");};
```



## 3.5.3 형식 추론

람다 표현식을 조금 더 단순화할 수 있는 방법이 있다.

자바 컴파일러는 람다 표현식의 대상형식을 이용해서 함수 디스크립터를 알 수 있으므로 컴파일러는 람다 시그니처도 추론할 수 있다.

```java
List<Apple> greenApple = filter(inventory, (Apple apple) -> GREEN.equals(apple.getColor())));
List<Apple> greenApple = filter(inventory, apple -> GREEN.equals(apple.getColor())));

Comparator<Apple> c = (Apple a1, Apple a2) -> a1.getWeight().compareTo(a2.getWeight());
Comparator<Apple> c = (a1, a2) -> a1.getWeight().compareTo(a2.getWeight());
```

상황에 따라 명시적으로 형식을 포함할 때가 좋을때가 있고, 배제하는 것이 가독성이 좋을 때도 있다.

정해진 규칙은 없음으로 개발자 스스로가 가독성이 좋은 방향을 택하면 된다.



### 3.5.4 지역 변수 사용

람다 표현식은 인수를 모두 자신의 바디 안에서만 사용했다. 하지만 외부의 변수도 사용할 수 있다.



`자유변수`

파라미터로 넘겨진 변수가 아닌 외부에서 정의도니 변수



`람다 캡쳐링`

람다 표현식에서 익명 함수가 하는 것처럼 자유변수를 활용하는 것.



**예시**

```java
int portNumber = 1337;
Runnable r = () -> System.out.println(portNumber);
```

람다는 인스턴스 변수와, 정적 변수를 자유롭게 캡처할 수 있다.

하지만!

지역변수는 명시적으로 final로 선언이 되어 있거나, final로 선언된 변수와 똑같이 사용되어야한다.

즉, 람다 표현식은 한번 만 할당 할 수 있는 지역 변수를 캡처할 수 있다.



**지역변수의 제약 예시** 

```java
int portNumber = 1337;
Runnable r = () -> System.out.println(portNumber);
int portNumber = 31337;

//error : 람다에서 참고하는 지역변수는 final로 선언되어있거나, final처럼 취급해야함.
```

---



# 3.6 메서드 참조

`메서드 참조`

- 기존 메서드 정의를 재활용해서 람다처럼 전달 할 수 있다.
- 특정 메서드만 호출하는 람다의 축약형이다.
- 메서드를 어떻게 호출해야하는지의 설명을 참조하기 보다는 메서드명을 직접참조하는 방식이다.
- 메서드명 앞에 구분자( :: )를 붙이는 방식으로 메서드를 참조할 수 있다

```java
(Apple apple) -> apple.getWeight( )        //Apple::getWeight

( )-> Tread.currentThread( ).dumpStack( ) //Thread.currentThread( )::dumpStack

(str, i)-> str.substring(i)               //String::substring

(String s) -> System.out.println(s)       //System.out::println

(String s) -> this.isValidName(s)         //this::isValidName 
```



**메서드 참조 만들기**

1. **정적 메서드 참조**

    ```java
    // ex) Integer의 pareInt 메서드는 Integer::parseInt로 표현
    
    ( args ) -> ClassName. staticMethod(args)                              //람다
    ClassName::staticMethod                                                //메서드 참조
    ```

    

2. **다양한 형식의 인스턴스 메서드 참조**

    ```java
    //ex) String의 length 메서드는 String::length로 표현
    
    (arg0, rest) -> arg0.instanceMethod(rest)                              //람다
    ClassName::instanceMethod                                              //메서드 참조
    ```

    

3. **기존 객체의 인스턴스 메서드 참조**

    ```java
    //ex) Transaction 객체를 할당받은 expensiveTranscation 지역변수가 있고,
    //Transcation 객체에는 getValue라는 메서드가 있다면, 
    //이를 expensiveTransaction::getValue로 표현
    
    (args) -> expr.instanceMethod(args)                                    //람다
    expr::instanceMethod                                                   //메서드 참조
    ```


컴파일러는 람다 표현식의 형식을 검사하던 방식과 비슷한 과정으로 메서드 참조가 주어진 함수형 인터페이스와 호환하는지 확인한다.

즉, 메서드 참조는 Context형식과 일치해야한다.



`생성자 참조`

ClassName::new처럼 클래스명과 new키워드를 이용해서 기존 생성자의 참조를 만들 수 있다.

이것은 정적 메서드의 참조를 만드는 방법과 비슷하다.

```java
//arg 0 개인 생성자 Apple()
Supplier<Apple> c1 = Apple::new;
Apple a1 = c1.get();

//arg 1 개인 생성자 Apple(Integer weight)
Function<Integer, Apple> c2 = Apple::new;
Apple a2 = c2.apply(100);
                 

//arg 2 개인 생성자 Apple(String color, Integer weight)
BiFunction<String, Integer, Apple> c3 = Apple::new;
Apple a3 = c3.apply("GREEN", 100);
```

- 생성자의 인수가 0개 일 때 ⇒ ( )          → T ⇒ Supplier<T>
- 생성자의 인수가 1개 일 때 ⇒  T          → R ⇒ Function<T, R>
- 생성자의 인수가 2개 일 때 ⇒  T, U     → R ⇒ BiFunction<T, U, R>
- 생성자의 인수가 3개 일 때 ⇒  T, U, V → R ⇒ TriFunction<T, U, V, R>
- 생성자의 인수가 n개 일 때 ⇒  ???
  
    Hint. Stream( )의 map!!
    

---



# 3.7 람다 메서드 참조 활용하기.



```java
public class Main {
    public static void main(String[] args) {
        List<Apple> inventory = new ArrayList<>();

        inventory.add(new Apple("a", 2));
        inventory.add(new Apple("b", 3));
        inventory.add(new Apple("c", 1));
        inventory.add(new Apple("d", 4));
        inventory.add(new Apple("e", 7));
        inventory.add(new Apple("f", 6));
        inventory.add(new Apple("g", 5));
        inventory.add(new Apple("h", 10));
        inventory.add(new Apple("i", 9));
        inventory.add(new Apple("j", 8));

        System.out.println("\n<=== Init Apple Inventory ===>");
        for (Apple apple : inventory) {
            System.out.println(apple);
        }

        //클래스로 구현체 삽입
        inventory.sort(new AppleCompartor());

        //익명 클래스로 구현체 삽입
        inventory.sort(new Comparator<Apple>() {
            @Override
            public int compare(Apple o1, Apple o2) {
                return o1.getWeight().compareTo(o2.getWeight());
            }
        });

        //람다를 이용하여 구현체 삽입
        inventory.sort((Apple a, Apple b) -> a.getWeight().compareTo(b.getWeight()));
        inventory.sort((a, b) -> a.getWeight().compareTo(b.getWeight()));
        inventory.sort(Comparator.comparing(Apple::getWeight).reversed());
        inventory.sort(comparing((Apple a) -> a.getWeight()));
        inventory.sort(comparing(Apple::getWeight));
        inventory.sort(comparing(Apple::getWeight).reversed().thenComparing(Apple::getName));


        System.out.println("\n<=== After Sort ===>");
        for (Apple apple : inventory) {
            System.out.println(apple);
        }
    }
}
```

---



# 3.8 람다 표현식을 조합할 수 있는 유용한 메서드

자바8 API의 몇몇 함수의 인터페이스는 다양한 유틸리티 메서드를 포함한다.

Comparator, Function, Predicate와 같은 함수형 인터페이스는 람다표현식을 조합할 수 있도록 유틸리티 메서드를 제공한다.

`디폴트 메서드 default method`

 메소드 선언 시에 default를 명시하게 되면 인터페이스 내부에서도 로직이 포함된 메소드를 선언할 수 있다. 이는 추상메서드가 아니므로 함수형 인터페이스의 정의에 벗어나지 않음.



**Comparator 조합 예시**

(comparing, reversed, thenComparing… 등등)

```java
//Compartor 인터페이스의 정적메서드 comparing( )을 이용해서 비교에 사용되는 키를 바로 지정.
Comparator<Apple> c = Compartor.comparing(Apple::getWeight);

//Compartor 인터페이스의 디폴트메서드 reversed( )를 이용해서 비교자의 순서를 뒤바꿈.
inventory.sort(comparing(Apple::getWeight).reversed());

//Compartor 인터페이스의 디폴트메서드 thenComparing( )를 이용해서 
//2번째 비교에 사용되는 키를 지정가능
inventory.sort(comparing(Apple::getWeight).reversed()
																					.thenComparing(Apple::getCountry));
```



**Predicate 조합 예시**

(negate, and, or)

```java
//Predicate 인터페이스의 디폴트메서드 negate를 이용해서 Predicate 객체 redApple의 결과 부정.
Predicate<Apple> notRedApple = redApple.negate();

//Predicate 인터페이스의 디폴트메서드 and를 이용해서 Predicate 객체 redApple에
//추가적인 조건을 만들수 있음.
Predicate<Apple> redAndHeavyApple = redApple.and(apple -> apple.getWeight() > 150);

//Predicate 인터페이스의 디폴트메서드 and를 이용해서 Predicate 객체 redApple에
//추가적인 조건을 만들수 있음.
Predicate<Apple> redAndHeavyAppleOrGreen 
= redApple.and(apple -> apple.getWeight() > 150)
			    .or(apple -> Green.equals(apple.getColor));
```



**Function 조합** **예시**

(andThen, compose)

```java
//Function 인터페이스의 디폴트메서드 antThen을 이용해서 함수를 합성할 수 있음.
Function<Integer, Integer> f = x -> x + 1;
Function<Integer, Integer> g = x -> x * 2;
Function<Integer, Integer> h = f.andThen(g);  //g(f(x))
int result = h.apply(1)                       //(1 + 1) * 2 = 4

//Function 인터페이스의 디폴트메서드 compose을 이용해서 함수를 합성할 수 있음.
Function<Integer, Integer> f = x -> x + 1;
Function<Integer, Integer> g = x -> x * 2;
Function<Integer, Integer> h = f.Compose(g);  //f(g(x))
int result = h.apply(1)                       //(1 * 2) + 1 = 3
```