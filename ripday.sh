#!/bin/sh
JAVA_HOME=/opt/data/root/jdk1.8.0_121
export JAVA_HOME
PATH=$HOME/bin:$JAVA_HOME/bin:$PATH
export PATH
java -Xmx3024M -cp ripper-1.jar ru.rik.ripper.RipDay "$1"
