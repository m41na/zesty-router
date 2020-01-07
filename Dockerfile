FROM adoptopenjdk/openjdk11:jre-11.0.5_10-alpine

ARG DEPLOY_NAME=demo
ARG DEPLOY_DIR=/usr/local/etc/zesty
ARG ASSETS_DIR=$DEPLOY_DIR/www
ARG DEPLOY_JAR=$DEPLOY_DIR/zesty-router-0.1.1-shaded.jar
ARG HTTP_PORT=7081
ARG HTTPS_PORT=8443
ARG HTTP_HOST=localhost

ENV APP_NAME=$DEPLOY_NAME
ENV APP_JAR=$DEPLOY_JAR

RUN mkdir -p $DEPLOY_DIR
COPY ./target/zesty-router-0.1.1-shaded.jar $DEPLOY_DIR
WORKDIR $DEPLOY_DIR
RUN java -jar ./zesty-router-0.1.1-shaded.jar com.practicaldime.zesty.app.AppLoader -p $HTTP_PORT -h $HTTP_HOST -n $APP_NAME -httpsPort $HTTPS_PORT
