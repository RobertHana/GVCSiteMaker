// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.admin.commonwidgets;

import java.math.*;

import net.global_village.eofextensions.*;
import net.global_village.foundation.*;

import com.gvcsitemaker.admin.appserver.*;
import com.gvcsitemaker.admin.unit.*;
import com.gvcsitemaker.admin.user.*;
import com.gvcsitemaker.admin.website.*;
import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.components.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


public class Main extends SMAuthComponent implements ValidationMessageStore, SMSecurePage
{
    public OrgUnit selectedUnit;

    public String siteID;
    public String ownerID;
    public OrgUnit searchUnit;
    public String filespaceUsageOperator;
    public String filespaceUsageUnit;
    public Integer filespaceUsage;
    public String lastModifiedDateOperator;
    public NSTimestamp lastModifiedDate;
    public boolean published = false;
    public boolean unpublished = false;

    public final NSNumberFormatter positiveIntegerFormatter;

    private static final String FILESPACE_USAGE_GREATER_THAN = "greater than";
    private static final String FILESPACE_USAGE_LESS_THAN = "less than";
    public final NSArray FilespaceUsageOperators = new NSArray(new String[] {FILESPACE_USAGE_GREATER_THAN, FILESPACE_USAGE_LESS_THAN});
    private static final String FILESPACE_USAGE_UNITS_PERCENT_OF_QUOTA= "% of quota";
    private static final String FILESPACE_USAGE_UNITS_MB= "Mb";
    public final NSArray FilespaceUsageUnits = new NSArray(new String[] {FILESPACE_USAGE_UNITS_PERCENT_OF_QUOTA, FILESPACE_USAGE_UNITS_MB});
    private static final String LAST_MODIFIED_DATE_AFTER = "after";
    private static final String LAST_MODIFIED_DATE_BEFORE = "before";
    public final NSArray LastModifiedDateOperators = new NSArray(new String[] {LAST_MODIFIED_DATE_BEFORE, LAST_MODIFIED_DATE_AFTER});

    public String userID;

    protected NSMutableDictionary validationFailures = new NSMutableDictionary();
    private static final String NO_CRITERIA_ERROR_KEY = "noCriteriaErrorKey";
    private static final String INVALID_USER_ID_ERROR_KEY = "invalidUserIDErrorKey";
    private static final String FILESPACE_USAGE_NOT_POSITIVE_NUMBER_ERROR_KEY = "filespaceUsageNotPositiveNumberErrorKey";
    private static final String INVALID_DATE_FORMAT_ERROR_KEY = "invalidDateFormatErrorKey";

    /** @TypeInfo OrgUnit */
    protected NSArray accessibleOrgUnits;
    

    public Main(WOContext aContext)
    {
        super(aContext);
        positiveIntegerFormatter = new NSNumberFormatter("0");
        positiveIntegerFormatter.setAllowsFloats(false);
        positiveIntegerFormatter.setMinimum(new BigDecimal(0));
    }



    public void appendToResponse( WOResponse aResponse, WOContext aContext)
    {
        // The first time through the user may not be authenticated.  Don't do this until after login.
        if ( ((Session)session()).isUserAuthenticated())
        {
            if ( ! ((Session)session()).currentUser().isUnitAdmin())
            {
                ErrorPage errorPage = (ErrorPage)pageWithName("ErrorPage");
                errorPage.setMessage("Access Refused.");
                errorPage.setReason("This area is for unit and system administrators only.");

                //errorPage.appendToResponse(aResponse, aContext);
                aResponse.setContent(errorPage.generateResponse().content());
            }
            else
            {
                if (accessibleOrgUnits() == null)
                {
                    if (currentAdmin().isSystemAdmin())
                    {
                        accessibleOrgUnits = NSArray.EmptyArray;  // AKA no restrictions
                    }
                    else
                    {
                        accessibleOrgUnits = OrgUnit.allUnitsAtOrBelow(currentAdmin().adminableUnits(), false);
                    }
                }
                super.appendToResponse(aResponse, aContext);
            }
        }
        else
        {
            super.appendToResponse(aResponse, aContext);
        }

        // Clear the error messages
        clearValidationFailures();
    }



    /**
     * Records the validation failure for later retrieval in validationFailureForKey(String aKey).  They key is used as a method of uniquely indentifying the validation failure so that the correct one can be returned.
     *
     * @param validationFailure message to display in the UI for this validation failure.
     * @param key a unique key to associate this failure with
     */
    public void registerValidationFailureForKey(String validationFailure, String key)
    {
        /** require [valid_param] validationFailure != null; [valid_key] key != null; **/
        validationFailures.setObjectForKey(validationFailure, key);
        /** ensure [message_recorded] validationFailures.objectForKey(key) != null; **/
    }



    /**
     * Returns the registered validation error for aKey or null if no validation error was registered.  The key is used as a method of uniquely indentifying the validation failure so that the correct one can be returned.
     *
     * @param key a unique key associated with this failure
     * @return the appropriate registered validation error or null if no validation error was registered.
     */
    public String validationFailureForKey(String aKey)
    {
        /** require  [valid_param] aKey != null; **/
        return (String)validationFailures.objectForKey(aKey);
    }



    /**
     * Returns true if any validation errors have been registered.
     *
     * @return true if any validation errors have been registered.
     */
    public boolean hasValidationFailures()
    {
        return validationFailures.count() > 0;
    }



    /**
     * Removes any validation errors that have been registered.
     */
    public void clearValidationFailures()
    {
        validationFailures.removeAllObjects();
        /** ensure [did_clear] ! hasValidationFailures(); **/
    }



    public String pageTitle()
    {
        return SMApplication.appProperties().propertyForKey("ProductName") + " Administration - Main Page";
    }



    public CreateUnitPage createUnits() {
        return (CreateUnitPage)pageWithName("CreateUnitPage");
    }

    public CreateWebsitePage createWebsites() {
        return (CreateWebsitePage)pageWithName("CreateWebsitePage");
    }

    public CreateUserPage createUsers() {
        return (CreateUserPage)pageWithName("CreateUserPage");
    }



    public ManageUnitPage manageUnit() {
        ManageUnitPage nextPage = (ManageUnitPage)pageWithName("ManageUnitPage");
        nextPage.setUnit(selectedUnit);
        return nextPage;
    }



    /**
     * Performs a search with the params the user has entered.
     * 
     * @return this page if there was a validation error, otherwise returns the ManageWebsitesPage
     */
    public WOComponent findWebsite()
    {
        NSMutableArray databaseQualifiers = new NSMutableArray();
        NSMutableArray inMemoryQualifiers = new NSMutableArray();

        if ( ! StringAdditions.isEmpty(siteID))
        {
            String searchSiteID = "*" + siteID + "*";
            EOQualifier qualifier = new EOKeyValueQualifier("siteID", EOQualifier.QualifierOperatorCaseInsensitiveLike, searchSiteID);
            databaseQualifiers.addObject(qualifier);
        }

        if ( ! StringAdditions.isEmpty(ownerID))
        {
            String searchOwnerID = "*" + ownerID + "*";
            EOQualifier qualifier = new EOKeyValueQualifier("configureGroup.users.userID", EOQualifier.QualifierOperatorCaseInsensitiveLike, searchOwnerID);
            databaseQualifiers.addObject(qualifier);
        }

        if (searchUnit != null)
        {
            NSArray includedUnits = OrgUnit.allUnitsAtOrBelow(new NSArray(searchUnit), false);
            EOQualifier qualifier = NSArrayQualifierAdditions.orQualifier(includedUnits, 
                                                                          "parentOrgUnit", 
                                                                          EOQualifier.QualifierOperatorEqual);
            databaseQualifiers.addObject(qualifier);
        }

        if (filespaceUsage != null)
        {
            NSSelector operator = filespaceUsageOperator.equals(FILESPACE_USAGE_GREATER_THAN) ? EOQualifier.QualifierOperatorGreaterThan : EOQualifier.QualifierOperatorLessThan;
            EOQualifier qualifier;
            if (filespaceUsageUnit.equals(FILESPACE_USAGE_UNITS_MB))
            {
                Integer filespaceUsageBytes = new Integer(filespaceUsage.intValue() * 1024 * 1024);
                qualifier = new EOKeyValueQualifier("fileSizeUsage", operator, filespaceUsageBytes);
                databaseQualifiers.addObject(qualifier);
            }
            else
            {
                qualifier = new EOKeyValueQualifier("percentageOfQuotaUsed", operator, filespaceUsage);
                inMemoryQualifiers.addObject(qualifier);
            }
        }

        if (lastModifiedDate != null)
        {
            NSSelector operator;
            if (lastModifiedDateOperator.equals(LAST_MODIFIED_DATE_AFTER))
            {
                operator = EOQualifier.QualifierOperatorGreaterThan;
                lastModifiedDate = Date.endOfDayInDefaultTimeZone(lastModifiedDate);
            }
            else
            {
                operator = EOQualifier.QualifierOperatorLessThan;
                lastModifiedDate = Date.beginningOfDayInDefaultTimeZone(lastModifiedDate);
            }
            EOQualifier qualifier = new EOKeyValueQualifier("dateLastModified", operator, lastModifiedDate);
            databaseQualifiers.addObject(qualifier);
        }

        // Check for both admin and owner publication flags
        if (published != unpublished)
        {
            NSArray publicationQualifiers = new NSArray(new EOQualifier[] {
                new EOKeyValueQualifier("published", EOQualifier.QualifierOperatorEqual, published ? "Y" : "N"),
                new EOKeyValueQualifier("publicationPermitted", EOQualifier.QualifierOperatorEqual, published ? "Y" : "N")
            });
            
            // To be published both must be set, to be unpublished, either may be unset
            EOQualifier qualifier;
            if (published)
            {
                qualifier = new EOAndQualifier(publicationQualifiers);
            }
            else
            {
                qualifier = new EOOrQualifier(publicationQualifiers);
            }

            databaseQualifiers.addObject(qualifier);
        }

        if ( ! hasValidationFailures())
        {
            // Searching...
            if ((databaseQualifiers.count() > 0) || (inMemoryQualifiers.count() > 0))
            {
                // Restrict results to what Admin is allowed to see.
                EOQualifier qualifier = NSArrayQualifierAdditions.orQualifier(accessibleOrgUnits(), 
                                                                              "parentOrgUnit", 
                                                                              EOQualifier.QualifierOperatorEqual);
                databaseQualifiers.addObject(qualifier);

                EOAndQualifier searchDatabaseQualifier = new EOAndQualifier(databaseQualifiers);
                DebugOut.println(20, "Website searchDatabaseQualifier: " + searchDatabaseQualifier);
                EOFetchSpecification fetchSpec = new EOFetchSpecification("Website", searchDatabaseQualifier, null);
                fetchSpec.setUsesDistinct(true);  // Owner& co-owners search can produce duplicates
                NSArray siteResults = editingContext().objectsWithFetchSpecification(fetchSpec);
    
                EOAndQualifier searchInMemoryQualifier = new EOAndQualifier(inMemoryQualifiers);
                DebugOut.println(20, "Website searchInMemoryQualifier: " + searchInMemoryQualifier);
                siteResults = EOQualifier.filteredArrayWithQualifier(siteResults, searchInMemoryQualifier);

                ManageWebsitesPage nextPage = (ManageWebsitesPage)pageWithName("ManageWebsitesPage");
                nextPage.setWebsitesToManage(siteResults);
                return nextPage;
            }
            else
            {
                registerValidationFailureForKey("Search Failed: No search criteria was entered!", NO_CRITERIA_ERROR_KEY);
            }
        }

        return context().page();
    }



    public WOComponent updateWebsitesForOrgUnit()
    {
        // This action just updates the list of websites in the selected unit (handled in takeValuesFromRequest).  A redisplay is all that is needed.
        return context().page();
    }


    
    public WOComponent manageUser() {
        User user = User.userForUserID(userID, session().defaultEditingContext());

        if (user == null) {
            registerValidationFailureForKey("The User ID you entered could not be found. Please try again.", INVALID_USER_ID_ERROR_KEY);
            return context().page();
        }
        else {
            ManageUserPage nextPage = (ManageUserPage)pageWithName("ManageUserPage");
            nextPage.setUser(user);
            return nextPage;
        }
    }



    /**
     * Send message to all users with Configure Website permission.
     */
    public WOComponent emailConfigureWebsiteMembers()
    {
        EmailConfigureGroupMembers nextPage = (EmailConfigureGroupMembers)pageWithName("EmailConfigureGroupMembers");
        nextPage.setIsTextEntryMode(false);
        return nextPage;
    }



    /**
     * Overridden to handle validation errors caused by invalid data input.
     */
    public void validationFailedWithException(Throwable t, Object value, String keyPath)
    {
        if (keyPath.equals("lastModifiedDate"))
        {
            registerValidationFailureForKey("Search Failed: " + value + " is not recognized as a date!", INVALID_DATE_FORMAT_ERROR_KEY);
        }
        else if (keyPath.equals("filespaceUsage"))
        {
            registerValidationFailureForKey("Search Failed: Filespace usage value is not a positive integer!", FILESPACE_USAGE_NOT_POSITIVE_NUMBER_ERROR_KEY);
        }
        else
        {
             super.validationFailedWithException(t, value, keyPath);
        }
    }



    public boolean displayNoCriteriaErrorMessage() {
        return (validationFailureForKey(NO_CRITERIA_ERROR_KEY) != null);
    }

    public String noCriteriaErrorMessage() {
        return validationFailureForKey(NO_CRITERIA_ERROR_KEY);
    }


    public boolean displayInvalidUserIDErrorMessage() {
        return (validationFailureForKey(INVALID_USER_ID_ERROR_KEY) != null);
    }

    public String invalidUserIDErrorMessage() {
        return validationFailureForKey(INVALID_USER_ID_ERROR_KEY);
    }


    public boolean displayFilespaceUsageNegativeErrorMessage() {
        return (validationFailureForKey(FILESPACE_USAGE_NOT_POSITIVE_NUMBER_ERROR_KEY) != null);
    }

    public String filespaceUsageNegativeErrorMessage() {
        return validationFailureForKey(FILESPACE_USAGE_NOT_POSITIVE_NUMBER_ERROR_KEY);
    }


    public boolean displayDateFormatInvalidErrorMessage() {
        return (validationFailureForKey(INVALID_DATE_FORMAT_ERROR_KEY) != null);
    }

    public String dateFormatInvalidErrorMessage() {
        return validationFailureForKey(INVALID_DATE_FORMAT_ERROR_KEY);
    }

    public EOEditingContext editingContext()
    {
        return session().defaultEditingContext();
    }

    /** @TypeInfo OrgUnit */
    public NSArray accessibleOrgUnits() {
        return accessibleOrgUnits;
    }
    
    public User currentAdmin() {
        return ((SMSession)session()).currentUser();
    }
    
    
    /**
     * Returns the QueuedTasksPage that allows administrators to view all queued tasks.
     * 
     * @return the QueuedTasksPage
     */    
    public WOComponent viewQueuedTasks()
    {
    		WOComponent nextPage = pageWithName("QueuedTasksPage");
   		((QueuedTasksPage) nextPage).setPageToReturnTo(this);
   		
   		return nextPage;
   		
        /** ensure [result_not_null] Result != null; **/   		
    }
    
    /** invariant
    [has_error_messages_dictionary] validationFailures != null; **/


}
