#!/bin/bash

# Clean previously generated tasksets
cd output/
rm *.txt

# Clean previous results
cd ../src
rm *.txt

# Clean previous input folder
cd ../tasksetInput
rm *.txt

# Create new input
cd ..
python3 tasksetgen.py
wait

# Move new input to correct location
mv ./output/*.txt ./tasksetInput/
