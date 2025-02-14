package net.global_village.woextensions.securecomponents;

import com.webobjects.appserver.*;

/**
 * A Text Input control which can conditionally display itself as a TextField or a string.  This allows the UI to dynamically change based on object state or user permissions.  It must be placed inside a WOForm.  When isEditable is bound to true it displays a WOTextField containing value() when isEditable is bound to false it displays a WOString containing value().  The size binding can be used to control the size of the WOTextField and the maxLength binding can be used to limit how much can be typed into the WOTextField.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 4$
 */
public class SecureTextFieldComponent extends SecureComponent
{


    /**
     * Designated constructor.
     */
    public SecureTextFieldComponent(WOContext aContext)
    {
        super(aContext);
    }


    
    /**
     * Accessor for value binding.  The binding provides the displayed content of the WOComponent
     *
     * @return the value bound to value
     */
    public Object value()
    {
        return valueForBinding("value");
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
     * Accessor for maxLength binding.  This bidding can be used to assign a maxLength to the WOTextField
     *
     * @return the value bound to maxLength
     */
    public String maxLength()
    {
        return (String) valueForBinding("maxLength");
    }


 
    /**
     * Accessor for size binding.  This bidding can be used to assign a size to the WOTextField
     *
     * @return the value bound to size
     */
    public String size()
    {
        return (String) valueForBinding("size");
    }
}
