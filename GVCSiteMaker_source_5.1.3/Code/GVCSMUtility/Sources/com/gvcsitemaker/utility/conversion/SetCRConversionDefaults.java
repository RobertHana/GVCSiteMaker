package com.gvcsitemaker.utility.conversion;


import com.gvcsitemaker.core.pagecomponent.*;

import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * Sets the default for new bindings on page components.  Yeah, it needs to be renamed, but it is only
 * for conversion so why bother?
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class SetCRConversionDefaults extends WOComponent 
{
    protected String conversionMessage;

    
    /**
     * Designated constructor.
     */
    public SetCRConversionDefaults(WOContext context) 
    {
        super(context);
    }



    /**
     * Conversion method.
     * @return this
     */
    public WOComponent setDefaultConversion() 
    {
        boolean defaultValue = false;
        EOEditingContext ec;
        NSArray components;
        int converted;
        
        ec = new EOEditingContext();
        ec.lock();
        try
        {
            // Update Text components
            NSLog.out.appendln("Fetching Text");
            components = EOUtilities.objectsForEntityNamed(ec, "Text");
            NSLog.out.appendln("Fetched Text: " + components.count());
            converted = 0;
            for (int i = 0; i < components.count(); i++)
            {
                NSLog.out.appendln("Processing " + i);

                Text text = (Text) components.objectAtIndex(i);
                if ( ! text.hasValueForBindingKey(Text.CONVERT_CR_BINDINGKEY))
                {
                    try
                    {
                        text.setShouldConvertCR(defaultValue);
                        converted++;
                    }
                    catch (java.lang.IllegalStateException e)
                    {
                        ec.forgetObject(text);
                    }
                }
            }
            NSLog.out.appendln("Saving...");
            ec.saveChanges();
            NSLog.out.appendln("Saved");
            conversionMessage = "Checked " + components.count() + ", converted " + converted + " Text components.  ";
        }
        finally
        {
            ec.unlock();
            ec.dispose();
        }  
            
        ec = new EOEditingContext();
        ec.lock();
        try
        {
            // Update DataAccessStringColumn
            NSLog.out.appendln("Fetching DataAccessStringColumn");
            components = EOUtilities.objectsForEntityNamed(ec, "DataAccessStringColumn");
            NSLog.out.appendln("Fetched DataAccessStringColumn: " + components.count());
            converted = 0;
            for (int i = 0; i < components.count(); i++)
            {
                NSLog.out.appendln("Processing " + i);

                DataAccessStringColumn stringCol = (DataAccessStringColumn) components.objectAtIndex(i);
                if ( ! stringCol.hasValueForBindingKey(DataAccessStringColumn.CONVERT_CR_BINDINGKEY))
                {
                    try
                    {
                        stringCol.setShouldConvertCR(defaultValue);
                        converted++;
                    }
                    catch (java.lang.IllegalStateException e)
                    {
                        ec.forgetObject(stringCol);
                    }
                }
            }
            NSLog.out.appendln("Saving...");
            ec.saveChanges();
            NSLog.out.appendln("Saved");
            conversionMessage += "<br/>Checked " + components.count() + ", converted " + converted + " DataAccessStringColumn components.  ";
        }
        finally
        {
            ec.unlock();
            ec.dispose();
        }  

        ec = new EOEditingContext();
        ec.lock();
        try
        {
            // Update ListLink
            NSLog.out.appendln("Fetching ListLink");
            components = EOUtilities.objectsForEntityNamed(ec, "ListLink");
            NSLog.out.appendln("Fetched ListLink: " + components.count());
            converted = 0;
            for (int i = 0; i < components.count(); i++)
            {
                NSLog.out.appendln("Processing " + i);

                ListLink listLink = (ListLink) components.objectAtIndex(i);
                if ( ! listLink.hasValueForBindingKey(ListLink.OPEN_IN_NEW_WINDOW_BINDINGKEY))
                {
                    try
                    {
                        listLink.setShouldOpenInNewWindow(false);
                        converted++;
                    }
                    catch (java.lang.IllegalStateException e)
                    {
                        ec.forgetObject(listLink);
                    }
                }
            }
            NSLog.out.appendln("Saving...");
            ec.saveChanges();
            NSLog.out.appendln("Saved");
            conversionMessage += "<br/>Checked " + components.count() + ", converted " + converted + " ListLink components.  ";
        }
        finally
        {
            ec.unlock();
            ec.dispose();
        }  

        ec = new EOEditingContext();
        ec.lock();
        try
        {
            // Update DataAccessLookupColumn
            NSLog.out.appendln("Fetching DataAccessLookupColumn");
            components = EOUtilities.objectsForEntityNamed(ec, "DataAccessLookupColumn");
            NSLog.out.appendln("Fetched DataAccessLookupColumn: " + components.count());
            converted = 0;
            for (int i = 0; i < components.count(); i++)
            {
                NSLog.out.appendln("Processing " + i);

                DataAccessLookupColumn lookupCol = (DataAccessLookupColumn) components.objectAtIndex(i);
                if ( ! lookupCol.hasValueForBindingKey(DataAccessLookupColumn.DISPLAY_TYPE_BINDINGKEY))
                {
                    try
                    {

                    lookupCol.setDisplayType(DataAccessLookupColumn.POPUP_DISPLAY_TYPE);
                    converted++;
                    }
                    catch (java.lang.IllegalStateException e)
                    {
                        ec.forgetObject(lookupCol);
                    }
                }
            }
            NSLog.out.appendln("Saving...");
            ec.saveChanges();
            NSLog.out.appendln("Saved");
            conversionMessage += "<br/>Checked " + components.count() + ", converted " + converted + " DataAccessLookupColumn components.  ";
        }
        finally
        {
            ec.unlock();
            ec.dispose();
        }
    
        return this;
    }



    public String conversionMessage(){
        return conversionMessage;
    }
    public void setConversionMessage(String newConversionMessage) {
        conversionMessage = newConversionMessage;
    }

}
