#!/bin/bash

cd src
rm *.class
javac *.java

#numTasksEndIndex=79

for i in {0..79}; do
  echo "Inputting $i as the file to choose..."
  time echo "2 5 $i 2" |& java Main > "$i.txt"
done
