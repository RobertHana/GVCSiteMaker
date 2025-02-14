------------- Start of Conversion Script -------------
--
-- This document contains *** FrontBase Specific*** SQL statements 
-- intended to be run from a command line tool
--
-- *** NOTE ***  Make a backup before executing this SQL!  
--

SET TRANSACTION ISOLATION LEVEL SERIALIZABLE, LOCKING PESSIMISTIC;

delete from sm_section_version where oid_component in (select component_id from sm_page_comp where component_type = 'MixedMediaTextContentPane' or component_type = 'MixedMediaPaneArrangement');

-- add section versions for each mixed media text component
insert into sm_section_version
  (version_pkey,
  is_locked,
  name,
  details,
  oid_component,
  oid_section,
  version_number,
  oid_created_by,
  oid_modified_by,
  created_date,
  modified_date)
select
  (select UNIQUE from sm_section_version),
  'N',
  'Version 1',
  'Auto-created first version',
  pc.component_id,
  s.section_pkey,
  1,
  w.user_fkey,
  w.user_fkey,
  cast(current_timestamp as timestamp),
  cast(current_timestamp as timestamp)
from
  sm_website w,
  sm_section s,
  sm_page_comp pc
where
  w.website_pkey = s.website_fkey and
  s.component_id = pc.component_id and
  pc.component_type = 'MixedMediaPaneArrangement';

commit;


-- add column to SM_SECTION_VERSION
alter table "SM_SECTION_VERSION" add column "COMPONENT_ORDER" INTEGER;


-- add column to SM_FOOTER
alter table "SM_FOOTER" add column "CUSTOM_FOOTER" CLOB;

commit;


-- add unique constraint to file name, just in case PK change causes dupes
alter table "UMICH"."SM_SITE_FILE" add unique ("FILENAME") deferrable initially deferred;


-- this fk constraint was obviously never tested
alter table "TASK" drop constraint "_C0000000197" restrict;

-- this index causes problems in the new version of FrontBase
-- may not exist, which is an error so you may need to manually delete this line
drop index "SM_GROUP_TYPE";

commit;
