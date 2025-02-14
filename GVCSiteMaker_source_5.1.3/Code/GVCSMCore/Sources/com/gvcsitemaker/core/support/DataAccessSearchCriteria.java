// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.support;

import java.util.*;

import net.global_village.foundation.*;
import net.global_village.virtualtables.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.pagecomponent.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * DataAccessSearchCriteria is a data structure used to manage creating a search for a Database Table
 * and turning that search into an EOQualifier.  A search consists of 0..N DataAccessSearchCriterion,
 * each representing one element in a search.
 */
public class DataAccessSearchCriteria extends Object
{
    protected NSMutableArray searchCriteria;
    protected NSMutableArray sortOrders;
    private DataAccess dataAccessComponent;


    /**
     * Designated constructor.
     *
     * @param aDataAccessComponent DataAccess PageComponent used to qualify searches in a Section.
     * May be null for situations like exporting where no Section is involved
     */
    public DataAccessSearchCriteria(DataAccess aDataAccessComponent)
    {
        super();

        dataAccessComponent = aDataAccessComponent;
        searchCriteria = new NSMutableArray();
        sortOrders = new NSMutableArray();
    }



    /**
     * Adds a new search criterion as the last in the list and returns it.
     *
     * @return the newly created criterion
     */
    public DataAccessSearchCriterion newSearchCriterion()
    {
        searchCriteria.addObject(new DataAccessSearchCriterion(this));
        return (DataAccessSearchCriterion)searchCriteria.lastObject();

        /** ensure
        [valid_result] Result != null;
        [criterion_linked] Result.searchCriteria().equals(this);
        [in_search_criteria] searchCriteria().lastObject().equals(Result); **/
    }



    /**
     * Adds a new search criterion as the last in the list and returns it.
     *
     * @return the newly created sort order
     */
    public DataAccessSearchSortOrder newSortOrder()
    {
        DataAccessSearchSortOrder sortOrder = new DataAccessSearchSortOrder(this);
        sortOrders.addObject(sortOrder);
        return sortOrder;

        /** ensure
        [valid_result] Result != null;
        [criterion_linked] Result.searchCriteria().equals(this);
        [in_search_criteria] sortOrders().lastObject().equals(Result); **/
    }



    /**
     * Adds a new search criterion as the last in the list, initializes it with the passed values, and returns it.
     *
     * @param aJoinCondition the initial join condition for this criterion
     * @param aColumn the initial column for this criterion
     * @param aComparisonType the initial comparison type for this criterion
     * @param aValue the initial value for this criterion
     * @return the newly created criterion
     */
    public DataAccessSearchCriterion addSearchCriterion(String aJoinCondition, Column aColumn, String aComparisonType, String aValue)
    {
        /** require
        [valid_join_condition] (aJoinCondition == null) || DataAccessSearchCriterion.JOIN_CONDITIONS.containsObject(aJoinCondition);
        [valid_column] aColumn != null;
        [valid_comparison_type] (aComparisonType != null) && DataAccessSearchCriterion.COMPARISON_TYPES.containsObject(aComparisonType);
        [valid_value] aValue != null; **/

        DataAccessSearchCriterion newSearchCriterion = newSearchCriterion();
        newSearchCriterion.setJoinCondition(aJoinCondition);
        newSearchCriterion.setColumn(aColumn);
        newSearchCriterion.setComparisonType(aComparisonType);
        newSearchCriterion.setValue(aValue);

        return newSearchCriterion;

        /** ensure
        [valid_result] Result != null;
        [criterion_linked] Result.searchCriteria().equals(this);
        [in_search_criteria] searchCriteria().lastObject().equals(Result);
        [join_condition_set] Result.joinCondition() == aJoinCondition;
        [column_set] Result.column() == aColumn;
        [comparison_type_set] Result.comparisonType() == aComparisonType;
        [value_set] Result.value() == aValue; **/
    }



    /**
     * Sets the DataAccessSearchSortOrders to use for this criteria.
     * Previous DataAccessSearchSortOrder are overwritten.
     *
     * @param newSortOrders the initial column for this sort order
     */
    public void setSortOrder(NSArray newSortOrders)
    {
        /** require [valid_param] newSortOrders != null; **/
    	sortOrders = newSortOrders.mutableClone();
    }



    /**
     * Adds a new sort order as the last in the list, initializes it with the passed values, and returns it.
     *
     * @param aColumn the initial column for this sort order
     * @param aDirection the initial direction for this sort order
     * @return the newly created sort order
     */
    public DataAccessSearchSortOrder addSortOrder(Column aColumn, String aDirection)
    {
        /** require
        [valid_column] aColumn != null;
        [valid_direction] (aDirection != null) && DataAccessSearchSortOrder.ORDER_DIRECTIONS.containsObject(aDirection); **/

        DataAccessSearchSortOrder newSortOrder = newSortOrder();
        newSortOrder.setSortColumn(aColumn);
        newSortOrder.setSortDirection(aDirection);

        return newSortOrder;

        /** ensure
        [valid_result] Result != null;
        [criterion_linked] Result.searchCriteria().equals(this);
        [in_sort_orders] sortOrders().lastObject().equals(Result);
        [column_set] Result.sortColumn() == aColumn;
        [direction_set] Result.sortDirection() == aDirection; **/
    }



    /**
     * Removes all criteria.
     */
    public void removeAllCriteria()
    {
        searchCriteria.removeAllObjects();
        /** ensure [all_criteria_removed] ! hasCriteria(); **/
    }


    /**
     * Removes all sort orders.
     */
    public void removeAllSortOrders()
    {
        sortOrders.removeAllObjects();
        /** ensure [all_sort_orders_removed] ! hasSortOrders(); **/
    }



    /**
     * Removes all criteria in the passed list.  If the first one is removed, the join condition is removed from new first one.
     *
     * @param criteriaToRemove the criteria to remove
     */
    public void removeCriteriaInArray(NSArray criteriaToRemove)
    {
        /** require [valid_param] criteriaToRemove != null; **/

        searchCriteria.removeObjectsInArray(criteriaToRemove);

        // Since the first criterion might be have been removed, we need to make sure that it doesn't have a join condition
        if (hasCriteria())
        {
            criterionAtIndex(0).setJoinCondition(null);
        }

        /** ensure
        [join_condition_removed] ( ! hasCriteria()) || (criterionAtIndex(0).joinCondition() == null);
        [criteria_removed] (forall i : {0 .. criteriaToRemove.count() - 1} # ! searchCriteria().containsObject(criteriaToRemove.objectAtIndex(i))); **/
    }



    /**
     * Removes all sort orders in the passed list.
     *
     * @param sortOrdersToRemove the sort orders to remove
     */
    public void removeSortOrdersInArray(NSArray sortOrdersToRemove)
    {
        /** require [valid_param] sortOrdersToRemove != null; **/

        sortOrders.removeObjectsInArray(sortOrdersToRemove);

        /** ensure
        [sort_orders_removed] (forall i : {0 .. sortOrdersToRemove.count() - 1} # ! sortOrders().containsObject(sortOrdersToRemove.objectAtIndex(i))); **/
    }



    /**
     * Returns <code>true</code> if the given sort order is the first in the array, <code>false</code> otherwise.
     *
     * @param sortOrder the sort order to check
     * @return <code>true</code> if the given sort order is the first in the array, <code>false</code> otherwise
     */
    public boolean isFirstSortOrder(DataAccessSearchSortOrder sortOrder)
    {
        return hasSortOrders() && sortOrders().objectAtIndex(0).equals(sortOrder);
    }


    /**
     * Returns <code>true</code> if the given criterion is the first in the array, <code>false</code> otherwise.
     *
     * @param criterion the criterion to check
     * @return <code>true</code> if the given criterion is the first in the array, <code>false</code> otherwise
     */
    public boolean isFirstCriterion(DataAccessSearchCriterion criterion)
    {
        return hasCriteria() && searchCriteria().objectAtIndex(0).equals(criterion);
    }



    /**
     * Returns <code>true</code> if there are any criteria, <code>false</code> otherwise.
     *
     * @return <code>true</code> if there are any criteria, <code>false</code> otherwise
     */
    public boolean hasCriteria()
    {
        return criteriaCount() > 0;
    }


    /**
     * Returns <code>true</code> if there are any criteria, <code>false</code> otherwise.
     *
     * @return <code>true</code> if there are any criteria, <code>false</code> otherwise
     */
    public boolean hasCalculatedCriteria()
    {
        return criteriaCount() > 0;
    }


    /**
     * Returns the number of criteria.
     *
     * @return the number of criteria
     */
    public int criteriaCount()
    {
        return searchCriteria().count();
    }



    /**
     * Returns <code>true</code> if there are any sort orders, <code>false</code> otherwise.
     *
     * @return <code>true</code> if there are any sort orders, <code>false</code> otherwise
     */
    public boolean hasSortOrders()
    {
        return sortOrderCount() > 0;
    }



    /**
     * Returns <code>true</code> if there are any sort orders using the same column as the given column (except for the <code>exceptSortOrder</code>, which is excluded from the search so that the caller doesn't match itself), <code>false</code> otherwise.  We cannot use the column from the <code>exceptSortOrder</code> because that might not have been set yet (this method is used during validation).
     *
     * @param column the column to check
     * @param exceptSortOrder the sort order to exclude from the search (so the caller doesn't match itself)
     * @return <code>true</code> if there are any sort orders using the same column as the given sort order, <code>false</code> otherwise
     */
    public boolean hasSortOrderWithSameColumnAs(Column column, DataAccessSearchSortOrder exceptSortOrder)
    {
        /** require [valid_column_param] column != null; [valid_exceptSortOrder_param] exceptSortOrder != null; **/

        boolean hasSortOrderWithSameColumnAs = false;
        Enumeration sortOrderEnumerator = sortOrderEnumerator();
        while (sortOrderEnumerator.hasMoreElements())
        {
            DataAccessSearchSortOrder aSortOrder = (DataAccessSearchSortOrder)sortOrderEnumerator.nextElement();
            if ((aSortOrder != exceptSortOrder) && (aSortOrder.sortColumn() != null) && (aSortOrder.sortColumn().equals(column)))
            {
                hasSortOrderWithSameColumnAs = true;
                break;
            }
        }
        return hasSortOrderWithSameColumnAs;
    }


    /**
     * Returns the number of DataAccessSearchSortOrder.
     *
     * @return the number of DataAccessSearchSortOrder
     */
    public int sortOrderCount()
    {
        return sortOrders.count();
    }



    /**
     * Returns <code>true</code> if there is criteria at the passed index, <code>false</code> otherwise.
     *
     * @param index the criteria index to check
     * @return <code>true</code> if there is a criterion at <code>index</code>, <code>false</code> otherwise
     */
    public boolean hasCriterionAtIndex(int index)
    {
        return searchCriteria().count() > index;
    }


    /**
     * Returns the criteria at the passed index.
     *
     * @param index the index of the criteria index to return
     * @return the criteria at the passed index
     */
    public DataAccessSearchCriterion criterionAtIndex(int index)
    {
        /** require [has_criterion] hasCriterionAtIndex(index); **/
        return (DataAccessSearchCriterion)searchCriteria().objectAtIndex(index);
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the index of the passed DataAccessSearchCriterion, or -1 if it is not known.
     *
     * @param criterion the DataAccessSearchCriterion to return the index for
     * @return the index of the passed DataAccessSearchCriterion, or -1 if it is not known
     */
    public int indexOfCriterion(DataAccessSearchCriterion criterion)
    {
        /** require [valid_criterion] criterion != null; **/
        return searchCriteria().indexOfObject(criterion);
    }



    /**
     * Returns an Enumeration to iterate over the criteria in order
     *
     * @return an Enumeration to iterate over the criteria in order
     */
    public Enumeration criterionEnumerator()
    {
        return searchCriteria().objectEnumerator();
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns <code>true</code> if there is a sort order at the passed index, <code>false</code> otherwise.
     *
     * @param index the sort order index to check
     * @return <code>true</code> if there is a sort order at <code>index</code>, <code>false</code> otherwise
     */
    public boolean hasSortOrderAtIndex(int index)
    {
        return sortOrders().count() > index;
    }


    /**
     * Returns the DataAccessSearchSortOrder at the passed index.
     *
     * @param index the index of the criteria index to return
     * @return the DataAccessSearchSortOrder at the passed index
     */
    public DataAccessSearchSortOrder sortOrderAtIndex(int index)
    {
        /** require [has_sort_order] hasSortOrderAtIndex(index); **/
        return (DataAccessSearchSortOrder)sortOrders().objectAtIndex(index);
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns an Enumeration to iterate over the DataAccessSearchSortOrders in order
     *
     * @return an Enumeration to iterate over the DataAccessSearchSortOrders in order
     */
    public Enumeration sortOrderEnumerator()
    {
        return sortOrders().objectEnumerator();
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns a qualifier created from the search criteria.
     *
     * @param currentUser the User for VirtualUserField comparisons
     * @return a qualifier created from the search criteria
     */
    public EOQualifier searchQualifier(User currentUser)
    {
        /** require [valid_user] currentUser != null; **/

        EOQualifier qualifier = null;
        Enumeration criterionEnumerator = criterionEnumerator();
        while (criterionEnumerator.hasMoreElements())
        {
            DataAccessSearchCriterion searchCriterion = (DataAccessSearchCriterion) criterionEnumerator.nextElement();

            try
            {
                EOQualifier tempQualifier = searchCriterion.qualifier(currentUser);

                // Invert the qualifier if necessary
                String joinCondition = searchCriterion.joinCondition();
                if ((joinCondition != null) && (joinCondition.equals(DataAccessSearchCriterion.AND_NOT_JOIN_CONDITION) ||
                                                joinCondition.equals(DataAccessSearchCriterion.OR_NOT_JOIN_CONDITION)))
                {
                	tempQualifier = new EOAndQualifier(new NSArray(new Object[] {new EONotQualifier(tempQualifier),
                			EOQualifier.qualifierWithQualifierFormat(searchCriterion.column().normalizedName() + " != %@", new NSArray(NSArray.NullValue))}));
                }

                // The qualifier will only be null the first time through... the join condition should also be null the first time
                if (qualifier == null)
                {
                    /** check [no_join_condition] joinCondition == null; **/
                    qualifier = tempQualifier;
                }
                else if (joinCondition.equals(DataAccessSearchCriterion.AND_JOIN_CONDITION) ||
                         joinCondition.equals(DataAccessSearchCriterion.AND_NOT_JOIN_CONDITION))
                {
                    qualifier = new EOAndQualifier(new NSArray(new Object[] {qualifier, tempQualifier}));
                }
                else if (joinCondition.equals(DataAccessSearchCriterion.OR_JOIN_CONDITION) ||
                         joinCondition.equals(DataAccessSearchCriterion.OR_NOT_JOIN_CONDITION))
                {
                    qualifier = new EOOrQualifier(new NSArray(new Object[] {qualifier, tempQualifier}));
                }
                else
                {
                    // Should never get here
                    throw new Error("Invalid join condition.");
                }
            }
            catch (Exception e)
            {
                throw new ExceptionConverter(e);
            }
        }

        DebugOut.println(20, "User search qualifier: " + qualifier);
        return qualifier;
    }



    /**
     * Returns the <code>DataAccessSearchSortOrder</code>s as an <code>NSArray</code> of <code>EOSortOrdering</code>s.
     *
     * @return an array of <code>EOSortOrdering</code>s
     */
    public NSArray sortOrdering()
    {
        NSMutableArray sortOrdering = new NSMutableArray();
        Enumeration sortOrderEnumerator = sortOrderEnumerator();
        while (sortOrderEnumerator.hasMoreElements())
        {
            DataAccessSearchSortOrder sortOrder = (DataAccessSearchSortOrder)sortOrderEnumerator.nextElement();
            sortOrdering.addObject(sortOrder.sortOrdering());
        }
        return sortOrdering;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns an ordered list of the search criteria.
     *
     * @return an ordered list of the search criteria
     */
    public NSArray searchCriteria()
    {
        return searchCriteria;
    }



    /**
     * Returns an ordered list of the DataAccessSearchSortOrder
     *
     * @return an ordered list of the DataAccessSearchSortOrder
     */
    public NSArray sortOrders()
    {
        return sortOrders;
    }



    /**
     * Returns the DataAccess PageComponent for the section this search is being performed in.
     *
     * @return the DataAccess PageComponent for the section this search is being performed in
     */
    public DataAccess dataAccessComponent()
    {
        return dataAccessComponent;
    }



    /**
     * Returns a user-readable string representation of this object.
     *
     * @return a user-readable string representation of this object
     */
    public String toString()
    {
        return "search criteria: " + searchCriteria + " -- sort orders: " + sortOrders;

        /** ensure [valid_result] Result != null; **/
    }



    /** invariant
    [has_searchCriteria] searchCriteria != null;
    [has_sortOrders] sortOrders != null; **/


}
