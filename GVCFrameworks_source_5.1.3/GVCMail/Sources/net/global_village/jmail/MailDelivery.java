package net.global_village.jmail;

import java.util.*;

import javax.mail.*;

import net.global_village.foundation.*;


/**
 * Simple wrapper for JavaMail.  This can be treated as a singleton (see createInstance() and getInstance()) and / or an instance can be created normally.  Example usage:<br><br>
 * <br>
 * <pre>
 * MailDelivery mySMTPServer = new MailDelivery("smtp.myserver.com");
 * // or
 * MailDelivery.createInstance("smtp.myserver.com");
 * MailDelivery mySMTPServer = MailDelivery.getInstance();
 *
 * Message aMessage = mySMTPServer.newMessage();
 *
 * aMessage.setFromAddress("myself@myserver.com");
 * aMessage.setToAddress("myself@myserver.com");
 * aMessage.setReplyToAddress("myself@myserver.com");
 * aMessage.setSubject("Test Message");
 * aMessage.setText("This is a Test Message.");
 *
 * mySMTPServer.sendImmediate(aMessage);
 * </pre>
 *
 * @deprecated use ThreadedMailAgent
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class MailDelivery extends Object
{
    static protected MailDelivery instance;			// Used for Singleton
    protected javax.mail.Session mailSession;		// Session used for all mail activities in this object



    /**
     * Creates an instance of MailDelivery, configured for sending mail over SMTP over the indicated host, to be used for getInstance().  createInstance() and getInstance() allow MailDelivery to be used as if it were a singleton.  This makes it easier to use as there is no need to manage instance.
     *
     * @param smtpHost - the name of the SMTP mail server to use
     */
    static public void createInstance(String smtpHost)
    {
        /** require [smtpHost_not_null] smtpHost != null; **/

        instance = new MailDelivery(smtpHost);

        /** ensure [instance_not_null] instance != null; **/
    }



    /**
     * Creates the instance of MailDelivery previously created by createInstance().
     *
     */
    static public MailDelivery getInstance()
    {
        JassAdditions.pre("MailDelivery", "getInstance", instance != null);

        return instance;

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Creates a MailDelivery configured for sending mail over SMTP over the indicated host.
     *
     * @param smtpHost - the name of the SMTP mail server to use
     */
    public MailDelivery(String smtpHost)
    {
        /** require [smtpHost_not_null] smtpHost != null; **/

        Properties sessionProperties = (Properties) System.getProperties().clone();
        sessionProperties.put("mail.smtp.host", smtpHost);
        sessionProperties.put("mail.transport.protocol", "SMTP");

        mailSession = javax.mail.Session.getInstance (sessionProperties, null);
    }




    /**
     * Returns a new instance of net.global_village.jmail.Message configured to use this MailDelivery for sending.
     *
     */
    public Message newMessage()
    {
        return new net.global_village.jmail.Message(mailSession());

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Sends the passed message immediately and returns once it is sent.  It would be better to do this in a seperate thread!
     */
    public void sendImmediate(javax.mail.Message aMessage) throws MessagingException
    {
        /** require [aMessage_not_null] aMessage != null; **/

        Transport.send(aMessage);
    }



    /**
     * Returns the javax.mail.Session being used.
     *
     * @return the javax.mail.Session being used.
     */
    protected javax.mail.Session mailSession()
    {
        return mailSession;
    }

    /** invariant mailSession() != null; **/
}
