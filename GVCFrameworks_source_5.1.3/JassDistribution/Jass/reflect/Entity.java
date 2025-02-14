/*--- formatted by Jindent 2.1, (www.c-lab.de/~jindent) ---*/

package jass.reflect;

import java.io.*;

/**
 * An entity represent one of the essential language contructs in
 * Java: field, method, class, formal parameter and local variable.
 * It has a name, a type and modifiers. Entities can be compared for equality
 * with the method equals(jass.reflect.entity) which uses the getIdString() method.
 */
public class Entity implements Serializable {
    protected String name;
    protected Class  type;
    protected int    modifiers;
    protected Entity container;

    /**
     * Constructor declaration
     *
     *
     * @param name
     *
     * @see
     */
    public Entity(String name) {
	this.name = name;
    }

    /**
     * Constructor declaration
     *
     *
     * @see
     */
    public Entity() {}

    /**
     * Method declaration
     *
     *
     * @param name
     *
     * @see
     */
    public void setName(String name) {
	this.name = name;
    } 

    /**
     * Method declaration
     *
     *
     * @param type
     *
     * @see
     */
    public void setType(Class type) {
	this.type = type;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Class getType() {
	return type;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String getName() {
	return name;
    } 

    /**
     * Method declaration
     *
     *
     * @param m
     *
     * @see
     */
    public void setModifier(int m) {
	this.modifiers = m;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public int getModifier() {
	return modifiers;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean isPrimitive() {
	return type.isPrimitive();
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean isReference() {
	return !isPrimitive();
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean isVoid() {
	return type.getName().equals("void");
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean isFinal() {
	return Modifier.isFinal(modifiers);
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean isPrivate() {
	return Modifier.isPrivate(modifiers);
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean isPublic() {
	return Modifier.isPublic(modifiers);
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean isPackage() {
	return Modifier.isPackage(modifiers);
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean isStatic() {
	return Modifier.isStatic(modifiers);
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public boolean isProtected() {
	return Modifier.isProtected(modifiers);
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String getIdString() {
	return name;
    } 

    /**
     * Method declaration
     *
     *
     * @param e
     *
     * @see
     */
    public void setContainer(Entity e) {
	container = e;
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public Entity getContainer() {
	return container;
    } 

    /**
     * Method declaration
     *
     *
     * @param e
     *
     * @return
     *
     * @see
     */
    public boolean equals(Entity e) {
	if (container != null &&!container.equals(e.getContainer())) {
	    return false;
	} 

	return getIdString().equals(e.getIdString());
    } 

    /**
     * Method declaration
     *
     *
     * @return
     *
     * @see
     */
    public String toString() {
	StringBuffer sb = new StringBuffer();

	sb.append(jass.reflect.Modifier.toString(modifiers));

	if (type != this) {
	    sb.append(" " + type.getName());
	} 

	sb.append(" " + name);

	return sb.toString();
    } 

}



/*--- formatting done in "Sun Java Convention" style on 03-01-2001 ---*/

