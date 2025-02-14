// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.componentprimitives;
import com.gvcsitemaker.core.Website;
import com.gvcsitemaker.core.components.WebsiteContainerBase;
import com.gvcsitemaker.core.pagecomponent.PageComponent;

import com.webobjects.appserver.WOComponent;
import com.webobjects.appserver.WOContext;
import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;


/**
 * This is the base class for all WOComponents used to display PageComponent sub-classes (they act as a view for the PageComponent model).  There is very little fuctionality in these classes, their main purpose is to provide one of two possible user interfaces.  They provide an interface for editing (used when configuring a site) and one for display only (used when displaying a site).  These classes have two main responsibilities: know which object they are displaying and know if they are in edit mode or display mode.
 *
 * @invariant (currentMode != null) && (currentMode.equals(DISPLAY_MODE) || currentMode.equals(EDIT_MODE))
 */
public class ComponentPrimitive extends WOComponent
{

    private PageComponent componentObject = null;                      // The PageComponet object we are helping to display.

    // For editing mode
    protected String currentMode = null;                                 // Either DISPLAY_MODE or EDIT_MODE
    protected static final String DISPLAY_MODE = "display";
    protected static final String EDIT_MODE = "edit";


    /**
     * Designated constructor.
     */
    public ComponentPrimitive(WOContext aContext)
    {
        super(aContext);
    }



    /**
     * Returns false, ComponentPrimitives do their own synchronization.
     *
     * @return false, ComponentPrimitives do their own synchronization.
     */
    public boolean synchronizesVariablesWithBindings()
    {
        return false;
    }


    
    /**
     * Resets the componentObject reference and currentMode if sub-classes are made stateless.
     */
    public void reset()
    {
        super.reset();
        currentMode = null;
        setComponentObject(null);
    }



    /**
     * Returns the object (a PageComponent sub-class) for which we are acting as a view. 
     *
     * @return the object (a PageComponent sub-class) for which we are acting as a view. 
     */
    public PageComponent componentObject()
    {
        if (componentObject == null)
        {
            findComponentObject();
        }

        return componentObject;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Gets the object (a PageComponent sub-class) for which we are to act as a view from the bindings.  This must be called before using this component.
     */
    public void findComponentObject()
    {
        /** check [not_set] componentObject == null;  **/
        setComponentObject((PageComponent) valueForBinding("componentObject"));
        /** ensure [componentObject_set] componentObject != null;  **/
    }

     

    /**
     * Sets the object (a PageComponent sub-class) for which we are to act as a view.
     */
    protected void setComponentObject(PageComponent aPageComponent)
    {
        componentObject = aPageComponent;
        //** ensure [componentObject_set] componentObject == aPageComponent;  **/
    }



    /**
     * Returns the mode (display or edit) that this component is in.  The mode which has been set can be overriden by the editable status of the PageComponent we are displaying (see canEdit() on PageComponent).  If the PageComponent is not editable this method will return DISPLAY_MODE regardless of what the currentMode has been explicitly set to.
     *
     * @return the mode (display or edit) that this component is in.
     */
    public String currentMode()
    {
        if (currentMode == null)
        {
            currentMode = (String) valueForBinding("currentMode");
        }
        
        return componentObject().canEdit() ? currentMode : DISPLAY_MODE;
        /** ensure
        [valid_result] Result != null;
        [valid_display_mode] Result.equals(DISPLAY_MODE) || (Result.equals(EDIT_MODE) && componentObject().canEdit());
        **/
    }


    
    /**
     * Returns <code>true</code> if this component is in the editing mode according to currentMode().
     *
     * @return <code>true</code> if this component is in the editing mode according to currentMode().
     */
    public boolean isInEditMode()
    {
        return currentMode().equals(EDIT_MODE);
    }



    /**
     * Returns <code>true</code> if this component is in the display mode according to currentMode().
     *
     * @return <code>true</code> if this component is in the display mode according to currentMode().
     */
    public boolean isInDisplayMode()
    {
        return currentMode().equals(DISPLAY_MODE);
    }



    /**
     * Returns the Website containing this ComponentPrimtive during Edit mode (<b>at time of configration only</b>).
     *
     * @return the Website containing this ComponentPrimtive during Edit mode (<b>at time of configration only</b>).
     */
    public Website website()
    {
        /** require [in_edit_section] context().page() instanceof WebsiteContainerBase;  **/
        Website editorsWebsite = ((WebsiteContainerBase)context().page()).website();
        EOEditingContext componentsEC = componentObject().editingContext();
        return (Website)EOUtilities.localInstanceOfObject(componentsEC, editorsWebsite);
        /** ensure [valid_result] Result != null;  
                   [same_ec] componentObject().editingContext() == Result.editingContext();   **/
    }



}
