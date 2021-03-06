#!/bin/sh
### BEGIN INIT INFO
# Provides:          solarnode
# Required-Start:    $remote_fs $syslog
# Required-Stop:     $remote_fs $syslog
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: SolarNode daemon
# Description:       The SolarNode daemon is for collecting energy related
#                    data and uploading that to the central SolarNet service,
#                    as well as intelligently responding to "smart grid"
#                    events such as load shedding.
### END INIT INFO
# 
# SysV init script for the SolarNode daemon for Eclipse Equinox. Designed
# to be run as an /etc/init.d service by the root user.
#
# chkconfig: 3456 99 01
# description: Control the SolarNode Equinox server
#
# Set JAVA_HOME to the path to your JDK or JRE.
# 
# Set SOLARNODE_HOME to the directory that contains the following:
# 
# + <SOLARNODE_HOME>/
# |
# +--+ conf/                      <-- configuration
# |  |
# |  +-- config.init              <-- main Equinox configuration
# |  +--+ services/               <-- runtime configuration
# |
# +--+ app/                      
#    |
#    +--+ boot/                   <-- OSGi bootstrap bundles
#    +--+ core/                   <-- Core OSGi bundles
#    +--+ main/                   <-- SolarNode OSGi bundles
#
#
# Set PID_FILE to the path to the same path as specified in 
# solarnode.properties for the node.pidfile setting.
# 
# Set RUNAS to the name of the user to run the process as. The script
# will use "su" to run the node as this user, in the background.
# 
# The application is expected to be configured such that the main
# database and log files are stored in an OS-configured RAM disk,
# such as /dev/shm. This script will use rsync when the "stop"
# command is used to sync the DB_DIR contents into DB_BAK_DIR.
# When the "start" command is used, this script checks for the 
# existence of DB_BAK_DIR and if DB_DIR does not exist, will copy
# DB_BAK_DIR to DB_DIR before starting up the application.
# 
# A typical RAM disk hierarchy looks like the following:
# 
# + <RAM_DISK>/
# |
# +--+ db/                        <-- Main database
# +--+ log/                       <-- Application logs
# 
# Modify the APP_ARGS and JVM_ARGS variables as necessary.

JAVA_HOME=/usr
SOLARNODE_HOME=/home/solar
RAM_DIR=/run/shm/solar
RUNAS=solar

TMP_DIR=${RAM_DIR}/tmp
LOG_DIR=${RAM_DIR}/log
DB_DIR=${RAM_DIR}/db
VAR_DIR=${SOLARNODE_HOME}/var
DB_BAK_DIR=${VAR_DIR}/db-bak
EQUINOX_JAR=org.eclipse.osgi-3.9.1.v20140110-1610.jar
EQUINOX_CONF=${RAM_DIR}
EQUINOX_CONSOLE=4202
PID_FILE=${RAM_DIR}/solarnode.pid
APP_ARGS="-Dsn.home=${SOLARNODE_HOME} -Dderby.system.home=${DB_DIR} -Dsolarnetwork.pidfile=${PID_FILE} -Djava.util.logging.config.file=${SOLARNODE_HOME}/conf/jre-logging.properties"
JVM_ARGS="-Xmx64m -Djava.io.tmpdir=${TMP_DIR}"

# NOTE: for Java 6, these flags add these flags per https://bugs.eclipse.org/bugs/show_bug.cgi?id=359535
# JVM_ARGS="-XX:+UnlockDiagnosticVMOptions -XX:+UnsyncloadClass"

# Note: for JMX support, add these flags:
#JVM_ARGS="-Dcom.sun.management.jmxremote"

# NOTE: for debugging support, add these flags:
#JVM_ARGS="-Xdebug -Xnoagent -Xrunjdwp:server=y,transport=dt_socket,address=9142,suspend=n"

# NOTE: this supports Debian JNI, such as RXTX
if [ -d /usr/lib/jni ]; then
	JVM_ARGS="${JVM_ARGS} -Djava.library.path=/usr/lib/jni"
fi

START_CMD="${JAVA_HOME}/bin/java ${JVM_ARGS} ${APP_ARGS} -jar ${SOLARNODE_HOME}/app/${EQUINOX_JAR} -configuration ${EQUINOX_CONF} -console ${EQUINOX_CONSOLE} -clean"
START_SLEEP=14
STOP_TRIES=5

# function to create directory if doesn't already exist
setup_dir () {
	if [ ! -e $1 ]; then
		if [ -z "${RUNAS}" ]; then
			mkdir $1
		else
			su - $RUNAS -c "mkdir -p $1"
		fi
	fi
}

# function to stop process and wait for it to terminate
stop_proc () {
	pid=$1
	count=$2
	while { [ $((count-=1)) -gt 0 ] && kill "$pid" 2>/dev/null; } do
		sleep 1
	done
}

#function to copy the conf/config.ini into EQUINOX_CONF
setup_ini () {
	if [ ! -e "${EQUINOX_CONF}/config.ini" ]; then
		if [ -z "${RUNAS}" ]; then
			cp ${SOLARNODE_HOME}/conf/config.ini ${EQUINOX_CONF}
		else
			su - $RUNAS -c "cp ${SOLARNODE_HOME}/conf/config.ini ${EQUINOX_CONF}"
		fi
	fi
}

# function to start up process
do_start () {
	echo -n "Starting SolarNode server... "
	# Verify ram dir exists; create if necessary
	setup_dir ${RAM_DIR}
	
	# Verify tmp dir exists; create if necessary
	setup_dir ${TMP_DIR}
	
	# Verify log dir exists; create if necessary
	setup_dir ${LOG_DIR}
	
	# Verify var dir exists; create if necessary
	setup_dir ${VAR_DIR}
	
	# Copy config.ini into Equinox configuration dir
	setup_ini
	
	# Check to restore backup database
	if [ ! -e ${DB_DIR} -a -e ${DB_BAK_DIR} ]; then
		echo -n "restoring database... "
		cp -a ${DB_BAK_DIR} ${DB_DIR}
	fi
	
	if [ -z "${RUNAS}" ]; then
		${START_CMD} 1>${LOG_DIR}/stdout.log 2>&1 &
	else
		su - $RUNAS -c "${START_CMD} 1>${LOG_DIR}/stdout.log 2>&1 &"
	fi
	echo -n "sleeping for ${START_SLEEP} seconds to check PID... "
	sleep ${START_SLEEP}
	if [ -e $PID_FILE ]; then
		echo "Running as PID" `cat $PID_FILE`
	else
		echo "SolarNode does not appear to be running."
	fi
}

# function to stop process
do_stop () {
	pid=
	run=
	if [ -e $PID_FILE ]; then
		pid=`cat $PID_FILE`
		run=`ps -o pid= -p $pid`
	fi
	if [ -n "$run" ]; then
		echo -n "Stopping SolarNode $pid... "
		stop_proc $pid $STOP_TRIES
		run=`ps -o pid= -p $pid`
		
		# Backup DB to persistent storage
		if [ -z "$run" -a -e ${DB_DIR} ]; then
			echo -n "syncing database to backup dir... "
			setup_dir ${DB_BAK_DIR}
			rsync -am --delete ${DB_DIR}/* ${DB_BAK_DIR} 1>/dev/null 2>&1
		fi
		echo "done."
	else
		echo "SolarNode does not appear to be running."
	fi
}

# function to check status
do_status () {
	pid=
	run=
	if [ -e $PID_FILE ]; then
		pid=`cat $PID_FILE`
		run=`ps -o pid= -p $pid`
	fi
	if [ -n "$run" ]; then
		echo "SolarNode is running (PID $pid)"
	else
		echo "SolarNode does not appear to be running."
	fi
}

# Parse command line parameters.
case $1 in
	start)
		do_start
		;;

	status)
		do_status
		;;
	
	stop)
		do_stop
		;;

	*)
		# Print help
		echo "Usage: $0 {start|stop|status}" 1>&2
		exit 1
		;;
esac

exit 0

