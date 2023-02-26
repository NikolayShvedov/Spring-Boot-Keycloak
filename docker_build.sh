mvn clean install
docker image build ./ -t keycloak-project
docker run -p 8081:8081 -d --name spring-keycloak-project keycloak-project

# stop 'spring-keycloak-project' container
# docker stop spring-keycloak-project