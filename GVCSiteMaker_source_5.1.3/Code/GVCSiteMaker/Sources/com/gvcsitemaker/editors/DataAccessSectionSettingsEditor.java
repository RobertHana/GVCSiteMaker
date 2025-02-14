// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.editors;


import com.gvcsitemaker.core.pagecomponent.DataAccessMode;

import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSArray;


/**
 * This pages configures the overall (non mode specific) DataAccess section setting.
 */
public class DataAccessSectionSettingsEditor extends DataAccessBaseEditor 
{
    protected boolean isUseDefaultSearchInvalid;
    protected boolean isAddModeInvalid;
    protected boolean isImportModeInvalid;
    protected boolean isSearchModeInvalid;
    
    public boolean canBrowseOrSearchRecords;  // To avoid modifying component graph during RR Loop
    

    public DataAccessSectionSettingsEditor(WOContext context) 
    {
        super(context);
    }
    


    /**
     * Overridden to validate configuration.
     */
    public void takeValuesFromRequest(WORequest aRequest, WOContext aContext)
    {
        super.takeValuesFromRequest(aRequest, aContext);
        validateConfiguration();
        canBrowseOrSearchRecords = dataAccessComponent().canBrowseOrSearchRecords();
    }



    /**
     * Overidden to reset validation flags.
     */
    public void appendToResponse(WOResponse aResponse, WOContext aContext)
    {
        canBrowseOrSearchRecords = dataAccessComponent().canBrowseOrSearchRecords();
        super.appendToResponse(aResponse, aContext);

        setIsAddModeInvalid(false);
        setIsSearchModeInvalid(false);
        setIsImportModeInvalid(false);
        setIsUseDefaultSearchInvalid(false);
    }
  
  
  
    /**
     * Performs local validation for attributes changed directly in this component.  Sets validation 
     * flags (isSectionValid, isAddModeInvalid, isSearchModeInvalid, isUseDefaultSearchInvalid and 
     * isRestrictToDefaultResultsInvalid).
     */
    public void validateConfiguration()
    {
        if (dataAccessComponent().defaultMode().equals(DataAccessMode.ADD_MODE) && 
            ! dataAccessComponent().canAddRecords())
        {
            notifySectionInvalid();
            setIsAddModeInvalid(true);
        }

        if (dataAccessComponent().defaultMode().equals(DataAccessMode.SEARCH_MODE) &&
            ! dataAccessComponent().canBrowseOrSearchRecords())
        {
            notifySectionInvalid();
            setIsSearchModeInvalid(true);
        }

        if (dataAccessComponent().defaultMode().equals(DataAccessMode.IMPORT_MODE) &&
                ! dataAccessComponent().canImportRecords())
            {
                notifySectionInvalid();
                setIsImportModeInvalid(true);
            }

        if ((dataAccessComponent().useDefaultSearch()) && 
            (searchMode().orderedCriteria().count() == 0))
        {
            notifySectionInvalid();
            setIsUseDefaultSearchInvalid(true);
        }
    }



    /**
     * Wrapper method for dataAccessComponent().setDefaultMode(String).  Translates between the UI 
     * mode names (defaultModeDisplayNames) and the internal mode names (defaultModeNames).
     *
     * @param newDefaultMode the UI display name of the mode the make the new dataAccessComponent().defaultMode().
     */
    public void setDefaultMode(String newDefaultMode)
    {
        /** require [valid_param] newDefaultMode != null;  
                    [valid_mode] defaultModeDisplayNames().containsObject(newDefaultMode); **/
        dataAccessComponent().setDefaultMode((String) defaultModeNames().objectAtIndex(defaultModeDisplayNames().indexOfObject(newDefaultMode)));
    }



    /**
     * Returns the search mode, for use with the stored search configuration.
     *
     * @return the search mode, for use with the stored search configuration
     */
    public com.gvcsitemaker.core.pagecomponent.DataAccessSearchMode searchMode()
    {
        return (com.gvcsitemaker.core.pagecomponent.DataAccessSearchMode)dataAccessComponent()
            .componentForMode(DataAccessMode.SEARCH_MODE);
        /** ensure [valid_result] Result != null; **/
    }
    


    /**
     * Returns the name of the initial view presently congfigured.
     *
     * Returns the name of the initial view presently congfigured
     */
    public String initialViewSummary()
    {
        return defaultMode();
    }



    /**
     * Wrapper method for dataAccessComponent().defaultMode().  Translates between the UI mode names 
     * (DefaultModeDisplayNames) and the internal mode names (DefaultModeNames).
     *
     * @return the name to display in the UI for the current dataAccessComponent().defaultMode().
     */
    public String defaultMode()
    {
        return (String) defaultModeDisplayNames().objectAtIndex(defaultModeNames()
            .indexOfObject(dataAccessComponent().defaultMode()));
        /** ensure [valid_result] Result != null;  
                   [valid_mode] defaultModeDisplayNames().containsObject(Result); **/
    }
    
    
    //*********** Generic Get / Set methods  ***********\\
    public boolean isUseDefaultSearchInvalid() {
        return isUseDefaultSearchInvalid;
    }
    public void setIsUseDefaultSearchInvalid(boolean newIsUseDefaultSearchInvalid) {
        isUseDefaultSearchInvalid = newIsUseDefaultSearchInvalid;
    }
    
    /** @TypeInfo java.lang.String */
    public NSArray defaultModeNames() {
        return DataAccessConfigurationEditor.DefaultModeNames;
    }
    
    /** @TypeInfo java.lang.String */
    public NSArray defaultModeDisplayNames() {
        return DataAccessConfigurationEditor.DefaultModeDisplayNames;
    }
    
    public boolean isAddModeInvalid() {
        return isAddModeInvalid;
    }
    public void setIsAddModeInvalid(boolean newIsAddModeInvalid) {
        isAddModeInvalid = newIsAddModeInvalid;
    }

    public boolean isSearchModeInvalid() {
        return isSearchModeInvalid;
    }
    public void setIsSearchModeInvalid(boolean newIsSearchModeInvalid) {
        isSearchModeInvalid = newIsSearchModeInvalid;
    }

    public boolean isImportModeInvalid() {
        return isImportModeInvalid;
    }
    public void setIsImportModeInvalid(boolean isInvalid) {
        isImportModeInvalid = isInvalid;
    }

}
