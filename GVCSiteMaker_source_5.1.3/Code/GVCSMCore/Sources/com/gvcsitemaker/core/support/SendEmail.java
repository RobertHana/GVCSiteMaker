// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.support;

import java.util.*;

import net.global_village.foundation.*;
import net.global_village.jmail.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.components.*;
import com.webobjects.appserver.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


/**
 * Centralizes e-mail sending services for GVC.SiteMaker.
 */
public class SendEmail extends Object
{
    
    /**
     * Sends introductory message to newly created external (GVC.SiteMaker) managed user.
     *  
     * @param createdUser the newly created user that this message is being sent for
     */
    public static void sendUserCreationMessage(User createdUser) 
    {
        /** require [valid_createdUser] createdUser != null;  **/
        
        String subject = SMApplication.appProperties()
            .stringPropertyForKey(SMApplication.UserCreationSubjectKey);
        String userCreationMessage = UserCreationMessage.messageFor(createdUser);

        sendMessage(userCreationMessage, subject, createdUser.emailAddress());
    }



    /**
     * Sends password and link to change it to a GVC.SiteMaker managed user who has forgotten their 
     * password.
     *  
     * @param password the password to send
     * @param userEmail the user's e-mail address
     */
    public static void sendPasswordToForgetfulUser(WORequest request, String password, String recipient) 
    {
        /** require [valid_request] request!= null;
                    [valid_password] password != null;
                    [valid_recipient] recipient != null;  **/

        String subject = productName() + " password reminder"; 
        String passwordMessage =
            "This email was sent in response to your request from the " + productName() + " website.\n\n" +
            "Your password is as follows:\n\n\tpassword: " +
            password +
            "\n\nIf you would like to change your password, you can do so here:\n\n\t" +
            SMActionURLFactory.changePasswordURL(request, recipient);

        sendMessage(passwordMessage, subject, recipient);
    }


  
    /**
     * Sends an e-mail to the admins of <code>orgUnitToCreateSiteIn</code> (or to nearest admins 
     * above that unit if that unit has no direct admins), requesting that a new site be created in 
     * that OrgUnit with a siteID of <code>newSiteID</code> for the User <code>userToCeateSiteFor
     * </code>. The requesting user is CC'd on the message.
     *
     * @param orgUnitToCreateSiteIn the OrgUnit that the new site should be created under
     * @param userToCeateSiteFor the User who has requested the new site
     * @param newSiteID the siteID that the User has requested
     * @param requestorComments free form comments from the person asking for this site
     */     
    public static void sendRequestForNewSite( OrgUnit orgUnitToCreateSiteIn,
                                      User userToCreateSiteFor,
                                      String newSiteID,
                                      String requestorComments)
    {
        /** require
        [org_unit_to_create_site_in_not_null] orgUnitToCreateSiteIn != null;
        [user_to_create_site_for_not_null] userToCreateSiteFor != null;
        [new_site_id_not_null] newSiteID != null; **/
        
        NSArray recipients = (NSArray)orgUnitToCreateSiteIn.nearestAdmins().valueForKey("emailAddress");

        String subject =  "New " + productName() + " site request! (SiteID = " + newSiteID + ")";
        StringBuffer message = new StringBuffer(2048);
        message.append("New ");
        message.append(productName());
        message.append(" Site Request");
        message.append("\n\nRequested Site ID : ");
        message.append(newSiteID);
        message.append("\n\nRequested by      : ");
        message.append(userToCreateSiteFor.userID());
        message.append("\nCreate in Org Unit: ");
        message.append(orgUnitToCreateSiteIn.unitName());
        
        message.append("\n\nSpecial Requests / Comments:\n");
        message.append((requestorComments != null) ? requestorComments : "(none)");
        
        message.append("\n\nUnit Administrator, please create this site as soon as possible.\n");
        message.append(SMActionURLFactory.managePendingRequestsURL());
        
        composePlainTextEmail(defaultSender(),
                              recipients,
                              new NSArray(userToCreateSiteFor.emailAddress()),
                              subject,
                              message.toString());
    }

    
    
    /**
     * Sends message to owner of processedTask when Task succeeded
     *  
     * @param processedTask the Task related with the message
     */
    public static void sendSuccessfulTaskProcessingMessage(Task processedTask) 
    {
        /** require 
             [valid_processedTask] processedTask != null; 
             [owner_not_null] processedTask.owner()!= null; 
             **/
        
        String template = SMApplication.appProperties().stringPropertyForKey("QueuedTaskMessageSuccessTemplate");
        String processingMessage = QueuedTaskMessage.messageFor(processedTask, template);
        String subject = productName() + ": Successful Task Processing Notification";
        
        sendMessage(processingMessage, subject, processedTask.owner().emailAddress());
    }    

    
    
    /**
     * Sends message to owner of processedTask when Task failed.
     *  
     * @param processedTask the Task related with the message
     */
    public static void sendFailedTaskProcessingMessage(Task processedTask) 
    {
        /** require 
             [valid_processedTask] processedTask != null; 
             [owner_not_null] processedTask.owner()!= null; 
             **/
        
        String template = SMApplication.appProperties().stringPropertyForKey("QueuedTaskMessageFailureTemplate");
        String processingMessage = QueuedTaskMessage.messageFor(processedTask, template);
        String subject = productName() + ": Failed Task Processing Notification";
        
        sendMessage(processingMessage, subject, processedTask.owner().emailAddress());
    }    
    

    
    /**
     * Convenience method to use when message is from defaultSender() and there is only a single
     * recipient.
     * 
     * @param message the message to send
     * @param subject the subject of the message to send
     * @param recipient the single recipient
     */
    public static void sendMessage (String message, String subject, String recipient) 
    {
        /** require [valid_recipient] recipient != null;
                    [valid_subject] subject != null;
                    [valid_message] message != null;                    **/
        composePlainTextEmail(defaultSender(), 
                              new NSArray(recipient), 
                              NSArray.EmptyArray,
                              subject,
                              message);
    }



    /**
     * Workhorse method to use when there are multiple recipients and the possibility of CC 
     * addresses.  The message is sent in a single operation.  All recipients can see each other's
     * address.
     *  
     * @param sender the from/reply to address
     * @param toAddresses the addresses to send this message to
     * @param ccAddresses the addresses to copy this message to
     * @param subject the subject of the message to send
     * @param message the message to send
     */
    public static void composePlainTextEmail(String sender,
                                      NSArray toAddresses,
                                      NSArray ccAddresses,
                                      String subject,
                                      String messageText)

    {
        /** require [valid_sender] sender != null;
                    [valid_toAddresses] toAddresses != null;
                    [has_toAddresses] toAddresses.count() > 0;
                    [valid_ccAddresses] ccAddresses != null;
                    [valid_subject] subject != null;
                    [valid_messageText] messageText != null;                    **/
        
        // Handle special developement mode case: direct all e-mail to developer
        if (SMApplication.appProperties().booleanPropertyForKey("DevelopmentMode"))
        {
            messageText = "Original Recipients: " + toAddresses.componentsJoinedByString(", ") +
                      "\nOriginal CC: " + ccAddresses.componentsJoinedByString(", ") +
                      "\n\n" + messageText;
            
            toAddresses = new NSArray(SMApplication.appProperties().stringPropertyForKey("DeveloperID"));
            ccAddresses = NSArray.EmptyArray;
        }
                                     
        ThreadedMailAgent mailAgent = ThreadedMailAgent.getInstance();
        Message message = mailAgent.newMessage();
        try
        {
            message.setFromAddress(sender);
            message.setReplyToAddress(sender);
            message.setToAddresses(toAddresses);
            message.setCCAddresses(ccAddresses);
               
            message.setSubject(subject);
            message.setText(messageText);
            
            mailAgent.sendMessageDeferred(message);
        }
        catch (Exception e)
        {
            throw new ExceptionConverter(e);
        }        
    }



    /**
     * Workhorse method to use when there are multiple recipients must not be able to see each 
     * other's address.  The message is sent in multiple operations, one per recipient.  CC addresses
     * are not possible in this situation.
     *  
     * @param sender the from/reply to address
     * @param toAddresses the addresses to send this message to
     * @param subject the subject of the message to send
     * @param messageText the message to send
     */    
    public static void sendIndividualMessages(String sender,
                                              NSArray toAddresses,
                                              String subject,
                                              String messageText)
    {
        /** require [valid_sender] sender != null;
                    [valid_toAddresses] toAddresses != null;
                    [has_toAddresses] toAddresses.count() > 0;
                    [valid_subject] subject != null;
                    [valid_messageText] messageText != null;                    **/
        
        // Handle special developement mode case: direct all e-mail to developer
        if (SMApplication.appProperties().booleanPropertyForKey("DevelopmentMode"))
        {
            messageText = "Original Recipients: " + toAddresses.componentsJoinedByString(", ") +
                      "\n\n" + messageText;
            
            toAddresses = new NSArray(SMApplication.appProperties().stringPropertyForKey("DeveloperID"));
        }
        
        ThreadedMailAgent mailAgent = ThreadedMailAgent.getInstance();
        Enumeration toAddressEnumerator = toAddresses.objectEnumerator();
        while (toAddressEnumerator.hasMoreElements())
        {
            String toAddress = (String) toAddressEnumerator.nextElement();
            
            Message message = mailAgent.newMessage();
            try
            {
                message.setFromAddress(sender);
                message.setReplyToAddress(sender);
                message.setToAddress(toAddress);
                
                message.setSubject(subject);
                message.setText(messageText);
                
                mailAgent.sendMessageDeferred(message);
            }
            catch (javax.mail.internet.AddressException badAddress)
            {
                // Auto EC locking is not occuring here, so need to lock manually
                EOEditingContext ec = new EOEditingContext();
                ec.lock();
                try
                {
                    ErrorLogger.logErrorLocation(ec, null, badAddress, "BAD ADDRESS" );
                }
                finally
                {
                    ec.unlock();
                    ec.dispose();
                }
            }
            catch (Exception e)
            {
                throw new ExceptionConverter(e);
            }
        }
    }




    /**
     * Returns the default from address for this installation.
     * 
     * @return the default from address for this installation
     */
    public static String defaultSender()
    {
        return SMApplication.appProperties().stringPropertyForKey("FromAddress");
        /** ensure [valid_result] Result != null;  **/
    }
    
    
    
    /**
     * Support for non standard GVC.SiteMaker product name.
     * 
     * @ return the product name in use
     */
    public static String productName()
    {
        return SMApplication.productName();
        /** ensure [valid_result] Result != null;  **/
    }


}
