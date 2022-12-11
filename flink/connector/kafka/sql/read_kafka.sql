CREATE TABLE kafkaSource (name STRING, age BIGINT) WITH (
  'connector' = 'kafka',
  'topic' = 'learn-flink-kafka-sql-write',
  'scan.startup.mode' = 'latest-offset',
  'properties.group.id' = 'learn-flink-kafka-sql',
  'properties.bootstrap.servers' = 'kafka:40800',
  -- 'key.format' = 'csv',
  -- 'format' = 'json',
  'value.format' = 'json',
  'value.json.ignore-parse-errors' = 'false',
  'value.json.fail-on-missing-field' = 'true'
);

CREATE TABLE printSink (name STRING, age BIGINT) WITH (
  'connector' = 'print',
  'print-identifier' = '',
  'standard-error' = 'false'
);

INSERT INTO
  printSink
SELECT
  *
FROM
  kafkaSource;
