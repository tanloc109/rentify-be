FROM maven:3.9.9-amazoncorretto-21-alpine AS builder

WORKDIR /app

VOLUME /root/.m2

COPY pom.xml ./

RUN mvn dependency:go-offline

COPY . .

#RUN mvn clean package

#ARG SONAR_PROJECT_KEY=rentify
#ARG SONAR_PROJECT_NAME=rentify
#ARG SONAR_HOST_URL=http://localhost:9000
#ARG SONAR_LOGIN=admin
#ARG SONAR_PASSWORD=12345
#RUN mvn sonar:sonar -Dsonar.projectKey=${SONAR_PROJECT_KEY} -Dsonar.projectName=${SONAR_PROJECT_NAME} -Dsonar.host.url=${SONAR_HOST_URL} -Dsonar.login=${SONAR_LOGIN} -Dsonar.password=${SONAR_PASSWORD}

# Stage 2: Configure WildFly with PostgreSQL driver and datasource
FROM quay.io/wildfly/wildfly:35.0.0.Final-jdk21 as wildfly_builder

RUN /opt/jboss/wildfly/bin/add-user.sh root Aavn123!@# --silent

EXPOSE 8080 9990

ENV DATASOURCE_NAME PostgresDS
ENV DATASOURCE_JNDI java:/PostgresDS
ENV POSTGRESQL_VERSION 42.6.0
ENV JBOSS_HOME /opt/jboss/wildfly
ENV VALID_CONNECTION_CHECKER org.jboss.jca.adapters.jdbc.extensions.postgres.PostgreSQLValidConnectionChecker

# Define database environment variables (these will be overridden by docker-compose)
ARG DB_HOST=jdbc:postgresql://postgres:5432/vaccinedb
ARG DB_USER=postgres
ARG DB_PASS=VaccineX123!@#

# Install PostgreSQL drivers and configure datasource
USER root

RUN /bin/sh -c "$JBOSS_HOME/bin/standalone.sh &" && \
  sleep 10 && \
  mkdir -p /opt/jboss/wildfly/storage && \
  chown jboss:jboss /opt/jboss/wildfly/storage && \
  cd /tmp && \
  wget -O "postgresql-${POSTGRESQL_VERSION}.jar" "http://search.maven.org/remotecontent?filepath=org/postgresql/postgresql/${POSTGRESQL_VERSION}/postgresql-${POSTGRESQL_VERSION}.jar" && \
  $JBOSS_HOME/bin/jboss-cli.sh --connect --command="deploy /tmp/postgresql-${POSTGRESQL_VERSION}.jar" && \
  $JBOSS_HOME/bin/jboss-cli.sh --connect --command="data-source add --name=${DATASOURCE_NAME} --driver-name=postgresql-${POSTGRESQL_VERSION}.jar  --driver-class=org.postgresql.Driver --jndi-name=${DATASOURCE_JNDI} --connection-url=${DB_HOST}  --user-name=${DB_USER} --password=${DB_PASS} --validate-on-match=true --valid-connection-checker-class-name=${VALID_CONNECTION_CHECKER}" && \
  $JBOSS_HOME/bin/jboss-cli.sh --connect --command="/subsystem=logging/root-logger=ROOT:write-attribute(name=level,value=INFO)" && \
  $JBOSS_HOME/bin/jboss-cli.sh --connect --command=:shutdown && \
  rm -rf $JBOSS_HOME/standalone/configuration/standalone_xml_history/ $JBOSS_HOME/standalone/log/* \

# Stage 3: Deploy your WAR file
FROM wildfly_builder as deployer

# Ensure the deployments directory exists before copying the WAR file
RUN mkdir -p $JBOSS_HOME/standalone/deployments/

# Copy the WAR file from the builder stage
COPY --from=builder /app/target/vaccinex.war $JBOSS_HOME/standalone/deployments/

# Define the entry point for WildFly
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]