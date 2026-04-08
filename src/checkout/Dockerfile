FROM node:20-alpine AS build
WORKDIR /usr/src/app
COPY --chown=node:node package*.json ./
COPY --chown=node:node . .
RUN apk add --no-cache python3 make g++ && \
    yarn install --frozen-lockfile && \
    apk del python3 make g++
RUN yarn build
USER node

FROM node:20-alpine AS runtime

WORKDIR /app

RUN apk add --no-cache curl

USER node

COPY --chown=node:node --from=build /usr/src/app/node_modules ./node_modules
COPY --chown=node:node --from=build /usr/src/app/dist ./dist

EXPOSE 8080

ENTRYPOINT [ "node", "dist/main.js" ]