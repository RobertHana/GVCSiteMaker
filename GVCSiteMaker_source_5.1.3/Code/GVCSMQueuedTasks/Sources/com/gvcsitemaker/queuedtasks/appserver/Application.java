// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.queuedtasks.appserver;

import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.support.*;
import com.gvcsitemaker.custom.appserver.*;
import com.webobjects.appserver.*;
import com.webobjects.foundation.*;


/**
 * Contains initialization for TaskQueue
 */
public class Application extends SMCustomApplication 
{
    protected WOTimer newTimer;


    public static void main(String argv[]) 
    {
        WOApplication.main(argv, Application.class);
    }



    /**
     * Initializes application, creates TaskQueue, starts timer to drive task queue.
     */
    public Application() 
    {
        super();
    }



    /**
     * Loads the application configuration information and page map, configures
     * the debugging and WebObjects environment, and performs diagnostic checks.
     * This can be called again to reload the information.
     */
    public void initializeApplicationConfiguration()
    {
        super.initializeApplicationConfiguration();

        NSLog.out.appendln("Welcome to " + this.name() + " !");

        //Run the timer to checked for tasks to process
        TaskQueue newTaskQueue = new TaskQueue();

        //Before running the timer, make sure that there are no inProgress Tasks that were left inProgress in case the app crashed before this startup
        newTaskQueue.revertInProgressTasks();

        int timerInterval = ((SMApplication)application()).properties().intPropertyForKey("TaskQueueTimerInterval");
        NSLog.out.appendln("Checking for tasks every " + timerInterval + " seconds.");
        // TODO Make into a Java Timer to avoid run loop issues with WOTimer
        newTimer = WOTimer.scheduledTimer(timerInterval*1000, newTaskQueue, "checkQueue", null, null, true);
    }



    /**
     * Returns the prefix for PageMap.list for this application.  We cannot use name() as this is whatever is setup in Monitor and we don't want to go around renaming config files when the app name in Monitor changes.
     *
     * @return the prefix for PageMap.list for this application.
     */
    public String pageMapPrefix()
    {
        return "GVCSiteMaker";

        /** ensure [result_not_null] Result != null; **/
    }    



}
