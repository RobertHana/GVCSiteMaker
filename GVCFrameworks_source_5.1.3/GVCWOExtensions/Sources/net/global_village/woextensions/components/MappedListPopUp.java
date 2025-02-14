package net.global_village.woextensions.components;

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;


/**
 * <p>This component is an alternative to WOPopUp.  WOPopUp assumes that the displayed and selected 
 * values come from the same source (e.g. an NSArray of EOEnterpriseObjects).  The MappedListPopUp is
 * useful when there are display and internal representations that are not available from the same
 * object.  As an example, consider a popup displaying Yes and No while selecting Boolean.TRUE and
 * Boolean.False.  It is easy to displaying true and false while selecting Boolean.TRUE and
 * Boolean.False but how can you display Yes and No useing a WOPopup?</p>
 * 
 * <p>MappedListPopUp solves this by taking two lists: one to display and one to use for the 
 * <code>selection</code>.  These are assumed to be in two arrays at matching indexes (element n of 
 * the display list matches element n of the selection list.</p>
 * 
 * <p>The bindings are:<br/>
 * <table>
 * <th><td>Binding</td><td>Usage</td></th>
 * <tr><td>displayFrom</td><td>the <code>NSArray</code> to take the UI display values from</td></tr>
 * <tr><td>selectFrom</td><td>the <code>NSArray</code> to take the values for <code>selection</code> from</td></tr>
 * <tr><td>selection</td><td>the value to be changed when a different item is selected from the list</td></tr>
 * <tr><td>noSelectionString</td><td>Optional.  Enables the first item to be "empty."  Bind this attribute to a 
 * string (such as an empty string) that, if chosen, represents an empty selection. When this item 
 * is selected, the <code>selection</code> attribute is set to <code>null</code>.</td></tr>
 * <tr><td>formatter</td><td>Optional.  The formatter to format items in <code>displayFrom</code> with before
 * displaying them.</td></tr>
 * <table></p>
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class MappedListPopUp extends WOComponent 
{

    /** Used for the iteration over <code>displayFrom/displayList</code> */
    public Object anItem;


    public MappedListPopUp(WOContext context) 
    {
        super(context);
    }


    /**
     * Returns the value to display for <code>anItem</code>, formatted if <code>formatter()</code>
     * is not null.  
     *  
     * @return the value to display for <code>anItem</code>
     */
    public Object displayString() 
    {
        /** require [valid_item] anItem != null;  **/
        
        return (formatter() != null) ? formatter().format(anItem) : anItem;
        
        /** ensure [valid_result] Result != null;  **/
    }
                
                
                
                
    /**
     * Returns the value from <code>displayList()</code> that maps to <code>parentSelection()</code>.
     * If <code>parentSelection()</code> is null then the first element of <code>displayList()</code>
     * is returned. 
     * 
     * @return the value from <code>displayList()</code> to display as the selected value
     */
    public Object selection() 
    {
        Object selection = displayList().objectAtIndex(0);
        Object parentSelection = parentSelection();
        
        if (parentSelection != null)
        {
            selection = displayList().objectAtIndex(selectionList().indexOfObject(parentSelection));
        }
        
        return selection;
        /** ensure [valid_result] Result != null;  **/
    }
    


    /**
     * Sets the <code>parentSelection()</code> based on the value from <code>selectionList()</code> 
     * that maps to <code>newSelection()</code>.
     * 
     * @param newSelection value from <code>displayList()</code> that maps to the element in 
     * <code>selectionList()</code> to select
     */
    public void setSelection(String newSelection) 
    {
        /** require [valid_selection] (newSelection != null) && displayList().containsObject(newSelection);  **/
        if (newSelection == null)
        {
            setValueForBinding(null, "selection");
        }
        else
        {
            setValueForBinding(selectionList().objectAtIndex(displayList().indexOfObject(newSelection)), "selection");
        }
        /** ensure [value_set] selection().equals(newSelection);  **/
    }



    /**
     * Returns false, this component does its own synchronization.
     *
     * @return false, this component does its own synchronization
     */
    public boolean synchronizesVariablesWithBindings()
    {
        return false;
    }



    /**
     * Returns true, this component is stateless.
     *
     * @return true, this component is stateless.
     */
    public boolean isStateless()
    {
        return true;
    }
   

    /* Binding cover methods       ***************************************/
    public String noSelectionString() {
        return (String) valueForBinding("noSelectionString");
    }

    public java.text.Format formatter() {
        return (java.text.Format) valueForBinding("formatter");
    }

    public NSArray displayList() {
        return (NSArray) valueForBinding("displayFrom");
    }
    
    public NSArray selectionList() {
        return (NSArray) valueForBinding("selectFrom");
    }
    public Object parentSelection() {
        return valueForBinding("selection");
    }
        
    /* Generic setters and getters ***************************************/

   
    
}
