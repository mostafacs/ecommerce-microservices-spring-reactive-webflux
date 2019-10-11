FROM java:8
ENV port = 8000
EXPOSE 8000:8000
ADD /target/monitoring-1.0.jar monitoring.jar
ENTRYPOINT java -jar monitoring.jar monitoring