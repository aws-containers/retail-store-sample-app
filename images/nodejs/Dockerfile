# Package Stage
FROM amazonlinux:2

RUN curl -sL https://rpm.nodesource.com/setup_16.x | bash - && \
  yum install -y gcc-c++ make nodejs

WORKDIR /app

# Only copy the package.json file to work directory
COPY package.json .
COPY package-lock.json .
# Install all Packages
RUN npm ci
# Copy all other source code to work directory
ADD . /app
# TypeScript
RUN npm run build

ENTRYPOINT [ "node", "dist/main.js" ]