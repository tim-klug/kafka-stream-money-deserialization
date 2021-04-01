package com.example.demo.processor;

import com.example.demo.pojo.MyDataWithMoney;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Component;

@Component
public class StreamProcessor {

  private static final String IN = "processorIn";
  private static final String OUT = "processorOut";

  @StreamListener
  @SendTo({OUT})
  KStream<String, MyDataWithMoney> process(@Input(IN) KStream<String, MyDataWithMoney> payload) {
    return payload;
  }

  public interface KStreamProcessor {

    @Output(OUT)
    KStream<String, MyDataWithMoney> processOut();

    @Input(IN)
    KStream<String, MyDataWithMoney> processIn();
  }
}
