FROM node:24-alpine AS build
WORKDIR /build
COPY package.json yarn.lock .yarnrc.yml ./
COPY .yarn ./.yarn
RUN node .yarn/releases/yarn-4.11.0.cjs install --immutable
COPY . .
RUN node .yarn/releases/yarn-4.11.0.cjs build

FROM node:24-alpine AS prod-deps
WORKDIR /build
COPY --from=build /build/package.json /build/yarn.lock /build/.yarnrc.yml ./
COPY --from=build /build/.yarn ./.yarn
RUN node .yarn/releases/yarn-4.11.0.cjs workspaces focus --production

FROM node:24-alpine
LABEL org.opencontainers.image.source="https://github.com/yaboulhouda/retail-store-sample-app"
LABEL org.opencontainers.image.title="checkout"
LABEL org.opencontainers.image.description="Checkout service - retail store sample app"
ENV APPUSER=node
RUN apk --no-cache add ca-certificates
WORKDIR /app
USER $APPUSER
COPY --chown=$APPUSER:$APPUSER --from=build /build/dist ./dist
COPY --chown=$APPUSER:$APPUSER --from=build /build/package.json ./
COPY --chown=$APPUSER:$APPUSER --from=prod-deps /build/node_modules ./node_modules
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/health || exit 1

ENTRYPOINT ["node", "dist/main.js"]