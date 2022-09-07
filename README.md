# keycloak-security-example

[![MIT License](https://img.shields.io/apm/l/atomic-design-ui.svg?)](https://github.com/tterb/atomic-design-ui/blob/master/LICENSEs) [![Main Branch workflow](https://github.com/wkrzywiec/keycloak-security-example/actions/workflows/main.yaml/badge.svg?branch=main)](https://github.com/wkrzywiec/keycloak-security-example/actions/workflows/main.yaml)

This project is all about example implementation of OAuth 2.0 framework, both in a backend and a frontend application. It contains 3 major services:

* *Keycloak* (authorization server) - open-source tool for identity and access management,
* *backend* (protected resource) - a Java, Spring Boot service that provides a REST API endpoints which requires to provide a valid access token,
* *frontend* (client) - an Angular application that make use of backend's protected REST API endpoints. 

This project was created for learning purposes, if you would like to know more about OAuth 2.0 in general go check my blog posts listed below.

## Prerequisites

To run all necessary applications first you need to install Docker with Docker Compose (for Windows and MacOS it's already bundled with Docker). Instructions can be found on the official website:

* [Ubuntu (Linux)](https://docs.docker.com/install/linux/docker-ce/ubuntu/),
* [Windows](https://docs.docker.com/docker-for-windows/install/),
* [MacOS](https://docs.docker.com/docker-for-mac/install/).

Instructions for installing Docker Compose on Linux can be found [here](https://docs.docker.com/compose/install/).

### Edit hosts file

Apart from installing Docker you also need to edit **hosts** file of your OS.

JWT's payload contains a field **iss** (issuer). It's an URL of an authorization server, in our case Keycloak. In the backend application we need to provide exactly the same URL to the keycloak. But here is the problem that a Docker network and machine's hosts are not the same. From point of view of a backend service a keycloak will have different URL than from point of view of a user! 

To mitigate this problem you need to add following lines to the *hosts* file:
```
127.0.0.1	keycloak
```

Location of *hosts* file on different OS:
* [Linux (Ubuntu)](http://manpages.ubuntu.com/manpages/trusty/man5/hosts.5.html)
* [Windows 10](https://www.groovypost.com/howto/edit-hosts-file-windows-10/)
* [Mac](https://www.imore.com/how-edit-your-macs-hosts-file-and-why-you-would-want#page1)

## Usage

To run all apps just run following command in a terminal

```bash
> docker-compose up -d frontend
```

It will spin up all necessary parts like Keycloak with its database, frontend and backend service. During first run it might take couple of minutes, becasue Docker images needs to be either downloaded or build. Also first startup of all Docker containers might take awhile, especially a Keycloak container, because it not only run it but also it's applying an initial configuration like a predefined Keycloak realm, users, roles and clients. 

To check if everything is working you can list all running containers:

```bash
> docker ps

CONTAINER ID    STATUS          PORTS                              NAMES
1840d7564aeb   Up 46 seconds   0.0.0.0:80->80/tcp                 frontend
cba18013881c   Up 47 seconds   0.0.0.0:9000->9000/tcp             backend
01f15608d210   Up 47 seconds   0.0.0.0:8080->8080/tcp, 8443/tcp   keycloak
ac67959019f9   Up 48 seconds   0.0.0.0:5432->5432/tcp             postgres
```

To check if a Keycloak is good to look at its logs if it contains log ` Admin console listening on http://127.0.0.1:9990`:

```bash
> docker logs keycloak
... other logs

05:56:25,513 INFO  [org.wildfly.extension.undertow] (ServerService Thread Pool -- 62) WFLYUT0021: Registered web context: '/auth' for server 'default-server'
05:56:25,603 INFO  [org.jboss.as.server] (ServerService Thread Pool -- 46) WFLYSRV0010: Deployed "keycloak-server.war" (runtime-name : "keycloak-server.war")
05:56:25,655 INFO  [org.jboss.as.server] (Controller Boot Thread) WFLYSRV0212: Resuming server
05:56:25,658 INFO  [org.jboss.as] (Controller Boot Thread) WFLYSRV0025: Keycloak 11.0.2 (WildFly Core 12.0.3.Final) started in 14383ms - Started 687 of 992 services (703 services are lazy, passive or on-demand)
05:56:25,660 INFO  [org.jboss.as] (Controller Boot Thread) WFLYSRV0060: Http management interface listening on http://127.0.0.1:9990/management
05:56:25,660 INFO  [org.jboss.as] (Controller Boot Thread) WFLYSRV0051: Admin console listening on http://127.0.0.1:9990
```

Once everything is set up you can enter the page `http://localhost` and it will redirect you to the login page. After providing username and password you can playaround with an app.

In default Keycloak configurarion there is one realm defined - `test` that has two users: 

| Username  | Password | Roles   |
| --------- | -------- | ------- |
| luke      | password | VISITOR |
| han       | password | ADMIN   |


To enter the Keyclaok admin page use `http://localhost:8080` url where credentials are `admin` (both for username and password).

### Monitoring 

Apart from key services in the *docker-compose.yaml* file there are defined two monitoring services:

* Prometheus (`http://localhost:5000`),
* Grafana (`http://localhost:3000`).

To run them use on of the following commands:

```bash
# this one will run all services defined docker-compose.yaml
> docker-compose up -d

# this one will run only Prometheus and Grafana
> docker-compose up -d grafana
```

## Articles

* [Introduction to OAuth 2.0](https://medium.com/nerd-for-tech/introduction-to-oauth-2-0-7aa885a3db36)
* [Create and configure Keycloak OAuth 2.0 authorization server](https://wkrzywiec.medium.com/create-and-configure-keycloak-oauth-2-0-authorization-server-f75e2f6f6046)
* [Implementing OAuth 2.0 access token validation with Spring Security](https://wkrzywiec.medium.com/implementing-oauth-2-0-access-token-validation-with-spring-security-64c797b42b36)
* [Step-by-step guide how integrate Keycloak with Angular application](https://wkrzywiec.medium.com/step-by-step-guide-how-integrate-keycloak-with-angular-application-d96b05f7dfdd)
