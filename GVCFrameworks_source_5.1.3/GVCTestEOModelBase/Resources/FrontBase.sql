--
-- FrontBase script for GVCTestEOModelBase.eomodeld
--
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE, LOCKING PESSIMISTIC;

CREATE TABLE "ATTR_VALID_TEST_ENTITY" (
	"optional_boolean" CHARACTER(1),
	"optional_decimal_number" REAL,
	"optional_double" REAL,
	"optional_integer" INTEGER,
	"optional_memo" CLOB,
	"optional_string" VARCHAR(50),
	"optional_timestamp" TIMESTAMP WITH TIME ZONE,
	"required_boolean" CHARACTER(1) NOT NULL,
	"required_data" BLOB,
	"required_decimal_number" REAL NOT NULL,
	"required_double" REAL NOT NULL,
	"required_integer" INTEGER NOT NULL,
	"required_memo" CLOB,
	"required_string" VARCHAR(50) NOT NULL,
	"required_timestamp" TIMESTAMP WITH TIME ZONE NOT NULL,
	"short_data" BLOB,
	"short_string" VARCHAR(10) NOT NULL,
	"the_id" INTEGER NOT NULL
);

CREATE TABLE "ENTITY_WITH_COMPOUND_PK" (
	"key_1" INTEGER NOT NULL,
	"key_2" INTEGER NOT NULL
);

CREATE TABLE "ENTITY_WITH_CONSTRAINTS" (
	"quantity" REAL NOT NULL,
	"the_id" INTEGER NOT NULL
);

CREATE TABLE "ENTITY_WITH_DECIMAL_PK" (
	"data_attribute" BLOB,
	"key" INTEGER NOT NULL
);

CREATE TABLE "ENTITY_WITH_JBOOLEAN" (
	"flag" CHARACTER(1) NOT NULL,
	"the_name" VARCHAR(50) NOT NULL,
	"the_id" INTEGER NOT NULL
);

CREATE TABLE "ENTITY_WITH_DATA_PK" (
	"the_name" VARCHAR(50) NOT NULL,
	"the_data_pk" BLOB NOT NULL
);

CREATE TABLE "REL_TO_BARE" (
	"bare_id" INTEGER NOT NULL,
	"relationship_ID" INTEGER NOT NULL
);

CREATE TABLE "REL_TO_DECIMAL" (
	"decimal_id" INTEGER NOT NULL,
	"relationship_ID" INTEGER NOT NULL
);

CREATE TABLE "REL_VALID_TEST_ENTITY" (
	"bare_id" INTEGER NOT NULL,
	"decimal_id" INTEGER NOT NULL,
	"the_id" INTEGER NOT NULL
);

CREATE TABLE "SIMPLE_REL_TEST_ENTITY" (
	"id" INTEGER NOT NULL,
	"the_name" VARCHAR(50) NOT NULL
);

CREATE TABLE "SIMPLE_TEST_ENTITY" (
	"the_name" VARCHAR(50) NOT NULL,
	"the_id" INTEGER NOT NULL
);

CREATE TABLE "TO_MANY_ENTITY" (
	"id" INTEGER NOT NULL,
	"the_name" VARCHAR(50) NOT NULL
);

CREATE TABLE "TO_ONE_ENTITY" (
	"id" INTEGER NOT NULL,
	"the_name" VARCHAR(50) NOT NULL
);

CREATE TABLE "VALID_BARE_ENTITY" (
	"the_name" VARCHAR(50) NOT NULL,
	"the_id" INTEGER NOT NULL
);

SET UNIQUE = 1000000 FOR "ATTR_VALID_TEST_ENTITY";

SET UNIQUE = 1000000 FOR "ENTITY_WITH_CONSTRAINTS";

SET UNIQUE = 1000000 FOR "ENTITY_WITH_DECIMAL_PK";

SET UNIQUE = 1000000 FOR "ENTITY_WITH_JBOOLEAN";

SET UNIQUE = 1000000 FOR "REL_VALID_TEST_ENTITY";

SET UNIQUE = 1000000 FOR "SIMPLE_REL_TEST_ENTITY";

SET UNIQUE = 1000000 FOR "SIMPLE_TEST_ENTITY";

SET UNIQUE = 1000000 FOR "TO_MANY_ENTITY";

SET UNIQUE = 1000000 FOR "TO_ONE_ENTITY";

SET UNIQUE = 1000000 FOR "VALID_BARE_ENTITY";

ALTER TABLE "ATTR_VALID_TEST_ENTITY" ADD PRIMARY KEY ("THE_ID") NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "ENTITY_WITH_COMPOUND_PK" ADD PRIMARY KEY ("KEY_1","KEY_2") NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "ENTITY_WITH_CONSTRAINTS" ADD PRIMARY KEY ("THE_ID") NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "ENTITY_WITH_DECIMAL_PK" ADD PRIMARY KEY ("KEY") NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "ENTITY_WITH_JBOOLEAN" ADD PRIMARY KEY ("THE_ID") NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "ENTITY_WITH_DATA_PK" ADD PRIMARY KEY ("THE_DATA_PK") NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "REL_TO_BARE" ADD PRIMARY KEY ("BARE_ID","RELATIONSHIP_ID") NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "REL_TO_DECIMAL" ADD PRIMARY KEY ("DECIMAL_ID","RELATIONSHIP_ID") NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "REL_VALID_TEST_ENTITY" ADD PRIMARY KEY ("THE_ID") NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "SIMPLE_REL_TEST_ENTITY" ADD PRIMARY KEY ("ID") NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "SIMPLE_TEST_ENTITY" ADD PRIMARY KEY ("THE_ID") NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "TO_MANY_ENTITY" ADD PRIMARY KEY ("ID") NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "TO_ONE_ENTITY" ADD PRIMARY KEY ("ID") NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "VALID_BARE_ENTITY" ADD PRIMARY KEY ("THE_ID") NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "REL_TO_BARE" ADD FOREIGN KEY ("RELATIONSHIP_ID") REFERENCES "REL_VALID_TEST_ENTITY" ("THE_ID") DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE "REL_TO_BARE" ADD FOREIGN KEY ("BARE_ID") REFERENCES "VALID_BARE_ENTITY" ("THE_ID") DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE "REL_TO_DECIMAL" ADD FOREIGN KEY ("RELATIONSHIP_ID") REFERENCES "REL_VALID_TEST_ENTITY" ("THE_ID") DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE "REL_TO_DECIMAL" ADD FOREIGN KEY ("DECIMAL_ID") REFERENCES "ENTITY_WITH_DECIMAL_PK" ("KEY") DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE "REL_VALID_TEST_ENTITY" ADD FOREIGN KEY ("BARE_ID") REFERENCES "VALID_BARE_ENTITY" ("THE_ID") DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE "REL_VALID_TEST_ENTITY" ADD FOREIGN KEY ("DECIMAL_ID") REFERENCES "ENTITY_WITH_DECIMAL_PK" ("KEY") DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE "SIMPLE_REL_TEST_ENTITY" ADD FOREIGN KEY ("ID") REFERENCES "TO_ONE_ENTITY" ("ID") DEFERRABLE INITIALLY DEFERRED;

COMMIT;

SET TRANSACTION ISOLATION LEVEL SERIALIZABLE, LOCKING PESSIMISTIC;

CREATE TABLE "SIMPLE_TEST_USER" (
	"CAN_CHANGE_PASSWORD" CHARACTER(1) NOT NULL,
	"IS_ACCOUNT_DISABLED" CHARACTER(1) NOT NULL,
	"MUST_CHANGE_PASSWORD" CHARACTER(1) NOT NULL,
	"PASSWORD" VARCHAR(50) NOT NULL,
	"SIMPLE_ATTRIBUTE" VARCHAR(50) NOT NULL,
	"USER_ID" INTEGER NOT NULL,
	"USER_NAME" VARCHAR(50) NOT NULL
);

SET UNIQUE = 1000000 FOR "SIMPLE_TEST_USER";

ALTER TABLE "SIMPLE_TEST_USER" ADD PRIMARY KEY ("USER_ID") NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE entity_with_constraints ADD CONSTRAINT quantity_below_10 CHECK (quantity < 10);

COMMIT;


