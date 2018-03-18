# Open Decentralized Datastore [![Build Status](https://travis-ci.com/fawind/mongochain.svg?token=RTEhNHKreGSnaC3U1jh2&branch=master)](https://travis-ci.com/fawind/mongochain)

This project is a proof of concept of building a decentralized key-value store on top of [IPFS](https://ipfs.io/). This project was build as part of the course [Building Scalable Blockchain Applications with Big Data Technology](https://hpi.de/naumann/teaching/teaching/ws-1718/building-scalable-blockchain-applications-with-big-data-technology-ps-master.html) at the Hasso Plattner Institute.

The main focus of this project is a consensus algorithms for a network of nodes to agree on a consistent order of inserts transactions. The consensus algorithm is based on [ELASTICO](https://www.comp.nus.edu.sg/~loiluu/papers/elastico.pdf) by Luu et al. (2016) which promises near linear agreement throughput with an increasing node count. For network communication [Akka Cluster](https://doc.akka.io/docs/akka/2.5.5/scala/cluster-usage.html) is used. The results were evaluated using different sized networks with up to 200 nodes.

## Getting Started

### Run locally

For development, you can run the service locally with additional dummy actors using:
```
gradle -p store appRun
```

This requires a running instance of IPFS. To run the service with a mocked IPFS instance, run:
```
MOCK_IPFS="true" gradle -p store appRun
```

You can interact with the store through the REST-API as follows:

* Insert a new value: [localhost:9090/store/api/store/my-namespace/my-key/my-value](http://localhost:9090/store/api/store/my-namespace/my-key/my-value)
* Get the value for a key: [localhost:9090/store/api/store/my-namespace/my-key](http://localhost:9090/store/api/store/my-namespace/my-key)
* Inspect the index: [localhost:9090/store/api/store/my-namespace/index](http://localhost:9090/store/api/store/my-namespace/index)

### Run using Docker

1. Set the `COMMUNITY_COUNT` variable in `community-assigment-server/communityAssignmentService.py` to the amount of communities you want to have.
2. Build the docker-compose setup with running `docker-compose -f docker-compose.yml build` in the root directory
3. Start multiple nodes with `docker-compose -f docker-compose.yml up --scale store=<number_of_nodes>`

## Architecture

A store node consists of two major components, the **Datastore Service** and the **Consensus Service**. The Datastore Service handles incoming transactions and manages the in-memory index of the keys as well as the Transaction Log. The Consensus Service communicates with the network and takes part in the consensus rounds.

<p align="center">
  <img src="https://user-images.githubusercontent.com/7422050/37556907-f520788e-29fc-11e8-94cc-e8fabea1ad2f.png" width="700" alt="Architecture"/>
</p>

### Datastore Service

The Datastore Service manages the high-level interactions. It handles incoming read and write-requests, interacts with IPFS and forwards new transactions to the Consensus Service. The Datastore Service maintains an index containing the mapping of keys to IPFS content hashes, which are required to address the values stored in IPFS. Besides that, it manages the Transaction Log. The Transaction Log contains the immutable ordered list of all write-transactions. Because all transactions go through a consensus round, the Transaction Log is eventual consistent on all nodes in the network.

### Consensus Service

The Consensus Service starts and takes part in consensus rounds. For network communication we use Akka Cluster together with their PubSub implementation. Once a previous consensus round is finished, the Consensus Service pulls the next pending transaction from the Transaction Backlog and starts a new consensus round. The consensus algorithm is based on a simplified version of [ELASTICO](https://www.comp.nus.edu.sg/~loiluu/papers/elastico.pdf) using [PBFT](http://pmg.csail.mit.edu/papers/osdi99.pdf) internally. Due to the scope of this seminar, a couple of simplifications have been made such as leaving out dynamic community formation, identity management, and cryptographic protocols. Once the network agrees on the next transaction, the Consensus Service notifies the Datastore Service which integrates the new transaction in its Transaction Backlog and local index.

### Data Flow

#### Adding a key-value pair

When adding a new key-value pair to a node, the following steps are done:

1. The Datastore Service adds the value to IPFS and retrieves its content hash for addressing the value.
2. The tuple of the key and content hash of the value represents a new pending transaction which is added to the Transaction Backlog.
3. The Consensus Service pulls a pending transaction from the Transaction Backlog and initiates a new consensus round with the transaction. Based on the ELASTICO agreement protocol, a two-round consensus is done. First, the local community of the node agrees on a next transaction within the local community. Based on this result, the final community agrees on the next transaction of all transactions proposed by the local communities.
4. Once the network reaches consensus on the next transaction, the Consensus Service notifies the Datastore Service. The Datastore Service integrates the key into its local index and adds the transaction to the Transaction Log.

#### Getting the value for a key

When reading a key, that reached consistency in the network, the node only has to look up the respective content hash from its index. Using this content hash, it can return the value stored in IPFS. Note that in order to guarantee consistent reads, further measures such as *quorum reads* have to be implemented.

## Experiments

### Community Assignment Server

For our experiments, the Community Assignment Server simplifies the dynamic community assignment by registering new nodes joining the cluster and statically assigning a community number to each node.

Following http GET routes are supported:
- `/join` Get a community assignment
- `/members` List all registered nodes and their assignment
- `/members/count` Count the current registered nodes
- `/clear` Clear the current state of the server

### Setup

For our experiments, we initially set up two seed-nodes. Next, we dynamically added further nodes for the different experiment setups. Nodes can be deployed on various instances including container engines. In our case, we used [DigitalOcean](https://www.digitalocean.com) with one `s-1vcpu-1gb` instance for each store node. After all nodes have joined the network, we added new random key-value pairs to all nodes in the network using round-robin scheduling. Finally, the start and end events of the consensus rounds for each transaction are pulled from the logs of all nodes. The duration of a consensus round is defined by the difference between the timestamp of the event starting the consensus round and the latest timestamp of a node agreeing on this transaction.

### Results

The following table shows some of the results based on the experiment setup described before. For these experiments, the nodes are distributed equally between the communities.

| Description               | Median  | Mean |
| -------------------------:| -------:| ----:|
| 5 Nodes, 1 Community      |   0.35  | 0.5  |
| 25 Nodes, 2 Communities   |   0.59  | 0.89 |
| 100 Nodes, 25 Communities |   3.04  | 9.49 |
| 200 Nodes, 50 Communities |   17.89 | 45.64|

<p align="center">
  <img width="700" alt="blockchain_scale" src="https://user-images.githubusercontent.com/7405553/37558309-38126494-2a12-11e8-8fb5-cc51553675d9.png">
</p>

The chart shows that there are differences in growth between the median and mean. These differences might indicate an increasing network overhead and its resulting outliers of consensus durations.

More details on all experiments and the raw log files can be found [here](https://github.com/fawind/mongochain/wiki/Experiment-Results).

## Future Work

- Implement proof of work for joining a community preventing malicious user controlling too many nodes
- Add recovery mechanisms for nodes rejoining the cluster
- Integrate the assignment service within the nodes (maybe a similar approach as used in ELASTICO)
- Create dedicated Akka PubSub Clusters for every community to reduce messaging and cluster setup time
