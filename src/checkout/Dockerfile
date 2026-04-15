# src/checkout/Dockerfile
FROM node:20-alpine AS builder
WORKDIR /app

# enable corepack để dùng yarn v4
RUN corepack enable

COPY package.json yarn.lock .yarnrc.yml ./
COPY .yarn .yarn

RUN yarn install

COPY . .

RUN yarn build

# Final stage
FROM node:20-alpine

RUN apk update && apk add --no-cache wget && adduser -D -u 1000 appuser

WORKDIR /app

COPY --from=builder --chown=1000:1000 /app ./

USER 1000

EXPOSE 8080

HEALTHCHECK CMD wget -qO- http://localhost:8080/health || exit 1

CMD ["node", "dist/main.js"]