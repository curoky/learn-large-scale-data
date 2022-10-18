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
import subprocess

from pyspark.sql import SparkSession


def deleteTable(spark: SparkSession, tableName: str, tablePath: str):
    subprocess.call(['/opt/hadoop/bin/hadoop', 'fs', '-rm', '-r', '-f', tablePath])


def writeTable(spark: SparkSession, tableName: str, tablePath: str):
    hudi_options = {
        'hoodie.table.name': tableName,
        'hoodie.datasource.write.table.name': tableName,
        'hoodie.datasource.write.recordkey.field': 'uuid',
        'hoodie.datasource.write.partitionpath.field': 'tag',
        'hoodie.datasource.write.precombine.field': 'ts',
        'hoodie.datasource.write.operation': 'upsert',
        'hoodie.upsert.shuffle.parallelism': 2,
        'hoodie.insert.shuffle.parallelism': 2,
    }

    rawData = [{
        'uuid': 1,
        'name': 'Michael',
        'tag': 'good',
        'ts': 20220101
    }, {
        'uuid': 2,
        'name': 'Andy',
        'age': 30,
        'tag': 'good',
        'ts': 20220102
    }, {
        'uuid': 3,
        'name': 'Justin',
        'tag': 'bad',
        'age': 19,
        'ts': 20220103
    }]

    df = spark.read.json(spark.sparkContext.parallelize(rawData, 2))

    df.write.format('hudi'). \
        options(**hudi_options). \
        mode('overwrite'). \
        save(tablePath)


def readTable(spark: SparkSession, tableName: str, tablePath: str):
    tableDF = spark.read.format('hudi').load(tablePath)
    tableDF.createOrReplaceTempView('hudi_tutorial_table')

    spark.sql('describe hudi_tutorial_table').show()
    spark.sql('SELECT name FROM hudi_tutorial_table').show()


def main():
    spark = SparkSession \
                .builder \
                .enableHiveSupport() \
                .config('hive.exec.dynamic.partition', 'true') \
                .config('hive.exec.dynamic.partition.mode', 'nonstrict') \
                .config('spark.serializer', 'org.apache.spark.serializer.KryoSerializer') \
                .config('spark.sql.catalog.spark_catalog', 'org.apache.spark.sql.hudi.catalog.HoodieCatalog') \
                .config('spark.sql.extensions', 'org.apache.spark.sql.hudi.HoodieSparkSessionExtension') \
                .getOrCreate()

    tableName = 'hudi_tutorial_table'
    tablePath = '/tmp/hudi_tutorial_table'

    deleteTable(spark, tableName, tablePath)
    writeTable(spark, tableName, tablePath)
    readTable(spark, tableName, tablePath)


if __name__ == '__main__':
    logging.basicConfig(level=logging.INFO)
    main()
