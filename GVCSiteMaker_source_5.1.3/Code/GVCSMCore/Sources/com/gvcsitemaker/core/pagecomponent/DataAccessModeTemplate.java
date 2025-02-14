/**
 * Implementation of DataAccessModeTemplate common to all installations. //
 * Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann
 * Arbor, MI 48109 USA All rights reserved. // This software is published under
 * the terms of the Educational Community License (ECL) version 1.0, // a copy
 * of which has been included with this distribution in the LICENSE.TXT file.
 * 
 * @version $REVISION$
 */
package com.gvcsitemaker.core.pagecomponent;

import com.gvcsitemaker.core.utility.*;
import com.webobjects.foundation.*;


/*
 * DataAccessModeTemplate holds the custom template (if any) of a
 * DataAccessMode PageComponent.
 */
public class DataAccessModeTemplate extends _DataAccessModeTemplate
{


    /**
     * Factory method to create new instances of DataAccessModeTemplate.  Use this rather than calling the constructor directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of DataAccessModeTemplate or a subclass.
     */
    public static DataAccessModeTemplate newDataAccessModeTemplate()
    {
        return (DataAccessModeTemplate) SMEOUtils.newInstanceOf("DataAccessModeTemplate");

        /** ensure [result_not_null] Result != null; **/
    }



    /**
     * Returns the DataAccessMode this DataAccessModeTemplate is used in.  The modeled relationship is actually a to-many, dataAccessModes() to work around problems in EOF and EOModeler.  If propogate primary key is selected to use the PK from the DataAccessMode as the PK for this object, problems arise when the template is created after the DataAccessMode (attempts to update PK of DataAccessMode).  If the PK propogation is done manually, problems arise when the existing template is deleted and a new one create in the same transaction.  In this situation the insertion of the new template can happen first result in a PK uniqueness violation.  Finally, if the PK for this object is used in DataAccessMode as the destination and the relationship is modeled as a to-one, EOModeler complains as the to-one relationship does not specifiy the PK in the destination entity.  In order to keep everyone happy, the to-one relationship was done like this.
     *
     * @return the DataAccessMode this DataAccessModeTemplate is used in.
     */
    public DataAccessMode dataAccessMode()
    {
        /** require [valid_dataaccessmodes] (dataAccessModes() == null) || (dataAccessModes().count() < 2);  **/
        DataAccessMode dataAccessMode = null;
        if ((dataAccessModes() != null) && (dataAccessModes().count() > 0))
        {
            dataAccessMode = (DataAccessMode) dataAccessModes().lastObject();
        }

        return dataAccessMode;
    }



    /**
     * Validates the HTML template against the bindings for this mode.  See HtmlTemplateUtils.validateTemplateWithBindings(String, String, NSArray)
     *
     * @param newTemplate - the HTML template to validate
     * @exception NSValidation.ValidationException - if the template is not valid for the bindings.
     */
    public Object validateHtml(Object newTemplate) throws NSValidation.ValidationException
    {
        if ((newTemplate != null) && (dataAccessMode() != null))
        {
            try
            {
                HtmlTemplateUtils.validateTemplateWithBindings((String)newTemplate, dataAccessMode().generatedBindings(), NSArray.EmptyArray);
            }
            catch (NSValidation.ValidationException e)
            {
                DebugOut.println(1, "Validation failed for template mode: '" + dataAccessMode().mode() + "', section: '"
                        + dataAccessMode().dataAccessComponent().section().name() + "'");
                throw e;
            }
        }

        return newTemplate;
    }



}
