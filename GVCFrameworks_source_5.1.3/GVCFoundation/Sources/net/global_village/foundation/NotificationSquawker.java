package net.global_village.foundation;

import java.util.Vector;

import com.webobjects.foundation.NSLog;
import com.webobjects.foundation.NSNotification;
import com.webobjects.foundation.NSNotificationCenter;
import com.webobjects.foundation.NSSelector;


/**
 * Logs notifications for debugging purposes.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 5$
 */
public class NotificationSquawker extends Object
{
    // So it doesn't get garbage collected
    private static Vector _squawkers = new Vector();


    /**
     * Preferred constuctor.
     */
    public NotificationSquawker(String notificationName)
    {
        super();
        /** require [valid_param] notificationName != null; **/

        NSNotificationCenter defaultCenter = NSNotificationCenter.defaultCenter();
        NSSelector notificationCallback = new NSSelector("notificationCallback", new Class[] {NSNotification.class});
        defaultCenter.addObserver(this, notificationCallback, notificationName, null);
        _squawkers.add(this);
    }



    public void notificationCallback(NSNotification notification)
    {
        if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
        {
            NSLog.debug.appendln("Squawk: recieived notification " + notification.name() + " with object " + notification.object() + " and with userInfo: " + notification.userInfo());
        }
    }



}
