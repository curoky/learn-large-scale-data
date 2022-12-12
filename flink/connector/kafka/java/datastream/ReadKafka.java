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

import java.util.*;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.operators.Order;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.shaded.jackson2.org.yaml.snakeyaml.tokens.Token;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

public class ReadKafka {
  public static void main(String[] args) throws Exception {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    KafkaSource<String> source = KafkaSource.<String>builder()
                                     .setBootstrapServers("kafka:40800")
                                     .setTopics("mock_key_empty_value_str")
                                     .setGroupId("learn-flink-datastream-java")
                                     .setStartingOffsets(OffsetsInitializer.earliest())
                                     .setValueOnlyDeserializer(new SimpleStringSchema())
                                     .build();
    DataStream<String> text =
        env.fromSource(source, WatermarkStrategy.noWatermarks(), "Kafka Source");

    DataStream<String> counts =
        text.flatMap(new Tokenizer())
            .keyBy(value -> value.f0)
            .sum(1)
            .flatMap((Tuple2<String, Integer> in, Collector<String> out) -> {
              out.collect(in.f0 + ":" + in.f1);
            })
            .returns(Types.STRING);
    counts.print();
    env.execute("ReadKafka");
  }

  public static final class Tokenizer implements FlatMapFunction<String, Tuple2<String, Integer>> {
    @Override
    public void flatMap(String value, Collector<Tuple2<String, Integer>> out) {
      String[] tokens = value.toLowerCase().split("\\W+");
      for (String token : tokens) {
        if (token.length() > 0) {
          out.collect(new Tuple2<>(token, 1));
        }
      }
    }
  }
}
