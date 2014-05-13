#!/bin/bash

################################################################################
# author : kim kyeong min
# create : 2014.02.13
# desc   : Mediafarm Server  startup / shutdown
# Usage  : MediafarmServer.sh start/stop   
################################################################################

TODAY=`date "+%Y-%m-%d"`

JAVA_HOME=/usr/lib/jvm/java-7-openjdk-armhf
USER_NAME=root
SERVICE_NAME=pcDuinoServer
SERVICE_HOME=/home/ubuntu/Mediafarm/$SERVICE_NAME
LOG_HOME=$SERVICE_HOME/logs

ID=`whoami | awk '{print $1}'`

if [ $USER_NAME != $ID ]
then
echo "Mediafarm Server $1 Error : User Validateion is failed. This instance \
has been started as \"$ID\", actual script owner is \"$USER_NAME\"." 
exit
fi

if [ $# != 1 ]
then
echo "Mediafarm Server StartUp/Shutdown Error : Please input 'start/stop' \
Usage MediafarmServer.sh start/stop."
exit
fi

PID=`ps -ef|grep java|grep ${SERVICE_NAME}|awk '{print $2}'`

if [ "$PID" != "" -a $1 = "start" ]
then
echo "Mediafarm Server StartUp  Error : \"${SERVICE_NAME}\" is already StartUp!"
exit
fi

if [ "$PID" = "" -a $1 = "stop" ]
then
echo "Mediafarm Server Shutdown Error : \"${SERVICE_NAME}\" is yet to StartUp !"
exit
fi

CLASSPATH=\
$SERVICE_HOME/classes:\
$SERVICE_HOME/lib/log4j-1.2.14.jar:\
$SERVICE_HOME/lib/log4j.properties:\
$SERVICE_HOME/lib/RXTXcomm.jar:\
$SERVICE_HOME/lib/MediafarmBridge.jar:\


PROPERTY=\
-Dlog4j.configuration=file:$SERVICE_HOME/classes/log4j.properties

cd $SERVICE_HOME/classes;

case "$1" in

    start)
    nohup \
    $JAVA_HOME/bin/java \
    -cp $CLASSPATH \
    $PROPERTY \
    com.mediaflow.main.MainClass start >> ${LOG_HOME}/Service_${TODAY}.log 2>&1 &
    exit $?
    ;;

    stop)
    $JAVA_HOME/bin/java \
    -cp $CLASSPATH \
    $PROPERTY \
    com.mediaflow.main.MainClass stop
    exit $?
    ;;

   *)
    echo "MasterPCDuino.sh start/stop"
    exit 1
    ;;
esac
