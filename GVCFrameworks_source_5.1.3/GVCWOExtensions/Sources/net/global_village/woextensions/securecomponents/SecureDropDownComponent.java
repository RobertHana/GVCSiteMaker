package net.global_village.woextensions.securecomponents;

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;



/**
 * A Drop Down List Input control which can conditionally display itself as a PopUp or a string.  This allows the UI to dynamically change based on object state or user permissions.  It must be placed inside a WOForm.  When isPermissable is bound to true it displays a WOPopUpButton when isPermissable is bound to false it displays a WOString containing selection().  The WOPopUpButton is populated by the list binding with the current selection being the value binding.  displayStringPath is the name of the method to call on the objects in the array bound to list for display purposes.  The method named must return an Object, ideally a String.  The same keyValueCoding will be performed on the uneditable version if displayStringPath is bound to non null and isPermissable is false.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 4$
 */
public class SecureDropDownComponent extends SecureComponent
{
    public Object anObject;


    /**
     * Designated constructor.
     */
    public SecureDropDownComponent(WOContext aContext)
    {
        super(aContext);
    }


    
    /**
     * Resets WORepetition instance variable to be null after request response loop completes
     *
     */
    public void reset()
    {
        anObject = null;
    }

    

    /**
     * Accessor for selection binding.  The binding provides the displayed content of the WOComponent.  It is bound to selection in the WOPopUpButton
     *
     * @return the value bound to value
     */
    public Object selection()
    {
        return valueForBinding("selection");
    }



    /**
     * Accessor for formatter binding.  This binding formats the displayed content.
     *
     * @return the value bound to formatter
     */
    public java.text.Format formatter()
    {
        return (java.text.Format) valueForBinding("formatter");
    }



    /**
     * Accessor for name binding.  This bidding can be used to assign a name to the WOComponent for use with JavaScript
     *
     * @return the value bound to name
     */
    public String componentName()
    {
        return (String) valueForBinding("name");
    }


    
    /**
     * Accessor for list binding.  The binding provides a list of objects to populate the WOPopUpButton.
     *
     * @return the value bound to list
     */
    public NSArray objects()
    {
        return (NSArray) valueForBinding("list");
    }
    

    
    /**
     * Displays the current value of selection in an uneditable and specified form
     *
     * @return the current value of selection in an uneditable and specified form
     */
    public Object currentValue()
    {
        Object value;

        if ( ! hasBinding("displayStringPath") || selection() == null)
        {
            value = selection();
        }
        else
        {
            value = NSKeyValueCodingAdditions.DefaultImplementation.valueForKeyPath(selection(),
                                                                                    (String) valueForBinding("displayStringPath"));
        }

        return value;
    }



     /**
      * Returns the string value of anObject of value for the key path passed in
      *
      * @return the string value of anObject of value for the key path passed in
      */
     public String displayString()
     {
         String displayString;

         if ( ! hasBinding("displayStringPath"))
         {
             displayString = anObject.toString();
         }
         else
         {
             displayString = (String) NSKeyValueCodingAdditions.DefaultImplementation.valueForKeyPath(anObject,
                                                                                                      (String) valueForBinding("displayStringPath"));
         }

         return displayString;
     }

    

    /**
     * Accessor for onChangeJavascript binding.  The optional binding of what Javascript to run when the PopUpButton is changes
     *
     * @return the value bound to onChangeJavascript
     */
    public String onChangeJavascript()
    {
        String onChangeJavascript;

        if ( ! hasBinding("onChangeJavascript"))
        {
            onChangeJavascript = new String();  // bound to an empty string if non is provided
        }
        else
        {
            onChangeJavascript = (String) valueForBinding("onChangeJavascript");
        }
        
        return onChangeJavascript;
    }

    

    /**
     * Accessor for noSelectionString binding.  This bidding can be used to assign a noSelectionString to the WOComponent to be displayed when no value is selected
     *
     * @return the value bound to noSelectionString
     */
    public String noSelectionString()
    {
        return (String) valueForBinding("noSelectionString");
    }
}
