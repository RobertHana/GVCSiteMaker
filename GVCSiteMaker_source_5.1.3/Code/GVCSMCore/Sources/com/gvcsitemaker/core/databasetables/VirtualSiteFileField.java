/**
 * Implementation of VirtualSiteFileField common to all installations.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $REVISION$
 *
 */
package com.gvcsitemaker.core.databasetables;

import sun.misc.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.support.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;


public class VirtualSiteFileField extends _VirtualSiteFileField
{

    public static final String ColumnTypeName = "SiteFile";


    /**
     * Setup default values.
     *
     * @param ec the editing context in which this is being inserted
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        /** require [valid_param] ec != null; **/

        super.awakeFromInsertion(ec);
        setRestrictingValue(ColumnTypeName);
    }



    /**
     * Returns a copy of this field's value.  As SiteFiles are mutable EOs, a real copy (as a newly created EO) of this field's value is returned.
     *
     * @return a copy of this field's value
     */
    public Object valueCopy()
    {
        return siteFileValue().duplicate();
        /** ensure [valid_result] Result != null;  **/
    }



    /**
     * The generic accessor for fields.  This class returns its SiteFile value.
     *
     * @return this field's value
     */
    public Object value()
    {
        return siteFileValue();
    }



    /**
     * The generic setter for fields.  This class sets its SiteFile value.
     *
     * @param newValue the new value of the field
     */
    public void setValue(Object newValue)
    {
        /** require [valid_type] newValue == null || newValue instanceof SiteFile; **/
        setSiteFileValue((SiteFile)newValue);
    }



    /**
     * For regular table export, files are exported as a URL to access the SiteFile outside the context of a Data Access Section.  Only Website Owners and co-Owners have permission via this mechanism.  This method is used by the table export process to perform additional processing on an exported field value.  For data table package, it returns the binary data of file as string.  
     *
     * @param sender an object that this method can use to further distinguish what should be done with the value
     * @return a URL to access the SiteFile outside the context of a Data Access Section.  
     */
    public Object valueForExport(Object sender)
    {
        Object valueForExport = null;

        if (siteFileValue() != null)
        {   
            if ((sender != null) && (sender instanceof String)  && ((String) sender).equals(SMVirtualTable.DataTablePackageExport))
            {
                BASE64Encoder coder = new BASE64Encoder();
                String siteFileData = coder.encode(siteFileValue().data().bytes(0,siteFileValue().data().length()));

                valueForExport = new NSArray(new Object[] { siteFileValue().uploadedFilename(), 
                        siteFileValue().mimeType(), 
                        siteFileValue().fileSizeUsage(),
                        siteFileData });                     
            }
            else
            {
                valueForExport = SMDataAccessActionURLFactory.exportedFileUrl(row(),
                        column(),
                        siteFileValue().website(),
                        siteFileValue());
            }
        }

        return valueForExport;
    }
    
}

