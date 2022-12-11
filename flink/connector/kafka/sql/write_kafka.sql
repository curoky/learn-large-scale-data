CREATE TABLE Person (name STRING, age BIGINT) WITH ('connector' = 'datagen');

CREATE TEMPORARY TABLE GenPersons WITH (
  'connector' = 'datagen',
  'number-of-rows' = '10'
) LIKE Person (EXCLUDING ALL);

CREATE TABLE kafkaSink (name STRING, age BIGINT) WITH (
  'connector' = 'kafka',
  'topic' = 'learn-flink-kafka-sql-write',
  'properties.bootstrap.servers' = 'kafka:40800',
  'value.format' = 'json',
  'value.json.ignore-parse-errors' = 'false',
  'value.json.fail-on-missing-field' = 'true'
);

INSERT INTO
  kafkaSink
SELECT
  *
FROM
  GenPersons;
