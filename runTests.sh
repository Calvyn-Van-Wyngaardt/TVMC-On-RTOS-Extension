#!/bin/bash

cd src
rm *.class
javac *.java

#numTasksEndIndex=79
index=0
for i in {0..9}; do
  for j in {2..24}; do
    echo "Inputting $index as the file to choose..."
    (time echo "2 5 $index 2" | java Main) &> "$i-$j.txt"
    wait
    echo "Process completed. Moving on"
    index=$((index+1))
  done
done
