# Chapter 8. 컬렉션 API 개선
## 8.1 컬렉션 팩토리
### 1. 컬렉션 프레임워크(Collections Framework)
 * **데이터 군**을 저장하는 **클래스들을 표준화** 한 설계.
 * Collections(다수의 데이터) + 프레임워크(표준화 된 프로그래밍 방식)
 > **<핵심 인터페이스>**
 > 
 > <img src = "https://user-images.githubusercontent.com/107912763/213863110-d517eb79-ca82-44b1-b83c-93c74c91f04a.png" height = "200" width = "400">


<img src = "https://user-images.githubusercontent.com/107912763/213863121-d3cdd88b-35f7-4b1c-a752-b95a8266ae6c.png" height = "400" width = "500">


### 2. 리스트 팩토리
**자바 9**부터는 **정적 팩토리 메서드**인 `List.of()`를 사용하여 간단하게 **변경할 수 없는 불변 리스트**를 만들 수 있다.
하지만 이 리스트는 변경할 수 없는 컬렉션(ImmutableCollections)이므로 **새로운 요소를 추가하거나 제거, 변경**할 수 없다. ⇒ `UnsupportedOperationException` 예외 발생

+) 인수로 `null` 넘기는 것도 안됨


``` java
List<String> friends = List.of("Raphael", "Olivia", "Thibaut");
System.out.println(friends);
```

#### 추가 내용
``` java
// 자바 8 이전 (요소를 추가, 제거할 수는 없지만 요소는 set()으로 변경 가능)
List<Integer> nums = Arrays.asList(1, 2, 3, 4);
// 자바 9 이후 (변경 불가)
List<Integer> nums = List.of(1, 2, 3, 4);
```
``` java
// 자바 8 이전
List<Integer> nums = new ArrayList<>(Arrays.asList(1, 2, 3, 4));
// 자바 9 이후
List<Integer> nums = new ArrayList<>(List.of(1, 2, 3, 4));
```


#### 스트림API와 리스트 팩토리
데이터 처리 형식을 설정하거나 데이터를 변환할 필요가 없다면 사용하기 간편한 **팩토리 메서드**를 사용하면 된다.
* 구현이 더 단순하고 목적을 달성하는데 충분하기 때문


### 3. 집합 팩토리
**자바 9**부터는 `List.of()`와 마찬가지로 `Set.of()`라는 **정적 팩토리 메서드**를 제공한다.
**집합**도 **변경할 수 없는 컬렉션**이므로 새로운 요소를 추가하거나 제거할 수 없다.
* 추가 or 제거 시도 시 → `UnsupportedOperationException` 예외가 발생
* 인수 중에 중복된 인수가 있음 → `IllegalArgumentException` 예외

+) 당연히 인수로 `null`넘기는 건 안됨.
``` java
// 가능
Set<String> friends = Set.of("Raphael", "Olivia", "Thibaut");

// 요소가 중복되어 있다는 IllegalArgumentException 발생
Set<String> friends = Set.of("Raphael", "Olivia", "Olivia");
```


### 4. 맵 팩토리
**자바 9**에서는 두 가지 방법으로 **바꿀 수 없는 맵을 초기화**할 수 있다. 특징은 set과 비슷함.


#### 방법 1. Map.of 팩토리 메서드에 키와 값을 번갈아 제공하는 방법(주로 10개 이하의 키와 값 쌍을 가진 작은 맵)
#### 방법 2. Map.ofEntries 팩토리 메서드 이용 (주로 큰 맵)
``` java
// 자바 8 이전 (변경 가능)
Map<Integer, String> map = new HashMap<>();
map.put(1, "one");
map.put(2, "two");
map.put(3, "three");
 
// 자바 9 이후 (변경 불가)
// 최대 10개의 키-값 쌍을 넣을 수 있고 (키1, 값1, 키2, 값2, ..., 키10, 값10)과 같이 넣으면 된다.
Map<Integer, String> map = Map.of(
        1, "one", 
        2, "two", 
        3, "three"
);
 
// 자바 9 이후 (변경 불가)
// 이 방법은 Map.of()와 달리 개수 제한이 없다.
Map<Integer, String> map = Map.ofEntries(
        Map.entry(1, "one"),
        Map.entry(2, "two"),
        Map.entry(3, "three")
);
```


---
## 8.2 리스트와 집합 처리

### 1. removeIf 메서드
* **삭제할 요소를 가리키는 프레디케이트**를 인수로 받는다. 그리고 이를 만족하는 **요소를 제거**한다.
* `List`나 `Set`을 구현하거나 그 구현을 상속 받은 모든 클래스에서 이용 가능하다.


**<예제> - 3의 배수만 제거하기**
``` java
List<Integer> numbers = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
System.out.println("numbers: " + numbers.toString());

numbers.removeIf(n -> (n % 3 == 0));
System.out.println("numbers(after remove): " + numbers.toString());
```


### 2. replaceAll 메서드
* 인자로 **함수**를 받는데, 함수는 **리스트의 요소를 인자**로 받으며 **변경할 값을 리턴**한다. 
* 즉, 리스트의 **모든 요소를 순회하면서 값을 변경**할 수 있다.
* **스트림 API를 사용하지 않는 이유!** → 우리는 지금 기존 컬렉션을 바꾸는 것을 하고 있으니까


**<예제> - 리스트의 요소가 null일 때, 빈 문자열로 변경하여 null이 존재하지 않도록 만드는 예제**
``` java
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Example1 {

    public static void main(String[] args) {

        List<String> myList = new ArrayList<>(Arrays.asList
				("A", null, "B", null, "C", null, "D"));
        System.out.println(myList);

        myList.replaceAll(s -> s != null ? s : "");
        System.out.println(myList);
    }
}
```

---
## 8.3 맵 처리
**자바 8**부터 Map 인터페이스는 BiConsumer를 인수로 받는 `forEach` 메서드를 지원하므로 코드를 좀 더 간단하게 구현할 수 있다.
``` java
ageOfFriends.forEach((friend, age) -> 
                     System.out.println(friend + " is " +age+" years old"));
```


### 1. 정렬 메서드
```
Entry.comparingByValue         맵의 항목을 값을 기준으로 정렬
Entry.comparingByKey           맵의 항목을 키를 기준으로 정렬
```
**<예시>**
``` java
Map<String, String> favoriteMovies = Map.ofEntries(
        Map.entry("ljo", "Star Wars"),
        Map.entry("hsy", "Matrix"),
        Map.entry("yhh", "James Bond")
);

favoriteMovies.entrySet().stream()
		.sorted(Entry.comparingByKey())
		.forEachOrdered(System.out::println); // 키 값 순서대로
```
**<결과>**
```
hsy=Matrix
ljo=Star Wars
yhh=James Bond
```


### 2. getOrDefault 메서드
**기존에 찾으려는 키가 존재하지 않을 경우** NPE을 방지하기 위해 `null` 체크를 해야 했지만, `getOrDefault` 를 이용하면 이를 해결 할 수 있다. → **getOrDefault는 기본 값을 반환**한다.
* **첫 번째 인수**는 `키`, **두 번째 인수**로는 `기본 값`을 받는다.
* 맵에 키가 존재하지 않으면 두 번째 인수로 받은 기본 값을 반환
* 키가 존재하더라도 값이 `null`이면, `null`을 반환할 수 있으니 주의
``` java
Map<String, String> favouriteMovies
					= Map.ofEntries(entry("Raphael", "Star Wars"), entry("Olivia", "James Bond"));

System.out.println(favouriteMovies.getOrDefault("Olivia", "Matrix")
//결과 : James Bond
System.out.println(favouriteMovies.getOrDefault("Thibaut", "Matrix")
//결과 : Matrix
```


### 3. 계산 패턴
맵에 키가 존재하는지 여부에 따라 어떤 동작을 실행하고 **결과를 저장해야 하는 상황**이 필요한 때
* `computeIfAbsent` : 제공된 **키에 해당하는 값이 없으면**, 키를 이용해 새 값을 계산하고 맵에 추가
* `computeIfPresent` : 제공된 **키가 존재**하면 새 값을 계산하고 맵에 추가. 키와 관련된 값이 널이X
* `compute` : 제공된 키로 새 값을 계산하고 맵에 저장
**<예제> - computeIfPresent & compute 예시 - text에 같은 단어가 몇 번 나왔는지 계산**
``` java
String text = "KOREA KOREA USA USA USA CANADA JAPAN ";

Map<String, Integer> wordMap = new HashMap<>();

wordMap.put("KOREA", 0);
wordMap.put("USA", 0);
wordMap.put("CHINA", 0);
wordMap.put("JAPAN", 0);
```

**computeIfPresent사용**

``` java
for(String word : text.split(" ")){
	wordMap.computeIfPresent(word, (String key, Integer value) ->  ++value);
}

System.out.println("wordMap = " + wordMap);

/*결과 : wordMap = {USA=3, CHINA=0, JAPAN=1, KOREA=2} */
```
**compute 사용 시** → 에러(`NullPointerException`)가 발생
=> CANADA가 없기 때문이다. text에 CANADA가 없다면 결과는 동일하다.


### 4. 삭제 패턴
* 제공된 키에 해당하는 맵 요소를 제거하는 `remove` 메서드는 이미 있다.
  * 삭제할 경우 키가 존재하는지 확인하고 값을 삭제
* **자바 8** 에서는 **키가 특정한 값과 연관되어 있을 때만 항목을 제거**하는 **오버로드 버전 메서드**를 제공한다.
**<예제> - 기존 버전**
``` java
String key = "Raphael"
String value = "Jack Reacher 2";
if(fovouriteMovies.containsKey(key) && Objects.equals(favouriteMovies.get(key), value)){
			favouriteMovies.remove(key);   // 기존 버전
			return true;
    }
  else{
			return false;
    }
```

**- 새로운 방법**

``` java
favouriteMovies.remove(key, value); // new 버전
```



### 5. 교체 패턴
맵의 **항목을 바꾸는데 사용**할 수 있는 메서드들
* `replaceAll`
  * Bifunction 을 적용한 결과로 **각 항목의 값을 교체**한다.
  * 이 메서드는 `List` 의 `replaceAll` 과 비슷한 동작을 수행
 
 
* `Replace`
  * **키가 존재**하면 맵의 값을 바꾼다.
  * 키가 특정 값으로 **매핑되었을 때만** 값을 교체하는 **오버로드 버전**도 있다.



### 6. 합침
`putAll` 사용 시 → **중복된 키**가 있다면 문제가 됨!
따라서 `merge` 메서드 사용 - 중복된 키를 어떻게 합칠지 결정하는 BiFunction을 인수로 받는다.

**<예제>**
``` java
Map<String, String> family = Map.ofEntries(
	entry("Teo", "Star Wars"), entry("Cristina", "James Bond")
);
Map<String, String> friends = Map.ofEntries(
	entry("Raphael", "Star Wars"), entry("Cristina", "Matrix")
);

// merge 메서드 사용 - 조건에 따라 맵을 합치는 코드
Map<String, String> everyone = new HashMap<>(family);
friends.forEach((k, v) -> 
	everyone.merge(k, v, (movie1, movie2) -> movie1 + " & " + movie2)
);

// 결과 : {Raphael=Star Wars, Cristina=James Bond & Matrix, Teo=Star Wars}
```
+) `merge` 메서드는 `null`값 관련 상황 복잡한 처리도 가능




---
## 8-4. 개선된 ConcurrentHashMap
#### 기존 HashMap
* 키와 값을 묶어서 하나의 데이터(entry)로 저장
* **해싱**을 사용하기 때문에 **많은 양의 데이터를 검색하는데 있어서 뛰어난 성능**을 보임
* 여기서 키는 저장된 값을 찾는데 사용되기 때문에 컬렉션 내에서 **유일**해야 함.
  * 하나의 키에 대해 여러 검색 결과 값을 얻는다면 원하는 값이 무엇인지 알 수 없으니까
 
 
 #### 개선된 ConcurrentHashMap
 * 내부 자료구조의 특정 부분만 잠궈 동시 추가, 갱신 작업을 허용
 
 
 ### 1. 리듀스와 검색
 ```
forEach   : 각 (키, 값) 쌍에 주어진 액션을 실행
reduce    : 모든 (키, 값) 쌍을 제공된 리듀스 함수를 이용해 결과로 합침
search    : null 이 아닌 값을 반환할 때까지 각 (키, 값) 쌍에 함수를 적용
 ```
 
 
 **연산 형태**
1. 키, 값으로 연산 (`forEach` , `reduce` , `search`)
2. 키로 연산 ( `forEachKey`, `reduceKey`, `searchKey` )
3. 값으로 연산 ( `forEachValue`, `reduceValue`, `searchValue` )
4. `Map.Entry` 객체로 연산 ( `forEachEntry`, `reduceEntry`, `searchEntry` )


위의 연산들은 `ConcurrentHashMap`의 **상태를 잠그지 않고 연산을 수행**한다.
따라서, 이들 연산에 제공한 함수는 계산이 진행되는 동안 **바뀔 수 있는 객체, 값, 순서 등에 의존하지 않아야 한다.**
또한 연산에 **병렬성 기준값**(threshold)를 정해야 한다. 맵의 크기가 **기준 값보다 작으면 순차적으로 연산**을 진행한다.



### 2. 계수
`ConcurrentHashMap` 클래스는 맵의 **매핑 개수를 반환**하는 `mappingCount` 메서드 제공.

`mappingCount` 메서드는 매핑의 개수가 **int의 범위를 넘어서는 이후의 상황을 대처할 수 있다.**



### 3. 집합뷰
`ConcurrentHashMap`을 집합 뷰로 반환하는 `keySet` 메서드를 제공한다.
**맵을 바꾸면 집합도 바뀌고 반대로 집합을 바꾸면 맵도 영향을 받는다.**
`newKeySet`이라는 메서드를 통해 `ConcurrentHashMap`으로 **유지되는 집합**을 만들 수도 있다.


  
