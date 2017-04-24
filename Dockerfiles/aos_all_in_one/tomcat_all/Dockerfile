FROM tomcat:8.0-jre8
MAINTAINER Michal Edery

ENV http_proxy="http://proxy.il.hpecorp.net:8080"
ENV https_proxy="http://proxy.il.hpecorp.net:8080"

RUN apt-get update && apt-get -y install openjdk-8-jdk

RUN rm -rf webapps/ROOT
COPY wars.zip webapps
COPY updatewars.sh webapps
COPY startTomcat.sh /
RUN chmod +x /startTomcat.sh
ENTRYPOINT ["sh", "-c", "/startTomcat.sh"]
