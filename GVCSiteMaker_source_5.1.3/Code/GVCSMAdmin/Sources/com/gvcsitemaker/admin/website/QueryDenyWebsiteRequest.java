// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.admin.website;
import net.global_village.foundation.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.support.*;
import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * This page either sends a query about a website request, or denies one.
 */
public class QueryDenyWebsiteRequest extends WOComponent implements SMSecurePage
{
    protected static final int QUERY_MODE = 1;
    protected static final int DENY_MODE = 2;
    
    protected int currentMode = 0;  // Invalid initial value
    protected String toAddresses;
    protected String subject;
    protected String message;
    protected WebsiteRequest theWebsiteRequest;
    protected String subjectErrorMessage;
    protected String toAddressesErrorMessage;
    
    
    /**
     * Designated constructor.
     */
    public QueryDenyWebsiteRequest(WOContext context) 
    {
        super(context);
    }



    /**
     * Sets up this page to deny <code>aWebsiteRequest</code>.
     * 
     * @param aWebsiteRequest the WebsiteRequest to deny.
     */
    public void denyWebsiteRequest(WebsiteRequest aWebsiteRequest)
    {
        setCurrentMode(DENY_MODE);
        setTheWebsiteRequest(aWebsiteRequest);
    }



    /**
     * Sets up this page to query <code>aWebsiteRequest</code>.
     * 
     * @param aWebsiteRequest the WebsiteRequest to deny.
     */
    public void queryWebsiteRequest(WebsiteRequest aWebsiteRequest)
    {
        setCurrentMode(QUERY_MODE);
        setTheWebsiteRequest(aWebsiteRequest);
    }



    /**
     * Sets the WebsiteRequest to work with and established some message defaults.
     * 
     * @param newTheWebsiteRequest the WebsiteRequest to work with 
     */
    protected void setTheWebsiteRequest(WebsiteRequest newTheWebsiteRequest) 
    {
        theWebsiteRequest = newTheWebsiteRequest;
        setToAddresses(theWebsiteRequest().requester().emailAddress());
        setSubject("Your " + SMApplication.productName() + " Request");
        setMessage(defaultMessage());
    }
    
    
    
    /**
     * Action method for the Send button.  Validates input and re-displays page with error messages
     * if input is not valid.  If input is valid, processes a Website Request denial or query and
     * returns the ManagePendingWebsiteRequests page.
     * 
     * @return this page if input is invalid, the ManagePendingWebsiteRequests page otherwise
     */
    public WOComponent processAction() 
    {
        WOComponent nextPage = context().page();  // Assume errors
        
        validateInput();
        if ( ! hasValidationErrors())
        {
            NSArray respondToAddresses = new UserIDTokenizer(toAddresses()).emailAddressesOfValidUserIDs();
            
            // For some reason empty messages are OK.  Change null to empty string for better results
            if (message() == null)
            {
                setMessage("");
            }
            
            // Use a NotifyingEditingContext as this works with WebsiteRequest to send out e-mails
            EOEditingContext ec = ((SMSession)session()).newEditingContext();
            try
            {
                WebsiteRequest localRequest = (WebsiteRequest) EOUtilities.localInstanceOfObject(ec, theWebsiteRequest());
                User localUser = (User) EOUtilities.localInstanceOfObject(ec, currentUser());
                if (isDenyMode())
                {
                    localRequest.deny(localUser, respondToAddresses, subject(), message());
                }
                else if (isQueryMode())
                {
                    localRequest.query(localUser, respondToAddresses, subject(), message());
                }
                 
                ec.saveChanges();
                nextPage = pageWithName("ManagePendingWebsiteRequests");
            }
            finally
            {
                ((SMSession)session()).unregisterEditingContext(ec);
            }
        }
        
        return nextPage;        
    }

    
    
    /**
     * Action method to cancel the action and return to the ManagePendingWebsiteRequests page
     * 
     * @return ManagePendingWebsiteRequests page
     */
    public WOComponent cancel() 
    {
        return pageWithName("ManagePendingWebsiteRequests");
    }



    /**
     * Returns the default message for the current mode.
     * 
     * @return the default message for the current mode
     */
    public String defaultMessage()
    {
        /** require [has_request] theWebsiteRequest() != null;   **/ 
        StringBuffer defaultMessage = new StringBuffer(2048);
        defaultMessage.append("Regarding this website request:");
        defaultMessage.append("\nSite ID: ");
        defaultMessage.append(theWebsiteRequest().siteID());
        defaultMessage.append("\nRequested by: ");
        defaultMessage.append(theWebsiteRequest().requester().userID());
        defaultMessage.append("\nOrg Unit: ");
        defaultMessage.append(theWebsiteRequest().orgUnit().unitName());
        defaultMessage.append("\nRequest Note: ");
        if (theWebsiteRequest().requesterNote() != null)
        {
            defaultMessage.append(theWebsiteRequest().requesterNote());
        }
        
        return defaultMessage.toString();
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Validates user input and sets UI error messages if invalid input found.
     */
    public void validateInput()
    {
        setToAddressesErrorMessage(null); 
        setSubjectErrorMessage(null);
        
        UserIDTokenizer userIDTokenizer = new UserIDTokenizer(toAddresses());

        if (userIDTokenizer.foundInvalidUserIDs())
        {
            setToAddressesErrorMessage("These To addresses are not acceptable: " + 
                userIDTokenizer.invalidUserIDs().componentsJoinedByString(", "));
        }
        else if (userIDTokenizer.validUserIDs().count() == 0)
        {
            setToAddressesErrorMessage("This message must be sent to at least one person.");
        }

        if (StringAdditions.isEmpty(subject()))
        {
            setSubjectErrorMessage("You must enter a subject for this message");
        }
    }



    /**
     * Returns <code>true</code> if <code>validateInput()</code> found any validation errors.
     * 
     * @return <code>true</code> if <code>validateInput()</code> found any validation errors
     */
    public boolean hasValidationErrors()
    {
        return (subjectErrorMessage() != null) || (toAddressesErrorMessage() != null);
    }
    
    
    
    /**
     * Returns <code>true</code> is this page is in "deny Website Request" mode.
     *  
     * @return <code>true</code> is this page is in "deny Website Request" mode
     */
    public boolean isDenyMode()
    {
        return currentMode == DENY_MODE;       
    }



    /**
     * Returns <code>true</code> is this page is in "query Website Request" mode.
     *  
     * @return <code>true</code> is this page is in "query Website Request" mode
     */
    public boolean isQueryMode()
    {
        return currentMode == QUERY_MODE;       
    }



    /**
     * Returns title for this page appropriate to current mode.
     *  
     * @return title for this page appropriate to current mode
     */
    public String pageTitle() 
    {
        return isQueryMode() ? "Website Request Query" : "Close Website Request";
    }
    
    
    /* ********** Generic setters and getters ************** */

    public int currentMode() {
        return currentMode;
    }
    public void setCurrentMode(int i) {
        currentMode = i;
    }

    public String toAddresses() {
        return toAddresses;
    }
    public void setToAddresses(String newToAddresses) {
        toAddresses = newToAddresses;
    }
    
    public String subject() {
        return subject;
    }
    public void setSubject(String newSubject) {
        subject = newSubject;
    }
    
    public String message() {
        return message;
    }
    public void setMessage(String newMessage) {
        message = newMessage;
    }
    
    public WebsiteRequest theWebsiteRequest() {
        return theWebsiteRequest;
    }

    public User currentUser() {
        return ((SMSession)session()).currentUser();
    }
    
    public EOEditingContext editingContext() {
        return theWebsiteRequest().editingContext();
    }
   
    public String subjectErrorMessage() {
        return subjectErrorMessage;
    }
    public void setSubjectErrorMessage(String newSubjectErrorMessage) {
        subjectErrorMessage = newSubjectErrorMessage;
    }
    
    public String toAddressesErrorMessage() {
        return toAddressesErrorMessage;
    }
    public void setToAddressesErrorMessage(String newToAddressesErrorMessage) {
        toAddressesErrorMessage = newToAddressesErrorMessage;
    }
    
}
