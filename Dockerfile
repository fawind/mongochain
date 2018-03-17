FROM java:openjdk-8-alpine

ADD . /home/akka/workspace/mongochain/

RUN apk add --update bash && rm -rf /var/cache/apk/*
RUN apk add --no-cache curl

ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.0.0/wait /wait

RUN chmod +x /wait
RUN chmod +x /home/akka/workspace/mongochain/start-akka.sh

RUN rm -rf /home/akka/workspace/mongochain/store/build
RUN /home/akka/workspace/mongochain/store/gradlew -p \
        /home/akka/workspace/mongochain/store buildProduct

CMD /wait && /home/akka/workspace/mongochain/start-akka.sh community-assignment-server
