
package net.global_village.foundation.tests;

import junit.framework.*;


/**
 * Contains all tests.
 *
 * @author Copyright (c) 2001-2005  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class AllTestsSuite extends TestSuite
{


    public static Test suite()
    {
        return new AllTestsSuite();
    }


    public AllTestsSuite()
    {
        super();
        //$JUnit-BEGIN$
        addTest(new TestSuite(ArrayFormatterTest.class));
        addTest(new TestSuite(BigDecimalAdditionsTest.class));
        addTest(new TestSuite(BigDecimalFormatterTest.class));
        addTest(new TestSuite(BooleanFormatterTest.class));
        addTest(new TestSuite(BooleanTest.class));
        addTest(new TestSuite(ClassAdditionsTest.class));
        addTest(new TestSuite(CollectionTest.class));
        addTest(new TestSuite(ComplexKeyTest.class));
        addTest(new TestSuite(DateTest.class));
        addTest(new TestSuite(DebugTest.class));
        addTest(new TestSuite(DefaultValueRetrievalTest.class));
        addTest(new TestSuite(DelimitedStringTokenizerTest.class));
        addTest(new TestSuite(DynamicInstanceVariableTest.class));
        addTest(new TestSuite(FileUtilitiesTest.class));
        addTest(new TestSuite(FixedWidthStringTokenizerTest.class));
        addTest(new TestSuite(InSetFormatterTest.class));
        addTest(new TestSuite(IntegerFormatterTest.class));
        addTest(new TestSuite(KeyValueComparatorTest.class));
        addTest(new TestSuite(LengthCheckFormatterTest.class));
        addTest(new TestSuite(LocalizationTest.class));
        addTest(new TestSuite(LoggerFactoryTest.class));
        addTest(new TestSuite(LongFormatterTest.class));
        addTest(new TestSuite(NSArrayAdditionsTest.class));
        addTest(new TestSuite(NSBundleAdditionsTest.class));
        addTest(new TestSuite(NullFormatterTest.class));
        addTest(new TestSuite(OptionalTimeDateFormatterTest.class));
        addTest(new TestSuite(ScaledSumOperatorTest.class));
        addTest(new TestSuite(ScalingFormatterTest.class));
        addTest(new TestSuite(StrictDateAtNoonFormatterTests.class));
        addTest(new TestSuite(StrictDateFormatterTest.class));
        addTest(new TestSuite(StringAdditionsTest.class));
        addTest(new TestSuite(StringArrayFormatterTest.class));
        addTest(new TestSuite(TemplateSubstitutionTest.class));
        addTest(new TestSuite(TimestampInRangeTest.class));
        addTest(new TestSuite(ValidationExceptionAdditionsTest.class));
        addTest(new TestSuite(PropertyListSerializationTest.class));
        //$JUnit-END$
    }



}
