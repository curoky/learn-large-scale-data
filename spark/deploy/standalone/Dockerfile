# https://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-common/SingleCluster.html

FROM ubuntu:22.04

LABEL org.opencontainers.image.authors="cccuroky@gmail.com"

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update -qq \
  && apt-get install -y -qq --no-install-recommends \
    sudo ca-certificates curl git ncdu openssh-server openssh-client \
    openjdk-11-jdk

RUN groupadd -g 1000 -o hdfs \
  && useradd -m -u 1000 -g 1000 hdfs \
  && usermod -aG sudo hdfs \
  && echo "hdfs:123456" | chpasswd \
  && mkdir /opt/spark \
  && chown hdfs:hdfs -R /opt/spark \
  && echo '%sudo ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers

USER hdfs
WORKDIR /opt/spark

ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64

# https://archive.apache.org/dist/spark/spark-3.2.1/spark-3.2.1-bin-hadoop3.2-scala2.13.tgz
RUN curl -sSL https://dlcdn.apache.org/spark/spark-3.3.0/spark-3.3.0-bin-hadoop3-scala2.13.tgz \
    | tar -xz --strip-components=1 -C /opt/spark

COPY script/entrypoint.sh /opt/spark/entrypoint.sh

RUN cp /opt/spark/conf/log4j2.properties.template /opt/spark/conf/log4j2.properties

CMD ["/opt/spark/entrypoint.sh"]
