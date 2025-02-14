// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.admin.user;


import net.global_village.foundation.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.foundation.*;


/**
 * Sends a message to users specified in one of two ways: 1) via a text entry box (navigated from 
 * the ManageWebsitesPage) and 2) via a select list of groups.<p>
 *
 * Since this page is pretty simple, I decided to leave the two modes in one component.  Of course, 
 * it would be even simpler if this were refactored, but seems the extra work is not worth it at 
 * this point.
 */
public class EmailConfigureGroupMembers extends WOComponent implements SMSecurePage
{
    protected boolean isTextEntryMode = true;

    /** Text entry mode */
    protected String textEnteredUserIDs;
    protected UserIDTokenizer tokenizer;

    /** Select mode */
    public boolean sendToOwners = false;
    public boolean sendToStyleAdministrators = false;
    public boolean sendToUnitAdministrators = false;
    public boolean sendToSystemAdministrators = false;

    public String messageSubject;
    public String messageBody;
    protected boolean isConfirmingMessage = false;
    protected boolean isMessageComplete = true;
    protected boolean hasUserIDsCorrespondingToInvalidAddresses = false;


    public EmailConfigureGroupMembers(WOContext aContext)
    {
        super(aContext);
    }



    /**
     * Overridden to reset validation error messages.
     */
    public void appendToResponse(WOResponse aResponse, WOContext aContext)
    {
        super.appendToResponse(aResponse, aContext);
        setIsMessageComplete(true);            
    }
    
    
    
    /**
     * Overridden to setup the User ID tokenizer if we are in the type-in mode.
     */
    public void takeValuesFromRequest(WORequest aRequest, WOContext aContext)
    {
        super.takeValuesFromRequest(aRequest, aContext);

        if (isTextEntryMode())
        {
            tokenizer = new UserIDTokenizer(textEnteredUserIDs());
        }
    }



    /**
     * Check the validity of manually entered addresses.
     *
     * @return any invalid addresses from the appropriate mode
     */
    public boolean hasMalformedToAddresses()
    {
        // The last condition eliminates this raising a NPE when first displayed...
        if (isTextEntryMode() && (tokenizer != null))
        {
            return tokenizer.foundInvalidUserIDs();
        }
        return false;
    }



    /**
     * Returns any invalid addresses from the appropriate mode - of course, hard to enter an invalid
     * address when you're selecting from a list.
     * 
     * @return any invalid addresses from the appropriate mode
     */
    public NSArray invalidAddresses()
    {
        if (isTextEntryMode())
        {
            return tokenizer.invalidUserIDs();
        }
        else
        {
            return new NSArray();
        }

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns invalid addresses as a string.
     * 
     * @return invalid addresses as a string
     */
    public String invalidAddressesString()
    {
        return invalidAddresses().componentsJoinedByString(", ");
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the addresses from the appropriate mode.
     * 
     * @return the addresses from the appropriate mode
     */
    public NSArray addresses()
    {
        NSMutableSet addresses = new NSMutableSet();

        if (isTextEntryMode())
        {
            addresses.addObjectsFromArray(tokenizer.emailAddressesOfValidUserIDs());
        }
        else
        {
            if (sendToOwners)
            {
                addresses.addObjectsFromArray((NSArray)User.usersWithConfigPermission(session().defaultEditingContext()).valueForKey("emailAddress"));
            }
            if (sendToStyleAdministrators)
            {
                addresses.addObjectsFromArray((NSArray)User.styleAdministrators(session().defaultEditingContext()).valueForKey("emailAddress"));
            }
            if (sendToSystemAdministrators)
            {
                addresses.addObjectsFromArray((NSArray)User.systemAdministrators(session().defaultEditingContext()).valueForKey("emailAddress"));
            }
            if (sendToUnitAdministrators)
            {
                addresses.addObjectsFromArray((NSArray)User.unitAdministrators(session().defaultEditingContext()).valueForKey("emailAddress"));
            }
        }

        DebugOut.println(20, "Addresses: " + addresses);
        return addresses.allObjects();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Sends the message and redisplays the page with a confirmation message.
     * 
     * @return this page
     */
    public WOComponent sendMessage()
    {
        if (hasMalformedToAddresses())
        {
            // Don't do anything, an error message will be displayed
        }
        else if (StringAdditions.isEmpty(messageSubject))
        {
            // Subject is required, show error message
            setIsMessageComplete(false);            
        }
        else if (addresses().count() == 0)
        {
            // To is required, show error message
            setIsMessageComplete(false);            
        }
        else
        { 
            SendEmail.sendIndividualMessages(currentAdmin().emailAddress(), 
                                             addresses(), 
                                             messageSubject, 
                                             messageBody == null ? "" : messageBody);
            setIsConfirmingMessage(true);
        }

        /* This makes no sense and seems to be impossible.  I commented it out.  -chuck
        if (invalidAddresses().count() > 0)
        {
            setHasUserIDsCorrespondingToInvalidAddresses(true);
        }
        */
        
        return context().page();

        /** ensure [valid_result] Result != null; **/
    }



    public boolean isConfirmingMessage() {
        return isConfirmingMessage;
    }
    public void setIsConfirmingMessage(boolean newIsConfirmingMessage) {
        isConfirmingMessage = newIsConfirmingMessage;
    }


    public boolean isMessageComplete() {
        return isMessageComplete;
    }
    public void setIsMessageComplete(boolean newIsMessageComplete) {
        isMessageComplete = newIsMessageComplete;
    }


    public boolean isTextEntryMode()
    {
        return isTextEntryMode;
    }

    public void setIsTextEntryMode(boolean b)
    {
        isTextEntryMode = b;
    }



    public String textEnteredUserIDs()
    {
        return textEnteredUserIDs;
    }

    public void setTextEnteredUserIDs(String string)
    {
        textEnteredUserIDs = string;
    }



    public boolean hasUserIDsCorrespondingToInvalidAddresses()
    {
        return hasUserIDsCorrespondingToInvalidAddresses;
    }

    public void setHasUserIDsCorrespondingToInvalidAddresses(boolean b)
    {
        hasUserIDsCorrespondingToInvalidAddresses = b;
    }


    public User currentAdmin() {
        return ((SMSession)session()).currentUser();
    }


}
