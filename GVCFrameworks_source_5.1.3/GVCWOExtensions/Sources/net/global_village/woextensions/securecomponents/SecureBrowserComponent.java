package net.global_village.woextensions.securecomponents;

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;


 
/**
* A Browser List Input control which can conditionally display itself as a Browser or a series of strings.  This allows the UI to dynamically change based on object state or user permissions.  It must be placed inside a WOForm.  When isPermissable is bound to true it displays a WOBrowser.  When isPermissable is bound to false it displays WOStrings containing value() for the selections.  Each selection is on a separate line.  If there are no selections "None" is printed onscreen.  The WOBrowser is populated by the list binding with the current selections being the selections binding.  displayStringPath is the name of the method to call on the objects in the array bound to list for display purposes.  The method named must return an Object, ideally a String.  The same keyValueCoding will be performed on the uneditable version if displayStringPath is bound to non null and isPermissable is false.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 4$
 *
 */  
public class SecureBrowserComponent extends SecureComponent
{
    public Object anObject;


    /**
     * Designated constructor.
     */
    public SecureBrowserComponent(WOContext aContext)
    {
        super(aContext);
    }  


    
    /**
     * Resets WORepetition instance variable to be null after request response loop completes
     */
    public void reset()
    {
        anObject = null;
    }

    

    /**
     * Accessor for selections binding.  The binding provides the displayed content of the WOComponent.  It is bound to selections in the WOBrowser
     *
     * @return the value bound to value
     */
    public NSArray selections()
    {
        return (NSArray) valueForBinding("selections");
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
     * Accessor for name binding.  This binding can be used to assign a name to the WOComponent for use with JavaScript
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
             displayString = (String) NSKeyValueCodingAdditions.DefaultImplementation.valueForKeyPath(anObject,                                                                                            (String) valueForBinding("displayStringPath"));
         }

         return displayString;
     }

    

    /**
     * Accessor for isMultiple binding.  Determines whether multiple selections can be made.
     *
     * @return true if bound to true
     */
    public boolean isMultiple()
    {
        return booleanValueForBinding("isMultiple", false);  // this method has preconditions which I've managed to violate
        // this may mean this is a required binding...
    }



    /**
     * Returns true if <code>selections()</code> contains objects
     *
     * @return true if <code>selections()</code> contains objects
     */
    public boolean hasSelectedObjects()
    {
        /** require [has_selections] selections() != null; **/

        return selections().count() > 0;
    }
 


    /**
     * Returns the no selection string or "None" if there is no binding.
     *
     * @return the no selection string or "None" if there is no binding.
     */
    public String noSelectionString()
    {
        String noSelectionString;

        if (hasNonNullBindingFor("noSelectionString"))
        {
            noSelectionString = (String) valueForBinding("noSelectionString");
        }
        else
        {
            noSelectionString = "None";
        }

        return noSelectionString;

        /** ensure [valid_result] Result != null; **/
    }



}
