// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.core.support;

import net.global_village.foundation.*;
import net.global_village.virtualtables.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.pagecomponent.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.foundation.*;

import er.extensions.foundation.*;


/**
 * This class generates custom URLS for Data Access Sections.
 */
public class SMDataAccessActionURLFactory extends Object
{

    public static final String BAD_REQUEST_URL = "action_not_allowed_by_configuration_or_table_has_no_data";


    /**
     * Returns the URL to use after Adding a new record (or cancelling the addition).  This URL is intended to be used in a redirect.
     *
     * @param dataAccessParameters - parameters to use to create the URL
     * @return the URL to use after Adding a new record (or cancelling the addition).
     */
    public static String postAddUrl(DataAccessParameters dataAccessParameters)
    {
        /** require [valid_da_params] dataAccessParameters != null;    **/

        return baseSectionUrlWithFormValues(dataAccessParameters, dataAccessParameters.postAddParameters());

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the URL to re-display the current record in the current mode.  This URL is intended to be used in a redirect.
     *
     * @param dataAccessParameters - parameters to use to create the URL
     * @return the URL to re-display the current record in the current mode.
     */
    public static String currentModeAndRecordUrl(DataAccessParameters dataAccessParameters)
    {
        /** require [valid_da_params] dataAccessParameters != null;   **/

        return baseSectionUrlWithFormValues(dataAccessParameters, dataAccessParameters.currentModeAndRecordParameters());

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the URL to display the current record in the current mode after removing any user supplier qualifier (search paramters). This URL is intended to be used in a redirect.
     *
     * @param dataAccessParameters parameters to use to create the URL
     * @return the URL to display the current record in the current mode after removing any user supplier qualifier (search paramters)
     */
    public static String currentModeAndRecordWithoutQualifierUrl(DataAccessParameters dataAccessParameters)
    {
        /** require [valid_da_params] dataAccessParameters != null; **/

        return baseSectionUrlWithFormValues(dataAccessParameters, dataAccessParameters.currentModeAndRecordWithoutQualifierParameters());

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the URL to display the current record in Single Record mode.  This URL is intended to be used in a redirect.
     *
     * @param dataAccessParameters parameters to use to create the URL
     * @return the URL to display the current record in Single Record mode
     */
    public static String currentRecordInSingleMode(DataAccessParameters dataAccessParameters)
    {
        /** require [valid_da_params] dataAccessParameters != null;  **/

        return baseSectionUrlWithFormValues(dataAccessParameters, dataAccessParameters.currentRecordInSingleModeParameters());

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the URL to display the current record in List of Records mode.  This URL is intended to be used in a redirect.
     *
     * @param dataAccessParameters parameters to use to create the URL
     * @return the URL to display the current record in List of Records mode
     */
    public static String currentRecordInListMode(DataAccessParameters dataAccessParameters)
    {
        /** require [valid_da_params] dataAccessParameters != null;  **/

        return baseSectionUrlWithFormValues(dataAccessParameters, dataAccessParameters.currentRecordInListModeParameters());

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the URL to use to display the current record in the next mode after performing some action.
     *
     * @param dataAccessParameters parameters to use to create the URL
     * @return the URL to use to display the current record in the next mode
     */
    public static String nextModeAndCurrentRecordUrl(DataAccessParameters dataAccessParameters)
    {
        /** require [valid_da_params] dataAccessParameters != null;  **/

        return baseSectionUrlWithFormValues(dataAccessParameters, dataAccessParameters.nextModeAndCurrentRecordParameters());

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the URL to use when cancelling the addition of records.  The mode to display is determined in priority of:<br/>
     * <ul>
     * <li>List Mode if the default mode is add and there is either no nextMode or nextMode=add</li>
     * <li>nextMode</li>
     * <li>The default mode</li>
     * </ul>
     *
     * @param dataAccessParameters parameters to use to create the URL
     * @return the URL to use to display when cancelling the addition of records
     */
    public static String cancelAddUrl(DataAccessParameters dataAccessParameters)
    {
        /** require [valid_da_params] dataAccessParameters != null;  **/

        return baseSectionUrlWithFormValues(dataAccessParameters, dataAccessParameters.cancelAddParameters());

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the URL to use when cancelling the importation of records.  The mode to display is determined 
     * in priority of:<br/>
     * <ul>
     * <li>List Mode if the default mode is import and there is either no nextMode or nextMode=import</li>
     * <li>nextMode</li>
     * <li>The default mode</li>
     * </ul>
     *
     * @param dataAccessParameters parameters to use to create the URL
     * @return the URL to use to display when cancelling the importation of records
     */
    public static String cancelImportUrl(DataAccessParameters dataAccessParameters)
    {
        /** require [valid_da_params] dataAccessParameters != null;  **/

        return baseSectionUrlWithFormValues(dataAccessParameters, dataAccessParameters.cancelImportParameters());

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the URL to display the indicated record in Single Record mode with the current mode as the next mode.  This URL is intended to be used in a redirect.
     *
     * @param dataAccessParameters parameters to use to create the URL
     * @return the URL to display the current record in Single Record mode
     */
    public static String recordInSingleModeWithNextMode(DataAccessParameters dataAccessParameters)
    {
        /** require  [valid_da_params] dataAccessParameters != null;  **/

        return baseSectionUrlWithFormValues(dataAccessParameters, dataAccessParameters.recordInSingleModeWithNextModeParameters());

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the URL to display the first record in the result set in Single mode.
     *
     * @param dataAccessParameters parameters to use to create the URL
     * @return the URL to display the first record in the result set in Single mode
     */
    public static String firstRecordInSingleModeUrl(DataAccessParameters dataAccessParameters)
    {
        /** require [valid_da_params] dataAccessParameters != null;  **/

        return baseSectionUrlWithFormValues(dataAccessParameters, dataAccessParameters.firstRecordInSingleModeParameters());

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the URL to display the previous record in the result set in Single mode.
     *
     * @param dataAccessParameters parameters to use to create the URL
     * @return the URL to display the previous record in the result set in Single mode
     */
    public static String previousRecordInSingleModeUrl(DataAccessParameters dataAccessParameters)
    {
        /** require [valid_da_params] dataAccessParameters != null; **/
        JassAdditions.pre("SMDataAccessActionURLFactory", "previousRecordInSingleModeUrl [has_rows] ", dataAccessParameters.hasRows());
        JassAdditions.pre("SMDataAccessActionURLFactory", "previousRecordInSingleModeUrl [has_current_row] ", dataAccessParameters.currentRow() != null);
        JassAdditions.pre("SMDataAccessActionURLFactory", "previousRecordInSingleModeUrl [is_not_first_row] ", !dataAccessParameters.isFirstRow());

        return baseSectionUrlWithFormValues(dataAccessParameters, dataAccessParameters.previousRecordInSingleModeParameters());

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the URL to display the next record in the result set in Single mode.
     *
     * @param dataAccessParameters parameters to use to create the URL
     * @return the URL to display the next record in the result set in Single mode
     */
    public static String nextRecordInSingleModeUrl(DataAccessParameters dataAccessParameters)
    {
        /** require [valid_da_params] dataAccessParameters != null; **/
        JassAdditions.pre("SMDataAccessActionURLFactory", "nextRecordInSingleModeUrl [has_rows] ", dataAccessParameters.hasRows());
        JassAdditions.pre("SMDataAccessActionURLFactory", "nextRecordInSingleModeUrl [has_current_row] ", dataAccessParameters.currentRow() != null);
        JassAdditions.pre("SMDataAccessActionURLFactory", "nextRecordInSingleModeUrl [is_not_last_row] ", !dataAccessParameters.isLastRow());

        return baseSectionUrlWithFormValues(dataAccessParameters, dataAccessParameters.nextRecordInSingleModeParameters());

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the URL to display the last record in the result set in Single mode.
     *
     * @param dataAccessParameters parameters to use to create the URL
     * @return the URL to display the last record in the result set in Single mode
     */
    public static String lastRecordInSingleModeUrl(DataAccessParameters dataAccessParameters)
    {
        /** require [valid_da_params] dataAccessParameters != null; **/
        JassAdditions.pre("SMDataAccessActionURLFactory", "lastRecordInSingleModeUrl [has_rows] ", dataAccessParameters.hasRows());

        return baseSectionUrlWithFormValues(dataAccessParameters, dataAccessParameters.lastRecordInSingleModeParameters());

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the URL to display the first group of records in List of Records mode.
     *
     * @param dataAccessParameters parameters to use to create the URL
     * @return the URL to display the first group of records in List of Records mode
     */
    public static String firstGroupInListModeUrl(DataAccessParameters dataAccessParameters)
    {
        /** require [valid_da_params] dataAccessParameters != null; **/

        return baseSectionUrlWithFormValues(dataAccessParameters, dataAccessParameters.firstGroupInListModeParameters());

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the URL to display the previous group of records in List of Records mode.
     *
     * @param dataAccessParameters parameters to use to create the URL
     * @return the URL to display the previous group of records in List of Records mode
     */
    public static String previousGroupInListModeUrl(DataAccessParameters dataAccessParameters)
    {
        /** require [valid_da_params] dataAccessParameters != null; **/
        JassAdditions.pre("SMDataAccessActionURLFactory", "previousGroupInListModeUrl [is_not_first_group] ", !dataAccessParameters.isFirstGroup());

        return baseSectionUrlWithFormValues(dataAccessParameters, dataAccessParameters.previousGroupInListModeParameters());

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the URL to display the next group of records in List of Records mode.
     *
     * @param dataAccessParameters parameters to use to create the URL
     * @return the URL to display the next group of records in List of Records mode
     */
    public static String nextGroupInListModeUrl(DataAccessParameters dataAccessParameters)
    {
        /** require [valid_da_params] dataAccessParameters != null; **/
        JassAdditions.pre("SMDataAccessActionURLFactory", "nextGroupInListModeUrl [is_not_last_group] ", !dataAccessParameters.isLastGroup());

        return baseSectionUrlWithFormValues(dataAccessParameters, dataAccessParameters.nextGroupInListModeParameters());

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the URL to display the last group of records in List of Records mode.
     *
     * @param dataAccessParameters parameters to use to create the URL
     * @return the URL to display the last group of records in List of Records mode
     */
    public static String lastGroupInListModeUrl(DataAccessParameters dataAccessParameters)
    {
        /** require [valid_da_params] dataAccessParameters != null; **/

        return baseSectionUrlWithFormValues(dataAccessParameters, dataAccessParameters.lastGroupInListModeParameters());

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the URL to add a new record and then return to the current mode.
     *
     * @param dataAccessParameters parameters to use to create the URL
     * @return the URL to add a new record and then return to the current mode
     */
    public static String addRecordAndReturnToCurrentModeUrl(DataAccessParameters dataAccessParameters)
    {
        /** require [valid_da_params] dataAccessParameters != null; **/

        return baseSectionUrlWithFormValues(dataAccessParameters, dataAccessParameters.addRecordAndReturnToCurrentModeParameters());

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the URL to import records and then return to the current mode.
     *
     * @param dataAccessParameters parameters to use to create the URL
     * @return the URL to import records and then return to the current mode
     */
    public static String importRecordsAndReturnToCurrentModeUrl(DataAccessParameters dataAccessParameters)
    {
        /** require [valid_da_params] dataAccessParameters != null; **/

        return baseSectionUrlWithFormValues(dataAccessParameters, dataAccessParameters.importRecordsAndReturnToCurrentModeParameters());

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the URL to add a new record based on the current record and then return to the current mode.
     *
     * @param dataAccessParameters parameters to use to create the URL
     * @return the URL to add a new record based on the current record and then return to the current mode
     */
    public static String duplicateRecordAndReturnToCurrentModeUrl(DataAccessParameters dataAccessParameters)
    {
        /** require [valid_da_params] dataAccessParameters != null; **/

        return baseSectionUrlWithFormValues(dataAccessParameters, dataAccessParameters.duplicateRecordAndReturnToCurrentModeParameters());

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the URL to show the Search for Records mode.
     *
     * @param dataAccessParameters - parameters to use to create the URL
     * @return the URL to show the Search for Records mode.
     */
    public static String searchForRecordsUrl(DataAccessParameters dataAccessParameters)
    {
        /** require [valid_da_params] dataAccessParameters != null;   **/

        return baseSectionUrlWithFormValues(dataAccessParameters, dataAccessParameters.searchForRecordsParameters());

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the URL to return the contents of a SiteFile in particular field of a particular row in a Database Table.  e.g.<br>
     * http://www.GVCSiteMaker.com/siteID/section.name/da.data/recordID/column_name/filename
     *
     * @param dataAccessParameters - parameters to use to create the URL
     * @param dataAccessSiteFileColumn - column identifying the name of the field in dataAccessParameters.currentRow()
     * @param siteFile - the SiteFile held in this field, used to determine the filename to place on the URL
     * @return the URL to show the Search for Records mode.
     */
    public static String fileUrl(DataAccessParameters dataAccessParameters, DataAccessSiteFileColumn dataAccessSiteFileColumn, SiteFile siteFile)
    {
        /** require
        [valid_da_params] dataAccessParameters != null;
        [valid_dataAccessSiteFileColumn] dataAccessSiteFileColumn != null;
        [valid_siteFile] siteFile != null;
        **/
        JassAdditions.pre("SMDataAccessActionURLFactory", "fileUrl [has_current_row]", dataAccessParameters.currentRow() != null);

        Section section = dataAccessParameters.dataAccessComponent().section();
        String baseUrl = section.sectionURL();

        String currentRowPK = dataAccessParameters.currentRowPK();
        String columnName = dataAccessSiteFileColumn.normalizedFieldName();
        String fileName = siteFile.uploadedFilename();

        return baseUrl + "/da.data/" + currentRowPK + "/" + columnName + "/" + fileName;

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the URL to access this SiteFile from an database table export.  Only Website Owners and co-Owners have permission via this mechanism. e.g.<br>
     * <code>https://www.GVCSiteMaker.com/siteID/da.data/recordID/column_name/filename</code>
     *
     * @param column - column identifying the name of the field
     * @param siteFile - the SiteFile held in this field, used to determine the filename to place on the URL
     * @return the URL to access this SiteFile from an database table export.  
     */
    public static String exportedFileUrl(VirtualRow row, VirtualColumn column, Website website, SiteFile siteFile)
    {
        /** require
        [valid_row] row != null;
        [valid_column] column != null;
        [valid_website] website != null;
        [valid_siteFile] siteFile != null; **/

        // Secure Website URL
        String baseUrl = SMActionURLFactory.siteURL(website.siteID(), null, true) + "/da.data/";

        String currentRowPK;
        if (SMApplication.smApplication().isUsingIntegerPKs())
        {
            currentRowPK = row.virtualRowID().toString();
        }
        else
        {
            Object downcaster = row.virtualRowID();
            currentRowPK = ERXStringUtilities.byteArrayToHexString(((NSData)downcaster).bytes());
        }
        String columnName = column.normalizedName();
        String fileName = siteFile.uploadedFilename();

        return baseUrl + currentRowPK + "/" + columnName + "/" + fileName;

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the URL to use if a template attempts to generate a URL at an inappropriate time (e.g. to navigate when there are no records or when navigation is not permitted).
     *
     * @param dataAccessParameters - parameters to use to create the URL
     * @return the URL to use if a template attempts to generate a URL at an inappropriate time
     */
    public static String badRequestUrl(DataAccessParameters dataAccessParameters)
    {
        /** require [valid_da_params] dataAccessParameters != null;   **/
        return baseSectionUrl(dataAccessParameters) + "&" + BAD_REQUEST_URL;
    }



    /**
     * Returns the base URL for the Section being displayed in the context of the Website being displayed with the passed string of form values tacked onto the end.
     *
     * @param dataAccessParameters - parameters to use to create the URL
     * @param formValues - the String of form values to append to the URL
     * @return the base URL for the Section being displayed in the context of the Website being displayed with the passed string of form values tacked onto the end.
     */
    protected static String baseSectionUrlWithFormValues(DataAccessParameters dataAccessParameters, String formValues)
    {
        /** require [valid_da_params] dataAccessParameters != null;  [valid_formValues] formValues != null;  **/

        return baseSectionUrl(dataAccessParameters) + "&" + formValues;

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns the base URL for the Section being displayed in the context of the Website being displayed.
     *
     * @param dataAccessParameters - parameters to use to create the URL
     * @return the base URL for the Section being displayed in the context of the Website being displayed.
     */
    protected static String baseSectionUrl(DataAccessParameters dataAccessParameters)
    {
        /** require [valid_da_params] dataAccessParameters != null;  **/

        return dataAccessParameters.pageScaffold().activeDataAccessSectionUrl();

        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns URL of to show either a section of a site, or a section of a site embedded within 
     * another site.  URLs of the first form look like
     * <br>
     * <code>http://domain/WebsiteID/sectionname</code>
     * <br>
     * URLs of the second form look like
     * <br>
     * <code>http://domain/WebsiteID/EmbeddedWebSiteID/sectionname</code>
     * <br>
     * where EmbeddedWebSiteID is the ID of the site which directly contains the section sectioname.
     *
     * @param aWebsite the Website to generate a URL for
     * @param aSection the Section to display
     * @param aRequest the WORequest to get the domain from
     * @return URL to show either a section of a site, or a section of a site embedded within another site
     * @see SMActionURLFactory.sectionURL(Website, Section, WORequest)
     */
    public static String sectionURL(Website aWebsite, Section aSection, WORequest aRequest)
    {
        /** require
        [a_website_not_null] aWebsite != null;
        [a_section_not_null] aSection != null;
        [a_request_not_null] aRequest != null; **/

        String urlForSection;

        if (aWebsite.sections().containsObject(aSection))
        {
            // This is a section directly in this site, not an embedded section.
            urlForSection = aSection.sectionURL(aRequest);
        }
        else
        {
            urlForSection = SMActionURLFactory.siteURL(aWebsite.siteID(), aRequest, aSection.requiresSSLConnection()) + "/"
                    + aSection.website().siteID().toLowerCase() + "/" + aSection.normalName();
        }

        return urlForSection;

        /** ensure [result_not_null] Result != null; **/
    }



}
