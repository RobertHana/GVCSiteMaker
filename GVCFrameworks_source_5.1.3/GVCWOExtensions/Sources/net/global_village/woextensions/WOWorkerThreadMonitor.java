package net.global_village.woextensions;

import java.util.*;

import com.webobjects.foundation.*;

import net.global_village.foundation.*;
import net.global_village.foundation.Date;


/**
 * This class is a poor substitute for better ways of fixing deadlocking problems.  It is for use only
 * in desperate times, like when you need a few hours of sleep while fixing the real problem or while
 * you are waiting for Apple to release a patch.  OK, consider yourself warned!<br/>
 * <br/>
 * What this class does is to watch the requests as they are dispatched and keep track of the ones
 * that have not yet returned a response.  Periodically it examines these as yet unfinished requests
 * and calculates how long each has been running.  If one has been running more than a set amount of time
 * then it is assumed that the thread has deadlocked.  As there is no recovery for this, the application
 * terminates immediately.  In a nut shell, this is an automated way of doing kill -9 on the instance.<br/>
 * <br/>
 * The WOWorkerThreadMonitor is configured via values in the system properties:<br/>
 * <b>WOWorkerThreadMonitor.MonitorForDeadlock</b><br/>
 * Default: true<br/>
 * Set to true to enable deadlock checking, anything else to disable.<br/>
 * <br/>
 * <b>WOWorkerThreadMonitor.SecondsForDeadlock</b><br/>
 * Default: 180<br/>
 * The number of seconds that a request can be working before it is assumed that it has deadlocked.<br/>
 * <br/>
 * <b>WOWorkerThreadMonitor.SecondsBetweenChecks</b><br/>
 * Default: 60<br/>
 * The number of seconds between checks for deadlocks.  This should be kept lower than
 * WOWorkerThreadMonitor.SecondsForDeadlock.<br/>
 * <br/>
 * <b>WOWorkerThreadMonitor.DeadlockWatchWindowStart</b><br/>
 * Default: 00:00<br/>
 * The time of day, in 24 hour format, when deadlock detection should be started.  This, along with
 * WOWorkerThreadMonitor.DeadlockWatchWindowEnd can be used to restrict deadlock detection to certain
 * hours of the day, like when you are sleeping for instance.  The rest of the time you can let them
 * happen normally and examine the thread dumps etc. when the application is in a deadlocked state.
 * The time format must be HH:MM, no spaces allowed and always five characters.<br/>
 * <br/>
 * <b>WOWorkerThreadMonitor.DeadlockWatchWindowEnd</b><br/>
 * Default: 23:59<br/>
 * See WOWorkerThreadMonitor.DeadlockWatchWindowStart for a description<br/>
 * <br/>
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class WOWorkerThreadMonitor extends java.util.TimerTask {

    public static final String MONITOR_FOR_DEADLOCK = "WOWorkerThreadMonitor.MonitorForDeadlock";
    public static final String DEFAULT_MONITOR_FOR_DEADLOCK = "true";

    public static final String SECONDS_FOR_DEADLOCK = "WOWorkerThreadMonitor.SecondsForDeadlock";
    public static final String DEAFULT_SECONDS_FOR_DEADLOCK = "180";

    public static final String SECONDS_BETWEEN_CHECKS = "WOWorkerThreadMonitor.SecondsBetweenChecks";
    public static final String DEAFULT_SECONDS_BETWEEN_CHECKS = "60";

    public static final String DEADLOCK_WATCH_WINDOW_START = "WOWorkerThreadMonitor.DeadlockWatchWindowStart";
    public static final String DEFAULT_DEADLOCK_WATCH_WINDOW_START = "00:00";

    public static final String DEADLOCK_WATCH_WINDOW_END = "WOWorkerThreadMonitor.DeadlockWatchWindowEnd";
    public static final String DEFAULT_DEADLOCK_WATCH_WINDOW_END = "23:59";

    protected static final boolean RUN_AS_DAEMON = true;
    protected static final long STARTUP_DELAY = 120 * 1000;

    protected int watchWindowStartHours = -1;
    protected int watchWindowStartMinutes = -1;
    protected int watchWindowEndHours = -1;
    protected int watchWindowEndMinutes = -1;
    protected long maximumMilliSecondsToWait = -1;
    protected NSMutableDictionary runningThreads = new NSMutableDictionary();
    protected Timer deadlockCheckTimer;


    /**
     * Designated constructor
     */
    public WOWorkerThreadMonitor() {
        super();

        // Check if we shoul be running or not
        java.lang.Boolean shouldMonitor = new java.lang.Boolean(System.getProperty(MONITOR_FOR_DEADLOCK, DEFAULT_MONITOR_FOR_DEADLOCK));
        if ( ! shouldMonitor.booleanValue())
        {
            NSLog.out.appendln("Disabling WOWorkerThreadMonitor...");
            return;
        }

        // Register for notification at start and end of Request-Response loop
        NSNotificationCenter notificationCenter = NSNotificationCenter.defaultCenter();
        notificationCenter.addObserver(this,
                new NSSelector("recordDispatchStart", new Class[] {NSNotification.class}),
                WOApplication.ApplicationWillDispatchRequestNotification,
                null);
        notificationCenter.addObserver(this,
                new NSSelector("recordDispatchCompletion", new Class[] {NSNotification.class}),
                WOApplication.ApplicationDidDispatchRequestNotification,
                null);

        String watchWindowStart = validated24HourTimeProperty(DEADLOCK_WATCH_WINDOW_START, DEFAULT_DEADLOCK_WATCH_WINDOW_START);
        NSArray hoursAndMinutes = NSArray.componentsSeparatedByString(watchWindowStart, ":");
        watchWindowStartHours = Integer.parseInt((String)hoursAndMinutes.objectAtIndex(0));
        watchWindowStartMinutes = Integer.parseInt((String)hoursAndMinutes.lastObject());

        String watchWindowEnd = validated24HourTimeProperty(DEADLOCK_WATCH_WINDOW_END, DEFAULT_DEADLOCK_WATCH_WINDOW_END);
        hoursAndMinutes = NSArray.componentsSeparatedByString(watchWindowEnd, ":");
        watchWindowEndHours = Integer.parseInt((String)hoursAndMinutes.objectAtIndex(0));
        watchWindowEndMinutes = Integer.parseInt((String)hoursAndMinutes.lastObject());

        String maxSecondsToWait = validatedIntegerProperty(SECONDS_FOR_DEADLOCK, DEAFULT_SECONDS_FOR_DEADLOCK);
        maximumMilliSecondsToWait = Integer.parseInt(maxSecondsToWait) * 1000;

        String secondsBetweenChecks = validatedIntegerProperty(SECONDS_BETWEEN_CHECKS, DEAFULT_SECONDS_BETWEEN_CHECKS);
        long millisecondsBetweenChecks = Integer.parseInt(secondsBetweenChecks) * 1000;

        deadlockCheckTimer = new Timer(RUN_AS_DAEMON);
        deadlockCheckTimer.scheduleAtFixedRate(this, STARTUP_DELAY, millisecondsBetweenChecks);

        NSLog.out.appendln("Starting up WOWorkerThreadMonitor...");
        NSLog.out.appendln("  checking for deadlock between the hours of " +
                watchWindowStartHours + ":" + watchWindowStartMinutes + " and " +
                watchWindowEndHours + ":" + watchWindowEndMinutes);
        NSLog.out.appendln("  checking for deadlock every " + (millisecondsBetweenChecks/1000) + " seconds");
        NSLog.out.appendln("  deadlock will be assumed after " + (maximumMilliSecondsToWait/1000) + " seconds");
    }



    /**
     * This method is called when a new request is dispatched.  The name of the WOWorkerThread is
     * recorded along with the starting time.
     *
     * @param notification NSNotification with WORequest as notification object
     */
    public void recordDispatchStart(NSNotification notification)
    {
        runningThreads().setObjectForKey(new NSTimestamp(), Thread.currentThread().getName());
    }



    /**
     * This method is called when a request has been dispatched.  The record for this WOWorkerThread is
     * discarded.
     *
     * @param notification NSNotification with WORequest as notification object
     */
    public void recordDispatchCompletion(NSNotification notification)
    {
        runningThreads().removeObjectForKey(Thread.currentThread().getName());
    }



    /**
     * Returns the dictionary, keyed on thread name, of the WOWorkerThreads actively dispatching
     * requests.  This is synchronized as both the thread running the WOWorkerThread monitor and
     * WOWorkerThreads can call this method.
     *
     * @return the dictionary, keyed on thread name, of the WOWorkerThreads actively dispatching
     * requests
     */
    public synchronized NSMutableDictionary runningThreads()
    {
        return runningThreads;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * This method is the core of the deadlock check.  It enumerates all the active threads in
     * runningThreads() and if one has gone on too long the application is terminated.
     */
    public void run() {

        // If we are not in the checking window then do nothing.
        if ( ! shouldCheckForDeadlock())
        {
            return;
        }

        // Synchronize on runningThreads so that the collection can't get modified while we are enumerating it.
        synchronized (runningThreads)
        {
            Enumeration enumeration = runningThreads().keyEnumerator();
            long now = new NSTimestamp().getTime();
            while (enumeration.hasMoreElements()) {
                String threadName = (String) enumeration.nextElement();
                if (hasExceededCutoff(now, (NSTimestamp)runningThreads().objectForKey(threadName)))
                {
                    NSLog.out.appendln("Killing Application, deadlock assumed for " + threadName);
                    System.exit(1);
                }
            }
        }
    }



    /**
     * Returns <code>true</code> if the current time is in the window of when deadlocks should be detected.
     *
     * @return <code>true</code> if the current time is in the window of when deadlocks should be detected
     */
    protected boolean shouldCheckForDeadlock()
    {
        GregorianCalendar now = new GregorianCalendar();
        int hourOfDay = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);

        boolean isAfterStart = (hourOfDay > watchWindowStartHours) ||
                               ((hourOfDay == watchWindowStartHours) && (minute >= watchWindowStartMinutes));
        boolean isBeforeEnd = (hourOfDay < watchWindowEndHours) ||
                              ((hourOfDay == watchWindowEndHours) && (minute <= watchWindowEndMinutes));
        // The or might seem a bit odd but it is what works with the sliding window:
        return isAfterStart || isBeforeEnd;
    }



    /**
     * Returns <code>true</code> if the difference between now and the start time is greater than
     * the number of seconds before deadock is assumed.
     *
     * @param now the current time or a close approximation of it
     * @param startTime the time that a request was dispatched
     *
     * @return <code>true</code> if startTime - now > the number of seconds before deadock is assumed
     */
    protected boolean hasExceededCutoff(long now, NSTimestamp startTime)
    {
        long millisecondRunning = now - startTime.getTime();
        return (millisecondRunning > maximumMilliSecondsToWait);
    }




   /**
    * Utility method for dealing with properties in 24 hour time format.  If the named property is
    * not present or malformed the default is returned.
    *
    * @param propertyName the name of the system property to look for
    * @param defaultTimeString the default to use if the named property is not present or malformed
    *
    * @return either the value for the property or the default
    */
    public String validated24HourTimeProperty(String propertyName, String defaultTimeString)
    {
        /** require [valid_property_name] propertyName != null;
                    [valid_default] (defaultTimeString != null) &&
                                    Date.isValid24HourTime(defaultTimeString);  **/
        String propertyValue = System.getProperty(propertyName, defaultTimeString);
        return Date.isValid24HourTime(propertyValue) ? propertyValue : defaultTimeString;
        /** ensure Date.isValid24HourTime(Result);  **/
    }



    /**
     * Utility method for dealing with integer properties.  If the named property is  not present or
     * malformed the default is returned.
     *
     * @param propertyName the name of the system property to look for
     * @param defaultValue the default to use if the named property is not present or malformed
     *
     * @return either the value for the property or the default
     */
    public String validatedIntegerProperty(String propertyName, String defaultValue)
    {
        /** require [valid_property_name] propertyName != null;
                    [valid_default] (defaultValue != null) &&
                                    StringAdditions.isInteger(defaultValue);  **/
        String propertyValue = System.getProperty(propertyName, defaultValue);
        return (StringAdditions.isInteger(propertyValue) ? propertyValue : defaultValue).trim();
        /** ensure StringAdditions.isInteger(Result);  **/
    }


}


