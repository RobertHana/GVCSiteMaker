package net.global_village.jndi;

import java.security.*;

import com.webobjects.jndiadaptor.JNDIPlugIn.*;

/**
 * KerberosPrivilegedAction for JNDIPlugIn's ChannelOperation.  Taken from description at:
 * http://developer.apple.com/documentation/WebObjects/Reference/API/com/webobjects/jndiadaptor/package-summary.html
 *
 * @author Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
class KerberosPrivilegedAction extends Object implements PrivilegedAction 
 {
     private ChannelOperation _operation = null;

     KerberosPrivilegedAction(ChannelOperation operation) 
     {
         super();
         _operation = operation;
     }
        
     public Object run() 
     {
         return _operation.performOperation();
     }
 }
