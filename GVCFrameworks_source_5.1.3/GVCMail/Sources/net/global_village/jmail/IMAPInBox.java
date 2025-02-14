package net.global_village.jmail;


import java.util.Properties;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;


/**
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class IMAPInBox extends Object
{

    protected javax.mail.Session mailSession;		// Session used for all mail activities in this object
    protected Folder inbox;

    /**
     * Creates a IMAPInBox on the indicated host with the provided account information.
     *
     * @param IMAPHost - the name of the  mail server to use
     * @param account - the name of the mailbox
     * @param password - the password for the mailbox
     */
    public IMAPInBox(String IMAPHost,
                     String account,
                     String password) throws MessagingException
    {
        /** require 
            [IMAPHost_not_null] IMAPHost != null;
            [account_not_null] account != null;
            [password_not_null] password != null;
            **/
        
        // I'm not clear on what the advantage / disadvantage is of using the default session as here or in a custom one as in MailDelivery.  I chose this method here as it is not possible to specify the IMAP password in the properties passed to the Session constructor.  Rather that setup part of the information here and part of it later I chose to do it all later.  -ch
        Properties props = System.getProperties();
        mailSession = Session.getDefaultInstance(props, null);
        Store store = mailSession.getStore("imap");
        store.connect(IMAPHost, account, password);

        // Open the in-box Folder
        inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_WRITE);
    }


}
