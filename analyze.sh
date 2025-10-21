#!/bin/bash

cd src

echo "|==========================|"
echo "| File Name | Match Found? |"

for i in {0..79}; do
   count="`grep --count \"MATCH FOUND\" \"$i.txt\"`"
#   echo "COUNT VAR SET = $count"

   if 0<"$count";
     then echo "|  $i.txt  |  N  |";
   else echo "|  $i.txt  |  Y  |";
   fi
done
