package net.global_village.jmail;

import java.util.Enumeration;
import javax.mail.internet.*;

import com.webobjects.foundation.*;

import net.global_village.foundation.JassAdditions;


/**
 * A collection of static methods useful for dealing with e-amail addresses and InternetAddress.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class EmailAddress
{

    /**
    * Returns the passed NSArray of e-mail addresses as a Java array of InternetAddress.
     *
     * @param emailAddresses - the NSArray of addresses to convert
     * @return the passed NSArray of e-mail addresses converted into a Java array of InternetAddress.
     * @exception AddressException if one of the e-mail addresses in emailAddresses can not be parsed.
     */
    static public InternetAddress[] internetAddresses(NSArray emailAddresses) throws AddressException
    {
        /** require [emailAddresses_not_null] emailAddresses != null; **/
        
        InternetAddress[] internetAddresses = new InternetAddress [emailAddresses.count ()];

        Enumeration emailAddressesEnumeration = emailAddresses.objectEnumerator ();
        int addressIndex = 0;
        while(emailAddressesEnumeration.hasMoreElements ())
        {
            internetAddresses[addressIndex++] = new InternetAddress ((String)emailAddressesEnumeration.nextElement ());
        }

        JassAdditions.post("Message", "internetAddresses", internetAddresses.length == emailAddresses.count());
        return internetAddresses;
        /** ensure [result_not_null] Result != null; 
                   [has_all_addresses] Result.length == emailAddresses.count();  **/
    }



    /**
    * Returns an NSArray of e-mail addresses from the passed Java array of InternetAddress.
     *
     * @param internetAddresses the Java array of InternetAddress to convert
     * @return the passed Java array of InternetAddress converted into an NSArray of e-mail addresses.
     */
    static public NSArray emailAddresses(InternetAddress[] internetAddresses)
    {
        /** require [internetAddresses_not_null] internetAddresses != null; **/
        
        NSMutableArray emailAddresses = new NSMutableArray(internetAddresses.length);

        int addressIndex = 0;
        while(addressIndex < internetAddresses.length)
        {
            emailAddresses.addObject(internetAddresses[addressIndex++].getAddress());
        }

        JassAdditions.post("Message", "emailAddresses", internetAddresses.length == emailAddresses.count());
        return emailAddresses;

        /** ensure [result_not_null] Result != null; **/
    }

    

    /**
     * Returns <code>true</code> if <code>anEmail</code> is of valid email format: 
     * "&lt;string&gt;@&lt;string&gt;.&lt;string&gt;" where &lt;string&gt; is not empty, 
     * <code>false</code> otherwise.
     *
     * @param anAddress the string to check
     * @return <code>true</code> if <code>anEmail</code> is of valid email format, 
     * <code>false</code> otherwise
     */
    public static boolean isValidAddressFormat(String anAddress)
    {
        /** require [valid_param] anAddress != null; **/

        NSArray listItems = NSArray.componentsSeparatedByString(anAddress, "@");
        boolean hasAtSignInMiddle = (listItems.count() == 2) &&
            (((String)listItems.objectAtIndex(0)).length() > 0) &&
            (((String)listItems.objectAtIndex(1)).length() > 0);

        boolean hasValidDomain = false;
        if (hasAtSignInMiddle)
        {
            NSArray domainItems = NSArray.componentsSeparatedByString((String)listItems.objectAtIndex(1), ".");
            hasValidDomain = (domainItems.count() >= 2) &&
                (((String)domainItems.objectAtIndex(0)).length() > 0) &&
                (((String)domainItems.objectAtIndex(1)).length() > 0);
        }

        // Last check: will it be accepted by InternetAddress?
        boolean isValid = hasAtSignInMiddle && hasValidDomain;
        if (isValid)
        {
            try
            { 
                new InternetAddress(anAddress);
            }
            catch (AddressException e)
            {
                isValid = false;
            }
        }
        
        return isValid;
    }



    /**
     * Do not create objects of this class.  It only serves to collect static methods. 
     */
    private EmailAddress()
    {
        super();
        // TODO Auto-generated constructor stub
    }

}
