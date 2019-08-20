FROM java:8
ENV port = 8087
EXPOSE 8087:8087
ADD /target/authentication-1.0.jar authentication.jar
ENTRYPOINT java -jar authentication.jar authentication