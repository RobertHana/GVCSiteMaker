// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.editors;
import com.gvcsitemaker.core.Section;
import com.gvcsitemaker.core.interfaces.ValidationMessageStore;
import com.gvcsitemaker.core.support.OrderedComponentList;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSMutableDictionary;



/**
 * Support methods common to sub-editors for a Data Access Section.
 */
public abstract class DataAccessSubEditor extends DataAccessBaseEditor  implements ValidationMessageStore
{
    public com.gvcsitemaker.core.pagecomponent.DataAccessColumn aColumn; // Current item in WORepetition over orderedComponents
    protected com.gvcsitemaker.core.pagecomponent.DataAccessMode dataAccessMode;  // PageComponent for this editor
    protected OrderedComponentList orderedComponents;
    protected NSMutableDictionary validationFailures = new NSMutableDictionary();


    /**
     * Designated constructor.
     */
    public DataAccessSubEditor(WOContext context)
    {
        super(context);
    }



    /**
     * Sets orderedComponents before rendering page to avoid repeated, expensive calls to dataAccessMode().orderedComponents().
     */
    public void appendToResponse(WOResponse aResponse, WOContext aContext)
    {
        super.appendToResponse(aResponse, aContext);
        clearValidationFailures();
        /** ensure [orderedComponents_is_set] orderedComponents != null;  **/
    }



    /**
     * Sets the Section whose list of links is being edited.  Overridden to also set orderedComponents.
     *
     * @param aSection the Section whose list of links should be edited.
     */
    public void setSection(Section aSection)
    {
        /** require [non_null_param] aSection != null; **/
        if (section != aSection)
        {
            super.setSection(aSection);
            setDataAccessMode(dataAccessComponent().componentForMode(editorModeName()));
        }
        /** ensure [section_is_set] section() == aSection; [has_data_access_mode] dataAccessMode() != null;  **/
    }



    /**
     * Returns the String name of the mode from com.gvcsitemaker.core.pagecomponent.DataAccessMode
     *
     * @return the String name of the mode from com.gvcsitemaker.core.pagecomponent.DataAccessMode.
     */
    public abstract String editorModeName();



    /**
     * Returns <code>true</code> if there is more than one column in the layout.  This method is used to determine what UI elements and actions are to be shown (move up move down, etc)
     *
     * @return <code>true</code> if there is more than one link in the layout.
     */
    public boolean hasMultipleColumns()
    {
        return dataAccessMode().toChildren().count() > 1;
    }



    /**
     * Moves this link closer to the start of the list of links and redisplays the page.
     */
    public WOComponent moveUp()
    {
        dataAccessMode().moveEarlier(aColumn);

        return context().page();
        /** ensure [non_null_result] Result != null; **/
    }



    /**
     * Moves this link closer to the end of the list of links and redisplays the page.
     */
    public WOComponent moveDown()
    {
        dataAccessMode().moveLater(aColumn);

        return context().page();
        /** ensure [non_null_result] Result != null; **/
    }



    /**
     * Moves this link to the start of the list of links and redisplays the page.
     */
    public WOComponent moveFirst()
    {
        dataAccessMode().moveFirst(aColumn);

        return context().page();
        /** ensure [non_null_result] Result != null; **/
    }



    /**
     * Moves this link to the end of the list of links and redisplays the page.
     */
    public WOComponent moveLast()
    {
        dataAccessMode().moveLast(aColumn);

        return context().page();
        /** ensure [non_null_result] Result != null; **/
    }



    /**
     * Updates the sort order of the columns being edited.
     *
     * @return the page to show after saving
     */
    public void updateSorting()
    {
        dataAccessMode().setComponentOrderFrom(orderedComponents.ordered());
        _doUpdateWithPreview(false);
        clearValidationFailures();
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns <code>true</code> if the current item in the WORepetition is the first column in the layout.  This method is used to determine what actions are to be shown (move up move down, etc)
     *
     * @return <code>true</code> if the current item in the WORepetition is the first column in the layout.
     */
    public boolean isTopColumn()
    {
        /** require [multiple_columns] hasMultipleColumns();       **/
        //return orderedComponents.objectAtIndex(0).equals(aColumn);
        return true;
    }



    /**
     * Returns <code>true</code> if the current item in the WORepetition is the last column in the layout.  This method is used to determine what actions are to be shown (move up move down, etc)
     *
     * @return <code>true</code> if the current item in the WORepetition is the last column in the layout.
     */
    public boolean isBottomColumn()
    {
        /** require [multiple_columns] hasMultipleColumns();       **/
        //return orderedComponents.lastObject().equals(aColumn);
        return true;
    }



    /**
     * Returns <code>true</code> if the column in the WORepetition is a calculated column
     *
     * @return <code>true</code> if the column in the WORepetition is a calculated column
     */
    public boolean isCalculatedColumn()
    {
        return aColumn instanceof com.gvcsitemaker.core.pagecomponent.DataAccessCalculatedColumn;
    }


    //*********** ValidationMessageStore methods  ***********\\


    /**
     * Records a validation error caused by an configuraton input value in the DataAccessColumn component primitive currently being processed by this mode.  This needs to be recorded in the mode as DataAccessColumns are stateless.  The DataAccessColumns  can later retrieve their associated validation failure message (if any) by calling validationFailure().
     *
     * @param validationFailure - message to display in the UI for this validation failure.
     */
    public void registerValidationFailure(String validationFailure)
    {
        /** require [valid_param] validationFailure != null;   [has_column] aColumn != null;   **/

        registerValidationFailureForKey(validationFailure, "");

        /** ensure [message_recorded] validationFailure() != null;  **/
    }



    /**
     * Records the validation failure for later retrieval in validationFailureForKey(String aKey).  They key is used as a method of uniquely indentifying the validation failure so that the correct one can be returned.
     *
     * @param validationFailure - message to display in the UI for this validation failure.
     * @param key - a unique key to associate this failure with
     */
    public void registerValidationFailureForKey(String validationFailure, String key)
    {
        /** require  [valid_param] validationFailure != null;  [valid_key] key != null;   **/
        validationFailures.setObjectForKey(validationFailure, validationFailureKeyForCurrentRowColumn() + key);
        ((DataAccessBaseEditor)parent()).notifySectionInvalid();
        /** ensure [message_recorded] validationFailureForKey(key) != null;  **/
    }



    /**
     * Returns the validation error caused by an input value in the DataAccessColumn component primitive currently being processed by this mode.  Returns null if no validation error occured.  This needs to be recorded in the mode as DataAccessColumns are stateless.  The DataAccessColumns  register their associated validation failure message by calling registerValidationFailure(String).
     *
     * @return the a validation error caused by an input value in the DataAccessColumn component primitive currently being processed by this mode.  Returns null if no validation error occured.
     */
    public String validationFailure()
    {
        /** require [has_column] aColumn != null;  **/

        return validationFailureForKey("");
    }



    /**
     * Returns the registered validation error for aKey or null if no validation error was registered.  They key is used as a method of uniquely indentifying the validation failure so that the correct one can be returned.
     *
     * @param key - a unique key associated with this failure
     * @return the appropriate registered validation error or null if no validation error was registered.
     */
    public String validationFailureForKey(String aKey)
    {
        /** require  [valid_param] aKey != null;   **/

        return (String) validationFailures.objectForKey(validationFailureKeyForCurrentRowColumn() + aKey);
    }



    /**
     * Returns a dictionay key uniquely indentifying the DataAccessColumn component primitive currently being processed by this mode.
     *
     * @return a dictionay key uniquely indentifying the DataAccessColumn component primitive currently being processed by this mode.
     */
    protected String validationFailureKeyForCurrentRowColumn()
    {
        /** require   [has_column] aColumn != null;     **/
        return editingContext().globalIDForObject(aColumn).toString();

        /** ensure [valid_result] Result != null;  **/
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
        validationFailures = new NSMutableDictionary();
        /** ensure [did_clear] ! hasValidationFailures();  **/
    }




    //*********** Generic Get / Set methods  ***********\\



    public com.gvcsitemaker.core.pagecomponent.DataAccessMode dataAccessMode()
    {
        return dataAccessMode;
    }
    public void setDataAccessMode(com.gvcsitemaker.core.pagecomponent.DataAccessMode newMode)
    {
        dataAccessMode = newMode;
    }

    public OrderedComponentList orderedComponents()
    {
        if (orderedComponents == null)
        {
            orderedComponents = new OrderedComponentList(dataAccessMode().orderedComponents());
        }

        return orderedComponents;

        /** ensure [valid_result] Result != null;  **/
    }


}
