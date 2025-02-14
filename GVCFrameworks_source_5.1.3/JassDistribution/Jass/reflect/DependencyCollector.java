/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import jass.util.*;
import java.io.*;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class DependencyCollector {
    protected Set reads = new Set();
    protected Set calls = new Set();
    protected Set writes = new Set();
    protected Set creates = new Set();

    /**
     * Method declaration
     *
     *
     * @param pw
     * @param indent
     *
     * @see
     */
    public void dump(PrintWriter pw, String indent) {
	if (reads.elements().length > 0) {
	    pw.println();
	    pw.print(indent + "READS  :" + reads);
	} 

	if (calls.elements().length > 0) {
	    pw.println();
	    pw.print(indent + "CALLS  :" + calls);
	} 

	if (writes.elements().length > 0) {
	    pw.println();
	    pw.print(indent + "WRITES :" + writes);
	} 

	if (creates.elements().length > 0) {
	    pw.println();
	    pw.print(indent + "CREATES:" + creates);
	} 
    } 

    /**
     * Method declaration
     *
     *
     * @param e
     *
     * @see
     */
    public void addReads(Entity e) {
	reads.addElement(e);
    } 

    /**
     * Method declaration
     *
     *
     * @param e
     *
     * @see
     */
    public void addWrites(Entity e) {
	writes.addElement(e);
    } 

    /**
     * Method declaration
     *
     *
     * @param e
     *
     * @see
     */
    public void addCalls(Entity e) {
	calls.addElement(e);
    } 

    /**
     * Method declaration
     *
     *
     * @param c
     *
     * @see
     */
    public void addCreates(Creation c) {
	creates.addElement(c);
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Set getReads() {
	return reads;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Set getCalls() {
	return calls;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Set getWrites() {
	return writes;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Set getCreates() {
	return creates;
    } 

    /**
     * Method declaration
     *
     *
     * @param dc
     *
     * @see
     */
    public void union(DependencyCollector dc) {
	reads.union(dc.getReads());
	writes.union(dc.getWrites());
	creates.union(dc.getCreates());
	calls.union(dc.getCalls());
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

