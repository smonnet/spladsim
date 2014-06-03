#!/bin/bash

if [ $# -lt 1 ]
    then
    echo usage $0 configuration_file
    exit
fi
java -cp bin:lib/jep-2.3.0.jar:lib/lip6peersim-1.0.5.jar:lib/djep-1.0.0.jar  peersim.Simulator $1