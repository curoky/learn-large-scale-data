CREATE CATALOG myhive WITH (
  'type' = 'hive',
  'default-database' = 'default',
  'hive-conf-dir' = '/opt/hive/conf'
);

USE CATALOG myhive;

CREATE TEMPORARY TABLE printSink (name STRING, age BIGINT) WITH (
  'connector' = 'print',
  'print-identifier' = '',
  'standard-error' = 'false'
);

INSERT INTO
  printSink
SELECT
  *
FROM
  hiveSink;
