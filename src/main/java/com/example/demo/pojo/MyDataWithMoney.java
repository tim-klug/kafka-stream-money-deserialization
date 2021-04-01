package com.example.demo.pojo;

import org.javamoney.moneta.Money;

public class MyDataWithMoney {
  private Money someMoney;

  public Money getSomeMoney() {
    return someMoney;
  }

  public void setSomeMoney(Money someMoney) {
    this.someMoney = someMoney;
  }
}
