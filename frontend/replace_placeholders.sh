#!/bin/bash

envsubst < /usr/share/nginx/html/assets/config/config.prod.json > /usr/share/nginx/html/assets/config/config.json
envsubst "\$BACKEND_BASE_PATH" < /temp/default.conf > /etc/nginx/conf.d/default.conf

exec "$@"