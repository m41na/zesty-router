#!/usr/bin/env bash
JAVA_HOME="$GRAALVM_HOME"
app_jar=zesty-demos-1.0-SNAPSHOT-shaded.jar
rm -rf ./deploy
mkdir -p ./deploy/bin
cp ./target/"$app_jar" ./deploy
cp ../zesty-cli/target/zesty-cli-0.1.1.jar ./deploy/bin
cd ./deploy
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=*:5005 -cp ./bin/zesty-cli-0.1.1.jar com.practicaldime.zesty.cli.AppLoader
