/**
 * Implementation of VirtualSiteFileColumn common to all installations.
 *
// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision$
 */
package com.gvcsitemaker.core.databasetables;

import java.io.*;

import sun.misc.*;

import com.gvcsitemaker.core.*;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


public class VirtualSiteFileColumn extends _VirtualUserColumn
{
    static public final String VirtualSiteFileColumnRestrictingValue = "VirtualSiteFileColumn";


    /**
     * Setup default values.
     *
     * @param ec the editing context in which this is being inserted
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        /** require [valid_param] ec != null; **/

        super.awakeFromInsertion(ec);
        setRestrictingValue(VirtualSiteFileColumnRestrictingValue);
    }



    /**
     * Returns the name of the field attribute that the import process should use to set the field data.
     *
     * @return the name of the field attribute that the import process should use
     */
    public String importAttributeName()
    {
        return "fileFkey";
        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Used by the table import process to perform additional processing on an imported field value.  Decodes XML data
     * and creates a new SiteFile related to this field.<p>
     *
     * Note that no coercion is done, so <code>value</code> must be the correct type.
     *
     * @param sender an object that this method can use to further distinguish what should be done with the value
     * @param value the value we got during import, possibly formatted by a formatter
     * @return the value to use during table import
     * @throws Exception
     */
    public Object valueForImportedValue(Object sender, Object value)
    {
        /** require [valid_value_param] value != null; [value_is_string] value instanceof String; **/

        Object siteFileValue = null;

        if (value != null)
        {
            NSArray siteFileValues = NSPropertyListSerialization.arrayForString(value.toString());

            SiteFile siteFile = SiteFile.newSiteFile();
            editingContext().insertObject(siteFile);
            siteFile.addObjectToBothSidesOfRelationshipWithKey(((SMVirtualTable) table()).website().databaseTablesFolder(),"folder");

            siteFile.setUploadedFilename((String) siteFileValues.objectAtIndex(0));
            siteFile.setMimeType((String) siteFileValues.objectAtIndex(1));
            siteFile.setFileSizeUsage(new Integer(siteFileValues.objectAtIndex(2).toString()));
            siteFile.setFolder(((SMVirtualTable) table()).website().databaseTablesFolder());

            BASE64Decoder decoder = new BASE64Decoder();
            String mungedFileData = (String) siteFileValues.objectAtIndex(3);
            String normalFileData =  mungedFileData.replace(' ', '\n');
            try {
                NSData data = new NSData(decoder.decodeBuffer(normalFileData));
                siteFile.setData(data);

            } catch (IOException e) {
                throw new ExceptionConverter(e);
            }

            siteFileValue = siteFile.filename();
        }

        return siteFileValue;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * VirtualSiteFileColumns sort on their SiteFile value.
     *
     * @return the keypath to use when comparing fields of this type when sorting rows
     */
    public String keyPathForSorting()
    {
        return normalizedName();
    }
}
