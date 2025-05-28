ARG MSVC_NAME=msvc-usuario

FROM eclipse-temurin:17-jdk-alpine AS build
ARG MSVC_NAME
WORKDIR /app/$MSVC_NAME

COPY  ./pom.xml /app
COPY ./$MSVC_NAME/.mvn ./.mvn
COPY ./$MSVC_NAME/mvnw .
COPY ./$MSVC_NAME/pom.xml .
 
RUN ./mvnw clean package -Dmaven.test.skip -Dmaven.main.skip -Dspring-boot.repackage.skip && rm -r ./target/

COPY ./$MSVC_NAME/src ./src

RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
ARG MSVC_NAME

WORKDIR /app

COPY --from=build /app/$MSVC_NAME/target/msvc-usuario-0.0.1-SNAPSHOT.jar .
ENV PORT=8001
ENV DB_HOST=mysql8
ENV DB_PORT=3306
ENV DB_NAME=msvc_usuario
ENV DB_USER=root
ENV DB_PASSWORD=admin
EXPOSE ${PORT}

ENTRYPOINT ["java","-jar","msvc-usuario-0.0.1-SNAPSHOT.jar"]
