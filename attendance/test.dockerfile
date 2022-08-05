FROM gradle:7.4.2-jdk11 AS build
RUN mkdir -p /app
COPY ./ /app
WORKDIR /app
RUN gradle --no-daemon :attendance:build -x test

FROM openjdk:11.0.12-jre
WORKDIR /app
COPY --from=build /app/attendance/build/libs/attendance-0.0.1-all.jar /usr/local/bin
COPY attendance/rie-entry-script.sh /entry_script.sh
COPY attendance/run-jar.sh /usr/local/bin
ADD aws/aws-lambda-rie /usr/local/bin/aws-lambda-rie

ENTRYPOINT [ "/entry_script.sh" ]