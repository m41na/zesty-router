#!/usr/bin/env bash
cp ../router-cli/target/router-cli-0.1.1.jar ./deploy/bin
cd ./deploy

# java9+ -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005
# java8- -Xdebug -Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=y
if [ -n "$GRAALVM_HOME" ]; then
  JAVA_HOME=$GRAALVM_HOME
fi
"$JAVA_HOME"/bin/java -cp .:./bin/router-cli-0.1.1.jar com.practicaldime.zesty.cli.AppLoader
