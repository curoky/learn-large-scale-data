# Copyright (c) 2022-2022 curoky(cccuroky@gmail.com).
#
# This file is part of learn-large-scale-data.
# See https://github.com/curoky/learn-large-scale-data for further info.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import logging

from pyspark.sql import Row, SparkSession


def main():
    spark = SparkSession \
                .builder \
                .enableHiveSupport() \
                .config('hive.exec.dynamic.partition', 'true') \
                .config('hive.exec.dynamic.partition.mode', 'nonstrict') \
                .getOrCreate()

    spark.sql('DROP TABLE IF EXISTS spark_hive_tuturial_py').show()
    spark.sql(
        'CREATE TABLE IF NOT EXISTS spark_hive_tuturial (key INT, value STRING) USING hive').show()

    df = spark.createDataFrame([
        Row(key=0, value='v0'),
        Row(key=1, value='v1'),
    ])
    df.show()
    df.createOrReplaceTempView('kvs')

    spark.sql('INSERT INTO spark_hive_tuturial SELECT key, value FROM kvs').show()

    spark.sql('SELECT * from spark_hive_tuturial LIMIT 10').show()

    spark.stop()


if __name__ == '__main__':
    logging.basicConfig(level=logging.INFO)
    main()
