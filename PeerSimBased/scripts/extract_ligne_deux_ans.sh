#!/bin/bash
if [ $# -lt "1" ]
then
  echo "usage $0 working directory"
fi
cd $1

for i in *.churnPast.txt ; do sed -n "732 p" $i; done > lastlines.txt
