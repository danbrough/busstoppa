#!/bin/bash


cd `dirname $0`

adb pull /data/data/danbroid.busapp/databases/busapp.db > /dev/null

exec sqlite3 busapp.db




