// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.admin.unit;
import com.gvcsitemaker.admin.user.*;
import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.appserver.*;
import com.webobjects.appserver.*;


/**
 * Component to display and edit the administrators of an OrgUnit.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class UnitAdminList extends WOComponent
{

    protected OrgUnit currentUnit;
    protected User userItem;


    /*
     * Designated constructor. 
     */
    public UnitAdminList(WOContext aContext)
    {
        super(aContext);
    }

    

    /**
     * Returns the page to administer <code>etUserItem()</code>.
     * 
     * @return ManageUserPage
     */
    public ManageUserPage manageUser() 
    {
        /** require [has_user] getUserItem() != null;   **/ 
        ManageUserPage nextPage = (ManageUserPage)pageWithName("ManageUserPage");
        nextPage.setUser(getUserItem());
            
        return nextPage;
        /** ensure [valid_next_page] (Result != null)  && (Result != context().page());    **/
    }
    
    

    /**
     * Removes this user from the admins of this unit and redisplays page.
     *  
     * @return the displayed page.
     */
    public WOComponent removeAction() 
    {
        /** require [has_user] getUserItem() != null;   
                    [user_can_be_removed] canRemoveAdmin();       **/ 
        session().defaultEditingContext().revert();
        getCurrentUnit().removeAdmin( getUserItem(),
                                      session().defaultEditingContext());
        return context().page();

        /** ensure [valid_next_page] Result == context().page();    
                   [was_removed] ! getCurrentUnit().admins().containsObject(getUserItem());  **/
    }



    /**
     * Returns <code>true</code> if the logged in user is <b>not</b> a system administrator.  This 
     * is used to restrict access to functionality.
     *  
     * @return <code>true</code> if the logged in user is <b>not</b> a system administrator
     */
    public boolean isntSystemAdmin()
    {
        return ! ((SMSession)session()).currentUser().isSystemAdmin();
    }



    /**
     * Returns <code>true</code> if <code>getUserItem()</code> can be removed as an admin for
     * <code>getCurrentUnit()</code>.  Only the last system admin in the root unit can't be removed.
     * The currently logged in user can only remove themselves if they are a system admin removing 
     * themself from a non-root unit.  Other removals will trigger a permissions error.   
     *  
     * @return <code>true</code> if <code>getUserItem()</code> can be removed as an admin for
     * <code>getCurrentUnit()</code>
     */
    public boolean canRemoveAdmin()
    {
        boolean isRemovingLoggedInUser = getUserItem().equals(((SMSession)session()).currentUser());
        boolean isLoggedInUserSystemAdmin = ! isntSystemAdmin();
        boolean isRootUnit = getCurrentUnit().isRootUnit();
        return  ( ( ! isRemovingLoggedInUser) || (isLoggedInUserSystemAdmin && ! isRootUnit)) &&
               ! (getUserItem().isLastSystemAdmin() && isRootUnit);
    }



    /* ********** Generic setters and getters ************** */
    public OrgUnit getCurrentUnit() {
        return currentUnit;
    }
    public void setCurrentUnit(OrgUnit newCurrentUnit) {
        currentUnit = newCurrentUnit;
    }

    public User getUserItem() {
        return userItem;
    }
    public void setUserItem(User newUserItem) {
        userItem = newUserItem;
    }

}
