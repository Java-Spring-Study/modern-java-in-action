import java.util.Currency;

public class Transaction {

  private final Trader trader;

  private final int value;
  private final int year;

  private Currency currency;

  public Transaction(Trader trader, int year, int value) {
    this.trader = trader;
    this.value = value;
    this.year = year;
  }

  public int getValue() {
    return value;
  }

  public Trader getTrader() {
    return trader;
  }

  public int getYear() {
    return year;
  }

  public Currency getCurrency() {
    return currency;
  }
}
