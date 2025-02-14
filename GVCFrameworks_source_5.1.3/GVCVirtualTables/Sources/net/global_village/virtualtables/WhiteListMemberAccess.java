package net.global_village.virtualtables;

import java.lang.reflect.*;
import java.util.*;

import ognl.*;

import org.apache.log4j.*;

import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 * Prevents access to static and instance members of all classes not in the white list (MemberAccessWhiteList.plist).
 * This was done to prevent attacks such as <code>@java.lang.System@exit(1)</code> in
 * the expression of a calculated column
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 2$
 */
public class WhiteListMemberAccess extends DefaultMemberAccess
{

    public static final NSArray WhiteList = NSBundleAdditions.listWithName(NSBundle.bundleForClass(WhiteListMemberAccess.class),
                                                                           "MemberAccessWhiteList.plist");

    private Logger logger = LoggerFactory.makeLogger();


    public WhiteListMemberAccess(boolean allowAllAccess)
    {
        super(allowAllAccess);
    }


    public WhiteListMemberAccess(boolean allowPrivate, boolean allowProtected, boolean allowPackageProtected)
    {
        super(allowPrivate, allowProtected, allowPackageProtected);
    }



    /**
     * Returns false if the target class is not in WhiteList.
     *
     * @see ognl.DefaultMemberAccess#isAccessible(java.util.Map, java.lang.Object, java.lang.reflect.Member, java.lang.String)
     */
    public boolean isAccessible(Map context,
                                Object target,
                                Member member,
                                String propertyName)
    {
        Class targetClass = (target instanceof Class) ? (Class)target : target.getClass();
        if ( ! WhiteList.containsObject(targetClass.getCanonicalName()))
        {
            logger.info("Blocking access to " + targetClass.getCanonicalName() + "." + member.getName() + "()");
        }
        return WhiteList.containsObject(targetClass.getCanonicalName());
    }

}
