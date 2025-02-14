package net.global_village.woextensions;

import com.webobjects.appserver.*;
import com.webobjects.foundation.*;


/**
 * This interface defines the method that must exist in a customized exception page. The WOApplication class will use the defined method to provide your customized exception page with the exception that was raised.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 4$
 */  
public interface ExceptionPage
{


    /**
     * This method should be used to retain the exception, extra info, and the context. It is called by WOApplication's handleError method. 
     *
     * @param anException the exception that was raised
     * @param userInfo other info related to this exception
     * @param aContext the context the exception was raised in
     */
    public void reportException(Throwable anException, NSDictionary userInfo, WOContext aContext);



}
