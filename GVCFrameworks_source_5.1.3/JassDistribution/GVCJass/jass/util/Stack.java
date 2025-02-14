package jass.util;
import java.lang.StringBuffer;

public class Stack implements Cloneable {
  protected int max;
  protected Object[] stack;
  protected int top;

  public Stack(int size) {
    stack = new Object[size];
    max = size;
    // no elements in stack
    top = 0;
  }
  
  public boolean empty() {
    return top == 0;
    /** ensure change {} **/    
  }
  
  public boolean full() {
    return top == max;
    /** ensure change {} **/
  }
  
  public Object pop() {
    /** require stack_is_not_empty: !empty() **/
    return stack[--top];
    /** ensure change {top}; jassOld.top == top+1; jassResult == stack[jassOld.top-1] **/
  }
  
  public void push(Object o) {
    /** require valid_object: o != null; stack_is_not_full: !full() **/
    stack[top++] = o;
    /** ensure change {top,stack}; stack[top-1] == o **/
  }

  public boolean contains(Object o) {
    /** require valid_object: o != null **/
    int i = 0;
		while (i < top) 
 			/** invariant 0 <= i && i <= top **/ 
 			/** variant top-i **/ {
	 	    if (stack[i].equals(o))	return true;
  		  i++;
  	}
		return false;
		/** ensure change {} **/
  }

	public Object peek() {
		/** require stack_is_not_empty: !empty() **/
		return stack[top-1];
		/** ensure change{} **/
	}

	/**
	*	Returns an array of all elements of the stack that are no stack marks.
	* @return the array of elements that are no stack marks.
	*/
	public Object[] elements() {
		// count non marks
		int count = 0;
		for (int i = 0; i < top; i++) 
		/** invariant 0 <= i && i <= top **/
		/** variant top-i **/
			if (!(stack[i] instanceof jass.util.StackMark)) count++;
		// copy all non mark to os
		Object[] os = new Object[count];
		int j = 0;
		for (int i = 0; i < top; i++) 
		/** invariant 0 <= i && i <= top **/
		/** variant top-i **/
			if (!(stack[i] instanceof jass.util.StackMark)) os[j++] = stack[i];
		return os;
		/** ensure change{} **/
	}

	public void clear() {
		top = 0;
		/** ensure empty() **/
	}

	public String toString() {
		StringBuffer sb = new StringBuffer("Stack: BOTTOM [");
		boolean first = true;
		for (int i = 0; i < top; i++) {
			if (!first) sb.append(","); else first = false;
			sb.append(stack[i].toString());
		}
		sb.append("] TOP");
		return sb.toString();
	}

	public Object clone() throws CloneNotSupportedException {
		Stack s = new Stack(max);
		Object[] os = s.elements();
		for (int i = 0; i < os.length; i++) {
			s.push(os[i]);
		}
		return s;
	}


  /** invariant top >= 0 && top <= max; max > 0 **/
}