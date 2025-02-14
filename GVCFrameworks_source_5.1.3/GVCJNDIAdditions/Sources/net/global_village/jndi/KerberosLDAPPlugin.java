package net.global_village.jndi;

import java.util.*;

import javax.naming.*;
import javax.naming.directory.*;
import javax.security.auth.*;
import javax.security.auth.kerberos.*;

import com.webobjects.foundation.*;
import com.webobjects.jndiadaptor.*;

import net.global_village.foundation.*;

/**
 * Non-working implementation cribbed from
 * http://developer.apple.com/documentation/WebObjects/Reference/API/com/webobjects/jndiadaptor/package-summary.html
 *
 * This does not work and never has.  It is simply here as a guide if we come back to this in the
 * future.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class KerberosLDAPPlugin extends com.webobjects.jndiadaptor.LDAPPlugIn
{
    private Subject authenticatedSubject;



    public InitialDirContext createInitialDirContext(JNDIAdaptor adaptor)
    {
        NSLog.out.appendln("Running createInitialDirContext");

        // set up the InitialDirContext's environment as a Hashtable
        final Hashtable hashtable = new Hashtable(3);

        // two values are read from the adaptors connection dictionary
        NSDictionary connectionDictionary = adaptor.connectionDictionary();
        hashtable.put(Context.PROVIDER_URL, (connectionDictionary.objectForKey("serverUrl")));
        hashtable.put(Context.INITIAL_CONTEXT_FACTORY, (connectionDictionary.objectForKey("initialContextFactory")));

        // a third value is set to force the authentication mechanism for the InitialDirContext to be GSSAPI
        hashtable.put(Context.SECURITY_AUTHENTICATION, "GSSAPI");

        // create a ChannelOperation that creates the new InitialDirContext
        ChannelOperation channelOperation = new ChannelOperation()
        {
            public Object performOperation()
            {
                try
                {
                    return new InitialDirContext(hashtable);
                }
                catch (NamingException exception)
                {
                    throw new ExceptionConverter(exception);
                }
            }
        };

        // return the context as a result of a privileged action.

        /*
         * Fails here
         * **** Starting fetch ****
Running createInitialDirContext
In authenticatedSubject
Creating authenticatedSubject
Created authenticatedSubject
GSSException: No valid credentials provided (Mechanism level: Failed to find any Kerberos Ticket)
    at sun.security.jgss.krb5.Krb5InitCredential.getInstance(Krb5InitCredential.java:142)
    at sun.security.jgss.krb5.Krb5MechFactory.getCredentialElement(Krb5MechFactory.java:70)
    at sun.security.jgss.GSSManagerImpl.getCredentialElement(GSSManagerImpl.java:149)
    at sun.security.jgss.GSSCredentialImpl.add(GSSCredentialImpl.java:334)
    at sun.security.jgss.GSSCredentialImpl.<init>(GSSCredentialImpl.java:59)
    at sun.security.jgss.GSSCredentialImpl.<init>(GSSCredentialImpl.java:36)
    at sun.security.jgss.GSSManagerImpl.createCredential(GSSManagerImpl.java:96)
    at sun.security.jgss.GSSContextImpl.initSecContext(GSSContextImpl.java:178)
    at sun.security.jgss.GSSContextImpl.initSecContext(GSSContextImpl.java:158)
    at com.sun.security.sasl.gsskerb.GssKerberosV5.evaluateChallenge(GssKerberosV5.java:160)
    at com.sun.jndi.ldap.sasl.LdapSasl.saslBind(LdapSasl.java:113)
    at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
    at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
    at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
    at java.lang.reflect.Method.invoke(Method.java:324)
    at com.sun.jndi.ldap.LdapClient.saslBind(LdapClient.java:399)
    at com.sun.jndi.ldap.LdapClient.authenticate(LdapClient.java:215)
    at com.sun.jndi.ldap.LdapCtx.connect(LdapCtx.java:2597)
    at com.sun.jndi.ldap.LdapCtx.<init>(LdapCtx.java:275)
    at com.sun.jndi.ldap.LdapCtxFactory.getUsingURL(LdapCtxFactory.java:173)
    at com.sun.jndi.ldap.LdapCtxFactory.getUsingURLs(LdapCtxFactory.java:191)
    at com.sun.jndi.ldap.LdapCtxFactory.getLdapCtxInstance(LdapCtxFactory.java:136)
    at com.sun.jndi.ldap.LdapCtxFactory.getInitialContext(LdapCtxFactory.java:66)
    at javax.naming.spi.NamingManager.getInitialContext(NamingManager.java:662)
    at javax.naming.InitialContext.getDefaultInitCtx(InitialContext.java:243)
    at javax.naming.InitialContext.init(InitialContext.java:219)
    at javax.naming.InitialContext.<init>(InitialContext.java:195)
    at javax.naming.directory.InitialDirContext.<init>(InitialDirContext.java:80)
    at net.global_village.kerberos.KerberosLDAPPlugin$1.performOperation(KerberosLDAPPlugin.java:53)
    at net.global_village.kerberos.KerberosPrivilegedAction.run(KerberosPrivilegedAction.java:23)
    at java.security.AccessController.doPrivileged(Native Method)
    at javax.security.auth.Subject.doAs(Subject.java:319)
    at net.global_village.kerberos.KerberosLDAPPlugin.createInitialDirContext(KerberosLDAPPlugin.java:63)
         */
        return (InitialDirContext)(Subject.doAs(authenticatedSubject(), new KerberosPrivilegedAction(channelOperation)));
    }



    public Object performChannelOperation(ChannelOperation operation)
    {
        KerberosPrivilegedAction action = new KerberosPrivilegedAction(operation);
        return Subject.doAs(authenticatedSubject(), action);
    }




    public Subject authenticatedSubject()
    {
        return null;
    }

    public Subject foo()
    {
        NSLog.out.appendln("In authenticatedSubject");

        if (authenticatedSubject == null)
        {
            System.setProperty("java.security.krb5.realm", "UMICH.EDU");
            System.setProperty("java.security.krb5.kdc", "kerberos.umich.edu");

            NSLog.out.appendln("Creating authenticatedSubject");
            Object credential = "dummy";
            /*
             * This does not work.  See exception above.  Appears to be caused by using different
             * key stores.  Probably need to use JDK mechanism to authenticate.
            try
            {
                KerberosAuthenticator.createInstance("kerberos.umich.edu", "UMICH.EDU");
            }
            catch (UnknownHostException e)
            {
                e.printStackTrace();
            }
            credential = KerberosAuthenticator.getInstance().credential("principal", "password");
             */

            KerberosPrincipal principal = new KerberosPrincipal("principal");
            authenticatedSubject = new Subject();
            authenticatedSubject.getPrincipals().add(principal);
            authenticatedSubject.getPublicCredentials().add(credential);
            NSLog.out.appendln("Created authenticatedSubject");
        }


        return authenticatedSubject;
    }

}
