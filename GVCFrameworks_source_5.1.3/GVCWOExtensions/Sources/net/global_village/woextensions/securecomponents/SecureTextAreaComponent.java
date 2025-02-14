package net.global_village.woextensions.securecomponents;

import com.webobjects.appserver.*;

/**
 * A Text Input control which can conditionally display itself as a TextArea or a string.  This allows the UI to dynamically change based on object state or user permissions.  It must be placed inside a WOForm.  When isEditable is bound to true it displays a WOText containing value() when isEditable is bound to false it displays a WOString containing value().  The cols and rows binding can be used to control the size of the WOText and the maxLength binding can be used to limit how much can be typed into the WOText.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 4$
 */
public class SecureTextAreaComponent extends SecureComponent
{

    /**
     * Designated constructor.
     */
    public SecureTextAreaComponent(WOContext aContext)
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
     * Accessor for name binding.  This bidding can be used to assign a name to the WOComponent for use with JavaScript
     *
     * @return the value bound to name
     */
    public String componentName()
    {
        return (String) valueForBinding("name");
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
     * Accessor for cols binding.  This bidding can be used to control the size of the WOText
     *
     * @return the value bound to cols
     */
    public String cols()
    {
        return (String) valueForBinding("cols");
    }



    /**
     * Accessor for rows binding.  This bidding can be used to control the size of the WOText
     *
     * @return the value bound to rows
     */
    public String rows()
    {
        return (String) valueForBinding("rows");
    }
}
