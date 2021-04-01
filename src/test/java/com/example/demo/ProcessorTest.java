package com.example.demo;

import static com.example.demo.ProcessorTest.topicIn;
import static com.example.demo.ProcessorTest.topicOut;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.pojo.MyDataWithMoney;
import com.example.demo.serde.CustomJsonSerde;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.javamoney.moneta.Money;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@EmbeddedKafka(topics = topicOut)
@SpringBootTest
public class ProcessorTest {

  static final String topicIn = "Event-In";
  static final String topicOut = "Event-Out";
  static final String GROUP_NAME = "DemoApp";

  @Autowired
  private ObjectMapper objectMapper;

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Autowired
  private EmbeddedKafkaBroker embeddedKafka;

  @Test
  void shouldSerializeAndReceiveMessage() {
    var payload = new MyDataWithMoney();
    payload.setSomeMoney(Money.of(100, Monetary.getCurrency("EUR")));

    Map<String, Object> senderProps = KafkaTestUtils.producerProps(embeddedKafka);
    senderProps.put("key.serializer", StringSerializer.class);
    senderProps.put("value.serializer", JsonSerializer.class);
    senderProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);
    var pf = new DefaultKafkaProducerFactory<String, MyDataWithMoney>(senderProps);
    pf.setValueSerializer(new CustomJsonSerde().serializer());
    var template = new KafkaTemplate<>(pf, true);
    template.setDefaultTopic(topicIn);
    template.sendDefault("0:0", payload);

    Map<String, Object> consumerProps = KafkaTestUtils.consumerProps(GROUP_NAME, "false", embeddedKafka);
    consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    consumerProps.put("key.deserializer", StringDeserializer.class);
    consumerProps.put("value.deserializer", JsonDeserializer.class);
    var cf = new DefaultKafkaConsumerFactory<>(consumerProps, new StringDeserializer(),
        new CustomJsonSerde().deserializer());

    var consumer = cf.createConsumer();
    embeddedKafka.consumeFromAnEmbeddedTopic(consumer, topicOut);
    consumer.assignment();
    var result = KafkaTestUtils.getRecords(consumer, 10_000);
    consumer.commitSync();

    assertThat(result).isNotNull();
    assertThat(result.count()).isEqualTo(1);
  }
}
