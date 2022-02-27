# Spring Boot with Kafka Streams for SMS Delivery Filtering

This is a pilot repo is utilizing Spring Boot 2.5.7 and Kafka broker for the use case of *SMS Delivery Filtering*.

If you are an action first words later person or you prefer the Prof of Concept before the Analysis, your next stop
is going to be section [Getting Started](#gs) in order to make this application run locally on your machine and then
return to the documentation for more enlightening details.

In the scopes of this pilot is to dive into the Spring Cloud, Kafka Streams Binder, and Brokers Messaging with Spring 
Boot and Java. Secondly, to be used as a testbed for any forthcoming Spring Boot updates, to build a use case, which 
will be used as a working example app or a starting point for any further plannings and implementations, and finally, 
investigate and adapt a non blocking function-driven-approach for the REST API producer's calls.

## Introduction

While explaining the following activity diagram, a *Sender* sends a request to the web API to post an SMS rule, which 
will be used later for sms filtering. Moreover, a *Sender* sends a request to a web API to post a message Body to a 
*Receiver*. The API call triggers the *Producer* to send an *Action* object containing the SMS to kafka brokers, 
during the *BiFunction* application the SMS rules are going to be used in KTables format from Kafka Streams Binder 
to filter the incoming messages to Kafka broker,and finally the *Consumer* reduces from Kafka the action to delegate 
the SMS. The delegation, for the time being, is just a log prompt on the consumer.  

For more details about Spring Kafka Streams, you can visit 
[Spring Kafka Steams and Spring Cloud Streams](https://spring.io/blog/2018/04/19/kafka-streams-and-spring-cloud-stream).

![Activity Diagram](http://www.plantuml.com/plantuml/proxy?src=https://raw.githubusercontent.com/andreoug/spring-boot-kafka-producer-consumer/main/docs/umls/sms-delivery-filtering-sequence-diagram.puml)

*Producer*, *consumer*, and *bifunction* are Spring profiles loaded on this application as we will
explain later on the section about [Spring Boot Profiles](#sbp).

## Use Cases
In this repo we describe and implement the use cases of an action delivery of an SMS and an SMS Rule as follows:
- Sender forwards an SMS Rule to the Broker
- Sender forwards an SMS to a Receiver


## <a name="gs"> 1. Getting Started</a>
The easiest way to pull, deploy and run this produce-consume kafka string message on your
laptop or any other clean system is through Staging environment, where only docker
installation is required.

### 1.1. Working Environments
Currently, the working environments are the following.
- Staging Environment,
- Development Environment.

The key differences between the working environments (dev and staging) are the following:
- The containerization on staging of the app as you can figure out on [docker-compose.yml](docker-compose.yml) and
  [docker-compose-kafka-only.yml](docker-compose-kafka-only.yml)
- The seperated network that is *kafka-net* network defined in [docker-compose.yml](docker-compose.yml)

#### 1.1.1. Prerequisites
As you can figure out in the following table, the least required prerequisites to give a try to this repo are with 
staging environment. But if you have already java and maven experience, development environment is similarly easy to 
build and experiment.

##### 1.1.1.1. Prerequisites Details
| Framework | Staging | Development |
| :-------------- | :----: | :-----: |
| docker         | &checkmark;  | &checkmark;|
| docker-compose | &checkmark;  | &checkmark; |
| maven          |              | &checkmark; |
| java           |              | &checkmark;|

#### 1.1.2. Staging Environment
Follow the steps on the section about [Working on the Staging Environment](#wotse).

##### 1.1.2.1. Versioning Details

| Program | Package |  Version | Ruled by |
| :---- | :------- |---------:| --------: |
| docker | Docker CE |  20.10.8 | env. |
| docker-compose | Docker CE |   1.29.2 | env. |
| Kafka | Kafka Broker |        - | [docker-compose.yml](docker-compose.yml) |
| mvn | Maven |    3.6.1 | [Dockerfile](Dockerfile) |
| java | Java |      1.8 | [Dockerfile](Dockerfile) |
| lib | Apache-Kafka-Streams |    2.7.2 | spring-boot |
| lib | Jackson-Datatype-JSR310 |   2.12.5 | spring-boot |
| lib | Lombok |  1.18.22 | spring-boot |
| lib | Openapi-UI |    1.6.3 | [pom.xml](pom.xml) |
| lib | Spring-Cloud-Stream |    3.1.6 | spring-boot |
| lib | Spring-Cloud-Stream-Binder-Kafka-Streams |    3.1.6 | spring-boot |
| lib | Spring-Boot-Starter-Webflux |    2.5.7 | spring-boot |
| spring-boot | Spring-Boot-Starter-Parent |    2.5.7 | [pom.xml](pom.xml) |
| spring-cloud | Spring-Cloud | 2020.0.5 | [pom.xml](pom.xml) |

#### 1.1.3. Development Environment
Follow the steps on section about [Working on the Development Environment](#wotde).

##### 1.1.3.1 Versioning Details

| Program | Package                                  |   Version | Ruled by |
| :---- |:-----------------------------------------|----------:| --------: |
| docker | Docker CE                                |   20.10.8 | env. |
| docker-compose | Docker CE                                |    1.29.2 | env. |
| Kafka | Kafka Broker                             |         - | [docker-compose.yml](docker-compose.yml) |
| mvn | Maven                                    |     3.8.1 | env. |
| java | Java                                     | 1.8.0_292 | env. |
| lib | Apache-Kafka-Streams                     |     2.7.2 | spring-boot |
| lib | Jackson-Datatype-JSR310                  |    2.12.5 | spring-boot |
| lib | Lombok                                   |   1.18.22 | spring-boot |
| lib | Spring-Cloud-Stream                      |     3.1.6 | spring-boot |
| lib | Spring-Cloud-Stream-Binder-Kafka-Streams |     3.1.6 | spring-boot |
| lib | Spring-Boot-Starter-Webflux              |     2.5.7 | spring-boot |
| spring-boot | Spring-Boot-Starter-Parent               |     2.5.7 | [pom.xml](pom.xml) |
| spring-cloud | Spring-Cloud                             |  2020.0.4 | [pom.xml](pom.xml) |


### <a name="wotse">1.2. Working on the Staging Environment</a>

1. Deploy the Kafka broker with Zookeeper and also producer, bifunction, and consumer of the app using docker-compose as follows

```bash
  docker-compose up -d
```

2. Send a sms rule to the app to produce it as follows

```bash
    curl -X POST "http://localhost:9000/kafka/send-rule" -H  "accept: */*" \
    -H  "Content-Type: application/json" \
    -d "{\"verb\":\"BLOCK\",\"allSenders\":true,\"receiver\":\"0123456789\"}"
```

3. Send two SMS's to the app to produce and consume it as follows

```bash
    curl -X POST "http://localhost:9000/kafka/send-sms" -H  "accept: */*" \
    -H  "Content-Type: application/json" \
    -d "{\"body\":\"string\",\"sender\":\"0000000000\",\"receiver\":\"9876543210\"}" 
    
    curl -X POST "http://localhost:9000/kafka/send-sms" -H  "accept: */*" \
    -H  "Content-Type: application/json" \
    -d "{\"body\":\"string\",\"sender\":\"0000000000\",\"receiver\":\"0123456789\"}" 
```

4. Check the logs from producer through docker-compose logs

```bash
    $ docker-compose logs producer
    ...
    producer      | 2022-02-26 14:54:16.655  WARN 1 --- [| adminclient-1] org.apache.kafka.clients.NetworkClient   : [AdminClient clientId=adminclient-1] Connection to node -1 (localhost/127.0.0.1:9092) could not be established. Broker may not be available.
    producer      | 2022-02-26 14:54:16.976  INFO 1 --- [| adminclient-1] o.a.k.clients.admin.KafkaAdminClient     : [AdminClient clientId=adminclient-1] Forcing a hard I/O thread shutdown. Requests in progress will be aborted.
    producer      | 2022-02-26 14:54:16.979  INFO 1 --- [| adminclient-1] o.a.kafka.common.utils.AppInfoParser     : App info kafka.admin.client for adminclient-1 unregistered
    producer      | 2022-02-26 14:54:16.981  INFO 1 --- [| adminclient-1] o.a.k.c.a.i.AdminMetadataManager         : [AdminClient clientId=adminclient-1] Metadata update failed
    producer      | 
    producer      | org.apache.kafka.common.errors.TimeoutException: Call(callName=fetchMetadata, deadlineMs=1645887276955, tries=1, nextAllowedTryMs=-9223372036854775709) timed out at 9223372036854775807 after 1 attempt(s)
    producer      | Caused by: org.apache.kafka.common.errors.TimeoutException: The AdminClient thread has exited. Call: fetchMetadata
    producer      | 
    producer      | 2022-02-26 14:54:16.997  INFO 1 --- [| adminclient-1] org.apache.kafka.common.metrics.Metrics  : Metrics scheduler closed
    producer      | 2022-02-26 14:54:16.997  INFO 1 --- [| adminclient-1] org.apache.kafka.common.metrics.Metrics  : Closing reporter org.apache.kafka.common.metrics.JmxReporter
    producer      | 2022-02-26 14:54:16.997  INFO 1 --- [| adminclient-1] org.apache.kafka.common.metrics.Metrics  : Metrics reporters closed
    producer      | 2022-02-26 14:54:17.000  INFO 1 --- [           main] o.s.i.endpoint.EventDrivenConsumer       : Adding {logging-channel-adapter:_org.springframework.integration.errorLogger} as a subscriber to the 'errorChannel' channel
    producer      | 2022-02-26 14:54:17.000  INFO 1 --- [           main] o.s.i.channel.PublishSubscribeChannel    : Channel 'application.errorChannel' has 1 subscriber(s).
    producer      | 2022-02-26 14:54:17.001  INFO 1 --- [           main] o.s.i.endpoint.EventDrivenConsumer       : started bean '_org.springframework.integration.errorLogger'
    producer      | 2022-02-26 14:54:17.244  INFO 1 --- [           main] o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port 9000
    producer      | 2022-02-26 14:54:17.268  INFO 1 --- [           main] amsProducerBifunctionConsumerApplication : Started SpringBootKafkaStreamsProducerBifunctionConsumerApplication in 46.976 seconds (JVM running for 48.058)
    producer      | 2022-02-26 14:55:34.862  INFO 1 --- [or-http-epoll-2] c.p.s.producer.Producer                  : #~#: Producing (key: 0123456789), action -> Action(id=5c2dddba, sms=null, created=null, updated=null, status=null, smsRule=SmsRule(verb=BLOCK, allSenders=true, receiver=0123456789, timestamp=2022-02-26T14:55:34.860))
    producer      | 2022-02-26 14:55:34.878  INFO 1 --- [or-http-epoll-2] o.a.k.clients.producer.ProducerConfig    : ProducerConfig values: 
    producer      |         acks = 1
    producer      |         batch.size = 16384
    producer      |         bootstrap.servers = [kafka:9092]
    ...
    producer      | 2022-02-26 14:55:34.950  INFO 1 --- [or-http-epoll-2] o.a.kafka.common.utils.AppInfoParser     : Kafka version: 2.7.2
    producer      | 2022-02-26 14:55:34.951  INFO 1 --- [or-http-epoll-2] o.a.kafka.common.utils.AppInfoParser     : Kafka commitId: 37a1cc36bf4d76f3
    producer      | 2022-02-26 14:55:34.951  INFO 1 --- [or-http-epoll-2] o.a.kafka.common.utils.AppInfoParser     : Kafka startTimeMs: 1645887334950
    producer      | 2022-02-26 14:55:35.499  INFO 1 --- [ad | producer-1] org.apache.kafka.clients.Metadata        : [Producer clientId=producer-1] Cluster ID: d2si7YvbRuWGDzZQCYYNWQ
    producer      | 2022-02-26 14:56:03.418  INFO 1 --- [or-http-epoll-3] c.p.s.producer.Producer                  : #~#: Producing (key: 9876543210), action -> Action(id=b1481430, sms=Sms(body=string, sender=0000000000, receiver=9876543210, timestamp=2022-02-26T14:56:03.417), created=2022-02-26T14:56:03.417, updated=2022-02-26T14:56:03.417, status=CREATED, smsRule=null)
    producer      | 2022-02-26 14:59:37.027  INFO 1 --- [or-http-epoll-5] c.p.s.producer.Producer                  : #~#: Producing (key: 0123456789), action -> Action(id=5bafc81e, sms=Sms(body=string, sender=0000000000, receiver=0123456789, timestamp=2022-02-26T14:59:37.027), created=2022-02-26T14:59:37.027, updated=2022-02-26T14:59:37.027, status=CREATED, smsRule=null)

```

5. Check the logs from bifunction through docker-compose logs

```bash
    $ docker-compose logs bifunction
    ...
    bifunction    | 2022-02-26 14:53:43.955  INFO 1 --- [           main] o.a.k.s.p.internals.StreamThread         : stream-thread [bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1] Creating restore consumer client
    bifunction    | 2022-02-26 14:53:43.968  INFO 1 --- [           main] o.a.k.clients.consumer.ConsumerConfig    : ConsumerConfig values: 
    bifunction    |         allow.auto.create.topics = true
    bifunction    |         auto.commit.interval.ms = 5000
    bifunction    |         auto.offset.reset = none
    bifunction    |         bootstrap.servers = [kafka:9092]
    ...
    bifunction    | 2022-02-26 14:53:44.038  WARN 1 --- [           main] o.a.k.clients.consumer.ConsumerConfig    : The configuration 'spring.json.trusted.packages' was supplied but isn't a known config.
    bifunction    | 2022-02-26 14:53:44.038  WARN 1 --- [           main] o.a.k.clients.consumer.ConsumerConfig    : The configuration 'spring.json.type.mapping' was supplied but isn't a known config.
    bifunction    | 2022-02-26 14:53:44.038  INFO 1 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka version: 2.7.2
    bifunction    | 2022-02-26 14:53:44.038  INFO 1 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka commitId: 37a1cc36bf4d76f3
    bifunction    | 2022-02-26 14:53:44.038  INFO 1 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka startTimeMs: 1645887224038
    bifunction    | 2022-02-26 14:53:44.052  INFO 1 --- [           main] o.a.k.s.p.internals.StreamThread         : stream-thread [bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1] Creating thread producer client
    bifunction    | 2022-02-26 14:53:44.060  INFO 1 --- [           main] o.a.k.clients.producer.ProducerConfig    : ProducerConfig values: 
    bifunction    |         acks = 1
    bifunction    |         batch.size = 16384
    bifunction    |         bootstrap.servers = [kafka:9092]
    ...
    bifunction    | 2022-02-26 14:53:44.113  WARN 1 --- [           main] o.a.k.clients.producer.ProducerConfig    : The configuration 'spring.json.trusted.packages' was supplied but isn't a known config.
    bifunction    | 2022-02-26 14:53:44.113  WARN 1 --- [           main] o.a.k.clients.producer.ProducerConfig    : The configuration 'spring.json.type.mapping' was supplied but isn't a known config.
    bifunction    | 2022-02-26 14:53:44.114  INFO 1 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka version: 2.7.2
    bifunction    | 2022-02-26 14:53:44.114  INFO 1 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka commitId: 37a1cc36bf4d76f3
    bifunction    | 2022-02-26 14:53:44.114  INFO 1 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka startTimeMs: 1645887224114
    bifunction    | 2022-02-26 14:53:44.128  INFO 1 --- [           main] o.a.k.s.p.internals.StreamThread         : stream-thread [bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1] Creating consumer client
    bifunction    | 2022-02-26 14:53:44.133  INFO 1 --- [           main] o.a.k.clients.consumer.ConsumerConfig    : ConsumerConfig values: 
    bifunction    |         allow.auto.create.topics = false
    bifunction    |         auto.commit.interval.ms = 5000
    bifunction    |         auto.offset.reset = earliest
    bifunction    |         bootstrap.servers = [kafka:9092]
    ...
    bifunction    | 2022-02-26 14:53:44.136  INFO 1 --- [read-1-producer] org.apache.kafka.clients.Metadata        : [Producer clientId=bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1-producer] Cluster ID: d2si7YvbRuWGDzZQCYYNWQ
    bifunction    | 2022-02-26 14:53:44.154  INFO 1 --- [           main] o.a.k.s.p.i.a.AssignorConfiguration      : stream-thread [bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1-consumer] Cooperative rebalancing enabled now
    bifunction    | 2022-02-26 14:53:44.193  WARN 1 --- [           main] o.a.k.clients.consumer.ConsumerConfig    : The configuration 'spring.json.type.mapping' was supplied but isn't a known config.
    bifunction    | 2022-02-26 14:53:44.193  WARN 1 --- [           main] o.a.k.clients.consumer.ConsumerConfig    : The configuration 'spring.json.trusted.packages' was supplied but isn't a known config.
    bifunction    | 2022-02-26 14:53:44.193  INFO 1 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka version: 2.7.2
    bifunction    | 2022-02-26 14:53:44.193  INFO 1 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka commitId: 37a1cc36bf4d76f3
    bifunction    | 2022-02-26 14:53:44.193  INFO 1 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka startTimeMs: 1645887224193
    bifunction    | 2022-02-26 14:53:44.211  INFO 1 --- [           main] org.apache.kafka.streams.KafkaStreams    : stream-client [bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068] State transition from CREATED to REBALANCING
    bifunction    | 2022-02-26 14:53:44.212  INFO 1 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1] Starting
    bifunction    | 2022-02-26 14:53:44.212  INFO 1 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1] State transition from CREATED to STARTING
    bifunction    | 2022-02-26 14:53:44.216  INFO 1 --- [-StreamThread-1] o.a.k.clients.consumer.KafkaConsumer     : [Consumer clientId=bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Subscribed to topic(s): rules.topic, topic
    bifunction    | 2022-02-26 14:53:44.249  INFO 1 --- [-StreamThread-1] org.apache.kafka.clients.Metadata        : [Consumer clientId=bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Cluster ID: d2si7YvbRuWGDzZQCYYNWQ
    bifunction    | 2022-02-26 14:53:44.252  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Discovered group coordinator kafka:9092 (id: 2147482646 rack: null)
    bifunction    | 2022-02-26 14:53:44.258  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] (Re-)joining group
    bifunction    | 2022-02-26 14:53:44.306  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] (Re-)joining group
    bifunction    | 2022-02-26 14:53:44.318  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Successfully joined group with generation Generation{generationId=3, memberId='bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1-consumer-670df147-2ca3-42dd-82e2-e109044f91e3', protocol='stream'}
    bifunction    | 2022-02-26 14:53:44.395  INFO 1 --- [-StreamThread-1] a.k.s.p.i.a.HighAvailabilityTaskAssignor : Decided on assignment: {d2f41743-6d70-4ce8-a9ee-8220aad3f068=[activeTasks: ([0_0]) standbyTasks: ([]) prevActiveTasks: ([]) prevStandbyTasks: ([]) changelogOffsetTotalsByTask: ([]) taskLagTotals: ([0_0=0]) capacity: 1 assigned: 1]} with no followup probing rebalance.
    bifunction    | 2022-02-26 14:53:44.397  INFO 1 --- [-StreamThread-1] o.a.k.s.p.i.StreamsPartitionAssignor     : stream-thread [bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1-consumer] Assigned tasks [0_0] including stateful [0_0] to clients as: 
    bifunction    | d2f41743-6d70-4ce8-a9ee-8220aad3f068=[activeTasks: ([0_0]) standbyTasks: ([])].
    bifunction    | 2022-02-26 14:53:44.411  INFO 1 --- [-StreamThread-1] o.a.k.s.p.i.StreamsPartitionAssignor     : stream-thread [bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1-consumer] Client d2f41743-6d70-4ce8-a9ee-8220aad3f068 per-consumer assignment:
    bifunction    |         prev owned active {}
    bifunction    |         prev owned standby {bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1-consumer-670df147-2ca3-42dd-82e2-e109044f91e3=[]}
    bifunction    |         assigned active {bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1-consumer-670df147-2ca3-42dd-82e2-e109044f91e3=[0_0]}
    bifunction    |         revoking active {}      assigned standby {}
    bifunction    | 
    bifunction    | 2022-02-26 14:53:44.412  INFO 1 --- [-StreamThread-1] o.a.k.s.p.i.StreamsPartitionAssignor     : stream-thread [bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1-consumer] Finished stable assignment of tasks, no followup rebalances required.
    bifunction    | 2022-02-26 14:53:44.414  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Finished assignment for group at generation 3: {bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1-consumer-670df147-2ca3-42dd-82e2-e109044f91e3=Assignment(partitions=[rules.topic-0, topic-0], userDataSize=56)}
    bifunction    | 2022-02-26 14:53:44.458  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Successfully synced group in generation Generation{generationId=3, memberId='bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1-consumer-670df147-2ca3-42dd-82e2-e109044f91e3', protocol='stream'}
    bifunction    | 2022-02-26 14:53:44.458  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Updating assignment with
    bifunction    |         Assigned partitions:                       [topic-0, rules.topic-0]
    bifunction    |         Current owned partitions:                  []
    bifunction    |         Added partitions (assigned - owned):       [topic-0, rules.topic-0]
    bifunction    |         Revoked partitions (owned - assigned):     []
    bifunction    | 
    bifunction    | 2022-02-26 14:53:44.459  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Notifying assignor about the new Assignment(partitions=[rules.topic-0, topic-0], userDataSize=56)
    bifunction    | 2022-02-26 14:53:44.461  INFO 1 --- [-StreamThread-1] o.a.k.s.p.i.StreamsPartitionAssignor     : stream-thread [bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1-consumer] No followup rebalance was requested, resetting the rebalance schedule.
    bifunction    | 2022-02-26 14:53:44.467  INFO 1 --- [-StreamThread-1] o.a.k.s.processor.internals.TaskManager  : stream-thread [bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1] Handle new assignment with:
    bifunction    |         New active tasks: [0_0]
    bifunction    |         New standby tasks: []
    bifunction    |         Existing active tasks: []
    bifunction    |         Existing standby tasks: []
    bifunction    | 2022-02-26 14:53:44.512  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Adding newly assigned partitions: topic-0, rules.topic-0
    bifunction    | 2022-02-26 14:53:44.513  INFO 1 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1] State transition from STARTING to PARTITIONS_ASSIGNED
    bifunction    | 2022-02-26 14:53:44.530  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Found no committed offset for partition topic-0
    bifunction    | 2022-02-26 14:53:44.531  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Found no committed offset for partition rules.topic-0
    bifunction    | 2022-02-26 14:53:44.563  INFO 1 --- [           main] o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port 8080
    bifunction    | 2022-02-26 14:53:44.581  INFO 1 --- [           main] amsProducerBifunctionConsumerApplication : Started SpringBootKafkaStreamsProducerBifunctionConsumerApplication in 10.73 seconds (JVM running for 12.099)
    bifunction    | 2022-02-26 14:53:44.751  INFO 1 --- [-StreamThread-1] o.a.k.s.s.i.RocksDBTimestampedStore      : Opening store rules.topic-STATE-STORE-0000000001 in regular mode
    bifunction    | 2022-02-26 14:53:44.765  INFO 1 --- [-StreamThread-1] o.a.k.s.p.i.ProcessorStateManager        : stream-thread [bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1] task [0_0] State store rules.topic-STATE-STORE-0000000001 did not find checkpoint offset, hence would default to the starting offset at changelog bifunctionProcessor-applicationId-rules.topic-STATE-STORE-0000000001-changelog-0
    bifunction    | 2022-02-26 14:53:44.765  INFO 1 --- [-StreamThread-1] o.a.k.s.processor.internals.StreamTask   : stream-thread [bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1] task [0_0] Initialized
    bifunction    | 2022-02-26 14:53:44.776  INFO 1 --- [-StreamThread-1] o.a.k.clients.consumer.KafkaConsumer     : [Consumer clientId=bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1-restore-consumer, groupId=null] Subscribed to partition(s): bifunctionProcessor-applicationId-rules.topic-STATE-STORE-0000000001-changelog-0
    bifunction    | 2022-02-26 14:53:44.780  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.SubscriptionState    : [Consumer clientId=bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1-restore-consumer, groupId=null] Seeking to EARLIEST offset of partition bifunctionProcessor-applicationId-rules.topic-STATE-STORE-0000000001-changelog-0
    bifunction    | 2022-02-26 14:53:44.794  INFO 1 --- [-StreamThread-1] org.apache.kafka.clients.Metadata        : [Consumer clientId=bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1-restore-consumer, groupId=null] Cluster ID: d2si7YvbRuWGDzZQCYYNWQ
    bifunction    | 2022-02-26 14:53:44.812  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.SubscriptionState    : [Consumer clientId=bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1-restore-consumer, groupId=null] Resetting offset for partition bifunctionProcessor-applicationId-rules.topic-STATE-STORE-0000000001-changelog-0 to position FetchPosition{offset=0, offsetEpoch=Optional.empty, currentLeader=LeaderAndEpoch{leader=Optional[kafka:9092 (id: 1001 rack: null)], epoch=0}}.
    bifunction    | 2022-02-26 14:53:44.918  INFO 1 --- [-StreamThread-1] o.a.k.s.p.i.StoreChangelogReader         : stream-thread [bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1] Finished restoring changelog bifunctionProcessor-applicationId-rules.topic-STATE-STORE-0000000001-changelog-0 to store rules.topic-STATE-STORE-0000000001 with a total number of 0 records
    bifunction    | 2022-02-26 14:53:44.919  INFO 1 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1] Processed 0 total records, ran 0 punctuators, and committed 0 total tasks since the last update
    bifunction    | 2022-02-26 14:53:44.928  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Found no committed offset for partition topic-0
    bifunction    | 2022-02-26 14:53:44.928  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Found no committed offset for partition rules.topic-0
    bifunction    | 2022-02-26 14:53:44.938  INFO 1 --- [-StreamThread-1] o.a.k.s.processor.internals.StreamTask   : stream-thread [bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1] task [0_0] Restored and ready to run
    bifunction    | 2022-02-26 14:53:44.939  INFO 1 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1] Restoration took 426 ms for all tasks [0_0]
    bifunction    | 2022-02-26 14:53:44.939  INFO 1 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1] State transition from PARTITIONS_ASSIGNED to RUNNING
    bifunction    | 2022-02-26 14:53:44.942  INFO 1 --- [-StreamThread-1] org.apache.kafka.streams.KafkaStreams    : stream-client [bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068] State transition from REBALANCING to RUNNING
    bifunction    | 2022-02-26 14:53:44.945  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.SubscriptionState    : [Consumer clientId=bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Resetting offset for partition topic-0 to position FetchPosition{offset=0, offsetEpoch=Optional.empty, currentLeader=LeaderAndEpoch{leader=Optional[kafka:9092 (id: 1001 rack: null)], epoch=0}}.
    bifunction    | 2022-02-26 14:53:44.946  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.SubscriptionState    : [Consumer clientId=bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Resetting offset for partition rules.topic-0 to position FetchPosition{offset=0, offsetEpoch=Optional.empty, currentLeader=LeaderAndEpoch{leader=Optional[kafka:9092 (id: 1001 rack: null)], epoch=0}}.
    bifunction    | 2022-02-26 14:55:45.015  INFO 1 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1] Processed 1 total records, ran 0 punctuators, and committed 0 total tasks since the last update
    bifunction    | 2022-02-26 14:56:03.452  INFO 1 --- [-StreamThread-1] c.p.s.KafkaBiFunctionConfiguration       : key: 9876543210, action: Action(id=b1481430, sms=Sms(body=string, sender=0000000000, receiver=9876543210, timestamp=2022-02-26T14:56:03), created=2022-02-26T14:56:03, updated=2022-02-26T14:56:03.452, status=PENDING, smsRule=null)
    bifunction    | 2022-02-26 14:57:45.100  INFO 1 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1] Processed 2 total records, ran 0 punctuators, and committed 2 total tasks since the last update
    bifunction    | 2022-02-26 14:59:37.045  INFO 1 --- [-StreamThread-1] c.p.s.KafkaBiFunctionConfiguration       : key: 0123456789, action: Action(id=5bafc81e, sms=Sms(body=string, sender=0000000000, receiver=0123456789, timestamp=2022-02-26T14:59:37), created=2022-02-26T14:59:37, updated=2022-02-26T14:59:37.045, status=PENDING, smsRule=SmsRule(verb=BLOCK, allSenders=true, receiver=0123456789, timestamp=2022-02-26T14:56:10))
    bifunction    | 2022-02-26 14:59:45.186  INFO 1 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1] Processed 1 total records, ran 0 punctuators, and committed 0 total tasks since the last update
    bifunction    | 2022-02-26 15:01:45.189  INFO 1 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [bifunctionProcessor-applicationId-d2f41743-6d70-4ce8-a9ee-8220aad3f068-StreamThread-1] Processed 0 total records, ran 0 punctuators, and committed 1 total tasks since the last update

```

6. Check the logs from consumer to see the consumed message through docker-compose logs

```bash
    $ docker-compose logs consumer
    ...
    consumer      | 2022-02-26 14:53:40.437  INFO 1 --- [           main] .k.s.AbstractKafkaStreamsBinderProcessor : Binder Generated Kafka Streams Application ID: consumeService-applicationId
    consumer      | 2022-02-26 14:53:40.437  INFO 1 --- [           main] .k.s.AbstractKafkaStreamsBinderProcessor : Use the binder generated application ID only for development and testing. 
    consumer      | 2022-02-26 14:53:40.438  INFO 1 --- [           main] .k.s.AbstractKafkaStreamsBinderProcessor : For production deployments, please consider explicitly setting an application ID using a configuration property.
    consumer      | 2022-02-26 14:53:40.438  INFO 1 --- [           main] .k.s.AbstractKafkaStreamsBinderProcessor : The generated applicationID is static and will be preserved over application restarts.
    consumer      | 2022-02-26 14:53:40.535  INFO 1 --- [           main] .c.s.b.k.s.KafkaStreamsFunctionProcessor : Key Serde used for consumeService-in-0: org.apache.kafka.common.serialization.Serdes$StringSerde
    consumer      | 2022-02-26 14:53:40.751  INFO 1 --- [           main] .c.s.b.k.s.KafkaStreamsFunctionProcessor : Value Serde used for consumeService-in-0: org.springframework.kafka.support.serializer.JsonSerde
    consumer      | 2022-02-26 14:53:40.752  INFO 1 --- [           main] .k.s.AbstractKafkaStreamsBinderProcessor : Native decoding is enabled for consumeService-in-0. Inbound deserialization done at the broker.
    consumer      | 2022-02-26 14:53:41.172  INFO 1 --- [           main] o.s.i.endpoint.EventDrivenConsumer       : Adding {logging-channel-adapter:_org.springframework.integration.errorLogger} as a subscriber to the 'errorChannel' channel
    consumer      | 2022-02-26 14:53:41.173  INFO 1 --- [           main] o.s.i.channel.PublishSubscribeChannel    : Channel 'application.errorChannel' has 1 subscriber(s).
    consumer      | 2022-02-26 14:53:41.176  INFO 1 --- [           main] o.s.i.endpoint.EventDrivenConsumer       : started bean '_org.springframework.integration.errorLogger'
    consumer      | 2022-02-26 14:53:41.179  INFO 1 --- [           main] o.s.c.s.binder.DefaultBinderFactory      : Creating binder: kstream
    consumer      | 2022-02-26 14:53:41.523  INFO 1 --- [           main] o.s.c.s.binder.DefaultBinderFactory      : Caching the binder: kstream
    consumer      | 2022-02-26 14:53:41.525  INFO 1 --- [           main] o.s.c.s.binder.DefaultBinderFactory      : Retrieving cached binder: kstream
    consumer      | 2022-02-26 14:53:41.748  INFO 1 --- [           main] o.a.k.clients.admin.AdminClientConfig    : AdminClientConfig values: 
    consumer      |         bootstrap.servers = [kafka:9092]
    ...
    consumer      | 2022-02-26 14:53:42.065  INFO 1 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka version: 2.7.2
    consumer      | 2022-02-26 14:53:42.068  INFO 1 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka commitId: 37a1cc36bf4d76f3
    consumer      | 2022-02-26 14:53:42.068  INFO 1 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka startTimeMs: 1645887222062
    consumer      | 2022-02-26 14:53:42.839  INFO 1 --- [| adminclient-1] o.a.kafka.common.utils.AppInfoParser     : App info kafka.admin.client for adminclient-1 unregistered
    consumer      | 2022-02-26 14:53:42.846  INFO 1 --- [| adminclient-1] org.apache.kafka.common.metrics.Metrics  : Metrics scheduler closed
    consumer      | 2022-02-26 14:53:42.846  INFO 1 --- [| adminclient-1] org.apache.kafka.common.metrics.Metrics  : Closing reporter org.apache.kafka.common.metrics.JmxReporter
    consumer      | 2022-02-26 14:53:42.846  INFO 1 --- [| adminclient-1] org.apache.kafka.common.metrics.Metrics  : Metrics reporters closed
    consumer      | 2022-02-26 14:53:42.891  INFO 1 --- [           main] org.apache.kafka.streams.StreamsConfig   : StreamsConfig values: 
    consumer      |         acceptable.recovery.lag = 10000
    consumer      |         application.id = consumeService-applicationId
    consumer      |         application.server = 
    consumer      |         bootstrap.servers = [kafka:9092]
    ...
    consumer      | 2022-02-26 14:53:42.922  INFO 1 --- [           main] org.apache.kafka.streams.KafkaStreams    : stream-client [consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9] Kafka Streams version: 2.7.2
    consumer      | 2022-02-26 14:53:42.923  INFO 1 --- [           main] org.apache.kafka.streams.KafkaStreams    : stream-client [consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9] Kafka Streams commit ID: 37a1cc36bf4d76f3
    consumer      | 2022-02-26 14:53:42.930  INFO 1 --- [           main] o.a.k.clients.admin.AdminClientConfig    : AdminClientConfig values: 
    consumer      |         bootstrap.servers = [kafka:9092]
    ...
    consumer      | 2022-02-26 14:53:42.937  WARN 1 --- [           main] o.a.k.clients.admin.AdminClientConfig    : The configuration 'spring.json.trusted.packages' was supplied but isn't a known config.
    consumer      | 2022-02-26 14:53:42.937  WARN 1 --- [           main] o.a.k.clients.admin.AdminClientConfig    : The configuration 'spring.json.type.mapping' was supplied but isn't a known config.
    consumer      | 2022-02-26 14:53:42.937  INFO 1 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka version: 2.7.2
    consumer      | 2022-02-26 14:53:42.938  INFO 1 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka commitId: 37a1cc36bf4d76f3
    consumer      | 2022-02-26 14:53:42.938  INFO 1 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka startTimeMs: 1645887222937
    consumer      | 2022-02-26 14:53:42.940  INFO 1 --- [           main] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1] Creating restore consumer client
    consumer      | 2022-02-26 14:53:42.955  INFO 1 --- [           main] o.a.k.clients.consumer.ConsumerConfig    : ConsumerConfig values: 
    consumer      |         allow.auto.create.topics = true
    consumer      |         auto.commit.interval.ms = 5000
    consumer      |         auto.offset.reset = none
    consumer      |         bootstrap.servers = [kafka:9092]
    ...
    consumer      | 2022-02-26 14:53:43.020  WARN 1 --- [           main] o.a.k.clients.consumer.ConsumerConfig    : The configuration 'spring.json.trusted.packages' was supplied but isn't a known config.
    consumer      | 2022-02-26 14:53:43.020  WARN 1 --- [           main] o.a.k.clients.consumer.ConsumerConfig    : The configuration 'spring.json.type.mapping' was supplied but isn't a known config.
    consumer      | 2022-02-26 14:53:43.020  INFO 1 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka version: 2.7.2
    consumer      | 2022-02-26 14:53:43.020  INFO 1 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka commitId: 37a1cc36bf4d76f3
    consumer      | 2022-02-26 14:53:43.021  INFO 1 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka startTimeMs: 1645887223020
    consumer      | 2022-02-26 14:53:43.034  INFO 1 --- [           main] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1] Creating thread producer client
    consumer      | 2022-02-26 14:53:43.042  INFO 1 --- [           main] o.a.k.clients.producer.ProducerConfig    : ProducerConfig values: 
    consumer      |         acks = 1
    consumer      |         batch.size = 16384
    consumer      |         bootstrap.servers = [kafka:9092]
    ...
    consumer      | 2022-02-26 14:53:43.110  WARN 1 --- [           main] o.a.k.clients.producer.ProducerConfig    : The configuration 'spring.json.trusted.packages' was supplied but isn't a known config.
    consumer      | 2022-02-26 14:53:43.111  WARN 1 --- [           main] o.a.k.clients.producer.ProducerConfig    : The configuration 'spring.json.type.mapping' was supplied but isn't a known config.
    consumer      | 2022-02-26 14:53:43.112  INFO 1 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka version: 2.7.2
    consumer      | 2022-02-26 14:53:43.112  INFO 1 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka commitId: 37a1cc36bf4d76f3
    consumer      | 2022-02-26 14:53:43.112  INFO 1 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka startTimeMs: 1645887223111
    consumer      | 2022-02-26 14:53:43.120  INFO 1 --- [           main] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1] Creating consumer client
    consumer      | 2022-02-26 14:53:43.124  INFO 1 --- [           main] o.a.k.clients.consumer.ConsumerConfig    : ConsumerConfig values: 
    consumer      |         allow.auto.create.topics = false
    consumer      |         auto.commit.interval.ms = 5000
    consumer      |         auto.offset.reset = earliest
    consumer      |         bootstrap.servers = [kafka:9092]
    ...
    consumer      | 2022-02-26 14:53:43.146  INFO 1 --- [read-1-producer] org.apache.kafka.clients.Metadata        : [Producer clientId=consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1-producer] Cluster ID: d2si7YvbRuWGDzZQCYYNWQ
    consumer      | 2022-02-26 14:53:43.151  INFO 1 --- [           main] o.a.k.s.p.i.a.AssignorConfiguration      : stream-thread [consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1-consumer] Cooperative rebalancing enabled now
    consumer      | 2022-02-26 14:53:43.191  WARN 1 --- [           main] o.a.k.clients.consumer.ConsumerConfig    : The configuration 'spring.json.type.mapping' was supplied but isn't a known config.
    consumer      | 2022-02-26 14:53:43.191  WARN 1 --- [           main] o.a.k.clients.consumer.ConsumerConfig    : The configuration 'spring.json.trusted.packages' was supplied but isn't a known config.
    consumer      | 2022-02-26 14:53:43.191  INFO 1 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka version: 2.7.2
    consumer      | 2022-02-26 14:53:43.191  INFO 1 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka commitId: 37a1cc36bf4d76f3
    consumer      | 2022-02-26 14:53:43.191  INFO 1 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka startTimeMs: 1645887223191
    consumer      | 2022-02-26 14:53:43.212  INFO 1 --- [           main] org.apache.kafka.streams.KafkaStreams    : stream-client [consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9] State transition from CREATED to REBALANCING
    consumer      | 2022-02-26 14:53:43.213  INFO 1 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1] Starting
    consumer      | 2022-02-26 14:53:43.213  INFO 1 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1] State transition from CREATED to STARTING
    consumer      | 2022-02-26 14:53:43.217  INFO 1 --- [-StreamThread-1] o.a.k.clients.consumer.KafkaConsumer     : [Consumer clientId=consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1-consumer, groupId=consumeService-applicationId] Subscribed to topic(s): active.topic
    consumer      | 2022-02-26 14:53:43.261  INFO 1 --- [-StreamThread-1] org.apache.kafka.clients.Metadata        : [Consumer clientId=consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1-consumer, groupId=consumeService-applicationId] Cluster ID: d2si7YvbRuWGDzZQCYYNWQ
    consumer      | 2022-02-26 14:53:43.264  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1-consumer, groupId=consumeService-applicationId] Discovered group coordinator kafka:9092 (id: 2147482646 rack: null)
    consumer      | 2022-02-26 14:53:43.273  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1-consumer, groupId=consumeService-applicationId] (Re-)joining group
    consumer      | 2022-02-26 14:53:43.343  INFO 1 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1] Processed 0 total records, ran 0 punctuators, and committed 0 total tasks since the last update
    consumer      | 2022-02-26 14:53:43.354  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1-consumer, groupId=consumeService-applicationId] (Re-)joining group
    consumer      | 2022-02-26 14:53:43.369  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1-consumer, groupId=consumeService-applicationId] Successfully joined group with generation Generation{generationId=3, memberId='consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1-consumer-762964b4-d330-4875-9999-461fb5a771ad', protocol='stream'}
    consumer      | 2022-02-26 14:53:43.441  INFO 1 --- [-StreamThread-1] a.k.s.p.i.a.HighAvailabilityTaskAssignor : Decided on assignment: {100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9=[activeTasks: ([0_0]) standbyTasks: ([]) prevActiveTasks: ([]) prevStandbyTasks: ([]) changelogOffsetTotalsByTask: ([]) taskLagTotals: ([]) capacity: 1 assigned: 1]} with no followup probing rebalance.
    consumer      | 2022-02-26 14:53:43.444  INFO 1 --- [-StreamThread-1] o.a.k.s.p.i.StreamsPartitionAssignor     : stream-thread [consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1-consumer] Assigned tasks [0_0] including stateful [] to clients as: 
    consumer      | 100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9=[activeTasks: ([0_0]) standbyTasks: ([])].
    consumer      | 2022-02-26 14:53:43.456  INFO 1 --- [-StreamThread-1] o.a.k.s.p.i.StreamsPartitionAssignor     : stream-thread [consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1-consumer] Client 100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9 per-consumer assignment:
    consumer      |         prev owned active {}
    consumer      |         prev owned standby {consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1-consumer-762964b4-d330-4875-9999-461fb5a771ad=[]}
    consumer      |         assigned active {consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1-consumer-762964b4-d330-4875-9999-461fb5a771ad=[0_0]}
    consumer      |         revoking active {}      assigned standby {}
    consumer      | 
    consumer      | 2022-02-26 14:53:43.456  INFO 1 --- [-StreamThread-1] o.a.k.s.p.i.StreamsPartitionAssignor     : stream-thread [consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1-consumer] Finished stable assignment of tasks, no followup rebalances required.
    consumer      | 2022-02-26 14:53:43.458  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1-consumer, groupId=consumeService-applicationId] Finished assignment for group at generation 3: {consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1-consumer-762964b4-d330-4875-9999-461fb5a771ad=Assignment(partitions=[active.topic-0], userDataSize=48)}
    consumer      | 2022-02-26 14:53:43.499  INFO 1 --- [e-applicationId] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1-consumer, groupId=consumeService-applicationId] Successfully synced group in generation Generation{generationId=3, memberId='consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1-consumer-762964b4-d330-4875-9999-461fb5a771ad', protocol='stream'}
    consumer      | 2022-02-26 14:53:43.502  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1-consumer, groupId=consumeService-applicationId] Updating assignment with
    consumer      |         Assigned partitions:                       [active.topic-0]
    consumer      |         Current owned partitions:                  []
    consumer      |         Added partitions (assigned - owned):       [active.topic-0]
    consumer      |         Revoked partitions (owned - assigned):     []
    consumer      | 
    consumer      | 2022-02-26 14:53:43.502  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1-consumer, groupId=consumeService-applicationId] Notifying assignor about the new Assignment(partitions=[active.topic-0], userDataSize=48)
    consumer      | 2022-02-26 14:53:43.505  INFO 1 --- [-StreamThread-1] o.a.k.s.p.i.StreamsPartitionAssignor     : stream-thread [consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1-consumer] No followup rebalance was requested, resetting the rebalance schedule.
    consumer      | 2022-02-26 14:53:43.510  INFO 1 --- [-StreamThread-1] o.a.k.s.processor.internals.TaskManager  : stream-thread [consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1] Handle new assignment with:
    consumer      |         New active tasks: [0_0]
    consumer      |         New standby tasks: []
    consumer      |         Existing active tasks: []
    consumer      |         Existing standby tasks: []
    consumer      | 2022-02-26 14:53:43.623  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1-consumer, groupId=consumeService-applicationId] Adding newly assigned partitions: active.topic-0
    consumer      | 2022-02-26 14:53:43.623  INFO 1 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1] State transition from STARTING to PARTITIONS_ASSIGNED
    consumer      | 2022-02-26 14:53:43.652  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1-consumer, groupId=consumeService-applicationId] Found no committed offset for partition active.topic-0
    consumer      | 2022-02-26 14:53:43.656  INFO 1 --- [-StreamThread-1] o.a.k.s.processor.internals.StreamTask   : stream-thread [consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1] task [0_0] Initialized
    consumer      | 2022-02-26 14:53:43.675  INFO 1 --- [-StreamThread-1] o.a.k.s.processor.internals.StreamTask   : stream-thread [consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1] task [0_0] Restored and ready to run
    consumer      | 2022-02-26 14:53:43.677  INFO 1 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1] Restoration took 54 ms for all tasks [0_0]
    consumer      | 2022-02-26 14:53:43.678  INFO 1 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1] State transition from PARTITIONS_ASSIGNED to RUNNING
    consumer      | 2022-02-26 14:53:43.683  INFO 1 --- [-StreamThread-1] org.apache.kafka.streams.KafkaStreams    : stream-client [consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9] State transition from REBALANCING to RUNNING
    consumer      | 2022-02-26 14:53:43.690  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1-consumer, groupId=consumeService-applicationId] Found no committed offset for partition active.topic-0
    consumer      | 2022-02-26 14:53:43.815  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.SubscriptionState    : [Consumer clientId=consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1-consumer, groupId=consumeService-applicationId] Resetting offset for partition active.topic-0 to position FetchPosition{offset=0, offsetEpoch=Optional.empty, currentLeader=LeaderAndEpoch{leader=Optional[kafka:9092 (id: 1001 rack: null)], epoch=0}}.
    consumer      | 2022-02-26 14:53:43.906  INFO 1 --- [           main] o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port 8080
    consumer      | 2022-02-26 14:53:43.938  INFO 1 --- [           main] amsProducerBifunctionConsumerApplication : Started SpringBootKafkaStreamsProducerBifunctionConsumerApplication in 10.119 seconds (JVM running for 11.556)
    consumer      | 2022-02-26 14:55:43.395  INFO 1 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1] Processed 0 total records, ran 0 punctuators, and committed 0 total tasks since the last update
    consumer      | 2022-02-26 14:56:03.625  INFO 1 --- [-StreamThread-1] c.p.s.KafkaConsumerConfiguration         : Action consumed [Action(id=b1481430, sms=Sms(body=string, sender=0000000000, receiver=9876543210, timestamp=2022-02-26T14:56:03), created=2022-02-26T14:56:03, updated=2022-02-26T14:56:03, status=DELIVERING, smsRule=null)] action.sms.body: [string]
    consumer      | 2022-02-26 14:57:43.473  INFO 1 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1] Processed 1 total records, ran 0 punctuators, and committed 1 total tasks since the last update
    consumer      | 2022-02-26 14:59:43.567  INFO 1 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1] Processed 0 total records, ran 0 punctuators, and committed 0 total tasks since the last update
    consumer      | 2022-02-26 15:01:43.568  INFO 1 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1] Processed 0 total records, ran 0 punctuators, and committed 0 total tasks since the last update
    consumer      | 2022-02-26 15:03:43.604  INFO 1 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1] Processed 0 total records, ran 0 punctuators, and committed 0 total tasks since the last update
    consumer      | 2022-02-26 15:05:43.691  INFO 1 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1] Processed 0 total records, ran 0 punctuators, and committed 0 total tasks since the last update
    consumer      | 2022-02-26 15:07:43.726  INFO 1 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1] Processed 0 total records, ran 0 punctuators, and committed 0 total tasks since the last update
    consumer      | 2022-02-26 15:09:43.802  INFO 1 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1] Processed 0 total records, ran 0 punctuators, and committed 0 total tasks since the last update
    consumer      | 2022-02-26 15:11:43.872  INFO 1 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-100c02d7-f94d-4ecf-b6e4-b9302ba9f4b9-StreamThread-1] Processed 0 total records, ran 0 punctuators, and committed 0 total tasks since the last update

```

### <a name="wotde">1.3. Working on the Development Environment</a>

1. Deploy the Kafka broker with Zookeeper using docker-compose as follows

```bash
    docker-compose -f docker-compose-kafka-only.yml up -d
```
2. Use Maven to clean and package your app as follows
```bash
    mvn clean package
```
3. Run your app with java as follows

```bash
    java -jar target/*.jar
```
4. On a new terminal, run your app consumer profile
```bash
    java -Dspring.profiles.active=consumer -jar target/*.jar
```

5. Send a sms rule to the app to produce it as follows

 ```bash
    curl -X POST "http://localhost:9000/kafka/send-rule" -H  "accept: */*" \
    -H  "Content-Type: application/json" \
    -d "{\"verb\":\"BLOCK\",\"allSenders\":true,\"receiver\":\"0123456789\"}"
 ```
6. Send two message to the app to produce it as follows

```bash
    curl -X POST "http://localhost:9000/kafka/send-sms" -H  "accept: */*" \
    -H  "Content-Type: application/json" \
    -d "{\"body\":\"string\",\"sender\":\"0000000000\",\"receiver\":\"9876543210\"}" 
    
    curl -X POST "http://localhost:9000/kafka/send-sms" -H  "accept: */*" \
    -H  "Content-Type: application/json" \
    -d "{\"body\":\"string\",\"sender\":\"0000000000\",\"receiver\":\"0123456789\"}" 
```

7. Check your logs from terminals

```bash
    ...
    2022-02-26 17:34:09.060  INFO 48968 --- [           main] amsProducerBifunctionConsumerApplication : The following profiles are active: producer,bifunction,consumer
    2022-02-26 17:34:09.734  INFO 48968 --- [           main] faultConfiguringBeanFactoryPostProcessor : No bean named 'errorChannel' has been explicitly defined. Therefore, a default PublishSubscribeChannel will be created.
    2022-02-26 17:34:09.753  INFO 48968 --- [           main] faultConfiguringBeanFactoryPostProcessor : No bean named 'integrationHeaderChannelRegistry' has been explicitly defined. Therefore, a default DefaultHeaderChannelRegistry will be created.
    2022-02-26 17:34:09.968  INFO 48968 --- [           main] trationDelegate$BeanPostProcessorChecker : Bean 'org.springframework.integration.config.IntegrationManagementConfiguration' of type [org.springframework.integration.config.IntegrationManagementConfiguration] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
    2022-02-26 17:34:09.979  INFO 48968 --- [           main] trationDelegate$BeanPostProcessorChecker : Bean 'integrationChannelResolver' of type [org.springframework.integration.support.channel.BeanFactoryChannelResolver] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
    2022-02-26 17:34:09.981  INFO 48968 --- [           main] trationDelegate$BeanPostProcessorChecker : Bean 'integrationDisposableAutoCreatedBeans' of type [org.springframework.integration.config.annotation.Disposables] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
    2022-02-26 17:34:11.166  INFO 48968 --- [           main] .k.s.AbstractKafkaStreamsBinderProcessor : Binder Generated Kafka Streams Application ID: bifunctionProcessor-applicationId
    2022-02-26 17:34:11.166  INFO 48968 --- [           main] .k.s.AbstractKafkaStreamsBinderProcessor : Use the binder generated application ID only for development and testing. 
    2022-02-26 17:34:11.166  INFO 48968 --- [           main] .k.s.AbstractKafkaStreamsBinderProcessor : For production deployments, please consider explicitly setting an application ID using a configuration property.
    2022-02-26 17:34:11.166  INFO 48968 --- [           main] .k.s.AbstractKafkaStreamsBinderProcessor : The generated applicationID is static and will be preserved over application restarts.
    2022-02-26 17:34:11.202  INFO 48968 --- [           main] .c.s.b.k.s.KafkaStreamsFunctionProcessor : Key Serde used for bifunctionProcessor-in-0: org.apache.kafka.common.serialization.Serdes$StringSerde
    2022-02-26 17:34:11.209  INFO 48968 --- [           main] .c.s.b.k.s.KafkaStreamsFunctionProcessor : Value Serde used for bifunctionProcessor-in-0: org.springframework.kafka.support.serializer.JsonSerde
    2022-02-26 17:34:11.210  INFO 48968 --- [           main] .k.s.AbstractKafkaStreamsBinderProcessor : Native decoding is enabled for bifunctionProcessor-in-0. Inbound deserialization done at the broker.
    2022-02-26 17:34:11.223  INFO 48968 --- [           main] .c.s.b.k.s.KafkaStreamsFunctionProcessor : Key Serde used for bifunctionProcessor-in-1: org.apache.kafka.common.serialization.Serdes$StringSerde
    2022-02-26 17:34:11.227  INFO 48968 --- [           main] .c.s.b.k.s.KafkaStreamsFunctionProcessor : Value Serde used for bifunctionProcessor-in-1: org.springframework.kafka.support.serializer.JsonSerde
    2022-02-26 17:34:11.395  INFO 48968 --- [           main] o.a.k.clients.admin.AdminClientConfig    : AdminClientConfig values: 
            bootstrap.servers = [localhost:9092]
    ...
    2022-02-26 17:34:11.543  INFO 48968 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka version: 2.7.2
    2022-02-26 17:34:11.544  INFO 48968 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka commitId: 37a1cc36bf4d76f3
    2022-02-26 17:34:11.545  INFO 48968 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka startTimeMs: 1645889651541
    2022-02-26 17:34:12.006  INFO 48968 --- [| adminclient-1] o.a.kafka.common.utils.AppInfoParser     : App info kafka.admin.client for adminclient-1 unregistered
    2022-02-26 17:34:12.013  INFO 48968 --- [| adminclient-1] org.apache.kafka.common.metrics.Metrics  : Metrics scheduler closed
    2022-02-26 17:34:12.013  INFO 48968 --- [| adminclient-1] org.apache.kafka.common.metrics.Metrics  : Closing reporter org.apache.kafka.common.metrics.JmxReporter
    2022-02-26 17:34:12.013  INFO 48968 --- [| adminclient-1] org.apache.kafka.common.metrics.Metrics  : Metrics reporters closed
    2022-02-26 17:34:12.023  INFO 48968 --- [           main] o.s.i.endpoint.EventDrivenConsumer       : Adding {logging-channel-adapter:_org.springframework.integration.errorLogger} as a subscriber to the 'errorChannel' channel
    2022-02-26 17:34:12.023  INFO 48968 --- [           main] o.s.i.channel.PublishSubscribeChannel    : Channel 'application.errorChannel' has 1 subscriber(s).
    2022-02-26 17:34:12.024  INFO 48968 --- [           main] o.s.i.endpoint.EventDrivenConsumer       : started bean '_org.springframework.integration.errorLogger'
    2022-02-26 17:34:12.025  INFO 48968 --- [           main] o.s.c.s.binder.DefaultBinderFactory      : Creating binder: kstream
    2022-02-26 17:34:12.103  INFO 48968 --- [           main] o.s.c.s.binder.DefaultBinderFactory      : Caching the binder: kstream
    2022-02-26 17:34:12.103  INFO 48968 --- [           main] o.s.c.s.binder.DefaultBinderFactory      : Retrieving cached binder: kstream
    2022-02-26 17:34:12.203  INFO 48968 --- [           main] o.s.c.s.b.k.p.KafkaTopicProvisioner      : Using kafka topic for outbound: active.topic
    2022-02-26 17:34:12.204  INFO 48968 --- [           main] o.a.k.clients.admin.AdminClientConfig    : AdminClientConfig values: 
            bootstrap.servers = [localhost:9092]
    ...
    2022-02-26 17:34:12.208  INFO 48968 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka version: 2.7.2
    2022-02-26 17:34:12.208  INFO 48968 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka commitId: 37a1cc36bf4d76f3
    2022-02-26 17:34:12.208  INFO 48968 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka startTimeMs: 1645889652208
    2022-02-26 17:34:12.256  INFO 48968 --- [| adminclient-2] o.a.kafka.common.utils.AppInfoParser     : App info kafka.admin.client for adminclient-2 unregistered
    2022-02-26 17:34:12.261  INFO 48968 --- [| adminclient-2] org.apache.kafka.common.metrics.Metrics  : Metrics scheduler closed
    2022-02-26 17:34:12.261  INFO 48968 --- [| adminclient-2] org.apache.kafka.common.metrics.Metrics  : Closing reporter org.apache.kafka.common.metrics.JmxReporter
    2022-02-26 17:34:12.261  INFO 48968 --- [| adminclient-2] org.apache.kafka.common.metrics.Metrics  : Metrics reporters closed
    2022-02-26 17:34:12.262  INFO 48968 --- [           main] o.s.c.s.b.kafka.streams.KStreamBinder    : Key Serde used for (outbound) active.topic: org.apache.kafka.common.serialization.Serdes$StringSerde
    2022-02-26 17:34:12.268  INFO 48968 --- [           main] o.s.c.s.b.kafka.streams.KStreamBinder    : Value Serde used for (outbound) active.topic: org.springframework.kafka.support.serializer.JsonSerde
    2022-02-26 17:34:12.269  INFO 48968 --- [           main] o.s.c.s.b.kafka.streams.KStreamBinder    : Native encoding is enabled for active.topic. Outbound serialization done at the broker.
    2022-02-26 17:34:12.275  INFO 48968 --- [           main] o.s.c.s.binder.DefaultBinderFactory      : Retrieving cached binder: kstream
    2022-02-26 17:34:12.315  INFO 48968 --- [           main] o.a.k.clients.admin.AdminClientConfig    : AdminClientConfig values: 
            bootstrap.servers = [localhost:9092]
    ...
    2022-02-26 17:34:12.319  INFO 48968 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka version: 2.7.2
    2022-02-26 17:34:12.319  INFO 48968 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka commitId: 37a1cc36bf4d76f3
    2022-02-26 17:34:12.320  INFO 48968 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka startTimeMs: 1645889652319
    2022-02-26 17:34:12.366  INFO 48968 --- [| adminclient-3] o.a.kafka.common.utils.AppInfoParser     : App info kafka.admin.client for adminclient-3 unregistered
    2022-02-26 17:34:12.369  INFO 48968 --- [| adminclient-3] org.apache.kafka.common.metrics.Metrics  : Metrics scheduler closed
    2022-02-26 17:34:12.369  INFO 48968 --- [| adminclient-3] org.apache.kafka.common.metrics.Metrics  : Closing reporter org.apache.kafka.common.metrics.JmxReporter
    2022-02-26 17:34:12.369  INFO 48968 --- [| adminclient-3] org.apache.kafka.common.metrics.Metrics  : Metrics reporters closed
    2022-02-26 17:34:12.372  INFO 48968 --- [           main] o.s.c.s.binder.DefaultBinderFactory      : Creating binder: ktable
    2022-02-26 17:34:12.442  INFO 48968 --- [           main] o.s.c.s.binder.DefaultBinderFactory      : Caching the binder: ktable
    2022-02-26 17:34:12.442  INFO 48968 --- [           main] o.s.c.s.binder.DefaultBinderFactory      : Retrieving cached binder: ktable
    2022-02-26 17:34:12.446  INFO 48968 --- [           main] o.a.k.clients.admin.AdminClientConfig    : AdminClientConfig values: 
            bootstrap.servers = [localhost:9092]
    ...
    2022-02-26 17:34:12.450  INFO 48968 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka version: 2.7.2
    2022-02-26 17:34:12.450  INFO 48968 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka commitId: 37a1cc36bf4d76f3
    2022-02-26 17:34:12.450  INFO 48968 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka startTimeMs: 1645889652450
    2022-02-26 17:34:12.496  INFO 48968 --- [| adminclient-4] o.a.kafka.common.utils.AppInfoParser     : App info kafka.admin.client for adminclient-4 unregistered
    2022-02-26 17:34:12.499  INFO 48968 --- [| adminclient-4] org.apache.kafka.common.metrics.Metrics  : Metrics scheduler closed
    2022-02-26 17:34:12.499  INFO 48968 --- [| adminclient-4] org.apache.kafka.common.metrics.Metrics  : Closing reporter org.apache.kafka.common.metrics.JmxReporter
    2022-02-26 17:34:12.499  INFO 48968 --- [| adminclient-4] org.apache.kafka.common.metrics.Metrics  : Metrics reporters closed
    2022-02-26 17:34:12.529  INFO 48968 --- [           main] org.apache.kafka.streams.StreamsConfig   : StreamsConfig values: 
            acceptable.recovery.lag = 10000
            application.id = bifunctionProcessor-applicationId
            application.server = 
            bootstrap.servers = [localhost:9092]
    ...
    2022-02-26 17:34:12.569  WARN 48968 --- [           main] o.a.k.s.p.internals.StateDirectory       : Using /tmp directory in the state.dir property can cause failures with writing the checkpoint file due to the fact that this directory can be cleared by the OS
    2022-02-26 17:34:12.668  INFO 48968 --- [           main] o.a.k.s.p.internals.StateDirectory       : Reading UUID from process file: 3b7f2cd4-987b-4c83-ab63-f711e6925e2b
    2022-02-26 17:34:12.676  INFO 48968 --- [           main] org.apache.kafka.streams.KafkaStreams    : stream-client [bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b] Kafka Streams version: 2.7.2
    2022-02-26 17:34:12.677  INFO 48968 --- [           main] org.apache.kafka.streams.KafkaStreams    : stream-client [bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b] Kafka Streams commit ID: 37a1cc36bf4d76f3
    2022-02-26 17:34:12.683  INFO 48968 --- [           main] o.a.k.clients.admin.AdminClientConfig    : AdminClientConfig values: 
            bootstrap.servers = [localhost:9092]
    ...
    2022-02-26 17:34:12.690  WARN 48968 --- [           main] o.a.k.clients.admin.AdminClientConfig    : The configuration 'spring.json.trusted.packages' was supplied but isn't a known config.
    2022-02-26 17:34:12.690  WARN 48968 --- [           main] o.a.k.clients.admin.AdminClientConfig    : The configuration 'spring.json.type.mapping' was supplied but isn't a known config.
    2022-02-26 17:34:12.690  INFO 48968 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka version: 2.7.2
    2022-02-26 17:34:12.690  INFO 48968 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka commitId: 37a1cc36bf4d76f3
    2022-02-26 17:34:12.690  INFO 48968 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka startTimeMs: 1645889652690
    2022-02-26 17:34:12.691  INFO 48968 --- [           main] o.a.k.s.p.internals.StreamThread         : stream-thread [bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1] Creating restore consumer client
    2022-02-26 17:34:12.704  INFO 48968 --- [           main] o.a.k.clients.consumer.ConsumerConfig    : ConsumerConfig values: 
            allow.auto.create.topics = true
            auto.commit.interval.ms = 5000
            auto.offset.reset = none
            bootstrap.servers = [localhost:9092]
    ...
    2022-02-26 17:34:12.757  WARN 48968 --- [           main] o.a.k.clients.consumer.ConsumerConfig    : The configuration 'spring.json.trusted.packages' was supplied but isn't a known config.
    2022-02-26 17:34:12.757  WARN 48968 --- [           main] o.a.k.clients.consumer.ConsumerConfig    : The configuration 'spring.json.type.mapping' was supplied but isn't a known config.
    2022-02-26 17:34:12.758  INFO 48968 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka version: 2.7.2
    2022-02-26 17:34:12.758  INFO 48968 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka commitId: 37a1cc36bf4d76f3
    2022-02-26 17:34:12.758  INFO 48968 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka startTimeMs: 1645889652758
    2022-02-26 17:34:12.770  INFO 48968 --- [           main] o.a.k.s.p.internals.StreamThread         : stream-thread [bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1] Creating thread producer client
    2022-02-26 17:34:12.781  INFO 48968 --- [           main] o.a.k.clients.producer.ProducerConfig    : ProducerConfig values: 
            acks = 1
            batch.size = 16384
            bootstrap.servers = [localhost:9092]
    ...
    2022-02-26 17:34:12.815  WARN 48968 --- [           main] o.a.k.clients.producer.ProducerConfig    : The configuration 'spring.json.trusted.packages' was supplied but isn't a known config.
    2022-02-26 17:34:12.815  WARN 48968 --- [           main] o.a.k.clients.producer.ProducerConfig    : The configuration 'spring.json.type.mapping' was supplied but isn't a known config.
    2022-02-26 17:34:12.815  INFO 48968 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka version: 2.7.2
    2022-02-26 17:34:12.815  INFO 48968 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka commitId: 37a1cc36bf4d76f3
    2022-02-26 17:34:12.815  INFO 48968 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka startTimeMs: 1645889652815
    2022-02-26 17:34:12.823  INFO 48968 --- [           main] o.a.k.s.p.internals.StreamThread         : stream-thread [bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1] Creating consumer client
    2022-02-26 17:34:12.826  INFO 48968 --- [           main] o.a.k.clients.consumer.ConsumerConfig    : ConsumerConfig values: 
            allow.auto.create.topics = false
            auto.commit.interval.ms = 5000
            auto.offset.reset = earliest
            bootstrap.servers = [localhost:9092]
    ...
    2022-02-26 17:34:12.831  INFO 48968 --- [read-1-producer] org.apache.kafka.clients.Metadata        : [Producer clientId=bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1-producer] Cluster ID: a8JB43BlSs6nND8wGb1LkA
    2022-02-26 17:34:12.845  INFO 48968 --- [           main] o.a.k.s.p.i.a.AssignorConfiguration      : stream-thread [bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1-consumer] Cooperative rebalancing enabled now
    2022-02-26 17:34:12.869  WARN 48968 --- [           main] o.a.k.clients.consumer.ConsumerConfig    : The configuration 'spring.json.type.mapping' was supplied but isn't a known config.
    2022-02-26 17:34:12.869  WARN 48968 --- [           main] o.a.k.clients.consumer.ConsumerConfig    : The configuration 'spring.json.trusted.packages' was supplied but isn't a known config.
    2022-02-26 17:34:12.869  INFO 48968 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka version: 2.7.2
    2022-02-26 17:34:12.869  INFO 48968 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka commitId: 37a1cc36bf4d76f3
    2022-02-26 17:34:12.869  INFO 48968 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka startTimeMs: 1645889652869
    2022-02-26 17:34:12.880  INFO 48968 --- [           main] org.apache.kafka.streams.KafkaStreams    : stream-client [bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b] State transition from CREATED to REBALANCING
    2022-02-26 17:34:12.880  INFO 48968 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1] Starting
    2022-02-26 17:34:12.881  INFO 48968 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1] State transition from CREATED to STARTING
    2022-02-26 17:34:12.881  INFO 48968 --- [-StreamThread-1] o.a.k.clients.consumer.KafkaConsumer     : [Consumer clientId=bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Subscribed to topic(s): rules.topic, topic
    2022-02-26 17:34:12.911  INFO 48968 --- [-StreamThread-1] org.apache.kafka.clients.Metadata        : [Consumer clientId=bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Cluster ID: a8JB43BlSs6nND8wGb1LkA
    2022-02-26 17:34:12.921  INFO 48968 --- [-StreamThread-1] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Discovered group coordinator localhost:9092 (id: 2147482646 rack: null)
    2022-02-26 17:34:12.923  INFO 48968 --- [-StreamThread-1] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] (Re-)joining group
    2022-02-26 17:34:12.984  INFO 48968 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1] Processed 0 total records, ran 0 punctuators, and committed 0 total tasks since the last update
    2022-02-26 17:34:13.010  INFO 48968 --- [-StreamThread-1] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] (Re-)joining group
    2022-02-26 17:34:13.064  INFO 48968 --- [           main] o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port 9000
    2022-02-26 17:34:13.079  INFO 48968 --- [           main] amsProducerBifunctionConsumerApplication : Started SpringBootKafkaStreamsProducerBifunctionConsumerApplication in 4.775 seconds (JVM running for 5.24)
    2022-02-26 17:34:13.087  INFO 48968 --- [-StreamThread-1] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Successfully joined group with generation Generation{generationId=1, memberId='bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1-consumer-47c025f3-08bc-419f-ab0c-993f887d355b', protocol='stream'}
    2022-02-26 17:34:13.187  INFO 48968 --- [-StreamThread-1] a.k.s.p.i.a.HighAvailabilityTaskAssignor : Decided on assignment: {3b7f2cd4-987b-4c83-ab63-f711e6925e2b=[activeTasks: ([0_0]) standbyTasks: ([]) prevActiveTasks: ([]) prevStandbyTasks: ([0_0]) changelogOffsetTotalsByTask: ([0_0=0]) taskLagTotals: ([0_0=0]) capacity: 1 assigned: 1]} with no followup probing rebalance.
    2022-02-26 17:34:13.189  INFO 48968 --- [-StreamThread-1] o.a.k.s.p.i.StreamsPartitionAssignor     : stream-thread [bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1-consumer] Assigned tasks [0_0] including stateful [0_0] to clients as: 
    3b7f2cd4-987b-4c83-ab63-f711e6925e2b=[activeTasks: ([0_0]) standbyTasks: ([])].
    2022-02-26 17:34:13.195  INFO 48968 --- [-StreamThread-1] o.a.k.s.p.i.StreamsPartitionAssignor     : stream-thread [bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1-consumer] Client 3b7f2cd4-987b-4c83-ab63-f711e6925e2b per-consumer assignment:
            prev owned active {}
            prev owned standby {bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1-consumer-47c025f3-08bc-419f-ab0c-993f887d355b=[0_0]}
            assigned active {bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1-consumer-47c025f3-08bc-419f-ab0c-993f887d355b=[0_0]}
            revoking active {}      assigned standby {}
    
    2022-02-26 17:34:13.195  INFO 48968 --- [-StreamThread-1] o.a.k.s.p.i.StreamsPartitionAssignor     : stream-thread [bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1-consumer] Finished stable assignment of tasks, no followup rebalances required.
    2022-02-26 17:34:13.195  INFO 48968 --- [-StreamThread-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Finished assignment for group at generation 1: {bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1-consumer-47c025f3-08bc-419f-ab0c-993f887d355b=Assignment(partitions=[rules.topic-0, topic-0], userDataSize=56)}
    2022-02-26 17:34:13.360  INFO 48968 --- [-StreamThread-1] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Successfully synced group in generation Generation{generationId=1, memberId='bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1-consumer-47c025f3-08bc-419f-ab0c-993f887d355b', protocol='stream'}
    2022-02-26 17:34:13.361  INFO 48968 --- [-StreamThread-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Updating assignment with
            Assigned partitions:                       [topic-0, rules.topic-0]
            Current owned partitions:                  []
            Added partitions (assigned - owned):       [topic-0, rules.topic-0]
            Revoked partitions (owned - assigned):     []
    
    2022-02-26 17:34:13.361  INFO 48968 --- [-StreamThread-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Notifying assignor about the new Assignment(partitions=[rules.topic-0, topic-0], userDataSize=56)
    2022-02-26 17:34:13.362  INFO 48968 --- [-StreamThread-1] o.a.k.s.p.i.StreamsPartitionAssignor     : stream-thread [bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1-consumer] No followup rebalance was requested, resetting the rebalance schedule.
    2022-02-26 17:34:13.363  INFO 48968 --- [-StreamThread-1] o.a.k.s.processor.internals.TaskManager  : stream-thread [bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1] Handle new assignment with:
            New active tasks: [0_0]
            New standby tasks: []
            Existing active tasks: []
            Existing standby tasks: []
    2022-02-26 17:34:13.383  INFO 48968 --- [-StreamThread-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Adding newly assigned partitions: topic-0, rules.topic-0
    2022-02-26 17:34:13.383  INFO 48968 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1] State transition from STARTING to PARTITIONS_ASSIGNED
    2022-02-26 17:34:13.503  INFO 48968 --- [r-applicationId] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Found no committed offset for partition topic-0
    2022-02-26 17:34:13.503  INFO 48968 --- [r-applicationId] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Found no committed offset for partition rules.topic-0
    2022-02-26 17:34:13.795  INFO 48968 --- [-StreamThread-1] o.a.k.s.s.i.RocksDBTimestampedStore      : Opening store rules.topic-STATE-STORE-0000000001 in regular mode
    2022-02-26 17:34:13.799  INFO 48968 --- [-StreamThread-1] o.a.k.s.processor.internals.StreamTask   : stream-thread [bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1] task [0_0] Initialized
    2022-02-26 17:34:13.829  INFO 48968 --- [-StreamThread-1] o.a.k.clients.consumer.KafkaConsumer     : [Consumer clientId=bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1-restore-consumer, groupId=null] Subscribed to partition(s): bifunctionProcessor-applicationId-rules.topic-STATE-STORE-0000000001-changelog-0
    2022-02-26 17:34:13.830  INFO 48968 --- [-StreamThread-1] o.a.k.c.c.internals.SubscriptionState    : [Consumer clientId=bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1-restore-consumer, groupId=null] Seeking to EARLIEST offset of partition bifunctionProcessor-applicationId-rules.topic-STATE-STORE-0000000001-changelog-0
    2022-02-26 17:34:13.841  INFO 48968 --- [-StreamThread-1] org.apache.kafka.clients.Metadata        : [Consumer clientId=bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1-restore-consumer, groupId=null] Cluster ID: a8JB43BlSs6nND8wGb1LkA
    2022-02-26 17:34:13.855  INFO 48968 --- [-StreamThread-1] o.a.k.c.c.internals.SubscriptionState    : [Consumer clientId=bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1-restore-consumer, groupId=null] Resetting offset for partition bifunctionProcessor-applicationId-rules.topic-STATE-STORE-0000000001-changelog-0 to position FetchPosition{offset=0, offsetEpoch=Optional.empty, currentLeader=LeaderAndEpoch{leader=Optional[localhost:9092 (id: 1001 rack: null)], epoch=0}}.
    2022-02-26 17:34:13.957  INFO 48968 --- [-StreamThread-1] o.a.k.s.p.i.StoreChangelogReader         : stream-thread [bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1] Finished restoring changelog bifunctionProcessor-applicationId-rules.topic-STATE-STORE-0000000001-changelog-0 to store rules.topic-STATE-STORE-0000000001 with a total number of 0 records
    2022-02-26 17:34:13.965  INFO 48968 --- [-StreamThread-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Found no committed offset for partition topic-0
    2022-02-26 17:34:13.965  INFO 48968 --- [-StreamThread-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Found no committed offset for partition rules.topic-0
    2022-02-26 17:34:13.969  INFO 48968 --- [-StreamThread-1] o.a.k.s.processor.internals.StreamTask   : stream-thread [bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1] task [0_0] Restored and ready to run
    2022-02-26 17:34:13.970  INFO 48968 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1] Restoration took 586 ms for all tasks [0_0]
    2022-02-26 17:34:13.970  INFO 48968 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1] State transition from PARTITIONS_ASSIGNED to RUNNING
    2022-02-26 17:34:13.970  INFO 48968 --- [-StreamThread-1] org.apache.kafka.streams.KafkaStreams    : stream-client [bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b] State transition from REBALANCING to RUNNING
    2022-02-26 17:34:13.979  INFO 48968 --- [-StreamThread-1] o.a.k.c.c.internals.SubscriptionState    : [Consumer clientId=bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Resetting offset for partition topic-0 to position FetchPosition{offset=0, offsetEpoch=Optional.empty, currentLeader=LeaderAndEpoch{leader=Optional[localhost:9092 (id: 1001 rack: null)], epoch=0}}.
    2022-02-26 17:34:13.979  INFO 48968 --- [-StreamThread-1] o.a.k.c.c.internals.SubscriptionState    : [Consumer clientId=bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Resetting offset for partition rules.topic-0 to position FetchPosition{offset=0, offsetEpoch=Optional.empty, currentLeader=LeaderAndEpoch{leader=Optional[localhost:9092 (id: 1001 rack: null)], epoch=0}}.
    2022-02-26 17:34:55.977  INFO 48968 --- [ctor-http-nio-2] c.p.s.producer.Producer                  : #~#: Producing (key: 0123456789), action -> Action(id=b022f17f, sms=null, created=null, updated=null, status=null, smsRule=SmsRule(verb=BLOCK, allSenders=true, receiver=0123456789, timestamp=2022-02-26T17:34:55.977))
    2022-02-26 17:34:55.978  INFO 48968 --- [ctor-http-nio-2] o.a.k.clients.producer.ProducerConfig    : ProducerConfig values: 
            acks = 1
            batch.size = 16384
            bootstrap.servers = [localhost:9092]
    ...
    2022-02-26 17:34:55.982  INFO 48968 --- [ctor-http-nio-2] o.a.kafka.common.utils.AppInfoParser     : Kafka version: 2.7.2
    2022-02-26 17:34:55.983  INFO 48968 --- [ctor-http-nio-2] o.a.kafka.common.utils.AppInfoParser     : Kafka commitId: 37a1cc36bf4d76f3
    2022-02-26 17:34:55.983  INFO 48968 --- [ctor-http-nio-2] o.a.kafka.common.utils.AppInfoParser     : Kafka startTimeMs: 1645889695982
    2022-02-26 17:34:55.991  INFO 48968 --- [ad | producer-1] org.apache.kafka.clients.Metadata        : [Producer clientId=producer-1] Cluster ID: a8JB43BlSs6nND8wGb1LkA
    2022-02-26 17:35:07.181  INFO 48968 --- [ctor-http-nio-3] c.p.s.producer.Producer                  : #~#: Producing (key: 9876543210), action -> Action(id=42a5a6da, sms=Sms(body=string, sender=0000000000, receiver=9876543210, timestamp=2022-02-26T17:35:07.180), created=2022-02-26T17:35:07.180, updated=2022-02-26T17:35:07.180, status=CREATED, smsRule=null)
    2022-02-26 17:35:07.198  INFO 48968 --- [-StreamThread-1] c.p.s.KafkaBiFunctionConfiguration       : key: 9876543210, action: Action(id=42a5a6da, sms=Sms(body=string, sender=0000000000, receiver=9876543210, timestamp=2022-02-26T17:35:07), created=2022-02-26T17:35:07, updated=2022-02-26T17:35:07.198, status=PENDING, smsRule=null)
    2022-02-26 17:35:16.514  INFO 48968 --- [ctor-http-nio-4] c.p.s.producer.Producer                  : #~#: Producing (key: 0123456789), action -> Action(id=969b543c, sms=Sms(body=string, sender=0000000000, receiver=0123456789, timestamp=2022-02-26T17:35:16.513), created=2022-02-26T17:35:16.514, updated=2022-02-26T17:35:16.514, status=CREATED, smsRule=null)
    2022-02-26 17:35:16.522  INFO 48968 --- [-StreamThread-1] c.p.s.KafkaBiFunctionConfiguration       : key: 0123456789, action: Action(id=969b543c, sms=Sms(body=string, sender=0000000000, receiver=0123456789, timestamp=2022-02-26T17:35:16), created=2022-02-26T17:35:16, updated=2022-02-26T17:35:16.522, status=PENDING, smsRule=SmsRule(verb=BLOCK, allSenders=true, receiver=0123456789, timestamp=2022-02-26T17:34:55))
    2022-02-26 17:36:12.997  INFO 48968 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1] Processed 3 total records, ran 0 punctuators, and committed 2 total tasks since the last update
    2022-02-26 17:38:13.098  INFO 48968 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [bifunctionProcessor-applicationId-3b7f2cd4-987b-4c83-ab63-f711e6925e2b-StreamThread-1] Processed 0 total records, ran 0 punctuators, and committed 0 total tasks since the last updat
```
As we can see in the previous log's first line, *The following profiles are active: producer,bifunction,consumer*, as it is defined 
in [application.yml](src/main/resources/application.yml), but this won't let consumer profile to be loaded like the other two profiles: producer and bifunction. That 
    is the reason that we needed the other terminal with the execution of consumer profile separately. 

And so, on the consumer terminal:

```bash
    ...
    2022-02-26 17:35:43.468  INFO 49117 --- [           main] amsProducerBifunctionConsumerApplication : The following profiles are active: consumer
    2022-02-26 17:35:44.091  INFO 49117 --- [           main] faultConfiguringBeanFactoryPostProcessor : No bean named 'errorChannel' has been explicitly defined. Therefore, a default PublishSubscribeChannel will be created.
    2022-02-26 17:35:44.112  INFO 49117 --- [           main] faultConfiguringBeanFactoryPostProcessor : No bean named 'integrationHeaderChannelRegistry' has been explicitly defined. Therefore, a default DefaultHeaderChannelRegistry will be created.
    2022-02-26 17:35:44.334  INFO 49117 --- [           main] trationDelegate$BeanPostProcessorChecker : Bean 'org.springframework.integration.config.IntegrationManagementConfiguration' of type [org.springframework.integration.config.IntegrationManagementConfiguration] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
    2022-02-26 17:35:44.344  INFO 49117 --- [           main] trationDelegate$BeanPostProcessorChecker : Bean 'integrationChannelResolver' of type [org.springframework.integration.support.channel.BeanFactoryChannelResolver] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
    2022-02-26 17:35:44.344  INFO 49117 --- [           main] trationDelegate$BeanPostProcessorChecker : Bean 'integrationDisposableAutoCreatedBeans' of type [org.springframework.integration.config.annotation.Disposables] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
    2022-02-26 17:35:45.488  INFO 49117 --- [           main] .k.s.AbstractKafkaStreamsBinderProcessor : Binder Generated Kafka Streams Application ID: consumeService-applicationId
    2022-02-26 17:35:45.488  INFO 49117 --- [           main] .k.s.AbstractKafkaStreamsBinderProcessor : Use the binder generated application ID only for development and testing. 
    2022-02-26 17:35:45.488  INFO 49117 --- [           main] .k.s.AbstractKafkaStreamsBinderProcessor : For production deployments, please consider explicitly setting an application ID using a configuration property.
    2022-02-26 17:35:45.488  INFO 49117 --- [           main] .k.s.AbstractKafkaStreamsBinderProcessor : The generated applicationID is static and will be preserved over application restarts.
    2022-02-26 17:35:45.519  INFO 49117 --- [           main] .c.s.b.k.s.KafkaStreamsFunctionProcessor : Key Serde used for consumeService-in-0: org.apache.kafka.common.serialization.Serdes$StringSerde
    2022-02-26 17:35:45.641  INFO 49117 --- [           main] .c.s.b.k.s.KafkaStreamsFunctionProcessor : Value Serde used for consumeService-in-0: org.springframework.kafka.support.serializer.JsonSerde
    2022-02-26 17:35:45.641  INFO 49117 --- [           main] .k.s.AbstractKafkaStreamsBinderProcessor : Native decoding is enabled for consumeService-in-0. Inbound deserialization done at the broker.
    2022-02-26 17:35:45.818  INFO 49117 --- [           main] o.s.i.endpoint.EventDrivenConsumer       : Adding {logging-channel-adapter:_org.springframework.integration.errorLogger} as a subscriber to the 'errorChannel' channel
    2022-02-26 17:35:45.818  INFO 49117 --- [           main] o.s.i.channel.PublishSubscribeChannel    : Channel 'application.errorChannel' has 1 subscriber(s).
    2022-02-26 17:35:45.819  INFO 49117 --- [           main] o.s.i.endpoint.EventDrivenConsumer       : started bean '_org.springframework.integration.errorLogger'
    2022-02-26 17:35:45.820  INFO 49117 --- [           main] o.s.c.s.binder.DefaultBinderFactory      : Creating binder: kstream
    2022-02-26 17:35:45.899  INFO 49117 --- [           main] o.s.c.s.binder.DefaultBinderFactory      : Caching the binder: kstream
    2022-02-26 17:35:45.899  INFO 49117 --- [           main] o.s.c.s.binder.DefaultBinderFactory      : Retrieving cached binder: kstream
    2022-02-26 17:35:46.005  INFO 49117 --- [           main] o.a.k.clients.admin.AdminClientConfig    : AdminClientConfig values: 
            bootstrap.servers = [localhost:9092]
    ...
    2022-02-26 17:35:46.163  INFO 49117 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka version: 2.7.2
    2022-02-26 17:35:46.164  INFO 49117 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka commitId: 37a1cc36bf4d76f3
    2022-02-26 17:35:46.164  INFO 49117 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka startTimeMs: 1645889746161
    2022-02-26 17:35:46.525  INFO 49117 --- [| adminclient-1] o.a.kafka.common.utils.AppInfoParser     : App info kafka.admin.client for adminclient-1 unregistered
    2022-02-26 17:35:46.530  INFO 49117 --- [| adminclient-1] org.apache.kafka.common.metrics.Metrics  : Metrics scheduler closed
    2022-02-26 17:35:46.530  INFO 49117 --- [| adminclient-1] org.apache.kafka.common.metrics.Metrics  : Closing reporter org.apache.kafka.common.metrics.JmxReporter
    2022-02-26 17:35:46.530  INFO 49117 --- [| adminclient-1] org.apache.kafka.common.metrics.Metrics  : Metrics reporters closed
    2022-02-26 17:35:46.548  INFO 49117 --- [           main] org.apache.kafka.streams.StreamsConfig   : StreamsConfig values: 
            acceptable.recovery.lag = 10000
            application.id = consumeService-applicationId
            application.server = 
            bootstrap.servers = [localhost:9092]
    ...
    2022-02-26 17:35:46.573  INFO 49117 --- [           main] org.apache.kafka.streams.KafkaStreams    : stream-client [consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248] Kafka Streams version: 2.7.2
    2022-02-26 17:35:46.573  INFO 49117 --- [           main] org.apache.kafka.streams.KafkaStreams    : stream-client [consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248] Kafka Streams commit ID: 37a1cc36bf4d76f3
    2022-02-26 17:35:46.577  INFO 49117 --- [           main] o.a.k.clients.admin.AdminClientConfig    : AdminClientConfig values: 
            bootstrap.servers = [localhost:9092]
    ...
    2022-02-26 17:35:46.584  WARN 49117 --- [           main] o.a.k.clients.admin.AdminClientConfig    : The configuration 'spring.json.trusted.packages' was supplied but isn't a known config.
    2022-02-26 17:35:46.585  WARN 49117 --- [           main] o.a.k.clients.admin.AdminClientConfig    : The configuration 'spring.json.type.mapping' was supplied but isn't a known config.
    2022-02-26 17:35:46.585  INFO 49117 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka version: 2.7.2
    2022-02-26 17:35:46.585  INFO 49117 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka commitId: 37a1cc36bf4d76f3
    2022-02-26 17:35:46.585  INFO 49117 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka startTimeMs: 1645889746585
    2022-02-26 17:35:46.587  INFO 49117 --- [           main] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1] Creating restore consumer client
    2022-02-26 17:35:46.597  INFO 49117 --- [           main] o.a.k.clients.consumer.ConsumerConfig    : ConsumerConfig values: 
            allow.auto.create.topics = true
            auto.commit.interval.ms = 5000
            auto.offset.reset = none
            bootstrap.servers = [localhost:9092]
    ...
    2022-02-26 17:35:46.638  WARN 49117 --- [           main] o.a.k.clients.consumer.ConsumerConfig    : The configuration 'spring.json.trusted.packages' was supplied but isn't a known config.
    2022-02-26 17:35:46.638  WARN 49117 --- [           main] o.a.k.clients.consumer.ConsumerConfig    : The configuration 'spring.json.type.mapping' was supplied but isn't a known config.
    2022-02-26 17:35:46.638  INFO 49117 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka version: 2.7.2
    2022-02-26 17:35:46.638  INFO 49117 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka commitId: 37a1cc36bf4d76f3
    2022-02-26 17:35:46.638  INFO 49117 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka startTimeMs: 1645889746638
    2022-02-26 17:35:46.647  INFO 49117 --- [           main] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1] Creating thread producer client
    2022-02-26 17:35:46.653  INFO 49117 --- [           main] o.a.k.clients.producer.ProducerConfig    : ProducerConfig values: 
            acks = 1
            batch.size = 16384
            bootstrap.servers = [localhost:9092]
    ...
    2022-02-26 17:35:46.679  WARN 49117 --- [           main] o.a.k.clients.producer.ProducerConfig    : The configuration 'spring.json.trusted.packages' was supplied but isn't a known config.
    2022-02-26 17:35:46.679  WARN 49117 --- [           main] o.a.k.clients.producer.ProducerConfig    : The configuration 'spring.json.type.mapping' was supplied but isn't a known config.
    2022-02-26 17:35:46.679  INFO 49117 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka version: 2.7.2
    2022-02-26 17:35:46.679  INFO 49117 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka commitId: 37a1cc36bf4d76f3
    2022-02-26 17:35:46.679  INFO 49117 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka startTimeMs: 1645889746679
    2022-02-26 17:35:46.685  INFO 49117 --- [           main] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1] Creating consumer client
    2022-02-26 17:35:46.688  INFO 49117 --- [           main] o.a.k.clients.consumer.ConsumerConfig    : ConsumerConfig values: 
            allow.auto.create.topics = false
            auto.commit.interval.ms = 5000
            auto.offset.reset = earliest
            bootstrap.servers = [localhost:9092]
    ...
    2022-02-26 17:35:46.692  INFO 49117 --- [read-1-producer] org.apache.kafka.clients.Metadata        : [Producer clientId=consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1-producer] Cluster ID: a8JB43BlSs6nND8wGb1LkA
    2022-02-26 17:35:46.700  INFO 49117 --- [           main] o.a.k.s.p.i.a.AssignorConfiguration      : stream-thread [consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1-consumer] Cooperative rebalancing enabled now
    2022-02-26 17:35:46.720  WARN 49117 --- [           main] o.a.k.clients.consumer.ConsumerConfig    : The configuration 'spring.json.type.mapping' was supplied but isn't a known config.
    2022-02-26 17:35:46.720  WARN 49117 --- [           main] o.a.k.clients.consumer.ConsumerConfig    : The configuration 'spring.json.trusted.packages' was supplied but isn't a known config.
    2022-02-26 17:35:46.720  INFO 49117 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka version: 2.7.2
    2022-02-26 17:35:46.720  INFO 49117 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka commitId: 37a1cc36bf4d76f3
    2022-02-26 17:35:46.721  INFO 49117 --- [           main] o.a.kafka.common.utils.AppInfoParser     : Kafka startTimeMs: 1645889746720
    2022-02-26 17:35:46.729  INFO 49117 --- [           main] org.apache.kafka.streams.KafkaStreams    : stream-client [consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248] State transition from CREATED to REBALANCING
    2022-02-26 17:35:46.729  INFO 49117 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1] Starting
    2022-02-26 17:35:46.730  INFO 49117 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1] State transition from CREATED to STARTING
    2022-02-26 17:35:46.730  INFO 49117 --- [-StreamThread-1] o.a.k.clients.consumer.KafkaConsumer     : [Consumer clientId=consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1-consumer, groupId=consumeService-applicationId] Subscribed to topic(s): active.topic
    2022-02-26 17:35:46.752  INFO 49117 --- [-StreamThread-1] org.apache.kafka.clients.Metadata        : [Consumer clientId=consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1-consumer, groupId=consumeService-applicationId] Cluster ID: a8JB43BlSs6nND8wGb1LkA
    2022-02-26 17:35:46.754  INFO 49117 --- [-StreamThread-1] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1-consumer, groupId=consumeService-applicationId] Discovered group coordinator localhost:9092 (id: 2147482646 rack: null)
    2022-02-26 17:35:46.757  INFO 49117 --- [-StreamThread-1] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1-consumer, groupId=consumeService-applicationId] (Re-)joining group
    2022-02-26 17:35:46.786  INFO 49117 --- [-StreamThread-1] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1-consumer, groupId=consumeService-applicationId] (Re-)joining group
    2022-02-26 17:35:46.795  INFO 49117 --- [-StreamThread-1] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1-consumer, groupId=consumeService-applicationId] Successfully joined group with generation Generation{generationId=1, memberId='consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1-consumer-be52c5ee-ef7a-4528-ab78-5b47bd9067c4', protocol='stream'}
    2022-02-26 17:35:46.828  INFO 49117 --- [-StreamThread-1] a.k.s.p.i.a.HighAvailabilityTaskAssignor : Decided on assignment: {8692e1ea-dd77-4672-b0f4-d83c1db14248=[activeTasks: ([0_0]) standbyTasks: ([]) prevActiveTasks: ([]) prevStandbyTasks: ([]) changelogOffsetTotalsByTask: ([]) taskLagTotals: ([]) capacity: 1 assigned: 1]} with no followup probing rebalance.
    2022-02-26 17:35:46.829  INFO 49117 --- [-StreamThread-1] o.a.k.s.p.i.StreamsPartitionAssignor     : stream-thread [consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1-consumer] Assigned tasks [0_0] including stateful [] to clients as: 
    8692e1ea-dd77-4672-b0f4-d83c1db14248=[activeTasks: ([0_0]) standbyTasks: ([])].
    2022-02-26 17:35:46.834  INFO 49117 --- [-StreamThread-1] o.a.k.s.p.i.StreamsPartitionAssignor     : stream-thread [consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1-consumer] Client 8692e1ea-dd77-4672-b0f4-d83c1db14248 per-consumer assignment:
            prev owned active {}
            prev owned standby {consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1-consumer-be52c5ee-ef7a-4528-ab78-5b47bd9067c4=[]}
            assigned active {consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1-consumer-be52c5ee-ef7a-4528-ab78-5b47bd9067c4=[0_0]}
            revoking active {}      assigned standby {}
    
    2022-02-26 17:35:46.834  INFO 49117 --- [-StreamThread-1] o.a.k.s.p.i.StreamsPartitionAssignor     : stream-thread [consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1-consumer] Finished stable assignment of tasks, no followup rebalances required.
    2022-02-26 17:35:46.835  INFO 49117 --- [-StreamThread-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1-consumer, groupId=consumeService-applicationId] Finished assignment for group at generation 1: {consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1-consumer-be52c5ee-ef7a-4528-ab78-5b47bd9067c4=Assignment(partitions=[active.topic-0], userDataSize=48)}
    2022-02-26 17:35:46.842  INFO 49117 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1] Processed 0 total records, ran 0 punctuators, and committed 0 total tasks since the last update
    2022-02-26 17:35:46.849  INFO 49117 --- [-StreamThread-1] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1-consumer, groupId=consumeService-applicationId] Successfully synced group in generation Generation{generationId=1, memberId='consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1-consumer-be52c5ee-ef7a-4528-ab78-5b47bd9067c4', protocol='stream'}
    2022-02-26 17:35:46.850  INFO 49117 --- [-StreamThread-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1-consumer, groupId=consumeService-applicationId] Updating assignment with
            Assigned partitions:                       [active.topic-0]
            Current owned partitions:                  []
            Added partitions (assigned - owned):       [active.topic-0]
            Revoked partitions (owned - assigned):     []
    
    2022-02-26 17:35:46.850  INFO 49117 --- [-StreamThread-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1-consumer, groupId=consumeService-applicationId] Notifying assignor about the new Assignment(partitions=[active.topic-0], userDataSize=48)
    2022-02-26 17:35:46.851  INFO 49117 --- [-StreamThread-1] o.a.k.s.p.i.StreamsPartitionAssignor     : stream-thread [consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1-consumer] No followup rebalance was requested, resetting the rebalance schedule.
    2022-02-26 17:35:46.852  INFO 49117 --- [-StreamThread-1] o.a.k.s.processor.internals.TaskManager  : stream-thread [consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1] Handle new assignment with:
            New active tasks: [0_0]
            New standby tasks: []
            Existing active tasks: []
            Existing standby tasks: []
    2022-02-26 17:35:46.878  INFO 49117 --- [-StreamThread-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1-consumer, groupId=consumeService-applicationId] Adding newly assigned partitions: active.topic-0
    2022-02-26 17:35:46.878  INFO 49117 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1] State transition from STARTING to PARTITIONS_ASSIGNED
    2022-02-26 17:35:46.887  INFO 49117 --- [-StreamThread-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1-consumer, groupId=consumeService-applicationId] Found no committed offset for partition active.topic-0
    2022-02-26 17:35:46.895  INFO 49117 --- [           main] o.s.b.web.embedded.netty.NettyWebServer  : Netty started on port 8080
    2022-02-26 17:35:46.908  INFO 49117 --- [-StreamThread-1] o.a.k.c.c.internals.SubscriptionState    : [Consumer clientId=consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1-consumer, groupId=consumeService-applicationId] Resetting offset for partition active.topic-0 to position FetchPosition{offset=0, offsetEpoch=Optional.empty, currentLeader=LeaderAndEpoch{leader=Optional[localhost:9092 (id: 1001 rack: null)], epoch=0}}.
    2022-02-26 17:35:46.909  INFO 49117 --- [           main] amsProducerBifunctionConsumerApplication : Started SpringBootKafkaStreamsProducerBifunctionConsumerApplication in 3.931 seconds (JVM running for 4.544)
    2022-02-26 17:35:46.943  INFO 49117 --- [-StreamThread-1] o.a.k.s.processor.internals.StreamTask   : stream-thread [consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1] task [0_0] Initialized
    2022-02-26 17:35:46.949  INFO 49117 --- [-StreamThread-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1-consumer, groupId=consumeService-applicationId] Found no committed offset for partition active.topic-0
    2022-02-26 17:35:46.952  INFO 49117 --- [-StreamThread-1] o.a.k.s.processor.internals.StreamTask   : stream-thread [consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1] task [0_0] Restored and ready to run
    2022-02-26 17:35:46.953  INFO 49117 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1] Restoration took 75 ms for all tasks [0_0]
    2022-02-26 17:35:46.953  INFO 49117 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1] State transition from PARTITIONS_ASSIGNED to RUNNING
    2022-02-26 17:35:46.954  INFO 49117 --- [-StreamThread-1] org.apache.kafka.streams.KafkaStreams    : stream-client [consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248] State transition from REBALANCING to RUNNING
    2022-02-26 17:35:47.009  INFO 49117 --- [-StreamThread-1] c.p.s.KafkaConsumerConfiguration         : Action consumed [Action(id=42a5a6da, sms=Sms(body=string, sender=0000000000, receiver=9876543210, timestamp=2022-02-26T17:35:07), created=2022-02-26T17:35:07, updated=2022-02-26T17:35:07, status=DELIVERING, smsRule=null)] action.sms.body: [string]
    2022-02-26 17:37:46.906  INFO 49117 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1] Processed 1 total records, ran 0 punctuators, and committed 1 total tasks since the last update
    2022-02-26 17:39:46.915  INFO 49117 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-8692e1ea-dd77-4672-b0f4-d83c1db14248-StreamThread-1] Processed 0 total records, ran 0 punctuators, and committed 0 total tasks since the last update
```

### 1.4. Benchmarking 

For the benchmarking we use [ab - Apache HTTP server benchmarking tool](https://httpd.apache.org/docs/2.4/programs/ab.html).
ab is a tool for benchmarking your Apache Hypertext Transfer Protocol (HTTP) server. It is designed to give you an 
impression of how your current Apache installation performs. This especially shows you how many requests per second the 
Apache installation is capable of serving.        

### 1.4.1 Benchmarking Staging Environment 
Right after you follow the steps on section about [Working on the Staging Environment](#wotse), you can run the ab
tool as follows:
```bash
  ➜  ab -v1 -n2000 -c2000 -T'application/json' -ppostfile  http://localhost:9000/kafka/send-sms
  This is ApacheBench, Version 2.3 <$Revision: 1879490 $>
  Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
  Licensed to The Apache Software Foundation, http://www.apache.org/
  
  Benchmarking localhost (be patient)
  Completed 200 requests
  Completed 400 requests
  Completed 600 requests
  Completed 800 requests
  Completed 1000 requests
  Completed 1200 requests
  Completed 1400 requests
  Completed 1600 requests
  Completed 1800 requests
  Completed 2000 requests
  Finished 2000 requests
  
  
  Server Software:        
  Server Hostname:        localhost
  Server Port:            9000
  
  Document Path:          /kafka/send-sms
  Document Length:        73 bytes
  
  Concurrency Level:      2000
  Time taken for tests:   3.096 seconds
  Complete requests:      2000
  Failed requests:        0
  Total transferred:      288000 bytes
  Total body sent:        424000
  HTML transferred:       146000 bytes
  Requests per second:    645.99 [#/sec] (mean)
  Time per request:       3096.041 [ms] (mean)
  Time per request:       1.548 [ms] (mean, across all concurrent requests)
  Transfer rate:          90.84 [Kbytes/sec] received
                          133.74 kb/s sent
                          224.58 kb/s total
  
  Connection Times (ms)
                min  mean[+/-sd] median   max
  Connect:        0   82  36.7     80     143
  Processing:    81 1488 795.8   1538    2927
  Waiting:        7 1487 796.5   1537    2927
  Total:        150 1570 764.9   1612    2972
  
  Percentage of the requests served within a certain time (ms)
    50%   1612
    66%   1975
    75%   2188
    80%   2320
    90%   2618
    95%   2756
    98%   2911
    99%   2944
   100%   2972 (longest request)
```

In order to increase the available operational power locally, we stop bifunction and consumer containers and run the same benchmark test:

```bash
  ➜ ab -v1 -n2000 -c2000 -T'application/json' -ppostfile  http://localhost:9000/kafka/send-sms
  This is ApacheBench, Version 2.3 <$Revision: 1879490 $>
  Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
  Licensed to The Apache Software Foundation, http://www.apache.org/
  
  Benchmarking localhost (be patient)
  Completed 200 requests
  Completed 400 requests
  Completed 600 requests
  Completed 800 requests
  Completed 1000 requests
  Completed 1200 requests
  Completed 1400 requests
  Completed 1600 requests
  Completed 1800 requests
  Completed 2000 requests
  Finished 2000 requests
  
  
  Server Software:        
  Server Hostname:        localhost
  Server Port:            9000
  
  Document Path:          /kafka/send-sms
  Document Length:        73 bytes
  
  Concurrency Level:      2000
  Time taken for tests:   1.414 seconds
  Complete requests:      2000
  Failed requests:        0
  Total transferred:      288000 bytes
  Total body sent:        424000
  HTML transferred:       146000 bytes
  Requests per second:    1414.04 [#/sec] (mean)
  Time per request:       1414.385 [ms] (mean)
  Time per request:       0.707 [ms] (mean, across all concurrent requests)
  Transfer rate:          198.85 [Kbytes/sec] received
                          292.75 kb/s sent
                          491.60 kb/s total
  
  Connection Times (ms)
                min  mean[+/-sd] median   max
  Connect:        0   42  16.9     41      75
  Processing:    15  676 384.8    690    1323
  Waiting:        1  676 384.9    690    1323
  Total:         76  718 368.0    731    1339
  
  Percentage of the requests served within a certain time (ms)
    50%    731
    66%    927
    75%   1072
    80%   1089
    90%   1209
    95%   1280
    98%   1315
    99%   1326
   100%   1339 (longest request
```
The result of stopping bifunction and consumer local containers was the increasing of the mean requests per second locally 
from 645.99 to 1414.04. 

### 1.4.2 Benchmarking Development Enviroment
Right after you follow the steps on section about [Working on the Development Environment](#wotde), you can run the ab 
tool as follows:
```bash
  ➜  ab -v1 -n2000 -c2000 -T'application/json' -ppostfile  http://localhost:9000/kafka/send-sms
  This is ApacheBench, Version 2.3 <$Revision: 1879490 $>
  Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
  Licensed to The Apache Software Foundation, http://www.apache.org/
  
  Benchmarking localhost (be patient)
  Completed 200 requests
  Completed 400 requests
  Completed 600 requests
  apr_socket_recv: Connection reset by peer (54)
  Total of 652 requests completed

  ➜  ab -v1 -n2000 -c2000 -T'application/json' -ppostfile  http://localhost:9000/kafka/send-sms
  This is ApacheBench, Version 2.3 <$Revision: 1879490 $>
  Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
  Licensed to The Apache Software Foundation, http://www.apache.org/
  
  Benchmarking localhost (be patient)
  apr_socket_recv: Connection reset by peer (54)
  Total of 1 requests completed
  ➜  spring-boot-kafka-streams-producer-bifunction-consumer git:(refactoring-procucer-with-webflux) ✗ ab -v1 -n2000 -c2000 -T'application/json' -ppostfile  http://localhost:9000/kafka/send-sms
  This is ApacheBench, Version 2.3 <$Revision: 1879490 $>
  Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
  Licensed to The Apache Software Foundation, http://www.apache.org/
  
  Benchmarking localhost (be patient)
  Completed 200 requests
  Completed 400 requests
  Completed 600 requests
  apr_socket_recv: Connection reset by peer (54)
  Total of 696 requests completed
  ➜  spring-boot-kafka-streams-producer-bifunction-consumer git:(refactoring-procucer-with-webflux) ✗ ab -v1 -n2000 -c2000 -T'application/json' -ppostfile  http://localhost:9000/kafka/send-sms
  This is ApacheBench, Version 2.3 <$Revision: 1879490 $>
  Copyright 1996 Adam Twiss, Zeus Technology Ltd, http://www.zeustech.net/
  Licensed to The Apache Software Foundation, http://www.apache.org/
  
  Benchmarking localhost (be patient)
  Completed 200 requests
  Completed 400 requests
  Completed 600 requests
  Completed 800 requests
  Completed 1000 requests
  Completed 1200 requests
  Completed 1400 requests
  Completed 1600 requests
  Completed 1800 requests
  Completed 2000 requests
  Finished 2000 requests
  
  
  Server Software:        
  Server Hostname:        localhost
  Server Port:            9000
  
  Document Path:          /kafka/send-sms
  Document Length:        73 bytes
  
  Concurrency Level:      2000
  Time taken for tests:   1.414 seconds
  Complete requests:      2000
  Failed requests:        0
  Total transferred:      288000 bytes
  Total body sent:        424000
  HTML transferred:       146000 bytes
  Requests per second:    1414.04 [#/sec] (mean)
  Time per request:       1414.385 [ms] (mean)
  Time per request:       0.707 [ms] (mean, across all concurrent requests)
  Transfer rate:          198.85 [Kbytes/sec] received
                          292.75 kb/s sent
                          491.60 kb/s total
  
  Connection Times (ms)
                min  mean[+/-sd] median   max
  Connect:        0   42  16.9     41      75
  Processing:    15  676 384.8    690    1323
  Waiting:        1  676 384.9    690    1323
  Total:         76  718 368.0    731    1339
  
  Percentage of the requests served within a certain time (ms)
    50%    731
    66%    927
    75%   1072
    80%   1089
    90%   1209
    95%   1280
    98%   1315
    99%   1326
   100%   1339 (longest request)
```

## <a name="sbp">2. Spring Boot Profiles</a>
Spring boot gives the option to the application to have profiles which are used to separate with components, services
or just java beans will be initialised at boot time. According to microservices architectural guidelines, every
microservice should do exactly one job and no more. Therefore, we separate the actions of this app at staging 
environment into to different spring boot profiles as they are listed below.

- producer
- consumer
- bifunction

You can check yourself for the seperated profiles in [application.yml](src/main/resources/application.yml) in the
source code and then check the [docker-compose.yml](docker-compose.yml) for the use of the different profiles for the
related containers.

### 2.1. Producer, Consumer, Function, and BiFunction for Kafka Streams

As you can see in line 3 of [application.yml](src/main/resources/application.yml) file, if *SPRING_PROFILES_ACTIVE* is
not defined, the default profiles that will be initialized on spring boot are: producer, consumer, and bifunction. 
Well... :) For the time being, only producer and bifunction can run on the same JVM in this setup.
While you have deployed kafka separately with [docker-compose-kafka-only.yml](docker-compose-kafka-only.yml), if we
clean, package and run java jar with the following two lines, you will - by default - have started producer and 
bifunction profiles. Therefore, we need an extra terminal in the same directory for consumer's execution.

```bash
    mvn clean package
    java -jar target/*.jar
```
or
```bash
    mvn spring-boot:run
```
and on a new terminal
```bash
    java -Dspring.profiles.active=consumer -jar target/*.jar
```
or on the new terminal you can use maven's spring-boot phase to pass the profiles' parameter through *spring-boot.run.profiles* as it is
used in the following command:
```bash
    mvn spring-boot:run -Dspring-boot.run.profiles=consumer
```

### 2.2. Producer to Kafka

In case you want to deploy only producer spring boot profile, then we need the following two lines. The trick is to
define the *spring.profiles.active* in the JVM parameters as producer.
```bash
    mvn clean package
    java -Dspring.profiles.active=producer -jar target/*.jar
```
or you can use maven's spring-boot phase to pass the profiles' parameter through *spring-boot.run.profiles* as it is
used in the following command:
```bash
    mvn spring-boot:run -Dspring-boot.run.profiles=producer
```

Moreover, it is worth mentioning that Producer is still as statefull implementation in spring cloud and also 
for this repo. In fact, as I am writing this, there is a pending request for functional producer in spring cloud repo. 
Therefore, we need the Spring Web library to implement a controller and a service to trigger the available Kafka 
Producer.

### 2.3. BiFunction to Kafka

In case you want to deploy only bifunction spring boot profile, then we need the following two lines. The same trick is 
to define the *spring.profiles.active* in the JVM parameters as bifunction.
```bash
    mvn clean package
    java -Dspring.profiles.active=bifunction -jar target/*.jar
```
or you can use maven's spring-boot phase to pass the profiles' parameter through *spring-boot.run.profiles* as it is
used in the following command:
```bash
    mvn spring-boot:run -Dspring-boot.run.profiles=bifunction
```
In bifunction profile, we load a bean with a BiFunction in order to left join the incoming KSteam of  
SMS's actions and the static KTable of SmsRules's actions with the matched keys, which are the receiver's number. 
Right after, we check if there is a rule and if this rule permits the production of the action to the given 
*active.topic* destination or not. If a new request updates a sms's rule, then any forthcoming SMS will be filtered by 
the updated rule. 

### 2.4. Consumer from Kafka

Likewise, to deploy only consumer spring boot profile, then we need the following two lines. The same trick for the JVM
parameters for consumer.
```bash
    mvn clean package
    java -Dspring.profiles.active=consumer -jar target/*.jar
```
or likewise, use maven's spring-boot phase for consumer as we did for producer:
```bash
    mvn spring-boot:run -Dspring-boot.run.profiles=consumer
```


## 3. Config Kafka Docker Images on docker-compose file

There are three images that most GitHub's repositories are using for Kafka brokers in docker-compose file, as it is
shown in the following yml examples:
- [Wurstmeister](https://github.com/wurstmeister/kafka-docker) images from
  [wurstmeister/kafka](https://hub.docker.com/r/wurstmeister/kafka) docker hub,
- [Bitnami](https://github.com/bitnami/bitnami-docker-kafka) images from
  [bitnami/kafka](https://hub.docker.com/r/bitnami/kafka) docker hub.
- [Confluentinc](https://github.com/confluentinc/cp-docker-images) images from
  [confluentinc/cp-kafka](https://hub.docker.com/r/confluentinc/cp-kafka/) docker hub.

For this pilot, [Wurstmeister](https://github.com/wurstmeister/kafka-docker) images are currently utilized.

### 3.1. Wurstmeister Kafka

```yml
#sample from docker-compose.yml file
version: '3'
services:
  zookeeper:
    image: wurstmeister/zookeeper
    container_name: zookeeper
    ports:
      - '2181:2181'
  kafka:
    image: wurstmeister/kafka
    container_name: Kafka
    ports:
      - '9092:9092'
    environment:
      KAFKA_ADVERTISED_HOST_NAME: localhost
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
```
### 3.2. Bitnami Kafka

[comment]: <> (todo-geand: It has not be tested in dev or staging env yet)
```yml
#sample from docker-compose.yml file
version: "2"

services:
  zookeeper:
    image: docker.io/bitnami/zookeeper:3.7
    ports:
      - "2181:2181"
    volumes:
      - "zookeeper_data:/bitnami"
    environment:
      - ALLOW_ANONYMOUS_LOGIN=yes
  kafka:
    image: docker.io/bitnami/kafka:3
    ports:
      - "9092:9092"
    volumes:
      - "kafka_data:/bitnami"
    environment:
      - KAFKA_CFG_ZOOKEEPER_CONNECT=zookeeper:2181
      - ALLOW_PLAINTEXT_LISTENER=yes
    depends_on:
      - zookeeper

volumes:
  zookeeper_data:
    driver: local
  kafka_data:
    driver: local
```

### 3.3. Confluentinc  Kafka

Confluent and Kafka latest version are available at [confluentinc/cp-all-in-one](https://github.com/confluentinc/cp-all-in-one/tree/latest/cp-all-in-one) repo.

[comment]: <> (todo-geand: It has not be tested in dev or staging env yet)

```yml
#sample from docker-compose.yml file
version: '2'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:7.0.1
    hostname: zookeeper
    container_name: zookeeper
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000

  broker:
    image: confluentinc/cp-server:7.0.1
    hostname: broker
    container_name: broker
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
      - "9101:9101"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: 'zookeeper:2181'
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://broker:29092,PLAINTEXT_HOST://localhost:9092
      KAFKA_METRIC_REPORTERS: io.confluent.metrics.reporter.ConfluentMetricsReporter
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0
      KAFKA_CONFLUENT_LICENSE_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_CONFLUENT_BALANCER_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_TRANSACTION_STATE_LOG_MIN_ISR: 1
      KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 1
      KAFKA_JMX_PORT: 9101
      KAFKA_JMX_HOSTNAME: localhost
      KAFKA_CONFLUENT_SCHEMA_REGISTRY_URL: http://schema-registry:8081
      CONFLUENT_METRICS_REPORTER_BOOTSTRAP_SERVERS: broker:29092
      CONFLUENT_METRICS_REPORTER_TOPIC_REPLICAS: 1
      CONFLUENT_METRICS_ENABLE: 'true'
      CONFLUENT_SUPPORT_CUSTOMER_ID: 'anonymous'
#...
```

## 4. Models
The models that will transfer the data are the [Action](src/main/java/com/pilot/commons/Action.java),
which will curry the [Sms](src/main/java/com/pilot/commons/Sms.java) enriched with
[Status](src/main/java/com/pilot/commons/Status.java). The simplified POJO versions of these Models are following.

### 4.1. Action Model

```java
    public class Action {
        private String id;
        private Sms sms;
        private LocalDateTime created;
        private LocalDateTime updated;
        private String status;
    }
```

### 4.2. Sms Model

```java 
    public class Sms {
        private String body;
        private String sender;
        private String receiver;
        private LocalDateTime timestamp;
    }
```
### 4.3. Status Emum

```java 
    public enum Status {
        CREATED,
        PENDING,
        INVALID,
        VALID,
        DELIVERED
    }
```
### 4.4. SmsRule Model

```java 
    public class SmsRule {
        private String verb;
        private Boolean allSenders;
        private String receiver;
        private LocalDateTime timestamp;
    }
```
### 4.5. Verb Emum

```java 
    public enum Verb {
        ALLOW,
        BLOCK
    }
```

### 4.6. Web Request and Response bodies
The web [SmsRequest](src/main/java/com/pilot/springbootkafkastreamsproducerbifunctionconsumer/web/SmsRequest.java), which is triggered
when a user sends and web [SmsResponse](src/main/java/com/pilot/springbootkafkastreamsproducerbifunctionconsumer/web/SmsResponse.java)
that will be received 

[//]: #Todo-geand: swagger-ui is not ready to support the app yet
[//]: # (are available from [swagger-ui]&#40;http://localhost:9000/swagger-ui.html&#41; )
as long the server (in producer's profile) is deployed.
For the case SMS rules, the web
[SmsRuleRequest](src/main/java/com/pilot/springbootkafkastreamsproducerbifunctionconsumer/web/SmsRuleRequest.java) is triggered when a
user sends a rule and web
[SmsRuleResponse](src/main/java/com/pilot/springbootkafkastreamsproducerbifunctionconsumer/web/SmsResponse.java) that will be received
are also available.

## 5. Read More
You might be interested to check the [Common HowTo's](docs/how-to.md).