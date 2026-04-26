FROM node:20-alpine AS build
WORKDIR /usr/src/app
COPY --chown=node:node package*.json ./
COPY --chown=node:node . .
RUN apk add --no-cache python3 make g++ && \
    YARN_ENABLE_IMMUTABLE_INSTALLS=false yarn install && \
    apk del python3 make g++
RUN yarn build
USER node

FROM node:20-alpine AS prod-deps
WORKDIR /usr/src/app
COPY --chown=node:node package*.json ./
COPY --chown=node:node yarn.lock ./
COPY --chown=node:node .yarn/ .yarn/
COPY --chown=node:node .yarnrc.yml ./
RUN yarn workspaces focus --production --all
USER node

FROM node:20-alpine AS runtime

WORKDIR /app

RUN apk upgrade --no-cache && \
    apk add --no-cache curl && \
    rm -rf /usr/local/lib/node_modules/npm \
           /usr/local/lib/node_modules/corepack \
           /usr/local/bin/npm \
           /usr/local/bin/npx \
           /usr/local/bin/corepack

USER node

COPY --chown=node:node --from=prod-deps /usr/src/app/node_modules ./node_modules
COPY --chown=node:node --from=build /usr/src/app/dist ./dist

EXPOSE 8080

ENTRYPOINT [ "node", "dist/main.js" ]