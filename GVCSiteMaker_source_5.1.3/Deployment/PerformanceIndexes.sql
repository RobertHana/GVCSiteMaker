-- 
-- Performance Indexes
--
CREATE INDEX SM_DA_WEBSITE_GRP_NOTIFICATION     ON SM_DA_WEBSITE_GRP_NOTIFICATION (COMPONENT_ID);
CREATE INDEX SM_FILE_PASSWORD_SITE_FILE_FKEY    ON SM_FILE_PASSWORD (SITE_FILE_PASSWORD_FKEY);
CREATE INDEX SM_GROUP_TYPE                      ON SM_GROUP (TYPE);
CREATE INDEX SM_GROUP_FILE_FILE_PKEY            ON SM_GROUP_FILE (FILE_PKEY);
CREATE INDEX SM_GROUP_PARENT_WEBSITE            ON SM_GROUP (PARENT_WEBSITE);
CREATE INDEX SM_GROUP_SECTION_SECTION_PKEY      ON SM_GROUP_SECTION (SECTION_PKEY);
CREATE INDEX SM_ORG_UNIT_OID_PARENT_ORG_UNIT    ON SM_ORG_UNIT (OID_PARENT_ORG_UNIT);
CREATE INDEX SM_PAGE_COMP_PARENT_ID             ON SM_PAGE_COMP (PARENT_ID);
CREATE INDEX SM_PAGE_COMP_FILE_ID               ON SM_PAGE_COMP (RELATED_FILE_ID);
CREATE INDEX SM_PAGE_COMP_FIELD_ID              ON SM_PAGE_COMP (FIELD_ID);
CREATE INDEX SM_PAGE_COMP_EMBED_SECTION_FKEY    ON SM_PAGE_COMP (EMBED_SECTION_FKEY);
CREATE INDEX SM_PAGE_COMP_EMBED_WEBSITE_FKEY    ON SM_PAGE_COMP (EMBED_WEBSITE_FKEY);
CREATE INDEX SM_PAGE_COMP_TEMPLATE_OID          ON SM_PAGE_COMP (TEMPLATE_OID);
CREATE INDEX SM_PAGE_COMP_COMPONENT_TYPE        ON SM_PAGE_COMP (COMPONENT_TYPE);
CREATE INDEX SM_SECTION_WEBSITE_FKEY            ON SM_SECTION (WEBSITE_FKEY);
CREATE INDEX SM_SECTION_COMPONENT_ID            ON SM_SECTION (COMPONENT_ID);
CREATE INDEX SM_SECTION_STYLE_OID_OWN_UNIT      ON SM_SECTION_STYLE (OID_OWN_UNIT);
CREATE INDEX SM_SECTION_VERSION_OID_COMPONENT   ON SM_SECTION_VERSION (OID_COMPONENT);
CREATE INDEX SM_SITE_FILE_FOLDER_PARENT_FOLDER  ON SM_SITE_FILE_FOLDER (OID_PARENT_FOLDER);
CREATE INDEX SM_SITE_FILE_FOLDER_WEBSITE_FKEY   ON SM_SITE_FILE_FOLDER (WEBSITE_FKEY);
CREATE INDEX SM_SITE_FILE_OID_SITE_FILE_FOLDER  ON SM_SITE_FILE (OID_SITE_FILE_FOLDER);
CREATE INDEX SM_SITE_FILE_WEBSITE_FKEY          ON SM_SITE_FILE (WEBSITE_FKEY);
CREATE INDEX SM_STYLE_OID_OWN_UNIT              ON SM_STYLE (OID_OWN_UNIT);
CREATE INDEX SM_USER_GROUP_USER_PKEY            ON SM_USER_GROUP (USER_PKEY);
CREATE INDEX SM_USER_ID                         ON SM_USER (USER_ID);
CREATE INDEX SM_USER_ORG_UNIT_FOR_STYLES        ON SM_USER (ORG_UNIT_FOR_STYLES);
CREATE INDEX SM_USER_ORG_UNIT_TO_ADMIN          ON SM_USER (ORG_UNIT_TO_ADMIN);
CREATE INDEX SM_WEBSITE_OID_PARENT_ORG_UNIT     ON SM_WEBSITE (OID_PARENT_ORG_UNIT);
CREATE INDEX SM_WEBSITE_OID_SECTION_STYLE       ON SM_WEBSITE (OID_SECTION_STYLE);
CREATE INDEX SM_WEBSITE_USER_FKEY               ON SM_WEBSITE (USER_FKEY);
CREATE INDEX SM_WEBSITE_SITE_ID                 ON SM_WEBSITE (SITE_ID);
CREATE INDEX CONFIG_GROUP                       ON SM_WEBSITE (GROUP_PKEY);
CREATE INDEX SM_WEBSITE_TABLE_WEBSITE_FKEY      ON SM_WEBSITE_TABLE (WEBSITE_FKEY);


--
-- Case Insensitve Collation for Website SiteID searches
--
CREATE COLLATION CaseInsensitive FOR INFORMATION_SCHEMA.SQL_TEXT FROM EXTERNAL('CaseInsensitive.coll1');
COMMIT;
ALTER COLUMN "sm_website"."site_id" TO COLLATE CASEINSENSITIVE;
COMMIT;
CREATE INDEX SM_WEBSITE_SITE_ID                 ON SM_WEBSITE (SITE_ID);
COMMIT;


CREATE INDEX VIRTUAL_ROW_VIRTUAL_TABLE_ID       ON "VIRTUAL_ROW" (VIRTUAL_TABLE_ID, VIRTUAL_ROW_ID);
CREATE INDEX VIRTUAL_COLUMN_LOOKUP_COLUMN_ID    ON "VIRTUAL_COLUMN" (LOOKUP_COLUMN_ID);
CREATE INDEX VIRTUAL_COLUMN_TABLE_ID            ON "VIRTUAL_COLUMN" (TABLE_ID, COLUMN_ID);
CREATE INDEX VIRTUAL_FIELD_COLUMN_ID            ON "VIRTUAL_FIELD" (VIRTUAL_COLUMN_ID, VIRTUAL_FIELD_ID);
CREATE INDEX VIRTUAL_FIELD_VIRTUAL_ROW_ID       ON "VIRTUAL_FIELD" (VIRTUAL_ROW_ID, VIRTUAL_FIELD_ID);
CREATE INDEX VIRTUAL_FIELD_LOOKUP_VALUE_ID      ON "VIRTUAL_FIELD" ("LOOKUP_VALUE_ID");

