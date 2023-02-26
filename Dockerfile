#
# Build stage
#
FROM public.ecr.aws/docker/library/maven:3.8.6-amazoncorretto-17 AS build

COPY /src /home/app/src
COPY /pom.xml /home/app

RUN mvn -f /home/app/pom.xml clean install

#
# Package stage
#
FROM amazoncorretto:11-alpine-jdk

COPY --from=build /home/app/target/keycloak-project-1.0-SNAPSHOT.jar /usr/local/lib/keycloak-project.jar

ENTRYPOINT ["java","-jar","/usr/local/lib/keycloak-project.jar"]