# Builds a docker image from a locally built Maven war. Requires 'mvn package' to have been run beforehand
FROM tomcat:11-jre21
LABEL org.opencontainers.image.vendor="Europeana Foundation" \
      org.opencontainers.image.authors="api@europeana.eu" \
      org.opencontainers.image.documentation="https://pro.europeana.eu/page/apis" \
      org.opencontainers.image.source="https://github.com/europeana/" \
      org.opencontainers.image.licenses="EUPL-1.2"

WORKDIR /usr/local/tomcat/webapps

# Add APM agent
ENV ELASTIC_APM_VERSION 1.55.6
ADD https://repo1.maven.org/maven2/co/elastic/apm/elastic-apm-agent/$ELASTIC_APM_VERSION/elastic-apm-agent-$ELASTIC_APM_VERSION.jar /usr/local/elastic-apm-agent.jar

# Copy unzipped directory so we can mount config files in Kubernetes pod
COPY target/api-template/ ./ROOT/
