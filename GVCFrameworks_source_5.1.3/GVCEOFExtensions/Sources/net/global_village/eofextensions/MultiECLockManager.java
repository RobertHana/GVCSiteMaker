/* MultiECLockManager.java created by j-rochkind@northwestern.edu on Mon 21-Jul-2003 */

package net.global_village.eofextensions;

import java.lang.ref.*;
import java.lang.reflect.*;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 * <p>This class handles locking of EOEditingContexts local to a session.  Each WOSession should have 
 * one of these MultiECLockManager objects. The session should call lock() on it in awake() and 
 * unlock() in sleep().</p>
 * 
 * <p>The preferred way to create new editing contexts is to use either <code>newEditingContext()</code> 
 * or <code>newEditingContext(EOEditingContext parentEC)</code>.  If you want to create 
 * your own then you must call registerEditingContext(EOEditingContext) before using the editing
 * context.  Registering an editing context will ensure that it is locked if the session's default 
 * editing context has been locked.  Unregistering????</p>
 * 
 * <p>A particular thread can call lock() multiple times, as long it calls unlock() the same number 
 * of times to relinquish all locks---that is, lock() functions recursively.</p>
 * 
 * <p>If a particular thread has outstanding lock() calls not undone with unlock(), only that thread 
 * may call unlock() or registerEditingContext(). If any other thread calls these methods in this 
 * state, an exception will be raised. But in the intended design, this shouldn't be possible anyway. 
 * Any thread may call unregisterEditingContext at any time---ECs are queued up for unregistering at
 * an appropriate time and in an appropriate thread, if neccesary.<p>
 * 
 * <p>Suggested implementation of WOSession sub-class using the MultiECLockManager:</p>
 * <pre>
 * public class Session extends WOSession implements MultiECLockManager.Session
 * {
 *     protected MultiECLockManager ecLockManager;
 * 
 * public Session()
 *     {
 *         super();
 *         ecLockManager = new MultiECLockManager(EOEditingContext.class);
 *     }
 * 
 *     public void awake()
 *     {
 *         super.awake();
 *         ecLockManager().lock();
 *     }
 * 
 *     public void sleep()
 *     {
 *         // Need to check as this gets called after terminate() when logging out
 *         if (ecLockManager().isLocked())
 *         {
 *             ecLockManager().unlock();
 *         }
 *         super.sleep();
 *     }
 * 
 *     public void terminate()
 *     {
 *         // Need to make sure this is unlocked so that editing contexts nested 
 *        // in the defaultEditingContext don't hold locks on it when it gets disposed 
 *         if (ecLockManager().isLocked())
 *         {
 *             ecLockManager().unlock();
 *        }
 *         
 *        super.terminate();
 *     }
 * 
 *     public EOEditingContext newEditingContext()
 *     {
 *         return ecLockManager().newEditingContext();
 *     }
 * 
 *     public EOEditingContext newEditingContext(EOEditingContext parentEC)
 *     {
 *         return ecLockManager().newEditingContext(parentEC);
 *     }
 * 
 *     public void discard(EOEditingContext ec)
 *     {
 *         ecLockManager().unregisterEditingContext(ec);
 *     }
 * 
 *     public MultiECLockManager ecLockManager() {
 *         return ecLockManager;
 *     }
 * 
 * }
 * </pre>
 * <p>Original code from Jonathan Rochkind (j-rochkind at northwestern.edu) taken from 
 * http://wocode.com/cgi-bin/WebObjects/WOCode.woa/1/wa/ShareCodeItem?itemId=301
 * Nov, 2003 - added DBC, modified to create and return registered editing contexts, Chuck Hill
 * 
 * @author Jonathan Rochkind
 */
public class MultiECLockManager 
{
    protected NSMutableSet weakReferences = new NSMutableSet();
    protected NSMutableDictionary strongReferences = new NSMutableDictionary();
    protected NSMutableArray unregisterQueue = new NSMutableArray();
    protected int lockCount = 0;
    protected Thread lockerThread;
    protected Class editingContextClass;
    private static final Class EOEditingContextClass = EOEditingContext.class; // Hack around for Jass
    
    
    /**
     * Designated constructor.  The paramter is useful to create a manager that creates non-standard
     * editing contexts such as the LockScreamerEditingContext. 
     * 
     * @param classOfEditingContexts the class for this MultiECLockManager to create new editing 
     * context instances from.
     */
    public MultiECLockManager(Class classOfEditingContexts)
    {
        super();
        /** require [valid_class] classOfEditingContexts != null;  **/
        editingContextClass = classOfEditingContexts;
    }
    


    /**
     * Convenience constructor for an MultiECLockManager that creates editing contexts of class
     * EOEditingContext.
     */
    public MultiECLockManager()
    {
        this(EOEditingContextClass);
    }

    

    /**
     * Locks all registered ECs. Creates strong references to registered ECs, to ensure they don't 
     * get garbage collected with outstanding locks.  Records which thread has requested the lock.
     */
    public synchronized void lock() 
    {
        // If used as intended, with lock() and unlock() happening in session awake()and sleep(), or 
        // equivelent -- then I don't think there's any way that thread2 can be attempting to lock() 
        // while thread1 has lock()ed and not yet unlock()ed. However, if this DOES happen -- I'm not 
        // confident this code is deadlock-safe. So just to be sure, we throw an exception if it 
        // does happen, even though we don't believe it ever should. If the exception is thrown, it 
        // really indicates that this code needs to be redesigned.  Of course if the class is not 
        // used as intended...
        sameThreadAssert("Attempt to lock from a second thread while locked from another.");

        // We make strong references to all ECs that we are going to lock, to keep them from getting 
        // Garbage Collected while they have outstanding locks. In the case of Nested ECs especially,
        // it's disasterous to have them GC'd with outstanding locks.
        makeStrongReferences();

        //Now we go through all the registered ECs and lock them one by one.
        NSArray allECs = strongReferences.allValues();
        for (int i = allECs.count() - 1; i >= 0; i--) 
        {
            EOEditingContext ec = (EOEditingContext) allECs.objectAtIndex(i);
            ec.lock();
        }
        
        
        // We record which thread is doing the lock, because some operations may be dependent on 
        // this, we may want to disallow some operations from a different thread, while outstanding 
        // locks exist.
        lockerThread = Thread.currentThread();
        lockCount++;

        /** ensure [all_ecs_locked] true; /# API provides no isLocked() function. #/
                   [thread_recorded] lockerThread != null;
                   [lock_count_incremented] lockCount > 0; **/
    }



    /**
     * Unlocks all registered ECs.  If unlock() has been called as many times as lock(),all strong 
     * references are removed, the locking thread discarded, and any ECs previously put in the queue 
     * to unregister are unregistered.
     * 
     * @throws IllegalStateException if no locks are outstanding, or if a thread other than the one 
     * which locked tries to unlock.
     */
    public synchronized void unlock() 
    {
        if ( ! isLocked()) 
        {
            throw new IllegalStateException("MultiECLockManager: Attempt to unlock without a previous lock!");
        }
        
        sameThreadAssert("Can't unlock from different thread than locked!");


        //We know we have strong references already set, because we did that upon locking.
        NSArray allECs = strongReferences.allValues();
        for ( int i = allECs.count() - 1; i >= 0; i--) 
        {
            EOEditingContext ec = (EOEditingContext) allECs.objectAtIndex( i );
            ec.unlock();
        }

        lockCount--;

        if ( ! isLocked() ) 
        {
            //unregister anyone we have waiting in the queue.
            emptyUnregisterQueue();
            //We don't need the strong references anymore, we have no locks.
            strongReferences.removeAllObjects();
            //And we have no locker thread anymore, we're done with locks
            //for the moment.
            lockerThread = null;
        }
        /** ensure [isLocked_or_valid_state] isLocked() ||
                                             ((unregisterQueue.count() == 0) && 
                                              (strongReferences.count() == 0) &&
                                               (lockerThread == null)             );      **/
    }



    /**
     * Returns a new peer editing context.  All editing contexts should be created with this method 
     * or it's parent version, below.
     *
     * @return a new peer editing context
     */
    public synchronized EOEditingContext newEditingContext()
    {
        return newEditingContext(EOEditingContext.defaultParentObjectStore()); 
        /** ensure [valid_result] Result != null; 
                   [is_registered] findReference(Result) != null;
                   [is_locked] ( ! isLocked()) || 
                               (strongReferences.objectForKey(findReference(Result)) != null);  **/
    }



    /**
     * Returns a new child editing context.
     *
     * @param parentStore the new editing context's parent EOObjectStore
     * @return a new child editing context
     */
    public synchronized EOEditingContext newEditingContext(EOObjectStore parentStore)
    {
        /** require [valid_param] parentStore != null; **/
        try
        {
            Class ecClass = editingContextClass();
            Constructor ecConstructor = ecClass.getConstructor(new Class[] { EOObjectStore.class });
            EOEditingContext ec = (EOEditingContext) ecConstructor.newInstance(new Object[] { parentStore });
            registerEditingContext(ec); 
            return ec;
        }
        catch (Exception e)
        {
            throw new ExceptionConverter(e);
        }
        /** ensure [valid_result] Result != null; 
                   [is_registered] findReference(Result) != null;
                   [is_locked] ( ! isLocked()) || 
                               (strongReferences.objectForKey(findReference(Result)) != null);  **/
    }



    /**
     * Registers EC. If other registered ECs are currently locked, locks the newly registered EC the 
     * same number of times to match the outstanding locks on other registered ECs. If there are 
     * locks outstanding, and a thread other than the one which locked is attempting to register an 
     * EC, throw an exception.  If the EC argument is ALREADY registered, do nothing.
     * 
     * @param ec
     */
    public synchronized void registerEditingContext(EOEditingContext ec) 
    {
        /** require [valid_param] ec != null;  **/

    	// If we are currently locked, and ANOTHER thread is trying to register an EC, that's very 
        // bad. Let's throw an exception.
        sameThreadAssert("When ECs are locked, can't register an EC from another thread!");

        //Make sure we catch a double registration--we don't want to have it registered twice!
        WeakReference alreadyRegistered = findReference(ec);
        if (alreadyRegistered != null)
        {
            //throw or ignore? Let's just ignore.
            return;
        }

        WeakReference ref = new WeakReference(ec);
        weakReferences.addObject(ref);

        // If we already have some locks, and we're registering a new EC, lock it the proper number 
        // of times to bring it up to speed.  Remember, we made sure we are in the lockerThread.
        if (isLocked())
        {
            strongReferences.setObjectForKey(ec, ref);
            for ( int i = 0; i < lockCount; i++) 
            {
                ec.lock();
            }
        }
        /** ensure [is_registered] findReference(ec) != null;
                   [is_locked] ( ! isLocked()) || 
                               (strongReferences.objectForKey(findReference(ec)) != null);  **/
    }



    /**
     * Unregisters EC from receiver. If the EC is not currently registered, does nothing.  If there 
     * are outstanding locks, does not unregister the EC immediately, instead puts it in a queue to 
     * unregister. The queue will be handled when there are no longer outstanding locks. This means 
     * it should be safe to unregister an EC in some other objects finalize() method, even though 
     * that will be in an unpredictable thread.
     */
    public synchronized void unregisterEditingContext(EOEditingContext argEC) 
    {
        /** require [valid_param] argEC != null;  **/

        //Find it's weak reference
        WeakReference correspondingReference = findReference(argEC);
        if ( correspondingReference == null ) 
        {
            //Hmm, it's not registered. Ignore, or throw?  Let's ignore.
            return;
        }
        
        /* The EC needs to be unlocked the same number of times it was locked.  This actually only 
         * matters with Nested ECs, but it really does matter with them. We'll handle this by just 
         * adding it to a queue to deregister later after we've completely unlocked---this way we 
         * don't need to worry about what thread is calling unregister, if it's a different thread
         * than the one that locked.
         */
        if ( isLocked() ) 
        {
            unregisterQueue.addObject( correspondingReference );
        }
        else 
        {
            //No outstanding locks, we can deregister immediately.
            weakReferences.removeObject( correspondingReference );
        }
        /** ensure [queued_or_unregistered] (findReference(argEC) == null) ||
                                            unregisterQueue.containsObject(findReference(argEC));  **/
    }



    /**
     * Makes strong references to all registered ECs, by putting them in a dictionary keyed by their 
     * weak reference. This is the method that will remove stale EC WeakReferences, if they're 
     * referent has been garbage collected.
     */
    protected synchronized void makeStrongReferences() 
    {
        NSArray weakReferencesArr = weakReferences.allObjects();
        for ( int i = weakReferencesArr.count() - 1; i >= 0; i--) 
        {
            WeakReference ref = (WeakReference) weakReferencesArr.objectAtIndex( i );
            EOEditingContext ec = (EOEditingContext) ref.get();
            if (ec == null)
            {
                // It's been garbage collected so remove it from the list, we don't need it anymore.
                weakReferences.removeObject(ref);
            }
            else
            {
                // Store the strong reference in a dictionary, to keep it from being collected.
                strongReferences.setObjectForKey(ec, ref);
            }
        }
        /** ensure [all_references_made] weakReferences.count() == strongReferences.count();   **/
    }



    /**
     * Since we often are only holding onto a WeakReference to our registered ECs, to allow ECs to 
     * be GC'd even though they are registerd, this is a convenience method to find the appropriate 
     * WeakReference given an EC that's registered.  If the argument is NOT a registered EC, will 
     * return null.
     * 
     * @param argEC the <code>EOEditingContext</code> to return a <code>WeakReference</code> for
     * @return <code>WeakReference</code> for <code>argEC</code> or null if none found
     */
    protected synchronized WeakReference findReference(EOEditingContext argEC) 
    {
        /** require [valid_param] argEC != null;  **/

        WeakReference correspondingReference = null;
        NSArray weakReferencesArr = weakReferences.allObjects();
        for ( int i = weakReferencesArr.count() - 1; i >= 0; i--) 
        {
            WeakReference ref = (WeakReference) weakReferencesArr.objectAtIndex( i );
            EOEditingContext checkEC = (EOEditingContext) ref.get();
            if ( checkEC != null && checkEC == argEC ) 
            {
                correspondingReference = ref;
                break;
            }
        }
        return correspondingReference;
    }



    /**
     * Any ECs that had unregistration requested with outstanding locks, just get added to the 
     * unregisterQueue. When there are no longer outstanding locks, this method is called to 
     * unregister all those ECs.
     */
    protected synchronized void emptyUnregisterQueue() 
    {
        for ( int i = unregisterQueue.count() - 1; i >= 0; i--) 
        {
            WeakReference ref = (WeakReference) unregisterQueue.objectAtIndex( i );
            weakReferences.removeObject( ref );
            unregisterQueue.removeObjectAtIndex( i );
        }
        /** ensure [all_unregistered] (unregisterQueue.count() == 0);  **/
    }

    
    
    /**
     * This method is intended to be called from some operations in the MultiECLockManager to ensure
     * proper usage.  Throws an exception if there is a positive lockCount, and the current thread 
     * is not the same thread that made the lock().  In general, for intended uses of the 
     * MultiECLockManager, this should never happen. But code that requires it to be true calls this 
     * assert message just in case. If an exception is thrown, it either indicates a problem with 
     * the MultiECLockManager code (possibly requiring a redesign so the code does NOT require this 
     * assert), or a problem with developer code using this object.
     * 
     * @param messageAddition additional text for the exception message
     * @throws IllegalStateException if there is a positive lockCount and the current thread is not
     * the same thread that made the lock()
     */
    private void sameThreadAssert(String messageAddition) throws IllegalStateException 
    {
        if ( isLocked() ) 
        {
            Thread currentThread = Thread.currentThread();
            if ( currentThread != lockerThread ) 
            {
                throw new IllegalStateException(
                    "MultiECLockManager: " + messageAddition + 
                    "; current thread: " + currentThread.getName() + 
                    "; original locking thread: " + lockerThread.getName());
            }
        }
    }



    /**
     * Returns EOEditingContext.class or the subclass that this  MultiECLockManager returns when 
     * asked for a new instance.
     *
     * @return EOEditingContext.class or the subclass that this  MultiECLockManager returns
     */
    protected Class editingContextClass()
    {
        return editingContextClass;
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * Returns <code>true</code> if this MultiECLockManager is currently locked.
     * 
     * @return <code>true</code> if this MultiECLockManager is currently locked
     */
    public synchronized boolean isLocked()
    {
        return lockCount > 0;
    }



    /**
     * Interface for a session to advertise that it has a MultiECLockManager used for locking all 
     * 'local' ECs within that session. This interface is only meant to be implemented by a 
     * WOSession, and that WOSession subclass is responsible for locking/unlocking the 
     * MultiECLockManager in appropriate places. (awake() and sleep() reccomended ). 
     */
    public static interface Session 
    {
    }



    /** invariant [has_weakReferences] weakReferences != null;
                  [has_strongReferences] strongReferences != null;
                  [has_unregisterQueue] unregisterQueue != null;
                  [has_editingContextClass] editingContextClass != null;
                  [valid_lockCount] lockCount >= 0;
                  [thread_recorded] (lockCount == 0) || (lockerThread != null); **/


}
