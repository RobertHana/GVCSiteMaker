package net.global_village.testeomodelbase;

import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;

import net.global_village.foundation.*;


/**
 * Work around for bug (?) in EOF where validateValueForKey does not call into the class description.  Either inherit from this class, or implement this method in your base class for EO objects:
 * <pre>
 *     public Object validateValueForKey( Object value, String key)
 * {
 *     return classDescription().validateValueForKey(value, key);
 * }
 * </pre>

 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 3$
 */
public class EOGenericRecord extends com.webobjects.eocontrol.EOGenericRecord
{

    /**
     * Checks to ensure that the passed classDescription class is not equal to EOEntityClassDescription class.  If it is not, it returns an instance of the correct class for the passed entity name.  
     * CHECKME not sure if we need this any more.
     *
     * @param classDescription the EOClassDescription that is being used to create this EO object.  Its class should not be EOEntityClassDescription class, but due to a bug in EOF it may be.
     * @param entityName the name of the entity for this object.
     */
    public static EOClassDescription classDescriptionForEntityNamed(EOClassDescription classDescription, String entityName)
    {
        /** require
            [classDescription_not_null] classDescription != null;
            [entityName_not_null] entityName != null; **/
        
        boolean isBadClassDescription = (classDescription.getClass().equals(EOEntityClassDescription.class));

        EOClassDescription theClassDescription = isBadClassDescription ? EOClassDescription.classDescriptionForEntityName(entityName) :  classDescription;

        JassAdditions.post("EOGenericRecord", "classDescriptionForEntityNamed", ( ! theClassDescription.getClass().equals(EOEntityClassDescription.class)));
        return theClassDescription;

        /** ensure
            [result_not_null] Result != null;  **/
    }


    
    /**
     * Standard EOF constuctor.
     */
    public EOGenericRecord()
    {
        super();
    }



}
