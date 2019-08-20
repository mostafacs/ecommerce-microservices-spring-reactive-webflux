FROM java:8
ENV port = 8087
EXPOSE 8087:8087
ADD /target/product-1.0.jar product.jar
ENTRYPOINT java -jar product.jar product