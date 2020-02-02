FROM adoptopenjdk/openjdk11:jre-11.0.5_10-alpine

ARG APP_NAME=demo
ARG DEPLOY_DIR=/opt/www/zesty
ARG ASSETS_DIR=$DEPLOY_DIR/public
ARG HTTP_PORT=7081
ARG HTTP_HOST=localhost
ARG HTTPS_PORT=8443

ENV APP_NAME=$APP_NAME
ENV DEPLOY_DIR=$DEPLOY_DIR
ENV ASSETS_DIR=$ASSETS_DIR
ENV HTTP_PORT=$HTTP_PORT
ENV HTTP_HOST=$HTTP_HOST
ENV HTTPS_PORT=$HTTPS_PORT

RUN mkdir -p $DEPLOY_DIR/bin
COPY ./target/router-cli-0.1.1.jar $DEPLOY_DIR/bin
WORKDIR $DEPLOY_DIR
RUN java -jar ./bin/router-cli-0.1.1.jar com.practicaldime.zesty.cli.AppLoader -p "$HTTP_PORT" -h "$HTTP_HOST" -n "$APP_NAME" -httpsPort "$HTTPS_PORT" -assets "$DEPLOY_DIR"
