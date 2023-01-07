# Chapter 4  스트림 소개

컬렉션은 데이터를 그룹화하고, 처리를 할수 있게한다. 하지만 뭔가가 아쉽다…?



- SLECT name FROM dishes WHERE calories < 400
  
    칼로리가 낮은 요리를 선택하라는 SQL 질의다 이와같은 선언형으로 표현할 수는 없을까?
    
    (어떻게 구현해야할지 명시할 필요 없음)
    
  
  
- 방대한 양의 데이터가 있는 컬렉션을 병렬로 처리할 수 있는 방법은 없을까 ?
  
    (구현은 가능하지만 까다롭다…)
    
    

자바8에서 위의 아쉬움들을 덜어줄 `스트림(stream)` 이 등장했다.



---

# 4.1 스트림이란 무엇인가?

스트림은 자바8에서 추가된 기능이다. 선언형(데이터를 처리하는 임시 구현 코드 대신 질의로 표현 가능)으로 컬렉션 데이터를 처리 할 수 있다.

스트림의 강력함을 예제를 통해 먼저 확인해보자.



**예제**

**Java 7 : 저칼로리 음식 이름 그룹화**

```java
//자바7

List<Dish> lowCaloriesDishes = new ArrayList<>();
        System.out.println("Java 7");
        for (Dish dish : menu) {
            if( dish.getCalories() < 400){
                lowCaloriesDishes.add(dish);
            }
        }
        Collections.sort(lowCaloriesDishes, new Comparator<Dish>() {
            @Override
            public int compare(Dish dish1, Dish dish2) {
                return Integer.compare(dish1.getCalories(), dish2.getCalories());
            }
        });
        List<String> lowCaloriesDishesName = new ArrayList<>();
        for (Dish dish : lowCaloriesDishes) {
            lowCaloriesDishesName.add(dish.getName());
        }
```



**Java 8 : 저칼로리 음식 이름 그룹화 코드**

```java
//JAVA 8
System.out.println("\nUsing Stream in Java 8");
List<String> lowCaloriesDishesName2 = menu.stream()
        .filter(dish -> dish.getCalories() < 400)
        .sorted(Comparator.comparing(Dish::getCalories))
        .map(Dish::getName)
        .collect(Collectors.toList());
```



**Java 8 : 저칼로리 음식 이름 그룹화 코드**

```java
//JAVA 8 : ParallelStream
System.out.println("\nUsing parallelStream in Java 8");
List<String> lowCaloriesDishesName3 = menu.parallelStream()
        .filter(dish -> dish.getCalories() < 400)
        .sorted(Comparator.comparing(Dish::getCalories))
        .map(Dish::getName)
        .collect(Collectors.toList());
```



**스트림 API 특징**

1. 선언형 : 더 간결하고 가독성이 좋다.

2. 조립할 수 있음 : 유연성이 좋다.

3. 병렬화 : 성능이 좋다.

   

추가로 filter, sorted, map, collect와 같은 연산은 `고수준 빌딩 블록` 으로 이루어져 있으므로 특정 스레딩 모델에 제한되지 않고 자유롭게 어떤 상황에서든 사용할 수 있다.

⇒ 데이터 처리 과정을 병렬화하면서 스레드와 락을 걱정할 필요가 없다.



**라이브러리 소개👍** 

`구아바, 아파치` : 멀티맵, 멀티셋등 추가적인 컨테이너 클래스를 제공한다.

`람다제이` : 함수형 프로그래밍에서 영감을 받은 선언형으로 컬렉션을 제어하는 다양한 유틸리티 제공한다.



---

# 4.2 스트림 시작하기.

`스트림(stream)`

**데이터 처리 연산**을 지원하도록 **소스**에서 추출된 **연속된 요소**



스트림 정의의 키워드를 분석해보자.

- 연속된 요소
  
    컬렉션과 마찬가지로 스트림은 특정 요소 형식으로 이루어진 연속된 값 집합의 인터페이스를 제공한다. 
    
    **컬렉션** => 데이터 위주 => 자료구조이므로 시간 복잡성, 공간 복잡성과 관련된 요소 저장 및 접근 연산이 주를 이룬다. 
    
    **스트림** => 계 산   위주 =>  filter, sorted, map처럼 표현 계산식이 주를 이룬다.
    
    
    
- 데이터 처리 연산
  
    스트림은 함수형 프로그래밍 언어에서 일반적으로 지원하는 연산과 데이터베이스와 비슷한 연산을 지원한다.
    
    
    
- 소스
  
    스트림은 컬렉션, 배열, I/O자원 등의 데이터 제공 소스로 부터 데이터를 소비한다.
    
    정렬된 소스로 부터 스트림을 만들면 요소의 정렬이 유지되어있다.
    
    

**예시**

```java
List<String> threeHighCaloricDishNames =
        menu.stream()                           // 메뉴에서 스트림을 얻는다.
            .filter(d -> d.getCalories() > 300) // 고칼로리 요리 필터링
            .map(Dish::getName)                 // 요리명 추출
            .limit(3)                           // 선착순 세개만 선택
            .collect(toList());                 // 결과를 다른 리스트로 저장
System.out.println(threeHighCaloricDishNames);  // [pork, beef, chicken]
```

- 여기서 **데이터 소스**는 요리 리스트.

- 데이터 소스는 **연속된 요소**를 스트림에 제공.

- 스트림에 filter, map, limit, collect로 이어지는 일련의 **데이터 처리 연산**을 적용.

- collect를 제외한 모든 연산은 서로 **파이프라인**을 형성할 수 있도록 스트림을 반환.

- 마지막으로 collect 연산으로 파이프라인을 처리해서 결과를 반환

- collect를 호출하기 전까지는 menu에서 아무것도 선택되지 않으며 출력 결과도 없다.

  

**스트림 API 특징**

1. 파이프 라이닝 
   
    대부분의 스트림 연산은 스트림 연산끼리 연결해서 커다란 파이프라인을 구성할 수 있도록 스트림 자신을 반환한다.
    
    ⇒ 게으름(lazziness), 쇼트서킷(short circuiting)과 같은 최적화도 얻을수 있다.
    
    
    
2. 내부 반복
   
    반복자를 이용해서 명시적으로 반복하는 컬렉션과 달리 스트림은 내부반복을 지원한다.
    
    

---

# 4.3 스트림과 컬렉션

자바의 기존 `컬렉션`과 새로운 `스트림` 모두 **연속된 요소** 형식의 값을 저장하는 자료구조의 인터페이스를 제공한다.



**스트림과 컬렉션의 차이**

컬렉션과 스트림의 가장 큰 차이는 데이터를 **언제** 계산하느냐이다.



**컬렉션**은 현재 자료구조가 포함하는 **모든** 값을 메모리에 저장하는 자료구조다.

컬렉션에 요소를 추가하거나 제거할 수 있으며, 컬렉션의 모든 요소는 컬렉션에 추가하기 전에 계산되어야 한다.



**스트림**은 이론적으로 **요청할 때만 요소를 계산**하는 고정된 자료구조다.

스트림에는 요소를 추가하거나 제거할 수 없다. 결과적으로 스트림은 생산자와 소비자 관계를 형성한다.

컬렉션이 DVD에 저장된 영화라면, 스트림은 인터넷으로 스트리밍하는 영화라고 할 수 있다.



## 4.3.1 딱 한번만 탐색할 수 있다

반복자와 마찬가지로 스트림도 한번만 탐색할 수 있다. 즉, 탐색된 스트림의 요소는 소비된다.

한 번 탐색한 요소를 다시 탐색하려면 초기 데이터 소스에서 새로운 스트림을 만들어야 한다.

만약 I/O채널처럼 반복 사용할 수 없는 소스라면 새로운 스트림을 다시 만들 수 없다.



## 4.3.2 외부 반복과 내부 반복

컬렉션 인터페이스를 사용하려면 for-each 등을 사용해서 사용자가 직접 요소를 반복하는 **외부 반복**을 사용한다. 

반면, 스트림 라이브러리는 반복을 알아서 처리하고 결과 스트림값을 어딘가에 저장해주는 **내부 반복**을 사용한다.

내부 반복을 사용하면 작업을 투명하게 병렬로 처리하거나 더 최적화된 다양한 순서로 처리할 수 있다.



**예시**

```java
//컬렉션 : for-each 루프를 이용하는 외부반복
List<String> names = new ArrayList<>();
for(Dish dish : menu){                      //명시적 반복
	names.add(dish.getName());                
}

//컬렉션 : 내부적으로 숨겨졌던 반복자를 사용한 외부반복
List<String> names = new ArrayList<>();
Iterator<String> iterator = menu.iterator();
for(iterator.hasNext()){                    //명시적 반복
	Dish dish = iterator.next();          
	names.add(dish.getName());           
}

//스트림 : 내부 반복
List<String> names = menu.stream()
												 .map(Dish::getName)
                         .collect(toList());
```



# 4.4 스트림 연산

스트림 인터페이스의 연산을 크게 두 가지로 구분할 수 있다.

- **중간 연산** : filter, map, limit 등으로 서로 연결하여 파이프라인을 형성한다.
- **최종 연산** : collect로 파이프라인을 실행한 다음 닫는다.

![image-20230106142002948](C:\Users\oryuk\AppData\Roaming\Typora\typora-user-images\image-20230106142002948.png)

```java
List<String> names = menu.stream()
                            .filter(dish -> dish.getCalories() > 300) //중간 연산
                            .map(Dish::getName)                       //중간 연산
                            .limit(3)                                 //중간 연산
                         	.collect(toList());                       //최종 연산
```



## **4.4.1 중간 연산**

중간 연산은 다른 스트림을 반환한다. 따라서 여러 중간 연산을 연결해 질의로 만들 수 있다.

중간 연산의 중요한 특징은 단말 연산을 스트림 파이프라인에 실행하기 전까지는 아무 연산도 수행하지 않는다는 것, 즉 게으르다는 것이다. 

중간 연산을 합친 다음에 합쳐진 중간 연산을 최종 연산으로 한번에 처리하기 때문이다.

스트림의 게으른 특성 덕분에 **쇼트서킷**, **루프 퓨전** 등의 최적화 효과를 얻을 수 있다.(다음 장에서 자세히 설명한다)



## **4.4.2 최종 연산**

최종 연산은 스트림 파이프라인에서 결과를 도출한다. 이를 통해 List, Integer, void 등 스트림 이외의 결과가 반환된다.



## 4.4.3 스트림 이용하기

스트림 이용 과정은 다음과 같이 세가지로 요약.

- 질의를 수행할 (컬렉션 같은) 데이터 소스.
- 스트림 파이프라인을 구성할 중간 연산 연결
- 스트림 파이프라인을 실행하고 결과를 만들 최종 연산



**스트림 중간 연산**

| 연산 | 형식 | 반환 형식 | 연산의 인수 |  함수 디스크립터 |
| --- | --- | --- | --- | --- |
| filter | 중간 연산 | Stream<T> | Predicate<T> | T → boolean |
| map | 중간 연산 | Stream<R> | Function<T, R> | T → R |
| limit | 중간 연산 | Stream<T> |  |  |
| sorted | 중간 연산 | Stream<T> | Comparator<T> | (T, T) → int |
| distinct | 중간 연산 | Stream<T> |  |  |



**스트림 최종 연산**

| 연산 | 형식 | 반환 형식 | 목적 |
| --- | --- | --- | --- |
| forEach | 최종 연산 | void | 스트림의 각 요소를 소비하면서 람다를 적용한다. |
| count | 최종 연산 | long(generic) | 스트림의 요소 개수를 반환한다. |
| collect | 최종 연산 |  | 스트림을 리듀스해서 리스트, 맵, 정수 형식의 컬렉션을 만든다. |
