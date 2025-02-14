// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.editors;

import com.gvcsitemaker.core.pagecomponent.DataAccessMode;
import com.gvcsitemaker.core.support.DataAccessParameters;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSArray;


/**
 * This page is used to control editors that configure a Data Access Section after a table has been 
 * assigned.
 */
public class DataAccessConfigurationEditor extends DataAccessBaseEditor 
{
    public static final String SINGLE_RECORD_MODE = "SingleRecord";
    public static final String LIST_OF_RECORDS_MODE = "ListOfRecords";
    public static final String ADD_FORM_MODE = "AddForm";
    public static final String SEARCH_FORM_MODE = "SearchForm";
    public static final String LAYOUT_TEMPLATES_MODE = "LayoutTemplates";
    public static final String SECTION_SETTINGS_MODE = "SectionSettings";
    
    /** @TypeInfo java.lang.String */
    public static final NSArray previewModeNames= new NSArray(new String[] {SINGLE_RECORD_MODE,
        LIST_OF_RECORDS_MODE, SEARCH_FORM_MODE, ADD_FORM_MODE});  // For preview URL

    /** @TypeInfo java.lang.String */
    public static final NSArray DefaultModeDisplayNames = new NSArray(new String[] {"A Single Record",
        "List of Records", "A \"Search Records\" Form", "An \"Add Record\" Form", "An \"Import Records\" Form"});  // For default mode popup

    /** @TypeInfo java.lang.String */
    public static final NSArray DefaultModeNames = new NSArray(new String[] {DataAccessMode.SINGLE_MODE,
        DataAccessMode.LIST_MODE, DataAccessMode.SEARCH_MODE, DataAccessMode.ADD_MODE, DataAccessMode.IMPORT_MODE});  // For default mode popup

    protected String currentDisplayMode;  // For tab display
    protected boolean isSectionValid;  // For global validation



    /**
     * Designated constructor.
     */
    public DataAccessConfigurationEditor(WOContext context) 
    {
        super(context);
        setCurrentDisplayMode(SECTION_SETTINGS_MODE);  // Default tab to display.
        setIsSectionValid(true);
    }



    /**
     * Overidden to reset validation flags.
     */
    public void appendToResponse(WOResponse aResponse, WOContext aContext)
    {
        super.appendToResponse(aResponse, aContext);

        setIsSectionValid(true);
    }



    /**
     * Takes the user to the main config page.
     *
     * @return the main config page
     */
    public WOComponent backToWebsiteSections()
    {
        return pageWithName("ConfigTabSet");
    }



    /**
     * Overidden to mark the Section as being invalid.  This is where the notifications stop.
     */
    public void notifySectionInvalid()
    {
        setIsSectionValid(false);
    }



    /**
     * Validates the contents of the Section's component(s) and saves the changes to this Section if they are valid.  The page containing this editor is returned.  If the save takes place and shouldPreview is <code>true</code> a preview of the Section is displayed in another browser window.
     *
     * @param shouldPreview <code>true</code> a preview of the Section should be displayed in another browser window.
     * @return the page containing the editor
     */
    protected WOComponent _doUpdateWithPreview(boolean shouldPreview)
    {
        WOComponent resultPage = context().page();

        if ( !  isSectionValid() )
        {
            // Just reshow page?
        }
        else
        {
            resultPage = super._doUpdateWithPreview(shouldPreview);
        }

        return resultPage;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the URL to preview the section being edited or null if showPreview() has not been called in this R-R loop.  This is overridden here to preview the Data Access Mode which matches the tab currently being displayed at the bottom.  If the Layout tab is being displayed the default mode is chosen for prevew as the Layout tab does not correspond to a single Data Access Mode.
     *
     * @return the URL to preview the section being edited or null.
     */
    public String previewURL()
    {
        String previewURL = super.previewURL();

        if (previewURL != null)
        {
            String modeName = dataAccessComponent().defaultMode();

            // Translate between the names for the tabs and the names of modes in DataAccess
            int modeIndex = previewModeNames.indexOfObject(currentDisplayMode());
            if (modeIndex != NSArray.NotFound)
            {
                modeName = (String)DefaultModeNames.objectAtIndex(modeIndex);
            }
            
            previewURL += "&" + DataAccessParameters.MODE_KEY + "=" + modeName;
        }
        
        return previewURL;
    }



    //*********** Mode Manipulation Methods ***********\\


    /**
     * Attempts to switch to Single Record mode and redisplays page.
     *
     * @return this page
     */
    public WOComponent selectSingleRecordMode() 
    {
        changeToMode(SINGLE_RECORD_MODE);
        return context().page();
    }



    /**
     * Attempts to switch to List of Records mode and redisplays page.
     *
     * @return this page
     */
    public WOComponent selectListOfRecordsMode()
    {
        changeToMode(LIST_OF_RECORDS_MODE);
        return context().page();
    }



    /**
     * Attempts to switch to Add Form mode and redisplays page.
     *
     * @return this page
     */
    public WOComponent selectAddFormMode()
    {
        changeToMode(ADD_FORM_MODE);
        return context().page();
    }



    /**
     * Attempts to switch to Search Form mode and redisplays page.
     *
     * @return this page
     */
    public WOComponent selectSearchFormMode()
    {
        changeToMode(SEARCH_FORM_MODE);
        return context().page();
    }



    /**
     * Attempts to switch to Layout Templates mode and redisplays page.
     *
     * @return this page
     */
    public WOComponent selectLayoutTemplatesMode()
    {
        changeToMode(LAYOUT_TEMPLATES_MODE);
        return context().page();
    }



    /**
     * Attempts to switch to Options mode and redisplays page.
     *
     * @return this page
     */
    public WOComponent selectSectionSettingsMode()
    {
        changeToMode(SECTION_SETTINGS_MODE);
        return context().page();
    }

    
    
    /**
     * Attempts to switch to named mode. Modes can only be changed if there are no validation errors in the current mode.
     *
     * @param newModeName the name of the mode to switch to.
     */
    public void changeToMode(String newModeName)
    {
        /** require [valid_mode] isValidModeName(newModeName);**/

        if (isSectionValid())
        {
            setCurrentDisplayMode(newModeName);
        }

        /** ensure [valid_mode] isValidModeName(currentDisplayMode); **/
    }



    /**
     * Returns <code>true</code> if modeName is a valid Data Access Editor mode.
     *
     * @return <code>true</code> if modeName is a valid Data Access Editor mode.
     */
    public boolean isValidModeName(String modeName)
    {
        return (modeName != null) && (modeName.equals(SINGLE_RECORD_MODE) ||
                                     modeName.equals(LIST_OF_RECORDS_MODE) ||
                                     modeName.equals(ADD_FORM_MODE) ||
                                     modeName.equals(SEARCH_FORM_MODE) ||
                                     modeName.equals(LAYOUT_TEMPLATES_MODE) ||
                                     modeName.equals(SECTION_SETTINGS_MODE));
    }



    /**
     * Returns the WOComponent name for the sub-editor to use for currentDisplayMode().
     *
     * @return the WOComponent name for the sub-editor to use for currentDisplayMode().
     */
    public String componentNameForMode()
    {
        return "DataAccess" + currentDisplayMode() + "Editor";
    }


    public boolean isSingleRecordMode()
    {
        return currentDisplayMode().equals(SINGLE_RECORD_MODE);
    }

    public boolean isListOfRecordsMode()
    {
        return currentDisplayMode().equals(LIST_OF_RECORDS_MODE);
    }

    public boolean isAddFormMode()
    {
        return currentDisplayMode().equals(ADD_FORM_MODE);
    }

    public boolean isSearchFormMode()
    {
        return currentDisplayMode().equals(SEARCH_FORM_MODE);
    }

    public boolean isLayoutTemplatesMode()
    {
        return currentDisplayMode().equals(LAYOUT_TEMPLATES_MODE);
    }

    public boolean isSectionSettingsMode()
    {
        return currentDisplayMode().equals(SECTION_SETTINGS_MODE);
    }
    


    //*********** Generic Get / Set methods  ***********\\

    public String currentDisplayMode() {
        return currentDisplayMode;
    }
    public void setCurrentDisplayMode(String newMode) {
        currentDisplayMode = newMode;
    }

    public boolean isSectionValid() {
        return isSectionValid;
    }
    public void setIsSectionValid(boolean validity) {
        isSectionValid = validity;
    }

    /** @TypeInfo java.lang.String */
    public NSArray defaultModeNames() {
        return DefaultModeNames;
    }
    
    /** @TypeInfo java.lang.String */
    public NSArray defaultModeDisplayNames() {
        return DefaultModeDisplayNames;
    }
    

}
