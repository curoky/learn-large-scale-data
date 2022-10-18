#!/usr/bin/env bash
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

set -xeuo pipefail

/opt/spark/sbin/start-master.sh --host 0.0.0.0 --port 4200
/opt/spark/sbin/start-worker.sh --host 0.0.0.0 --port 4201 spark://0.0.0.0:4200

while true; do sleep 1000; done
