// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core.mixedmedia;

import java.util.*;

import com.gvcsitemaker.core.utility.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

/**
 * 	MixedMediaContentConfiguration represents a single instance for each media type loaded from XML configuration files during application startup
 *	- uses XMLParser to parse configuration files into this class
 *	- will know the following information regarding media type: 
 *		- title
 *		- knows if it uses IFRAMES
 *		- if it takes a source URL and what prefixes are allowed
 *		- instructions for specific parameters
 *		- list of parameters and template to generate HTML
 *	- has a special instance for the Text type
 *	- knows all media types
 */
public class MixedMediaContentConfiguration implements NSValidation, NSKeyValueCoding
{
	protected String title;
	protected boolean usesIFrame = false;
	protected boolean requiresIFrame = false;
	protected boolean requiresSource = false;
	protected String parameterInstructions;
	protected String template;
	protected NSArray urlPrefixes = new NSArray();
	protected NSArray parameters = new NSArray();
	
	
    /**
     * Returns matching MixedMediaConfigurableContent in the list of configurableContents based on typeName, returns null if not in the list
     *
     */
    public static MixedMediaContentConfiguration configurableContent(NSArray configurableContents, String typeName)
    {
        /** require [configurableContents_not_null] configurableContents != null;
         		   [typeName_not_null] typeName != null; 
         **/
    	
    		MixedMediaContentConfiguration configurableContent = null;
    		
    		EOQualifier qual = EOQualifier.qualifierWithQualifierFormat("title = %@", new NSArray(typeName));
    		NSArray matchingContent = EOQualifier.filteredArrayWithQualifier(configurableContents, qual);

        if (matchingContent.count() == 1)
        {
        		configurableContent = (MixedMediaContentConfiguration) matchingContent.objectAtIndex(0);
        }
        else if (matchingContent.count() > 1)
        {
            throw new RuntimeException("There is more than 1 MixedMedia type that has the same name: " + typeName);        	
        }
        else //not found
        {
            throw new RuntimeException("Configuration file for MixedMedia type " + typeName + " was not found.");        	
        }
        
        return configurableContent;
        
        /** ensure [result_not_null] Result != null; **/        
    }       
    
    
    
    /**
     * Validate template with parameters
     */
    public void validateTemplate() throws NSValidation.ValidationException
    {
    		if (template() != null)
    		{
    	        try
    	        {
    	            // Add temporary bindings for each of the parameters to test
    	            StringBuffer wodFile = new StringBuffer(2048);        	
    	            Enumeration parameterEnumerator = parameters().objectEnumerator();
    	            while(parameterEnumerator.hasMoreElements())
    	            {
    	                 MixedMediaParameter aMixedMediaParameter = (MixedMediaParameter) parameterEnumerator.nextElement();
    	                 wodFile.append(aMixedMediaParameter.name()+ ": WOString {\n");
    	                 wodFile.append("    value = \"\";\n");
    	                 wodFile.append("}\n");
    	                 wodFile.append("\n");
    	            }    

    	            Enumeration componentNamesEnumerator = reservedComponentNames().objectEnumerator();
    	            while(componentNamesEnumerator.hasMoreElements())
    	            {
    	                 String aComponentName = (String) componentNamesEnumerator.nextElement();
    	                 wodFile.append(aComponentName+ ": WOString {\n");
    	                 wodFile.append("    value = \"\";\n");
    	                 wodFile.append("}\n");
    	                 wodFile.append("\n");
    	            }                 

    	            HtmlTemplateUtils.elementsFromTemplateWithBindings(template(), wodFile.toString(), new NSArray());
    	        }
    	        catch (IllegalArgumentException e)
    	        {
    	            throw new NSValidation.ValidationException(e.getMessage());
    	        }    			
    		}
    }
    
    
    
    public NSArray reservedComponentNames()
    {
    		return new NSArray(new Object[]{"SourceURL", "SourceFile", "SourcePath", "SourceURLAsDownload", "Height"});

         /** ensure [result_not_null] Result != null; **/     		
    }

    
    
    public void setTitle(String aValue) 
    {
        title = aValue;
    }



    public String title() 
    {
        return title;
    }
    
    
    
    public void setUsesIFrame(boolean aValue) 
    {
        usesIFrame = aValue;
    }



    public boolean usesIFrame() 
    {
        return usesIFrame;
    }    
    
    
    
    public void setRequiresIFrame(boolean aValue) 
    {
        requiresIFrame = aValue;
    }



    public boolean requiresIFrame() 
    {
        return requiresIFrame;
    }  
    
    
    
    public void setRequiresSource(boolean aValue) 
    {
        requiresSource = aValue;
    }



    public boolean requiresSource() 
    {
        return requiresSource;
    }    
    
    
    
    public void setParameterInstructions(String aValue) 
    {
        parameterInstructions = aValue;
    }



    public String parameterInstructions() 
    {
        return parameterInstructions;
    }    
    
    
    
    public void setTemplate(String aValue) 
    {
        template = aValue;
    }



    public String template() 
    {
        return template;
    }
    
    
    
    public void setUrlPrefixes(NSArray aValue) 
    {
        urlPrefixes = aValue;
    }



    public NSArray urlPrefixes() 
    {
        return urlPrefixes;
    }    
    
    
    
    public void setParameters(NSArray aValue) 
    {
        parameters = aValue;
    }



    public NSArray parameters() 
    {
        return parameters;
    }      
    
    
    /******** Work-around to avoid formatter exceptions when decoding String to boolean *********/
    public boolean validateUsesIFrame(String usesIFrameStr) throws NSValidation.ValidationException {
        return (new Boolean(usesIFrameStr)).booleanValue();
    }    
  
    public boolean validateRequiresIFrame(String requiresIFrameStr) throws NSValidation.ValidationException {
        return (new Boolean(requiresIFrameStr)).booleanValue();
    }    
    
    public boolean validateRequiresSource(String requiresSourceStr) throws NSValidation.ValidationException {
        return (new Boolean(requiresSourceStr)).booleanValue();
    }        
    
    /****** Required methods for implementing NSKeyValueCoding *******/
    public Object valueForKey(String key) {
    	   return NSKeyValueCoding.DefaultImplementation.valueForKey(this, key);
    	}    
    
    public void takeValueForKey(Object value, String key) {
 	   NSKeyValueCoding.DefaultImplementation.takeValueForKey(this, value, key);
 	}        
    
    public Object validateValueForKey(Object value,String key) {
      return NSValidation.DefaultImplementation.validateValueForKey(this,value,key);
    }   

	public Object validateTakeValueForKeyPath(Object value, String key) throws ValidationException {
		return NSValidation.DefaultImplementation.validateTakeValueForKeyPath(this,value,key);
	}
    

	
     
    	
}