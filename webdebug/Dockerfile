
FROM ubuntu:jammy
VOLUME /files
ENV DEBIAN_FRONTEND noninteractive

RUN apt-get update && \
    apt-get install -q -y --no-install-recommends \
    openjdk-17-jre file curl unzip wget iputils-ping locales locales-all && \
    rm -rf /var/lib/apt/lists/* && \
    sed -i '/ru_RU.UTF-8/s/^# //g' /etc/locale.gen && \
    locale-gen

ENV LC_ALL ru_RU.UTF-8
ENV LANG ru_RU.UTF-8
ENV LANGUAGE ru_RU.UTF-8

ENV JAVA_HOME /usr/lib/jvm/java-17-openjdk-amd64

RUN wget -q https://marigostra.ru/persist/apache/apache-tomcat-10.1.25.zip && unzip *.zip && rm -f *.zip && mv apache-tomcat* /tomcat
WORKDIR /tomcat
RUN chmod 755 bin/*.sh
RUN rm -rf webapps/*
COPY build/libs/webdeb.war webapps/ROOT.war

CMD [ "/tomcat/bin/catalina.sh", "run" ]
