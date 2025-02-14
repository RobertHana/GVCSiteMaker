package net.global_village.foundation.tests;

import java.math.BigDecimal;

/**
 *
 * @author Copyright (c) 2001-5  Global Village Consulting, Inc.  All rights reserved.
 * This software is published under the terms of the Educational Community License (ECL) version 1.0,
 * a copy of which has been included with this distribution in the LICENSE.TXT file.
 */
public class ObjectWithBigDecimal extends Object
{
    protected BigDecimal quantity;

    public BigDecimal quantity()
    {
        return quantity;
    }

    public void setQuantity(BigDecimal value)
    {
        quantity = value;
    }
}
