
FROM postgres:11
MAINTAINER Mostafa Albana
EXPOSE 5432:5433


ENV PGDATA /var/lib/postgresql-static/data
VOLUME  ["$PGDATA"]
RUN mkdir -p $PGDATA

RUN apt-get update && \
    apt-get -y install software-properties-common

#Install JDK
RUN apt-get update \
   && apt-get  install -y openjdk-8-jdk


ADD /target/postgresdb-1.0-jar-with-dependencies.jar postgresdb.jar
ENV database_name=ecommerce
ENV schema_name=ecommerce
ENV database_url=jdbc:postgresql://localhost:5432/ecommerce
ENV user_name=ecommerce
ENV user_password=admin123

RUN echo "psql -U postgres --command \"CREATE ROLE $user_name LOGIN PASSWORD '$user_password' SUPERUSER INHERIT CREATEDB CREATEROLE NOREPLICATION;\" && \
          psql -U postgres --command \" CREATE DATABASE $database_name WITH OWNER $user_name ENCODING 'UTF8';\" && \
          pg_ctl -D $PGDATA stop &&  \
          pg_ctl -D $PGDATA start &&  \
          java -jar  postgresdb.jar " > /docker-entrypoint-initdb.d/setup.sh



