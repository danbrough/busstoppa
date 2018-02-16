#!/usr/bin/env bash


cd `dirname $0`

# download the latest stops
wget https://www.metlink.org.nz/api/v1/StopList/ -O ../app/src/main/assets/wgtn_stops.json








