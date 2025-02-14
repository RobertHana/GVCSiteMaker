package net.global_village.eofextensions;

import com.webobjects.eoaccess.*;
import com.webobjects.foundation.*;


/**
 * <p>EOPrototypeSelector sets the prototypes in the <code>EOJDBCPrototypes</code> entity at runtime.  
 * It does this for a single database, taking the prototype configuration from the entity named 
 * <code>dbPrototypesName()</code> which must be in the same model as the <code>EOJDBCPrototypes</code>
 * entity.  This same model restriction is to avoid references to entities in models which have not 
 * yet been loaded, as there is no  control over the order in which models get loaded.</p>  
 * 
 * <p>There is no requirement that each prototype in <code>EOJDBCPrototypes</code> be updated by 
 * the prototype configuration from the entity named <code>dbPrototypesName()</code>.  Prototypes to be
 * updated are matched by name, prototypes are skipped if there is no corresponding attribute in 
 * the entity named <code>dbPrototypesName()</code>.  This allows <code>EOJDBCPrototypes</code> to hold
 * prototypes split across multiple database types.</p> 
 * 
 * <p>If you need to connect to more than one type of database you will need to 
 * segregate the prototype names in each database (according to domain <b>not</b>
 * database) and use one <code>EOPrototypeSelector</code> instance to handle each.</p>
 * 
 * <p><code>EOPrototypeSelector</code> can be installed as a notification handler catching the 
 * <code>EOModelGroup.ModelAddedNotification</code>.  It can also be used when enumerating over
 * <code>EOModelGroup.defaultGroup().models()</code> or other similar situations.</p>
 * 
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class EOPrototypeSelector
{
    protected String dbPrototypesName;



    /**
     * Constructs a new <code>EOPrototypeSelector</code> to update matching prototypes in 
     * <code>EOJDBCPrototypes</code> to match those for found in 
     * <code>EO&lt;databaseType&gt;Prototypes</code>.
     * 
     * @param databaseType the type or name of the database to take prototype configuration from
     */
    public EOPrototypeSelector(String databaseType)
    {
        super();
        /** require [valid_databaseType] databaseType != null;  **/
        
        dbPrototypesName = "EO" + databaseType + "Prototypes";
    }

   
    
    /**
     * Registers this object to received the <code>EOModelGroup.ModelAddedNotification</code> and call
     * <code>handleModelAddedNotification()</code> when this notification is received.
     */
    public void installModelAddedListener()
    {
        NSSelector modelAddedSelector = 
            new NSSelector("handleModelAddedNotification", new Class[] { NSNotification.class } );
        NSNotificationCenter.defaultCenter().
            addObserver(this, modelAddedSelector, EOModelGroup.ModelAddedNotification, null);
    }
    
    
    
    /**
     * Handles the <code>EOModelGroup.ModelAddedNotification</code>.  The object in the 
     * <code>aNotification</code> parameter is the model being added.  The model is extracted from 
     * this notification and passed to <code>updatePrototypes()</code>.
     *
     * @param aNotification notification of which EOModel is being added.
     */
    public void handleModelAddedNotification(NSNotification aNotification)
    {
        /** require [valid_param] aNotification != null; 
                    [is_model_added_notification] 
                        aNotification.name().equals(EOModelGroup.ModelAddedNotification);  **/

        EOModel theModel = (EOModel) aNotification.object();
        updatePrototypes(theModel);
    }


    
    /**
     * Updates the <code>EOJDBCPrototypes</code> entity in <code>aModel</code> if it is present,
     * otherwise it does nothing.  
     * 
     * @param aModel the EOModel to check for the <code>EOJDBCPrototypes</code> entity
     * @exception IllegalStateException if <code>EOJDBCPrototypes</code> is found but no entity named
     * <code>dbPrototypesName()</code> is found
     */
    public void updatePrototypes(EOModel aModel)
    {
        /** require [aModel_valid] aModel != null; **/
        
        EOEntity jdbcPrototypesEntity = aModel.entityNamed("EOJDBCPrototypes");
        EOEntity databasePrototypesEntity = aModel.entityNamed(dbPrototypesEntityName());
        
        // Validate model
        if (jdbcPrototypesEntity != null)
        {
            if (databasePrototypesEntity == null)
            {
                throw new IllegalStateException("Found EOJDBCPrototypes in model " +
                    aModel.name() + " but did not find database entity named " + 
                    dbPrototypesEntityName());
            }
            else
            {
                if (NSLog.debugLoggingAllowedForLevel(NSLog.DebugLevelDetailed))
                {
                    NSLog.debug.appendln("Updating EOJDBCPrototypes with configuration from " + 
                        dbPrototypesName + " in model " + aModel.name());
                }
                
                for (int i = 0; i < jdbcPrototypesEntity.attributes().count(); i++)
                {
                    EOAttribute jdbcPrototype = 
                        (EOAttribute)jdbcPrototypesEntity.attributes().objectAtIndex(i);
                    String prototypesName = (String)jdbcPrototype.name();
                    EOAttribute dbPrototype = 
                        (EOAttribute)databasePrototypesEntity.attributeNamed(prototypesName);
                    if (dbPrototype != null)
                    {
                        jdbcPrototype.setDefinition(dbPrototype.definition());
                        jdbcPrototype.setExternalType(dbPrototype.externalType()); 
                        jdbcPrototype.setPrecision(dbPrototype.precision()); 
                        jdbcPrototype.setReadFormat(dbPrototype.readFormat()); 
                        jdbcPrototype.setScale(dbPrototype.scale()); 
                        jdbcPrototype.setUserInfo(dbPrototype.userInfo()); 
                        jdbcPrototype.setValueType(dbPrototype.valueType()); 
                        jdbcPrototype.setWidth(dbPrototype.width()); 
                        jdbcPrototype.setWriteFormat(dbPrototype.writeFormat());
                    }
                }
            }
        }
        
        /** ensure [prototpes_updated] /# All the prototypes in EOJDBCPrototypes
         which have matching prototypes in the database specific prototypes have
         been updated.#/ true; **/
    }
    
    
    
    /**
     * Returns the name of the entity (formed as <code>EO&lt;databaseType&gt;Prototypes</code> ) that 
     * the <code>EOJDBCPrototypes</code> entity should be updated from. 
     * 
     * @return the name of the entity that the <code>EOJDBCPrototypes</code> entity should be 
     * updated from
     */
    public String dbPrototypesEntityName()
    {
        return dbPrototypesName;
    }
    
    
    /** invariant [has_dbPrototypesEntityName] dbPrototypesName != null;  **/
    
}
