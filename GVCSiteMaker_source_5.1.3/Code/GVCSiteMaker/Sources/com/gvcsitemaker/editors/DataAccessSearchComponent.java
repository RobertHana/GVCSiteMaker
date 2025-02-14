// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.editors;

import java.util.Enumeration;

import com.gvcsitemaker.core.appserver.SMSession;
import com.gvcsitemaker.core.databasetables.SMVirtualTable;
import com.gvcsitemaker.core.interfaces.ValidationMessageStore;
import com.gvcsitemaker.core.pagecomponent.DataAccessColumnSearchSortOrder;
import com.gvcsitemaker.core.pagecomponent.DataAccessColumnSearchValue;
import com.gvcsitemaker.core.pagecomponent.PageComponent;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSValidation;

import net.global_village.virtualtables.Column;


/**
 * Implements a UI for specifying searches on Virtual Tables.  Note that this class is <em>not</em> a subclass of BaseEditor.<p>
 * 
 * NOTE: This component manages the editing for two different types of DataAccessSearchMode children: DataAccessColumnSearchValue and DataAccessColumnSearchSortOrder.  We did this because this editor needs to have a child EC so that search criteria validation failures will be handled properly.  The problem is that you cannot have the same PageComponent in different ECs modifying it's children at the same time, since the component ordering of one will overwrite the other on merge.  Another solution that we looked at is to make two more PageComponent subclasses, one each to hold the two current child types of DataAccessSearchMode.  This would allow us to have each group of children in different ECs.  Since we didn't need that facility, and since it would take longer to do, we decided against this design at this time.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision$
 */
public class DataAccessSearchComponent extends WOComponent implements ValidationMessageStore
{
    protected Column aColumn;

    protected com.gvcsitemaker.core.pagecomponent.DataAccessSearchMode searchMode;

    protected DataAccessColumnSearchValue aColumnSearchValue;
    protected int columnSearchValuesIndex;

    protected EOEditingContext editingContext;

    protected NSMutableDictionary validationFailures = new NSMutableDictionary();

    public NSArray joinConditions = com.gvcsitemaker.core.support.DataAccessSearchCriterion.JOIN_CONDITIONS;
    public NSArray comparisonTypes = com.gvcsitemaker.core.support.DataAccessSearchCriterion.COMPARISON_TYPES;

    /** Bindings for the "When displayed as a list records..." UI. */
    public DataAccessColumnSearchSortOrder aSortOrdering;
    protected int aSortOrderIndex;
    /** @TypeInfo java.lang.String */
    protected final NSArray orderDirections = new NSArray(new String[] {DataAccessColumnSearchSortOrder.ORDER_DIRECTION_ASCENDING, DataAccessColumnSearchSortOrder.ORDER_DIRECTION_DESCENDING});

    protected final String isRestrictToDefaultResultsInvalidValidationKey = "isRestrictToDefaultResultsInvalid";
    protected final String isRestrictSetWithUseDefaultNotSetValidationKey = "isRestrictSetWithUseDefaultNotSet";
    protected final String hasSortColumnTwiceValidationKey = "hasSortColumnTwice";

    	public boolean showSearchOptions;
    	
    	

    /**
     * Designated constructor.
     */
    public DataAccessSearchComponent(WOContext context)
    {
        super(context);
    }



    /**
     * Removes from the stored search criteria those criteria that 1) is not the first row and the join condition is null ("not used..."), or 2) <em>is</em> the first row and none of the fields (column, comparison type, or value) are entered.
     */
    public void deleteUnusedCriteria()
    {
        boolean firstRow = true;
        NSMutableArray searchValuesToRemove = new NSMutableArray();
        Enumeration criteriaEnumerator = localSearchMode().orderedCriteria().objectEnumerator();
        while (criteriaEnumerator.hasMoreElements())
        {
            DataAccessColumnSearchValue dataAccessColumnSearchValue = (DataAccessColumnSearchValue)criteriaEnumerator.nextElement();

            // Delete if either: 1) its not the first row and the join condition is null ("not used..."), or 2) it _is_ the first row and none of the fields (column, comparison type, or value) are entered
            if ((( ! firstRow) && (dataAccessColumnSearchValue.joinCondition() == null)) ||
                ((firstRow) && ((dataAccessColumnSearchValue.column() == null) && (dataAccessColumnSearchValue.comparisonType() == null) && (dataAccessColumnSearchValue.value() == null))))
            {
                searchValuesToRemove.addObject(dataAccessColumnSearchValue);

                // We can safely do this now since we're not iterating over the validationFailures collection...
                validationFailures.removeObjectForKey(new Integer(dataAccessColumnSearchValue.hashCode()).toString());
            }

            firstRow = false;
        }
        
        localSearchMode().removeChildren(searchValuesToRemove);
        
        //Ensure that there is no join condition if there are only 1 criterion 
        if (localSearchMode().orderedCriteria().count() == 1)
        {
            ((DataAccessColumnSearchValue) localSearchMode().orderedCriteria().objectAtIndex(0)).setJoinCondition(null);
        }
    }


    /**
     * Removes from the stored search criteria those criteria that 1) is not the first row and the join condition is null ("not used..."), or 2) <em>is</em> the first row and none of the fields (column, comparison type, or value) are entered.
     */
    public void deleteUnusedSortOrderings()
    {
        NSMutableArray sortOrderValuesToRemove = new NSMutableArray();
        Enumeration sortOrderEnumerator = localSearchMode().orderedSortOrders().objectEnumerator();
        while (sortOrderEnumerator.hasMoreElements())
        {
            DataAccessColumnSearchSortOrder dataAccessColumnSearchSortOrder = (DataAccessColumnSearchSortOrder)sortOrderEnumerator.nextElement();

            if ((dataAccessColumnSearchSortOrder.column() == null) || (dataAccessColumnSearchSortOrder.sortDirection() == null))
            {
                sortOrderValuesToRemove.addObject(dataAccessColumnSearchSortOrder);
            }
        }
        localSearchMode().removeChildren(sortOrderValuesToRemove);
    }



    /**
     * Performs local validation for attributes changed directly in this component.  Sets validation flags (isUseDefaultSearchInvalid and isRestrictToDefaultResultsInvalid).
     */
    public void validateConfiguration()
    {
        // Note the use of the DataAccessSearchMode in the parent EC here (ie we're not using localSearchMode()).  This is because we store restrictToDefaultResults in the DataAccess page component and don't need it to be in the child EC
        if ((searchMode().dataAccessComponent().restrictToDefaultResults()) && (localSearchMode().orderedCriteria().count() == 0))
        {
            registerValidationFailureForKey("true", isRestrictToDefaultResultsInvalidValidationKey);
        }

        // Note the use of the DataAccessSearchMode in the parent EC here (ie we're not using localSearchMode()).  This is because we store restrictToDefaultResults in the DataAccess page component and don't need it to be in the child EC
        if ((searchMode().dataAccessComponent().restrictToDefaultResults()) && ( ! searchMode().dataAccessComponent().useDefaultSearch()))
        {
            registerValidationFailureForKey("true", isRestrictSetWithUseDefaultNotSetValidationKey);
        }

        if (localSearchMode().hasDuplicateSortColumns())
        {
            registerValidationFailureForKey("true", hasSortColumnTwiceValidationKey);
        }
    }



    /**
     * Overridden to handle saving the search values to the parent EC.
     */
    public void takeValuesFromRequest(WORequest aRequest, WOContext aContext)
    {
        super.takeValuesFromRequest(aRequest, aContext);

        deleteUnusedCriteria();
        deleteUnusedSortOrderings();

        validateConfiguration();

        if ( ! hasValidationFailures())
        {
            try
            {
               editingContext().saveChanges();

                // Clear the cached values in the parent EC
                searchMode().clearCachedValues();
                Enumeration searchValueEnumerator = searchMode().orderedComponents().objectEnumerator();
                while (searchValueEnumerator.hasMoreElements())
                {
                    PageComponent searchValue = (PageComponent)searchValueEnumerator.nextElement();
                    searchValue.clearCachedValues();
                }
            }
            catch (NSValidation.ValidationException e)
            {
                registerValidationFailureForKey(e.getMessage(), new Integer(e.object().hashCode()).toString());
                ((DataAccessSectionSettingsEditor)parent()).notifySectionInvalid();
            }
        }
        else
        {
            ((DataAccessSectionSettingsEditor)parent()).notifySectionInvalid();
        }
    }



    /**
     * Overridden to make sure there is at least one stored search criterion available for the user to modify.
     */
    public void appendToResponse(WOResponse response, WOContext context)
    {
        // This set's up the initial search values by sync'ing with the search mode's children. If the search mode has no children, then we create an empty search value so that the user isn't confused...
        if (localSearchMode().orderedCriteria().count() == 0)
        {
            addMoreCriteria();
        }

        super.appendToResponse(response, context);

        clearValidationFailures();

        /** ensure [stored_search_has_criteria] localSearchMode().orderedCriteria().count() >= 1; **/
    }



    /**
     * Adds new criteria to the search.
     *
     * @return the page that this component is part of
     */
    public WOComponent addMoreCriteria()
    {
        localSearchMode().addChild(DataAccessColumnSearchValue.newDataAccessColumnSearchValue());
        return context().page();
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns <code>true</code> if the sort ordering column is the first in the repitition, <code>false</code> otherwise.
     *
     * @return <code>true</code> if the sort ordering column is the first in the repitition, <code>false</code> otherwise
     */
    public boolean isFirstSortColumn()
    {
        return aSortOrderIndex() == 0;
    }


    /**
     * Returns the no selection string for the column pick lists.  Returns <code>null</code> for the column in the first sort ordering (since the user isn't allowed to not select the first one), "" for the remaining columns.
     *
     * @return <code>null</code> for the columns in the first sort ordering, "" for the remaining columns
     */
    public String sortOrderingColumnNoSelectionString()
    {
        return isFirstSortColumn() ? null : " ";
    }

    /**
     * Returns the no selection string for the order direction lists.  Returns <code>null</code> for the order direction in the first sort ordering (since the user isn't allowed to not select the first one), "" for the remaining sort orderings.
     *
     * @return <code>null</code> for the order direction in the first sort ordering, "" for the remaining sort orderings
     */
    public String sortOrderingDirectionNoSelectionString()
    {
        return isFirstSortColumn() ? null : " ";
    }



    /**
     * Adds a <code>DataAccessColumnSearchSortOrder</code> child to the search mode and returns this page.
     *
     * @return this page
     */
    public WOComponent addSortOrder()
    {
        localSearchMode().addSortOrder(false);
        return context().page();
        /** ensure [valid_result] Result != null; **/
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
     * Returns the registered validation error for aKey or null if no validation error was registered.  The key is used as a method of uniquely indentifying the validation failure so that the correct one can be returned.
     *
     * @return the appropriate registered validation error or null if no validation error was registered.
     */
    public String validationFailureForColumnSearchValue()
    {
        return validationFailureForKey(new Integer(aColumnSearchValue().hashCode()).toString());
    }



    /**
     * Returns true if any validation errors have been registered.
     *
     * @return true if any validation errors have been registered.
     */
    public boolean columnSearchValueHasValidationFailures()
    {
        return validationFailureForKey(new Integer(aColumnSearchValue().hashCode()).toString()) != null;
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



    /**
     * Overridden to handle validation errors caused by data input when configuring this PageComponent.
     */
    public void validationFailedWithException(Throwable t, Object value, String keyPath)
    {
        if ((keyPath.startsWith("aColumnSearchValue")) && ((keyPath.endsWith("column")) || (keyPath.endsWith("comparisonType")) || (keyPath.endsWith("value"))))
        {
            // Force the invalid value into the EO so that the user can see what was wrong.
            takeValueForKeyPath(value, keyPath);

            if ((aColumnSearchValue().joinCondition() != null) || (columnSearchValuesIndex() == 0))
            {
                registerValidationFailureForKey(t.getMessage(), new Integer(aColumnSearchValue().hashCode()).toString());
            }
        }
        else
        {
             super.validationFailedWithException(t, value, keyPath);
        }
    }



    //********** Generic accessor/mutators below here ***********\\

    public SMVirtualTable table()
    {
        return (SMVirtualTable)localSearchMode().databaseTable();
    }



    /** This is mainly used by the WO binding facility.  Use <code>localSearchMode()</code> instead. */
    public com.gvcsitemaker.core.pagecomponent.DataAccessSearchMode searchMode()
    {
        return searchMode;
    }


    /** This should be used by the WO binding facility <em>only</em>. */
    public void setSearchMode(com.gvcsitemaker.core.pagecomponent.DataAccessSearchMode newSearchMode)
    {
        searchMode = newSearchMode;
    }



    /**
     * Returns the search mode in our editing context.
     */
    public com.gvcsitemaker.core.pagecomponent.DataAccessSearchMode localSearchMode()
    {
        return (com.gvcsitemaker.core.pagecomponent.DataAccessSearchMode)EOUtilities.localInstanceOfObject(editingContext(), searchMode());
    }



    public DataAccessColumnSearchValue aColumnSearchValue()
    {
        return aColumnSearchValue;
    }


    public void setAColumnSearchValue(DataAccessColumnSearchValue newAColumnSearchValue)
    {
        aColumnSearchValue = newAColumnSearchValue;
    }


    public int columnSearchValuesIndex()
    {
        return columnSearchValuesIndex;
    }


    public void setColumnSearchValuesIndex(int newColumnSearchValuesIndex)
    {
        columnSearchValuesIndex = newColumnSearchValuesIndex;
    }



    public boolean shouldDisplayJoinConditionPopup()
    {
        return columnSearchValuesIndex != 0;
    }



    public Column aColumn() {
        return aColumn;
    }

    public void setAColumn(Column newAColumn) {
        aColumn = newAColumn;
    }


    public NSArray orderDirections() {
        return orderDirections;
    }



    public int aSortOrderIndex()
    {
        return aSortOrderIndex;
    }

    public void setASortOrderIndex(int i)
    {
        aSortOrderIndex = i;
    }



    public boolean isRestrictToDefaultResultsInvalid()
    {
        return validationFailureForKey(isRestrictToDefaultResultsInvalidValidationKey) != null;
    }


    public boolean isRestrictSetWithUseDefaultNotSet()
    {
        return validationFailureForKey(isRestrictSetWithUseDefaultNotSetValidationKey) != null;
    }


    public boolean hasSortColumnTwice()
    {
        return validationFailureForKey(hasSortColumnTwiceValidationKey) != null;
    }



    public EOEditingContext editingContext()
    {
        if (editingContext == null)
        {
            editingContext = ((SMSession)session()).newEditingContext(searchMode().editingContext());
        }
        return editingContext;
    }



}
