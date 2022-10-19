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

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoder;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class ReadHive {
  public static class KV implements Serializable {
    private int key;
    private String value;

    public int getKey() {
      return key;
    }

    public void setKey(int key) {
      this.key = key;
    }

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }

    public static KV newKV(int key, String value) {
      KV kv = new KV();
      kv.key = key;
      kv.value = value;
      return kv;
    }
  }

  public static void main(String[] args) {
    // warehouseLocation points to the default location for managed databases and tables
    String warehouseLocation = new File("spark-warehouse").getAbsolutePath();
    SparkSession spark = SparkSession.builder()
                             .appName("Java Spark Hive Example")
                             .config("spark.sql.warehouse.dir", warehouseLocation)
                             .enableHiveSupport()
                             .getOrCreate();

    spark.sql("DROP TABLE IF EXISTS spark_hive_tuturial").show();

    spark.sql("CREATE TABLE IF NOT EXISTS spark_hive_tuturial (key INT, value STRING) USING hive")
        .show();

    Encoder<KV> kvEncoder = Encoders.bean(KV.class);

    Dataset<KV> kvsDS =
        spark.createDataset(Arrays.asList(KV.newKV(0, "v0"), KV.newKV(1, "v1")), kvEncoder);
    kvsDS.show();
    kvsDS.createOrReplaceTempView("kvs");

    spark.sql("INSERT INTO spark_hive_tuturial SELECT key, value FROM kvs").show();

    Dataset<Row> sqlDF = spark.sql("SELECT * from spark_hive_tuturial LIMIT 10");
    sqlDF.show();

    spark.close();
  }
}
