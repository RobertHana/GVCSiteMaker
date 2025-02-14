package net.global_village.security;

import net.global_village.foundation.JassAdditions;

import com.webobjects.eoaccess.EOUtilities;
import com.webobjects.eocontrol.EOEditingContext;


/**
 * A single privilege to access or perform a specific function in an application.
 * Other privileges may also be associated with this privilege so that having this
 * privilege grants other privileges as well.
 *
 * @author Copyright (c) 2001-2006  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 5$
 */
public class Privilege extends _Privilege
{
    private static final long serialVersionUID = 1609665297518507518L;


    /**
     * Protected method to fetch the <code>Privilege</code> with the indicated name.
     *
     * @param aName the name of the <code>Privilege</code> to find.
     * @param anEditingContext the editing context to use.
     * @return the <code>Privilege</code> requested if it exists
     */
    protected static Privilege _privilege(String aName,
                                          EOEditingContext anEditingContext)
    {
        /** require
        [valid_aName_param] aName != null;
        [valid_anEditingContext_param] anEditingContext != null; **/
        JassAdditions.pre("Privilege", "_privilege", aName.length() > 0);

        Privilege newPrivilege;

        try
        {
            newPrivilege = (Privilege) EOUtilities.objectMatchingKeyAndValue(anEditingContext,
                                                                             "GVCPrivilege",
                                                                             "name",
                                                                             aName);
        }
        catch(Exception e)
        {
            newPrivilege = null;
        }

        return newPrivilege;
    }



    /**
     * Determine if a named <code>Privilege</code> with the given <code>EOEditingContext</code> exists.
     *
     * @param aName name of the <code>Privilege</code> to find
     * @param editingContext editingContext used for obtaining the Privilege
     * @return <code>true</code> if the named <code>privilege</code> exists.
     */
    public static boolean privilegeExists(String aName,
                                          EOEditingContext editingContext)
    {
        /** require
        [valid_aName_param] aName != null;
        [valid_editingContext_param] editingContext != null; **/
        JassAdditions.pre("Privilege", "privilegeExists", aName.length() > 0);

        return _privilege(aName, editingContext) != null;
    }



    /**
     * Method to obtain a named <code>Privilege</code> with the given <code>EOEditingContext</code>.
     *
     * @param aName name of the <code>Privilege</code> to find
     * @param editingContext editingContext used for obtaining the Privilege
     * @return the named privilege if it exists.
     */
    public static Privilege privilege(String aName,
                                      EOEditingContext editingContext)
    {
        /** require
        [valid_aName_param] aName != null;
        [valid_editingContext_param] editingContext != null; **/
        JassAdditions.pre("Privilege", "privilege", aName.length() > 0);

        return Privilege._privilege(aName, editingContext);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Compares two <code>Privileges</code> by name case insensitively
     *
     * @param otherPrivilege the privilege to compared against
     * @return the same result as the String compareTo() method
     */
    public int compareByName(Privilege otherPrivilege)
    {
        /** require [valid_param] otherPrivilege != null; **/

        return name().compareToIgnoreCase(otherPrivilege.name());
    }



    /**
     * Convience method to return the name of the <code>Privilege</code> when description is called.
     *
     * @return a descriptive string of the privilege (its name).
     */
    public String toString()
    {
        /** require
        [valid_name] name() != null;
        [name_is_at_least_one_char_long] name().length() > 0; **/

        return name();

        /** ensure
        [valid_result] Result != null;
        [result_is_at_least_one_char_long] Result.length() > 0; **/
    }



    /**
     * Determines if having this <code>Privilege</code> also grants the other privilege.
     *
     * @param aPrivilege		the privilege used to check if it is included or not.
     * @return <code>true</code> if having this privilege grants access to aPrivilege.
     */
    public boolean includesPrivilege(Privilege aPrivilege)
    {
        /** require [valid_param] aPrivilege != null; **/

        return includedPrivileges().containsObject(aPrivilege);
    }



}
