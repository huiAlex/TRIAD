#!/bin/bash
function trans_xml(){
	for file in $(ls $1)
	do
		if [ -d $1"/"$file ]
		then
			trans_xml $1"/"$file
		else
			srcml $1"/"$file --position -o $file.xml
		fi

	done
}
 trans_xml $1

