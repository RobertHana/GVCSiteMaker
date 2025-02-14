package jass.reflect;

/**
 * Representation of a local variable.
 *
 * @author
 * @version %I%, %G%
 */
public class LocalVariable extends Entity {

    /**
     * Constructor declaration
     *
     *
     * @see
     */
    public LocalVariable() {}

    /**
     * Constructor declaration
     *
     *
     * @param name
     *
     * @see
     */
    public LocalVariable(String name) {
	this.name = name;
    }

    /**
     * Set the modifiers for thois variable to final (b = true) or
     * nothoing 
     *
     * @param b determine whether this variable is final
     *
     * @see jass.reflect.Modifier.FINAL
     * @see jass.reflect.Modifier.NOTHING
     */
    public void setFinal(boolean b) {
	if (b) {
	    modifiers = jass.reflect.Modifier.FINAL;
	} else {
	    modifiers = jass.reflect.Modifier.NOTHING;
	}
    } 

    /**
     * Is the other object equal to this local variable? Local
     * variables are equal if they have the same name.
     *
     * @param o the object, that is possibly equal
     *
     * @return true if the other object is an instance of
     *         LocalVariable and is labeled with the same name.
     */
    public boolean equals(Object o) {
	if (o instanceof jass.reflect.LocalVariable) {
	    return ((Entity) o).getName().equals(name) /* && ((Entity)o).container == container */;
	} else {
	    return false;
	}
    } 

    /**
     * Return a String representation. The string will be build by
     * "[Local] " + type + " " + name.
     */
    public String toString() {
	return "[Local] " + type + " " + name;
    } 

    /**
     * Produce Java-code for this local variable.
     */
    public String toJava() {
	StringBuffer javaCode = 
	    new StringBuffer(Modifier.toJava(modifiers));
	javaCode.append(" ").append(type.getName());
	javaCode.append(" ").append(name);
	return javaCode.toString();
    } 

}

