------------- Start of Conversion Script -------------
--
-- This document contains *** FrontBase Specific*** SQL statements 
-- intended to be run from a command line tool
--
-- *** NOTE ***  Make a backup before executing this SQL!  
--

SET TRANSACTION ISOLATION LEVEL SERIALIZABLE, LOCKING PESSIMISTIC;


create procedure drop_not_null_on_column(:tname varchar(256), :cname varchar(256))
begin
   create view check_constraints as table definition_schema.check_constraints;

   declare constr varchar(256);
   set : constr = cast(null as varchar(256));
   set : constr =
   select "constraint_name" from information_schema.table_constraints t0,
			      check_constraints t1,
			      information_schema.constraint_names t2,
			      information_schema.tables t3
	where t3."table_name" = :tname
		and
	      t0.table_pk = t3.table_pk
		and
	      t0.constraint_name_pk = t1.constraint_name_pk
		and
	      t1.check_clause = '"' || :cname || '" IS NOT NULL' collate information_schema.case_insensitive
		and
	      t0.constraint_name_pk = t2.constraint_names_pk;

   drop view check_constraints restrict;

   if :constr is not null then
      execute immediate 'alter table "' || :tname || '" drop constraint "' || :constr || '" restrict;';
   end if;
end;

call drop_not_null_on_column('SM_WEBSITE', 'DATE_LAST_MODIFIED');
call drop_not_null_on_column('SM_WEBSITE', 'DATE_CREATED');
call drop_not_null_on_column('SM_SECTION_VERSION', 'CREATED_DATE');

drop procedure drop_not_null_on_column restrict;

--
-- Update to fix optimistic locking problem with dates in different timezones
--
alter column "SM_WEBSITE"."DATE_LAST_MODIFIED" to timestamp;
alter column "SM_WEBSITE"."DATE_CREATED" to timestamp;

alter column "SM_SECTION_VERSION"."CREATED_DATE" to timestamp;
alter column "SM_SECTION_VERSION"."MODIFIED_DATE" to timestamp;

alter column "VIRTUAL_FIELD"."TIMESTAMP_VALUE" to timestamp;


alter table "SM_GROUP" add column "SECRET" VARCHAR(255);
alter table "SM_GROUP" add column "OID_SECTION_STYLE" NUMERIC;

-- Add MIME type to Section for XML Sections
alter table "SM_SECTION" add column "MIME_TYPE" VARCHAR(50);
update  "SM_SECTION" set "MIME_TYPE" = 'text/html'; 
alter table "SM_SECTION" add constraint SECTION_MIME_TYPE_NOT_NULL check ("MIME_TYPE" is not null);

-- Add column to support auto versioning for Text/Image sections
alter table "SM_SECTION" add column "IS_AUTO_VERSIONING" character(1);
update  "SM_SECTION" set "IS_AUTO_VERSIONING" = 'N'; 
alter table "SM_SECTION" add constraint IS_AUTO_VERSIONING_NOT_NULL check ("IS_AUTO_VERSIONING" is not null);

-- Add expression to VirtualColumn for calculated columns
alter table "VIRTUAL_COLUMN" add column	"EXPRESSION" VARCHAR(2000000);

-- Add Calculated column type
INSERT INTO column_type (the_id, entity_name_restrictor,  name, text_description) values ((select UNIQUE from gvc_lookup),  'Calculated', 'Calculated', 'Calculation');

-- Fix bug in Site File column type
update column_type set entity_name_restrictor = 'SiteFile' where name = 'SiteFile';

-- Add Remote Participants groups
insert into sm_group
  (group_pkey, creation_date, group_name, type, parent_website)
select
  (select unique from sm_group), current_timestamp, 'Remote Participants', 'Remote Participant', website_pkey
from
  sm_website;

commit;
