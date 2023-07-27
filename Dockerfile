FROM eclipse-temurin:17
COPY build/libs/*.jar app.jar
ENV PDF_DIRECTORY /usr/src/app/invoices
RUN mkdir -p $PDF_DIRECTORY
ENTRYPOINT ["java","-jar","/app.jar"]