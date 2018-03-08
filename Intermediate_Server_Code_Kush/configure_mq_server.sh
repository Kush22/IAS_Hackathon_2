#!/bin/bash

# Install Erlang for supporting rabbitmq
sudo apt-get install erlang

# Installing the rabbitmq-server
sudo apt-get install rabbitmq-server

# Enable the rabbitmq-server
sudo systemctl enable rabbitmq-server

# Start the server, would be able to satisfy the requests
sudo systemctl start rabbitmq-server

# Enabling the management server so that rabbitmq quesues be visible here
sudo rabbitmq-plugins enable rabbitmq_management

# Adding admin user to the rabbitmq
sudo rabbitmqctl add_user admin admin
sudo rabbitmqctl set_user_tags admin administrator
sudo rabbitmqctl set_permissions -p / admin ".*" ".*" ".*"

echo "======== Rabbit MQ Sever Installed =========="
echo "Visit: http://localhost:15672/#/ to run the web version of RabbitMQ server"