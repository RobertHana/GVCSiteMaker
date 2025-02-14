package net.global_village.woextensions.securecomponents;


import com.webobjects.appserver.*;

/**
* If isPermissable is true displays a checkbox controlled by isChecked, if isPermissable is not true displays a string value for isChecked.  If isChecked is true string is "Yes", "No" otherwise.  It must be placed inside a WOForm.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 4$
 */  
public class SecureCheckBoxComponent extends SecureComponent
{

    /**
     * Designated constructor.
     */
    public SecureCheckBoxComponent(WOContext aContext)
    {
        super(aContext);
    }


    
    /**
     * Accessor for isChecked binding.  The binding provides the displayed content of the WOComponent
     *
     * @return the value bound to isChecked
     */
    public boolean isChecked()
    {
        return  booleanValueForBinding("isChecked", false);
    }



    /**
     * Returns a string value for isChecked() Yes if isChecked is true, No otherwise
     *
     * @return a string value for isChecked() Yes if isChecked is true, No otherwise
     */
    public String stringValue()
    {
        String stringValue = "No";

        if (isChecked())
        {
            stringValue = "Yes";
        }
        
        return stringValue;
    }

}
