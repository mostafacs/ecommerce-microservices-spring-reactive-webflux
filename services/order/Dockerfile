FROM java:8
ENV port = 8081
EXPOSE 8081
ADD /target/order-1.0.jar order.jar
ENTRYPOINT ["java", "-jar", "order.jar"]