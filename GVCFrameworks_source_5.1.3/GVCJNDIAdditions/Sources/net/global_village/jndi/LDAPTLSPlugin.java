package net.global_village.jndi;

import java.io.IOException;
import java.util.Hashtable;

import javax.naming.*;
import javax.naming.directory.InitialDirContext;
import javax.naming.ldap.*;

import com.webobjects.eoaccess.EOAdaptor;
import com.webobjects.foundation.*;
import com.webobjects.jndiadaptor.*;

import net.global_village.foundation.ExceptionConverter;

/**
 * Extends LDAPPlugin to perform binding and all queries secures by TLS.  For debugging, launch with
 * -Djavax.net.debug=all
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class LDAPTLSPlugin extends LDAPPlugIn
{
    protected EOAdaptor ldapAdaptor;
    
    
    public InitialDirContext createInitialDirContext(JNDIAdaptor adaptor)
    {
        /** require [valid_adaptor] adaptor != null;
                    [valid_connectionDictionary] adaptor.connectionDictionary() != null;  **/


        // We will need this for later exception handling.
        ldapAdaptor = adaptor;
        
        Hashtable environment = new Hashtable(3);
        
        // These values are read from the adaptors connection dictionary
        NSDictionary connectionDictionary = adaptor.connectionDictionary(); 
        environment.put(Context.PROVIDER_URL, (String)(connectionDictionary.objectForKey("serverUrl")));
        environment.put(Context.INITIAL_CONTEXT_FACTORY, (String)connectionDictionary.objectForKey("initialContextFactory"));

        try 
        {
            if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
            {
                NSLog.debug.appendln("LDAPTLSPlugin: creating initial context with: " + environment);
            }
            
            // Create a basic context
            InitialLdapContext ldapContext = new InitialLdapContext(environment, null);
           
            if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
            {
                NSLog.debug.appendln("LDAPTLSPlugin: Negotiating TLS");
            }

           // Switch to secure mode
            StartTlsResponse tls = (StartTlsResponse) ldapContext.extendedOperation(new StartTlsRequest());
            tls.negotiate();

            if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
            {
                NSLog.debug.appendln("LDAPTLSPlugin: TLS Negotiated, setting bind info");
            }

            // Simple authentication is OK as this now happens over a secure connection
            environment.put(Context.SECURITY_AUTHENTICATION, "simple");
            environment.put(Context.SECURITY_PRINCIPAL, (String)connectionDictionary.objectForKey("username"));
            environment.put(Context.SECURITY_CREDENTIALS, (String)connectionDictionary.objectForKey("password"));

            return ldapContext;
        } 
        catch (NamingException exception) 
        {
            if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
            {
                NSLog.debug.appendln("LDAPTLSPlugin Threw: ");
                NSLog.debug.appendln(exception);
            }
            throw new ExceptionConverter(exception);
        }
        catch (IOException e)
        {
            throw new ExceptionConverter(e);
        }
        
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Overridden to catch exceptions caused by mis-configuration.
     * 
     * @see com.webobjects.jndiadaptor.JNDIPlugIn#performChannelOperation(com.webobjects.jndiadaptor.JNDIPlugIn.ChannelOperation)
     */
    public Object performChannelOperation(ChannelOperation channelOp)
    {
        try 
        {
            return super.performChannelOperation(channelOp);
        }
        catch (NullPointerException npe)
        {
            /*
             * If this exception happens it usually means that nothing was fetched.  This can be 
             * caused by a bad LDAP url or an incorrect RDN (contains spaces for example).  The trace
             * is:
             *  at com.sun.jndi.ldap.LdapNamingEnumeration.getNextBatch(LdapNamingEnumeration.java:110)
             *  at com.sun.jndi.ldap.LdapNamingEnumeration.nextAux(LdapNamingEnumeration.java:244)
             *  at com.sun.jndi.ldap.LdapNamingEnumeration.nextImpl(LdapNamingEnumeration.java:235)
             *  at com.sun.jndi.ldap.LdapNamingEnumeration.next(LdapNamingEnumeration.java:183)
             *  at com.webobjects.jndiadaptor._JNDIWorker.doNext(_JNDIWorker.java:201)
             *  at com.webobjects.jndiadaptor._JNDIWorker$2.performOperation(_JNDIWorker.java:189)
             *  at com.webobjects.jndiadaptor.JNDIPlugIn.performChannelOperation(JNDIPlugIn.java:283)
             */
            NSLog.err.appendln("performChannelOperation threw:");
            NSLog.err.appendln(npe);
            throw new RuntimeException("Unable to perform operation.  Check LDAP configuration.");
        }
        catch (JNDIAdaptorException e)
        {
            // Propogate this exception so that reconnection is handled by EOF
            if (ldapAdaptor.isDroppedConnectionException(e))
            {
                throw e;
            }

            /*
             * If this exception happens it usually is the result of a misconfigured URL.
             * 
             * _JNDIWorker.java     180     doApply     com.webobjects.jndiadaptor
             *  _JNDIWorker.java    151     performOperation    com.webobjects.jndiadaptor
             *  JNDIPlugIn.java     283     performChannelOperation     com.webobjects.jndiadaptor
             *  LDAPTLSPlugin.java  97  performChannelOperation     net.global_village.jndi
             */
            NSLog.err.appendln("performChannelOperation threw:");
            NSLog.err.appendln(e);
            throw new RuntimeException("Unable to perform operation.  Check LDAP configuration.");
        }
    }

}
