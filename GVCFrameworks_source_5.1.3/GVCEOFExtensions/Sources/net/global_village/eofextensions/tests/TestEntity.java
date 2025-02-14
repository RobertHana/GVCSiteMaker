
package net.global_village.eofextensions.tests;




/**
 * Variation of SimpleTestEntity in GVCEOTestModelBase moved here to test GenericRecord and to avoid circular dependancy on GVCEOTestModelBase.
 *
 * @author Copyright (c) 2000  Global Village Consulting, Inc.  All rights reserved.
 * @version $Revision: 4$
 */  
public class TestEntity extends _TestEntity
{
    private String cachedValue;
    
    
    public void clearCachedValues()
    {
        super.clearCachedValues();
        setCachedValue(null);
    }


    public void setCachedValue(String newValue)
    {
        cachedValue = newValue;
    }
    
    public String cachedValue()
    {
  	    return cachedValue;
    }
}
