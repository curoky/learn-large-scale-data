/*
 * Copyright (c) 2022-2022 curoky(cccuroky@gmail.com).
 *
 * This file is part of learn-large-scale-data.
 * See https://github.com/curoky/learn-large-scale-data for further info.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.time.Instant;
import java.util.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import scala.Tuple2;

// https://spark.apache.org/docs/latest/streaming-kafka-0-10-integration.html

public class ReadKafka {
  public static void main(String[] args) {
    SparkConf conf = new SparkConf().setAppName("ReadKafka");
    JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.seconds(5));

    Map<String, Object> kafkaParams = new HashMap<>();
    kafkaParams.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    kafkaParams.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    kafkaParams.put(ConsumerConfig.GROUP_ID_CONFIG, "ReadKafka-group-1");
    kafkaParams.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1000);
    kafkaParams.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka:40800");
    kafkaParams.put(
        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest"); // "latest", "earliest", "none"
    kafkaParams.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

    List<String> topics = Arrays.asList("mock_key_empty_value_str");

    JavaInputDStream<ConsumerRecord<String, String>> stream =
        KafkaUtils.createDirectStream(jssc, LocationStrategies.PreferConsistent(),
            ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams));

    // print message info in executor
    stream.foreachRDD(rdd -> {
      rdd.flatMap(content -> {
           System.out.print(
               String.format("topic: %s, ts: %s, nts: %s, key: %s, value: %s\n", content.topic(),
                   content.timestamp(), Instant.now().toString(), content.key(), content.value()));
           return Arrays.asList(content.value()).iterator();
         })
          .collect();
    });

    // to pair
    JavaPairDStream<String, String> pairDStream =
        stream.mapToPair(record -> new Tuple2<>(record.key(), record.value()));

    // ignore key, split value to list
    JavaDStream<String> valueListDStream =
        pairDStream.flatMap(record -> Arrays.asList(record._2().split(" ")).iterator());

    valueListDStream.print();

    System.out.println("streamingContext.start");
    jssc.start();
    try {
      jssc.awaitTermination();
    } catch (Exception e) {
      System.out.println("streamingContext.awaitTermination: " + e.getStackTrace());
    }
  }
}
