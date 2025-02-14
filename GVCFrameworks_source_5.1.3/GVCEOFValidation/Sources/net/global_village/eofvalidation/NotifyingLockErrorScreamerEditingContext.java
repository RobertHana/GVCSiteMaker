// Based on / shamelessly copied from:
// LockErrorScreamerEditingContext.java
//
// Copyright (c) 2002 Red Shed Software. All rights reserved.
// by Jonathan 'Wolf' Rentzsch (jon at redshed dot net)
// enchanced by Anthony Ingraldi (a.m.ingraldi at larc.nasa.gov)
//
// Thu Mar 28 2002 wolf: Created.
// Thu Apr 04 2002 wolf: Made NSRecursiveLock-aware by Anthony.

package net.global_village.eofvalidation;

import java.io.*;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


public class NotifyingLockErrorScreamerEditingContext extends NotifyingEditingContext
{
	private static final long serialVersionUID = -8721424334953123208L;

	private String _nameOfLockingThread = null;
	private NSMutableArray _stackTraces = new NSMutableArray();


    /**
     * Standard constructor.
     *
     */
     public NotifyingLockErrorScreamerEditingContext() 
     {
         super();
     }



    /**
     * Constructor for nested editing contexts.
     * 
     * @param parent EOEditingContext that this editing context is a child of
     */
    public NotifyingLockErrorScreamerEditingContext(EOObjectStore parent) 
    {
         super(parent);
     }



    /**
     * Overridden to capture trace of where lock was taken and to show error and 
     * trace if the editing context is already lock by by a thread other than the
     * one asking for the lock.  For editing contexts associated with sessions
     * this usually results in deadlock for the session and also for the application
     * if requrests are not being dispatched concurrently.
     */
    public void lock() 
    {
        String nameOfCurrentThread = Thread.currentThread().getName();
        
        // If we have not recorded any traces this editing context is not currently
        // locked and this results in a the lock being taken by a new thread.
        if (_stackTraces.count() == 0) 
        {
            _stackTraces.addObject(_trace());
            _nameOfLockingThread = nameOfCurrentThread;
            if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed)) NSLog.debug.appendln("+++ Lock number (" +  _stackTraces.count() + ") for " + this + " in " + nameOfCurrentThread);
        }
        else // This editing context has already been locked.
        {
            // If the thread is the same then this is a secondary call that 
            // results in an increased recursionCount() for the NSRecursiveLock.
            if (nameOfCurrentThread.equals(_nameOfLockingThread)) 
            {
                _stackTraces.addObject(_trace());
                if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed)) NSLog.debug.appendln("+++ Lock number (" + _stackTraces.count() + ") for " + this + " in " + nameOfCurrentThread);
            } 

            // If the thread is not the same it will block.  For editing contexts
            // in a session this results in deadlock so an error message is output.
            // The error includes the trace of the most recent lock taken which is
            // probably the offending unreleased lock.  It might not be if your 
            // lock and unlocks are not cleanly nested.  In that case you might 
            // need to capture and display traces of all the lock and unlock calls
            // to find what is not nested correctly.
            else
            {
                NSLog.err.appendln("!!! Attempting to lock editing context " + this + " from " + nameOfCurrentThread
                		+ " that was previously locked in " + _nameOfLockingThread);
                NSLog.err.appendln("!!! Current stack trace: \n" + _trace());
                NSLog.err.appendln("!!! Stack trace for most recent lock: \n" + _stackTraces.lastObject());
            }
        }
        super.lock();
    }



    /**
     * Overridden to capture trace of where lock was taken and to show error and 
     * trace if the editing context is already lock by by a thread other than the
     * one asking for the lock.
     */
     public void unlock() 
     {
         // This will throw an IllegalStateException if the editing context is
         // not locked, or if the unlocking thread is not the thread with the lock.  
         super.unlock();

         // This editing context is already locked, so remove the trace for the 
         // latest lock(), assuming that it corresponds to this unlock().
         if (_stackTraces.count() > 0)
             _stackTraces.removeLastObject();

         // No more traces means that we are no longer locked so dis-associate 
         // ourselves with the thread that had us locked.
         if (_stackTraces.count() == 0)
             _nameOfLockingThread = null;

         String nameOfCurrentThread = Thread.currentThread().getName();
         if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed)) NSLog.debug.appendln("--- Unlocked in " +  nameOfCurrentThread + " (" + _stackTraces.count() + " remaining) for " + this);
     }



    /**
     * Allowing locked editing contexts to be disposed is not a good practice.  
     * This method calls goodbye() to outputs a warning message if this happens.  
     */
     public void dispose() 
     {
         try 
         {
             goodbye();
         } 
         finally 
         {
             super.dispose();
         }
     }



    /**
     * Allowing locked editing contexts to be garbage collected is not a good 
     * practice.  This method calls goodbye() to outputs a warning message if 
     * this happens.  
     */
    protected void finalize() throws Throwable 
    {
    	try 
    	{
    		goodbye();
    	} 
    	finally 
    	{
    		super.finalize();
    	}
    }



    /**
     * Support method with check common to dispose() and finalize().  Allowing 
     * locked editing contexts to be garbage collected or disposed is not a good 
     * practice.  It is probably OK for peer editing contexts, but this is not a 
     * really good coding practice.  This method outputs a warning message if this
     * happens.  If you want to follow this bad practice, change the test below to:<br>
     * <code>((_stackTraces.count() != 0) && (parent() instanceof EOEditingContext))</code>
     */
    public void goodbye() 
    {
        if (_stackTraces.count() != 0) 
        {
            NSLog.err.appendln("!!! editing context " + this + " being disposed with " + _stackTraces.count() + " locks.");
            NSLog.err.appendln("!!! Most recently locked by: \n"
                               + _stackTraces.lastObject());
            NSLog.err.appendln("!!! Current stack trace: \n" + _trace());
        }
    }



    /**
     * Utility method to return the stack trace from the current location as a string.
     * 
     * @return the stack trace from the current location as a string
     */
     private String _trace() 
     {
         StringWriter stringWriter = new StringWriter();
         PrintWriter printWriter = new PrintWriter(stringWriter);
         (new Throwable()).printStackTrace(printWriter);
    
         return stringWriter.toString();
     }
}
