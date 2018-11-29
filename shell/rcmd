#!/bin/bash

show_usage="args: [-h|--host -c|--cmd]"
host='192.168.88.24'
cmd='ls'

#while [ -n "$1" ]
#do
#	case "$1" in
#		"-h"|"--host") host=$2; shift 2;;
#		"-c"|"--cmd") cmd=$2; shift 2;;
#		*) echo $1,$2,$show_usage; break;;
#	esac
#done

while getopts ":h:c:" opt
do
	case $opt in
		h) host=$OPTARG;;
		c) cmd=$OPTARG;;
		?) echo "unknow args"; echo $show_usage; exit 1;;
	esac
done

ssh $host $cmd
