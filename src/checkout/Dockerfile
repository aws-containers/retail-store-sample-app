# src/checkout/Dockerfile
FROM node:20-alpine AS builder
WORKDIR /app

# copy root workspace
COPY package.json yarn.lock nx.json ./
COPY tsconfig.base.json ./

RUN yarn install --frozen-lockfile

COPY . .
RUN yarn nx build checkout

RUN yarn workspaces focus --production checkout

# Final
FROM node:20-alpine

RUN apk update && apk add --no-cache wget && adduser -D -u 1000 appuser

WORKDIR /app

COPY --from=builder --chown=1000:1000 /app/dist/apps/checkout ./dist
COPY --from=builder --chown=1000:1000 /app/node_modules ./node_modules

USER 1000

EXPOSE 8080

HEALTHCHECK CMD wget -qO- http://localhost:8080/health || exit 1

CMD ["node", "dist/main.js"]