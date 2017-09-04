#!/bin/bash

echo $ANDROID_HOME
ls -la ${ANDROID_HOME}/tools/bin/


SWARMER_VERSION=0.2.0
SWARMER_JAR=/tmp/swarmer.jar

curl --fail --location https://jcenter.bintray.com/com/gojuno/swarmer/swarmer/${SWARMER_VERSION}/swarmer-${SWARMER_VERSION}.jar --output ${SWARMER_JAR}
$ANDROID_HOME/tools/bin/sdkmanager --update
$ANDROID_HOME/tools/bin/sdkmanager "system-images;android-25;google_apis;x86_64"


java -jar ${SWARMER_JAR} start \
--emulator-name test_emulator_1 \
--package "system-images;android-25;google_apis;x86_64" \
--android-abi google_apis/x86_64 \
--path-to-config-ini .buildscript/swarmer/emulator1-config.ini \
--emulator-start-options -prop persist.sys.language=en -prop persist.sys.country=US \
--redirect-logcat-to test_emulator_1_logcat.txt \
--emulator-start-options  -verbose --no-window \
--emulator-start-timeout-seconds 180
