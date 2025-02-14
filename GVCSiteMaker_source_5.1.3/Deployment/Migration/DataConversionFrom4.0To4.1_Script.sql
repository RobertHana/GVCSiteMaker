------------- Start of Conversion Script -------------
--
-- This document contains *** FrontBase Specific*** SQL statements 
-- intended to be run from a command line tool
--
-- *** NOTE ***  Make a backup before executing this SQL!  
--

SET TRANSACTION ISOLATION LEVEL SERIALIZABLE, LOCKING PESSIMISTIC;

--
-- Update for to support secure, unauthenticated access to public sections
--

alter table "SM_SECTION" add column "REQUIRE_HTTPS_ACCESS" character(1);

--
-- Update for to support section versioning and workflow
--

alter table "SM_SECTION" add column "IS_VERSIONING" character(1);

CREATE TABLE "SM_SECTION_CONTRIBUTOR_GROUP" (
	"GROUP_PKEY" NUMERIC NOT NULL,
	"SECTION_PKEY" NUMERIC NOT NULL
);

CREATE TABLE "SM_SECTION_EDITOR_GROUP" (
	"GROUP_PKEY" NUMERIC NOT NULL,
	"SECTION_PKEY" NUMERIC NOT NULL
);

CREATE TABLE "SM_SECTION_VERSION" (
	"CREATED_DATE" TIMESTAMP WITH TIME ZONE NOT NULL,
	"DETAILS" VARCHAR(255),
	"IS_LOCKED" CHARACTER(1) NOT NULL,
	"MODIFIED_DATE" TIMESTAMP WITH TIME ZONE,
	"NAME" VARCHAR(50) NOT NULL,
	"OID_COMPONENT" NUMERIC NOT NULL,
	"OID_CREATED_BY" NUMERIC NOT NULL,
	"OID_MODIFIED_BY" NUMERIC,
	"OID_SECTION" NUMERIC NOT NULL,
	"VERSION_NUMBER" NUMERIC NOT NULL,
	"VERSION_PKEY" NUMERIC NOT NULL
);

ALTER TABLE "SM_SECTION_CONTRIBUTOR_GROUP" ADD PRIMARY KEY ("GROUP_PKEY","SECTION_PKEY") NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "SM_SECTION_EDITOR_GROUP" ADD PRIMARY KEY ("GROUP_PKEY","SECTION_PKEY") NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "SM_SECTION_VERSION" ADD PRIMARY KEY ("VERSION_PKEY") NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "SM_SECTION_CONTRIBUTOR_GROUP" ADD FOREIGN KEY ("SECTION_PKEY") REFERENCES "SM_SECTION" ("SECTION_PKEY") DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE "SM_SECTION_EDITOR_GROUP" ADD FOREIGN KEY ("SECTION_PKEY") REFERENCES "SM_SECTION" ("SECTION_PKEY") DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE "SM_SECTION_VERSION" ADD FOREIGN KEY ("OID_SECTION") REFERENCES "SM_SECTION" ("SECTION_PKEY") DEFERRABLE INITIALLY DEFERRED;

--
-- Performance indexes
--
CREATE INDEX SM_SECTION_VERSION_OID_COMPONENT   ON SM_SECTION_VERSION (OID_COMPONENT);


--
-- Add shortName field to SiteFile
--
alter table "SM_SITE_FILE" add column "SHORT_NAME" varchar(300);

--
-- Changes to support Portable Tables queueing
--
CREATE TABLE "TASK_SECTION" (
	"TASK_ID" NUMERIC NOT NULL,
	"SECTION_ID" NUMERIC NOT NULL
);

CREATE TABLE "TASK_TABLE" (
	"TASK_ID" NUMERIC NOT NULL,
	"TABLE_ID" INTEGER NOT NULL
);

alter table "TASK" add column "BINDINGS" clob;

--- need to drop null constraint for IS_NAVIGABLE, what should be the SCRIPT for this, this should be done manually, 
--- please refer to GVCSiteMaker4.1ConversionSteps.txt
--- alter table "TASK" drop constraint "_C0000000134" cascade; 

alter table "TASK" add column "RELATED_TABLE_ID" NUMERIC;

alter table "TASK" add column "RELATED_SITE_FILE_FOLDER_ID" numeric;

alter table "TASK" add column "RELATED_SITE_FILE_ID" numeric;

ALTER TABLE "TASK_SECTION" ADD PRIMARY KEY ("TASK_ID","SECTION_ID") NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "TASK_TABLE" ADD PRIMARY KEY ("TABLE_ID","TASK_ID") NOT DEFERRABLE INITIALLY IMMEDIATE;

ALTER TABLE "TASK" ADD FOREIGN KEY ("RELATED_SITE_FILE_FOLDER_ID") REFERENCES "SM_SITE_FILE_FOLDER" ("OID") DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE "TASK" ADD FOREIGN KEY ("RELATED_SITE_FILE_ID") REFERENCES "SM_SITE_FILE" ("FILE_PKEY") DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE "TASK_SECTION" ADD FOREIGN KEY ("SECTION_ID") REFERENCES "SM_SECTION" ("SECTION_PKEY") DEFERRABLE INITIALLY DEFERRED;

ALTER TABLE "TASK_TABLE" ADD FOREIGN KEY ("TABLE_ID") REFERENCES "SM_VIRTUAL_TABLE" ("TABLE_ID") DEFERRABLE INITIALLY DEFERRED;

update "COLUMN_TYPE" set "ENTITY_NAME_RESTRICTOR" = 'SiteFile' where name = 'SiteFile';

--
-- Add new Package Section Type
--
insert into "SM_SECTION_TYPE" ( "EDITOR_NAME", "KEY", "NAME", "OID", "ORDER", "PAGE_COMP_ENTITY" ) values ( 'PackageSectionEditor', 'PackageSection', 'Temporary Section from Package', 10, 9, 'SectionCopy' );

--
-- Changes to support queueing of Site Copy
--

alter table "TASK" add column "ORG_UNIT_ID" numeric;

ALTER TABLE "TASK" ADD FOREIGN KEY ("ORG_UNIT_ID") REFERENCES "SM_ORG_UNIT" ("OID") DEFERRABLE INITIALLY DEFERRED;

alter table "TASK" add column "REQUEST_ID" numeric;

ALTER TABLE "TASK" ADD FOREIGN KEY ("REQUEST_ID") REFERENCES "SM_WEBSITE_REQUEST" ("OID") DEFERRABLE INITIALLY DEFERRED;


--
-- Changes to support configurable header
--
alter table "SM_WEBSITE" add column "CUSTOM_HEADER_CONTENT" clob;

SET UNIQUE = 1000000 FOR "SM_SECTION_VERSION";

-- 
-- For Section Versioning
--

update SM_SECTION set is_versioning = 'N';

update SM_SECTION set is_versioning = 'Y' where section_pkey in (select s.section_pkey from SM_PAGE_COMP p, SM_SECTION s where p.component_id = s.component_id and p.component_type = 'TextImageLayout');

update SM_SECTION set require_https_access = 'N';

-- 
-- Update virtual_columns for SiteFile fields to be imported using Portable Tables
-- 
update virtual_column set restricting_value = 'VirtualSiteFileColumn' where column_id in (select distinct(virtual_column_id) from virtual_field where restricting_value = 'SiteFile');

commit;