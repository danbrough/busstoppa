#!/usr/bin/env bash

pretty_print(){
  python3 -c "import sys,json;print(json.dumps(json.load(sys.stdin),indent='\t'))"
}

TEMPFILE=`mktemp --suffix=_stops`
DEST="$1"


STOPS_URL="https://www.metlink.org.nz/api/v1/StopList/"
curl $STOPS_URL 2> /dev/null  | pretty_print > $TEMPFILE || exit 1

if [ ! -f "$DEST" ]; then
  echo $DEST doesnt exist .. writing new file
  mv $TEMPFILE "$DEST"
  SUCCESS=$?
fi











