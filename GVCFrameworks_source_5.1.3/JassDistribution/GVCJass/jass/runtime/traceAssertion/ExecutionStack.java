/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.runtime.traceAssertion;

import java.util.Stack;

/**
 * Class declaration
 *
 *
 * @author
 * @version %I%, %G%
 */
public class ExecutionStack {
    Stack stack;

    /**
     * Constructor declaration
     *
     *
     * @see
     */
    public ExecutionStack() {
	stack = new Stack();
    }

    /**
     * Method declaration
     *
     *
     * @param entry
     *
     * @return
     *
     * @see
     */
    public boolean entryIsSupercall(ExecutionEntry entry) {
	boolean		 isSupercall = false;
	ExecutionEntry[] entries = getEntries();
	int		 iEntry = entries.length - 1;

	if (entry.getReference().isBeginOfMethod()) {
	    --iEntry;
	} 

	while (!isSupercall && iEntry >= 0) {
	    ExecutionEntry  stackEntry = entries[iEntry];
	    MethodReference callReference = entry.getReference();
	    MethodReference stackReference = stackEntry.getReference();
	    boolean	    sameObject = 
		stackEntry.getCallerId().equals(entry.getCallerId());
	    boolean	    sameMethodcall = 
		stackReference.getMethodname().equals(callReference.getMethodname());
	    boolean	    differentClass = 
		!stackReference.getClassname().equals(callReference.getClassname());

	    if (sameObject && sameMethodcall && differentClass) {
		isSupercall = true;
	    } 

	    // System.out.println("##  " + entry);
	    // System.out.println("<-->" + stackEntry);
	    // System.out.println();
	    --iEntry;
	} 

	return isSupercall;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public ExecutionEntry[] getEntries() {
	ExecutionEntry[] entries = new ExecutionEntry[stack.size()];

	stack.toArray(entries);

	return entries;
    } 

    /**
     * Method declaration
     *
     *
     * @param entry
     *
     * @see
     */
    public void push(ExecutionEntry entry) {
	stack.push(entry);
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public ExecutionEntry pop() {
	return (ExecutionEntry) stack.pop();
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-05-2001 ---*/

