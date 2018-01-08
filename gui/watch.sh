#!/bin/bash

isOSX=0

while getopts :x opt; do
    case $opt in
        x) isOSX=1;;
    esac
done

find-js() {
    if [ $isOSX == 1 ]
    then
	    gfind ./src \( -name '*.js' -o -name '*.jsx' \) -printf "%p-%t\n"
    else
        find ./src \( -name '*.js' -o -name '*.jsx' \) -printf "%p-%t\n"
    fi
}
find-less() {
    if [ $isOSX == 1 ]
    then
	    gfind ./src \( -name '*.less' -o -name '*.css' \) -printf "%p-%t\n"
    else
        find ./src \( -name '*.less' -o -name '*.css' \) -printf "%p-%t\n"
    fi
}
build-js() {
	npm run build-js
}
build-less() {
	npm run build-less
}

FJS=$(find-js)
FLESS=$(find-less)

while true
do
	NJS=$(find-js)
	if [[ $FJS != $NJS ]]
	then
		echo '------ JS' $(date '+%Y-%m-%d %H:%M:%S') ...
		build-js
		echo '------'
		FJS=$NJS
	fi

	NLESS=$(find-less)
	if [[ $FLESS != $NLESS ]]
	then
		echo '------ LESS' $(date '+%Y-%m-%d %H:%M:%S') ...
		build-less
		echo '------'
		FLESS=$NLESS
	fi

	sleep 1
done
