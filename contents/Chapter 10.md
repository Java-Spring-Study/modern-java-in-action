# Chapter10. 람다를 이용한 도메인 전용 언어(DSL)

## **1. 도메인 전용 언어(domain-specific languages, DSL)**

- 특정 비즈니스 도메인의 문제를 해결하려고 만들어진 특수 프로그래밍 언어
- 특정 비즈니스 도메인을 인터페이스로 만든 API
- 의도를 명확하게 전달하고, 가독성이 좋은 코드를 작성할 수 있도록 DSL을 잘 설계하여야 한다.

- **장점**
    - **간결함** : 비즈니스 로직을 간편하게 캡슐화하여 반복을 피하고 코드를 간결하게 만든다.
    - **가독성** : 비도메인 전문가도 코드를 쉽게 이해할 수 있다.
    - **유지보수** : 잘 설계된 DSL로 구현한 코드는 유지보수가 쉽다.
    - **높은 수준의 추상화** : 도메인의 문제와 직접적으로 관련되지 않은 세부 사항을 숨긴다.
    - **집중** : 코드에 집중할 수 있어 생산성이 좋아진다.
    - **관심사 분리** : 애플리케이션 인프라구조 관련 문제와는 독립적으로, 비즈니스 관련된 코드에서 집중하기 용이하다.
- **단점**
    - **DSL 설계의 어려움** : 간결하게 제한적인 언어에 도메인 지식을 담기 어렵다.
    - **개발 비용** : 코드에 DSL을 추가하는 작업은 초기 프로젝트에 많은 비용과 시간이 소모되고, 유지보수와 변경은 프로젝트에 부담을 준다.
    - **추가 우회 계층** : 추가적인 계층으로 도메인 모델을 감싸며 이때 계층을 최대한 작게 만들어 성능 문제를 회피한다.
    - **새로 배워야 하는 언어** : DSL을 프로젝트에 추가하면서 배워야 하는 언어가 더 늘어난다. 여러 비즈니스 도메인을 다루는 개별 DSL을 사용하는 상황에서 이들이 유기적으로 동작하도록 합치는 것이 쉽지 않다.
    - **호스팅 언어 한계** : 장황하고 엄격한 문법을 가진 언어로는 문법 제약을 받아 읽기 어려워지므로 사용자 친화적 DSL을 만들기가 어렵다.

---

- DSL의 카테고리는 순수 자바 코드와 같은 기존 호스팅 언어로 구현하는 **내부 DSL**과 호스팅 언어와는 독립적으로 자체의 문법을 가지는 **외부 DSL**로 나누어진다.
  
<br>

### **내부 DSL**

- 자바와 같은 호스팅 언어를 기반으로 구현된다.
- 유연성이 떨어지는 자바 문법 때문에 읽기 쉽고 간단한 DSL를 만드는 데 한계가 있었지만, 람다 표현식으로 어느정도 해결되었다.
- 람다 표현식과 메서드 참조로 익명 내부 클래스로 DSL을 구현하는 것보다 신호대비 잡음을 줄일 수 있다.

```java
List<String> numbers = Arrays.asList("one", "two", "three");

// 익명 내부 클래스: 굵은 글씨가 코드의 잡음
numbers.forEach(new Consumer<String>() {

  @Override
  public void accept(String s) {
    System.out.println(s);
  }

});

// 람다 표현식
numbers.forEach(s -> System.out.println(s));

// 메서드 참조
numbers.forEach(System.out::println);
```

- 장점
    - 외부 DSL에 비해 새로운 패턴과 기술을 배우기 위한 노력이 줄어든다.
    - 다른 언어의 컴파일러나 외부 DSL 도구를 사용할 필요 없이 자바 코드와 DSL을 함께 컴파일 할 수 있다.
    - 기존 자바 IDE의 자동 완성, 리팩터링 기능을 그대로 사용할 수 있다.
    - 추가 DSL을 쉽게 기존 코드로 합칠 수 있다.
  
<br>

### **다중 DSL**

- 스칼라, 루비, 코틀린, 실론 등 자바가 아니라도 JVM에서 실행된다.
- 제약을 줄이고, 간편한 문법을 지향하도록 설계되었다.
- 스칼라 내장 DSL에서는 문법적 잡음 없이 이해하기 쉬운 코드를 작성할 수 있는데, 자바로는 비슷한 결과를 얻기 어렵다.

```scala
// 주어진 횟수만큼 반복 실행하는 유틸리티 함수 구현
def times(i: Int, f: => Unit): Unit = {
    f
    if ( i > 1 ) times(i - 1, f)
}
times(3, println("Hello World"))
 
// times 함수를 *[커링]하거나 두 그룹으로 인수를 놓을 수 있다
// *[커링]은 f(a, b, c)처럼 단일 호출로 처리하는 함수를 f(a)(b)(c)와 같이
// 각각의 인수가 호출 가능한 프로세스로 호출된 후 병합되도록 변환하는 것
def times(i: Int)(f: => Unit): Unit = { f
    if (i > 1 times(i - 1)(f)
}
 
// 여러 번 실행할 명령을 중괄호 안에 넣어 같은 결과를 얻을 수 있다
times(3) {
    println("Hello World")
}
 
// 함수가 반복할 인수를 받는 한 함수를 가지면서 Int를 익명 클래스로 암묵적 변환하도록 정의
implicit def intToTimes(i: Int) = new {
    def times(f: => Unit): Unit = {
        def times(i: Int, f: => Unit): Unit = {
            f
            if( i > 1 ) times(i - 1, f)
        }
        times(i, f)
    }
}
 
// 3은 자동으로 컴파일러에 의해 클래스 인스턴스로 변환되며, i 필드에 저장된다
3 times {
    println("Hello World")
}
```

- 단점
    - 새로운 프로그래밍 언어를 배워야 한다.
    - 여러 컴파일러로 소스를 빌드하도록 빌드 과정을 개선해야 한다.
    - JVM에서 실행되는 거의 모든 언어가 자바와 100% 호환을 주장하고 있지만 완벽하지 않을 때가 많으므로, 성능이 손실될 때도 있다.

<br>

### **외부 DSL**

- 호스팅 언어와는 독립적으로 자체의 문법과 구문을 가지는 새 언어를 설계해야 함
- 새 언어를 파싱하고, 파서 결과 분석하고, 외부 DSL을 실행할 코드 작성하는 큰 작업
- 필요한 특성을 완벽하게 제공하는 언어를 설계할 수 있는 무한한 유연성
- 기존 애플리케이션과 명확하게 분리되지만, DSL과 호스트 언어 사이에 인공계층이 생김


---

<br>

## **2. 최신 자바 API의 작은 DSL**

- 내부 DSL에서 살펴봤듯이 익명 내부 클래스를 구현하면 불필요한 코드가 추가되어야 했는데,<br>
람다와 메서드 참조를 이용해 코드의 가독성, 재사용성, 결합성을 높일 수 있다.

<br>

### **컬렉션을 조작하는 DSL, 스트림 API**

- Stream 인터페이스는 컬렉션의 항목을 필터, 정렬, 변환, 그룹화, 조작하는 DSL로 간주할 수 있다.
- 함수형으로 로그 파일의 에러행을 읽는 예제

```java
List<String> errors = Files.lines(Paths.get(fileName)) //파일 열어서 문자열 스트림 만듦
                           .filter(line -> line.startsWith("ERROR"))
                           .limit(40)
                           .collect(toList());
```

- 스트림 API의 플루언트 형식은 잘 설계된 DSL의 또 다른 특징이다.
- **플루언트 스타일**
    - 스트림 API의 특성인 메서드 체인을 자바의 루프의 복잡함 제어와 비교해 유창함을 의미한다.
    - 소프트웨어 공학에서 **플루언트 인터페이스**는 메소드 체이닝에 상당 부분 기반한 객체 지향
     API 설계 메소드이며, 소스 코드의 가독성을 산문과 유사하게 만드는 것이 목적이다.
    
<br>

### **데이터를 수집하는 DSL, Collectors**

- 스트림과 마찬가지로 데이터 수집을 수행하는 DSL로 간주할 수 있다.
- Collectors API로 Collectors를 중첩하여 다중 수준 Collector를 만들 수 있다.

```java
Collector<? super Car, ?, Map<Brand, Map<Color, List<Car>>>> carGroupingCollector =
	groupingBy(Car::getBrand, groupingBy(Car::getColor));
```

- 셋 이상의 컴포넌트를 조합할 때는 플루언트 형식이 중첩 형식에 비해 가독성이 좋다.
  
- groupingBy 팩터리 메서드에 작업을 위임하는 GroupingBuilder를 만들어 더 쉽게 해결한다.

```java
import static java.util.stream.Collectors.groupingBy;
public class GroupingBuilder<T, D, K> {
    private final Collector<? super T, ?, Map<K, D>> collector;
    
    private GroupingBuilder(Collector<? super T, ?, Map<K, D>> collector) {
        this.collector = collector;
    }
    
    public Collector<? super T, ?, Map<K, D>> get() {
        return collector;
    }
    
    public <J> GroupingBuilder<T, Map<K, D>, J>
	        after(Function<? super T, ? extends J> classifier) {
        return new GroupingBuilder<>(groupingBy(classifier, collector));
    }

    public static <T, D, K> GroupingBuilder<T, List<T>, K>
	        groupOn(Function<? super T, ? extends K> classifier) {
        return new GroupingBuilder<>(groupingBy(classifier));
    }
}

// 중첩된 그룹화 수준에 반대로 그룹화 함수를 구현하므로 유틸리티 사용 코드가 직관적이지 않다
Collector<? super Car, ?, Map<Brand, Map<Color, List<Car>>>> 
    carGroupingCollector = groupOn(Car::getColor).after(Car::getBrand).get();
```

---

<br>

## **3. 자바로 DSL을 만드는 패턴과 기법**

- DSL은 특정 도메인 모델에 적용하기 위해 가독성 높은 API를 제공한다고 했다.<br>
예제 도메인 모델을 정의하여 DSL을 만드는 패턴을 살펴본다.
---
- 도메인 모델 정의
    
    ```java
    // 주어진 시장에 주식 가겨을 모델링하는 순수 자바 빈즈
    public class Stock {
        private String symbol;
        private String market;
        
        // getter, setter 생략
    }
    
    // 주어진 가격에서 주어진 양의 주식을 사거나 파는 거래
    public class Trade {
        public enum Type {BUY, SELL}
        private Type type;
        
        private Stock stock;
        private int quantity;
        private double price;
        
        // getter, setter 생략
    }
    
    // 고객이 요청한 한 개 이상의 거래의 주문
    public class Order {
        private String customer;
        private List<Trade> trades = new ArrayList<>();
        
        public void addTrade(Trade trade) {
            trades.add(trade);
        }
        
        public double getValue() {
            return trades.stream().mapToDouble(Trade::getValue).sum();
        }
        
        // getter, setter 생략
    }
    ```
    
- 도메인 객체의 API를 직접 이용해 주식 거래 주문을 만든다.
    
    ```java
    Order order = new Order();
    order.setCustomer("BigBank");
    
    Trade trade1 = new Trade();
    trade1.setType(Trade.Type.BUY);
    
    Stock stock1 = new Stock();
    stock1.setSymbol("IBM");
    stock1.setMarket("NYSE");
    
    trade1.setStock(stock1);
    trade1.setPrice(125.00);
    trade1.setQuantity(80);
    order.addTrade(trade1);
    
    Trade trade2 = new Trade();
    trade2.setType(Trade.Type.BUY);
    
    Stock stock2 = new Stock();
    stock2.setSymbol("GOOGLE");
    stock2.setMarket("NASDAQ");
    
    trade2.setStock(stock2);
    trade2.setPrice(375.00);
    trade2.setQuantity(50);
    order.addTrade(trade2);
    ```
    
    ⇒ 코드가 장황하고 직관적이지 않아 이해하기 어려우므로 DSL이 필요하다.
    
---
<br>

- **1) 메서드 체인**
    
    ```java
    Order order = forCustomer("BigBank")
             .buy(80)
             .stock("IBM")
             .on("NYSE")
             .at(125.00)
             .sell(50)
             .stock("GOOGLE")
             .on("NASDAQ")
             .at(375.00)
             .end();
    ```
    
    ⇒ 한 개의 메서드 호출로도 거래 주문을 정의할 수 있다.
    이 결과를 위해 플루언트 API로 도메인 객체를 만드는 빌더를 구현해야 한다.
    
    - 메서드 체인 DSL을 제공하는 주문 빌더
        
        ```java
        public class MethodChainingOrderBuilder {
        
        		// 빌더로 감싼 주문
            public final Order order = new Order();
            
            private MethodChainingOrderBuilder(String customer) {
                order.setCustomer(customer);
            }
            
        		// 고객 주문을 만드는 정적 팩토리 메서드
            public static MethodChainingOrderBuilder forCustomer(String customer) {
                return new MethodChainingOrderBuilder(customer);
            }
            
        		// 주식을 사는 TradeBuilder 만들기
            public TradeBuilder buy(int quantity) {
                return new TradeBuilder(this, Trade.Type.BUY, quantity);
            }
            
        		// 주식을 파는 TradeBuilder 만들기
            public TradeBuilder sell(int quantity) {
                return new TradeBuilder(this, Trade.Type.SELL, quantity);
            }
        
            // 유연하게 추가 주문을 만들도록 주문 빌더 자체를 반환
            public MethodChainingOrderBuilder addTrade(Trade trade) {
        				// 주문에 주식추가
                order.addTrade(trade);
                return this;
            }
        
            // 주문 만들기 종료하고 반환
            public Order end() {
                return order; 
            }
        }
        ```
        
    - 거래 빌더
        
        ```java
        public class TradeBuilder {
            private final MethodChainingBuilder builder;
            public final Trade trade = new Trade();
            
            private TradeBuilder(MethodChainingBuilder builder, Trade.Type type, int quantity) {
                this.builder = builder;
                trade.setType(type);
                trade.setQuantity(quantity);
            }
            
        		// Stock 클래스의 인스턴스를 만든다
            public StockBuilder stock(String symbol) {
                return new StockBuilder(builder, trade, symbol);
            }
        }
        ```
        
    - 주식 빌더
        
        ```java
        public class StockBuilder {
            private final MethodChainingOrderBuilder builder;
            private final Trade trade;
            private final Stock stock = new Stock();
            
            private StockBuilder(MethodChainingOrderBuilder builder, Trade trade, String symbol) {
                this.builder = builder;
                this.trade = trade;
                stock.setSymbol(symbol);
            }
            
        		// 주식의 시장을 지정하고, 거래에 주식을 추가하고, 최종 빌더를 반환하는 메서드
            public TradeBuilderWithStock on(String market) {
                stock.setMarket(market);
                trade.setStock(stock);
                return new TradeBuilderWithStock(builder, trade);
            }
        }
        ```
        
    - 거래 주식의 단위 가격 설정 후 원래 주문 빌더를 반환
        
        ```java
        public class TradeBuilderWithStock {
            private final MethodChainingOrderBuilder builder;
            private final Trade trade;
            
            public TradeBuilderWithStock(MethodChainingOrderBuilder builder, Trade trade) {
                this.builder = builder;
                this.trade = trade;
            }
            
            public MethodChainingOrderBuilder at(double price) {
                trade.setPrice(price);
                return builder.addTrade(trade);
            }
        }
        ```
        
    
    - `MethodChainingOrderBuilder` 가 끝날 때까지 다른 거래를 플루언트 방식으로 추가할 수 있다.
    - 여러 빌드 클래스를 만듦으로써 사용자가 미리 지정된 절차에 따라 플루언트 API의 메서드를 호출하도록 강제한다.
    
    - 장점
        - 주문에 사용한 파라미터가 빌더 내부로 국한된다.
        - 정적 메서드 사용을 최소화하고 메서드 이름이 인수의 이름을 대신하도록 하여 DSL 가독성을 개선한다.
        - 플루언트 DSL에는 분법적 잡음이 최소화된다.
    - 단점
        - 빌더를 구현해야 한다.
        - 상위수준의 빌더를 하위수준의 빌더와 연결할 많은 접착 코드가 필요하다.
        - 도메인 객체 중첩구조와 일치하게 들여쓰기를 강제하는 방법이 없다.
  
---

<br>
    
- **2) 중첩된 함수 이용**
    
    다른 함수 안에 함수를 이용해 도메인 모델을 만든다.
    
    ```java
    Order order = order("BigBank",
                        buy(80, stock("IBM", on("NYSE")), at(125.00)),
                        sell(50, stock("GOOGLE", on("NASDAQ")), at(375.00))
                        );
    ```
    
    - 다음 코드의 주문 빌더는 이런 DSL 형식으로 사용자에게 API를 제공할 수 있음을 보여준다.<br>
    (모든 정적 메서드는 임포트했다고 가정한다.)
        
        ```java
        public class NestedFunctionOrderBuilder {
            public static Order order(String customer, Trade ... trades) {
        				// 해당 고객의 주문 만들기
                Order order = new Order();
                order.setCustomer(customer);
        				// 주문에 모든 거래 추가
                Stream.of(trades).forEach(order::addTrade); 
                return order;
            }
        
            // 주식 매수 거래 만들기
            public static Trade buy(int quantity, Stock stock, Double price) {
                return builderTrade(quantity, stock, price, Trade.Type.BUY);
            }
        
            // 주식 매도 거래 만들기
            public static Trade sell(int quantity, Stock stock, Double price) {
                return builderTrade(quantity, stock, price, Trade.Type.SELL);
            }
            
            private static Trade buildTrade(int quantity, Stock stock, double price, Trade.Type buy) {
                Trade trade = new Trade();
                trade.setQuantity(quantity);
                trade.setType(buy);
                trade.setStock(stock);
                trade.setPrice(price);
                return trade;
            }
        
            // 거래된 주식의 단가를 정의하는 더미 메서드
            public static double at(double price) {
                return price;
            }
        
            // 거래된 주식 만들기
            public static Stock stock(String symbol, String market) {
                Stock stock = new Stock();
                stock.setSymbol(symbol);
                stock.setMarket(market);
                return stock;
            }
        
            // 주식이 거래된 시장을 정의하는 더미 메서드
            public static String on(String market) {
                return market;
            }
        }
        ```
        
    
    - 장점
        - 함수의 중첩 방식이 도메인 객체 계층 구조에 그대로 반영된다는 것 (주문→거래→주식)
    - 단점
        - 많은 괄호를 사용해야 한다.
        - 인수 목록을 정적 메서드에 넘겨줘야 한다.
        - 인수의 의미가 이름이 아니라 위치에 의해 정의된다.
            
            (인수의 역할을 확실하게 만드는 여러 더미 메서드( `at()`, `on()` )로 완화할 수 있다.)

---

<br>
            
- **3) 람다 표현식을 이용한 함수 시퀀싱**
    
    람다 표현식으로 정의한 함수 시퀀스를 사용한다.
    
    ```java
    Order order = order(o -> {
        o.forCustomer("BigBank");
        o.buy(t -> {
            t.quantity(80);
            t.price(125.00);
            t.stock(s -> {
                s.symbol("IBM");
                s.market("NYSE");
            });
        });
        o.sell(t -> {
            t.quantity(50);
            t.price(375.00);
            t.stock(s -> {
                s.symbol("GOOGLE");
                s.market("NASDAQ");
            });
        });
    });
    ```
    
    람다 표현식으로 도메인 모델을 만들어내는 여러 빌더를 구현해야 하는데, 이 빌더들은 메서드 체인 패턴으로 만들려는 객체의 중간 상태를 유지한다.
    
    메서드 체인 패턴에는 주문을 만드는 최상위 수준의 빌더를 가졌지만, 이번엔 `Consumer` 객체를 빌더가 인수로 받음으로 DSL 사용자가 람다 표현식으로 인수를 구현할 수 있게 한다.
    
    - 함수 시퀀싱 DSL을 제공하는 주문 빌더
        
        ```java
        public class LambdaOrderBuilder {
        
        		// 빌더로 주문을 감싼다.
            private Order order = new Order();
          
            public static Order order(Consumer<LambdaOrderBuilder> consumer) {
                LambdaOrderBuilder builder = new LambdaOrderBuilder();
        				// 주문 빌더로 전달된 람다 표현식 실행
                consumer.accept(builder);
        		    // OrderBuilder의 Consumer를 실행해 만들어진 주문을 반환한다.
                return builder.order;
            }
        
            // 주문을 요청한 고객 설정
            public void forCustomer(String customer) {
                order.setCustomer(customer);
            }
        
            // 주식 매수 주문을 만들도록 TradeBuilder 소비
            public void buy(Consumer<TradeBuilder> consumer) {
                trade(consumer, Trade.TYPE.BUY);
            }
        
            // 주식 매도 주문을 만들도록 TradeBuilder 소비
            public void sell(Consumer<TradeBuilder> consumer) {
                trade(consumer, Trade.TYPE.SELL);
            }
            
            private void trade(Consumer<TradeBuilder> consumer, Trade.Type type) {
                TradeBuilder builder = new TradeBuilder();
                builder.trade.setType(type);
        				// TradeBuilder로 전달할 람다 표현식 실행
                consumer.accept(builder);
        				// TradeBuilder의 Consumer를 실행해 만든 거래를 주문에 추가
                order.addTrade(builder.trade);
            }
        }
        ```
        
        ⇒ 주문 빌더의 `buy()`, `sell()` 메서드는 두 개의 `Consumer<TradeBuilder>` 람다 표현식을 받는다. 이 람다표현식을 실행하는 주식 매수, 매도 거래가 만들어진다.
        
    - 거래 빌더
        
        ```java
        // TradeBuilder는 세 번째 빌더의 Consutmer 즉 거래된 주식을 받는다.
        public class TradeBuilder {
            private Trade trade = new Trade();
            
            public void quantity(int quantity) {
                trade.setQuantity(quantity);
            }
            
            public void price(double price) {
                trade.setPrice(price);
            }
            
            public void stock(Consumer<StockBuilder> consumer) {
                StockBuilder builder = new StockBuilder();
                consumer.accept(builder);
                trade.setStock(builder.stock);
            }
        }
        ```
        
        ```java
        public class StockBuilder {
            private Stock stock = new Stock();
            
            public void symbol(String symbol) {
                stock.setSymbol(symbol);
            }
            
            public void market(String market) {
                stock.setMarket(market);
            }
        }
        ```
        
    
    - 장점
        - 메서드 체인 패턴처럼 플루언트 방식으로 정의할 수 있다.
        - 중첩 함수 형식처럼 다양한 람다 표현식의 중첩 수준과 비슷하게 도메인 객체의 계층 구조를 유지한다.
    - 단점
        - 많은 설정 코드가 필요하며, 람다 표현식 문법에 의한 잡음의 영향을 받는다.
    

⇒ 두 개 이상의 형식을 한 DSL에 조합할 수도 있기 때문에 자신이 만들려는 도메인 모델에 어떤 DSL 패턴이 맞는지는 실험을 통해 알아본다!

---

<br>

## **4. 실생활의 자바 8 DSL**

### **jOOQ**

- SQL을 구현하는 내부적 DSL로, 자바에 내장된 형식 안전 언어다.

```java
// select * from BOOK where BOOK.PUBLISHED_IN = 2006 ORDER BY BOOK.TITLE
// 위 쿼리를 다음처럼 구현할 수 있다.
create.selectFrom(BOOK)
      .where(BOOK.PUBLISHED_IN.eq(2016))
      .orderBy(BOOK.TITLE)
```

### **큐컴버**

- 다른 동적 주도 개발(Behavior-driven deveopment, BDD) 프레임워크와 마찬가지로, 명령문을 실행할 수 있는 테스트 케이스로 변환한다.
- 개발자가 비즈니스 시나리오를 평문 영어로 구현할 수 있도록 도와주는 BDD 도구이다.

```java
// 큐컴버 스크립팅 언어로 간단한 비즈니스 시나리오를 정의(외부 DSL 활용)
Feature: Buy stock
    Senario: Buy 10 IBM stocks
				// 전제 조건 정의(Given)
        Given the price of a "IBM" stock is 125$
				// 시험하려는 도메인 객체의 실질 호출(When)
        When I buy 10 "IBM"
				// 테스트 케이스의 결과를 확인하는 assertion(Then)
        Then the order value should be 1250$
```

- 큐컴버의 DSL은 간단하지만, 외부적 DSL과 내부적 DSL이 어떻게 효과적으로 합쳐질 수 있으며, 람다와 함께 가독성 있는 함축된 코드를 구현할 수 있는지를 잘 보여준다.

### **스프링 통합**

- 의존성 주입에 기반한 스프링 프로그래밍 모델을 확장한다.
- 복잡한 엔터프라이즈 통합 솔루션을 구현하는 단순한 모델을 제공하고,
비동기, 메시지 주도 아키텍처를 쉽게 적용할 수 있게 돕는 것이 핵심 목표이다.
- 동사로 구현한 여러 엔드포인트를 한 개 이상의 메시지 흐름으로 조합해 통합 과정이 구성된다.
- 스프링 통합 DSL에서 가장 널리 사용하는 패턴은 **메서드 체인**으로 전달되는 메시지 흐름을 만들고 데이터를 변환하는 기능에 적합하다.
- 최상위 수준의 객체를 만들거나 내부의 복잡한 메서드 인수에는 함수 시퀀싱과 람다 표현식을 사용한다.