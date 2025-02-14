// Copyright (c) 2001-2005, The Regents of the University of Michigan, Ann Arbor, MI 48109 USA   All rights reserved.
// This software is published under the terms of the Educational Community License (ECL) version 1.0,
// a copy of which has been included with this distribution in the LICENSE.TXT file.
package com.gvcsitemaker.core.support;


import java.util.*;

import com.gvcsitemaker.core.*;
import com.gvcsitemaker.core.utility.*;
import com.webobjects.eoaccess.*;
import com.webobjects.foundation.*;


/**
 * SMSectionHierarchy represents the hierarchy of all Sections that can be navigated to from a single
 * Website.  This hierarchy resembles the GUI representation of a path through a directory tree in 
 * a file system. It is intended to be used for creating a hierarchial menu of all the sections in a 
 * site. The hierarchy is rooted at the WebSites's home section.  See the method openHierarchyToSection
 * for details on which sections are visible in a given situation.    
 * 
 * It also maintains the concept of the navigable Sections in the hierarchy, a list that ignores visibility
 * other than whether a Section is navigable or not.  This is for use in navigation systems where all Sections
 * are always available to be navigated to, e.g. previous and next links, nad an HTML Select based popup
 * navigation system.
 */
public class SMSectionHierarchy extends Object
{

    public static final int NOT_NESTED = 0;
    protected static final boolean INCLUDE_HOME_SECTION = true;
    protected static final boolean EXCLUDE_HOME_SECTION = false;
    
    protected NSMutableArray sectionsInHierarchy;
    protected NSMutableDictionary processedSections;
    protected NSMutableArray visibleSectionsInHierarchy;
    protected NSMutableArray navigableSectionsInHierarchy;

    
    /**
     * Designated constructor.  Creates a hierarchy of sections from the Home section of rootWebsite 
     * down to destinationSection.  destinationSection may be in any Website reachable by navigating 
     * through the sections and sub-sections of rootWebsite.  Unpublished Websites are 
     * not entered.
     *
     * @param rootWebsite - the Website to root the hierarchy at.  The Home section of this is the root of the hirearchy
     * @param destinationSection - the Section to navigate to in the hierarchy of Sections.
     */
    public SMSectionHierarchy(Website rootWebsite, Section destinationSection)
    {
        super();

        /** require
        [valid_website] rootWebsite != null;
        [valid_section] destinationSection != null;
        [ec_matches] rootWebsite.editingContext().equals(destinationSection.editingContext());
        /# [can_reach_destination]  must be able to reach destination from root #/
         **/

       DebugOut.println(10, rootWebsite.siteID() + " opening sections down to " + destinationSection.name());
       NSTimestamp startTime = new NSTimestamp();
       sectionsInHierarchy = new NSMutableArray();
       processedSections = new NSMutableDictionary();
       navigableSectionsInHierarchy = new NSMutableArray();
       createHierarchy(rootWebsite, NOT_NESTED, INCLUDE_HOME_SECTION);
       openHierarchyToSection(destinationSection);
       NSTimestamp stopTime = new NSTimestamp();
       double milliseconds = (double)stopTime.getTime() - startTime.getTime();
       DebugOut.println(10, "creation time: " + (milliseconds / 1000.0) );
       DebugOut.println(10, "navigableSectionsInHierarchy: " + navigableSectionsInHierarchy );
       
        /** ensure
        [sectionsInHierarchy_set] allSectionsInHierarchy() != null;
        [hierarchy_was_created] allSectionsInHierarchy().count() > 0;        **/
    }



    /**
     * Adds Sections from aWebsiute to the hierarchy, nesting them at depth. 
     *
     * @param aWebsite the Website to start building the hierarchy at
     * @param depth the depth to nest the section in aWebsite at
     * @param includeHomeSection <code>true</code> if the Home section of aWebsite should be added to 
     * hierarchy, <code>false</code> for embedded websites. 
     */
    protected void createHierarchy(Website aWebsite, int depth, boolean includeHomeSection)
    {
        /** require
        [valid_website] aWebsite != null;
        [valid_depth] depth >= NOT_NESTED;
         **/
        
        NSArray orderedSections = includeHomeSection ? aWebsite.orderedSections() : aWebsite.nonHomeSections();

        Enumeration sectionEnum = orderedSections.objectEnumerator();
        while(sectionEnum.hasMoreElements())
        {
            Section aSection = (Section)sectionEnum.nextElement();

            // Escape for cycles in embedding.  This _should_ not happen but does when editing takes
            // place on different instances
            if (hasProcessedSection(aSection))
            {
                DebugOut.println(1, "Circular Embedding detected!  Site " + aWebsite.siteID() + ", section " + aSection.name());
            }
            else
            {
                // Add this section
                int sectionDepth = aSection.indentation().intValue() + depth;
                addSectionAtDepth(aSection, sectionDepth);

                // Add embedded sites.  Skip unpublished sites and Sections that do not embed the entire site.
                if (aSection.hasLinkedSite() && aSection.linkedSite().canBeDisplayed() &&
                        ( ! (aSection.type() instanceof EmbeddedSectionSectionType)))
                {
                    createHierarchy(aSection.linkedSite(), sectionDepth + 1, EXCLUDE_HOME_SECTION);
                }
            }
        }
        
        /** ensure [all_sections_added]  /# sectionsInHierarchy contains all the elements in aWebsite.nonHomeSections() #/ true;  **/
    }



    /**
     * Adds section to the end of the hierarchy nested at depth.
     *
     * @param section - the section to add to the hierarchy
     * @param depth - the depth to nest the section at
     */
    protected void addSectionAtDepth( Section section, int depth)
    {
        /** require
        [valid_section] section != null;
        [valid_depth] depth >= NOT_NESTED;
        [section_not_processed] ! hasProcessedSection(section);
         **/

        DebugOut.println(30, "Adding section " + section.name() + " at " +depth);
        SectionDepth sectionAtDepth = new SectionDepth(section, depth);
        sectionsInHierarchy.addObject(sectionAtDepth);
        processedSections.setObjectForKey(section, section.globalID());

        // There is a potential issue here.  It was decided before that if a non-navigable section
        // had sections indented under it, and the non-navigable section was shown, that the sections
        // under it would also appear.  So it is possible for the navigableSectionsInHierarchy to contain
        // sections which are not normally visible / navigable.
        if (section.isNavigable().isTrue())
        {
            navigableSectionsInHierarchy.addObject(sectionAtDepth);
        }
        
        /** ensure
            [section_added] ((SectionDepth)allSectionsInHierarchy().lastObject()).section().equals(section);
            [section_depth_correct] ((SectionDepth)allSectionsInHierarchy().lastObject()).depth() == depth;
            [section_recorded] hasProcessedSection(section);
         **/
    }

    
    
    /**
     * Bug avoidance code to handle circular embedded errors in data.
     * 
     * @param section the section to check
     * @return <code>true</code> if these section has already been seen.
     */
    protected boolean hasProcessedSection(Section section)
    {
        /** require [valid_section] section != null;         **/
        return processedSections.objectForKey(section.globalID()) != null;
    }
    
    
    
    /**
     * Marks the visible sections in the hierarchy.  
     * <ol>
     *   <li>Sections indicated as not navigable are never shown regardless of any other circumstances.</li>
     *   <li>Sections that are not nested (in the root website) are visible.</li>
     *   <li>Sections that do not fall into one of these categories are marked visible by their
     * relationship (position wise) to the destination section:
     *       <ul>
     *         <li>The chain of section from the destination out to the nearest, unnested, section above 
     *             it are visible.  Sections are included in this chain based on an ever diminishing level 
     *             of nesting (sections are only included in this chain if they are nested the same as 
     *             or less than the section immediately below it).</li>
     *         <li>The chain of section from the destination out to the nearest, unnested, section below 
     *             it are visible.  Sections are included in this chain based on an ever diminishing level 
     *             of nesting (sections are only included in this chain if they are nested the same as 
     *             or less than the section immediately below it).</li>
     *         <li>The peer sections of the destination section are visible, as are nested sections immediately below it.</li>
     *       </ul>
     *    </li>
     * </ol>
     * @param destinationSection the section to form a visible path to
     */
    protected void openHierarchyToSection(Section destinationSection)
    {
        /** require  [valid_section] destinationSection != null;    **/
        
        visibleSectionsInHierarchy = new NSMutableArray();
        
        int indexOfDestination = allSectionsInHierarchy().indexOfObject(new SectionDepth(destinationSection));

        // Made up embedded site URLs can't be handled.
        if (indexOfDestination == -1) 
        {
            throw new RuntimeException("The URL is invalid. Site " + destinationSection.website().siteID() + " is not embedded in this site.");
        }
        
        int destinationDepth = sectionAtIndex(indexOfDestination).depth();
        
        // Mark  as visible the chain of sections from the destination, through its peers up to its  
        // parent, and then back out to the nearest, unnested, section above it.
        int currentPosition = indexOfDestination;
        int mostOutdentedSeen = destinationDepth;
        while (sectionAtIndex(currentPosition).isNested())
        {
            // Only mark sections as visible if we are at the same or a less indented level.  This
            // prevents expanding too much.
            if (depthAtIndex(currentPosition) <= mostOutdentedSeen)
            {
                sectionAtIndex(currentPosition).makeVisible();
                mostOutdentedSeen = depthAtIndex(currentPosition); 
            }
            currentPosition--;
        }
        

        // The same as above, only moving down and out.
        currentPosition = indexOfDestination + 1;
        mostOutdentedSeen = destinationDepth;
        while ((currentPosition < allSectionsInHierarchy().count()) &&
               (sectionAtIndex(currentPosition).isNested()) )
        {
            if (depthAtIndex(currentPosition) <= mostOutdentedSeen)
            {
                sectionAtIndex(currentPosition).makeVisible();
                mostOutdentedSeen = depthAtIndex(currentPosition); 
            }
            currentPosition++;
        }

        
        // Mark any nested sections immediately below it as visible.
        currentPosition = indexOfDestination + 1;
        if (currentPosition < allSectionsInHierarchy().count())
        {
            // We can't just add one.  If an embedded site has its first, non home, section indented
            // then it will have an effective double indentation.
            int depthBelowDestination = sectionAtIndex(currentPosition).depth();
            
            // Loop until stopped by an outdented (unnested) section
            while ((currentPosition < allSectionsInHierarchy().count()) &&
                   sectionAtIndex(currentPosition).isBelowDepth(destinationDepth))
            {
                if (sectionAtIndex(currentPosition).isAtDepth(depthBelowDestination))
                {
                    sectionAtIndex(currentPosition).makeVisible();
                }
                currentPosition++;
            }
            
        }

        // Copy all the visible sections
        for (int i = 0; i < allSectionsInHierarchy().count(); i++)
        {
            // All sections that are not nested are always visible
            if ( ! sectionAtIndex(i).isNested())
            {
                sectionAtIndex(i).makeVisible();
            }
            
            if (sectionAtIndex(i).isVisible())
            {
                visibleSectionsInHierarchy.addObject(sectionAtIndex(i));
            }
        }
    }
    
    

    /**
     * Returns the sections, visible or not, in this hierarchy in order.
     *
     * @return the sections in this hierarchy in order
     */
    public NSMutableArray allSectionsInHierarchy()
    {
        return sectionsInHierarchy;

        /** ensure [sectionsInHierarchy_valid] Result != null; **/
    }

    
    
    /**
     * Returns the visible sections in this hierarchy in order.
     *
     * @return the visible sections in this hierarchy in order.
     */
    public NSMutableArray visibleSectionsInHierarchy()
    {
        return visibleSectionsInHierarchy;
        /** ensure [sectionsInHierarchy_valid] Result != null; **/
    }

    
    
    /**
     * Returns the navigable sections (visible or not) in this hierarchy in order.
     *
     * @return the visible sections in this hierarchy in order.
     */
    public NSMutableArray navigableSectionsInHierarchy()
    {
        return navigableSectionsInHierarchy;
        /** ensure [sectionsInHierarchy_valid] Result != null; **/
    }    
    
    
    
    /**
     * Returns the SectionDepth object representing the Section at position i.
     * @param i the zero based index of the Section
     * @return the SectionDepth object representing the Section at position i
     */
    public SectionDepth sectionAtIndex(int i)
    {
        /** require [valid_index] (i >= 0) && (i <= allSectionsInHierarchy().count());   **/
        
        return ((SectionDepth)allSectionsInHierarchy().objectAtIndex(i));
    }


    
    /**
     * Returns the SectionDepth object representing aSection.  Returns null if not found.
     * 
     * @param aSection the Section represented by the SectionDepth to return
     * @return the SectionDepth object representing aSection.
     */
    public SectionDepth sectionDepth(Section aSection)
    {
        /** require [aSection_not_null] aSection != null; **/    
    	
    		SectionDepth matchingSectionDepth = null;
    		
        for (int i = 0; i < allSectionsInHierarchy().count(); i++)
        {
        		//make sure they are in the same editingContext
        		Section localSectionInHierarchy = (Section) EOUtilities.localInstanceOfObject(aSection.editingContext(), sectionAtIndex(i).section());
        		if (localSectionInHierarchy.equals(aSection))
        		{
        			matchingSectionDepth = sectionAtIndex(i);
        			break;
        		}
        }

        return matchingSectionDepth;
    }     

    
    
    /**
     * Returns the depth from the SectionDepth object representing the Section at position i.
     * @param i the zero based index of the Section
     * @return the depth from the SectionDepth object representing the Section at position i
     */
    public int depthAtIndex(int i)
    {
        /** require [valid_index] (i >= 0) && (i <= allSectionsInHierarchy().count());   **/
        
        return sectionAtIndex(i).depth();
        
        /** ensure [valid_result] Result >= NOT_NESTED;  **/
    }


   
    /**
     * Returns the SectionDepth for the Section immediately following this one in the navigable hierarchy.
     * 
     * @param aSection the Section to compare against
     * @return the SectionDepth for the Section immediately following aSection
     */  
    public SectionDepth nextNavigableSection(Section aSection)
    {
        /** require [valid_section] (aSection != null) && hasNextNavigableSection(aSection);  **/
        int sectionIndex = navigableSectionsInHierarchy.indexOfObject(sectionDepth(aSection));
        return (SectionDepth) navigableSectionsInHierarchy.objectAtIndex(sectionIndex + 1);
    }
    
    
    
    /**
     * Returns true if the section passed is not the last one on the navigable list.
     * 
     * @param aSection the Section to compare against
     * @return true if aSection is not the last one on the list
     */  
    public boolean hasNextNavigableSection(Section aSection)
    {
        /** require [valid_section] (aSection != null);  **/
        return navigableSectionsInHierarchy.indexOfObject(sectionDepth(aSection)) < (navigableSectionsInHierarchy.count() - 1);
    }        

    
    
    /**
     * Returns the SectionDepth for the Section immediately preceeding this one in the navigable hierarchy.
     * 
     * @param aSection the Section to compare against
     * @return the SectionDepth for the Section immediately preceeding aSection
     */   
    public SectionDepth previousNavigableSection(Section aSection)
    {
        /** require [valid_section] (aSection != null) && hasPreviousNavigableSection(aSection);  **/
        int sectionIndex = navigableSectionsInHierarchy.indexOfObject(sectionDepth(aSection));
        return (SectionDepth) navigableSectionsInHierarchy.objectAtIndex(sectionIndex - 1);
    }
    
    
    /**
     * Returns true if the section passed is not the first one on the navigable list.
     * 
     * @param aSection the Section to compare against
     * @return true if aSection is not the first one on the list
     */    
    public boolean hasPreviousNavigableSection(Section aSection)  
    {
        /** require [valid_section] (aSection != null);  **/
        return navigableSectionsInHierarchy.indexOfObject(sectionDepth(aSection)) > 0;
    }



    /**
     * Pairs Section and display depth information.
     */
    public class SectionDepth extends Object
    {
        private Section section;
        private int depth;
        private boolean isVisible;
 

        /**
         * Designated constructor.
         * 
         * @param aSection the Section being represented in the hierarchy
         * @param aDepth the depth of the section's nesting in the hierarchy
         */
        public SectionDepth(Section aSection, int aDepth)
        {
            super();
            section = aSection;
            depth = aDepth;
        }



        /**
         * Conveniece constructor when depth is not relevant (not nested).
         * 
         * @param aSection the Section being represented in the hierarchy
         */
        public SectionDepth(Section aSection)
        {
            this(aSection, NOT_NESTED);
        }
        
        
        /**
         * @return <code>true</code> is this section is nested
         */
        public boolean isNested() 
        {
            return depth() > NOT_NESTED;
        }
        
        
        
        /**
         * @param aDepth the depth to compare
         * @return <code>true</code> if the depth for this object is the same as aDepth
         */
        public boolean isAtDepth(int aDepth)
        {
            return aDepth == depth();
        }

        
        
        /**
         * @param aDepth the depth to compare
         * @return <code>true</code> if the depth for this object is below (nested beneath) aDepth
         */
        public boolean isBelowDepth(int aDepth)
        {
            return aDepth < depth();
        }
        
        
        /**
         * Marks this section as visible.
         *
         */
        public void makeVisible()
        {
            isVisible = section().isNavigable().booleanValue();
        }
        
        
        /**
         * SectionDepth compares on Section equality only.  Depth and visibility are ignored.
         */
        public boolean equals(Object testSection) {
            return testSection.equals(section());
        }



        public Section section()
        {
            return section;
        }


        public int depth()
        {
            return depth;
        }


        public boolean isVisible()
        {
            return isVisible;
        }



        public String toString()
        {
            return ((section != null) ? section.name() : "null section") + ": " + depth + ", vis: " + isVisible;
        }



    }



}
