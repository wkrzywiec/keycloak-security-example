-- keycloak
CREATE USER keycloak WITH ENCRYPTED PASSWORD 'keycloak';
CREATE DATABASE keycloak;
GRANT ALL PRIVILEGES ON DATABASE keycloak TO keycloak;
