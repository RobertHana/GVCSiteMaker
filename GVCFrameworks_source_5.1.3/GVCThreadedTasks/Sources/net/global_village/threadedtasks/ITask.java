package net.global_village.threadedtasks;

import com.webobjects.eocontrol.*;


/**
 * Interface defining the methods that a task must implement.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public interface ITask
{


    /**
     * Returns a user present-able name for this task.
     * If Java had real class methods, this would be a class method.  Alas...
     *
     * @return a user present-able name for this task
     */
    public abstract String name();



    /**
     * This is called when a thread managing tasks of this class is created (before the thread
     * starts running).  This method should do any cleanup needed if the previous exit was dirty.
     * If Java had real class methods, this would be a class method.  Alas...
     *
     * @param editingContext locked EOEditing context that can be used in the cleanup
     */
    public abstract void cleanup(EOEditingContext editingContext);



    /**
     * Performs this task.
     *
     * @param editingContext EOEditingContext to process the task in
     * @return a string giving the status of processing (Success, Failed, Problem, whatever)
     */
    public abstract String process(EOEditingContext editingContext);



    /**
     * Signals this task to stop.  This can do any cleanup needed, null variables, stop other threads etc.
     *
     * @param editingContext locked EOEditing context that can be used in the cleanup
     */
    public abstract void end(EOEditingContext editingContext);


}
