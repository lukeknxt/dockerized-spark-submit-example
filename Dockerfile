FROM hseeberger/scala-sbt:11.0.12_1.5.5_2.12.14 as build
COPY . /app
WORKDIR /app
RUN apt-get update -y && apt-get install wget -y
ENV JAVA_OPTS="-Xmx2048m"
RUN wget https://apache.mirror.digitalpacific.com.au/spark/spark-3.0.3/spark-3.0.3-bin-hadoop3.2.tgz \
        &&  tar -xzf spark-3.0.3-bin-hadoop3.2.tgz \
        &&  mv spark-3.0.3-bin-hadoop3.2 /opt/spark
RUN sbt package

FROM adoptopenjdk/openjdk11
RUN apt-get update -y && apt-get install tini
WORKDIR /app
ENV SPARK_HOME=/opt/spark
ENV PATH="${SPARK_HOME}/bin:${PATH}"
ENV MASTER="local[*]"
EXPOSE 4040
COPY --from=build /app/target/scala-2.12/*.jar ./
COPY --from=build /opt/spark /opt/spark

ENTRYPOINT ["/usr/bin/tini", "--"]
CMD ["sh", "-c", "spark-submit --master $MASTER /app/scala-spark-docker-test.jar net.lukeknight.Main"]
