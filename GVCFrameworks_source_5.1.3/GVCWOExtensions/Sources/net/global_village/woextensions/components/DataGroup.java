package net.global_village.woextensions.components;

import java.util.*;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;



/**
 * A <code>DataGroup</code> organizes a sorted group of similar Enterprise Objects and is able to split it into sub-subgroups to model a hierarchy of EOs.  It is intended to be used as the model for drill down components.  Representative or aggregate values from the contained EOs of the <code>DataGroup</code> are accessed through Key-Value coding. Note that a <code>DataGroup</code> object only handles one level of hierarchy (other level are handled by other <code>DataGroups</code>).
 *
 * To create a <code>DataGroup</code>, you need to pass in the following:
 * <ul>
 * <li>data - an array of EOs to be managed by this <code>DataGroup</code>; all the <code>EOs</code> must be of the same type family, which means that all these EOs must respond to the same keypaths and compare methods passed during its creation)
 * <li>groupAttributeKeyPath - the attribute keyPath used for grouping this data; if you want to perform calculations for a specific attribute, use this format "@<aggregate function>.attribute", ie "@sum.hours", "@avg.hours"
 * <li>attributeKeyPaths - the ordered array of attribute keyPaths that will determine the groupAttributeKeyPath to be used to generate its subGroups; groupAttributeKeyPath of this DataGroup must also belong to attributeKeyPaths;
 * <li>comparators - the ordered array of NSComparator instances that will be used when generating the subGroups; it is expected that the NSComparator at a particular index will result in objects which are ordered in a sensible way with respect to the attributeKeyPath at the matching index. (WHAT DOES THIS MEAN? - ch: If they do not match, more than one group with the same title will be created.)
 * </ul>
 *
 * Below is an example that creates a <code>DataGroup</code> for the array of EOCustomObjects, the arrayOfWorkDetails().  It is created for the attribute key path "project.name" which is the first level of the hiearchy. From these param values, this <code>DataGroup's subGroup</code> will contain dataGroups for "taskArea.area.name", which is the next attribute key path from the <code>attributeKeyPaths</code> array.:<br>
 * <code>
 *     DataGroup newDataGroup = new DataGroup("project.name",
 *                                             arrayOfWorkDetails(),
 *                                             new Array(new Object[] {"project.name", "taskArea.area.name", "taskArea.task.name", "user.fullName", "@sum.hours"}),
 *                                             new Array(new Object[] {"compareByProject", "compareByArea", "compareByTask", "compareByUser", "compareByHours"})),
 * </code>
 *
 * Note that a DataGroup does not require valid attributeKeyPaths for it to function properly, you can pass in a dummy attribute key path like "$dummyAttribute" to initialize your DataGroups. For example, if you want to create the root DataGroup for an array of workDetails with the "project.name" as the first level, you can create it using this...
 *
 * <code>
 *     DataGroup newDataGroup = new DataGroup("$dummyAttribute%",
 *                                             arrayOfWorkDetails(),
 *                                             new Array(new Object[] {"$dummyAttribute%", "project.name", "taskArea.area.name", "taskArea.task.name", "user.fullName", "@sum.hours"}),
 *                                             new Array(new Object[] {"$dummyCompare%", "compareByProject", "compareByArea", "compareByTask", "compareByUser", "compareByHours"})),
 * </code>
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 6$
 *
 */
public class DataGroup extends Object
{
    protected String groupAttributeKeyPath;
    protected NSArray data;
    protected NSArray attributeKeyPaths;
    protected NSArray comparators;
    protected NSMutableDictionary cachedValues;
    protected NSMutableArray cachedSubGroups;
    protected boolean isExpanded;

    
    /**
     * Designated constructor.
     *
     * @param theGroupAttributeKeyPath the attribute keyPath used for grouping this data
     * @param theData the EOCustomObjects to be managed by this DataGroup
     * @param theAttributeKeyPaths the ordered array of attribute keyPaths that will determine the groupAttributeKeyPath to be used to generate its subGroups
     * @param theComparators the ordered array of NSComparators that will be used when generating the subGroups
     */
    public DataGroup(String theGroupAttributeKeyPath,
                     NSArray theData,
                     NSArray theAttributeKeyPaths,
                     NSArray theComparators)
    {
        super();
        /** require
        [valid_theGroupAttributeKeyPath_param] theGroupAttributeKeyPath != null;
        [valid_theData_param] theData != null;
        [has_data] theData.count() > 0;
        [all_data_are_EOCustomObject] forall i : {0 .. theData.count() - 1} # theData.objectAtIndex(i) instanceof EOCustomObject;
        [valid_theAttributeKeyPaths_param] theAttributeKeyPaths != null;
        [attribute_keypaths_contain_group_keypath] theAttributeKeyPaths.containsObject(theGroupAttributeKeyPath);
        [valid_theComparators_param] theComparators != null;
        [has_comparator_for_each_keypath] theAttributeKeyPaths.count() == theComparators.count(); **/

        groupAttributeKeyPath = theGroupAttributeKeyPath;
        data = theData;
        attributeKeyPaths = theAttributeKeyPaths;
        comparators = theComparators;
        
        isExpanded = false;
        cachedValues = new NSMutableDictionary();
        cachedSubGroups = null;
    }



    /**
     * The array that contains the data for this group.
     */
    public synchronized NSArray data()
    {
        return data;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Sets this group's data with the newData.
     */
    public synchronized void setData(NSArray newData)
    {
        /** require [valid_param] newData != null; **/

        if ((data == null) || ( ! data.isEqualToArray(newData)))
        {
            data = newData;

            // Discard old cached data.
            setCachedValues(new NSMutableDictionary());
            setCachedSubGroups(null);
        }

        /** ensure [data_changed] data.isEqualToArray(newData); **/
    }



    /**
     * Returns the keyPath to be displayed by this group.
     */
    public String groupAttributeKeyPath()
    {
        return groupAttributeKeyPath;
    }



    /**
     * Sets the group key path with aValue
     */
    public void setGroupAttributeKeyPath(String aValue)
    {
        /** require [valid_param] aValue != null;**/

        groupAttributeKeyPath = aValue;

        /** ensure [group_attribute_keypath_changed] groupAttributeKeyPath.equals(aValue); **/
    }



    /**
     * Returns the dictionary that contains the keys and values derived from the data.  The key contains the keyPath and the value contains is corresponding value from data.
     */
    public NSMutableDictionary cachedValues()
    {
        return cachedValues;

        /** ensure [valid_result] Result != null; **/
    }



    public void setCachedValues(NSMutableDictionary aValue)
    {
        cachedValues = aValue;
    }



    /**
     * Returns the object that corresponds to <code>aKeyPath</code> in the <code>data</code> array if aKeyPath is in attributeKeyPaths(), or from the <code>dataGroup</code> otherwise.
     *
     * @param aKeyPath the attribute key path of the value to look for
     * @return the object that corresponds to <code>aKeyPath</code> in the <code>data</code> array.
     */
    public Object valueForKeyPath(String aKeyPath)
    {
        /** require [valid_param] aKeyPath != null; **/

        Object theObject = NSKeyValueCoding.NullValue;

        if (attributeKeyPaths().containsObject(aKeyPath))
        {
            theObject = cachedValues().objectForKey(aKeyPath);

            if (theObject == null)
            {
               //try to get the value from data and add it to the dictionary...
                
                if (aKeyPath.indexOf("@") == -1)             //if aKeyPath does not contain an "@" sign
                {
                    theObject = ((EOCustomObject)data.objectAtIndex(0)).valueForKeyPath(aKeyPath);
                }
                else
                {
                    theObject = data.valueForKey(aKeyPath);                    
                }

                cachedValues().setObjectForKey(theObject, aKeyPath);
            }
        }
        // We need to defer to super so that we can use binding in WOComponents!
        else
        {
            theObject = valueForKeyPath(aKeyPath);
        }

        return theObject;

        /** ensure [valid_result] Result != null; **/
    }


    /**
     * Returns <code>true</code> if this data group contains subGroups, <code>false</code> otherwise.
     *
     * @return <code>true</code> if this data group contains subGroups, <code>false</code> otherwise.
     */
    public boolean hasSubGroups()
    {
        return ( ! isLastGroup()) || (nextAttributeKeyPath().indexOf("@") != -1);
    }



    /**
     * Returns <code>true</code> if this data group is the last group, <code>false</code> otherwise.
     *
     * @return <code>true</code> if this data group is the last group, <code>false</code> otherwise.
     */
    public boolean isLastGroup()
    {
        return attributeKeyPaths().lastObject().equals(groupAttributeKeyPath());
    }



    /**
     * Returns the group attribute key path for this <code>datagroup's subGroup</code>
     */
    public String nextAttributeKeyPath()
    {
        /** require [is_not_last_group] ! isLastGroup(); **/

        return (String) attributeKeyPaths().objectAtIndex(attributeKeyPaths().indexOfObject(groupAttributeKeyPath()) + 1);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * An accessor for cachedSubGroups.
     *
     * @return an array of dataGroups
     */
    public NSArray subGroups()
    {
        /** NOTrequire [is_expanded] isExpanded(); **/

        return cachedSubGroups();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns <code>true</code> if this data group is displayed as expanded or collapsed.
     */
    public boolean isExpanded()
    {
        return isExpanded;
    }



    /**
     * Toggles the value of <code>isExpanded</code>.  It generates the <code>cachedSubGroups</code> if necessary.
     */
    public void toggleExpansion()
    {
        /** require [has_subgroups] hasSubGroups(); **/

        isExpanded = ( ! isExpanded());

        if ((isExpanded()) && (cachedSubGroups() == null))
        {
            String attributeKeyPathForSubGroup = nextAttributeKeyPath();
            NSComparator comparatorForSubGroup = (NSComparator)comparators.objectAtIndex(attributeKeyPaths.indexOfObject(attributeKeyPathForSubGroup));

            //Sort data() using comparator, converting any comparison exceptions to runtime ones.
            NSArray sortedObjects = NSArrayAdditions.sortedArrayUsingComparator(data(), comparatorForSubGroup);    
            
            //Iterate through sortedObjects
            String keyPathToBeUsedForGrouping = attributeKeyPathForSubGroup;
            Object valueCommonToGroup = null;
            NSMutableArray groupedData = new NSMutableArray();
            cachedSubGroups = new NSMutableArray();
                
            Enumeration objectEnum = sortedObjects.objectEnumerator();
            while (objectEnum.hasMoreElements())
            {
                EOCustomObject anObject = (EOCustomObject) objectEnum.nextElement();

                if (valueCommonToGroup == null)
                {
                    valueCommonToGroup = anObject.valueForKeyPath(keyPathToBeUsedForGrouping);
                }

                if ( ! anObject.valueForKeyPath(keyPathToBeUsedForGrouping).equals(valueCommonToGroup))
                {

                    DataGroup newDataGroup = new DataGroup(keyPathToBeUsedForGrouping,
                                                           groupedData,
                                                           attributeKeyPaths(),
                                                           comparators());
                    cachedSubGroups.addObject(newDataGroup);
                    groupedData = new NSMutableArray();
                    valueCommonToGroup = anObject.valueForKeyPath(keyPathToBeUsedForGrouping);
                }
                groupedData.addObject(anObject);
            }
            if (groupedData.count() > 0)
            {
                DataGroup newDataGroup = new DataGroup(keyPathToBeUsedForGrouping,
                                                       groupedData,
                                                       attributeKeyPaths(),
                                                       comparators());
                cachedSubGroups.addObject(newDataGroup);
            }
        }

        /** ensure [is_not_expanded_or_has_cached_subgroups] ((isExpanded() && (cachedSubGroups() != null))) || (! isExpanded()); **/
    }



    /**
     * Recurse through all the data group's subGroups and return them in a one-level array.
     */
    public NSArray allSubGroups()
    {
        /** require [has_subgroups] hasSubGroups(); **/

        NSMutableArray visibleDataGroups = new NSMutableArray();

        Enumeration subGroupEnumeration = subGroups().objectEnumerator();
        while (subGroupEnumeration.hasMoreElements())
        {
            DataGroup currentDataGroup = (DataGroup)subGroupEnumeration.nextElement();
            visibleDataGroups.addObject(currentDataGroup);
            if (currentDataGroup.isExpanded())
            {
                visibleDataGroups.addObjectsFromArray(currentDataGroup.allSubGroups());    
            }
        }

        return visibleDataGroups;

        /** ensure [is_not_expanded_or_has_cached_subgroups] ((isExpanded() && (cachedSubGroups() != null))) || (! isExpanded()); **/
    }



    public NSArray attributeKeyPaths()
    {
        return attributeKeyPaths;
    }



    public void setAttributeKeyPaths(NSArray aValue)
    {
        attributeKeyPaths = aValue;
    }



    public NSArray comparators()
    {
        return comparators;
    }



    public void setComparators(NSArray aValue)
    {
        comparators = aValue;
    }



    public NSMutableArray cachedSubGroups()
    {
        return cachedSubGroups;
    }



    public void setCachedSubGroups(NSMutableArray value)
    {
        cachedSubGroups = value;
    }



    public void expandAll()
    {
        if (hasSubGroups() && (nextAttributeKeyPath().indexOf("@") == -1))
        {
            toggleExpansion();

            Enumeration subGroupEnum = subGroups().objectEnumerator();
            while (subGroupEnum.hasMoreElements())
            {
                DataGroup aSubGroup = (DataGroup) subGroupEnum.nextElement();
                aSubGroup.expandAll();
            }
        }
    }

    /** invariant
      [groupAttributeKeyPath_not_null] groupAttributeKeyPath != null;	
      [data_not_null] data != null;
      [has_data] data.count() > 0;
      [attributeKeyPaths_not_null] attributeKeyPaths != null;
      [groupAttributeKeyPath_in_attributeKeyPaths] attributeKeyPaths.containsObject(groupAttributeKeyPath);
      [comparators_not_null] comparators != null;
      [attributeKeyPaths_matches_comparators_count] (attributeKeyPaths().count() == comparators().count());
   **/
}


