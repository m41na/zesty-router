#!/usr/bin/env bash
#start database server
#java -cp /c/Tools/h2/bin/h2-1.4.197.jar org.h2.tools.Server -tcp -tcpAllowOthers -tcpPort 9095
#or (windows-style)
#java -cp c:\Tools\h2\bin\h2-1.4.197.jar org.h2.tools.Server -tcp -tcpAllowOthers -tcpPort 9095
#start database UI client
#java -jar /c/Tools/h2/bin/h2-1.4.197.jar
#java -jar c:\Tools\h2\bin\h2-1.4.197.jar
#start demo server instance
#java -cp "target\zesty-router-0.1.1-shaded.jar;target\test-classes" com.practicaldime.router.core.demo.DemoApp 8090
#start demo proxy server
#java -cp "target\zesty-router-0.1.1-shaded.jar;target\test-classes" com.practicaldime.router.core.demo.Simproxy
#start consul agent
#consul agent -dev -node [node_name]
#view cluster members
#consul members
#view cluster members using http request
#curl localhost:8500/v1/catalog/nodes
#view cluster members using dns interface
#dig @127.0.0.1 -p 8600 [node_name].node.consul
#stop consul agent
#consul leave
#registering services manually (mkdir ./consul.d)
#{"service":
#  {"name": "instance1",
#   "tags": ["jetty"],
#   "port": 80
#  }
#}
#consul agent -dev -node [node_name] -data-dir=/c/tmp/consul -enable-script-checks -config-dir=/c/tmp/consul.d
#querying services using dns interface
#dig @127.0.0.1 -p 8600 [tag_name].[service_name].service.consul (tag_name is optional)
#querying services using http request
#curl http://localhost:8500/v1/catalog/service/[node_name]
#update node to include health check
#{"service":
#  {"name": "instance1",
#    "tags": ["jetty"],
#    "port": 80,
#    "check": {
#      "args": ["curl", "localhost"],
#      "interval": "10s"
#    }
#  }
#}
#reload configuration without downtime
#consul reload
#querying services using http request and apply health check filter
#curl 'http://localhost:8500/v1/health/service/[node_name]?passing'

echo
set -e
echo checking consul essentials

CONSUL_DATA_DIR=/c/tmp/consul
CONSUL_CONFIG_DIR=/c/tmp/consul.d

if [ -d $CONSUL_DATA_DIR ]; then
  echo "-> consul data dir found"
else
  echo "you need to configure a data directory for consul"
  exit 1
fi

if [ -d $CONSUL_DATA_DIR ]; then
  echo "-> consul config dir found"
else
  echo "you need to configure a config directory for consul"
  exit 1
fi

echo "ready to start consul agent"
consul agent -dev -node machine
#consul agent -server -bootstrap -data-dir $CONSUL_DATA_DIR
