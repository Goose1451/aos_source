FROM tomcat:9.0.2-jre8
MAINTAINER Assaf Kubany

ENV http_proxy="http://proxy.il.hpecorp.net:8080"
ENV https_proxy="http://proxy.il.hpecorp.net:8080"

RUN apt-get update && \
    apt-get install -y \
    openjdk-8-jdk \
    postgresql \
    vim

RUN rm -rf webapps/ROOT webapps/docs
COPY wars.zip webapps
COPY updatewars.sh webapps
COPY wait-for-it.sh webapps
COPY startTomcat.sh /
COPY healthcheck.sh /
RUN chmod +x /startTomcat.sh
RUN chmod +x /healthcheck.sh
# unzip wars.zip, create folder for each war and unpack it
RUN cd webapps && unzip "wars.zip" && for i in $(find . -iname "*.war" | cut -d/ -f2 | cut -d. -f1); do echo "mkdir ${i}" && mkdir $i && chmod 777 ${i} && warfile=${i}.war && echo "$warfile" && echo "mv $warfile ${i}" &&  mv $warfile ${i}/ && echo "jar -xf  ${i}/$warfile" && cd ${i}/ && jar -xf $warfile && ls && rm $warfile && cd ../;done
RUN cd webapps && rm "wars.zip"
RUN ls webapps

RUN sed -i -e '$i <role rolename="manager-gui"\/>\n<role rolename="manager-script"\/>\n<user username="tomcatadmin" password="Password1" roles="manager-gui,manager-script"\/>' /usr/local/tomcat/conf/tomcat-users.xml && \
    cat /usr/local/tomcat/conf/tomcat-users.xml

ENV http_proxy=""
ENV https_proxy=""

ENTRYPOINT ["sh", "-c", "/startTomcat.sh"]
HEALTHCHECK --interval=60s --timeout=30s --retries=5 \
CMD /healthcheck.sh
