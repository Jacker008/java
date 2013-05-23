#!/bin/sh
 
#Set classpath
APP_HOME=$(pwd)
CLASSPATH=""
for i in "$APP_HOME"/lib/*.jar; do
   CLASSPATH="$CLASSPATH":"$i"
done

CLASSPATH="$CLASSPATH":"$APP_HOME"/conf
 
#jvm options
JAVA_OPTS="-Xms512m -Xmx1024m -Djava.awt.headless=true -XX:MaxPermSize=128m -server -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=85 -XX:+DisableExplicitGC -Xnoclassgc -Xverify:none"
MAIN_CLASS="com.wenwo.schedule.main.WenwoScheduleSlaver"

CMD="nohup java $JAVA_OPTS -classpath $CLASSPATH $MAIN_CLASS > /dev/null 2>&1 &"

PID_FILE=../pid-wenwo-schedule-slaver.pid

###################################
#startup
###################################
start() {
   eval "$CMD"
   echo $! > "$PID_FILE"
   echo "Process started."
}
 
###################################
#stop
###################################
stop() {
   kill `cat "$PID_FILE"` > /dev/null 2>&1
   echo "Process stopped"
}
 
###################################
#status
###################################
status() {
   pid=`cat "$PID_FILE"`
   sina_sub_process=`ps -ef | grep "$pid" | grep -v grep | wc -l`
   if [ $sina_sub_process -ne 0 ];  then
      echo "Process is running! (pid=$pid)"
   else
      echo "Process is not running"
   fi
}
 
###################################
 
###################################
#access only 1 argument:{start|stop|restart|status}
###################################
case "$1" in
   'start')
      start
      ;;
   'stop')
     stop
     ;;
   'restart')
     stop
     sleep 3
     start
     ;;
   'status')
     status
     ;;
  *)
     echo "Usage: $0 {start|stop|restart|status}"
     exit 1
esac
exit 0
