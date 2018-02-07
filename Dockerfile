FROM java:openjdk-8-alpine

ADD . /home/akka/workspace/mongochain/

RUN apk add --update bash && rm -rf /var/cache/apk/*
RUN apk add --no-cache curl

CMD /home/akka/workspace/mongochain/benchmarks/cluster-setup/start-akka.sh
