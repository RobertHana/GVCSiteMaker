package net.global_village.eofextensions;

import java.util.*;

import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

import net.global_village.foundation.*;


/**
 * Additions to NSArray to make creating qualifiers from an array easier.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 * @version $Revision: 6$
 */
public class NSArrayQualifierAdditions
{

    /**
     * Static methods only.  You'll never need to instantiate this class.
     */
    private NSArrayQualifierAdditions()
    {
        super();
    }



    /**
     * Returns an array of qualifiers with each element in self being compared to theKey by selector.
     * e.g <code>NSArray anArray = NSArrayQualifierAdditions.arrayOfQualifiers(theArray, "name", EOQualifier.QualifierOperatorEqual);</code>
     *
     * @param anArray the array to be be evaluated
     * @param theKey the key to be compared to each element
     * @param selector the selector to be used for the comparison
     * @return an array of qualifiers
     */
    public static NSArray arrayOfQualifiers(NSArray anArray,
                                            String theKey,
                                            NSSelector selector)
    {
        /** require
        [valid_anArray_param] anArray != null;
        [valid_theKey_param] theKey != null;
        [valid_selector_param] selector != null; **/

        NSMutableArray qualifierArray = new NSMutableArray();
        EOKeyValueQualifier qualifier;

        Enumeration objectEnumerator = anArray.objectEnumerator();
        while(objectEnumerator.hasMoreElements())
        {
            Object anObject = objectEnumerator.nextElement();

            qualifier = new EOKeyValueQualifier(theKey, selector, anObject);
            qualifierArray.addObject(qualifier);

        }

        JassAdditions.post("NSArrayQualifierAdditions", "arrayOfQualifiers", qualifierArray.count() == anArray.count());
        return qualifierArray;

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns a single <code>EOAndQualifier</code> of the AND of each element in self being compared to theKey by selector.
     * e.g. <code>EOAndQualifier andQualifier = NSArrayQualifierAdditions.andQualifier(someObjects, "name", EOQualifier.QualifierOperatorEqual);</code>
     *
     * @param anArray the array to be be evaluated
     * @param theKey the key to be compared to each element
     * @param selector the selector to be used for the comparison
     * @return an EOAndQualifier
     */
    public static EOAndQualifier andQualifier(NSArray anArray,
                                              String theKey,
                                              NSSelector selector)
    {
        /** require
        [valid_anArray_param] anArray != null;
        [valid_theKey_param] theKey != null;
        [valid_selector_param] selector != null; **/

        NSArray qualifierArray = NSArrayQualifierAdditions.arrayOfQualifiers(anArray,
                                                                             theKey,
                                                                             selector);

        return new EOAndQualifier(qualifierArray);

        /** ensure [valid_result] Result != null; **/
    }



    /**
     * Returns a single <code>EOOrQualifier</code> of the OR of each element in self being compared to theKey by selector.
     * e.g. <code>EOOrQualifier orQualifier = NSArrayQualifierAdditions.orQualifier(someObjects, "name", EOQualifier.QualifierOperatorEqual);</code>
     *
     * @param anArray		the array to be be evaluated
     * @param theKey		the key to be compared to each element
     * @param selector		the selector to be used for the comparison
     * @return an EOOrQualifier
     */
    public static EOOrQualifier orQualifier(NSArray anArray,
                                            String theKey,
                                            NSSelector selector)
    {
        /** require
        [valid_anArray_param] anArray != null;
        [valid_theKey_param] theKey != null;
        [valid_selector_param] selector != null; **/

        NSArray qualifierArray = NSArrayQualifierAdditions.arrayOfQualifiers(anArray,
                                                                             theKey,
                                                                             selector);

        return new EOOrQualifier(qualifierArray);

        /** ensure [valid_result] Result != null; **/
    }



}
