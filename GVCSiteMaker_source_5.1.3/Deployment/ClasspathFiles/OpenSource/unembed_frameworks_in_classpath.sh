#!/bin/sh

# Designed to be run from GVCSiteMaker/Deployment/ClasspathFiles
if [ ! -f OpenSource/GVCSiteMaker/MacOSClassPath.txt ]; then
    echo "Must be run from GVCSiteMaker/Deployment/ClasspathFiles"
    exit
fi

for i in GVCS*/*; do
    echo Processing $i
    mkdir OpenSource/`dirname $i` >& /dev/null
    sed "s|APPROOT/Frameworks|/System/Library/Frameworks|g" $i > OpenSource/$i
done
