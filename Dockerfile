FROM openjdk:17.0-jdk
ENV TZ "Asia/Ho_Chi_Minh"
RUN rm -rf /var/cache/apk/*
ARG JAR_FILE=out/artifacts/vertx_docker_example_jar/vertx-docker-example.jar
COPY ${JAR_FILE} /usr/app/
WORKDIR /usr/app
EXPOSE 8888
ENTRYPOINT exec java $JAVA_OPTS -jar $JAVA_ARGS vertx-docker-example.jar
