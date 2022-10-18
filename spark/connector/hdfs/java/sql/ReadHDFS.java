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
import org.apache.spark.sql.SparkSession;

public class ReadHDFS {
  public static void main(String[] args) {
    if (args.length < 1) {
      System.out.println("Usage: ReadHDFS <file>");
      System.exit(1);
    }
    System.out.println("args[0] = " + args[0]);

    SparkSession sparkSession = SparkSession.builder().appName("Read HDFS File").getOrCreate();

    Dataset<String> data = sparkSession.read().textFile(args[0]).cache();
    data.show();

    sparkSession.close();
  }
}
