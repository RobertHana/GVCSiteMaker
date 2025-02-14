package jass.util.doc;

import com.sun.tools.doclets.standard.*;
import com.sun.tools.doclets.*;
import com.sun.javadoc.*;
import java.util.*;
import java.io.*;

public class JassDoc extends Standard {    
    /**
     * The "start" method as required by Javadoc.
     *
     * @param Root
     * @see com.sun.javadoc.Root
     * @return boolean
     */
    public static boolean start(RootDoc root) throws IOException {
	System.out.println("--- This is JassDoc"); // output inserted
        try { 
            configuration().setOptions(root);
            (new JassDoc()).startGeneration(root); // using JassDoc
        } catch (DocletAbortException exc) {
	  //exc.printStackTrace();
            return false; // message has already been displayed
        }
        return true;
    }

    /**
     * Instantiate ClassWriter for each Class within the ClassDoc[]
     * passed to it and generate Documentation for that.
     */
    protected void generateClassCycle(ClassDoc[] arr, ClassTree classtree,
                            boolean nopackage) throws DocletAbortException {
        Arrays.sort(arr);
        for(int i = 0; i < arr.length; i++) {
            if (configuration().nodeprecated && 
                     arr[i].tags("deprecated").length > 0) {
                continue;
            }
            ClassDoc prev = (i == 0)? 
                            null:
                            arr[i-1];
            ClassDoc curr = arr[i];
            ClassDoc next = (i+1 == arr.length)? 
                            null:
                            arr[i+1];
            JassClassWriter.generate(curr, prev, next, classtree, nopackage);
        }
    }  
}





