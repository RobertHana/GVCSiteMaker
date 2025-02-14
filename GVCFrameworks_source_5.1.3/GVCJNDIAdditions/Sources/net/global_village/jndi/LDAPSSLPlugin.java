package net.global_village.jndi;

import java.util.*;

import javax.naming.*;
import javax.naming.directory.*;

import com.webobjects.foundation.*;
import com.webobjects.jndiadaptor.*;

import net.global_village.foundation.*;


/**
 * Extends LDAPPlugin to perform binding and all queries via SSL.
 * 
 * Non-working implementation, this does not work and never has.  It is simply here as a guide if we 
 * come back to this in the future.
 *
 * Useful links
 * http://forum.java.sun.com/thread.jsp?thread=348742&forum=2&message=1453985
 * http://javaalmanac.com/egs/javax.net.ssl/TrustAll.html
 * http://forum.java.sun.com/thread.jsp?forum=2&thread=453142&tstart=0&trange=15
 * http://www.caucho.com/support/resin-interest/0104/0427.html
 * 
 * May need to update certificate file to get connection, e.g.
 * keytool -import -trustcacerts -alias comodo -file C:\Temp\ComodoSecurityServicesCA.cer -keystore cacerts
 * keytool -import -trustcacerts -alias gte -file C:\Temp\GTECyberTrustRootCA.cer -keystore cacerts
 * 
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class LDAPSSLPlugin extends LDAPPlugIn
{
    public InitialDirContext createInitialDirContext(JNDIAdaptor adaptor)
    {
        /** require [valid_adaptor] adaptor != null;
                    [valid_connectionDictionary] adaptor.connectionDictionary() != null;  **/

        Hashtable environment = new Hashtable(3);
        
        // These values are read from the adaptor's connection dictionary
        NSDictionary connectionDictionary = adaptor.connectionDictionary(); 
        environment.put(Context.PROVIDER_URL, (String)(connectionDictionary.objectForKey("serverUrl")));
        environment.put(Context.INITIAL_CONTEXT_FACTORY, (String)connectionDictionary.objectForKey("initialContextFactory"));
        environment.put(Context.SECURITY_PRINCIPAL, (String)connectionDictionary.objectForKey("username"));
        environment.put(Context.SECURITY_CREDENTIALS, (String)connectionDictionary.objectForKey("password"));

        // Specify SSL connection
        environment.put(Context.SECURITY_PROTOCOL, "ssl");
        
        // Simple authentication is OK as this happens over a secure connection
        environment.put(Context.SECURITY_AUTHENTICATION, "simple");

        try 
        {
            return new InitialDirContext(environment);
        } 
        catch (NamingException exception) 
        {
            throw new ExceptionConverter(exception);
        }
        
        /** ensure [valid_result] Result != null;  **/
    }

}
