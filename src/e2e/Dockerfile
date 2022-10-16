FROM node:16-buster

RUN apt-get update && \
    DEBIAN_FRONTEND="noninteractive" \
    apt-get install -y --no-install-recommends \
    chromium \
    chromium-driver \
    libatk-bridge2.0-0 \
    libgconf-2-4 \
    libxss1 \
    openjdk-11-jre-headless \
    && rm -rf /var/lib/apt/lists/*
    
WORKDIR /app

RUN groupadd -r app && useradd -r -d /app -g app -G audio,video app \
	&& mkdir -p /app && chown -R app:app /app

USER app

ADD . /app

RUN npm ci

CMD [ "npm", "test" ]