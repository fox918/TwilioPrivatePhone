FROM openjdk:11-jdk
EXPOSE 8080:8080
RUN mkdir /app
COPY ./build/install/TwilioSimplePhone/ /app/
COPY ./custom.properties /app/bin/custom.properties
WORKDIR /app/bin
CMD ["./TwilioSimplePhone"]