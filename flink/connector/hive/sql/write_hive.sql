CREATE CATALOG myhive WITH (
  'type' = 'hive',
  'default-database' = 'default',
  'hive-conf-dir' = '/opt/hive/conf'
);

USE CATALOG myhive;

CREATE TEMPORARY TABLE Person (name STRING, age BIGINT) WITH ('connector' = 'datagen');

CREATE TEMPORARY TABLE GenPersons WITH (
  'connector' = 'datagen',
  'number-of-rows' = '10'
) LIKE Person (EXCLUDING ALL);

DROP TABLE IF EXISTS hiveSink;
CREATE TABLE hiveSink (name STRING, age BIGINT) WITH (
  'connector' = 'hive'
  -- 'default-database' = 'mydatabase',
  -- 'hive-conf-dir' = '/opt/hive/conf',
);

INSERT INTO
  hiveSink
SELECT
  *
FROM
  GenPersons;
