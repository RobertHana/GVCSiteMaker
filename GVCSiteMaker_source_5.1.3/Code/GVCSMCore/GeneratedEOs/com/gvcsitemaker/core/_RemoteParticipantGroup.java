
package com.gvcsitemaker.core;


import java.util.*;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 *
 * Created by Velocity Generator.<br>
 * DO NOT EDIT.  Make changes to RemoteParticipantGroup.java instead.
 *<p>
 * Copyright (c) ${copyrightYear} ${copyrightBy}   All rights reserved.
 *</p> 
 * @version $Revision$
 */  
public abstract class _RemoteParticipantGroup extends com.gvcsitemaker.core.WebsiteGroup
{


    public static final String REMOTEPARTICIPANTGROUP_ENTITY_NAME = "RemoteParticipantGroup";

    public static final String OIDSECTIONSTYLE = "oidSectionStyle";
    public static final String SECRET = "secret";

    public static final String SECTIONSTYLE = "sectionStyle";


     public static EOEntity entity(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;   **/
        return EOUtilities.entityNamed(editingContext, "RemoteParticipantGroup");
        /** ensure [valid_result] Result != null;  **/
    }
   

    public RemoteParticipantGroup localInstance(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null;  
                    [saved_eo] ! globalID().isTemporary();
         **/
        return (RemoteParticipantGroup)EOUtilities.localInstanceOfObject(editingContext, this);
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     *
     * @return Number     
     */
    public Number oidSectionStyle() {
        return (Number) storedValueForKey("oidSectionStyle");
    }



    public void setOidSectionStyle(Number value) 
    {
        takeStoredValueForKey(value, "oidSectionStyle");
    }
    
    

    /**
     *
     * @return String     
     */
    public String secret() {
        return (String) storedValueForKey("secret");
    }



    public void setSecret(String value) 
    {
        takeStoredValueForKey(value, "secret");
    }
    
    

    /**
     *
     * @return com.gvcsitemaker.core.SectionStyle
     
     */
    public com.gvcsitemaker.core.SectionStyle sectionStyle() 
    {
        return (com.gvcsitemaker.core.SectionStyle)storedValueForKey("sectionStyle");
    }



    public void setSectionStyle(com.gvcsitemaker.core.SectionStyle value) 
    {
        /** require [same_ec] value == null || editingContext() == value.editingContext();  **/
        takeStoredValueForKey(value, "sectionStyle");
    }
  
  
  

	/**
	 * @param editingContext EC to return objects in
	 * @return all instances of RemoteParticipantGroups in editingContext
	 */
    public static NSArray fetchAllRemoteParticipantGroups(EOEditingContext editingContext) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _RemoteParticipantGroup.fetchAllRemoteParticipantGroups(editingContext, null);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of RemoteParticipantGroups, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchAllRemoteParticipantGroups(EOEditingContext editingContext, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        return _RemoteParticipantGroup.fetchRemoteParticipantGroups(editingContext, null, sortOrderings);
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @param sortOrderings list of EOSortOrdering giving the order
	 * @return all instances of RemoteParticipantGroups matching qualifier, ordered by sortOrderings in editingContext
	 */
    public static NSArray fetchRemoteParticipantGroups(EOEditingContext editingContext, EOQualifier qualifier, NSArray sortOrderings) 
    {
        /** require [valid_ec] editingContext != null; **/
        EOFetchSpecification fetchSpec = new EOFetchSpecification(REMOTEPARTICIPANTGROUP_ENTITY_NAME, qualifier, sortOrderings);
        fetchSpec.setIsDeep(true);
        fetchSpec.setRefreshesRefetchedObjects(true);
        return editingContext.objectsWithFetchSpecification(fetchSpec);
        /** ensure [valid_result] Result != null;  **/
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return all instances of RemoteParticipantGroups where keyName.equals(value), ordered by sortOrderings in editingContext
     */
    public static RemoteParticipantGroup fetchRemoteParticipantGroup(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _RemoteParticipantGroup.fetchRemoteParticipantGroup(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
        /** ensure [valid_result] Result != null;  **/
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of RemoteParticipantGroups matching qualifier, null if no objects match
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
    public static RemoteParticipantGroup fetchRemoteParticipantGroup(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        NSArray eoObjects = _RemoteParticipantGroup.fetchRemoteParticipantGroups(editingContext, qualifier, null);
        RemoteParticipantGroup eoObject;
        int count = eoObjects.count();
        if (count == 0) 
        {
            eoObject = null;
        }
        else if (count == 1) 
        {
            eoObject = (RemoteParticipantGroup)eoObjects.objectAtIndex(0);
        }
        else {
            throw new EOUtilities.MoreThanOneException("There was more than one RemoteParticipantGroup that matched the qualifier '" + qualifier + "'.");
        }
    
        return eoObject;
    }



	/** 
	 * @param editingContext EC to return objects in
	 * @param keyName key for EOKeyValueQualifier
	 * @param value value for EOKeyValueQualifier
	 * @return single instance of RemoteParticipantGroups where keyName.equals(value), ordered by sortOrderings in editingContext
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
     */
    public static RemoteParticipantGroup fetchRequiredRemoteParticipantGroup(EOEditingContext editingContext, String keyName, Object value) 
    {
        /** require [valid_ec] editingContext != null; 
                    [valid_keyName] keyName != null;   **/
        return _RemoteParticipantGroup.fetchRequiredRemoteParticipantGroup(editingContext, new EOKeyValueQualifier(keyName, EOQualifier.QualifierOperatorEqual, value));
    }



	/**
	 * @param editingContext EC to return objects in
	 * @param qualifier EOQualfier selecting the objects to return
	 * @return single instance of RemoteParticipantGroups matching qualifier
	 * @throws EOObjectNotAvailableException if no objects matchr
	 * @throws EOUtilities.MoreThanOneException if multiple objects matched qualifier
	 */
	public static RemoteParticipantGroup fetchRequiredRemoteParticipantGroup(EOEditingContext editingContext, EOQualifier qualifier) 
    {
        /** require [valid_ec] editingContext != null; **/
        RemoteParticipantGroup eoObject = _RemoteParticipantGroup.fetchRemoteParticipantGroup(editingContext, qualifier);
        if (eoObject == null) 
        {
            throw new EOObjectNotAvailableException("There was no RemoteParticipantGroup that matched the qualifier '" + qualifier + "'.");
        }
        return eoObject;
        /** ensure [valid_result] Result != null;  **/
    }

}
