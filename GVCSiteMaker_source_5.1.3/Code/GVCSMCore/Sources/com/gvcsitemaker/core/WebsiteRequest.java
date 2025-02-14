// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core;


import net.global_village.foundation.*;

import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * A WebsiteRequest tracks a request for a single Website from its initiation to its final status as 
 * granted or denied.    
 */
public class WebsiteRequest extends _WebsiteRequest implements net.global_village.eofvalidation.EditingContextNotification
{
    // Transient values for post-save notifications
    protected String responseFrom = null;
    protected NSArray responseTo = null;
    protected String responseSubject = null; 
    protected String responseMessage = null;
    protected boolean wasReferred = false;
    
    public static final NSComparator PendingRequestComparator = new PendingRequestComparator();
    public static final NSComparator CompletedRequestComparator = new CompletedRequestComparator();


    /**
     * Factory method to create new instances of WebsiteRequest.  Use this rather than calling the 
     * constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of Group or a subclass.
     */
    public static WebsiteRequest newWebsiteRequest()
    {
        return (WebsiteRequest) SMEOUtils.newInstanceOf("WebsiteRequest");

        /** ensure [result_not_null] Result != null; **/
    }
    
    
    
    /**
     * Creates a new WebsiteRequest and records the request in the administrative notes.  An e-mail 
     * regarding this request will be sent in didInsert().
     *  
     * @param ec the net.global_village.eofvalidation.NotifyingEditingContext to create this request in
     * @param requestingUser the User making this request
     * @param requestedUnit the OrgUnit request to contain this new site
     * @param requestedSiteID the requested ID for the Website
     * @param requestorComments free form comments from the requestor
     * @return the newly created and inserted WebsiteRequest
     */
    public static WebsiteRequest makeRequest(EOEditingContext ec,
                                             User requestingUser, 
                                             OrgUnit requestedUnit, 
                                             String requestedSiteID, 
                                             String requestorComments)
    {
        /** require [valid_ec] (ec != null) && (ec instanceof net.global_village.eofvalidation.NotifyingEditingContext);
                    [valid_user] requestingUser != null;
                    [user_in_same_ec] ec.equals(requestingUser.editingContext());
                    [valid_unit] requestedUnit != null;
                    [unit_in_same_ec] ec.equals(requestedUnit.editingContext());
                    [valid_siteID] requestedSiteID != null;
                    [valid_comments] requestorComments != null;                    **/
        
        WebsiteRequest request = newWebsiteRequest();
        ec.insertObject(request);
        
        request.setRequester(requestingUser);
        request.setOrgUnit(requestedUnit);
        request.setSiteID(requestedSiteID);
        request.setRequesterNote(requestorComments);
        
        NSArray adminRecipients = (NSArray)requestedUnit.nearestAdmins().valueForKey("emailAddress");
        request.addAdministrativeNote(requestingUser, 
                                      "\n<b>New Site Request sent to:</b> " + adminRecipients.componentsJoinedByString(", "));
        
        return request;
        /** ensure [valid_result] Result != null;
                   [user_set] Result.requester().equals(requestingUser);
                   [unit_set] Result.orgUnit().equals(requestedUnit);
                   [site_id_set] Result.siteID().equals(requestedSiteID);
                   [comments_recorded] Result.requesterNote().equals(requestorComments);       **/   
    }
    
    
    
    /**
     * Returns a list of pending requests orderd by OrgUnit hierarchy.
     * 
     * @param anAdmin the EOEditingContext to return the requests in
     * @return a list of pending requests orderd by OrgUnit hierarchy
     */
    public static NSArray pendingRequestsForAdmin(User anAdmin)
    {
        /** require [valid_admin] anAdmin != null;  **/
        EOEditingContext ec = anAdmin.editingContext();
        EOQualifier stateQualifier = new EOKeyValueQualifier("dateResolved", 
                                                             EOQualifier.QualifierOperatorEqual, 
                                                             null);
        EOQualifier matchingRequests = new EOAndQualifier(new NSArray( new EOQualifier[] {
            stateQualifier, anAdmin.adminableUnitsQualifier("orgUnit")}));
        EOFetchSpecification fetchspec = new EOFetchSpecification("WebsiteRequest", matchingRequests, null);
        fetchspec.setRefreshesRefetchedObjects(true);
        NSArray unsortedRequests = ec.objectsWithFetchSpecification(fetchspec); 
        return NSArrayAdditions.sortedArrayUsingComparator(unsortedRequests, PendingRequestComparator);
        /** ensure [valid_result] Result != null;
                  /# Jass can't handle this contract on a static method.  Lame.  
                     [valid_state]  forall i : {0 .. Result.count() - 1} # 
                                    ((WebsiteRequest)Result.objectAtIndex(i)).isPending(); #/ **/
    }
    
    
    
    /**
     * Returns a list of completed requests orderd by OrgUnit hierarchy.
     * 
     * @param ec the EOEditingContext to return the requests in
     * @return a list of completed requests orderd by OrgUnit hierarchy
     */
    public static NSArray completedRequestsForAdmin(User anAdmin)
    {
        /** require [valid_Admin] anAdmin != null;  **/
        EOEditingContext ec = anAdmin.editingContext();
        EOQualifier stateQualifier = new EOKeyValueQualifier("dateResolved", 
                                                             EOQualifier.QualifierOperatorNotEqual, 
                                                             null);
        EOQualifier matchingRequests = new EOAndQualifier(new NSArray( new EOQualifier[] {
            stateQualifier, anAdmin.adminableUnitsQualifier("orgUnit")}));
        EOFetchSpecification fetchspec = new EOFetchSpecification("WebsiteRequest", matchingRequests, null);
        fetchspec.setRefreshesRefetchedObjects(true);
        NSArray unsortedRequests = ec.objectsWithFetchSpecification(fetchspec); 
        return NSArrayAdditions.sortedArrayUsingComparator(unsortedRequests, CompletedRequestComparator);
        /** ensure [valid_result] Result != null;
                  /# [valid_state]  forall i : {0 .. Result.count() - 1} # ! ((WebsiteRequest)Result.objectAtIndex(i)).isPending(); #/ **/
    }



    /**
     * Overridden to set the dateRequested.
     * 
     * @see com.webobjects.eocontrol.EOEnterpriseObject#awakeFromFetch(com.webobjects.eocontrol.EOEditingContext)
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        setDateRequested(new NSTimestamp());
        /** ensure [date_set] dateRequested() != null;  **/
    }



    /**
     * Returns <code>true</code> if this request is still pending (has not been granted or denied).
     *  
     * @return <code>true</code> if this request is still pending
     */
    public boolean isPending()
    {
        return (website() == null) && (dateResolved() == null);
        /** ensure [valid_state] (Result && ! (hasBeenGranted() || hasBeenDenied())) ||
                                 ( (! Result) && (hasBeenGranted() || hasBeenDenied()));  **/
    }



    /**
     * Returns <code>true</code> if this request has been granted (is not pending or denied).
     *  
     * @return <code>true</code> if this request has been granted
     */
    public boolean hasBeenGranted()
    {
        return (website() != null) && (dateResolved() != null);
        /** ensure [valid_state] (Result && ! (isPending() || hasBeenDenied())) ||
                                 ( (! Result) && (isPending() || hasBeenDenied()));  **/
    }



    /**
     * Returns <code>true</code> if this request has been denied (is not pending or granted).
     *  
     * @return <code>true</code> if this request has been denied
     */
    public boolean hasBeenDenied()
    {
        return (website() == null) && (dateResolved() != null);
        /** ensure [valid_state] (Result && ! (hasBeenGranted() || isPending())) ||
                                 ( (! Result) && (hasBeenGranted() || isPending()));  **/

    }
   
   
    /**
     * Adds a query about this request to the administrative notes and prepares a message with this 
     * query to be sent when this request is saved.
     */
    public void query(User admin, NSArray respondingTo, String querySubject, String queryMessage)
    {
        /** require [valid_state] isPending();
                    [valid_user] admin != null;
                    [user_is_admin] admin.isUnitAdmin();
                    [user_in_same_ec] editingContext().equals(admin.editingContext());
                    [valid_addresses] respondingTo != null;
                    [valid_subject] querySubject != null;   
                    [valid_message] queryMessage != null;  
                                        [ec_correct_type] editingContext() instanceof net.global_village.eofvalidation.NotifyingEditingContext;  **/

        makeResponse(admin, respondingTo, querySubject, queryMessage, "Query");
        
        /** ensure [message_prepared] hasMessageToSend();  **/
    }

    
    

    /**
     * Refers this WebsiteRequest to a different OrgUnit.  As a result of this, a new Website Request
     * message is sent to the admins of the new unit and to the requestor.
     * 
     * @param admin the unit administrator who is referring the request
     * @param unitReferredTo the OrgUnit the request is being referred to
     * @param referralMessage optional notes from admin
     */
    public void referTo(User admin, OrgUnit unitReferredTo, String referralMessage)
    {
        /** require [valid_state] isPending();
                    [valid_user] admin != null;
                    [user_is_admin] admin.isUnitAdmin();
                    [user_in_same_ec] editingContext().equals(admin.editingContext());
                    [valid_message] referralMessage != null;  
                    [ec_correct_type] editingContext() instanceof net.global_village.eofvalidation.NotifyingEditingContext;  **/

        StringBuffer referralNotes = new StringBuffer(500);
        referralNotes.append("<b>\nResponse type:</b> Referral ");
      
        NSArray responseSentTo = (NSArray) unitReferredTo.nearestAdmins().valueForKey("emailAddress");
        referralNotes.append("\n<b>New Request Sent to:</b> ");
        referralNotes.append(responseSentTo.componentsJoinedByString(", "));

        referralNotes.append("\n<b>Notes:</b> ");
        referralNotes.append(referralMessage);
        
        addAdministrativeNote(admin, referralNotes.toString());
        
        setOrgUnit(unitReferredTo);
        
        // Flag for post-save e-mail
        wasReferred = true;
        
        /** ensure [request_referred] orgUnit().equals(unitReferredTo);  **/
    }

    
    

    /**
     * Marks this request as granted and records the website that was created as a result of this
     * request.
     * 
     * @param resultingWebsite the website that was created as a result of this request
     */
    public void grant(Website resultingWebsite)
    {
        /** require [valid_website] resultingWebsite != null;  
                    [valid_state] isPending();  
                    [ec_correct_type] editingContext() instanceof net.global_village.eofvalidation.NotifyingEditingContext;  **/
        setWebsite(resultingWebsite);
        setDateResolved(new NSTimestamp());
        /** ensure [valid_state] (hasBeenGranted() && (! isPending()) && ( ! hasBeenDenied()));  **/
    }
    

    
    /**
     * Marks this request as denied, records the denial in the administrative notes, and prepares a 
     * message to be sent when this is saved.
     */
    public void deny(User admin, NSArray respondingTo, String denialSubject, String denialMessage)
    {
        /** require [valid_state] isPending();
                    [valid_user] admin != null;
                    [user_is_admin] admin.isUnitAdmin();
                    [user_in_same_ec] editingContext().equals(admin.editingContext());
                    [valid_addresses] respondingTo != null;
                    [valid_subject] denialSubject != null;   
                    [valid_message] denialMessage != null;  
                    [ec_correct_type] editingContext() instanceof net.global_village.eofvalidation.NotifyingEditingContext;  **/
        
        makeResponse(admin, respondingTo, denialSubject, denialMessage, "Denial");
        setDateResolved(new NSTimestamp());
        
        /** ensure [valid_state] (hasBeenDenied() && (! isPending()) && ( ! hasBeenGranted()));  
                   [message_prepared] hasMessageToSend();                                         **/
    }

    
    
    /**
     * Adds a response to this request to the administrative notes AND prepares a message with this 
     * response to be sent when this request is saved.
     * 
     * @param admin the user making this response
     * @param respondingTo the e-mail addresses to which this response will be sent
     * @param subject the subject of the e-mail message
     * @param message the body of the e-mail message
     * @param responseType identifier for type of response to be added to administrativeNote
     */
    protected void makeResponse(User admin, 
                                NSArray respondingTo, 
                                String subject, 
                                String message,
                                String responseType)
    {
        /** require [valid_state] isPending();
                    [valid_user] admin != null;
                    [user_is_admin] admin.isUnitAdmin();
                    [user_in_same_ec] editingContext().equals(admin.editingContext());
                    [valid_addresses] respondingTo != null;
                    [valid_subject] subject != null;   
                    [valid_message] message != null;  
                    [valid_responseType] responseType != null;                    
                    [ec_correct_type] editingContext() instanceof net.global_village.eofvalidation.NotifyingEditingContext;  **/

        StringBuffer annotatedResponse = new StringBuffer(message.length() + 200);
        annotatedResponse.append("<b>\nResponse type:</b> ");
        annotatedResponse.append(responseType);
        annotatedResponse.append("\n<b>Sent to:</b> ");
        annotatedResponse.append(respondingTo.componentsJoinedByString(", "));
        annotatedResponse.append("\n<b>Subject:</b> ");
        annotatedResponse.append(subject);
        annotatedResponse.append("\n<b>Message:</b> ");
        annotatedResponse.append(message);
        
        addAdministrativeNote(admin, annotatedResponse.toString());
        
        responseFrom = admin.emailAddress();
        responseTo = respondingTo;
        responseSubject = subject; 
        responseMessage = message;
        
        /** ensure [message_prepared] hasMessageToSend();  **/
    }    
    
    
    
    /**
     * Returns the number of days it took to resolve this request.
     * 
     * @return the number of days it took to resolve this request
     */
    public Integer daysToResolve()
    {
        /** require [valid_state] ! isPending();  **/
        return new Integer(Date.daysInBetween(dateRequested(), dateResolved()));
    }
    
    
    
    /**
     * Returns the site ID of the website created in response to this request (which may have been
     * changed since the request was granted, the current site ID is returned).  If the request is 
     * pending or was denied, the request site ID is returned.
     * 
     * @return the site ID of the website created in response to this request or the requested ID 
     * if the request is not granted.
     */
    public String grantedSiteID()
    {
        return hasBeenGranted() ? website().siteID() : siteID();
    }
    


    /**
     * Returns the user ID of the owner of the website created in response to this request.  The
     * website may have been created with an owner different than the original requestor or the 
     * owner may have been changed since the request was granted; the current owner's user ID is 
     * returned).  If the request is pending or was denied, the user ID of the requestor is 
     * returned.
     * 
     * @return the user ID of the owner of the website created in response to this request or the 
     * requesting user's user ID if no site has been created.
     */
    public String grantedToUserID()
    {
        return hasBeenGranted() ? website().owner().userID() : requester().userID();
    }
    
    
    
    /**
     * Returns the parent OrgUnit of the website created in response to this request (which may have 
     * been changed since the request was granted, the current site ID is returned).  If the request 
     * is pending or was denied, the requested OrgUnit.
     * 
     * @return the parent OrgUnit of the website created in response to this request or the requested 
     * OrgUnit if the request has not been granted.
     */
    public OrgUnit grantedInUnit()
    {
        return hasBeenGranted() ? website().parentOrgUnit() : orgUnit();
    }

    
    
    /**
     * Pre-pends <code>notes</code> onto <code>administrativeNotes()</code> along with the current
     * date and the user ID of the administrator.  e.g.<br/>
     * 8/16/03 8:23 AM (johnpj): Owner sent an Email asking for more filespace. Checked with prof. 
     * and local unit admin, who agreed, so I raised it to 100 Mb.
     * 
     * @param adminUser administrative user creating this note
     * @param notes contents of the note
     */
    public void addAdministrativeNote(User adminUser, String notes)
    {
        /** require [valid_user] adminUser != null;  
                    [user_in_same_ec] editingContext().equals(adminUser.editingContext());
                    [valid_notes] notes != null;  **/
        
        StringBuffer adminNotes = new StringBuffer();
        
        // Date
        NSTimestampFormatter dateFormatter = SMApplication.appProperties().
            timestampFormatterForKey("DateAndTimeFormatter");
        adminNotes.append("<b>");
        adminNotes.append(dateFormatter.format(new NSTimestamp()));

        // User
        adminNotes.append("</b> (");
        adminNotes.append(adminUser.userID());
        adminNotes.append("): ");

        // New Notes
        adminNotes.append(notes);
        
        // Previous notes
        if ( ! StringAdditions.isEmpty(administrativeNotes()))
        {
            adminNotes.append("\n\n");
            adminNotes.append(administrativeNotes());
        }

        setAdministrativeNotes(adminNotes.toString());
    }



    /**
     * Returns <code>true</code> if the site ID in this request is being used by an existing site.
     *  
     * @return <code>true</code> if the site ID in this request is being used by an existing site
     */
    public boolean isSiteIDInUse()
    {
        /** require [correct_state] isPending();  
                    [has_site_id] (siteID() != null) & (siteID().length() > 0);  **/
        return Website.websiteForSiteID(siteID(), editingContext()) != null;
    }
    
    
    /**
     * Compares pending WebsiteRequests by requested OrgUnit and then by date of request.
     */ 
     static class PendingRequestComparator extends NSComparator
     {

        /**
         * Comparison rules:<br/>
         * <ul>
         * <li>Requests for different OrgUnits compare by unit hierarchy</li>
         * <li>Requests for the same OrgUnit compare by date of original request</li>
         * </ul> 
         * @see com.webobjects.foundation.NSComparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object firstObject, Object secondObject) throws ComparisonException
        {
            if ((firstObject == null) || (secondObject == null))
            {
                throw new NSComparator.ComparisonException("Can't compare null objects");
            }
            
            WebsiteRequest firstRequest = (WebsiteRequest)firstObject;
            WebsiteRequest secondRequest = (WebsiteRequest)secondObject;
            int result = OrgUnit.HierarchicalComparator.compare(firstRequest.orgUnit(), 
                                                                secondRequest.orgUnit()); 
            
            if (result == NSComparator.OrderedSame)
            {
                result = NSComparator.AscendingTimestampComparator.compare(firstRequest.dateRequested(), 
                                                                           secondRequest.dateRequested());
            }
                        
            return result;
        }
         
         
     }



    /**
     * Compares completed WebsiteRequests by requested OrgUnit, requesting user, and then by granted 
     * site ID.
     */ 
     static class CompletedRequestComparator extends NSComparator
     {

        /**
         * Comparison rules:<br/>
         * <ul>
         * <li>Requests for different OrgUnits compare by unit name</li>
         * <li>Requests for the same OrgUnit compare by User ID of requestor</li>
         * <li>Requests for the same OrgUnit and User compare by granted site ID (or requested ID if 
         * not granted)</li>
         * </ul> 
         * @see com.webobjects.foundation.NSComparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object firstObject, Object secondObject) throws ComparisonException
        {
            if ((firstObject == null) || (secondObject == null))
            {
                throw new NSComparator.ComparisonException("Can't compare null objects");
            }

            WebsiteRequest firstRequest = (WebsiteRequest)firstObject;
            WebsiteRequest secondRequest = (WebsiteRequest)secondObject;

            if (firstRequest.isPending() || secondRequest.isPending())
            {
                throw new NSComparator.ComparisonException("Can't compare pending requests");
            }

            int result = OrgUnit.HierarchicalComparator.compare(firstRequest.orgUnit(), 
                                                                secondRequest.orgUnit()); 
            if (result == NSComparator.OrderedSame)
            {
                result = NSComparator.AscendingStringComparator.compare(firstRequest.grantedToUserID(), secondRequest.grantedToUserID());
                
                if (result == NSComparator.OrderedSame)
                {
                    result = NSComparator.AscendingStringComparator.compare(firstRequest.grantedSiteID(), secondRequest.grantedSiteID());
                }
            }
                        
            return result;
        }
         
     }

    /* Implmentation of EditingContextNotification */


    /**
     * Called after insertObject is invoked on super (EOEditingContext).
     */
    public void didInsert() {}
    

    /**
     * Called after a new WebsiteRequest is created to send an e-mail to the requestor and unit 
     * admins.  Also called when an existing WebsiteRequest is referred to a new OrgUnit.  
     */
    public void hasInserted() 
    {        
        SendEmail.sendRequestForNewSite(orgUnit(), requester(), siteID(), requesterNote());
    }
    

    /**
     * Called before deleteObject is invoked on super (EOEditingContext).
     */
    public void willDelete() {}


    /**
     * Called after the deletion is processed by the EOEditingContext.  At this point the delete rules have been applied and changes propogated to related objects.
     */
    public void didDelete() {}
    

    /**
     * Called after a deleted object has been removed from the persistent object store.  This would handle the need to clean up related, non-EO, resources when the EO was deleted.
     */
    public void hasDeleted() {}


    /**
     * Called before saveChanges is invoked on super (EOEditingContext).  This would allow things such as setting the dateModified before saving an EO.  Note that changes in this method may result in more updated (or inserted or deleted!) objects, so it should iterate until there are no more changes.
     */
    public void willUpdate() {}
    

    /**
     * Called when a WebsiteRequest is updated to check and see if any e-mail messages need to be 
     * sent.  
     */
    public void hasUpdated()
    {
        try
        {
            if (wasReferred)
            {
                // Treat as new request
                hasInserted();
            }
            else if (hasMessageToSend())
            {
                SendEmail.composePlainTextEmail(responseFrom, responseTo, NSArray.EmptyArray,
                                                responseSubject, responseMessage);
            }
        }
        finally
        {
            // Reset transient values so action is not repeated
            responseTo = null;
            responseSubject = null; 
            responseMessage = null;
            wasReferred = false;
        }
    }


    /**
     * Called after revert is invoked on our EOEditingContext.
     */
    public void hasReverted() {}



    public boolean hasMessageToSend()
    {
        return (responseTo != null) && (responseSubject != null) && (responseMessage != null);
    }
}

