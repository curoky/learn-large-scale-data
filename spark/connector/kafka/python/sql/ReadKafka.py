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

from pyspark.sql import SparkSession
from pyspark.sql.functions import explode, split

# https://spark.apache.org/docs/latest/structured-streaming-kafka-integration.html


def main():
    spark = SparkSession.builder.appName('ReadKafka').getOrCreate()

    df = spark.readStream.format('kafka') \
        .option('auto.offset.reset', 'earliest') \
        .option('max.poll.records', 100) \
        .option('kafka.bootstrap.servers', 'kafka:40800') \
        .option('kafka.group.id', 'ExampleStructedStreamingReadKafka') \
        .option('subscribe', 'mock_key_empty_value_str') \
        .load()
    # .option('kafka.cluster', '') \
    # .option('kafka.group.id', '')  \
    # .option('kafka.topic', 'hello') \

    lines = df.selectExpr('CAST(key AS STRING)', 'CAST(value AS STRING)')
    words = lines.select(explode(split(lines.value, ' ')).alias('word'))

    query = words  \
        .groupBy(words.word)  \
        .count()  \
        .writeStream  \
        .format('console')  \
        .trigger(processingTime='10 seconds')  \
        .outputMode('update')  \
        .option('checkpointLocation', '${hdfs checkpoint location}')  \
        .start()

    query.awaitTermination()


if __name__ == '__main__':
    logging.basicConfig(level=logging.INFO)
    main()
