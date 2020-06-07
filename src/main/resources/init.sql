-- Login to psql with superuser account (default is postgres) and execute the command: \i path_to_init.sql

CREATE USER candicode with CREATEDB REPLICATION PASSWORD '123456';

ALTER USER candicode WITH superuser;

DROP DATABASE IF EXISTS candicode;

CREATE DATABASE candicode WITH OWNER = candicode ENCODING = 'UTF8' TABLESPACE = pg_default CONNECTION LIMIT = -1;

-- Then, login to psql with user = candicode and execute the command: \i path_to_schema.sql