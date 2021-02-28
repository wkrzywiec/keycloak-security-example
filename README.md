# keycloak-security-example

[![MIT License](https://img.shields.io/apm/l/atomic-design-ui.svg?)](https://github.com/tterb/atomic-design-ui/blob/master/LICENSEs) [![Main Branch workflow](https://github.com/wkrzywiec/keycloak-security-example/actions/workflows/main.yaml/badge.svg?branch=main)](https://github.com/wkrzywiec/keycloak-security-example/actions/workflows/main.yaml)

### Edit hosts file
JWT's payload contains a field **iss** (issuer). It's an URL of an authorization server, in our case Keycloak. In the backend application we need to provide exactly the same URL to the keycloak. But here is the problem that a Docker network and machine's hosts are not the same. From point of view of a backend service a keycloak will have different URL than from point of view of a user! 

To mitigate this problem you need to add following lines to the *hosts* file:
```
127.0.0.1	keycloak
```

Location of *hosts* file on different OS:
* [Linux (Ubuntu)](http://manpages.ubuntu.com/manpages/trusty/man5/hosts.5.html)
* [Windows 10](https://www.groovypost.com/howto/edit-hosts-file-windows-10/)
* [Mac](https://www.imore.com/how-edit-your-macs-hosts-file-and-why-you-would-want#page1)