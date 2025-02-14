#! /bin/sh
# Intended to be run from within a PBWO build.  This will preprocess all
# java files (that don't reside in "excludedDirs") with Jass.


export PATH="/usr/local/bin:$PATH"
prog=`basename $0 .sh`

usage="\
Usage:
  $prog"


tempFile="$TEMP_FILES_DIR/jassFilesToProcess"
jassTempDir="$TEMP_FILES_DIR/derived_src_java/jass"
jassProcessedFilesPath="$TEMP_FILES_DIR/derived_src_java"
findTool="$NEXT_ROOT/$SYSTEM_DEVELOPER_DIR/Executables/Utilities/find"
javaTool="$NEXT_ROOT/$SYSTEM_DEVELOPER_DIR/Executables/javatool"


# Setup the list of files to process
excludedDirs="Tests.subproj"
findExclude=""
for i in $excludedDirs; do
    findExclude="$findExclude -name $i -o"
done
# Normally, I would assign this to a var, but it seems this causes problems...
$findTool $SRCROOT \( $findExclude -name \*.build \) -prune \! -type d -o -name \*java > $tempFile


mkdir -p "$jassTempDir/orig"
$javaTool -copy -newer -java_src $jassTempDir/orig `cat $tempFile`
cd "$jassTempDir/orig"

# Eliminate those files that are older than the processed file...
jassProcessFiles=""
for i in `$findTool . -name \*java` ; do
    if [ ! "$jassProcessedFilesPath/$i" -nt "$i" ]; then
        #echo $jassProcessedFilesPath/$i is NOT newer than $i, adding to files to preprocess
        jassProcessFiles="$jassProcessFiles $i"
    fi
done

if [ "$jassProcessFiles" != "" ]; then

    # Write out the files to process to avoid line lenth issues
    #echo $jassProcessFiles > $tempFile

    # Export the new classpath to avoid quoting issues!
    export CLASSPATH="$CLASSPATH;$jassTempDir/orig"

    # The -dataflow option and the "opt" contract option (which
    # enables dataflow) causes a problem with the <class>.class
    # construction.  Also, the "trace" contract option causes
    # hashCode() to be called before the object is fully initialized -
    # and it is not threadsafe!  So, we don't use those options...
    jassCommand="java jass.Jass \
        -contract [pre,post,inv,loop,check,refine,forall] \
        -d $jassTempDir"

    # This SHITE to get around lame-ass command line length limitation in WinBLOWS
    #tempJassProcessFiles=""
    #shortJassProcessFiles=""
    #for i in $jassProcessFiles; do
        # Tried to do some fancy here, but I guess I'll just do it one at a time
        #tempJassProcessFiles="$tempJassProcessFiles $i"
        #if [ $i ] ;
        #then
            #echo Executing jass on $shortJassProcessFiles
            #echo $jassCommand $i
            $jassCommand $jassProcessFiles
            result=$?
            if [ $result -ne 0 ]; then
                set  # show environment on fail
                exit $result
            fi

            #tempJassProcessFiles=$i
            #shortJassProcessFiles=""
        #fi
        #shortJassProcessFiles="$shortJassProcessFiles $i"
    #done

    # Copy the processed java files to Apple's processing dir so they get compiled
    cd "$jassTempDir"
    jassProcessedFiles=`$findTool * \( -name orig \) -prune \! -type d -o -name \*java`

    # Only copies if the source file is newer than the dest (or dest doesn't exist)
    echo "Copying PROCESSED java files to $jassProcessedFilesPath"
    for i in $jassProcessedFiles; do
        if [ ! "$jassProcessedFilesPath/$i" -nt "$i" ]; then
            mkdir -p $jassProcessedFilesPath/`dirname $i`
            cp -p "$i" "$jassProcessedFilesPath/$i"
            #echo Copied $i to $jassProcessedFilesPath/$i
        fi
    done

fi
