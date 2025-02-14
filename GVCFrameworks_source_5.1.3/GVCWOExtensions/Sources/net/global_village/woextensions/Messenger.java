package net.global_village.woextensions;

import java.io.*;

import net.global_village.foundation.*;
import net.global_village.jmail.*;

/**
 * Messenger provides convenient methods on sending emails.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 4$
 */  
public class Messenger extends Object
{

    /**
     * Designated constructor.
     */
    public Messenger()
    {
        super();
    }



    /**
     * Convenience method for sending e-mails using JavaMail.  Throws a RuntimeException if error is encountered when sending email.
     *
     * @param fromAddress the sender of the email
     * @param toAddress the recipient of the email
     * @param messageSubject the subject of the email
     * @param messageContents the contents of the email
     */
    static public void sendMessage(String fromAddress,
                                   String toAddress,
                                   String messageSubject,
                                   String messageContents)
    {
        /** require
        [valid_fromAddress_param] fromAddress != null;
        [valid_toAddress_param] toAddress != null;
        [valid_messageSubject_param] messageSubject != null;
        [valid_messageContents_param] messageContents != null; **/

        JassAdditions.pre("Messenger", "sendCustomerServiceMessage", fromAddress.length() > 0);
        JassAdditions.pre("Messenger", "sendCustomerServiceMessage", toAddress.length() > 0);
        JassAdditions.pre("Messenger", "sendCustomerServiceMessage", messageSubject.length() > 0);
        JassAdditions.pre("Messenger", "sendCustomerServiceMessage", messageContents.length() > 0);

        MailDelivery smtpServer = MailDelivery.getInstance();
        net.global_village.jmail.Message aMessage = smtpServer.newMessage();

        try
        {
            aMessage.setFromAddress(fromAddress);
            aMessage.setToAddress(toAddress);
            aMessage.setSubject(messageSubject);
            aMessage.setText(messageContents);

            smtpServer.sendImmediate(aMessage);
        }
        catch (javax.mail.MessagingException e)
        {
            throw new RuntimeException("Failed to send email message. Please verify if all email addresses are valid.");
        }
    }



    /**
     * Sends a message to the support address as defined in the <code>CustomInfo,plist</code>
     *
     * @param messageSubject the subject of the message
     * @param messageContents the contents of the message
     */
    static public void sendSupportMessage(String messageSubject, String messageContents)
    {
        /** require
        [valid_messageSubject_param] messageSubject != null;
        [valid_messageContents_param] messageContents != null; **/
        JassAdditions.pre("Messenger", "sendSupportMessage", messageSubject.length() > 0);
        JassAdditions.pre("Messenger", "sendSupportMessage", messageContents.length() > 0);
        JassAdditions.pre("Messenger", "sendSupportMessage", WOApplication.appProperties().hasPropertyForKey("ApplicationsEmailAddress"));
        JassAdditions.pre("Messenger", "sendSupportMessage", WOApplication.appProperties().hasPropertyForKey("SupportsEmailAddress"));
        
        String applicationsEmailAddress = WOApplication.appProperties().stringPropertyForKey("ApplicationsEmailAddress");
        String supportAddress = WOApplication.appProperties().stringPropertyForKey("SupportsEmailAddress");
        Messenger.sendMessage(applicationsEmailAddress,
                              supportAddress,
                              messageSubject,
                              messageContents);
    }



    /**
     * Sends an email consisting of the details of theException and extraInfo to the supportAddress as defined in the <code>CustomInfo,plist</code>
     *
     * @param theException the exception
     * @param extraInfo the extra information relating to the exception
     */
    static public void emailException(Throwable theException, String extraInfo)
    {
        /** require [valid_theException_param] theException != null; **/
        JassAdditions.pre("Messenger", "emailException", WOApplication.appProperties().hasPropertyForKey("SupportsEmailAddress"));

        String exceptionDetails = "Name: " + theException.getClass().getName();
        exceptionDetails += "\n" + theException.getMessage();

        if (theException instanceof com.webobjects.foundation.NSValidation.ValidationException)
        {
            exceptionDetails += "\nValidation failure: " +
            ((com.webobjects.foundation.NSValidation.ValidationException)theException).key() + "=" +
            ((com.webobjects.foundation.NSValidation.ValidationException)theException).object().toString();
        }

        // Add the stack trace
        StringWriter stringifier = new StringWriter();
        PrintWriter printifier = new PrintWriter( stringifier );
        theException.printStackTrace( printifier );
        exceptionDetails += "\nStackTrace\n" + stringifier.toString();

        Messenger.sendSupportMessage(WOApplication.application().name() + " Web Application Error Report", exceptionDetails);
    }
}
