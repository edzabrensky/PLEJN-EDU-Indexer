#!/bin/bash
FILE=Crawler.java
if [ "$#" -ne 3 ]; then
	echo "Need 3 parameters <seed of .edu addresses> <Amount of data to collect in bytes> <out-put-dir>"
else
	if test -f "$FILE"
	then
	    echo "$FILE exists"
	    javac $FILE
	    java Crawler $1 $2 $3
	else
		echo "Crawler.java does not exist!"
	fi
fi