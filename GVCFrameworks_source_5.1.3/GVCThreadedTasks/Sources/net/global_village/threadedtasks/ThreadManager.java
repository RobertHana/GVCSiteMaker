package net.global_village.threadedtasks;

import org.apache.log4j.*;

import com.webobjects.appserver.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;



/**
 * Manages a set of TaskThreads for a WOApplication.  Can instruct the threads to terminate.  Tells the
 * application to terminate after all of the threads have terminated if the application is marked as terminating.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class ThreadManager
{


    private volatile NSMutableArray taskThreads = new NSMutableArray();
    private volatile NSMutableArray deadThreads = new NSMutableArray();
    private volatile NSMutableArray retainedThreads = new NSMutableArray();
    protected Logger logger = LoggerFactory.makeLogger();


    /**
     * Constructor.
     */
    public ThreadManager()
    {
        super();

        // Link TaskThread.ThreadEndedNotification to our taskThreadEnded(NSNotification) method
        NSNotificationCenter.defaultCenter().addObserver(this, new NSSelector("taskThreadEnded", new Class[]{NSNotification.class}),
                                                         TaskThread.ThreadEndedNotification, null);
    }



    /**
     * Use this method to create and start a new TaskThread managed by this object.
     *
     * @param task object implementing ITask that determines the tasks to be managed by this thread
     * @param taskFrequency frequency in seconds
     * @param historySize number of past task checks and processes to remember
     * @param ec the EOEditingContext to use to process tasks in
     * @param retainThread <code>true</code> if this thread should be retained after it ends (e.g. for reporting or restarting)
     */
    public void startThread(ITask task, int taskFrequency, int historySize, EOEditingContext ec, boolean retainThread)
    {
        /** require [valid_class] task != null;
                    [valid_frequency] taskFrequency > 0;
                    [valid_historySize] historySize > 0;
                    [valid_ec] ec != null;
         **/
        TaskThread thread = new TaskThread(task, taskFrequency, historySize, ec);
        if (retainThread)
        {
        	retainThread(thread);
        }
        logger.info("Starting thread " + thread);
        taskThreads().addObject(thread);
        thread.start();
    }



	/**
     * Use this method to restart a TaskThread that was stopped or has crashed.
     *
     * @param thread the TaskThread to restart
     */
    public void restartThread(TaskThread thread)
    {
        /** require [valid_thread] thread != null;
                    [thread_stopped] stoppedThreads().containsObject(thread);         **/
        logger.info("Re-starting thread " + thread);
        startThread(thread.task(), (int)thread.frequency(), thread.runHistorySize(), thread.editingContext(), shouldRetainThread(thread));
        // A new thread has been started, discard the old one
        retainedThreads().removeObject(thread);
        deadThreads().removeObject(thread);
        /** ensure [thread_not_in_dead] ! deadThreads().containsObject(thread);         **/
    }



    /**
     * Returns copy of list of running TaskThreads.
     *
     * @see #taskThreads()
     * @return list of running TaskThreads
     */
    public synchronized NSArray threads()
    {
        return taskThreads().immutableClone();
        /** ensure [valid_result] Result != null;   **/
    }



    /**
     * Returns copy of list of TaskThreads that have stopped.
     *
     * @see #deadThreads()
     * @return list of TaskThreads that have stopped
     */
    public synchronized NSArray stoppedThreads()
    {
        return deadThreads().immutableClone();
        /** ensure [valid_result] Result != null;   **/
    }



    /**
     * Returns <code>true</code> if this object has running (any) TaskThreads.
     *
     * @return <code>true</code> if this object has running (any) TaskThreads
     */
    public synchronized boolean hasRunningThreads()
    {
        return taskThreads().count() > 0;
    }



    /**
     * Call this to ask all the thread to stop running as soon as they can safely do so.
     */
    public synchronized void shutdownThreads()
    {
        logger.info("Stopping all threads");
        for (int i = 0; i < taskThreads().count(); i++)
        {
            logger.info("Stopping thread " + taskThreads().objectAtIndex(i));
            ((TaskThread)taskThreads().objectAtIndex(i)).end();
        }
    }



    /**
     * This method is called when a TaskThread sends the TaskThread.ThreadEndedNotification. The thread is removed from
     * taskThreads() and added to deadThreads(). If there are no more running threads and the application isTerminating(),
     * terminate() is called <b>even</b> if there are active sessions.
     *
     * @see TaskThread
     * @see #taskThreads()
     * @see #deadThreads()
     * @see DelayedTermination
     *
     * @param notification NSNotification of TaskThread.ThreadEndedNotification
     */
    public synchronized void taskThreadEnded(NSNotification notification)
    {
        /** require [valid_notification] notification != null;
                    [notification_has_object] notification.object() != null;
                    [notification_object_is_taskthread] notification.object() instanceof TaskThread;
         **/
    	TaskThread taskThread = (TaskThread) notification.object();
        if (taskThreads().containsObject(taskThread))
        {
            logger.info("Thread stopped notification received for " + taskThread);

            taskThreads().removeObject(taskThread);
            if (shouldRetainThread(taskThread))
            {
                logger.info("Retaining thread for restart");
                deadThreads().addObject(taskThread);
            }

            if (taskThreads().count() == 0)
            {
                if (WOApplication.application().isTerminating())
                {
                    logger.info("Starting DelayedTermination thread");
                    new DelayedTermination().start();
                }
            }
        }
        /** ensure [thread_removed] ! taskThreads().containsObject(notification.object());  **/
    }



    /**
     * Returns list of managed TaskThreads.
     *
     * @return list of managed TaskThreads
     */
    protected synchronized NSMutableArray taskThreads()
    {
        return taskThreads;
        /** ensure [valid_result] Result != null;   **/
    }



    /**
     * Returns list of managed TaskThreads that have stopped, but are being retained.
     *
     * @return list of managed TaskThreads that have stopped, but are being retained
     */
    protected synchronized NSMutableArray deadThreads()
    {
        return deadThreads;
        /** ensure [valid_result] Result != null;   **/
    }



    /**
     * @return list of managed TaskThreads that should be retained after they stop
     */
    protected synchronized NSMutableArray retainedThreads()
    {
        return retainedThreads;
        /** ensure [valid_result] Result != null;   **/
    }



    /**
	 * @param thread the thread to test if it should be retained
	 * @return <code>true</code> if thread is in the list of retainedThreads()
	 */
    protected synchronized boolean shouldRetainThread(TaskThread thread) {
    	/** require [valid_thread] thread != null;   **/
    	return retainedThreads().containsObject(thread);
	}



    /**
	 *
	 *
	 * @param thread thread to add to list of threads to be retained after they end
	 */
    protected synchronized void retainThread(TaskThread thread) {
    	/** require [valid_thread] thread != null;   **/
    	retainedThreads().addObject(thread);
    	/** ensure [was_retained] shouldRetainThread(thread);   **/
	}



    /**
     * Returns the active TaskThread that is managing targetTask.  deadThreads() are not returned.
     * @param targetTask task identifying thread to return
     * @return TaskThread from taskThreads() that is managing targetTask, or null if not found
     */
    public synchronized TaskThread threadForTask(ITask targetTask)
    {
        /** require [valid_task] targetTask != null;   **/
        for (int i = 0; i < taskThreads.count(); i++)
        {
            TaskThread aTaskThread = (TaskThread) taskThreads.objectAtIndex(i);
            if (targetTask.equals(aTaskThread.task()))
            {
                return aTaskThread;
            }
        }
        return null;
    }



    /**
     * The method {@link #taskThreadEnded(NSNotification)} needs to terminate the application in response to a
     * notification.  However, there is a race condition that can result in the exception below if that the
     * application is terminated directly in the notification handler.  To avoid that, the notification handler
     * creates and starts this thread which will terminate the application after a short delay.
     *
     * <pre><code>
     * 2009-08-08 05:00:00,143 <Transfer Results Hook Files for SCA> ThreadManager - Last task thread ended, asking application to terminate.
     * Exception in thread "Transfer Results Hook Files for SCA" com.webobjects.foundation.NSForwardException java.lang.InterruptedException null:java.lang.InterruptedException
     * at com.webobjects.foundation.NSForwardException._runtimeExceptionForThrowable(NSForwardException.java:39)
     * at com.webobjects.foundation.NSSelector._safeInvokeMethod(NSSelector.java:124)
     * at com.webobjects.foundation.NSNotificationCenter$_Entry.invokeMethod(NSNotificationCenter.java:588)
     * at com.webobjects.foundation.NSNotificationCenter.postNotification(NSNotificationCenter.java:532)
     * at net.global_village.threadedtasks.TaskThread.run(TaskThread.java:173)
     * Caused by: java.lang.InterruptedException
     * at com.webobjects.appserver.WOApplication.applicationTimer(WOApplication.java:1126)
     * at com.webobjects.appserver.WOApplication$QuitTask.start(WOApplication.java:477)
     * at com.webobjects.appserver.WOApplication.terminate(WOApplication.java:1116)
     * at net.global_village.woextensions.WOApplication.terminate(WOApplication.java:1336)
     * at net.global_village.threadedtasks.ThreadManager.taskThreadEnded(ThreadManager.java:200)
     * at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
     * at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:39)
     * at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:25)
     * at java.lang.reflect.Method.invoke(Method.java:585)
     * at com.webobjects.foundation.NSSelector._safeInvokeMethod(NSSelector.java:122)
     * ... 3 more
     * 2009-08-08 05:00:00,144 <WorkerThread13> WOApplication - **** Application shut down initiated.
     * </code></pre>
     *
     * @author Copyright (c) 2006-2009 Harte-Hanks Shoppers, Inc.
     */
    private class DelayedTermination extends Thread
    {
        public void run()
        {
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
            }
            logger.info("Last task thread ended, asking application to terminate.");
            WOApplication.application().terminate();
        }

        public DelayedTermination()
        {
        }
    }


}
