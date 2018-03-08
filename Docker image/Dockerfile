FROM java:8 
COPY Recv.java /
COPY mysqlcon.jar /
COPY amqp-client-4.0.2.jar /
COPY rabbitmq-client.jar /
COPY slf4j-api-1.7.21.jar /
COPY slf4j-simple-1.7.22.jar /
COPY ip_config.properties /
RUN javac -cp mysqlcon.jar:amqp-client-4.0.2.jar:rabbitmq-client.jar:slf4j-api-1.7.21.jar:slf4j-simple-1.7.22.jar Recv.java 
EXPOSE 8080 8081
ENTRYPOINT ["java"]
CMD ["-cp",".:mysqlcon.jar:amqp-client-4.0.2.jar:rabbitmq-client.jar:slf4j-api-1.7.21.jar:slf4j-simple-1.7.22.jar","Recv"]


