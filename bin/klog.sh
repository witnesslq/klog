#!/usr/bin/env bash
start()
{
        nohup java ${VM_OPTS} -classpath conf:lib/* -server -Xmx512M -Xmx512M com.dafy.klog.consumer.KLogConsumerStartup $1 >nohup.out 2>&1 &
        echo $! > pid
}
stop()
{

        PID=""
        for line in `cat pid `
        do
        PID=${line}
        done
        kill -9 ${PID}
}
if [ $1 = "start" ];then
start
elif [ $1 = "stop" ];then
stop
elif [ $1 = "restart"  ];then
stop
sleep 1
start
echo "Restart OK"
else
echo "$1"
echo "Usage server.sh start|stop|restart"
fi
