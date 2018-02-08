#!/bin/bash
# Change this url to the corresponding state server url
RESP=$(curl 139.59.134.185:5000/join)

echo RESP $RESP

COMMUNITY_ID_AND_IP=${RESP%,*}
PRIMARY=${RESP#*,}
COMMUNITY_ID=${COMMUNITY_ID_AND_IP%;*}
IP=${COMMUNITY_ID_AND_IP#*;}

echo COMMUNITY_ID $COMMUNITY_ID
echo PRIMARY $PRIMARY
echo IP $IP

export PRIMARY=$PRIMARY
export COMMUNITY_ID=$COMMUNITY_ID

export STORE_ENV="docker"
export MOCK_IPFS="true"
export FAULT_THRESHOLD="1"
export PUBLISH_HOST=$IP
export PUBLISH_PORT="2551"
export SEED_NODE1="akka.tcp://consensus-system@139.59.134.185:2551"
export SEED_NODE2="akka.tcp://consensus-system@165.227.153.129:2551"

rm -rf /home/akka/workspace/mongochain/store/build
/home/akka/workspace/mongochain/store/gradlew -p \
        /home/akka/workspace/mongochain/store buildProduct

/home/akka/workspace/mongochain/store/build/output/store/start.sh | tee service.log
