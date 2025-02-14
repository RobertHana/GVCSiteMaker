package net.global_village.jmail;

import java.util.*;


/**
 * Simple implementation of FIFO queue to support ThreadedMailAgent.
 * 
 * This code is adapted from ERQueue from the ERJavaMail framework of Project Wonder.  This file 
 * is protected under the GNU Lesser Public License.
 * 
 * @author Camille Troillard <tuscland@mac.com>
 * @author Sacha Mallais <sacha@global-village.net>
 */
public class FIFOQueue extends Vector {

    protected int _maxSize = 0;
    public int maxSize () { return _maxSize; }
    public void setMaxSize (int size) { _maxSize = size; }

    public static class SizeOverflowException extends Exception {
        public SizeOverflowException () { super (); }
    }
    
    public FIFOQueue() {
        this (0);
    }

    public FIFOQueue(int maxSize) {
        super ();
        this.setMaxSize (maxSize);
    }

    public Object push (Object item) throws SizeOverflowException {
        if ((_maxSize == 0) || (this.size () < _maxSize))
            this.addElement (item);
        else
            throw new SizeOverflowException ();

        return item;
    }

    public synchronized Object pop () {
        Object element = this.elementAt (0);
        this.removeElementAt (0);
        return element;
    }

    public synchronized Object peek () {
        return this.elementAt (0);
    }

    public boolean empty () {
        return (this.size () == 0);
    }

    public synchronized int search (Object o) {
        return this.indexOf (o);
    }
}
