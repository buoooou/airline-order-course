FROM node:18 as build

WORKDIR /frontend-app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

FROM nginx:alpine
COPY --from=build /frontend-app/dist /usr/share/nginx/html
