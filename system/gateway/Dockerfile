FROM java:8
ENV port = 8765
EXPOSE 8765:8765
ADD /target/proxy-server-1.0.jar proxy.jar
ENTRYPOINT java -jar proxy.jar proxy