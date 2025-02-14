package jass.util.doc;


/**
 * This interface provides the tags, that are used by the JassDoclet
 * for the Design by Contract part of formal comments.
 */
public interface Tags {
    /**
     * The tag for the precondition (the requires clause).
     */
    public final static String JASSDOC_PRE  = "jass.require"; 

    /**
     * The tag for the postcondition (the ensure clause).
     */
    public final static String JASSDOC_POST = "jass.ensure"; 

    /**
     * The tag for the invariant (the ensure clause).
     */
    public final static String JASSDOC_INV  = "jass.invariant"; 
}
