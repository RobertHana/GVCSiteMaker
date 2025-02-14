package net.global_village.foundation;



/**
 * Represents a Boolean value as an object.  GVCBoolean can translate between the internal Boolean state and a string representation of 'Y' for true and 'N' for false.
 *
 * GVCBoolean was created to support storing boolean values in a database as character data. To create an attribute in EOModeler that is mapped to this class, set these items in the Attribute Inspector:
 * <ul>
 *   <li>External Type: CHARACTER, VARCHAR or equivalent
 *   <li>Internal Data Type: Custom
 *   <li>External Width: 1 (or greateer)
 *   <li>Class: GVCBoolean
 *   <li>Factory Method: booleanWithString:
 *   <li>Conversion Method: stringValue
 *   <li>Init Argument: NSString
 * </ul>
 *
 * To manually set up an EOAttribute, use this code:
 * <code>
 *   myBooleanAttribute.setValueClassName("GVCBoolean");
 *   myBooleanAttribute.setFactoryMethodArgumentType(EOFactoryMethodArgumentIsNSString);
 *   myBooleanAttribute.setValueFactoryMethodName("booleanWithString");
 *   myBooleanAttribute.setAdaptorValueConversionMethodName("stringValue");
 * </code>
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 1$
 */
public class GVCBoolean extends Object implements Cloneable
{
    /**
     * Values returned in the <a href="#stringValue()">stringValue()</a> method.
     */
    public static final String trueValueString = "Y";
    public static final String falseValueString = "N";

    /**
     * Values to be localized and returned in the <a href="#toString()">toString()</a> method.
     */
    public static final String trueValueDisplayString = "Yes";
    public static final String falseValueDisplayString = "No";

    /**
     * The value of the boolean.
     */
    protected boolean internalBooleanValue;


    /**
     * Factory method returning a <code>GVCBoolean</code> instance representing the <code>true</code> state.
     *
     * @return a <code>GVCBoolean</code> instance representing the <code>true</code> state
     */
    public static GVCBoolean trueBoolean()
    {
        return new GVCBoolean(true);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Factory method returning a <code>GVCBoolean</code> instance representing the <code>false</code> state.
     *
     * @return a <code>GVCBoolean</code> instance representing the <code>false</code> state
     */
    public static GVCBoolean falseBoolean()
    {
        return new GVCBoolean(false);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Factory method returning a <code>GVCBoolean</code> instance representing the <code>true</code> state.
     *
     * @return a <code>GVCBoolean</code> instance representing the <code>true</code> state
     */
    public static GVCBoolean yes()
    {
        return trueBoolean();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Factory method returning a <code>GVCBoolean</code> instance representing the <code>false</code> state.
     *
     * @return a <code>GVCBoolean</code> instance representing the <code>false</code> state
     */
    public static GVCBoolean no()
    {
        return falseBoolean();

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Factory method returning a <code>GVCBoolean</code> instance with a state equivalent to <code>booleanValue</code>.
     *
     * @param booleanValue the value of the <code>boolean</code>'s state
     * @return a <code>GVCBoolean</code> instance representing the state of <code>booleanValue</code>
     */
    public static GVCBoolean booleanWithBoolean(boolean booleanValue)
    {
        return new GVCBoolean(booleanValue);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Factory method returning a <code>GVCBoolean</code> instance that is <code>true</code> if <code>stringValue</code> is 'Y' or <code>false</code> if <code>stringValue</code> is 'N'.
     *
     * @param stringValue 'Y' for <code>true</code>, 'N' for <code>false</code>
     * @return a <code>GVCBoolean</code> instance representing the state of <code>booleanValue</code>
     */
    public static GVCBoolean booleanWithString(String stringValue)
    {
        JassAdditions.pre("Boolean", "booleanWithString", (stringValue.equals(trueValueString) || stringValue.equals(falseValueString)));

        GVCBoolean newBoolean;

        if (stringValue.equals(trueValueString))
        {
            newBoolean = trueBoolean();
        }
        else
        {
            newBoolean = falseBoolean();
        }

        return newBoolean;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Factory method for a custom attribute in EOModeler.  Has the same semantics as booleanWithString() (which it uses to implement this method).
     *
     * @param stringValue 'Y' for <code>true</code>, 'N' for <code>false</code>
     * @return a <code>GVCBoolean</code> instance representing the state of <code>booleanValue</code>
     */
    public static GVCBoolean objectWithArchiveData(String stringValue)
    {
        JassAdditions.pre("Boolean", "objectWithArchiveData", (stringValue.equals(trueValueString) || stringValue.equals(falseValueString)));

        return booleanWithString(stringValue);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Designated initializer.  Initializes the <code>GVCBoolean</code> with a state equivalent to <code>booleanValue</code>.
     *
     * @param booleanValue the value of the <code>boolean</code>'s state
     */
    public GVCBoolean(boolean booleanValue)
    {
        super();
        internalBooleanValue = booleanValue;

        /** ensure [internal_boolean_set] internalBooleanValue == booleanValue; **/
    }



    /**
     * Returns true if the Boolean state is true.
     *
     * @return <code>true</code> if this <code>GVCBoolean</code> represents <code>true</code>, <code>false</code> if it represents <code>false</code>
     */
    public boolean isTrue()
    {
        return internalBooleanValue;
    }



    /**
     * Returns false if the Boolean state is true.
     *
     * @return <code>false</code> if this <code>GVCBoolean</code> represents <code>true</code>, <code>true</code> if it represents <code>false</code>
     */
    public boolean isFalse()
    {
        return ( ! internalBooleanValue);
    }



    /**
     * Returns the value of the <code>boolean</code>.
     *
     * @return <code>true</code> if this <code>GVCBoolean</code> represents <code>true</code>, <code>false</code> if it represents <code>false</code>
     */
    public boolean booleanValue()
    {
        return internalBooleanValue;
    }



    /**
     * Sets the value of the <code>boolean</code>.
     *
     * @param newValue <code>true</code> if this <code>GVCBoolean</code> is <code>true</code>, <code>false</code> if it is <code>false</code>
     */
    public void setBooleanValue(boolean newValue)
    {
        internalBooleanValue = newValue;
    }



    /**
     * Returns 'Y' if the state is true, or 'N' if the state is false.
     *
     * @return 'Y' if <code>true</code>, 'N' if <code>false</code>
     */
    public String stringValue()
    {
        return internalBooleanValue ? trueValueString : falseValueString;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Accepts 'Y' if the new state should be true, or 'N' if the state should be false.
     *
     * @param stringValue 'Y' if <code>true</code>, 'N' if <code>false</code>
     */
    public void setStringValue(String stringValue)
    {
        /** require
        [valid_param] stringValue != null;
        [is_valid_string] stringValue.equals(trueValueString) || stringValue.equals(falseValueString); **/

        internalBooleanValue = stringValue.equals(trueValueString);
    }



    /**
     * Returns localized string for "Yes" if the state is <code>true</code>, or for "No" if the state is <code>false</code>.
     *
     * @return localized version of "Yes" if <code>true</code>, "No" if <code>false</code>
     */
    public String toString()
    {
        String theDescription;

        if (internalBooleanValue)
        {
            theDescription = Localization.localizedStringForString(this, trueValueDisplayString);
        }
        else
        {
            theDescription = Localization.localizedStringForString(this, falseValueDisplayString);
        }

        return theDescription;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Override equals so it does not do instance equality.  (Does value equality instead.)
     *
     * @param otherValue the value to check equality against
     * @return <code>true</code> if the two <code>GVCBoolean</code>s are equal, <code>false</code> otherwise
     */
    public boolean equals(Object otherValue)
    {
        /** require [valid_param] otherValue != null; **/

        boolean equals;

        if (getClass().isInstance(otherValue))
        {
            equals = (booleanValue() == ((GVCBoolean)otherValue).booleanValue());
        }
        else
        {
            equals = false;
        }

        return equals;

        /** ensure [definition] /# Return true iff this equals <code>otherValue</code> #/ true; **/
    }



    /**
     * Compares the localized string values.  Implement compare so that in-memory sortings have the same order as database orderings.
     *
     * @param otherBoolean the value to compare against
     * @return less than 0 if the localized string of <code>otherBoolean</code> is greater (alphanumerically) than the localized string of this, greater than 0 if <code>otherBoolean</code>'s is smaller, 0 if they are equal
     */
    public int compareTo(GVCBoolean otherBoolean)
    {
        /** require [valid_param] otherBoolean != null; **/

        return stringValue().compareTo(otherBoolean.stringValue());
    }



    /**
     * Returns a boolean that is the negated value of this object.
     *
     * @return  a boolean that is the negated value of this object
     */
    public GVCBoolean negate()
    {
        return new GVCBoolean( ! isTrue());
    }



    /**
     * Implemented so that EOF can do copying.
     *
     * @return a copy of this
     */
    public Object clone()
    {
        Object clone = null;
        try
        {
            clone = super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            // never happens, since we support it.  Throw an Error
            throw new Error("CloneNotSupportedException was thrown, but we do support cloning!");
        }
        return clone;

        /** ensure [valid_result] Result != null; Result.equals(this); **/
    }



}
