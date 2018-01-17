FROM gradle:4.4.1-jdk8

USER root
RUN apt-get update && apt-get upgrade -y
ADD store .

CMD gradle appStart
