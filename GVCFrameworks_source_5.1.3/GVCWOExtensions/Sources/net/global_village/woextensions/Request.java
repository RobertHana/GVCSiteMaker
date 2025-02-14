package net.global_village.woextensions;

import com.webobjects.appserver.*;
import com.webobjects.appserver._private.*;
import com.webobjects.foundation.*;


/**
 * WORequest subclass to fix a bug where WO appends a ":443" or ":80" to the
 * end of a URL.
 *
 * @author Copyright (c) 2010  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 1$
 */
public class Request extends WORequest
{


    public Request(String string, String string0, String string1, NSDictionary nsdictionary, NSData nsdata, NSDictionary nsdictionary2)
    {
        super(string, string0, string1, nsdictionary, nsdata, nsdictionary2);
    }



    /**
     * Mostly copied from ERXRequest.  Fixes the bug where WO appends a ":443" or ":80" to the
     * end of a URL.
     */
    public void _completeURLPrefix(StringBuffer stringbuffer, boolean secure, int port)
    {
        String serverName = _serverName();

        String portStr;
        if (port == 0)
        {
            portStr = secure ? "443" : _serverPort();
        }
        else
        {
            portStr = WOShared.unsignedIntString(port);
        }

        if (secure)
        {
            stringbuffer.append("https://");
        }
        else
        {
            stringbuffer.append("http://");
        }

        stringbuffer.append(serverName);
        if (portStr != null && ((secure && !"443".equals(portStr)) || (!secure && !"80".equals(portStr))))
        {
            stringbuffer.append(':');
            stringbuffer = stringbuffer.append(portStr);
        }
    }



}
