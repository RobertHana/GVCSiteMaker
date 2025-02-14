// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
// Arbor, MI 48109 USA All rights reserved.
// This software is published under the terms of the Educational Community
// License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT
// file.
package com.gvcsitemaker.pages;

import net.global_village.foundation.*;

import com.gvcsitemaker.appserver.*;
import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.components.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.support.*;
import com.webobjects.appserver.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/*
 * Web form to send a link to a file to e-mail addresses or access groups. Also
 * provides a mailto URL to allow the local e-mail client to be used. //
 * Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
 * Arbor, MI 48109 USA All rights reserved. // This software is published under
 * the terms of the Educational Community License (ECL) version 1.0, // a copy
 * of which has been included with this distribution in the LICENSE.TXT file.
 */
public class EmailPage extends com.gvcsitemaker.core.components.WebsiteContainerBase implements SMSecurePage
{
    protected String toAddresses;
    /** @TypeInfo com.gvcsitemaker.core.Group */
    protected NSArray availableAccessGroups;
    public Group anAccessGroup;
    public String subject;
    public String message;
    public WOComponent returnPage;
    public SiteFile file;
    public FilePassword filePassword;
    public String additionalMessage;
    /** @TypeInfo com.gvcsitemaker.core.Group */
    protected NSMutableArray selectedAccessGroups;

    // Not sure why this is required, but it is getting set by the Access Group tab
    public Group editingAccessGroup;


    /**
     * Designated constructor.
     */
    public EmailPage(WOContext aContext)
    {
        super(aContext);
        selectedAccessGroups = new NSMutableArray();
    }



    /**
     * Takes the user to the main config page.
     *
     * @return the main config page
     */
    public WOComponent backToWebsiteSections()
    {
        return pageWithName("ConfigTabSet");
    }



    /**
     * Sets the file to send a URL to and in the process sets the subject and message to be used.  A
     * password is generated if the file is access restricted.
     * 
     * @param newFile the file to send
     */
    public void setFile(SiteFile newFile)
    {
        /** require [valid_file] newFile != null;  **/

        file = newFile;


        subject = "The file '" + file.uploadedFilename() + "' is available for you...";

        String url;
        if (file().isPublic())
        {
            url = file().url();
        }
        else
        {
            // We need to create a password for the file as it is access protected
            filePassword = file().newPassword();
            editingContext().saveChanges();

            url = file().url(filePassword);
        }

        message = "You may pickup '" + file.uploadedFilename() + "' by directing your web browser to \n\n      " + url;

        // There is an extra message for files accessed via a password.
        if (!file().isPublic())
        {
            NSTimestampFormatter expiryDateFormatter = (NSTimestampFormatter) SMApplication.appProperties().propertyForKey("LongDateFormatter");
            message += "\n\n on or before " + expiryDateFormatter.format(filePassword().expirationDate()) + ".\n";
        }

        availableAccessGroups = null;

        /** ensure [file_set] file() != null; 
                   [valid_subject] subject() != null;  
                   [valid_message] message() != null;
                   [password_set] file().isPublic() || (filePassword() != null);       **/
    }



    /**
     * Action method to send the e-mail via the web form.
     * 
     * @return the page to display after sending the message.
     */
    public WOComponent sendEmail()
    {

        String sender = ((Session) session()).currentUser().emailAddress();
        NSMutableSet toList = new NSMutableSet();

        // Collect up manually entered addresses
        UserIDTokenizer userIDTokenizer = new UserIDTokenizer(toAddresses());

        if (userIDTokenizer.foundInvalidUserIDs())
        {
            ErrorPage error = (ErrorPage) pageWithName("ErrorPage");
            error.setReturnPage(this);
            error.setMessage("These To addresses are not acceptable: " + userIDTokenizer.invalidUserIDs().componentsJoinedByString(", "));
            error.setReason("Incorrect entries in To addresses.");
            return error;
        }

        toList.addObjectsFromArray(userIDTokenizer.emailAddressesOfValidUserIDs());

        // Collect up Access Group determined addresses
        for (int i = 0; i < selectedAccessGroups().count(); i++)
        {
            LocalGroup group = (LocalGroup) selectedAccessGroups().objectAtIndex(i);
            toList.addObjectsFromArray(group.membersEmailAddresses());
        }

        if (toList.count() == 0)
        {
            ErrorPage error = (ErrorPage) pageWithName("ErrorPage");
            error.setReturnPage(this);
            error.setMessage("You must select at least one Access Group or enter at least one address " + "in the 'to' field of your email.");
            error.setReason("Missing field entry.");
            return error;
        }

        String messageBody = message();

        if (additionalMessage() != null)
        {
            messageBody += "\n\n" + additionalMessage();
        }

        SendEmail.sendIndividualMessages(sender, toList.allObjects(), subject(), messageBody);

        return returnPage();
    }



    /**
     * Returns a mailto: url to send <code>file()</code>.  The URL has the form:<br/>
     * <code>mailto:?subject=file link&body=Explanation and link</code>
     *  
     * @return  a mailto: url to send <code>file()</code>
     */
    public String mailtoHyperlink()
    {
        /** require [has_file] file() != null;  **/

        StringBuffer mailToURL = new StringBuffer("mailto:?subject=");
        mailToURL.append(subject());
        mailToURL.append("&body=");
        mailToURL.append(message());

        return mailToURL.toString();
        /** ensure [valid_result] Result != null;  
                   [valid_url] Result.startsWith("mailto:");   **/
    }



    public WOComponent cancelEmail()
    {
        editingContext().revert();
        return returnPage;
    }


    public String pageTitle()
    {
        return SMApplication.appProperties().propertyForKey("ProductName") + ": Send Message to Pickup File";
    }


    public boolean isAccessGroupSelected()
    {
        return selectedAccessGroups().containsObject(anAccessGroup);
    }


    public void setIsAccessGroupSelected(boolean isAccessGroupSelected)
    {
        if (isAccessGroupSelected)
        {
            selectedAccessGroups().addObject(anAccessGroup);
        }
        else
        {
            selectedAccessGroups().removeObject(anAccessGroup);
        }

        /** ensure [selected_groups_correct] isAccessGroupSelected ? isAccessGroupSelected() 
                                                                   : ! isAccessGroupSelected();  **/
    }



    /** @TypeInfo com.gvcsitemaker.core.Group */
    public NSArray availableAccessGroups()
    {
        if (availableAccessGroups == null)
        {
            availableAccessGroups = file.website().orderedLocalGroups();
        }

        return availableAccessGroups;
    }


    /* Generic setters and getters ***************************************/

    public SiteFile file()
    {
        return file;
    }


    public FilePassword filePassword()
    {
        return filePassword;
    }


    public String subject()
    {
        return subject;
    }


    public String message()
    {
        return message;
    }


    public WOComponent returnPage()
    {
        return returnPage;
    }


    public void setReturnPage(WOComponent newReturnPage)
    {
        returnPage = newReturnPage;
    }


    public NSTimestamp currentTime()
    {
        return Date.now();
    }


    public String additionalMessage()
    {
        return additionalMessage;
    }


    public void setAdditionalMessage(String newAdditionalMessage)
    {
        additionalMessage = newAdditionalMessage;
    }


    public EOEditingContext editingContext()
    {
        return file().editingContext();
    }


    public String toAddresses()
    {
        return toAddresses;
    }


    public void setToAddresses(String newToAddresses)
    {
        toAddresses = newToAddresses;
    }


    /** @TypeInfo com.gvcsitemaker.core.Group */
    public NSMutableArray selectedAccessGroups()
    {
        return selectedAccessGroups;
    }

}
