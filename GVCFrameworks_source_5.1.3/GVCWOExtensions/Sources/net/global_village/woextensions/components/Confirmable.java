package net.global_village.woextensions.components;

import com.webobjects.appserver.*;

 
/**
 * This goes with ConfirmAction. Components which call a ConfirmAction page should implement this interface.<br>
 * <br>
 * <b>NOTE>/b>: Do not use <code>return context().page();</code> in the confirmAction and denyAction methods or the ConfimAction page will be redisplayed!  Use <code>return this;</code> instead.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 4$
 */

public interface Confirmable
{
    public WOComponent confirmAction();

    public WOComponent denyAction();
}
