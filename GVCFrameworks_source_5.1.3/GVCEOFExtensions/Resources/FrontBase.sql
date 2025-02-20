--
-- FrontBase script for TestModel1 in GVCEOFExtensions
-- 

SET TRANSACTION ISOLATION LEVEL SERIALIZABLE, LOCKING PESSIMISTIC;

CREATE TABLE "OWNED_OBJECT" (
	"NAME" VARCHAR(50) NOT NULL,
	"OID" INTEGER NOT NULL,
	"ROOT_OID" INTEGER NOT NULL
);

CREATE TABLE "ROOT_OBJECT" (
	"A_VALUE" INTEGER,
	"NAME" VARCHAR(50) NOT NULL,
	"OID" INTEGER NOT NULL
);

CREATE TABLE "ROOT_UNOWNED_JOIN" (
	"ROOT_OID" INTEGER NOT NULL,
	"UNOWNED_OID" INTEGER NOT NULL
);

CREATE TABLE "Test_Model1_Entity" (
	"the_name" VARCHAR(50) NOT NULL,
	"the_id" INTEGER NOT NULL
);

CREATE TABLE "UNOWNED_OBJECT" (
	"NAME" VARCHAR(50) NOT NULL,
	"OID" INTEGER NOT NULL
);

CREATE TABLE "KEYPATH_SIMPLE_TEST_ENTITY" (
	"fk" INTEGER,
	"the_name" VARCHAR(50) NOT NULL,
	"the_value" VARCHAR(50) NOT NULL,
	"the_id" INTEGER NOT NULL
);

SET UNIQUE = 1000000 FOR "OWNED_OBJECT";

SET UNIQUE = 1000000 FOR "ROOT_OBJECT";

SET UNIQUE = 1000000 FOR "Test_Model1_Entity";

SET UNIQUE = 1000000 FOR "UNOWNED_OBJECT";

SET UNIQUE = 1000000 FOR "KEYPATH_SIMPLE_TEST_ENTITY";

ALTER TABLE "OWNED_OBJECT" ADD PRIMARY KEY ("OID") NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "ROOT_OBJECT" ADD PRIMARY KEY ("OID") NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "ROOT_UNOWNED_JOIN" ADD PRIMARY KEY ("UNOWNED_OID","ROOT_OID") NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "TEST_MODEL1_ENTITY" ADD PRIMARY KEY ("THE_ID") NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "UNOWNED_OBJECT" ADD PRIMARY KEY ("OID") NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "OWNED_OBJECT" ADD FOREIGN KEY ("ROOT_OID") REFERENCES "ROOT_OBJECT" ("OID") DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE "ROOT_UNOWNED_JOIN" ADD FOREIGN KEY ("UNOWNED_OID") REFERENCES "UNOWNED_OBJECT" ("OID") DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE "ROOT_UNOWNED_JOIN" ADD FOREIGN KEY ("ROOT_OID") REFERENCES "ROOT_OBJECT" ("OID") DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE "KEYPATH_SIMPLE_TEST_ENTITY" ADD PRIMARY KEY ("THE_ID") NOT DEFERRABLE INITIALLY IMMEDIATE;

COMMIT;

