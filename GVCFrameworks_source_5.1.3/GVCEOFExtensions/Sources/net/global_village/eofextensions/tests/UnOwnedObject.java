package net.global_village.eofextensions.tests;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.eofextensions.*;

/**
 * EOEnterprise object used in testing of EOCopying / CopyableGenericRecord
 * Unowned object copied by reference like a lookup list would.
 *
 * @author Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class UnOwnedObject extends _UnOwnedObject
{

    public EOEnterpriseObject duplicate(NSMutableDictionary copiedObjects)
    {
        return EOCopying.Utility.referenceCopy(this);
    }


}
