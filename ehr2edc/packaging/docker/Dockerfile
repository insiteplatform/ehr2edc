FROM docker-registry.internal.custodix.com/containers/java:master

ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV SPRING_BOOT_OPTS=""
ENV EHR2EDC_JAR_PATH="/app/ehr2edc.jar"

WORKDIR /app
ADD ehr2edc.jar /app/ehr2edc.jar

EXPOSE 8110
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar $EHR2EDC_JAR_PATH $SPRING_BOOT_OPTS" ]
