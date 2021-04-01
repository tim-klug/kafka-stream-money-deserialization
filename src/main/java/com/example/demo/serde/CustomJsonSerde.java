package com.example.demo.serde;

import com.example.demo.pojo.MyDataWithMoney;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.zalando.jackson.datatype.money.MoneyModule;

public class CustomJsonSerde extends JsonSerde<MyDataWithMoney> {

  public CustomJsonSerde() {
    super(new ObjectMapper().registerModule(new MoneyModule()));
    serializer().configure(Map.of(JsonSerializer.ADD_TYPE_INFO_HEADERS, false), false);
  }
}
