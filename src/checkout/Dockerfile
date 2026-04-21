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

RUN apk update && apk add --no-cache wget 

WORKDIR /app

COPY --from=builder /app ./

USER node

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s --start-period=50s CMD wget -qO- http://localhost:8080/api || exit 1

CMD ["node", "dist/main.js"]