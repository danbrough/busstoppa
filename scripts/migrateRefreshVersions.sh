#!/bin/bash


cd `dirname $0` && cd ..
exec ./gradlew  migrateToRefreshVersionsDependenciesConstants --console=plain

