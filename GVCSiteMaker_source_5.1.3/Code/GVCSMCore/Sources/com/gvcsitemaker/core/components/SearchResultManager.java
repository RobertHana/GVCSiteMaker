// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.components;

import com.webobjects.foundation.*;


/**
 * SearchResultManager splits an array of objects into one or more arrays of equal size (with the exception of the last group which may be smaller if the number of objects being split is not an even multiple of the group size).  The resulting groups preserver the order in the original array.  SearchResultManager is intended to be used with UI components implementing page flipper functionality.  For example: <br>
 * [Previous] Page 1 of 7 [Next]
 * <br>
 * Page <u>1</u> <u>2</u> 3 <u>4</u> <u>5</u>
 * <br>
 * The objects being managed and the group size can be changed on the fly and the groups will be updated.
 */
public class SearchResultManager extends Object
{
    private int groupSize;
    private int currentGroupNumber;
    private NSMutableArray groups;
    private NSArray objectsToManage;

    public static final int defaultGroupSize = 10;

 

    /**
     * Preferred constructor.  Sets groupSize() to baseGroupSize.
     *
     * @param baseGroupSize the maximum new size to split the objects to manage into.
     */
    public SearchResultManager(int baseGroupSize)
    {
        super();

        /** require [valid_base_group_size] baseGroupSize >= 1; **/
        
        groups = new NSMutableArray();
        objectsToManage = new NSArray();
        groupSize = baseGroupSize;
        currentGroupNumber = 0;

        /** ensure [group_size_equals_base_group_size] groupSize == baseGroupSize; **/
    }



    /**
     * Default constructor.  Sets groupSize() to defaultGroupSize.
     */
    public SearchResultManager()
    {
        this(defaultGroupSize);
    }



    /**
     * Sets the maximum size to split objectsToManage() into.  Updates groups().
     *
     * @param newGroupSize the maximum new size to split objectsToManage() into.
     */
    public void setGroupSize(int newGroupSize)
    {
        /** require [valid_new_group_size] newGroupSize >= 1; **/
        
        groupSize = newGroupSize;
        updateGroups();

        /** ensure [correct_group_size] groupSize == newGroupSize;  **/
    }



    /**
     * Returns the maximum number of objects returned by objectsInGroup().
     *
     * @return the maximum number of objects in objectsInGroup().
     */
    public int groupSize()
    { 
        return groupSize;

        /** ensure [group_size_greater_than_zero] Result > 0; **/
    }



    /**
     * Sets the list of objects to split into and manage as groups.  Updates groups().
     *
     * @param newObjectsToManage the list of objects to split into and manage as groups .
     */
    public void setObjectsToManage(NSArray newObjectsToManage)
    {
        /** require [new_objects_to_manage_not_null] newObjectsToManage != null; **/

        objectsToManage = newObjectsToManage;
        updateGroups();

        /** ensure [objects_to_manage_equals_new_objects_to_manage] objectsToManage.equals(newObjectsToManage); **/
    }



    /**
     * Returns the whole list of objects being managed (not split into groups).
     *
     * @return the whole list of objects being managed (not split into groups).
     */
    public NSArray objectsToManage()
    {
        return objectsToManage;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the total number of objects in objectsToManage().
     *
     * @return the total number of objects in objectsToManage().
     */
    public int numberOfObjectsBeingManaged()
    {
        return objectsToManage().count();

        /** ensure [result_is_non_negative] Result >= 0; **/
    }



    /**
     * Returns <code>true</code> if there are search results to display.
     *
     * @return <code>true</code> if there are search results to display.
     */
    public boolean hasSearchResults()
    {
        return (numberOfObjectsBeingManaged() > 0);
    }


    
    /**
     * Returns the numbers of groups that objectsToManage() has been split into.
     *
     * @return the numbers of groups that objectsToManage() has been split into.
     */
    public int numberOfGroups()
    {
        return groups.count();

        /** ensure [result_is_non_negative] Result >= 0; **/
    }



    /**
     * Returns <code>true</code> if there are more than one group of search results.  This is to be used to hide the page flippers where there is only a single group..
     *
     * @return <code>true</code> if there are more than one group of search results.
     */
    public boolean hasMultipleGroups()
    {
        return numberOfGroups() > 1;
    }


    
    /**
     * Returns the number (e.g. index) of the group currently being returned by currentGroup().  Zero indicates that there are no groups.  This is returned as an Integer to make working with groupsNumbers easier.
     *
     * @return the number (e.g. index) of the group currently being returned by currentGroup().
     */
    public Integer currentGroupNumber()
    {
        return new Integer(currentGroupNumber);

        /** ensure
        [result_not_null] Result != null;
        [result_is_non_negative] Result.intValue() >= 0; **/
    }



    /**
     * Sets the number (e.g. index) of the group to be returned by currentGroup().
     *
     * @param newCurrentGroupNumber the index of the group to make the current group.
     */
    public void setCurrentGroupNumber(int newCurrentGroupNumber)
    {
        /** require
        [new_current_group_number_greater_than_zero] newCurrentGroupNumber >= 0;
        [valid_new_group_number] newCurrentGroupNumber <= numberOfGroups(); **/
        
        currentGroupNumber = newCurrentGroupNumber;
    }



    /**
     * Sets the number (e.g. index) of the group to be returned by currentGroup().
     *
     * @param newCurrentGroupNumber the index of the group to make the current group.
     */
    public void setCurrentGroupNumber(Integer newCurrentGroupNumber)
    {
        /** require
        [new_current_group_number_not_null] newCurrentGroupNumber != null;
        [new_current_group_number_greater_than_zero] newCurrentGroupNumber.intValue() >= 0;
        [valid_new_group_number] newCurrentGroupNumber.intValue() <= numberOfGroups(); **/
        
        currentGroupNumber = newCurrentGroupNumber.intValue();
    }



    /**
     * Returns an array of Integers 1..n where each number represents a group index.  This is intended to be used with a WORepetition to implement a page selector.
     *
     * @return an array of Integers 1..n where each number represents a group index.
     */
    public NSArray groupNumbers()
    {
        NSMutableArray groupNumbers = new NSMutableArray();
        int i;
        
        for (i = 1; i <= numberOfGroups(); i++)
        {
            groupNumbers.addObject(new Integer(i));
        }

        return groupNumbers;

        /** ensure
        [result_not_null] Result != null;
        [correct_number_of_groups] Result.count() == numberOfGroups(); **/
    }



    /**
     * Returns the number (e.g. index) of the first object in currentGroup(), relative to objectsBeingManaged().  This is intended for use in implementing displays like "Displaying results 16-20 of 28 that meet your criteria."  This is returned as an Integer to make working with the indexes easier.
     *
     * @return the number (e.g. index) of the first object in currentGroup(), relative to objectsBeingManaged().
     */
    public Integer indexOfFirstObjectInCurrentGroup()
    {
        return new Integer(objectsToManage().indexOfObject(currentGroup().objectAtIndex(0)) + 1);

        /** ensure
        [result_not_null] Result != null;
        [result_not_negative] Result.intValue() >= 0; **/
    }



    /**
     * Returns the number (e.g. index) of the last object in currentGroup(), relative to objectsBeingManaged().  This is intended for use in implementing displays like "Displaying results 16-20 of 28 that meet your criteria."  This is returned as an Integer to make working with the indexes easier.
     *
     * @return the number (e.g. index) of the last object in currentGroup(), relative to objectsBeingManaged().
     */
    public Integer indexOfLastObjectInCurrentGroup()
    {
        return new Integer(objectsToManage().indexOfObject(currentGroup().lastObject()) + 1);

        /** ensure
        [result_not_null] Result != null;
        [result_not_negative] Result.intValue() >= 0; **/
    }



    /**
     * Returns the group at the index currentGroup().  This is intended for use with a WORepetition to display the objects in the selected group.
     *
     * @return the group at the index currentGroup().
     */
    public NSArray currentGroup()
    {
        /** require [at_least_one_group_exists] numberOfGroups() > 0; **/
        
        return (NSArray)groups.objectAtIndex(currentGroupNumber().intValue() - 1);

        /** ensure
        [result_not_null] Result != null;
        [result_is_positive] Result.count() > 0; **/
    }



    /**
     * Returns the group at the index groupNumber.
     *
     * @return the group at the index groupNumber.
     */
    public NSArray group(int groupNumber)
    {
        /** require
        [group_number_positive] groupNumber > 0;
        [group_number_not_too_large] groupNumber <= numberOfGroups(); **/
        
        return (NSArray)groups.objectAtIndex(groupNumber - 1);

        /** ensure
        [result_not_null] Result != null;
        [result_is_positive] Result.count() > 0; **/
    }



    
    /**
     * Splits objectsToManage() into groups with a maximum size of groupSize().  All groups except the last will be of equal size.
     */
    protected void updateGroups()
    {
        groups = new NSMutableArray();

        NSRange range = new NSRange(0, 0);
        while (range.location() < numberOfObjectsBeingManaged())
        {
            if (((range.location() - 1) + groupSize()) < numberOfObjectsBeingManaged())
            {
                range = new NSRange(range.location(), groupSize());
            }
            else
            {
                range = new NSRange(range.location(), numberOfObjectsBeingManaged() % groupSize());
            }

            groups.addObject(objectsToManage().subarrayWithRange(range));
            range = new NSRange(range.location() + groupSize(), groupSize());
        }

        // Reset the current group number
        if (numberOfGroups() > 0)
        {
            setCurrentGroupNumber(1);
        }
        else  
        {
            setCurrentGroupNumber(0);
        }

        /** ensure
        [sucessful_update] ((numberOfGroups() == 0) && (numberOfObjectsBeingManaged() == 0)) ||
            ( ((numberOfGroups() - 1) * groupSize() + group(numberOfGroups()).count()) == numberOfObjectsBeingManaged());
        [correct_group_number] ((currentGroupNumber > 0) && (currentGroupNumber <= groups.count()) ) || (groups.count() == 0);  **/
    }



    // This invariant can not be enforced as Jass checks the invariant during internal method calls.  Thus this invariant fails during updateGroups().  It was moved there as a post condition.
    // [correct_group_number] ((currentGroupNumber > 0) && (currentGroupNumber <= groups.count()) ) || (groups.count() == 0);

    /** invariant
    [groups_not_null] groups != null;
    [objects_to_manage] objectsToManage != null;
    [group_size_at_least_one] groupSize >= 1;  **/
 }
