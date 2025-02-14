// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.utility.pages;


import java.lang.reflect.*;

import com.gvcsitemaker.core.support.*;

import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;
import net.global_village.jmail.*;


/**
 * Sends message to Internal, External, or all users.
 */
public class MessageToUsers extends WOComponent 
{
    public String errorMessage;
    public String from;
    public String subject;
    public String body;
    public String selectedUsers = "External";

    public MessageToUsers(WOContext context) 
    {
        super(context);
    }



    /**
     * Action method to validate parameters and send message.
     * @return
     */
    public WOComponent sendMessage() 
    {
        errorMessage = null;

        if (selectedUsers == null)
        {
            errorMessage = "A group of users must be selected.";
        }
        else if (StringAdditions.isEmpty(from))
        {
            errorMessage = "The from address is required.";
        }
        else if ( ! EmailAddress.isValidAddressFormat(from))
        {
            errorMessage = "The from address is not a valid e-mail address.";
        }
        else if (StringAdditions.isEmpty(subject))
        {
            errorMessage = "The subject is required.";
        }
        else if (StringAdditions.isEmpty(body))
        {
            errorMessage = "The message is required.";
        }
        else
        {
            try
            {
                NSArray targetedUsers = targetedUsers();
                if (targetedUsers.count() > 0)
                {
                    SendEmail.sendIndividualMessages(from, targetedUsers, subject, body);
                }
                errorMessage = "Message sent to " + targetedUsers.count() + " addresses.";
            }
            catch (InvocationTargetException e)
            {
                throw new ExceptionConverter(e);
            }
            catch (IllegalAccessException e)
            {
                throw new ExceptionConverter(e);
            }
        }
        return context().page();
    }



    /**
     * Returns the list of users to send this message to.
     * @return the list of users to send this message to
     */
    protected NSArray targetedUsers() throws InvocationTargetException, IllegalAccessException
    {
        NSArray targetedUsers = EOUtilities.objectsForEntityNamed(session().defaultEditingContext(), 
                                                                  "User");
        NSSelector internalUserSelector = new NSSelector("isInternalUser");

        if (selectedUsers.equals("External"))
        {
            targetedUsers = Collection.objectsResponding(targetedUsers,
                                                         java.lang.Boolean.FALSE,
                                                         internalUserSelector,
                                                         null);
        }
        else if (selectedUsers.equals("Internal"))
        {
            targetedUsers = Collection.objectsResponding(targetedUsers,
                                                         java.lang.Boolean.TRUE,
                                                         internalUserSelector,
                                                         null);
        }

        return (NSArray)targetedUsers.valueForKey("userID");
    }
}
