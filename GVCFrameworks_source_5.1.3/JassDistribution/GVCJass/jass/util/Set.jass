package jass.util;

public class Set implements Cloneable {

    protected Object[] set = new Object[0];
    
    public Set() {}
	
    public Set(Object[] elems) {
	set = elems;
    }
	
    public void addElement(Object o) {
	/** require o != null; **/
	if (!contains(o)) {
	    Object[] help = new Object[set.length+1];
	    System.arraycopy(set,0,help,0,set.length);
	    help[set.length] = o;	
	    // explicite garbage collect
	    set = null;
	    set = help;
	}
	/** ensure changeonly {set}; 
	           [last_inserted] contains(o); 
		   [valid_length] set.length == 
		                  Old.set.length+(Old.contains(o)?0:1); 
	**/
    }
	
    public boolean contains(Object o) {
	/** require o != null; **/
	for (int i = 0; i < set.length; i++)
	    /** invariant 0 <= i && i <= set.length;**/
	    /** variant set.length-i **/
	    if (set[i].equals(o)) return true;
	return false;
	/** ensure changeonly {}; **/
    }
	
    public Object get(Object o) {
	/** require o != null; **/
	int i = 0;
	while (i < set.length)			
	    /** invariant 0 <= i && i <= set.length; **/
	    /** variant set.length-i **/
	    if (set[i++].equals(o)) return set[--i];
	return null;
	/** ensure changeonly {}; **/
    }
	
    public Object[] elements() {
	return set;
	/** ensure changeonly{}; **/
    }
	
    public void union(Set s) {
	/** require s != null; **/
	Object[] s_objs = s.elements();
	for (int i = 0; i < s_objs.length; i++)
	    /** invariant 0 <= i && i <= s_objs.length; **/
	    /** variant s_objs.length-i **/
	    addElement(s_objs[i]);
	/** ensure changeonly {set}; **/
    }

    public Set intersect(Set s) {
	Set sret = new Set();
	for (int i = 0; i < set.length; i++) {
	    if (s.contains(set[i])) sret.addElement(set[i]);
	}
	return sret;
    }

    public void dump (java.io.PrintWriter pw, String indent) {
	pw.println(indent+"Set contains:");
	for (int i = 0; i < set.length; i++) {
	    //pw.println(indent+"  "+set[i]);
	}
    }
	
    public boolean empty() {
	return set.length == 0;
	/** ensure changeonly{}; **/
    }
	
    public String toString() {	
	StringBuffer sb = new StringBuffer("{");
	boolean first = true;
	for (int i = 0; i < set.length; i++) {
	    if (!first) sb.append(","); else first = false;
	    sb.append(set[i].toString());
	}
	sb.append("}");
	return sb.toString();
    }
	
    public Object clone() {
	Object[] set2 = new Object[set.length];
	System.arraycopy(set,0,set2,0,set.length);
	return new Set(set2);
    }

}
