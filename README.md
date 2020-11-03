# keycloak-security-example


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