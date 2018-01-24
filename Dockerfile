FROM java:openjdk-8-alpine

ADD ./store/build/output/store .

ADD https://github.com/ufoscout/docker-compose-wait/releases/download/2.0.0/wait /wait
RUN chmod +x /wait
RUN apk add --update bash && rm -rf /var/cache/apk/*

CMD /wait && ./start.sh
