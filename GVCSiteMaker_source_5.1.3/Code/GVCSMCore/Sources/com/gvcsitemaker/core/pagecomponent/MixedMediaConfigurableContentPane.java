// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.

package com.gvcsitemaker.core.pagecomponent;

import java.io.*;
import java.net.*;
import java.util.*;

import net.global_village.eofextensions.*;
import net.global_village.foundation.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.mixedmedia.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.appserver.*;
import com.webobjects.eoaccess.*;
import com.webobjects.eocontrol.*;
import com.webobjects.foundation.*;

/**
 * Subclass of MixedMediaPane, knows which MixedMediaContentConfiguration it is for, manages bindings for source and/or parameters specified by MixedMediaContentConfiguration
 */
public class MixedMediaConfigurableContentPane extends _MixedMediaConfigurableContentPane
{
    public static final String URLSourceTypeName = "url";
    public static final String FileSourceTypeName = "file";
    public static final String SectionSourceTypeName = "section";

    
    /**
     * Factory method to create new instances of MixedMediaConfigurableContentPane.  Use this rather than calling the constructor
     * directly so that customized versions in GVCSMCustom will be used if present.
     *
     * @return a new instance of MixedMediaConfigurableContentPane or a subclass.
     */
    public static MixedMediaConfigurableContentPane newMixedMediaConfigurableContentPane()
    {
        return (MixedMediaConfigurableContentPane) SMEOUtils.newInstanceOf("MixedMediaConfigurableContentPane");

        /** ensure [result_not_null] Result != null; **/
    }

    
    
    /**
     * Overriden to supply default binding values.
     *
     */
    public void awakeFromInsertion(EOEditingContext ec)
    {
        super.awakeFromInsertion(ec);
        
        setComponentType("MixedMediaConfigurableContentPane");
    }    

   
    
 
    /**
     * Returns the WOD file bindings to use with this pane.
     * 
     * @param aRequest the request to take the domain name from    
     * @return the WOD file bindings to use with this pane.
     */
    public String generatedBindings(WORequest aRequest)
    {
        StringBuffer wodFile = new StringBuffer(2048);

        // Add bindings for each of the parameters even if it is null
        Enumeration parameterEnumerator = contentConfiguration().parameters().objectEnumerator();
        while(parameterEnumerator.hasMoreElements())
        {
             MixedMediaParameter aMixedMediaParameter = (MixedMediaParameter) parameterEnumerator.nextElement();
             wodFile.append(aMixedMediaParameter.name()+ ": WOString {\n");
             wodFile.append("    value = parameterValueDictionary." + aMixedMediaParameter.name() + ";\n");
             wodFile.append("    escapeHTML = false;\n");
             wodFile.append("}\n");
             wodFile.append("\n");
        }    
        
        //binding for _SourceURLAsDownload_
        wodFile.append("SourceURLAsDownload" + ": WOString {\n");
        wodFile.append("    value = sourceURLAsDownload;\n");
        wodFile.append("}\n");
        wodFile.append("\n");
        
        //binding for _SourceURL_
        wodFile.append("SourceURL" + ": WOString {\n");
        wodFile.append("    value = pane.sourceURL;\n");
        wodFile.append("}\n");
        wodFile.append("\n");
        
        
        //binding for _SourceURLPath_
        wodFile.append("SourceFile" + ": WOString {\n");
        wodFile.append("    value = pane.sourceFile;\n");
        wodFile.append("}\n");
        wodFile.append("\n");
        
        //binding for _SourceURL_
        wodFile.append("SourcePath" + ": WOString {\n");
        wodFile.append("    value = pane.sourcePath;\n");
        wodFile.append("}\n");
        wodFile.append("\n");        

        return wodFile.toString();

        /** ensure [valid_result] Result != null;  **/
    }
    
    
    
    /**
     * Returns the URL for this pane's source, null if nothing is specified
     * 
     * @return the URL for this pane's source, null if nothing is specified
     */
    public String sourceURL()
    {
    		String sourceURL = null;
    		
    		if (sourceType() != null)
    		{
        		if (sourceType().equals(URLSourceTypeName))
        		{
        			sourceURL = uri();
        		}
        		else if (sourceType().equals(FileSourceTypeName) && (toRelatedFile() != null))
        		{
        			sourceURL = toRelatedFile().url();
        		}
        		else if (sourceType().equals(SectionSourceTypeName) && (relatedSection() != null))
        		{
        			sourceURL = relatedSection().sectionURL();
        		}
    		}
    		
    		return sourceURL;
    }
    
    
    
    /**
     * Returns the URL for this pane's source for download
     * 
     * @param aRequest the request to take the domain name from
     * @return the URL for this pane's source for download
     */
    public String sourceURLAsDownload(WORequest aRequest)
    {
    		String sourceURLAsDownload = null;
    		
    		if ((sourceType() != null) && (sourceType().equals(SectionSourceTypeName)))
    		{
    			sourceURLAsDownload = relatedSection().sectionContentsURL(aRequest);
    		}
    		else //For File and Section return the regular URL
    		{
    			sourceURLAsDownload = sourceURL();
    		}
    			
    		return sourceURLAsDownload;
    }    
    
    
    
    /**
     * Returns this pane's source file
     * 
     * @return the sourceFile
     */ 
    public String sourceFile()
    {
        //TODO: joee transfer StringUtils
    		String sourceFile = null; 
    		
    		if (sourceURL() != null)
    		{
    			int index = sourceURL().lastIndexOf("/");
    			if ((index != -1) && ((sourceURL().length() -1) > index))
    			{
    				sourceFile = sourceURL().substring(index+1); 
    			}
    		}	
    		
    		return sourceFile;
    }   
    
    
    
    /**
     * Returns the path of this pane's source
     * 
     * @return the path of this pane's source
     */     
    public String sourcePath()
    {
		String sourcePath = null; 
		
		if (sourceURL() != null)
		{
			int index = sourceURL().lastIndexOf("/");
			if (index >= 1)
			{
				sourcePath = sourceURL().substring(0, index); 
			}
		}	
		
		return sourcePath;
    }     
    
    
    
    
    /**
     * Returns true if the passed sourceURL is valid, false otherwise
     * 
     * @param sourceURL the sourceURL to evaluate
     * @return true if the passed sourceURL is valid, false otherwise
     */      
	public boolean isSourceValid(String sourceURL) 
   	{
        /** require [sourceURL_is_not_null] sourceURL != null; **/	
		
        boolean isSourceValid = false;
        
        try
        {
            URL url = new URL(sourceURL);
            url.openConnection().getContent();
            isSourceValid = true;
        }
        catch (MalformedURLException e)
        {
            DebugOut.println(30, "Failed to validate " + sourceURL + " , reason: " + e.getMessage());
        }
        catch (IOException e)
        {
            DebugOut.println(30, "Failed to validate " + sourceURL + " , reason: " + e.getMessage());
         }
        
        return isSourceValid;
   	}            
    
    
        
    /**
     * Performs some extra validations
     */
    public void validateForSave() throws NSValidation.ValidationException
    {
        NSMutableArray exceptions = new NSMutableArray();

        try
        {
            super.validateForSave();
        }
        catch ( NSValidation.ValidationException e)
        {
            exceptions.addObjectsFromArray(e.additionalExceptions());
        }
 
        //Validate Source has correct URL prefix
        if ((sourceURL() != null) && ( ! URLUtils.hasValidURIScheme((NSArray) contentConfiguration().urlPrefixes().valueForKey("prefix"), sourceURL())))
        {
            exceptions.addObject(new NSValidation.ValidationException("URL must start with: " + ((NSArray) contentConfiguration().urlPrefixes().valueForKey("prefix")).componentsJoinedByString(", ")));
        }

        if (exceptions.count() > 0)
        {
            throw NSValidation.ValidationException.aggregateExceptionWithExceptions(exceptions);
        }
    }
    
    
    
    /**
     * Validate values based on configuration requirements.
     */
    public void validateConfigurationRequirements() throws NSValidation.ValidationException
    {
        NSMutableArray exceptions = new NSMutableArray();

        //Validate Source has correct URL prefix
        if ((sourceURL() != null) && ( ! URLUtils.hasValidURIScheme((NSArray) contentConfiguration().urlPrefixes().valueForKey("prefix"), sourceURL())))
        {
            exceptions.addObject(new NSValidation.ValidationException("URL must start with: " + ((NSArray) contentConfiguration().urlPrefixes().valueForKey("prefix")).componentsJoinedByString(", ")));
        }

        //Validate Source requirement
        if (contentConfiguration().requiresSource() && (sourceURL() == null))
        {
            exceptions.addObject(new NSValidation.ValidationException("You must provide a Source for this pane."));
        }
        
        //Validate required parameters and their type
        Enumeration parameterEnumerator = contentConfiguration().parameters().objectEnumerator();
        while(parameterEnumerator.hasMoreElements())
        {
             MixedMediaParameter aParameter = (MixedMediaParameter) parameterEnumerator.nextElement(); 
             if ((aParameter.isRequired() && (valueForBindingKey(aParameter.name()) == null)))
             {
                 exceptions.addObject(new NSValidation.ValidationException("You must fill in " + aParameter.uiName() + " for this pane."));
             }
             
             //Validate values for Integers and PercentOrPixels typed parameters
             if ((aParameter.type().equals(MixedMediaParameter.IntegerType) || aParameter.type().equals(MixedMediaParameter.PercentOrPixelsType))
            		 && (valueForBindingKey(aParameter.name()) != null)
            		 && (! StringAdditions.isInteger((String) valueForBindingKey(aParameter.name()))))
             {
                 exceptions.addObject(new NSValidation.ValidationException(aParameter.uiName() + " must have a valid integer value."));
             }
        }
        
        if (exceptions.count() > 0)
        {
            throw NSValidation.ValidationException.aggregateExceptionWithExceptions(exceptions);
        }
    }    
    
    
    
    /**
     * Overriden to get value form contentConfiguration
     */       
    public boolean usesIFrame() 
    {
        return contentConfiguration().usesIFrame();
    }    

    
    
    /**
     * Overriden to get value form contentConfiguration
     */   
    public boolean requiresIFrame() 
    {
        return contentConfiguration().requiresIFrame();
    }    
 
    
    
    
    /**
     * Returns the bindings to be used when this pane is displayed in an iFRAME
     * 
     * @param aRequest the request to take the domain name from
     * @return the bindings to be used when this pane is displayed in an iFRAME
     */     
    public String iFrameBindings(WORequest aRequest) 
    {
        StringBuffer wodFile = new StringBuffer(2048);

        //binding for _SourceURLAsDownload_
        wodFile.append("SourceURLAsDownload" + ": WOString {\n");
        wodFile.append("    value = sourceURLAsDownload;\n");        
        wodFile.append("}\n");
        wodFile.append("\n");
        
        //binding for _SourceURL_
        wodFile.append("SourceURL" + ": WOString {\n");
        wodFile.append("    value = pane.sourceURL;\n");        
        wodFile.append("}\n");
        wodFile.append("\n");

        //binding for Height
        wodFile.append("Height" + ": WOString {\n");
        wodFile.append("    value = pane.height;\n");
        wodFile.append("}\n");
        wodFile.append("\n");
        
        return wodFile.toString();

        /** ensure [valid_result] Result != null;  **/
    }    

    
    
    /**
      * Returns a copy of this MixedMediaConfigurableContentPane with the relatedSection copied by reference.
      *
      * @param copiedObjects - the copied objects keyed on the EOGlobalID of the object the copy was made from.
      * @return a copy of this MixedMediaConfigurableContentPane
      */
     public EOEnterpriseObject duplicate(NSMutableDictionary copiedObjects)
     {
         /** require [copiedObjects_not_null] copiedObjects != null;  **/

         EOEnterpriseObject copy = super.duplicate(copiedObjects);
         EOEntity entity = ((EOEntityClassDescription)classDescription()).entity();
         
         if (Section.isSectionOnlyCopy(copiedObjects))
         {
             ((PageComponent)copy).addObjectToBothSidesOfRelationshipWithKey(relatedSection(), "relatedSection");
         }
         else
         {
             EOCopying.Utility.deepCopyRelationship(copiedObjects, this, copy, entity.relationshipNamed("relatedSection"));
         }

         return copy;

         /** ensure [copy_made] Result != null;   **/
     }    
}

