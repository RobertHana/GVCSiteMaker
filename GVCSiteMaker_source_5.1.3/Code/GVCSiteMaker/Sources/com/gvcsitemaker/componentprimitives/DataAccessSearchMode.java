package com.gvcsitemaker.componentprimitives;

import java.util.Enumeration;

import com.gvcsitemaker.core.appserver.SMApplication;
import com.gvcsitemaker.core.pagecomponent.DataAccessColumnSearchSortOrder;
import com.gvcsitemaker.core.pagecomponent.PageComponent;
import com.gvcsitemaker.core.support.DataAccessSearchCriterion;
import com.gvcsitemaker.core.support.DataAccessSearchParameters;
import com.gvcsitemaker.core.support.DataAccessSearchSortOrder;
import com.gvcsitemaker.core.support.SMDataAccessActionURLFactory;
import com.gvcsitemaker.core.support.DataAccessSearchSortOrder.DuplicateSortOrderValidationException;
import com.gvcsitemaker.core.utility.DebugOut;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.appserver.WORequest;
import com.webobjects.appserver.WOResponse;
import com.webobjects.foundation.NSArray;
import com.webobjects.foundation.NSMutableArray;
import com.webobjects.foundation.NSMutableDictionary;
import com.webobjects.foundation.NSNumberFormatter;
import com.webobjects.foundation.NSPathUtilities;
import com.webobjects.foundation.NSTimestamp;
import com.webobjects.foundation.NSTimestampFormatter;

import net.global_village.virtualtables.Column;


/**
 * DataAccessSearchMode implements the UI for com.gvcsitemaker.core.pagecomponent.DataAccessSearchMode, 
 * the Search for Records view of a Data Access Section.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision$
 */
public class DataAccessSearchMode extends DataAccessMode 
{
    public NSArray joinConditions = com.gvcsitemaker.core.support.DataAccessSearchCriterion.JOIN_CONDITIONS;
    public NSArray comparisonTypes = com.gvcsitemaker.core.support.DataAccessSearchCriterion.COMPARISON_TYPES;
    /** @TypeInfo java.lang.String */
    public NSArray orderDirections = new NSArray(new String[] {DataAccessColumnSearchSortOrder.ORDER_DIRECTION_ASCENDING, DataAccessColumnSearchSortOrder.ORDER_DIRECTION_DESCENDING});
    protected NSMutableArray groupSizes;

    public boolean showAllRecords;
    protected DataAccessSearchCriterion aCriterion;
    protected int criteriaIndex;
    protected Column aColumn;
    protected String aComparisonType;
    protected String aJoinCondition;
    
    protected String aGroupSize;

    public static final String DUPLICATE_SORT_ORDER_VALIDATION_FAILURE_KEY = "duplicateSortOrder";
    public DataAccessSearchSortOrder aSortOrdering;
    public int sortOrderingIndex;
    protected Column anOrderByColumn;
    protected String anOrderDirection;

    protected NSMutableDictionary criteriaByField;
    protected NSMutableDictionary lookupValues;
    protected NSNumberFormatter standardNumberFormatter;
    protected NSTimestampFormatter dateAndTimeFormatter;
    public Object lookupItem;

    
    
    /**
     * Designated constructor.
     */
    public DataAccessSearchMode(WOContext context)
    {
        super(context);
        standardNumberFormatter = (NSNumberFormatter)SMApplication.appProperties().propertyForKey("InputNumberFormatter");
        dateAndTimeFormatter = (NSTimestampFormatter)SMApplication.appProperties().propertyForKey("InputDateFormatter");
    }



    /**
     * Overridden to handle deleting unused criteria.
     */
    public void takeValuesFromRequest(WORequest aRequest, WOContext aContext)
    {
        super.takeValuesFromRequest(aRequest, aContext);
        
        // Just leave this alone for the simple search form.  See criteriaByField().  This gets done 
        // just before the search is performed
        if ( ! isUsingSimpleSearch())
        {
            deleteUnusedCriteria();
        }
        deleteUnusedSortOrders();
    }



    /**
     * Overridden to make sure there is at least one search criterion available for the user to modify.
     */
    public void appendToResponse(WOResponse response, WOContext context)
    {
        if ( ! criteria().hasCriteria())
        {
            addCriterion();
        }
        if ( ! criteria().hasSortOrders())
        {
            criteria().addSortOrder(databaseTable().columnNamed(com.gvcsitemaker.core.pagecomponent.DataAccessRecordIDColumn.ColumnTypeName), DataAccessColumnSearchSortOrder.ORDER_DIRECTION_ASCENDING);
        }

        super.appendToResponse(response, context);

        clearValidationFailures();

        /** ensure [search_has_criteria] criteria().hasCriteria(); **/
    }



    /**
     * Overridden set showAllRecords based on what is in the search criteria.
     */
    public void setComponentObject(PageComponent aPageComponent)
    {
        /** require [has_context] context() != null; [valid_param] aPageComponent != null; **/

        super.setComponentObject(aPageComponent);

        if (criteria().hasCriteria())
        {
            showAllRecords = false;
        }
        else
        {
            showAllRecords = true;
        }

        if (isUsingSimpleSearch())
        {
            setupForSimpleSearch();
        }
        
        /** ensure
        [componentObject_set] componentObject() != null;
        [has_editingContext] editingContext() != null;
        [has_dataAccessParameters] dataAccessParameters() != null; **/
    }


    
    /**
     * Sets up criteria() and criteriaByField() for use with the simple (and/or) search form.
     *
     */
    protected void setupForSimpleSearch()
    {
        criteria().removeAllCriteria();
        // Setup prepackaged criteria for the Simple Search Mode
        criteriaByField = new NSMutableDictionary();
        NSArray availableColumns = searchModeComponent().visibleFields();
        for (int i = 0; i < availableColumns.count(); i++) {
            com.gvcsitemaker.core.pagecomponent.DataAccessColumn aDAColumn = 
                (com.gvcsitemaker.core.pagecomponent.DataAccessColumn) availableColumns.objectAtIndex(i);
            DataAccessSearchCriterion newCriterion = criteria().newSearchCriterion();
            newCriterion.setColumn(aDAColumn.column());
            newCriterion.setComparisonType(aDAColumn.defaultComparison());
            
            if (i > 0)
            {
                newCriterion.setJoinCondition(DataAccessSearchCriterion.OR_JOIN_CONDITION);
            }

            criteriaByField.setObjectForKey(newCriterion, aDAColumn.column().normalizedName());
        }

        // Setup lists of lookup values
        lookupValues = new NSMutableDictionary();
        for (int i = 0; i < availableColumns.count(); i++) {
            com.gvcsitemaker.core.pagecomponent.DataAccessColumn aDAColumn = 
                (com.gvcsitemaker.core.pagecomponent.DataAccessColumn) availableColumns.objectAtIndex(i);
            if (aDAColumn instanceof com.gvcsitemaker.core.pagecomponent.DataAccessLookupColumn)
            {
                NSArray values = (NSArray)lookupValues((com.gvcsitemaker.core.pagecomponent.DataAccessLookupColumn)aDAColumn).valueForKey("value");
                NSMutableArray formattedValues = new NSMutableArray(values.count());
                
                // Dates, Numbers etc. benefit from formatting
                for (int j = 0; j < values.count(); j++) {
                    Object aValue = values.objectAtIndex(j);
                    formattedValues.addObject(formattedLookupValue(aValue));
                }
                lookupValues.setObjectForKey(formattedValues, aDAColumn.column().normalizedName());
            }
        }
        
    }
    
    
    
    /**
     * Converts a lookup value to a string, formatting it as appropriate.
     * 
     * @param aValue the lookup value to convert
     */
    public String formattedLookupValue(Object aValue) {

        // General case
        lookupItem = aValue;

        // Special cases
        if (aValue == null)
        {
            lookupItem = "";
        }
        else if (aValue instanceof NSTimestamp)
        {
            lookupItem = dateAndTimeFormatter.format(aValue);
        }
        else if (aValue instanceof Number)
        {
            lookupItem = standardNumberFormatter.format(aValue);
        }

        // The "just in" case
        if ( ! (lookupItem instanceof String))
        {
            lookupItem = lookupItem.toString();
        }
        
        return (String) lookupItem;
    }
    
    
    
    /**
     * Removes from the search criteria those criteria that 1) is not the first row and the join condition 
     * is null ("not used..."), or 2) <em>is</em> the first row and none of the fields (column, comparison 
     * type, or value) are entered. Also removes any error messages that might have been generated for those rows.
     */
    public void deleteUnusedCriteria()
    {
        NSMutableArray searchValuesToRemove = new NSMutableArray();
        NSMutableArray errorMessagesToRemove = new NSMutableArray();
        int aCriteriaIndex = -1;
        Enumeration criterionEnumerator = criteria().criterionEnumerator();
        while (criterionEnumerator.hasMoreElements())
        {
            DataAccessSearchCriterion criterion = (DataAccessSearchCriterion)criterionEnumerator.nextElement();
            aCriteriaIndex++;

            if (isCriterionUnused(criterion))
            {
                searchValuesToRemove.addObject(criterion);
                DebugOut.println(20, "Removing incomplete criterion: " + criterion);
                errorMessagesToRemove.addObject(new Integer(aCriteriaIndex).toString());
            }
        }
        validationFailures.removeObjectsForKeys(errorMessagesToRemove);
        criteria().removeCriteriaInArray(searchValuesToRemove);
    }

        
    
    /**
     * @param criterion the DataAccessSearchCriterion to check
     * @return <code>true</code> if the criterion has so little information that is to be considered
     * unused rather than invalid.
     */
    protected boolean isCriterionUnused(DataAccessSearchCriterion criterion)
    {
        if (isUsingSimpleSearch())
        {
            return (criterion.value() == null);
        }
        else
        {
            // it is unused if either: 1) its not the first row and the join condition is null ("not used..."), 
            // or 2) it _is_ the first row and none of the fields (column, comparison type, or value) are entered
            return ((! criterion.isFirstCriterion()) && (criterion.joinCondition() == null)) ||
                    ((criterion.isFirstCriterion()) && ((criterion.column() == null) &&
                                                        (criterion.comparisonType() == null) &&
                                                        (criterion.value() == null)));
        }
    }

    

    /**
     * Removes from the sort orders those that have <code>null</code> values for either the sort column or the sort direction.
     */
    public void deleteUnusedSortOrders()
    {
        NSMutableArray sortOrdersToRemove = new NSMutableArray();
        Enumeration sortOrderEnumerator = criteria().sortOrderEnumerator();
        while (sortOrderEnumerator.hasMoreElements())
        {
            DataAccessSearchSortOrder sortOrder = (DataAccessSearchSortOrder)sortOrderEnumerator.nextElement();

            if ((sortOrder.sortColumn() == null) || (sortOrder.sortDirection() == null))
            {
                sortOrdersToRemove.addObject(sortOrder);
                DebugOut.println(20, "Removing incomplete sort order: " + sortOrder);
            }
        }
        criteria().removeSortOrdersInArray(sortOrdersToRemove);
    }



    /**
     * Sets the search params in the URL and redirects to the proper page (single or list mode). This 
     * method is used when searching on a page with the default template so that the WO bindings will 
     * be used instead of taking values from the URL.
     *
     * @return this if there were validation errors, otherwise returns the single mode page (if the 
     * user specified a group size of 1) or the list mode page (for any other group size)
     */
    public WOComponent searchWithDefaultTemplate()
    {
        if (showAllRecords)
        {
            dataAccessParameters().findAll();
        }

        if ( ! hasValidationFailures())
        {
            // The simple search needs some special prep
            if (isUsingSimpleSearch())
            {
                // This needs to be cleaned up now that we are going to search
                deleteUnusedCriteria();

                // Since this is the default, if it is not changed, WO does not set it
                if (criteria().joinAllCondition() == null)
                {
                    criteria().setJoinAllCondition(defaultJoinAllCondition());
                }
            }

            String url;
            if (groupSize().equals("1"))
            {
                url = SMDataAccessActionURLFactory.firstRecordInSingleModeUrl(dataAccessParameters());
            }
            else
            {
                url = SMDataAccessActionURLFactory.firstGroupInListModeUrl(dataAccessParameters());
            }

            // This is done in case the user backtracks to try another search.  We don't really support this,
            // but it seems like an all too common thing to do...
            if (isUsingSimpleSearch())
            {
                setupForSimpleSearch();
            }

            dataAccessParameters().clearCachedValues();
            return redirectToUrl(url);
        }
        else
        {
            return context().page();
        }
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Sets the search params in the URL and redirects to the proper page (single or list mode).  
     * This method is used for searching with a custom template so that the search parameters will be 
     * taken off the URL, instead of taking them from the (non-existant) WO bindings.
     *
     * @return this if there were validation errors, otherwise returns the single mode page (if the 
     * user specified a group size of 1) or the list mode page (for any other group size)
     */
    public WOComponent search()
    {
        // Reset the params to what the user just entered.  We have to reset the form values because the 
        // DAParameter object does _not_ get recreated every time through the R-R loop, which causes the 
        // form values to remain the same for each reqest.  If we are searching with a custom template, 
        // then the only way to get the search params is to suck them out of the new form values, which 
        // is what we do here...
        dataAccessParameters().setFormValues(context().request().formValues());
        return searchWithDefaultTemplate();
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Adds a place in the UI for the users to add a single search term.
     *
     * @return the current page
     */
    public WOComponent addCriterion()
    {
        criteria().newSearchCriterion();
        return context().page();
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Adds a place in the UI for the users to add a single sort order.
     *
     * @return the current page
     */
    public WOComponent addSortOrder()
    {
        criteria().newSortOrder();
        return context().page();
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Controls the display of the join condition popup.
     *
     * @return <code>false</code> if we are on the first criterion, <code>true</code> otherwise
     */
    public boolean shouldShowJoinConditions()
    {
        return ! criteria().isFirstCriterion(aCriterion());
    }


    
    /**
     * Accessor for default version of simple search
     * @return <code>true</code> if aCriterion() is for a lookup column
     */
    public boolean isLookupColumn()
    {
        return criterionlookupValues() != null;
    }

    
    
    /**
     * Accessor for default version of simple search
     * @return NSArray of lookup values for aCriterion().column() or null if it is not a lookup column
     * 
     */
    public Object criterionlookupValues()
    {
        return lookupValues().objectForKey(aCriterion().column().normalizedName());
    }
    
    
    
    /**
     * Controls the display of the criterion error messages.
     *
     * @return <code>true</code> if this criterion has an error message to display, <code>false</code> otherwise
     */
    public boolean criterionHasError()
    {
        /** require [has_criterion] aCriterion() != null; **/
        return validationFailureForKey(new Integer(criteriaIndex()).toString()) != null;
    }



    /**
     * Displays the error message for the current criterion.
     *
     * @return the error message for the current criterion
     */
    public String criterionErrorMessage()
    {
        /** require
        [has_criterion] aCriterion() != null;
        [has_error_message] criterionHasError(); **/
        return validationFailureForKey(new Integer(criteriaIndex()).toString());
        /** ensure [valid_result] Result != null; **/
    }

    

    /**
     * Overridden to extend KVC to allow things like validationFailureByIndex.3 instead of 
     * validationFailureForKey("3").  This is the custom form counterpart to criterionErrorMessage().
     */
    public Object valueForKeyPath(String keyPath) {
        if (keyPath.startsWith("validationFailureByIndex"))
        {
            String failureKey = NSPathUtilities.pathExtension(keyPath);
            return validationFailureForKey(failureKey);
        }
        else
        {
            return super.valueForKeyPath(keyPath);
        }        
    }
    

    
    /**
     * Returns the list of columns that the user can search on.  
     *
     * @return the list of columns that the user can search on
     */
    public NSArray searchableAndVisibleColumns()
    {
        return (NSArray) searchModeComponent().visibleFields().valueForKey("column");
        /** ensure [valid_result] Result != null; **/
    }

    

    /**
     * Returns the list of columns that the user can order on.
     *
     * @return the list of columns that the user can order on
     */
    public NSArray orderableAndVisibleColumns()
    {
        // For now, these are the same
        return searchableAndVisibleColumns();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the no selection string for the column pick lists.  Returns <code>null</code> for the column 
     * in the first sort ordering (since the user isn't allowed to not select the first one), "" for the remaining columns.
     *
     * @return <code>null</code> for the columns in the first sort ordering, "" for the remaining columns
     */
    public String sortOrderingColumnNoSelectionString()
    {
        return aSortOrdering.isFirstSortOrder() ? null : "";
    }

    /**
     * Returns the no selection string for the order direction lists.  Returns <code>null</code> for the 
     * order direction in the first sort ordering (since the user isn't allowed to not select the first one), 
     * "" for the remaining sort orderings.
     *
     * @return <code>null</code> for the order direction in the first sort ordering, "" for the remaining sort orderings
     */
    public String sortOrderingDirectionNoSelectionString()
    {
        return aSortOrdering.isFirstSortOrder() ? null : "";
    }



    /**
     * Returns <code>true</code> if any column appears more than once in the sorting columns, <code>false</code> otherwise.
     * 
     * @return <code>true</code> if any column appears more than once in the sorting columns, <code>false</code> otherwise
     */
    public boolean hasDuplicateSortColumns()
    {
        return validationFailureForKey(DUPLICATE_SORT_ORDER_VALIDATION_FAILURE_KEY) != null;
    }


    
    /**
     * Returns the list of group sizes that the UI will display.  Gets the list from the config file, 
     * then add a "1" to the beginning so that users have the option of displaying search results in single record mode.
     *
     * @return the list of group sizes
     */
    public NSArray groupSizes()
    {
        if (groupSizes == null)
        {
            groupSizes = new NSMutableArray(SMApplication.appProperties().arrayPropertyForKey("DataAccessListSearchResultGroupSizes"));
            groupSizes.insertObjectAtIndex("1", 0);
        }
        
        return groupSizes;
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the value for the AND/OR radio buttons in the UI based on the setting in 
     * criteria().joinAllCondition().  If nothing has been set, a default of OR is returned.  We do not
     * want to set the default in DataAccessSearchParameters as it will then get applied to all search modes.
     * We only want this value set when the corresponding WO tag is in the template.
     * 
     * @return the value for the AND/OR radio buttons in the UI 
     */
    public String joinAllCondition()
    {
        return (criteria().joinAllCondition() == null) ? defaultJoinAllCondition() : criteria().joinAllCondition();
    }
    
    
    
    /**
     * Sets the joinAllCondition() in the DataAccessSearchParameters to what was selected in the UI.
     * 
     * @param newCondition one of the *.JOIN_CONDITION values from DataAccessSearchCriterion
     */
    public void setJoinAllCondition(String newCondition)
    {
        criteria().setJoinAllCondition(newCondition);
    }
    
    
    
    /**
     * @return the default value to use for the joinAll condition on the simple search form.
     */
    public String defaultJoinAllCondition()
    {
        return DataAccessSearchCriterion.OR_JOIN_CONDITION;
    }

    
    
    /**
     * There is no "no selection" option for the simple search, a comparison must always be selected.
     * Otherwise we show "Choose comparison...".
     * 
     * @return the noSelectionString for the comparison popup used in the default forms
     */
    public String comparisionNoSelectionString()
    {
        return isUsingSimpleSearch() ? null : "Choose comparison...";
    }
    
    
    
    /**
     * Overridden to handle validation errors caused by data input when searching.  Both default
     * and custom form validation failures will end up here.
     */
    public void validationFailedWithException(Throwable t, Object value, String keyPath)
    {
        if (isCriterionKeyPath(keyPath))
        {
            // Ignore the validation errors on criteria if we are about to do a Search All
            if ( ! showAllRecords)
            {
                // Force the invalid value into the cirterion so that the user can see it
                takeValueForKeyPath(value, keyPath);

                // The failed criteria can come from the WORepetition or from a name lookup
                DataAccessSearchCriterion failedCriterion;
                if (keyPath.startsWith("criteriaByField"))
                {
                    failedCriterion = (DataAccessSearchCriterion) valueForKeyPath(NSPathUtilities.stringByDeletingPathExtension(keyPath));
                }
                else
                {
                    failedCriterion = aCriterion();
                }
                String criterionFailureKey = new Integer(criteria().indexOfCriterion(failedCriterion)).toString();
                
                // Register validation failure unless the line is marked as ignore
                if ((failedCriterion.joinCondition() != null) || criteria().isFirstCriterion(failedCriterion))
                {
                    // For the simple search, ignore any failures if there is no value selected.  
                    // The column, comparision, and join will always be set.
                    if (isUsingSimpleSearch() && (failedCriterion.value() != null))
                    {
                        DebugOut.println(1, "Simple search failed validation on " + value + " for " + keyPath);
                        DebugOut.println(1, "Recording error " + t.getMessage() + " for " + criterionFailureKey);
                        registerValidationFailureForKey(t.getMessage(), criterionFailureKey);
                    }

                    if ( ! isUsingSimpleSearch())
                    {
                        registerValidationFailureForKey(t.getMessage(), criterionFailureKey);
                    }
                }
            }
        }
        else if (isSortOrderKeyPath(keyPath))
        {
            // Force the invalid value into the cirterion so that the user can see it
            takeValueForKeyPath(value, keyPath);

            // Register the error only if it is for a duplicate sort order column (we delete the unused 
            // sort orders at the end of takeValues())
            if (t instanceof DuplicateSortOrderValidationException)
            {
                registerValidationFailureForKey(t.getMessage(), DUPLICATE_SORT_ORDER_VALIDATION_FAILURE_KEY);
            }
        }
        else
        {
             super.validationFailedWithException(t, value, keyPath);
        }
    }


    
    /**
     * For use with validationFailedWithException.
     *
     *  @param keyPath the binding key path to check
     * @return true if the keyPath is from a criterion binding
     */
    protected boolean isCriterionKeyPath(String keyPath)
    {
        boolean hasPrefix = keyPath.startsWith("aCriterion") || keyPath.startsWith("criteriaByField");
        boolean hasSuffix = keyPath.endsWith(DataAccessSearchParameters.COLUMN_CRITERION_KEY) ||
                            keyPath.endsWith(DataAccessSearchParameters.COMPARISON_TYPE_CRITERION_KEY) ||
                            keyPath.endsWith(DataAccessSearchParameters.VALUE_CRITERION_KEY); 
        return hasPrefix && hasSuffix;
    }   

    
        
    /**
     * For use with validationFailedWithException.
     *
     *  @param keyPath the binding key path to check
     * @return true if the keyPath is from a sort order binding
     */
    protected boolean isSortOrderKeyPath(String keyPath)
    {
        boolean hasPrefix = keyPath.startsWith("aSortOrdering");
        boolean hasSuffix = keyPath.endsWith(DataAccessSearchParameters.COLUMN_SORT_ORDER_KEY) ||
                            keyPath.endsWith(DataAccessSearchParameters.DIRECTION_SORT_ORDER_KEY);
        return hasPrefix && hasSuffix;
    }   


        
    //************** Generic accessors/mutators **************\\

    public DataAccessSearchParameters criteria() {
        return dataAccessParameters().searchParameters();
        /** ensure [valid_result] Result != null; **/
    }
    
    public com.gvcsitemaker.core.pagecomponent.DataAccessSearchMode searchModeComponent() {
        return (com.gvcsitemaker.core.pagecomponent.DataAccessSearchMode) componentObject();
        /** ensure [valid_result] Result != null; **/
    }
    
    public boolean isUsingSimpleSearch() {
        return  searchModeComponent().isUsingSearchForm(com.gvcsitemaker.core.pagecomponent.DataAccessSearchMode.SIMPLE_SEARCH_FORM);
    }
    
    public DataAccessSearchCriterion aCriterion() {
        return aCriterion;
    }
    public void setACriterion(DataAccessSearchCriterion newCriterion) {
        aCriterion = newCriterion;
    }
    
    public int criteriaIndex() {
        return criteriaIndex;
    }
    public void setCriteriaIndex(int newCriteriaIndex) {
        criteriaIndex = newCriteriaIndex;
    }
    
    public Column aColumn() {
        return aColumn;
    }
    public void setAColumn(Column newColumn) {
        aColumn = newColumn;
    }
    
    public String aComparisonType() {
        return aComparisonType;
    }
    public void setAComparisonType(String newComparisonType) {
        aComparisonType = newComparisonType;
    }

    public String aJoinCondition() {
        return aJoinCondition;
    }
    public void setAJoinCondition(String newJoinCondition) {
        aJoinCondition = newJoinCondition;
    }

    public String aGroupSize() {
        return aGroupSize;
    }
    public void setAGroupSize(String newGroupSize) {
        aGroupSize = newGroupSize;
    }

    public String anOrderDirection() {
        return anOrderDirection;
    }
    public void setAnOrderDirection(String newOrderDirection) {
        anOrderDirection = newOrderDirection;
    }

    public String columnURLName() {
        return DataAccessSearchParameters.COLUMN_CRITERION_KEY + criteriaIndex();
    }
    public String joinConditionURLName() {
        return DataAccessSearchParameters.JOIN_CONDITION_CRITERION_KEY + criteriaIndex();
    }
    public String comparisonTypeURLName() {
        return DataAccessSearchParameters.COMPARISON_TYPE_CRITERION_KEY + criteriaIndex();
    }
    public String valueURLName() {
        return DataAccessSearchParameters.VALUE_CRITERION_KEY + criteriaIndex();
    }

    public String groupSize() {
        return criteria().groupSize();
    }
    public void setGroupSize(String newGroupSize) {
        criteria().setGroupSize(newGroupSize);
    }

    public Column anOrderByColumn() {
        return anOrderByColumn;
    }
    public void setAnOrderByColumn(Column newAnOrderByColumn) {
        anOrderByColumn = newAnOrderByColumn;
    }

    public NSMutableDictionary criteriaByField() {
        return criteriaByField;
    }
    
    public NSMutableDictionary lookupValues() {
        return lookupValues;
    }

        

}
