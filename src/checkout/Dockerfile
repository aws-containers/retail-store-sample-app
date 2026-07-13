FROM node:20-alpine AS build

WORKDIR /build

RUN chown node:node /build

COPY package.json ./

USER  node

RUN npm install

COPY --chown=node:node . .

RUN npm run build

FROM node:20-alpine AS runtime

ENV NODE_ENV=production

WORKDIR /app

RUN chown node:node /app

COPY --chown=node:node package.json ./

USER node

RUN npm install --omit=dev \
    && npm cache clean --force

COPY --chown=node:node \
    --from=build \
    /build/dist \
    ./dist


EXPOSE 8080

ENTRYPOINT ["node", "dist/main.js"]