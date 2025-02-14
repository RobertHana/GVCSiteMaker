package net.global_village.jmail;


import javax.mail.*;
import javax.mail.internet.*;

import com.webobjects.foundation.*;

import net.global_village.foundation.*;

/**
 * Extends com.sun.mail.smtp.SMTPMessage with some convenience methods and support for using NSArray in place of
 * Address[]. This class can be used to send messages with MailDelivery.<br>
 * <br>
 * It also provides methods for accessing addresses without needing to handle the pointless MessagingException. The docs
 * don't state why this can be thown and there is no apparent way to cause it. It is probably just another pointless
 * exception in the Java APIs or the result of some code in javax.mail using a method that throws this exception not
 * being smart enough to realize that it can not happen in that situation and not propagating it needlessly. So we will
 * do the reasonable thing and convert it to RuntimeException (as if it could ever happen!) so those using our class
 * don't need to deal with this nonsense.
 *
 * @author Copyright (c) 2001-2005 Global Village Consulting, Inc. All rights reserved. This software is published under
 *         the terms of the Educational Community License (ECL) version 1.0, a copy of which has been included with this
 *         distribution in the LICENSE.TXT file.
 */
public class Message extends com.sun.mail.smtp.SMTPMessage
{

    private Object associatedObject;


    public Message(MimeMessage source) throws MessagingException
    {
        super(source);
    }



    public Message(Session aSession)
    {
        super(aSession);
    }



    public Message(Session aSession, java.io.InputStream is) throws MessagingException
    {
        super(aSession, is);
    }



    /**
     * Sets the message "from:" address to anAddress.
     *
     * @param anAddress - e-mail address to use as "from" address for this javax.mail.Message.
     * @exception AddressException if one of the e-mail addresses in emailAddresses can not be parsed.
     */
    public void setFromAddress(String anAddress)
    {
        /** require [anAddress_not_null] anAddress != null; **/

        try
        {
            setFrom(new InternetAddress(anAddress));
        }
        catch (MessagingException e)
        {
            throw new ExceptionConverter(e);
        }

        /** ensure [fromAddress_equals_anAddress] fromAddress().equals(anAddress); **/
    }



    /**
     * Returns the e-mail addresses used as the "from:" address for this javax.mail.Message.
     *
     * @return the e-mail addresses in the list of direct "from:" recipients.
     */
    public String fromAddress()
    {
        String fromAddress;

        try
        {
            fromAddress = (String) (EmailAddress.emailAddresses((InternetAddress[]) getFrom())).objectAtIndex(0);
        }
        catch (MessagingException e)
        {
            throw new ExceptionConverter(e);
        }

        return fromAddress;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Adds anAddress to the list of "reply to" addresses.
     *
     * @param anAddress - e-mail address to add to the list of "reply to" addresses
     * @exception AddressException if the e-mail addresses anAddress can not be parsed.
     */
    public void setReplyToAddress(String anAddress)
    {
        /** require [anAddress_not_null] anAddress != null; **/

        setReplyToAddresses(new NSArray(anAddress));

        /** ensure [replyToAddresses_contains_anAddress] replyToAddresses().containsObject(anAddress); **/
    }



    /**
     * Adds the e-mail addresses in theAddresses to the list of "reply to" addresses. Note that this is additive and
     * does not replace any existing addresses.
     *
     * @param theAddresses - e-mail addresses to add to the list of direct "to:" recipients.
     * @exception AddressException if one of the e-mail addresses in emailAddresses can not be parsed.
     */
    public void setReplyToAddresses(NSArray theAddresses)
    {
        /** require [theAddresses_not_null] theAddresses != null; **/

        try
        {
            setReplyTo(EmailAddress.internetAddresses(theAddresses));
        }
        catch (MessagingException e)
        {
            throw new ExceptionConverter(e);
        }

        /***************************************************************************************************************
         * ensure [result_is_subset] net.global_village.foundation.Collection.collectionIsSubsetOfCollection(
         * replyToAddresses(), theAddresses);
         **************************************************************************************************************/
    }



    /**
     * Returns the e-mail addresses in the list of direct "to:" recipients.
     *
     * @return the e-mail addresses in the list of direct "to:" recipients.
     */
    public NSArray replyToAddresses()
    {
        NSArray replyToAddresses;

        try
        {
            replyToAddresses = EmailAddress.emailAddresses((InternetAddress[]) getReplyTo());
        }
        catch (MessagingException e)
        {
            throw new ExceptionConverter(e);
        }

        return replyToAddresses;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Adds anAddress to the list of direct "to:" recipients.
     *
     * @param anAddress - e-mail address to add to the list of direct "to:" recipients.
     * @exception AddressException if the e-mail addresses anAddress can not be parsed.
     */
    public void setToAddress(String anAddress) throws AddressException
    {
        /** require [anAddress_not_null] anAddress != null; **/

        setToAddresses(new NSArray(anAddress));

        /** ensure [toAddresses_contains_anAddress] toAddresses().containsObject(anAddress); **/
    }



    /**
     * Adds the e-mail addresses in theAddresses to the list of direct "to:" recipients. Note that this is additive and
     * does not replace any existing addresses.
     *
     * @param theAddresses - e-mail addresses to add to the list of direct "to:" recipients.
     * @exception AddressException if one of the e-mail addresses in emailAddresses can not be parsed.
     */
    public void setToAddresses(NSArray theAddresses) throws AddressException
    {
        /** require [theAddresses_not_null] theAddresses != null; **/

        addRecipientsWithoutException(javax.mail.Message.RecipientType.TO, EmailAddress.internetAddresses(theAddresses));

        /**
         * ensure [result_is_subset]
         * net.global_village.foundation.Collection.collectionIsSubsetOfCollection(toAddresses(), theAddresses); *
         */
    }



    /**
     * Returns the e-mail addresses in the list of direct "to:" recipients.
     *
     * @return the e-mail addresses in the list of direct "to:" recipients.
     */
    public NSArray toAddresses()
    {
        return EmailAddress
                        .emailAddresses((InternetAddress[]) getRecipientsWithoutException(javax.mail.Message.RecipientType.TO));

        /**
         * ensure [result_not_null] Result != null; [correct_result_count] Result.count() ==
         * getRecipientsWithoutException(javax.mail.Message.RecipientType.TO).length;
         */
    }



    /**
     * Adds anAddress to the list of direct "cc:" recipients.
     *
     * @param anAddress - e-mail address to add to the list of direct "cc:" recipients.
     * @exception AddressException if the e-mail addresses anAddress can not be parsed.
     */
    public void setCCAddress(String anAddress) throws AddressException
    {
        /** require [anAddress_not_null] anAddress != null; **/

        setCCAddresses(new NSArray(anAddress));

        /** ensure [ccAddresses_contains_anAddress] ccAddresses().containsObject(anAddress); **/

    }



    /**
     * Adds the e-mail addresses in theAddresses to the list of direct "cc:" recipients. Note that this is additive and
     * does not replace any existing addresses.
     *
     * @param theAddresses - e-mail addresses to add to the list of direct "cc:" recipients.
     * @exception AddressException if one of the e-mail addresses in emailAddresses can not be parsed.
     */
    public void setCCAddresses(NSArray theAddresses) throws AddressException
    {
        /** require [theAddresses_not_null] theAddresses != null; **/

        addRecipientsWithoutException(javax.mail.Message.RecipientType.CC, EmailAddress.internetAddresses(theAddresses));

        /***************************************************************************************************************
         * ensure [result_is_subset] net.global_village.foundation.Collection.
         * collectionIsSubsetOfCollection(ccAddresses(), theAddresses);
         **************************************************************************************************************/
    }



    /**
     * Returns the e-mail addresses in the list of direct "cc:" recipients.
     *
     * @return the e-mail addresses in the list of direct "cc:" recipients.
     */
    public NSArray ccAddresses()
    {
        return EmailAddress
                        .emailAddresses((InternetAddress[]) getRecipientsWithoutException(javax.mail.Message.RecipientType.CC));

        /**
         * ensure [result_not_null] Result != null; [correct_result_count] Result.count() ==
         * getRecipientsWithoutException(javax.mail.Message.RecipientType.CC).length;
         */
    }



    /**
     * Adds anAddress to the list of direct "bcc:" recipients.
     *
     * @param anAddress - e-mail address to add to the list of direct "bcc:" recipients.
     * @exception AddressException if the e-mail addresses anAddress can not be parsed.
     */
    public void setBCCAddress(String anAddress) throws AddressException
    {
        /** require [anAddress_not_null] anAddress != null; **/

        setBCCAddresses(new NSArray(anAddress));

        /** ensure [bccAddresses_contains_anAddress] bccAddresses().containsObject(anAddress); **/

    }



    /**
     * Adds the e-mail addresses in theAddresses to the list of direct "bcc:" recipients. Note that this is additive and
     * does not replace any existing addresses.
     *
     * @param theAddresses - e-mail addresses to add to the list of direct "bcc:" recipients.
     * @exception AddressException if one of the e-mail addresses in emailAddresses can not be parsed.
     */
    public void setBCCAddresses(NSArray theAddresses) throws AddressException
    {
        /** require [theAddresses_not_null] theAddresses != null; **/

        addRecipientsWithoutException(javax.mail.Message.RecipientType.BCC, EmailAddress
                        .internetAddresses(theAddresses));

        /***************************************************************************************************************
         * ensure [result_is_subset] net.global_village.foundation.Collection.
         * collectionIsSubsetOfCollection(bccAddresses(), theAddresses);
         **************************************************************************************************************/

    }



    /**
     * Returns the e-mail addresses in the list of direct "bcc:" recipients.
     *
     * @return the e-mail addresses in the list of direct "bcc:" recipients.
     */
    public NSArray bccAddresses()
    {
        return EmailAddress
                        .emailAddresses((InternetAddress[]) getRecipientsWithoutException(javax.mail.Message.RecipientType.BCC));

        /**
         * ensure [result_not_null] Result != null; [correct_result_count] Result.count() ==
         * getRecipientsWithoutException(javax.mail.Message.RecipientType.BCC).length;
         */
    }



    /**
     * Cover method for addRecipientsWithoutException(javax.mail.Message.RecipientType, Address[]) that translates
     * MessagingException into RuntimeException.
     */
    public void addRecipientsWithoutException(javax.mail.Message.RecipientType type, Address[] addresses)
    {
        try
        {
            addRecipients(type, addresses);
        }
        catch (MessagingException e)
        {
            throw new ExceptionConverter(e);
        }
    }



    /**
     * Cover method for Address[] getRecipientsWithoutException(javax.mail.Message.RecipientType) that translates
     * MessagingException into RuntimeException.
     */
    public Address[] getRecipientsWithoutException(javax.mail.Message.RecipientType type)
    {
        Address[] recipients;

        try
        {
            recipients = getRecipients(type);
            if (recipients == null)
            {
                recipients = new InternetAddress[] {};
            }
        }
        catch (MessagingException e)
        {
            throw new ExceptionConverter(e);
        }

        return recipients;
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns the object previously sent to setAssociatedObject(Object), or null if not set.
     * This is intended to be used to link this Message to  other objects in your application.
     * It will be particularly useful with the notifications sent by ThreadedMailAgent.
     *
     * @see #setAssociatedObject(Object)
     * @return the object previously sent to setAssociatedObject(Object), or null if not set
     */
    public Object associatedObject()
    {
        return associatedObject;
    }



    /**
     * Sets the optional object to associate with this message.  This is intended to be used to link this Message to
     * other objects in your application.  It will be particularly useful with the notifications sent by ThreadedMailAgent.
     *
     * @see #associatedObject()
     * @param anObject the optional object to associate with this message
     */
    public void setAssociatedObject(Object anObject)
    {
        associatedObject = anObject;
    }


}
