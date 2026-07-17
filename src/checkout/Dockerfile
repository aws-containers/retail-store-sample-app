FROM node:20-alpine AS build

WORKDIR /build

COPY package.json ./

RUN npm install

COPY . .

RUN npm run build

FROM node:20-alpine AS runtime

ENV NODE_ENV=production

WORKDIR /app

RUN chown node:node /app

COPY --chown=node:node package.json ./



RUN npm install --omit=dev \
    && npm cache clean --force\
    && apk add --no-cache \
        curl \
        ca-certificates

COPY --chown=node:node \
    --from=build \
    /build/dist \
    ./dist
USER node

EXPOSE 8080

ENTRYPOINT ["node", "dist/main.js"]