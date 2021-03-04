#!/bin/bash

source ~/.metlink


API_URL="https://api.opendata.metlink.org.nz/v1"

api_request(){
	curl -s -X GET  -H "accept: application/json" \
	-H "x-api-key: $METLINK_API_KEY"  "$API_URL/$@" | jq
}

ml_tripupdates(){
	api_request gtfs-rt/tripupdates
}


ml_routes(){
  api_request gtfs/routes
}

ml_feed_info(){
  api_request gtfs/feed_info
}

ml_transfers(){
  api_request gtfs/transfers
}

ml_stops(){
  api_request gtfs/stops
}

ml_vehicle_positions(){
  api_request gtfs-rt/vehiclepositions
}


ml_routes_by_stop(){
  api_request "gtfs/routes?stop_id=$1"
}


ml_stops_by_route(){
  api_request "gtfs/stops?route_id=$1"
}

ml_servicealerts(){
  api_request "gtfs-rt/servicealerts"
}


ml_stop_times(){
  api_request gtfs/stop_times
}