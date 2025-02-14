// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core.support;

import java.util.*;

import net.global_village.foundation.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.interfaces.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;
import com.webobjects.foundation.NSComparator.*;

/**
 * The class that manages the queued Tasks.  It fetches the Tasks and knows the next Task to process. Process one Task at a time.
 */
public class TaskQueue extends Object 
{
	protected EOEditingContext editingContext;
	protected NSArray queue;
	
    /**
     * Designated constructor
     */	
	public TaskQueue()
	{
		super();

        editingContext = ((SMApplication) WOApplication.application()).newEditingContext();
        editingContext.lock();
        queue = null;
	}
    
	
    
    /**
     * Returns the all the inProgress and waiting Tasks in the system sorted according to Task.TaskComparator
     * 
     * @return all the Tasks in the system sorted according to Task.TaskComparator
     */		
	public NSArray queue()
	{			
		if (queue == null)
		{
	        	EOQualifier inProgressQualifier = new EOKeyValueQualifier("status.key", EOQualifier.QualifierOperatorEqual, TaskStatus.inProgressStatusKey);
	        	EOQualifier waitingQualifier = new EOKeyValueQualifier("status.key", EOQualifier.QualifierOperatorEqual, TaskStatus.waitingStatusKey);
			EOOrQualifier qualifier = new EOOrQualifier(new NSArray(new Object[] {inProgressQualifier, waitingQualifier}));
		    EOFetchSpecification fetchSpec = new EOFetchSpecification("Task", qualifier, null);
		    fetchSpec.setRefreshesRefetchedObjects(true);

            NSArray allTasks = editingContext().objectsWithFetchSpecification(fetchSpec);	
			
	        try
	        {
	        		queue = allTasks.sortedArrayUsingComparator(Task.TaskComparator);
	        }
	        catch (ComparisonException e)
	        {
	            throw new ExceptionConverter(e);
	        }			
		}
		
        return queue;
        
        /** ensure [result_not_null] Result != null; **/
	}
	
	
	
    /**
     * Refreshes the queue by setting it to null so it is forced to refetch from the database.
     */		
	public void refreshQueue()
	{			
		queue = null;
 	}

	
	
    /**
     * Returns the next Task that is waiting in queue, null if no Task in waiting
     * 
     * @return the next Task that is waiting in queue.
     */		
	public Task nextTaskWaitingInQueue()
	{
		Task nextTaskWaitingInQueue = null;
		boolean hasFoundNextTask = false;
		
        Enumeration queueEnum = queue().objectEnumerator();

        while (queueEnum.hasMoreElements() && ( ! hasFoundNextTask))
        {
            Task aTask = (Task) queueEnum.nextElement();
            if (aTask.isWaiting())
            {
            		nextTaskWaitingInQueue = aTask;
            		hasFoundNextTask = true;
            }
        }		

        return nextTaskWaitingInQueue;
	}		
	
	
	
    /**
     * Returns true if there are any inProgress Tasks currently in the system
     * 
     * @return true if there are any inProgress Tasks currently in the system
     */		
	public boolean hasInProgressTasks()
	{
        return currentlyInProgressTasks().count() > 0;
	}
    
    
    
    /**
     * Returns true if there are any inProgress Tasks currently in the system
     * 
     * @return true if there are any inProgress Tasks currently in the system
     */     
    public NSArray currentlyInProgressTasks()
    {
        EOQualifier qualifier = new EOKeyValueQualifier("status.key", EOQualifier.QualifierOperatorEqual, TaskStatus.inProgressStatusKey);
        return EOQualifier.filteredArrayWithQualifier(queue(), qualifier);
        
        /** ensure [result_not_null] Result != null; **/        
    }    
	
	
	
    /**
     * Checks queue if there is a Task waiting in queue and performs Task.  Sends proper notification based on result of performing the Task.
     */		
	public void checkQueue()
	{
        try
        {
            DebugOut.println(1, "Check queue...");

            refreshQueue();

            if (!hasInProgressTasks())
            {
                Task nextTask = nextTaskWaitingInQueue();

                if (nextTask != null)
                {
                    nextTask.markTaskAsInProgress();

                    // Run task in its own EC so that it gets fresh data to copy
                    EOEditingContext taskEC = ((SMApplication) WOApplication
                                    .application()).newEditingContext();
                    taskEC.lock();
                    try
                    {
                        QueuableTask taskInTaskEC = (QueuableTask) EOUtilities
                                        .localInstanceOfObject(taskEC, nextTask);
                        try
                        {
                            if (taskInTaskEC.performTask())
                            {
                                nextTask.markTaskAsCompleted();
                            }
                            else
                            {
                                nextTask.markTaskAsFailed();
                            }                           
                        }
                        catch (Throwable t)
                        {
                            // calling taskEC.revert() can result in exceptions due to the use of NotifyingEditingContext
                            // So a new EC is created to ensure that markTaskAsFailed() will succeed.
                            EOEditingContext ec = new EOEditingContext();
                            ec.lock();
                            try
                            {
                                Task localTask = (Task) EOUtilities.localInstanceOfObject(ec, nextTask);
                                localTask.setFailureReason(t.getMessage());
                                localTask.markTaskAsFailed();                                
                            }
                            finally
                            {
                                ec.unlock();
                                ec.dispose();  
                            }

                            throw t;
                        }
                    }
                    finally
                    {
                        taskEC.unlock();
                        taskEC.dispose();
                    }
                }
            }
        }
        catch (Throwable t)
        {
            DebugOut.println(1, "Caught Exception in checkQueue " + t.getMessage());
            DebugOut.printStackTrace(1, t);
        }
    }	
	
    
    
    /**
     * Checks if there are inProgress Tasks and change their status to waiting
     * so it will be processed again. This is best called before the app the
     * processes the Tasks is started. This is a precaution in case the app
     * crashed while processing a Task.
     */     
    public void revertInProgressTasks()
    {
        NSLog.out.appendln("Updated Code");
        Enumeration inProgressTasksEnum = currentlyInProgressTasks().objectEnumerator();
        while (inProgressTasksEnum.hasMoreElements())
        {
            Task aTask = (Task) inProgressTasksEnum.nextElement();
            aTask.markTaskAsWaiting();
        }
            
        /** ensure [has_no_inProgress_Tasks] ( ! hasInProgressTasks()); **/
    }       
    
    
    
    /**
     * The editing context it uses to fetch the Tasks
     */	
	public EOEditingContext editingContext()
	{
		return editingContext;
	}	
}
