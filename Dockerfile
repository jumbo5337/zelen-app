FROM openjdk:11
LABEL maintainer="GDKharit <gdkharit@electrostal.space>"
ENV HOME_DIR="/opt/zelen-app"
ARG APP_ARTIFACT
ENV APP_ARTIFACT=${APP_ARTIFACT}
COPY target/${APP_ARTIFACT} ${HOME_DIR}/app.jar

EXPOSE 8080
WORKDIR ${HOME_DIR}
ENTRYPOINT ["java", "-jar", "app.jar"]