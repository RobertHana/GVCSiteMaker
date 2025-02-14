#!/bin/sh
# Create dirs used in a segregated WO installation.  NOTE: Must be run as root.
#
# USAGE:
# mkwodirs.sh <username> [ <dir> ]
# where
# username is the user that will be the owner of this installation and
# dir is an optional directory to create the directories in.  The default is /Users/<username>.

if [ "$1" = "" ]; then
    prog=`basename $0 .sh`
    echo Usage:
    echo "$prog <username> [ <dir> ]"
    echo username is the user that will be the owner of this installation
    echo dir is an optional directory to create the directories in.  The default is /Users/<username>.
    echo "  NB: must be run as root"
    exit 1;
else
    OWNING_USER=$1
fi

if [ "$2" = "" ]; then
    ROOT_FOR_DIRS=/Users/$OWNING_USER
else
    ROOT_FOR_DIRS=$2
fi
cd $ROOT_FOR_DIRS


# Applications
mkdir -m 750 -p Applications
chown $OWNING_USER:wheel Applications

# DocumentRoot
mkdir -m 755 -p DocumentRoot
chown $OWNING_USER:www DocumentRoot

# logs
mkdir -m 711 -p logs
chown $OWNING_USER:wheel logs
cd logs
mkdir -m 730 -p app
chown $OWNING_USER:wheel app
mkdir -m 730 -p www
chown $OWNING_USER:www www
cd ..

# Frameworks
mkdir -m 750 -p Frameworks
chown $OWNING_USER:wheel Frameworks

# UploadedFiles
mkdir -m 770 -p UploadedFiles
chown appserver:www UploadedFiles

# CGI-Executables
mkdir -m 750 -p CGI-Executables
chown $OWNING_USER:www CGI-Executables


echo Done.

