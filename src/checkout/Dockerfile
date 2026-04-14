FROM node:20-alpine AS builder
WORKDIR /app
COPY src/checkout/package*.json ./
RUN npm ci --only=production --frozen-lockfile
COPY src/checkout/ ./

FROM node:20-alpine
RUN apk --no-cache add curl && adduser -D -u 1000 appuser
WORKDIR /app
COPY --from=builder /app ./
USER 1000
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=5s CMD curl -f http://localhost:8080/health || exit 1
CMD ["node", "server.js"]