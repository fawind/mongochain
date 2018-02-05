#!/bin/bash
# Change this url to the corresponding state server url
RESP=$(curl 139.59.134.185:5000/join)

echo RESP $RESP

COMMUNITY_ID=${RESP%,*}
PRIMARY=${RESP#*,}

echo COMMUNITY_ID $COMMUNITY_ID
echo PRIMARY $PRIMARY

export PRIMARY=$PRIMARY
export COMMUNITY_ID=$COMMUNITY_ID

export STORE_ENV="docker"
export MOCK_IPFS="true"
export FAULT_THRESHOLD="0"
export PUBLISH_HOST="0.0.0.0"
export PUBLISH_PORT="2551"
export SEED_NODE="akka.tcp://consensus-system@139.59.134.185:2551"

/home/akka/workspace/mongochain/store/gradlew -p \
        /home/akka/workspace/mongochain/store buildProduct

/home/akka/workspace/mongochain/store/build/output/store/start.sh | tee service.log
