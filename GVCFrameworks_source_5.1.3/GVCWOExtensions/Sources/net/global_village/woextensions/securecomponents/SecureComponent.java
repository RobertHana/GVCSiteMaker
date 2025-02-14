package net.global_village.woextensions.securecomponents;

import com.webobjects.appserver.*;


/**
 * This is the super class for all Secure Components
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 4$
 */
public class SecureComponent extends net.global_village.woextensions.WOComponent
{


    /**
     * Designated constructor.
     */
    public SecureComponent(WOContext aContext)
    {
        super(aContext);
    }  



    /**
     * This method is overridden so variables are not synchronized with bindings.
     *
     * @return <code>false</code>
     */
    public boolean synchronizesVariablesWithBindings()
    {
        return false;
    }



    /**
     * This method is overridden as SecureComponents do not use instance variables and will save memmory by officially being declared isStateless
     *
     * @return <code>true</code>
     */
    public boolean isStateless()
    {
        return true;
    }



    /**
     * Accessor for isPermissable binding.  Determins whether the link is shown.
     *
     * @return true if bound to true
     */
    public boolean isPermissable()
    {
        return  booleanValueForBinding("isPermissable", false);
    }



    /**
     * Accessor for cssClassName binding.  This binding provides a class name which can be used to format the displayed content.
     *
     * @return the value bound to cssClassName
     */
    public String cssClassName()
    {
        return (String) valueForBinding("cssClassName");
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
    * This method is called during the takeValuesFromRequest:inContext phase.  As each value is taken from the request, any validation exceptions,	 formatter failures etc. are sent here.  This is the best place to catch these as both the old and new values are available here.  Also, WebObjects will leave the old value unchanged when a formatter fails, but will overwrite the old value when a validateAttribute method returns an exception.  <b>NOTE:</b> In order to use secure components and handle for instance formatter failures you must provide a binding for "name".  This is then used instead of keypath.  It is suggested the value you bind to "name" is a String equal to the expected keyPath.
     *
     * @param exception the exception raised
     * @param value the object that raised the exception
     * @param keyPath the keyPath of the exception ?
     */
    public void validationFailedWithException(java.lang.Throwable exception,
                                              java.lang.Object value,
                                              java.lang.String keyPath)
    {
        parent().validationFailedWithException(exception, value, componentName());
    }



}
