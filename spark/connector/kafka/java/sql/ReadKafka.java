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

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

// https://spark.apache.org/docs/latest/structured-streaming-kafka-integration.html

public class ReadKafka {
  public static void main(String[] args) {
    SparkSession spark = SparkSession.builder().appName("ReadKafka").getOrCreate();

    Dataset<Row> df = spark.readStream()
                          .format("kafka")
                          .option("kafka.bootstrap.servers", "kafka:40800")
                          .option("subscribe", "hello")
                          // .option("includeHeaders", "true")
                          .load();
    try {
      df.selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
          .writeStream()
          .format("console")
          .outputMode("append")
          .start()
          .awaitTermination();
    } catch (Exception e) {
      System.out.println("ReadKafkaV2: " + e.getStackTrace());
    }
  }
}
