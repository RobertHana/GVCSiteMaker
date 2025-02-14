package net.global_village.eofextensions;


import com.webobjects.foundation.*;

import er.extensions.eof.*;


/**
 * Principal Class for GVCEOFExtensions.
 *
 * @author Copyright (c) 2001-2009  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $REVISION$
 */
public class PrincipalClass
{

    static
    {
        NSLog.debug.appendln("Initializing GVCEOFExtensions");
        ERXDatabaseContextMulticastingDelegate.addDefaultDelegate(new net.global_village.eofextensions.DatabaseContextDelegate());
    }

}
