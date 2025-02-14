// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.core.pagecomponent;

import java.util.*;

import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import er.extensions.foundation.*;


/*
 * * Layout extends PageComponent by introducing the concept of an ordered list
 * of owned child PageComponents. It provides methods for maintaining this
 * ordering and working with the list of ordered child PageComponents. It
 * introduces the binding componentOrder (accessed as componentOrderDictionary)
 * which maintains the order of child components. Layout should be used where a
 * PageComponent needs to control other PageComponents and the ordering of those
 * PageComponents is important. <br><br> The componentOrder binding is a
 * dictionary linking componentIDs (as strings) to order relative to other
 * components, e.g 1000256 = 0003. The advantage of using componentID as the key
 * into the dictionary is that it allows fast lookup of the relative ordering
 * for a component. If the dictionary were inverted, we would need to scan
 * through toChildren to find each component. <br><br> <b>Note</b>: The
 * orderings in componentOrder should be considered relative rathern than
 * absolute. This is due to bugs and bad coding in the past, and should only
 * apply to Text / Image layouts at the University of Michigan. There is no
 * guarantee that they start with 1 or are contiguous. One based, contiguous
 * orderings are enforced as of GVC.SiteMaker 2.5. <br><br> Layout is not
 * intended to be directly instantiated. It provides service methods for
 * sub-classes to use.
 */
public class Layout extends _Layout
{
    // Binding keys
    public static final String COMPONENT_ORDER_BINDINGKEY = "componentOrder";


    /**
     * Overriden to supply default binding values.
     *
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setComponentType("Layout");

        // Ensure that bindings contains the required binding COMPONENT_ORDER_BINDINGKEY
        if (!hasValueForBindingKey(COMPONENT_ORDER_BINDINGKEY))
        {
            setComponentOrderDictionary(NSDictionary.EmptyDictionary);
        }
    }



    /**
     * Returns a deep copy of this Layout page component.  The component order dictionary is updated to reflect the newly copied objects.
     *
     * @param copiedObjects - the copied objects keyed on the EOGlobalID of the object the copy was made from.
     * @return a copy of this Layout
     */
    public EOEnterpriseObject duplicate(NSMutableDictionary copiedObjects)
    {
        /** require [copiedObjects_not_null] copiedObjects != null;  **/

        Layout newComponent = (Layout) super.duplicate(copiedObjects);

        // Update the component order for each child.  The compoent order dictionary is keyed on primary key.  We need to update the PK from that of the PageComponent we are copying to that of the copy.
        NSMutableDictionary coDict = new NSMutableDictionary(newComponent.componentOrderDictionary());
        for (int i = 0; i < toChildren().count(); i++)
        {
            PageComponent aChild = (PageComponent) toChildren().objectAtIndex(i);
            PageComponent aChildCopy = (PageComponent) aChild.copy(copiedObjects);

            aChildCopy.generatePrimaryKey();

            String oldPrimaryKey = aChild.componentIdAsString();
            String newPrimaryKey = aChildCopy.componentIdAsString();

            // oldPrimaryKey might be an NSData, but the values in the bindings dictionary might be legacy integer values
            if (coDict.objectForKey(oldPrimaryKey) == null && ( ! SMApplication.smApplication().isUsingIntegerPKs()))
            {
                oldPrimaryKey = oldPrimaryKey.replaceFirst("0+", "");
            }

            if (coDict.objectForKey(oldPrimaryKey) != null)
            {
                String theValue = (String) coDict.objectForKey(oldPrimaryKey);
                coDict.removeObjectForKey(oldPrimaryKey);
                coDict.setObjectForKey(theValue, newPrimaryKey);
            }
        }
        newComponent.setComponentOrderDictionary(coDict);

        return newComponent;

        /** ensure [copy_made] Result != null;   **/
    }



    /**
     * Orders the the child components, toChildren(), and updates the componentOrderDictionary to reflect this ordering.  This method is here to establish an API for sub-classes.  Sub-classes should probably <b>not</b> call super.layoutComponents() as Layout's implementation may conflict with theirs.
     * <br><br>
     * For Layout it is not necessary to call this method if child components are always added by calling addChild().  This method should be called if addToChildren() is called directly.  The existing component order is preserved and any components not already ordered are added to the end of the list in an arbitrary order.
     */
    public void layoutComponents()
    {
        NSMutableArray orderedComponents = new NSMutableArray(orderedComponents());

        // Check each child component
        Enumeration componentEnumerator = toChildren().objectEnumerator();
        while (componentEnumerator.hasMoreElements())
        {
            PageComponent aComponent = (PageComponent) componentEnumerator.nextElement();

            // Add it to the end of the list if it is not in the ordered list
            if (!orderedComponents.containsObject(aComponent))
            {
                orderedComponents.addObject(aComponent);
            }
        }

        setComponentOrderFrom(orderedComponents);

        /** ensure [correct_number_of_components] orderedComponents().count() == toChildren().count(); **/
    }



    /**
     * Returns the children of this layout (the components whose layout it is controlling) in the order they should be displayed.  This is one of the core methods of LayoutComoponent (the other is setComponentOrderFrom(NSArray)).
     *
     * @return the children of this layout (the components whose layout it is controlling) in the order they should be displayed.
     */
    public NSArray orderedComponents()
    {
        // STEP 1.  We start with a dictionary whose keys are componentIDs (as strings) and whose values are that component's display order.  For example, 1000256 = 0003; indicates that the PageComponent instance with a componentId of 1000256 is to be third in the list of components to be displayed.  The first thing we do is to translate this string componentID into an actual PageComponent instance and then use those instances to create a new, inverted dictionary whose keys are a component's display order and whose objects are the actual component instance, e.g. 0003 = <Image instance with componentId of 1000256>;.
        NSMutableDictionary componentsByOrder = new NSMutableDictionary();
        NSDictionary componentOrderDictionary = componentOrderDictionary();

        DebugOut.println(30, "toChildren.count() is " + toChildren().count());
        DebugOut.println(30, "componentOrderDictionary.count() is " + componentOrderDictionary.count());
        //DebugOut.println(60, "componentOrderDictionary is " + componentOrderDictionary);

        // Look up each child in the componentOrderDictionary to determine its ordering.
        Enumeration componentEnum = toChildren().objectEnumerator();
        while (componentEnum.hasMoreElements())
        {
            PageComponent aComponent = (PageComponent) componentEnum.nextElement();
            String orderKey = (String)componentOrderDictionary.objectForKey(aComponent.componentIdAsString());

            // componentId might be an NSData, but the values in the bindings dictionary might be legacy integer values
            if (orderKey == null && ( ! SMApplication.smApplication().isUsingIntegerPKs()))
            {
                String stringPK = aComponent.componentIdAsString().replaceFirst("0+", "");
                orderKey = (String)componentOrderDictionary.objectForKey(stringPK);
            }

            if (orderKey != null)
            {
                componentsByOrder.setObjectForKey(aComponent, orderKey);
            }
            // else this component is not in the order dictionary.  This is not reported
            // as an error as this method is called from layoutComponents before the missing
            // components are added.
        }

        DebugOut.println(30, "componentsByOrder.count() is " + componentsByOrder.count());
        //DebugOut.println(60, "componentsByOrder is " + componentsByOrder);


        // STEP 2. Now we have a dictionary whose keys are the display order of each component instance.  The ordering of keys from dictionary's keyEnumerator() is indeterminate so we need to create a sorted list of the keys so we can enumerate the components in the display order.
        NSArray componentOrder = componentsByOrder.allKeys();
        componentOrder = ArrayExtras.sortedArrayUsingComparator(componentOrder, NSComparator.AscendingStringComparator);

        DebugOut.println(30, "componentOrder has " + componentOrder.count() + " items");
        //DebugOut.println(60,"componentOrder is " + componentOrder);


        // STEP 3. Now we have a dictionary whose keys are a component's display order (and whose objects are the actual component instances) and a sorted list of keys indicating the actual order.  To create the list of child components in the correct order we iterate over the ordered keys, look up the component with that order, and add it to the end of an array.
        Enumeration orderEnumerator = componentOrder.objectEnumerator();
        NSMutableArray orderedComponents = new NSMutableArray();
        while (orderEnumerator.hasMoreElements())
        {
            orderedComponents.addObject(componentsByOrder.objectForKey(orderEnumerator.nextElement()));
        }

        return orderedComponents;

        /** ensure [result_not_null] Result != null;   **/
    }



    /**
     * Creates the componentOrderDictionary based on the passed array of PageComponents, maintaining their order in the array.  This is one of the core methods of LayoutComoponent (the other is orderedComponents()).
     *
     * @param orderedComponents - ordered list of all components in toChildren() where position in the list indicated display order.
     */
    public void setComponentOrderFrom(NSArray orderedComponents)
    {
        /** require
        [ordered_components_not_null] orderedComponents != null;
        [correct_number_of_components] toChildren().count() == orderedComponents.count(); **/

        Enumeration componentEnumerator = orderedComponents.objectEnumerator();
        NSMutableDictionary newComponentOrderDictionary = new NSMutableDictionary();
        int order = 1;

        while (componentEnumerator.hasMoreElements())
        {
            PageComponent aComponent = (PageComponent) componentEnumerator.nextElement();
            newComponentOrderDictionary.setObjectForKey(zeroPad(order), aComponent.componentIdAsString());
            order++;
        }

        setComponentOrderDictionary(newComponentOrderDictionary);
    }



    /**
     * Returns the value n as a String, zero padded on the left to be exactly four characters long.
     * This is used with componentOrderDictionary so that the ordering sort as numeric even though they are String.
     *
     * @return the value n as a String, zero padded on the left to be exactly four characters long
     */
    protected String zeroPad(int n)
    {
        /** require [n_in_range] ((n > 0) && (n <= 9999)); **/

        String s = "0000" + new Integer(n).toString();

        return s.substring(s.length() - 4);

        /** ensure
        [result_not_null] Result != null;
        [result_correct_length] Result.length() == 4;
        [result_still_equals_n] Integer.parseInt(Result) == n;  **/
    }



    /**
     * Adds aComponent to the toChildren relationship and maintains the component order by placing it at the end of the list.
     *
     * @param aComponent - the PageComponent to add to the toChildren relationship
     */
    public void addChild(PageComponent aComponent)
    {
        /** require
        [valid_param] aComponent != null;
        [component_not_added] ! toChildren().containsObject(aComponent); **/

        super.addChild(aComponent);
        aComponent.generatePrimaryKey();
        DebugOut.println(20, "Adding child " + aComponent);

        // Add the child to the end of the orderedComponents list and update the component order from this list
        NSMutableArray orderedComponents = new NSMutableArray(orderedComponents());
        orderedComponents.addObject(aComponent);
        setComponentOrderFrom(orderedComponents);

        /** ensure [component_added] toChildren().containsObject(aComponent);  **/

    }



    /**
     * Removes aComponent from the toChildren relationship and maintains the component order by removing it from the list.
     *
     * @param aComponent the PageComponent to remove from the toChildren relationship
     * @param delete should the child be deleted
     */
    public void removeChild(PageComponent aComponent, boolean delete)
    {
        /** require
        [a_component_not_null] aComponent != null;
        [a_component_is_child] toChildren().containsObject(aComponent); **/

        DebugOut.println(20, "Removing child " + aComponent);
        removeObjectFromBothSidesOfRelationshipWithKey(aComponent, "toChildren");
        if (delete)
        {
            editingContext().deleteObject(aComponent);
        }

        // Remove the child from the orderedComponents list and update the component order from this list
        NSMutableArray orderedComponents = new NSMutableArray(orderedComponents());
        orderedComponents.removeObject(aComponent);
        setComponentOrderFrom(orderedComponents);

        /** ensure [no_longer_a_child] ! toChildren().containsObject(aComponent); **/
    }



    /**
     * Removes aComponent from the toChildren relationship and maintains the component order by removing it from the list.
     *
     * @param aComponent the PageComponent to remove from the toChildren relationship
     */
    public void removeChild(PageComponent aComponent)
    {
        /** require
        [a_component_not_null] aComponent != null;
        [a_component_is_child] toChildren().containsObject(aComponent); **/

        removeChild(aComponent, true);

        /** ensure [no_longer_a_child] ! toChildren().containsObject(aComponent); **/
    }



    /**
     * Removes childrenToRemove from the toChildren relationship and maintains the component order by removing it from the list.
     *
     * @param childrenToRemove the PageComponents to remove from the toChildren relationship
     */
    public void removeChildren(NSArray childrenToRemove)
    {
        /** require [valid_param] childrenToRemove != null; **/

        Enumeration childrenToRemoveEnumerator = childrenToRemove.objectEnumerator();
        while (childrenToRemoveEnumerator.hasMoreElements())
        {
            PageComponent child = (PageComponent) childrenToRemoveEnumerator.nextElement();
            removeChild(child);
        }

        /** ensure [children_removed] (forall i: {0 .. childrenToRemove.count() - 1} # ! toChildren().containsObject(childrenToRemove.objectAtIndex(i))); **/
    }



    /**
     * Replace childToReplace with replacement, deleting iff deleteOldChild is true.
     *
     * @param childToReplace the PageComponent to replace
     * @param replacement the replacement for the childToReplace
     * @param deleteOldChild should the old child be deleted
     */
    public void replaceChild(PageComponent childToReplace, PageComponent replacement, boolean deleteOldChild)
    {
        /** require
        [a_component_not_null] childToReplace != null;
        [a_component_is_child] toChildren().containsObject(childToReplace);
        [replacement_not_null] replacement != null; **/

        DebugOut.println(20, "Replacing child " + childToReplace + " with " + replacement);
        int order = orderedComponents().indexOf(childToReplace);
        removeObjectFromBothSidesOfRelationshipWithKey(childToReplace, "toChildren");
        if (deleteOldChild)
        {
            editingContext().deleteObject(childToReplace);
        }

        // Remove the child from the orderedComponents list and update the component order from this list
        NSMutableArray orderedComponents = new NSMutableArray(orderedComponents());
        orderedComponents.removeObject(childToReplace);

        addObjectToBothSidesOfRelationshipWithKey(replacement, "toChildren");
        orderedComponents.insertObjectAtIndex(replacement, order);

        clearCachedValues();

        setComponentOrderFrom(orderedComponents);

        /** ensure
        [new_component_is_child] toChildren().containsObject(replacement);
        [old_component_not_child] ! toChildren().containsObject(childToReplace); **/
    }



    /**
     * Moves this PageComponent earlier in the component order by trading places with the PageComponent before it.  If the PageComponent is already first in the list no changes are made.
     *
     * @param aComponent - the PageComponent to move earlier in the component order.
     */
    public void moveEarlier(PageComponent aComponent)
    {
        /** require [a_component_not_null] aComponent != null; **/

        NSMutableArray orderedComponents = new NSMutableArray(orderedComponents());
        int componentIndex = orderedComponents.indexOfIdenticalObject(aComponent);

        if ((aComponent != orderedComponents.objectAtIndex(0)) && (componentIndex != NSArray.NotFound))
        {
            orderedComponents.removeObject(aComponent);
            orderedComponents.insertObjectAtIndex(aComponent, componentIndex - 1);
        }

        setComponentOrderFrom(orderedComponents);
    }



    /**
     * Moves this PageComponent later in the component order by trading places with the PageComponent after it.  If the PageComponent is already last in the list no changes are made.
     *
     * @param aComponent - the PageComponent to move later in the component order.
     */
    public void moveLater(PageComponent aComponent)
    {
        /** require [a_component_not_null] aComponent != null; **/

        NSMutableArray orderedComponents = new NSMutableArray(orderedComponents());
        int componentIndex = orderedComponents.indexOfIdenticalObject(aComponent);

        if ((aComponent != orderedComponents.lastObject()) && (componentIndex != NSArray.NotFound))
        {
            orderedComponents.removeObject(aComponent);
            orderedComponents.insertObjectAtIndex(aComponent, componentIndex + 1);
        }

        setComponentOrderFrom(orderedComponents);
    }



    /**
     * Makes this PageComponent first in the component order.
     *
     * @param aComponent - the PageComponent to move first in the component order.
     */
    public void moveFirst(PageComponent aComponent)
    {
        /** require [a_component_not_null] aComponent != null; **/

        NSMutableArray orderedComponents = new NSMutableArray(orderedComponents());
        orderedComponents.removeObject(aComponent);
        orderedComponents.insertObjectAtIndex(aComponent, 0);

        setComponentOrderFrom(orderedComponents);
    }



    /**
     * Makes this PageComponent last in the component order.
     *
     * @param aComponent - the PageComponent to move last in the component order.
     */
    public void moveLast(PageComponent aComponent)
    {
        /** require [a_component_not_null] aComponent != null; **/

        NSMutableArray orderedComponents = new NSMutableArray(orderedComponents());
        orderedComponents.removeObject(aComponent);
        orderedComponents.addObject(aComponent);

        setComponentOrderFrom(orderedComponents);
    }



    /**
     * Returns <code>true</code> if this PageComponent has any child PageComponents.
     *
     * @return <code>true</code> if this PageComponent has any child PageComponents.
     */
    public boolean hasChildren()
    {
        return toChildren().count() > 0;
    }



    /**
     * Performs some extra validations, particularly for bindings.  As the UI has validation outside of EOF, any of these failing should indicate a programming flaw.
     */
    public void validateForSave() throws NSValidation.ValidationException
    {
        NSMutableArray exceptions = new NSMutableArray();

        try
        {
            super.validateForSave();
        }
        catch (NSValidation.ValidationException e)
        {
            exceptions.addObjectsFromArray(e.additionalExceptions());
        }

        if (componentOrderDictionary() == null)
        {
            exceptions.addObject(new NSValidation.ValidationException("componentOrder is a required binding for a Layout"));
        }

        if (exceptions.count() > 0)
        {
            throw NSValidation.ValidationException.aggregateExceptionWithExceptions(exceptions);
        }
    }



    /**
     * Handles moving the component to a new position in the ordered list. 
     * 
     * @param section the Compone to move
     * @param position zero based index of position to move to
     */
    public void moveComponentToPosition(PageComponent aComponent, int position)
    {
        /** require [valid_component] aComponent != null && toChildren().containsObject(aComponent);
                    [valid_position] (position >= 0) && (position < toChildren().count());                                                 **/
        // The object gets removed from the list, then inserted again.  Despite this, we do not need 
        //to adjust the target index to accomodate the shorter list.
        NSMutableArray orderedComponents = new NSMutableArray(orderedComponents());
        orderedComponents.removeObject(aComponent);
        orderedComponents.insertObjectAtIndex(aComponent, position);

        setComponentOrderFrom(orderedComponents);
    }


    //************** Binding Cover Methods **************\\

    // Notes:
    // The contents of the bindings at this point in time are
    // {componentOrder = {324 = "0001"; 325 = "0002"; 326 = "0003"; }; }
    // The contents of the componentOrderDictionary are PK = Order, where PK is the primary key of a PageComponent sub-class, and Order is relative  order of that component in this layout.
    public NSDictionary componentOrderDictionary()
    {
        return (NSDictionary) valueForBindingKey(COMPONENT_ORDER_BINDINGKEY);
    }


    public void setComponentOrderDictionary(NSDictionary newComponentOrder)
    {
        setBindingForKey(newComponentOrder, COMPONENT_ORDER_BINDINGKEY);
    }


}
