// Copyright (c) 2001-2006, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA

package com.gvcsitemaker.core;

import com.gvcsitemaker.core.appserver.*;
import com.gvcsitemaker.core.utility.*;

import com.webobjects.eocontrol.*;

import net.global_village.woextensions.*;


/**
 * Group sub-class representing users authenticated via the Tools Interoperatiblity interface.
 */
public class RemoteParticipantGroup extends _RemoteParticipantGroup
{
    public static final String GROUP_TYPE = "Remote Participant";



    /**
     * Factory method to create new instances of RemoteParticipantGroup.  Use this rather than calling the constructor
     * directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of Group or a subclass.
     */
    public static RemoteParticipantGroup newGroup()
    {
        return (RemoteParticipantGroup) SMEOUtils.newInstanceOf("RemoteParticipantGroup");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Overridden to set type() and name().
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);

        setType(GROUP_TYPE);
        setName("Remote Participants");

        /** ensure  [type_not_null] type() != null; [name_not_null] name() != null;  **/
    }



    /**
     * Returns <code>true</code> if the current session is a remote participant session
     * and the authenticated remote participation group is this group.
     *
     * @param aUser the <code>User</code> to test for membership in this Group
     * @return <code>true</code> if <code>aUser</code> is a member of this Group
     */
    public boolean isMember(User aUser)
    {
        /** require [same_ec] editingContext() == aUser.editingContext();  **/
        // Need session access to check this
        SMSession session = (SMSession)WOSession.session();
        boolean isSiteOwner = aUser.configurableWebsites().contains(parentWebsite());

        DebugOut.println(40, "Authenticated Remote Participant Group authenticatedRemoteParticipantGroup: " + session.authenticatedRemoteParticipantGroup());
        DebugOut.println(40, "This Remote Participant Group: " + this);
        DebugOut.println(40, "isSiteOwner: " + isSiteOwner);
        DebugOut.println(40, "isRemoteParticipantSession: " + session.isRemoteParticipantSession());

        return ((isSiteOwner) ||
        	   (session.currentUser().equals(aUser) &&  // make sure we're talking about the user that is authenticated in this session
        	    session.isRemoteParticipantSession() &&
                session.authenticatedRemoteParticipantGroup() != null &&
                session.authenticatedRemoteParticipantGroup().globalID().equals(globalID())));
    }



    /**
     * Returns <code>false</code> as Remote Participant groups are not user deletable.
     *
     * @return <code>false</code>
     */
    public boolean canBeDeleted()
    {
        return false;
    }



    /**
     * Returns <code>true</code> as the Configure group is always in use
     *
     * @return <code>true</code> as the Configure group is always in use
     */
    public boolean isInUse()
    {
        return true;
    }



}
