FROM openjdk:8-alpine

COPY target/uberjar/ttd-api-token.jar /ttd-api-token/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/ttd-api-token/app.jar"]
