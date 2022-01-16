### STAGE 1: Build ###
FROM node:16.13-alpine AS build
WORKDIR /usr/src/app
COPY package-lock.json ./
COPY package.json ./
RUN npm ci
COPY . .
RUN npm run build

### STAGE 2: Run ###
FROM nginx:1.21.5-alpine
COPY default.conf /temp/default.conf
COPY replace_placeholders.sh /
COPY --from=build /usr/src/app/dist/frontend /usr/share/nginx/html
EXPOSE 80
ENTRYPOINT [ "sh", "/replace_placeholders.sh" ]
CMD ["nginx",  "-g", "daemon off;"]