#!/bin/sh
echo 
echo Starting Installation...
echo 

# This must be a fully qualified path
SMHOME=$1

echo SMHOME: $SMHOME
echo 

if [ ! -d $SMHOME -o -z "$SMHOME" ]; then
    echo "Usage: ./WOInstall.sh <Fully Qualified Install Dir>"
    exit
fi


mkdir $SMHOME/Install/Backup 2>/dev/null
n=0
for i in `cat $SMHOME/Install/Script/backup.list`
  do
  echo Backing Up $i
  n=`echo $n+1 |bc`
  gnutar czf $SMHOME/Install/Backup/`basename $i`.$n.tar.gz $SMHOME/$i 2>/dev/null
done
echo


echo Cleaning $SMHOME/Applications
rm -rf $SMHOME/Applications 2>/dev/null
echo Cleaning $SMHOME/Frameworks
rm -rf $SMHOME/Frameworks 2>/dev/null
echo


mkdir $SMHOME/Applications
mkdir $SMHOME/Frameworks
mkdir $SMHOME/DocumentRoot 2>/dev/null

cd $SMHOME/Install/Script

for i in `cat $SMHOME/Install/Script/archive.list`
do
  ARCH=`echo $i | awk -F: '{print $1}'`
  DIR=`echo $i | awk -F: '{print $2}'` 

  echo Installing $ARCH into $SMHOME/$DIR
  mkdir -p $SMHOME/$DIR 2>/dev/null
  cd $SMHOME/$DIR

  gnutar xzf $SMHOME/Install/Archive/$ARCH
done
echo


cd /
n=0
for i in `cat $SMHOME/Install/Script/backup.list`
  do 
  echo Restoring $i
  n=`echo $n+1 |bc`
  #cd ${SMHOME}/`dirname $i`
  gnutar xzf $SMHOME/Install/Backup/`basename $i`.$n.tar.gz 2>/dev/null
done
