FROM daocloud.io/java:8
VOLUME /tmp
ADD cloud-eureka-server-1.0.0.jar /app.jar

EXPOSE 8761

RUN bash -c 'touch /app.jar'

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]