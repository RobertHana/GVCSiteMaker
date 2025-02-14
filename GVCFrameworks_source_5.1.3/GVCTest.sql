-- This directory contains file and scripts to create / recreate 
-- the GVCTest database used for unit testing.  Run it like this:
--
-- /Library/FrontBase/bin/sql92 -i GVCTest.sql
--
-- If any of the models in the frameworks are updated, the .sql files 
-- in the project will also need to be updated before this is run

-- 1. Discard any database that is there now
connect to GVCTest user _system;
stop database;
delete database GVCTest;

-- 2. Create the database and user, mark to autostart at boot
create database GVCTest;
connect to GVCTest user _system;
autostart;
add GVCTest;
save;
quit;
create user tester;
set password tester user tester;
disconnect all;

-- 3. Create the schema
connect to GVCTest user tester password tester;
script ./GVCEOFExtensions/FrontBase.sql;
script ./GVCEOFValidation/FrontBase.sql;
script ./GVCGenericObjects/FrontBase.sql;
script ./GVCSecurity/FrontBase.sql;
script ./GVCTestEOModelBase/FrontBase.sql;
script ./GVCTestEOModelSubClass/FrontBase.sql;
script ./GVCVirtualTables/CreateVTTables.sql;

-- 4. Add test data and performance indexes
script ./GVCGenericObjects/FrontBaseTestData.sql;
script ./GVCSecurity/FrontBaseTestData.sql;
script ./GVCVirtualTables/VTInitialLoadFrontbase.sql;
script ./GVCVirtualTables/PerformanceIndexes.sql;

disconnect all;
