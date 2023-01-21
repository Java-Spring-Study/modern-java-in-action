# Chapter 7. 병렬 데이터 처리와 성능
## 7.1 병렬 스트림 (parallel stream)
### 1. 순차 스트림과 병렬 스트림

* **병렬스트림** - 스트림 요소를 분할하여 **각각의 스레드에서 처리**할 수 있도록 하는 것
  * **방법** : 스트림에 `parallel()`이라는 메서드 호출
* **순차스트림** - 병렬로 처리되지 않게 함
  * **방법** : 스트림에 `sequential()`이라는 메서드 호출

 => 모든 스트림은 기본적으로 병렬 스트림이 아니므로 `sequential`를 따로 호출할 필요는 없다.
**단지 parallel을 호출한 것을 취소할 때만 사용.**
따라서 최종적으로 호출된 메서드가 전체 파이프라인에 영향을 미친다.

 => parallel()와 sequential()은 새로운 스트림을 생성하는 것이 아니라, 그저 스트림의 **속성을 변경**할 뿐이다.


``` java
public long parallelSum(long n){
  return Stream.iterate(1L, i -> i+1)
                .limit(n)
                .parallel()
                .reduce(0L, Long::sum);
```
<img src="https://user-images.githubusercontent.com/107912763/213848683-87effa6d-3d6e-4ea6-8157-6366e537baef.png"  width="400" height="300">


### 2. 스트림 성능 측정
* 무엇이 더 빠를까? - for 루프? 병렬 스트림? 순차 스트림?
* 보통 **for 루프 → 순차 스트림 → 병렬 스트림 순**
  * **문제1** :  반복 결과로 **박싱된 객체**가 만들어짐 → 숫자를 더하려면 **언박싱하는 과정** 필요
  * **문제2** :  반복 작업은 병렬로 수행할 수 있는 **독립 단위로 나누기 어렵다.**
                => 스레드에 할당하는 오버헤드만 증가하게 된다.


* **더 특화된 메서드** - `LongStream.rangeClosed`
  * 기본형 long을 직접 사용하므로 박싱과 언박싱 오버헤드가 사라짐
  * 쉽게 청크로 분할할 수 있는 숫자 범위를 생산


    > **박싱과 언박싱 추가 설명**
    > ```
    > 자바는 int, long, boolean 같은 변수의 기본 자료형(Primitive Type)을 제공한다.
    > 하지만 이런 기본 자료형은 컬렉션(Collection)이나 지네릭(Generic)에서 제한적으로 사용 가능하다.
    > 따라서 자바는 각 기본 자료형에 대응되는 래퍼 클래스(Wrapper Class) 제공하고 있다.
    > ```
    >> **박싱(Boxing)** : 기본 자료형의 데이터를 대응되는 래퍼 클래스로 만드는 동작
    >> ``` java
    >> public class Boxing {
    >> public static void main(String[] args) {
    >>    
    >>    int i = 10;
    >>    Integer wI = new Integer(a); // int Boxing 동작
    >>    
    >>    double d = 3.14;
    >>    Double wD = new Double(d); // double Boxing 동작
    >>    
    >>    boolean b = true;
    >>    Boolean wB = new Boolean(b); // boolean Boxing 동작
    >>    }
    >>} ```


    >> **언박싱(Unboxing)** : 래퍼 클래스에서 기본 자료형으로의 변환
    >> ``` java
    >> public class Unboxing {
    >>  public static void main(String[] args) {
    >>    
    >>    Integer wI = new Integer(10);
    >>    int i = wI.intValue();     // Integer Unboxing 동작
    >>    
    >>    Double wD = new Double(3.14);
    >>    double d = wD.doubleValue(); // Double Unboxing 동작
    >>    
    >>    Boolean wB = new Boolean(true);
    >>    boolean b = wB.booleanValue(); // Boolean Unboxing 동작
    >>      }
    >>}



### 3. 병렬 스트림의 올바른 사용법 & 효과적으로 사용하기
 * 확신이 없으면 **직접 측정**하기
 * 박싱 주의 → 되도록이면 **기본형 특화 스트림** 사용
 * 순차 스트림보다 병렬 스트림에서 성능이 떨어지는 연산이 있으니 주의 → `limit`, `findfirst`같은 순서에 의존하는 연산
 * **소량의 데이터**에서는 병렬 스트림이 도움이 되지 않음
 * 스트림을 구성하는 **자료구조가 적절**한지 확인
 * 최종 연산의 병합 과정 **비용**을 확인


<img src="https://user-images.githubusercontent.com/107912763/213848693-1cfa3bf0-ddce-420f-9dd2-ed1a3ad825c1.png"  width="300" height="200">

---

## 7.2 포크/조인 프레임워크
*10년 전까지만 해도 CPU의 속도는 매년 약 2배씩 빠르게 향상되어왔다. 그러나 이제 한계에 도달하여 **코어의 개수를 늘여서 CPU의 성능을 향상시키는 방향**으로 발전해 가고 있다.
따라서 멀티 코어를 잘 활용할 수 있는 **멀티쓰레드 프로그래밍이 점점 더 중요해지고 있다.***


*따라서 JDK1.7부터 **‘fork & join 프레임워크’** 이 추가되었고, 이것은 하나의 작업을 작은 단위로 나눠서 여러 쓰레드가 동시에 처리하는 것을 쉽게 만들어 준다.*



### 1-1. RecursiveTask 활용
먼저 수행할 작업에 따라 `RecursiveAction`과 `RecursiveTask`, 두 클래스 중에서 하나를 상속 받아 구현해야 한다.
```
RecursiveAction  - 반환 값이 없는 작업을 구현할 때 사용
RecursiveTask    - 반환 값이 있는 작업을 구현할 때 사용
```
두 클래스 모두 `compute()`라는 추상 메서드를 가지고 있는데 우리는 상속을 통해 이 추상 메서드를 구현하기만 하면 된다.


**❗ 순서 - 1️⃣compute()구현 → 2️⃣쓰레드 풀과 수행할 작업을 생성 → 3️⃣invoke()를 호출해서 작업을 시작** 
```
쓰레드를 시작할 때 run이 아니라 start를 호출하는 것처럼 fork&join프레임웍으로 수행할 작업은 compute()가 아닌 invoke()로 시작한다.
단, 순차 코드에서 병렬 계산을 시작할 때만 사용! 이 경우가 아니면 compute나 fork 메서드를 직접 호출할 수 있다.
```
``` 
ForkJoinPool pool = new ForkJoinPool();  // 쓰레드 풀을 생성
SumTask task = new SumTask (from, to);   // 수행할 작업을 생성
Long result = pool.invoke(task);         // invoke()를 호출해서 작업을 시작
```
**✅ 쓰레드 풀 (thread pool)**
> 1. 지정된 수의 쓰레드를 생성해서 **미리 만들어 놓고 반복해서 재사용**
> 2. 쓰레드를 **반복해서 생성하지 않아도 된다**는 장점 &너무 많은 쓰레드가 생성되어 **성능이 저하되는 것을 막아준다는 장점**
> 3. 쓰레드가 수행해야 하는 **작업이 담긴 큐를 제공** → 각 쓰레드는 자신의 작업 큐에 담긴 작업을 **순서대로 처리**
> 
> 
> *기본적으로 쓰레드 풀은 코어의 개수와 동일한 개수의 쓰레드를 생성한다.*



### 1-2. 포크와 조인
```
fork()  -  해당 작업을 쓰레드 풀의 작업 큐에 넣는다. 비동기 메서드
join()   - 해당 작업의 수행이 끝날 때까지 기다렸다가, 수행이 끝나면 그 결과를 반환한다. 동기메서드
```
* **비동기 메서드** : 일반적인 메서드와 달리 메서드 호출만 할 뿐, 그 결과를 기다리지 않는다. 내부적으로는 다른 쓰레드에게 작업을 수행하도록 지시만 하고 결과를 기다리지 않고 돌아오는 것이다.



**합계 계산하기**
**compute() 구현 - 수행할 작업 + 어떻게 작업을 나눌 것인가?**
``` java
public Long compute() {
        long size = to - from + 1;
        if(size <= 5)          // 더할 숫자가 5개 이하
            return sum();     // 숫자의 합을 반환. sum()은 from부터 to까지의 수를 더해서 반환        
 ////////////////////////////////////////////////////////////////////////////////
			  long half = (from + to) /2;   // 범위를 반으로 나눠서 두 개의 작업을 생성
        SumTask leftSum = new SumTask (from, half); 
        SumTask rightSum = new SumTask (half + 1, to);
        
        leftSum.fork();   // 작업(leftSum)을 작업큐에 넣는다
				// 비동기 메서드. 호출 후 결과를 기다리지 않는다.
        return rightSum.compute() + leftSum.join();   // 동기 메서드. 호출 결과를 기다린다.
    }
```


* `return`문에서 `compute`가 재귀 호출될 때, `join`은 **호출되지 않는다.**
* 그러다가 작업을 더 이상 나눌 수 없게 되었을 때, **`compute`의 재귀 호출은 끝나고 `join`의 결과를 기다렸다가 더해서 결과를 반환**한다.
* 재귀 호출된 **`compute`가 모두 종료될 때, 최종 결과**를 얻는다.



### 2. 작업 훔쳐오기


**자신의 작업 큐가 비어있는 쓰레드**는 다른 쓰레드의 작업 큐에서 작업을 **가져와서 수행**한다.
* 이 과정은 **쓰레드 풀에 의해 자동적**으로 이루어진다.
* 이 과정을 통해 한 쓰레드에 작업이 몰리지 않고, 여러 쓰레드가 골고루 작업을 나누어 처리한다.

<img src="https://user-images.githubusercontent.com/107912763/213848701-5e4da293-8dd2-4e34-bbb6-e715a5a51baf.png"  width="400" height="250">


### 3. fork & join 제대로 사용하기


* `join` 메서드를 태스크에 호출하면 태스크가 생산하는 결과가 준비될 때 까지 **호출자를 블록**시킨다. 따라서 **두 서브태스크가 모두 시작된 다음에 `join`을 호출**해야 한다.
* `RecursiveTask` 내에서는 ForkJoinPool의 `invoke` 메서드 대신 `compute`나 `fork` 메서드를 호출한다. 순차 코드에서 병렬 계산을 시작할 때만 `invoke`를 사용한다.
* 두 서브태스크에서 메서드를 호출할 때는 `fork`와 `compute`를 **각각 호출하는 것이 효율적**이다. 그러면 두 서브태스크의 **한 태스크에는 같은 쓰레드를 재사용**할 수 있으므로 풀에서 불필요한 태스크를 할당하는 오버헤드를 피할 수 있다. ⇒ **`compute`는 새 쓰레드를 사용하지 않기 때문**
* **멀티 코어**에서 포크/조인 프레임워크를 사용하는 것이 **순차 처리보다 무조건 빠른 것은 아니다.**



## 7.3 Spilterator 인터페이스


*스트림은 어떻게 분할 로직을 개발하지 않고도 자동으로 스트림을 분할할까? ⇒ **Spliterator 기법***

**Spilterator**
* Spliterator는 ***분할할 수 있는 반복자***라는 의미
* **병렬 작업**에 특화
* 컬렉션은 spliterator라는 메서드를 제공하는 Spliterator 인터페이스를 구현한다.


#### <Spilterator interface>
``` java
  public interface Spliterator<T> {
    boolean tryAdvance(Consumer<? super T> action);
   /*  Spliterator 의 요소를 하나씩 순차적으로 소비하면서 탐색해야 할 요소가 남아있으면 true를
	 반환(iterator 동작과 같다) */
    Spliterator<T> trySplit(); 
   /*Spliterator 의 일부 요소(자신이 반환한 요소)를 분할해서
		 두 번째 Spliterator를 생성하는 메서드 */
    long estimateSize();    //  탐색해야 할 요소 수 정보 제공 메서드
    int characteristics();
}
```


### 1. 분할과정 - 재귀적이다.
* **trySplit**의 결과가 `null`이 될 때까지 이 과정을 반복
* Spilterator에 호출한 모든 trySplit의 결과가 `null`이면 **재귀 분할 과정이 종료**

<img src="https://user-images.githubusercontent.com/107912763/213848714-9e17dd73-1578-4b3e-b314-0f495177d7b3.png"  width="500" height="400">


#### Spilterator의 특성
Spliterator의 characteristics 추상 메서드는 **Spliterator 자체의 특성 집합을 int 타입으로 반환**한다.
<img src="https://user-images.githubusercontent.com/107912763/213848721-3cd30da7-6350-4009-a12c-f7941f2d8e7b.png"  width="350" height="250">
