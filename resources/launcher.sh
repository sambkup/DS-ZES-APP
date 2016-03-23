#!/bin/sh

NODES=(alice bob charlie daphnie eric frank george)

##############################
# Modify these parameters:
PWD=/Users/Sammy/Desktop/Launch_Codes
CONFIG_FILE_PATH=config.txt

##############################
# generate launcher files
for i in "${NODES[@]}"; do   
    echo \#\!/bin/sh > launch$i.sh
    echo cd $PWD >> launch$i.sh
    echo echo $i >> launch$i.sh
    echo java -jar lab3.jar $i $CONFIG_FILE_PATH >> launch$i.sh
    chmod +rwx launch$i.sh
done

##############################
# Clean launcher files

for i in "${NODES[@]}"; do   
    echo launching $i
    open -a Terminal launch$i.sh
done

sleep 10
##############################
# Clean launcher files (may not be necessary)

for i in "${NODES[@]}"; do   
    echo removing $i
    rm launch$i.sh
done