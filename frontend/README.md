# AirlineOrderFrontend

This project was generated using [Angular CLI](https://github.com/angular/angular-cli) version 20.1.2.

## Development server

# install dependencies
npm install

# generate a new component （pages/login）
npx ng generate component pages/login

# Method 1: Using npm start
npm start

# Method 2: Using npx
npx ng serve

http://localhost:4200/

# help
npx ng generate --help

## Building
npx ng build

This will compile your project and store the build artifacts in the `dist/` directory. By default, the production build optimizes your application for performance and speed.

## Running unit tests
npx ng test

To execute unit tests with the [Karma](https://karma-runner.github.io) test runner, use the following command:

## Running end-to-end tests
npx ng e2e

## Additional Resources
https://angular.dev/tools/cli

# 运行Dockerfile
docker build -t aws-airline-frontend .

# 生成aws镜像
docker tag aws-airline-frontend:latest 381492153714.dkr.ecr.ap-southeast-2.amazonaws.com/airline-order-frontend-sfm:V1

# 推送镜像到aws
docker push 381492153714.dkr.ecr.ap-southeast-2.amazonaws.com/airline-order-frontend-sfm:V1 