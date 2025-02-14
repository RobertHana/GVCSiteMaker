#!/bin/sh

echo $1

if [ "$1" = "" ]
then
echo "Usage: CreateUploadFolders.sh <Target Directory>"
exit
fi

i=0
while [ $i -lt 100 ]
do
if [ $i -lt 10 ]
then
mkdir $1/0$i;
chmod a+rwx $1/0$i;
else
mkdir $1/$i;
chmod a+rwx $1/$i;
fi

i=`echo $i+1 | bc`

done

