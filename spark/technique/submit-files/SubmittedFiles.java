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

import org.apache.commons.io.FileUtils;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.SparkSession;

public class SparkSubmittedFiles {
  public static void main(String[] args) {
    if (args.length < 1) {
      System.out.println("Usage: CheckSparkSubmittedFiles <file>");
      System.exit(1);
    }
    System.out.println("args[0] = " + args[0]);

    // show current path
    System.out.println("current path = " + System.getProperty("user.dir"));

    // show all files in current path
    System.out.println("files in current path = ");
    for (String file : new java.io.File(System.getProperty("user.dir")).list()) {
      System.out.println("--> " + file);
    }

    // check args path exist
    if (!new java.io.File(args[0]).exists()) {
      System.out.println("file " + args[0] + " not exist");
      System.exit(1);
    }

    // print file content in args path
    System.out.println("file " + args[0] + " content = ");
    try {
      System.out.println(FileUtils.readFileToString(new java.io.File(args[0]), "UTF-8"));
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }

    SparkSession spark = SparkSession.builder().appName("SparkSubmittedFiles").getOrCreate();

    // https://stackoverflow.com/questions/27299923/how-to-load-local-file-in-sc-textfile-instead-of-hdfs
    Dataset<String> data =
        spark.read().textFile("file:///" + System.getProperty("user.dir") + "/" + args[0]);
    data.show();

    spark.close();
  }
}
