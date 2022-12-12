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

import java.lang.*;
import java.util.*;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.operators.Order;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchema;
import org.apache.flink.connector.kafka.sink.KafkaRecordSerializationSchemaBuilder;
import org.apache.flink.connector.kafka.sink.KafkaSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.apache.flink.util.Collector;

class RandomStringSource extends RichParallelSourceFunction<String> {
  private volatile boolean cancelled = false;
  private Random random;

  @Override
  public void open(Configuration parameters) throws Exception {
    super.open(parameters);
    random = new Random();
  }

  @Override
  public void run(SourceContext<String> ctx) throws Exception {
    while (!cancelled) {
      String nextLong = "hahaha" + random.nextLong();
      Thread.sleep(2000);
      synchronized (ctx.getCheckpointLock()) {
        ctx.collect(nextLong);
      }
    }
  }

  @Override
  public void cancel() {
    cancelled = true;
  }
}

public class WriteKafka {
  public static void main(String[] args) {
    StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

    KafkaRecordSerializationSchema<String> serializer =
        KafkaRecordSerializationSchema.builder()
            .setValueSerializationSchema(new SimpleStringSchema())
            .setTopic("learn-flink-kakfa-java")
            .build();
    KafkaSink<String> sink = KafkaSink.<String>builder()
                                 .setBootstrapServers("kafka:40800")
                                 .setRecordSerializer(serializer)
                                 .build();
    DataStream<String> data = env.addSource(new RandomStringSource());
    data.sinkTo(sink);
    try {
      env.execute("WriteKafka");
    } catch (Exception e) {
      // TODO: handle exception
    }
  }
}
