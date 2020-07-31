# kafka-Stream-word-counting
````
counting app using kstream and statestore not KTable(internally as store db)


````

# topics
````
WORD_TOPIC
COUNT_STORE

````

# create Topics

````

kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 2 --partitions 2 --topic WORD_TOPIC --config min.insync.replicas=2

kafka-topics.bat --create --zookeeper localhost:2181 --replication-factor 2 --partitions 2 --topic COUNT_STORE --config min.insync.replicas=2

````

# start zookeeper
````
zookeeper-server-start.bat %KAFKA_HOME%\config\zookeeper.properties

````
# start kafka broker
````
kafka-server-start.bat %KAFKA_HOME%\config\server.properties

kafka-server-start.bat %KAFKA_HOME%\config\server1.properties

````
# produce console message in upstream-message topic
````
kafka-console-producer.bat --bootstrap-server localhost:9092 --topic WORD_TOPIC

kafka-console-producer.bat --broker-list localhost:9092 --topic WORD_TOPIC

````

# List the Topic
````
kafka-topics.bat --list --bootstrap-server localhost:9092
````

# inMemory State store Change log topic
````
the below change log backup topic is created .
when ever service goes down change log topic comes to rescue to restate inmemory 
state store.. 
the fault tolerance

word-count-count-store-changelog
````