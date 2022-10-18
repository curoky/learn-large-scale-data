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
import org.apache.hudi.DataSourceReadOptions;
import org.apache.hudi.DataSourceWriteOptions;
import org.apache.hudi.config.HoodieWriteConfig;
import org.apache.spark.sql.DataFrameWriter;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;

public class ReadHudi {
  static void writeTable(SparkSession spark, String tableName, String tablePath) {
    // https://github.com/apache/hudi/blob/master/hudi-examples/hudi-examples-spark/src/main/java/org/apache/hudi/examples/spark/HoodieSparkBootstrapExample.java

    List<String> records =
        Arrays.asList("{\"uuid\":1,\"name\":\"Michael\",\"tag\":\"good\",\"ts\":20220101}");
    Dataset<String> recordsDS = spark.createDataset(records, Encoders.STRING());
    Dataset<Row> df = spark.read().json(recordsDS);

    DataFrameWriter<Row> writer =
        df.write()
            .format("hudi")
            .option(HoodieWriteConfig.TBL_NAME.key(), tableName)
            .option(DataSourceWriteOptions.OPERATION().key(), "upsert")
            // .option(DataSourceWriteOptions.TABLE_NAME().key(), tableName)
            .option(DataSourceWriteOptions.RECORDKEY_FIELD().key(), "uuid")
            .option(DataSourceWriteOptions.PARTITIONPATH_FIELD().key(), "tag")
            .option(DataSourceWriteOptions.PRECOMBINE_FIELD().key(), "ts")
            .option("hoodie.insert.shuffle.parallelism", "2")
            .option("hoodie.upsert.shuffle.parallelism", "2")
            .mode(SaveMode.Overwrite);
    writer.save(tablePath);
  }

  static void readTable(SparkSession spark, String tableName, String tablePath) {
    Dataset<Row> snapshotQueryDF = spark.read().format("hudi").load(tablePath + "/*");

    snapshotQueryDF.createOrReplaceTempView("hudi_tutorial_table");

    spark.sql("describe hudi_tutorial_table").show();

    spark.sql("SELECT name FROM hudi_tutorial_table").show();
  }

  static void deleteTable(SparkSession spark, String tableName, String tablePath) {}

  public static void main(String[] args) {
    SparkSession spark =
        SparkSession.builder()
            .appName("Read Hudi")
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .enableHiveSupport()
            .getOrCreate();

    String tablePath = "/tmp/hudi_tutorial_table3";
    String tableName = "hudi_tutorial_table";

    deleteTable(spark, tableName, tablePath);
    writeTable(spark, tableName, tablePath);
    readTable(spark, tableName, tablePath);

    spark.close();
  }
}
