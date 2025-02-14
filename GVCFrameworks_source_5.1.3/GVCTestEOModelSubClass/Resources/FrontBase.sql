--
-- FrontBase script for GVCTestEOModelSubClass.eomodeld
--
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE, LOCKING PESSIMISTIC;

CREATE TABLE "ATTR_VALID_TEST_SUBCLASS" (
	"optional_boolean" CHARACTER(1) NOT NULL,
	"optional_decimal_number" REAL,
	"optional_double" REAL,
	"optional_integer" INTEGER,
	"optional_memo" CLOB,
	"optional_string" VARCHAR(50),
	"optional_timestamp" TIMESTAMP,
	"required_boolean" CHARACTER(1) NOT NULL,
	"required_data" BLOB NOT NULL,
	"required_decimal_number" REAL NOT NULL,
	"required_double" REAL NOT NULL,
	"required_integer" INTEGER NOT NULL,
	"required_memo" CLOB NOT NULL,
	"required_string" VARCHAR(50) NOT NULL,
	"required_timestamp" TIMESTAMP NOT NULL,
	"short_data" BLOB NOT NULL,
	"short_string" VARCHAR(10) NOT NULL,
	"sub_class_string" VARCHAR(50) NOT NULL,
	"the_id" INTEGER NOT NULL
);

CREATE TABLE "DECIMAL_PK_SUBCLASS" (
	"data_attribute" BLOB NOT NULL,
	"subclass_data" VARCHAR(10) NOT NULL,
	"key" INTEGER NOT NULL
);

SET UNIQUE = 1000000 FOR "ATTR_VALID_TEST_SUBCLASS";

SET UNIQUE = 1000000 FOR "DECIMAL_PK_SUBCLASS";

ALTER TABLE "ATTR_VALID_TEST_SUBCLASS" ADD PRIMARY KEY ("THE_ID") NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "DECIMAL_PK_SUBCLASS" ADD PRIMARY KEY ("KEY") NOT DEFERRABLE INITIALLY IMMEDIATE;

COMMIT;

