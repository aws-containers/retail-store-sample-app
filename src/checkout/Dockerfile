# src/checkout/Dockerfile
FROM node:20-alpine AS builder
WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .
RUN npm run build

# remove dev deps
RUN npm prune --production

# Final stage
FROM node:20-alpine

RUN apk update && apk add --no-cache wget && adduser -D -u 1000 appuser

WORKDIR /app

COPY --from=builder --chown=1000:1000 /app/node_modules ./node_modules
COPY --from=builder --chown=1000:1000 /app/dist ./dist

USER 1000

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=5s \
  CMD wget -qO- http://localhost:8080/health || exit 1

CMD ["node", "dist/main.js"]