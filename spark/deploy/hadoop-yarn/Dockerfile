# https://hadoop.apache.org/docs/stable/hadoop-project-dist/hadoop-common/SingleCluster.html

FROM ubuntu:22.04

LABEL org.opencontainers.image.authors="cccuroky@gmail.com"

ENV DEBIAN_FRONTEND=noninteractive

RUN apt-get update -qq \
  && apt-get install -y -qq --no-install-recommends \
    sudo ca-certificates curl git ncdu openssh-server openssh-client \
    openjdk-11-jdk python3

RUN groupadd -g 1000 -o hdfs \
  && useradd -m -u 1000 -g 1000 hdfs \
  && usermod -aG sudo hdfs \
  && echo "hdfs:123456" | chpasswd \
  && mkdir /opt/hadoop /opt/spark \
  && chown hdfs:hdfs -R /opt/hadoop \
  && chown hdfs:hdfs -R /opt/spark \
  && echo '%sudo ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers

USER hdfs

ENV JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
ENV HADOOP_HOME=/opt/hadoop
ENV HADOOP_CONF_DIR=/opt/hadoop/etc/hadoop

# passwordless ssh
RUN ssh-keygen -t rsa -P '' -f ~/.ssh/id_rsa \
  && cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys \
  && chmod 0600 ~/.ssh/authorized_keys

RUN curl -sSL https://dlcdn.apache.org/hadoop/common/hadoop-3.3.4/hadoop-3.3.4.tar.gz \
    | tar -xz --strip-components=1 -C /opt/hadoop

# https://archive.apache.org/dist/spark/spark-3.2.1/spark-3.2.1-bin-hadoop3.2-scala2.13.tgz
RUN curl -sSL https://dlcdn.apache.org/spark/spark-3.3.0/spark-3.3.0-bin-hadoop3-scala2.13.tgz \
    | tar -xz --strip-components=1 -C /opt/spark

COPY conf/ssh.conf /etc/ssh/ssh_config.d/my.conf
COPY conf/sshd.conf /etc/ssh/sshd_config.d/my.conf
COPY conf/hadoop-env.sh /opt/hadoop/etc/hadoop/hadoop-env.sh
COPY conf/yarn-env.sh /opt/hadoop/etc/hadoop/yarn-env.sh
COPY conf/core-site.xml /opt/hadoop/etc/hadoop/core-site.xml
COPY conf/hdfs-site.xml /opt/hadoop/etc/hadoop/hdfs-site.xml
COPY conf/yarn-site.xml /opt/hadoop/etc/hadoop/yarn-site.xml
COPY script/entrypoint.sh /opt/spark/entrypoint.sh

RUN cp /opt/spark/conf/log4j2.properties.template /opt/spark/conf/log4j2.properties

WORKDIR /opt/hadoop
RUN bin/hdfs namenode -format

CMD ["/opt/spark/entrypoint.sh"]
