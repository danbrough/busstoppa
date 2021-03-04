#!/bin/bash

cd `dirname $0`

ls *.json | while read n ; do
	STATUS=$(cat $n | jq '.departures[].status')
	if [ -z "$STATUS" ]; then
		echo $n has empty status
	fi
done

