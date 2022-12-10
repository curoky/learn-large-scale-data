#!/usr/bin/env python3
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

from kafka import KafkaConsumer


def main():
    consumer = KafkaConsumer(bootstrap_servers='kafka:40800',
                             auto_offset_reset='earliest',
                             consumer_timeout_ms=1000,
                             group_id='learn-kafka-py-basic')
    consumer.subscribe(['learn-kafka-py'])
    while True:
        message = consumer.poll(1000)
        if message:
            logging.info(message)

    consumer.close()


if __name__ == '__main__':
    logging.basicConfig(level=logging.INFO)
    main()
