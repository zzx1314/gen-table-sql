#!/bin/bash
source /etc/profile
BUILD_ID=dontKillMe
# create by neo
SERVER_HOME=$(dirname $(cd `dirname $0`; pwd))
# 日志路径
LOGBACK_LOGPATH=${SERVER_HOME}/logs
# 配置路径
CONFPATH=${SERVER_HOME}/conf
# java 配置参数文件
JVMFILE=${CONFPATH}/jvm.conf
#服务名
APPNAME=${project.name}-${project.version}
# jar路径
RUNJAR=${SERVER_HOME}/lib/${APPNAME}.jar
#logback 日志参数名称
PROPERTIES_LOGBACK_LOGPATH=log.path
#nacos客户端日志路径配置
JM_LOG_KEY=JM.LOG.PATH
HOME_LOG_KEY=user.home
#run!
isStart="false"
isRunning="0"
# 创建日志目录
if [ ! -d ${LOGBACK_LOGPATH} ];then
    mkdir -p ${LOGBACK_LOGPATH}
fi

# 启动
start() {

    # 读取java配置参数
    if [ -f ${JVMFILE} ];then
            jvmParam=$(sed ':a ; N;s/\n/ / ; t a ; ' ${JVMFILE})
            if [ -z "$jvmParam" ];then
                    jvmParam="-Xms256m -Xmx1024m"
            fi
    else
            jvmParam="-Xms256m -Xmx1024m"
    fi

    while ps -ef | grep ${APPNAME} | grep java | grep -v grep > /dev/null
    do
        isStart="true"
        isRunning="1"
        break
    done
    if [ ${isStart} = "false" ]
    then
        echo "Start ${APPNAME} program..."
        java  ${jvmParam} -Xbootclasspath/a:${CONFPATH} -D${PROPERTIES_LOGBACK_LOGPATH}=${LOGBACK_LOGPATH} -D${JM_LOG_KEY}=${LOGBACK_LOGPATH} -D${HOME_LOG_KEY}=${LOGBACK_LOGPATH} -Djava.util.logging.config.file=${CONFPATH}/logging.propertie -jar ${RUNJAR} 1>> /dev/null 2>&1 &
    else
        echo "${APPNAME} is already running..."
    fi
}

#停止
stop() {
    for PID in $(ps -ef | grep ${APPNAME} | grep java | grep -v grep | awk '{ print $2 }'); do
            echo ${PID}
            kill -9 ${PID}
            echo ${APPNAME} $PID were killed!
    done
}

case "$1" in

    start)
        start;;

    stop)
        stop;;

    restart)
        "$0" "stop"
        sleep 3
        "$0" "start"
        ;;
    *)
        echo "Please use start or stop or restart as first argument"
        ;;
esac

