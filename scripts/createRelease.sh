#!/bin/bash


cd `dirname $0` && cd ..

#arrIN=(${IN//;/ })



VERSION_NAME=$(./gradlew -q projectNextVersionName)

echo Creating release: '<'$VERSION_NAME'>'

while true; do
    read -p "Do you wish to proceed?: " yn
    case $yn in
        [Yy]* ) break;;
        [Nn]* ) exit;;
        * ) echo "Please answer yes or no.";;
    esac
done

if git tag | grep "$VERSION_NAME" > /dev/null; then
  while true; do
    read -p "Existing Tag $VERSION_NAME found. Shall I delete it?: " yn
    case $yn in
        [Yy]* ) break;;
        [Nn]* ) exit;;
        * ) echo "Please answer yes or no.";;
    esac
  done
  echo removing existing tag "$VERSION_NAME"
  git tag -d "$VERSION_NAME"
  git push origin --delete "$VERSION_NAME"
fi


./gradlew assembleDebug || exit 1


#sed -i  README.md  -e 's/Latest version.*/Latest version: '$VERSION_NAME'/g'

./gradlew -q projectIncrementVersion
git add .
git commit -am "$VERSION_NAME"
git tag "$VERSION_NAME" && git push && git push origin "$VERSION_NAME"
./gradlew :app:bundle

#
#sleep 1
#wget "https://jitpack.io/com/github/danbrough/util/${VERSION_NAME}/util-${VERSION_NAME}.jar" -O /tmp/rubbish.jar &
#sleep 5
#BUILD_URL="https://jitpack.io/com/github/danbrough/util/${VERSION_NAME}/build.log"

#echo loading $BUILD_URL
#curl "$BUILD_URL"


