package net.global_village.woextensions.orderablelists;


import com.webobjects.appserver.*;
import com.webobjects.foundation.*;


/**
 * Popup to be used with OrderableList to show a popup for the objects in the list allowing them
 * to be manually reordered.  The containing form is submitted when one of these popups is changed.
 * Auto-save logic for ordering could be put in the forms action method with multiple-submit labeled.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class ReorderPopup extends WOComponent {

    public static final String OBJECT_BINDING = "object";
    public static final String CONTAINER_BINDING = "container";

    public Object anIndex;
    private Object indexToMoveTo;


    public ReorderPopup(WOContext context) {
        super(context);
    }

    
    
    /**
     * @return the current index of object()
     */
    public Object selectedIndex() {
        return container().positionNameForObject(object());
    }
    
    
    
    /**
     * Moves the related object to the selected index if this is not the current position.  This component
     * should not be used in a form with other inputs as it will change the list in the repetition while
     * the takeValues phase is running.  If this is to be used in a form with other inputs, some arrangement
     * will have to be made with the container to delay reordering.
     * 
     * @param newSelectedIndex the one based index to move this object to
     */
    public void setSelectedIndex(Object newSelectedIndex) {
        if ( ! selectedIndex().equals(newSelectedIndex))
        {
            indexToMoveTo = newSelectedIndex;
        }
    }
    
    
    
    /**
     * Overridden so that the new order set during the takeValues phase can be processed before the form's
     * action runs.  This is a hack and may cause future bugs.  Be suspicious of this method!  
     * It was done this way to see if we could find a solution where the value is taken without
     * altering the collection being iterated during takeValues. 
     *
     * @see com.webobjects.appserver.WOElement#invokeAction(com.webobjects.appserver.WORequest, com.webobjects.appserver.WOContext)
     */
    public WOActionResults invokeAction(WORequest arg0, WOContext arg1) {
        if (indexToMoveTo != null)
        {
            container().moveObjectToPosition(object(), container().positionForPositionName(indexToMoveTo));
            indexToMoveTo = null;
        }

        return super.invokeAction(arg0, arg1);
    }
    
    
    
    /**
     * @return the OrderableList we the bound to
     */
    public OrderableList container() {
        return (OrderableList)valueForBinding(CONTAINER_BINDING);
    }
    
    
    
    /**
     * @return the list that is displayed in the popup
     */
    public NSArray list() {
        return container().positionsForObject(object());
    }
    
    
    
    /**
     * @return the object we are controlling the order for
     */
    public Object object() {
     return valueForBinding(OBJECT_BINDING);
    }

    
    
    /* (non-Javadoc)
     * @see com.webobjects.appserver.WOComponent#synchronizesVariablesWithBindings()
     */
    public boolean synchronizesVariablesWithBindings() {
        return false;
    }
      
    
}
