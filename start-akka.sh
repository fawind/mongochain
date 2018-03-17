#!/bin/bash
# arg1 = ASSIGNMENT_SERVER
# arg2 = SEED_NODE1
# arg3 = SEED_NODE2
if [ $# -eq 1 ]; then
    echo "SEED MODE: First node to join is seed"
elif [ $# -eq 3 ]; then
    echo "SEED MODE: 2 set seed nodes"
    SET_MODE=1
else
    echo "Usage: ./start-akka.sh assignment_server_ip seed_node_1_ip seed_node_2_ip"
    exit 1
fi

echo "Community Assignment Server: $1"
ASSIGNMENT_SERVER=$1

RESP=$(curl ${ASSIGNMENT_SERVER}:5000/join)

echo RESP $RESP

COMMUNITY_ID_AND_IP=${RESP%,*}
PRIMARY_AND_SEED=${RESP#*,}
PRIMARY=${PRIMARY_AND_SEED%-*}
SEED=${PRIMARY_AND_SEED#*-}
COMMUNITY_ID=${COMMUNITY_ID_AND_IP%;*}
IP=${COMMUNITY_ID_AND_IP#*;}

echo COMMUNITY_ID $COMMUNITY_ID
echo PRIMARY $PRIMARY
echo SEED $SEED
echo IP $IP

if [ -n "$SET_MODE" ]; then
    echo "SEED_NODE1=$1; SEED_NODE2=$2"
    SEED1=$1
    SEED2=$2
else
    echo "SEED_NODE1/2=$SEED"
    SEED1=$SEED
    SEED2=$SEED
fi

export PRIMARY=$PRIMARY
export COMMUNITY_ID=$COMMUNITY_ID

export STORE_ENV="docker"
export MOCK_IPFS="true"
export FAULT_THRESHOLD="1"
export PUBLISH_HOST=$IP
export PUBLISH_PORT="2551"
export SEED_NODE1="akka.tcp://consensus-system@$SEED1:2551"
export SEED_NODE2="akka.tcp://consensus-system@$SEED2:2551"

/home/akka/workspace/mongochain/store/build/output/store/start.sh | tee service.log
