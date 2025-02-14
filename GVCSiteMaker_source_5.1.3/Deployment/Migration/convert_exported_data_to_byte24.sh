#!/bin/sh

GAWK=gawk


# file to process in $1
if [ "$1" = "" ]; then
    prog=`basename $0 .sh`
    echo Usage:
    echo "$prog <path to schema directory>"
    exit 1;
else
    PATH_TO_SCHEMA=$1
fi


# GVC_LOOKUP
FILE_TO_PROCESS=3_55
NUMBER_OF_COLUMNS=2
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a2 = length($2) > 0 ? sprintf("X%048u", $2) : $2;   print $1, a2 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# GVC_VA_OBJECT
FILE_TO_PROCESS=3_56
NUMBER_OF_COLUMNS=2
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a2 = length($2) > 0 ? sprintf("X%048u", $2) : $2;   print $1, a2 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# SM_GROUP_SECTION
FILE_TO_PROCESS=3_44
NUMBER_OF_COLUMNS=2
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
              { a1 = length($1) > 0 ? sprintf("X%048u", $1) : $1;   a2 = length($2) > 0 ? sprintf("X%048u", $2) : $2;   print a1, a2 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# SM_PAGE_COMP
FILE_TO_PROCESS=3_45
NUMBER_OF_COLUMNS=15
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a2 = length($2) > 0 ? sprintf("X%048u", $2) : $2;   a5 = length($5) > 0 ? sprintf("X%048u", $5) : $5;   a6 = length($6) > 0 ? sprintf("X%048u", $6) : $6;   a9 = length($9) > 0 ? sprintf("X%048u", $9) : $9;   a10 = length($10) > 0 ? sprintf("X%048u", $10) : $10;   a11 = length($11) > 0 ? sprintf("X%048u", $11) : $11;   a12 = length($12) > 0 ? sprintf("X%048u", $12) : $12;   a13 = length($13) > 0 ? sprintf("X%048u", $13) : $13;   a14 = length($14) > 0 ? sprintf("X%048u", $14) : $14;   a15 = length($15) > 0 ? sprintf("X%048u", $15) : $15;   print $1, a2, $3, $4, a5, a6, $7, $8, a9, a10, a11, a12, a13, a14, a15 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# VIRTUAL_COLUMN
FILE_TO_PROCESS=3_57
NUMBER_OF_COLUMNS=10
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
              { a1 = length($1) > 0 ? sprintf("X%048u", $1) : $1;   a2 = length($2) > 0 ? sprintf("X%048u", $2) : $2;   a7 = length($7) > 0 ? sprintf("X%048u", $7) : $7;   a9 = length($9) > 0 ? sprintf("X%048u", $9) : $9;   print a1, a2, $3, $4, $5, $6, a7, $8, a9, $10 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# COLUMN_TYPE
FILE_TO_PROCESS=3_58
NUMBER_OF_COLUMNS=4
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
              { a4 = length($4) > 0 ? sprintf("X%048u", $4) : $4;    print $1, $2, $3, a4 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# ROOT_TABLE
FILE_TO_PROCESS=3_59
NUMBER_OF_COLUMNS=5
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
              { a4 = length($4) > 0 ? sprintf("X%048u", $4) : $4;    print $1, $2, $3, a4, $5 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# VIRTUAL_FIELD
FILE_TO_PROCESS=3_60
NUMBER_OF_COLUMNS=12
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
              { a2 = length($2) > 0 ? sprintf("X%048u", $2) : $2;   a3 = length($3) > 0 ? sprintf("X%048u", $3) : $3;   a4 = length($4) > 0 ? sprintf("X%048u", $4) : $4;   a5 = length($5) > 0 ? sprintf("X%048u", $5) : $5;   a10 = length($10) > 0 ? sprintf("X%048u", $10) : $10;   a11 = length($11) > 0 ? sprintf("X%048u", $11) : $11;   print $1, a2, a3, a4, a5, $6, $7, $8, $9, a10, a11, $12 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# VIRTUAL_ROW
FILE_TO_PROCESS=3_61
NUMBER_OF_COLUMNS=2
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
              { a1 = length($1) > 0 ? sprintf("X%048u", $1) : $1;   a2 = length($2) > 0 ? sprintf("X%048u", $2) : $2;   print a1, a2 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# VIRTUAL_TABLE
FILE_TO_PROCESS=3_62
NUMBER_OF_COLUMNS=5
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
              { a4 = length($4) > 0 ? sprintf("X%048u", $4) : $4;   print $1, $2, $3, a4, $5 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# SM_BANNER
FILE_TO_PROCESS=3_37
NUMBER_OF_COLUMNS=2
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a2 = length($2) > 0 ? sprintf("X%048u", $2) : $2;   print $1, a2 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# SM_FOOTER
FILE_TO_PROCESS=3_41
NUMBER_OF_COLUMNS=10
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a9 = length($9) > 0 ? sprintf("X%048u", $9) : $9;   print $1, $2, $3, $4, $5, $6, $7, $8, a9, $10 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# SM_GROUP
FILE_TO_PROCESS=3_42
NUMBER_OF_COLUMNS=9
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
              { a2 = length($2) > 0 ? sprintf("X%048u", $2) : $2;   a5 = length($5) > 0 ? sprintf("X%048u", $5) : $5;   a6 = length($6) > 0 ? sprintf("X%048u", $6) : $6;   a9 = length($9) > 0 ? sprintf("X%048u", $9) : $9;   print $1, a2, $3, $4, a5, a6, $7, $8, a9 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# SM_GROUP_FILE
FILE_TO_PROCESS=3_43
NUMBER_OF_COLUMNS=2
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
              { a1 = length($1) > 0 ? sprintf("X%048u", $1) : $1;   a2 = length($2) > 0 ? sprintf("X%048u", $2) : $2;   print a1, a2 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# SM_DA_MODE_TEMPLATE
FILE_TO_PROCESS=3_38
NUMBER_OF_COLUMNS=2
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a2 = length($2) > 0 ? sprintf("X%048u", $2) : $2;   print $1, a2 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# SM_ERROR_LOG
FILE_TO_PROCESS=3_39
NUMBER_OF_COLUMNS=11
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP   { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN               { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        NF != 11            { print $0 }
        NF == 11            { RS = "\xA7\n"; a7 = length($7) > 0 ? sprintf("X%048u", $7) : $7;   print $1, $2, $3, $4, $5, $6, a7, $8, $9, $10, $11 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# SM_FILE_PASSWORD
FILE_TO_PROCESS=3_40
NUMBER_OF_COLUMNS=4
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
              { a2 = length($2) > 0 ? sprintf("X%048u", $2) : $2;   a4 = length($4) > 0 ? sprintf("X%048u", $4) : $4;   print $1, a2, $3, a4 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# SM_ORG_UNIT
FILE_TO_PROCESS=3_46
NUMBER_OF_COLUMNS=8
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a5 = length($5) > 0 ? sprintf("X%048u", $5) : $5;   a6 = length($6) > 0 ? sprintf("X%048u", $6) : $6;   print $1, $2, $3, $4, a5, a6, $7, $8 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# SM_SECTION
FILE_TO_PROCESS=3_47
NUMBER_OF_COLUMNS=15
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a1 = length($1) > 0 ? sprintf("X%048u", $1) : $1;   a5 = length($5) > 0 ? sprintf("X%048u", $5) : $5;   a7 = length($7) > 0 ? sprintf("X%048u", $7) : $7;   a8 = length($8) > 0 ? sprintf("X%048u", $8) : $8;   a9 = length($9) > 0 ? sprintf("X%048u", $9) : $9;   print a1, $2, $3, $4, a5, $6, a7, a8, a9, $10, $11, $12, $13, $14, $15 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# SM_SECTION_STYLE
FILE_TO_PROCESS=3_48
NUMBER_OF_COLUMNS=19
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a9 = length($9) > 0 ? sprintf("X%048u", $9) : $9;   a10 = length($10) > 0 ? sprintf("X%048u", $10) : $10;   a11 = length($11) > 0 ? sprintf("X%048u", $11) : $11;   a12 = length($12) > 0 ? sprintf("X%048u", $12) : $12;   print $1, $2, $3, $4, $5, $6, $7, $8, a9, a10, a11, a12, $13, $14, $15, $16, $17, $18, $19 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# SM_SITE_FILE
FILE_TO_PROCESS=3_49
NUMBER_OF_COLUMNS=10
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a2 = length($2) > 0 ? sprintf("X%048u", $2) : $2;   a8 = length($8) > 0 ? sprintf("X%048u", $8) : $8;   a9 = length($9) > 0 ? sprintf("X%048u", $9) : $9;   print $1, a2, $3, $4, $5, $6, $7, a8, a9, $10 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# SM_STYLE
FILE_TO_PROCESS=3_50
NUMBER_OF_COLUMNS=11
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a5 = length($5) > 0 ? sprintf("X%048u", $5) : $5;   a6 = length($6) > 0 ? sprintf("X%048u", $6) : $6;   a7 = length($7) > 0 ? sprintf("X%048u", $7) : $7;   a8 = length($8) > 0 ? sprintf("X%048u", $8) : $8;   print $1, $2, $3, $4, a5, a6, a7, a8, $9, $10, $11 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# SM_USAGE_LOG
FILE_TO_PROCESS=3_51
NUMBER_OF_COLUMNS=3
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a2 = length($2) > 0 ? sprintf("X%048u", $2) : $2;   a3 = length($3) > 0 ? sprintf("X%048u", $3) : $3;   print $1, a2, a3 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# SM_USER
FILE_TO_PROCESS=3_52
NUMBER_OF_COLUMNS=4
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a1 = length($1) > 0 ? sprintf("X%048u", $1) : $1;   a4 = length($4) > 0 ? sprintf("X%048u", $4) : $4;   print a1, $2, $3, a4 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# SM_USER_GROUP
FILE_TO_PROCESS=3_53
NUMBER_OF_COLUMNS=2
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a1 = length($1) > 0 ? sprintf("X%048u", $1) : $1;   a2 = length($2) > 0 ? sprintf("X%048u", $2) : $2;   print a1, a2 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# SM_WEBSITE
FILE_TO_PROCESS=3_54
NUMBER_OF_COLUMNS=23
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a8 = length($8) > 0 ? sprintf("X%048u", $8) : $8;   a10 = length($10) > 0 ? sprintf("X%048u", $10) : $10;   a11 = length($11) > 0 ? sprintf("X%048u", $11) : $11;   a19 = length($19) > 0 ? sprintf("X%048u", $19) : $19;   a20 = length($20) > 0 ? sprintf("X%048u", $20) : $20;   print $1, $2, $3, $4, $5, $6, $7, a8, $9, a10, a11, $12, $13, $14, $15, $16, $17, $18, a19, a20, $21, $22, $23 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# SM_SITE_FILE_FOLDER
FILE_TO_PROCESS=3_63
NUMBER_OF_COLUMNS=4
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a2 = length($2) > 0 ? sprintf("X%048u", $2) : $2;   a3 = length($3) > 0 ? sprintf("X%048u", $3) : $3;   a4 = length($4) > 0 ? sprintf("X%048u", $4) : $4;   print $1, a2, a3, a4 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# SM_SECTION_TYPE
FILE_TO_PROCESS=3_64
NUMBER_OF_COLUMNS=6
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a3 = length($3) > 0 ? sprintf("X%048u", $3) : $3;   print $1, $2, a3, $4, $5, $6, $7 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# SM_WEBSITE_TABLE
FILE_TO_PROCESS=3_65
NUMBER_OF_COLUMNS=2
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a1 = length($1) > 0 ? sprintf("X%048u", $1) : $1;   a2 = length($2) > 0 ? sprintf("X%048u", $2) : $2;   print a1, a2 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# SM_VIRTUAL_TABLE
FILE_TO_PROCESS=3_66
NUMBER_OF_COLUMNS=7
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a1 = length($1) > 0 ? sprintf("X%048u", $1) : $1;   a5 = length($5) > 0 ? sprintf("X%048u", $5) : $5;   a7 = length($7) > 0 ? sprintf("X%048u", $7) : $7;   print a1, $2, $3, $4, a5, $6, a7 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# SM_LDAP_BRANCH
FILE_TO_PROCESS=3_67
NUMBER_OF_COLUMNS=8
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a6 = length($6) > 0 ? sprintf("X%048u", $6) : $6;   print $1, $2, $3, $4, $5, a6, $7, $8 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# SM_DA_WEBSITE_GRP_NOTIFICATION
FILE_TO_PROCESS=3_68
NUMBER_OF_COLUMNS=2
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a1 = length($1) > 0 ? sprintf("X%048u", $1) : $1;   a2 = length($2) > 0 ? sprintf("X%048u", $2) : $2;   print a1, a2 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# SM_DA_COLUMN_NOTIFICATION
FILE_TO_PROCESS=3_69
NUMBER_OF_COLUMNS=2
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a1 = length($1) > 0 ? sprintf("X%048u", $1) : $1;   a2 = length($2) > 0 ? sprintf("X%048u", $2) : $2;   print a1, a2 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# SM_WEBSITE_REQUEST
FILE_TO_PROCESS=3_70
NUMBER_OF_COLUMNS=9
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a4 = length($4) > 0 ? sprintf("X%048u", $4) : $4;   a5 = length($5) > 0 ? sprintf("X%048u", $5) : $5;   a8 = length($8) > 0 ? sprintf("X%048u", $8) : $8;   a9 = length($9) > 0 ? sprintf("X%048u", $9) : $9;   print $1, $2, $3, a4, a5, $6, $7, a8, a9 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# SM_CHANGE_LOG
FILE_TO_PROCESS=3_71
NUMBER_OF_COLUMNS=5
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a5 = length($5) > 0 ? sprintf("X%048u", $5) : $5;   a4 = length($4) > 0 ? sprintf("X%048u", $4) : $4;   print $1, $2, $3, a4, a5 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# SM_WEBSITE_CHANGE_LOG
FILE_TO_PROCESS=3_72
NUMBER_OF_COLUMNS=6
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a4 = length($4) > 0 ? sprintf("X%048u", $4) : $4;   a5 = length($5) > 0 ? sprintf("X%048u", $5) : $5;   a6 = length($6) > 0 ? sprintf("X%048u", $6) : $6;   print $1, $2, $3, a4, a5, a6 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# SM_ORG_UNIT_USER
FILE_TO_PROCESS=3_73
NUMBER_OF_COLUMNS=2
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a1 = length($1) > 0 ? sprintf("X%048u", $1) : $1;   a2 = length($2) > 0 ? sprintf("X%048u", $2) : $2;   print a1, a2 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# TASK_STATUS
FILE_TO_PROCESS=3_74
NUMBER_OF_COLUMNS=3
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a3 = length($3) > 0 ? sprintf("X%048u", $3) : $3;   print $1, $2, a3 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# TASK
FILE_TO_PROCESS=3_75
NUMBER_OF_COLUMNS=16
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a5 = length($5) > 0 ? sprintf("X%048u", $5) : $5;   a6 = length($6) > 0 ? sprintf("X%048u", $6) : $6;   a7 = length($7) > 0 ? sprintf("X%048u", $7) : $7;   a8 = length($8) > 0 ? sprintf("X%048u", $8) : $8;   a10 = length($10) > 0 ? sprintf("X%048u", $10) : $10;   a12 = length($12) > 0 ? sprintf("X%048u", $12) : $12;   a13 = length($13) > 0 ? sprintf("X%048u", $13) : $13;   a14 = length($14) > 0 ? sprintf("X%048u", $14) : $14;   a15 = length($15) > 0 ? sprintf("X%048u", $15) : $15;   a16 = length($16) > 0 ? sprintf("X%048u", $16) : $16;   print $1, $2, $3, $4, a5, a6, a7, a8, $9, a10, $11, a12, a13, a14, a15, a16 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# SM_SECTION_CONTRIBUTOR_GROUP
FILE_TO_PROCESS=3_76
NUMBER_OF_COLUMNS=2
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a1 = length($1) > 0 ? sprintf("X%048u", $1) : $1;   a2 = length($2) > 0 ? sprintf("X%048u", $2) : $2;   print a1, a2 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# SM_SECTION_EDITOR_GROUP
FILE_TO_PROCESS=3_77
NUMBER_OF_COLUMNS=2
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a1 = length($1) > 0 ? sprintf("X%048u", $1) : $1;   a2 = length($2) > 0 ? sprintf("X%048u", $2) : $2;   print a1, a2 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# SM_SECTION_VERSION
FILE_TO_PROCESS=3_78
NUMBER_OF_COLUMNS=12
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a6 = length($6) > 0 ? sprintf("X%048u", $6) : $6;   a7 = length($7) > 0 ? sprintf("X%048u", $7) : $7;   a8 = length($8) > 0 ? sprintf("X%048u", $8) : $8;   a9 = length($9) > 0 ? sprintf("X%048u", $9) : $9;   a11 = length($11) > 0 ? sprintf("X%048u", $11) : $11;   print $1, $2, $3, $4, $5, a6, a7, a8, a9, $10, a11, $12 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# TASK_SECTION
FILE_TO_PROCESS=3_79
NUMBER_OF_COLUMNS=2
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a1 = length($1) > 0 ? sprintf("X%048u", $1) : $1;   a2 = length($2) > 0 ? sprintf("X%048u", $2) : $2;   print a1, a2 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new


# TASK_TABLE
FILE_TO_PROCESS=3_80
NUMBER_OF_COLUMNS=2
LINES_TO_SKIP=$(($NUMBER_OF_COLUMNS+2))
$GAWK "NR > $LINES_TO_SKIP { print }" $PATH_TO_SCHEMA/$FILE_TO_PROCESS |
  $GAWK 'BEGIN              { FS = "\xA7\xA7"; RS = "\xA7\n"; OFS = "\xA7\xA7"; ORS = "\xA7\n" }
        /^<>CLOB<>/
        ! /^<>CLOB<>/      { a1 = length($1) > 0 ? sprintf("X%048u", $1) : $1;   a2 = length($2) > 0 ? sprintf("X%048u", $2) : $2;   print a1, a2 } ' > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
head -n $LINES_TO_SKIP $PATH_TO_SCHEMA/$FILE_TO_PROCESS > $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
cat $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new >> $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete
mv $PATH_TO_SCHEMA/$FILE_TO_PROCESS.complete $PATH_TO_SCHEMA/$FILE_TO_PROCESS
rm $PATH_TO_SCHEMA/$FILE_TO_PROCESS.new
