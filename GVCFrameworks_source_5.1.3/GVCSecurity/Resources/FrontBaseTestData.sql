--
--
-- Populate the database with test values for use in automated JavaEO testing
--
--
--  This script should be modified to not use hard coded keys...
-- 
--

-- Delete in the correct order...
DELETE FROM "GVC_PRIVILEGES_PRIVILEGE";
DELETE FROM "GVC_GROUPS_PRIVILEGE";
DELETE FROM "GVC_PRIVILEGE";
DELETE FROM "GVC_USERS_GROUP";
DELETE FROM "GVC_GROUP";
DELETE FROM "GVC_USER";

--
-- GVC_GROUP
--
INSERT INTO "GVC_GROUP"("DETAILED_DESCRIPTION","GROUP_ID", "NAME") VALUES ('Description with lame name', 1,  'Group One');
INSERT INTO "GVC_GROUP"("DETAILED_DESCRIPTION","GROUP_ID", "NAME") VALUES ('none', 2,  'Group Two');
INSERT INTO "GVC_GROUP"("DETAILED_DESCRIPTION","GROUP_ID", "NAME") VALUES ('none', 3,  'Group Three');
INSERT INTO "GVC_GROUP"("DETAILED_DESCRIPTION","GROUP_ID", "NAME") VALUES ('none', 4,  'Group Four');

--
-- GVC_USER
--
INSERT INTO "GVC_USER"("IS_ACCOUNT_DISABLED","CAN_CHANGE_PASSWORD", "USER_ID", "MUST_CHANGE_PASSWORD", "PASSWORD", "USER_NAME") VALUES ('N', 'N', 1, 'N', 'password', 'Name One');
INSERT INTO "GVC_USER"("IS_ACCOUNT_DISABLED","CAN_CHANGE_PASSWORD", "USER_ID", "MUST_CHANGE_PASSWORD", "PASSWORD", "USER_NAME") VALUES ('N', 'N', 2, 'N', 'password', 'Name Two');
INSERT INTO "GVC_USER"("IS_ACCOUNT_DISABLED","CAN_CHANGE_PASSWORD", "USER_ID", "MUST_CHANGE_PASSWORD", "PASSWORD", "USER_NAME") VALUES ('N', 'N', 3, 'N', 'password', 'Name Three');
INSERT INTO "GVC_USER"("IS_ACCOUNT_DISABLED","CAN_CHANGE_PASSWORD", "USER_ID", "MUST_CHANGE_PASSWORD", "PASSWORD", "USER_NAME") VALUES ('N', 'N', 4, 'N', 'password', 'Test User');
INSERT INTO "GVC_USER"("IS_ACCOUNT_DISABLED","CAN_CHANGE_PASSWORD", "USER_ID", "MUST_CHANGE_PASSWORD", "PASSWORD", "USER_NAME") VALUES ('Y', 'N', 5, 'N', 'password', 'Disabled User');



--
-- GVC_PRIVILEGE
--
INSERT INTO "GVC_PRIVILEGE"("PRIVILEGE_ID","NAME") VALUES (1, 'Privilege One');
INSERT INTO "GVC_PRIVILEGE"("PRIVILEGE_ID","NAME") VALUES (2, 'Privilege Two');
INSERT INTO "GVC_PRIVILEGE"("PRIVILEGE_ID","NAME") VALUES (3, 'Privilege Three');
INSERT INTO "GVC_PRIVILEGE"("PRIVILEGE_ID","NAME") VALUES (4, 'Privilege Four');
INSERT INTO "GVC_PRIVILEGE"("PRIVILEGE_ID","NAME") VALUES (5, 'Privilege Five');
INSERT INTO "GVC_PRIVILEGE"("PRIVILEGE_ID","NAME") VALUES (6, 'Test Privilege');

--
-- GVC_GROUPS_PRIVILEGE
--
INSERT INTO "GVC_GROUPS_PRIVILEGE"("GROUP_ID", "PRIVILEGE_ID") VALUES (1, 1);
INSERT INTO "GVC_GROUPS_PRIVILEGE"("GROUP_ID", "PRIVILEGE_ID") VALUES (1, 2);
INSERT INTO "GVC_GROUPS_PRIVILEGE"("GROUP_ID", "PRIVILEGE_ID") VALUES (1, 4);
INSERT INTO "GVC_GROUPS_PRIVILEGE"("GROUP_ID", "PRIVILEGE_ID") VALUES (2, 1);
INSERT INTO "GVC_GROUPS_PRIVILEGE"("GROUP_ID", "PRIVILEGE_ID") VALUES (2, 3);

--
-- GVC_USERS_GROUP
--
INSERT INTO "GVC_USERS_GROUP"("GROUP_ID", "USER_ID") VALUES (1, 1);
INSERT INTO "GVC_USERS_GROUP"("GROUP_ID", "USER_ID") VALUES (2, 4);
INSERT INTO "GVC_USERS_GROUP"("GROUP_ID", "USER_ID") VALUES (4, 1);
INSERT INTO "GVC_USERS_GROUP"("GROUP_ID", "USER_ID") VALUES (4, 2);
INSERT INTO "GVC_USERS_GROUP"("GROUP_ID", "USER_ID") VALUES (4, 3);
INSERT INTO "GVC_USERS_GROUP"("GROUP_ID", "USER_ID") VALUES (4, 5);

--
-- GVC_PRIVILEGES_PRIVILEGE
--
INSERT INTO "GVC_PRIVILEGES_PRIVILEGE"("PRIVILEGE_ID", "INCLUDED_PRIVILEGE_ID") VALUES (6, 1);
INSERT INTO "GVC_PRIVILEGES_PRIVILEGE"("PRIVILEGE_ID", "INCLUDED_PRIVILEGE_ID") VALUES (1, 2);
INSERT INTO "GVC_PRIVILEGES_PRIVILEGE"("PRIVILEGE_ID", "INCLUDED_PRIVILEGE_ID") VALUES (1, 4);

COMMIT;

--
-- To be inserted after creating Simple_Test_User table from GVCTestEOModelBase's 
-- TestGVCSecurity.eomodeld 
--
INSERT INTO "Simple_Test_User" ("IS_ACCOUNT_DISABLED", "CAN_CHANGE_PASSWORD", "MUST_CHANGE_PASSWORD", "PASSWORD", "SIMPLE_ATTRIBUTE", "USER_ID", "USER_NAME") VALUES ('N', 'Y', 'N', 'password 2', 'simple', (SELECT UNIQUE FROM GVC_USER), 'Test User 2');