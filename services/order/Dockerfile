FROM java:8
ENV port = 8081
EXPOSE 8081
ADD /target/order-1.0.jar order.jar
ENTRYPOINT java $EXTRA_JAR_ARGS -jar order.jar