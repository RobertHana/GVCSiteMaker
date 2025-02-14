
package net.global_village.threadedtasks;

import java.util.*;

import org.apache.log4j.*;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 * A thread that periodically performs a specific task and maintains a history of the recent
 * performances of that task.
 *
 * @see ITask
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class TaskThread extends Thread
{
    /** Name of NSNotification sent when this thread starts running.    */
    public static final String ThreadStartedNotification = "net.hhshoppers.cadre.core.queuedtask.TaskThreadStarted";

    /** Name of NSNotification sent when this thread stops running.    */
    public static final String ThreadEndedNotification = "net.hhshoppers.cadre.core.queuedtask.TaskThreadEnded";

    /** Name of NSNotification sent when this thread dies abnormally.  The userInfo() dictionary of the
        notification will have ThreadDiedReason key explaining why it died.    */
    public static final String ThreadDiedNotification = "net.hhshoppers.cadre.core.queuedtask.TaskThreadDied";

    /** Key into userInfo() dictionary for ThreadDiedNotification */
    public static final String ThreadDiedReason = "net.hhshoppers.cadre.core.queuedtask.TaskThreadDied";


    private Logger logger = LoggerFactory.makeLogger();
    private long frequency;
    private LinkedList  runHistory = new LinkedList();
    private int runHistorySize;
    private EOEditingContext editingContext;
    private volatile boolean shouldEnd = false;
    private ITask task;


    /**
     * Constructor.
     *
     * @param aTask object implementing ITask that determines the tasks managed by this thread
     * @param taskFrequency frequency in seconds
     * @param historySize number of past task checks and processes to remember
     * @param ec the EOEditingContext to use to process tasks in, this must not be locked as this object will lock it
     *
     */
    public TaskThread(ITask aTask, int taskFrequency, int historySize, EOEditingContext ec)
    {
        super();
        /** require [valid_class] aTask != null;
                    [valid_frequency] taskFrequency > 0;
                    [valid_historySize] historySize > 0;
                    [valid_ec] ec != null;
         **/

        editingContext = ec;
        runHistorySize = historySize;
        setFrequency(taskFrequency);
        task = aTask;
        logger.info("Created thread " + this);
        /** ensure [has_task] task() != null;
                   [has_editingContext] editingContext() != null;
                   [has_frequency] frequency() > 0;
                   [has_runHistorySize] runHistorySize() > 0;
         **/
    }



    /**
     * Implements processing the tasks.
     *
     * @see java.lang.Thread#run()
     */
    public void run()
    {
        logger.debug("run() called for " + task().name());
        setName(task().name());
        logger.info("Cleanup " + task().name());
        try
        {
            // Give task class a chance to cleanup if this thread crashed last time
            editingContext().lock();
            try
            {
                task().cleanup(editingContext());
                NSDelayedCallbackCenter.defaultCenter().eventEnded();
            }
            finally
            {
                editingContext().revert();  // In case the cleanup left a dirty state
                editingContext().unlock();
            }

            NSNotificationCenter.defaultCenter().postNotification(new NSNotification(ThreadStartedNotification, this));
            logger.info("Started " + task().name());

            while ( ! shouldEnd())
            {
                try
                {
                    setName(task().name());
                    logger.debug(getName() + " checking for tasks");
                    editingContext().lock();
                    try
                    {
                        recordRun(new TaskRunHistory(task().process(editingContext())));
                        NSDelayedCallbackCenter.defaultCenter().eventEnded();
                    }
                    catch (Exception e)
                    {
                        logger.error(getName() + " failed while processing task: ", e);
                        recordRun(new TaskRunHistory("Failed with exception: " + e.getMessage()));
                    }
                    finally
                    {
                        editingContext().revert();  // In case the processing left a dirty state
                        editingContext().unlock();
                    }

                    logger.debug(getName() + " waiting for " + frequency());
                    sleep(1000L * frequency());
                }
                catch (InterruptedException e)
                {
                    // Our sleep was interrupted, ignore as the next while check will see if we need to terminate
                    logger.info(getName() + " interrupted");
                }
                catch (Exception e)
                {
                    logger.error(getName() + " failed while processing task: ", e);
                }
            }
        }
        catch (Exception e)
        {
            // Something bad happened, record it as the final status and broadcast the "thread died!" notice
            recordRun(new TaskRunHistory(e.getLocalizedMessage()));
            NSNotificationCenter.defaultCenter().postNotification(new NSNotification(ThreadDiedNotification,
                                                                                     this,
                                                                                     new NSDictionary(e.getLocalizedMessage(), ThreadDiedReason)));
            throw new ExceptionConverter(e);
        }
        finally
        {
            logger.info(getName() + " stopping");

            // The task has now finished any processing; tell it to end
            editingContext().lock();
            try
            {
                task().end(editingContext());
            }
            finally
            {
                editingContext().unlock();

                // Clear status if were were interrupted to signal a stop
                if (Thread.interrupted())
                {
                    logger.debug("Interrupt status cleared");
                }

                NSNotificationCenter.defaultCenter().postNotification(new NSNotification(ThreadEndedNotification, this));
            }
        }
    }



    /**
     * Call this to signal this tread to end when it completes processing (if any).
     */
    public void end()
    {
        logger.info(getName() + " told to end");
        shouldEnd = true;
        interrupt();
        /** ensure [shouldEnd] shouldEnd();  **/
    }



    /**
     * @return <code>true</code> if end() has been called
     */
    public boolean shouldEnd()
    {
        return shouldEnd;
    }



    /**
     * Returns the frequency in seconds for run() to call process().
     *
     * @return the frequency in seconds for run() to call process()
     */
    public long frequency()
    {
        return frequency;
        /** ensure [above_zero] Result > 0;  **/
    }



    /**
     * Sets the frequency in seconds for run() to call process().
     *
     * @param newFrequency the frequency in seconds for run() to call process()
     */
    public void setFrequency(long newFrequency)
    {
        /** require [above_zero] newFrequency > 0;  **/
        frequency = newFrequency;
    }



    /**
     * The run() method calls this to record the time and result of a call to process().
     * This method limits the size of runHistory() to runHistorySize().
     *
     * @param run TaskRunHistory to record in the runHistory queue.
     */
    protected void recordRun(TaskRunHistory run)
    {
        /** require [valid_history] run != null;   **/
        if (runHistory().size() == runHistorySize())
        {
            runHistory().remove();
        }
        runHistory().add(run);

        /** ensure [recorded] runHistory().contains(run);
                   [correct_size] runHistory().size() <= runHistorySize();
         **/
    }



    /**
     * Returns the TaskRunHistory from the most recent call to process().  Returns
     * null if process has not yet been called.
     *
     * @return the TaskRunHistory from the most recent call to process()
     */
    public TaskRunHistory lastRun()
    {
        return runHistory().size() > 0 ? (TaskRunHistory)runHistory().getLast() : null;
    }



    /**
     * Returns the queue of TaskRunHistory.  The first is the oldest.
     *
     * @return the runHistory
     */
    public LinkedList runHistory()
    {
        return runHistory;
        /** ensure [non_null] Result != null;
                   [correct_size] Result.size() <= runHistorySize();
         **/
    }



    /**
     * The maximum size that runHistory() can grow to.
     *
     * @return maximum size that runHistory() can grow to
     */
    public int runHistorySize()
    {
        return runHistorySize;
        /** ensure [above_zero] Result > 0;  **/
    }



    /**
     *Sets the maximum size that runHistory() can grow to.
     *
     * @param size the maximum size that runHistory() can grow to
     */
    public void setRunHistorySize(int size)
    {
        /** require [above_zero] size > 0;  **/
        runHistorySize = size;
    }



    /**
     * Returns the EOEditingContext that is used for EO tasks in this thread.
     *
     * @return the EOEditingContext that is used for EO tasks in this thread
     */
    protected EOEditingContext editingContext()
    {
        return editingContext;
        /** ensure [non_null] Result != null;   **/
    }



    /**
     * Returns the task that this thread is performing.
     *
     * @return the task that this thread is performing
     */
    public ITask task()
    {
        return task;
        /** ensure [non_null] Result != null;   **/
    }



    /**
     * Returns result useful for logging and debugging.
     *
     * @see java.lang.Thread#toString()
     *
     * @return result useful for logging and debugging
     */
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        sb.append(task().name());
        sb.append(", frequency ");
        sb.append(frequency());

        return sb.toString();
    }


    /**
     * Simple class encapsulating a time and a status string.
     */
    public class TaskRunHistory
    {
        private NSTimestamp runTime;
        private String status;


        /**
         * Constructor.  Records runTime as the current time.
         *
         * @param aStatus String to record as status
         */
        public TaskRunHistory(String aStatus)
        {
            super();
            // Contracts are commented out as Jass can't handle nested classes with contracts
            //** require [valid_aStatus] aStatus != null; **/
            runTime = new NSTimestamp();
            status = aStatus;
        }



       /**
        * @return the time this TaskRunHistory was created
        */
       public NSTimestamp runTime()
       {
           return runTime;
           //** ensure [non_null] Result != null;   **/
       }



       /**
        * @return the status result this TaskRunHistory was created with
        */
       public String status()
       {
           return status;
           //** ensure [non_null] Result != null;   **/
       }

    }


}
