SET TRANSACTION ISOLATION LEVEL SERIALIZABLE, LOCKING PESSIMISTIC;

CREATE TABLE "GVC_GROUPS_PRIVILEGE" (
	"GROUP_ID" INTEGER NOT NULL,
	"PRIVILEGE_ID" INTEGER NOT NULL
);

CREATE TABLE "GVC_PRIVILEGE" (
	"NAME" VARCHAR(50) NOT NULL,
	"PRIVILEGE_ID" INTEGER NOT NULL
);

CREATE TABLE "GVC_PRIVILEGES_PRIVILEGE" (
	"INCLUDED_PRIVILEGE_ID" INTEGER NOT NULL,
	"PRIVILEGE_ID" INTEGER NOT NULL
);

CREATE TABLE "GVC_USER" (
	"CAN_CHANGE_PASSWORD" CHARACTER(1) NOT NULL,
	"IS_ACCOUNT_DISABLED" CHARACTER(1) NOT NULL,
	"MUST_CHANGE_PASSWORD" CHARACTER(1) NOT NULL,
	"PASSWORD" VARCHAR(50) NOT NULL,
	"USER_ID" INTEGER NOT NULL,
	"USER_NAME" VARCHAR(50) NOT NULL
);

CREATE TABLE "GVC_GROUP" (
	"DETAILED_DESCRIPTION" VARCHAR(255) NOT NULL,
	"GROUP_ID" INTEGER NOT NULL,
	"NAME" VARCHAR(50) NOT NULL
);

CREATE TABLE "GVC_USERS_GROUP" (
	"GROUP_ID" INTEGER NOT NULL,
	"USER_ID" INTEGER NOT NULL
);

SET UNIQUE = 1000000 FOR "GVC_PRIVILEGE";

SET UNIQUE = 1000000 FOR "GVC_USER";

SET UNIQUE = 1000000 FOR "GVC_GROUP";

ALTER TABLE "GVC_GROUPS_PRIVILEGE" ADD PRIMARY KEY ("GROUP_ID","PRIVILEGE_ID") NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "GVC_PRIVILEGE" ADD PRIMARY KEY ("PRIVILEGE_ID") NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "GVC_PRIVILEGES_PRIVILEGE" ADD PRIMARY KEY ("PRIVILEGE_ID","INCLUDED_PRIVILEGE_ID") NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "GVC_USER" ADD PRIMARY KEY ("USER_ID") NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "GVC_GROUP" ADD PRIMARY KEY ("GROUP_ID") NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "GVC_USERS_GROUP" ADD PRIMARY KEY ("GROUP_ID","USER_ID") NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "GVC_GROUPS_PRIVILEGE" ADD FOREIGN KEY ("PRIVILEGE_ID") REFERENCES "GVC_PRIVILEGE" ("PRIVILEGE_ID") DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE "GVC_GROUPS_PRIVILEGE" ADD FOREIGN KEY ("GROUP_ID") REFERENCES "GVC_GROUP" ("GROUP_ID") DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE "GVC_PRIVILEGES_PRIVILEGE" ADD FOREIGN KEY ("PRIVILEGE_ID") REFERENCES "GVC_PRIVILEGE" ("PRIVILEGE_ID") DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE "GVC_PRIVILEGES_PRIVILEGE" ADD FOREIGN KEY ("INCLUDED_PRIVILEGE_ID") REFERENCES "GVC_PRIVILEGE" ("PRIVILEGE_ID") DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE "GVC_USERS_GROUP" ADD FOREIGN KEY ("GROUP_ID") REFERENCES "GVC_GROUP" ("GROUP_ID") DEFERRABLE INITIALLY DEFERRED;

COMMIT;

