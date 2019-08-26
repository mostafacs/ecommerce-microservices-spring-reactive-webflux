FROM java:8
ENV port = 8080
EXPOSE 8080:8080
ADD /target/product-1.0.jar product.jar
ENTRYPOINT java $EXTRA_JAR_ARGS -jar product.jar product