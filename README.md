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

| Program | Package | Version | Ruled by |
| :---- | :------- | -------: | --------: |
| docker | Docker CE | 20.10.8 | env. |
| docker-compose | Docker CE | 1.29.2 | env. |
| Kafka | Kafka Broker | - | [docker-compose.yml](docker-compose.yml) |
| mvn | Maven | - | [Dockerfile](Dockerfile) |
| java | Java | - | [Dockerfile](Dockerfile) |
| lib | Apache-Kafka-Streams | 2.7.2 | spring-boot |
| lib | Jackson-Datatype-JSR310 | 2.5.7 | spring-boot |
| lib | Lombok | 1.18.22 | spring-boot |
| lib | Openapi-UI | 1.6.3 | [pom.xml](pom.xml) |
| lib | Spring-Cloud-Stream | 3.1.4 | spring-boot |
| lib | Spring-Cloud-Stream-Binder-Kafka-Streams | 3.1.4 | spring-boot |
| lib | Spring-Boot-Starter-Web | 2.5.7 | spring-boot |
| spring-boot | Spring-Boot-Starter-Parent | 2.5.7 | [pom.xml](pom.xml) |
| spring-cloud | Spring-Cloud | 2020.0.5 | [pom.xml](pom.xml) |

#### 1.1.3. Development Environment
Follow the steps on section about [Working on the Development Environment](#wotde).

##### 1.1.3.1 Versioning Details

| Program | Package | Version | Ruled by |
| :---- | :------- | -------: | --------: |
| docker | Docker CE | 20.10.8 | env. |
| docker-compose | Docker CE | 1.29.2 | env. |
| Kafka | Kafka Broker | - | [docker-compose.yml](docker-compose.yml) |
| mvn | Maven | 3.8.1 | env. |
| java | Java | 1.8.0_292 | env. |
| lib | Apache-Kafka-Streams | 2.7.2 | spring-boot |
| lib | Jackson-Datatype-JSR310 | 2.5.7 | spring-boot |
| lib | Lombok | 1.18.22 | spring-boot |
| lib | Openapi-UI | 1.5.2 | [pom.xml](pom.xml) |
| lib | Spring-Cloud-Stream | 3.1.4 | spring-boot |
| lib | Spring-Cloud-Stream-Binder-Kafka-Streams | 3.1.4 | spring-boot |
| lib | Spring-Boot-Starter-Web | 2.5.7 | spring-boot |
| spring-boot | Spring-Boot-Starter-Parent | 2.5.7 | [pom.xml](pom.xml) |
| spring-cloud | Spring-Cloud | 2020.0.4 | [pom.xml](pom.xml) |


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
  producer      | 2022-01-10 15:10:07.166  INFO 1 --- [nio-9000-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
  producer      | 2022-01-10 15:10:07.172  INFO 1 --- [nio-9000-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 6 ms
  producer      | 2022-01-10 15:10:07.583  INFO 1 --- [nio-9000-exec-1] c.p.s.producer.Producer                  : #~#: Producing (key: 0123456789), action -> Action(id=4e3bd520, sms=null, created=null, updated=null, status=null, smsRule=SmsRule(verb=BLOCK, allSenders=true, receiver=0123456789, timestamp=2022-01-10T15:10:07.562))
  producer      | 2022-01-10 15:10:07.674  INFO 1 --- [nio-9000-exec-1] o.a.k.clients.producer.ProducerConfig    : ProducerConfig values:
  ...
  producer      | 2022-01-10 15:10:08.030  INFO 1 --- [nio-9000-exec-1] o.a.kafka.common.utils.AppInfoParser     : Kafka version: 2.7.2
  producer      | 2022-01-10 15:10:08.032  INFO 1 --- [nio-9000-exec-1] o.a.kafka.common.utils.AppInfoParser     : Kafka commitId: 37a1cc36bf4d76f3
  producer      | 2022-01-10 15:10:08.033  INFO 1 --- [nio-9000-exec-1] o.a.kafka.common.utils.AppInfoParser     : Kafka startTimeMs: 1641827408026
  producer      | 2022-01-10 15:10:08.739  INFO 1 --- [ad | producer-1] org.apache.kafka.clients.Metadata        : [Producer clientId=producer-1] Cluster ID: I40ZnFp2T--DPmxjGf0BeA
  producer      | 2022-01-10 15:10:16.823  INFO 1 --- [nio-9000-exec-3] c.p.s.producer.Producer                  : #~#: Producing (key: 9876543210), action -> Action(id=680fea91, sms=Sms(body=string, sender=0000000000, receiver=9876543210, timestamp=2022-01-10T15:10:16.821), created=2022-01-10T15:10:16.821, updated=2022-01-10T15:10:16.821, status=CREATED, smsRule=null)
  producer      | 2022-01-10 15:11:15.205  INFO 1 --- [nio-9000-exec-5] c.p.s.producer.Producer                  : #~#: Producing (key: 0123456789), action -> Action(id=032734a0, sms=Sms(body=string, sender=0000000000, receiver=0123456789, timestamp=2022-01-10T15:11:15.205), created=2022-01-10T15:11:15.205, updated=2022-01-10T15:11:15.205, status=CREATED, smsRule=null)
```

5. Check the logs from bifunction through docker-compose logs

```bash
  $ docker-compose logs bifunction
  ...
  bifunction    | 2022-01-10 14:51:37.494  INFO 1 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [bifunctionProcessor-applicationId-e496a8a8-4f6c-49bc-8fbd-b02e2c773526-StreamThread-1] Restoration took 446 ms for all tasks [0_0]
  bifunction    | 2022-01-10 14:51:37.494  INFO 1 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [bifunctionProcessor-applicationId-e496a8a8-4f6c-49bc-8fbd-b02e2c773526-StreamThread-1] State transition from PARTITIONS_ASSIGNED to RUNNING
  bifunction    | 2022-01-10 14:51:37.499  INFO 1 --- [-StreamThread-1] org.apache.kafka.streams.KafkaStreams    : stream-client [bifunctionProcessor-applicationId-e496a8a8-4f6c-49bc-8fbd-b02e2c773526] State transition from REBALANCING to RUNNING
  bifunction    | 2022-01-10 14:51:37.503  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.SubscriptionState    : [Consumer clientId=bifunctionProcessor-applicationId-e496a8a8-4f6c-49bc-8fbd-b02e2c773526-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Resetting offset for partition topic-0 to position FetchPosition{offset=0, offsetEpoch=Optional.empty, currentLeader=LeaderAndEpoch{leader=Optional[kafka:9092 (id: 1001 rack: null)], epoch=0}}.
  bifunction    | 2022-01-10 14:51:37.504  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.SubscriptionState    : [Consumer clientId=bifunctionProcessor-applicationId-e496a8a8-4f6c-49bc-8fbd-b02e2c773526-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Resetting offset for partition rules.topic-0 to position FetchPosition{offset=0, offsetEpoch=Optional.empty, currentLeader=LeaderAndEpoch{leader=Optional[kafka:9092 (id: 1001 rack: null)], epoch=0}}.
  bifunction    | 2022-01-10 14:53:36.882  INFO 1 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [bifunctionProcessor-applicationId-e496a8a8-4f6c-49bc-8fbd-b02e2c773526-StreamThread-1] Processed 0 total records, ran 0 punctuators, and committed 0 total tasks since the last update
  ...
  bifunction    | 2022-01-10 15:09:20.591  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=bifunctionProcessor-applicationId-e496a8a8-4f6c-49bc-8fbd-b02e2c773526-StreamThread-1-consumer, groupId=bifunctionProcessor-applicationId] Discovered group coordinator kafka:9092 (id: 2147482646 rack: null)
  bifunction    | 2022-01-10 15:10:16.865  INFO 1 --- [-StreamThread-1] c.p.s.KafkaBiFunctionConfiguration       : key: 9876543210, action: Action(id=680fea91, sms=Sms(body=string, sender=0000000000, receiver=9876543210, timestamp=2022-01-10T15:10:16), created=2022-01-10T15:10:16, updated=2022-01-10T15:10:16.865, status=PENDING, smsRule=null)
  bifunction    | 2022-01-10 15:11:15.223  INFO 1 --- [-StreamThread-1] c.p.s.KafkaBiFunctionConfiguration       : key: 0123456789, action: Action(id=032734a0, sms=Sms(body=string, sender=0000000000, receiver=0123456789, timestamp=2022-01-10T15:11:15), created=2022-01-10T15:11:15, updated=2022-01-10T15:11:15.223, status=PENDING, smsRule=SmsRule(verb=BLOCK, allSenders=true, receiver=0123456789, timestamp=2022-01-10T15:10:07))
  bifunction    | 2022-01-10 15:11:19.997  INFO 1 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [bifunctionProcessor-applicationId-e496a8a8-4f6c-49bc-8fbd-b02e2c773526-StreamThread-1] Processed 3 total records, ran 0 punctuators, and committed 1 total tasks since the last update
```

6. Check the logs from consumer to see the consumed message through docker-compose logs

```bash
  $ docker-compose logs consumer
  ...
  consumer      | 2022-01-10 14:51:36.448  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumeService-applicationId-a364a111-94a2-4a1d-9db8-83489da32197-StreamThread-1-consumer, groupId=consumeService-applicationId] Found no committed offset for partition active.topic-0
  consumer      | 2022-01-10 14:51:36.491  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.SubscriptionState    : [Consumer clientId=consumeService-applicationId-a364a111-94a2-4a1d-9db8-83489da32197-StreamThread-1-consumer, groupId=consumeService-applicationId] Resetting offset for partition active.topic-0 to position FetchPosition{offset=0, offsetEpoch=Optional.empty, currentLeader=LeaderAndEpoch{leader=Optional[kafka:9092 (id: 1001 rack: null)], epoch=0}}.
  consumer      | 2022-01-10 14:52:04.113  INFO 1 --- [   scheduling-1] o.a.k.clients.admin.AdminClientConfig    : AdminClientConfig values:
  ...
  consumer      | 2022-01-10 14:52:04.119  INFO 1 --- [   scheduling-1] o.a.kafka.common.utils.AppInfoParser     : Kafka version: 2.7.2
  consumer      | 2022-01-10 14:52:04.119  INFO 1 --- [   scheduling-1] o.a.kafka.common.utils.AppInfoParser     : Kafka commitId: 37a1cc36bf4d76f3
  consumer      | 2022-01-10 14:52:04.119  INFO 1 --- [   scheduling-1] o.a.kafka.common.utils.AppInfoParser     : Kafka startTimeMs: 1641826324119
  consumer      | 2022-01-10 14:52:04.149  INFO 1 --- [| adminclient-2] o.a.kafka.common.utils.AppInfoParser     : App info kafka.admin.client for adminclient-2 unregistered
  consumer      | 2022-01-10 14:52:04.153  INFO 1 --- [| adminclient-2] org.apache.kafka.common.metrics.Metrics  : Metrics scheduler closed
  consumer      | 2022-01-10 14:52:04.153  INFO 1 --- [| adminclient-2] org.apache.kafka.common.metrics.Metrics  : Closing reporter org.apache.kafka.common.metrics.JmxReporter
  consumer      | 2022-01-10 14:52:04.153  INFO 1 --- [| adminclient-2] org.apache.kafka.common.metrics.Metrics  : Metrics reporters closed
  consumer      | 2022-01-10 14:53:34.564  INFO 1 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-a364a111-94a2-4a1d-9db8-83489da32197-StreamThread-1] Processed 0 total records, ran 0 punctuators, and committed 0 total tasks since the last update
  ...
  consumer      | 2022-01-10 15:09:20.583  INFO 1 --- [-StreamThread-1] o.a.k.c.c.internals.AbstractCoordinator  : [Consumer clientId=consumeService-applicationId-a364a111-94a2-4a1d-9db8-83489da32197-StreamThread-1-consumer, groupId=consumeService-applicationId] Discovered group coordinator kafka:9092 (id: 2147482646 rack: null)
  consumer      | 2022-01-10 15:10:17.096  INFO 1 --- [-StreamThread-1] c.p.s.KafkaConsumerConfiguration         : Action consumed [Action(id=680fea91, sms=Sms(body=string, sender=0000000000, receiver=9876543210, timestamp=2022-01-10T15:10:16), created=2022-01-10T15:10:16, updated=2022-01-10T15:10:16, status=DELIVERING, smsRule=null)] action.sms.body: [string]
  consumer      | 2022-01-10 15:11:20.002  INFO 1 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-a364a111-94a2-4a1d-9db8-83489da32197-StreamThread-1] Processed 1 total records, ran 0 punctuators, and committed 1 total tasks since the last update
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
    2022-01-10 17:28:23.612  INFO 72784 --- [nio-9000-exec-1] c.p.s.producer.Producer                  : #~#: Producing (key: 0123456789), action -> Action(id=8a9a8973, sms=null, created=null, updated=null, status=null, smsRule=SmsRule(verb=BLOCK, allSenders=true, receiver=0123456789, timestamp=2022-01-10T17:28:23.612))
    2022-01-10 17:28:23.614  INFO 72784 --- [nio-9000-exec-1] o.a.k.clients.producer.ProducerConfig    : ProducerConfig values:
    ...
    2022-01-10 17:28:23.619  INFO 72784 --- [nio-9000-exec-1] o.a.kafka.common.utils.AppInfoParser     : Kafka version: 2.7.2
    2022-01-10 17:28:23.619  INFO 72784 --- [nio-9000-exec-1] o.a.kafka.common.utils.AppInfoParser     : Kafka commitId: 37a1cc36bf4d76f3
    2022-01-10 17:28:23.619  INFO 72784 --- [nio-9000-exec-1] o.a.kafka.common.utils.AppInfoParser     : Kafka startTimeMs: 1641828503619
    2022-01-10 17:28:23.636  INFO 72784 --- [ad | producer-1] org.apache.kafka.clients.Metadata        : [Producer clientId=producer-1] Cluster ID: VIbOH-GwSYKXhNAz1GvElQ
    2022-01-10 17:28:33.512  INFO 72784 --- [nio-9000-exec-2] c.p.s.producer.Producer                  : #~#: Producing (key: 9876543210), action -> Action(id=186bb440, sms=Sms(body=string, sender=0000000000, receiver=9876543210, timestamp=2022-01-10T17:28:33.512), created=2022-01-10T17:28:33.512, updated=2022-01-10T17:28:33.512, status=CREATED, smsRule=null)
    2022-01-10 17:28:33.529  INFO 72784 --- [-StreamThread-1] c.p.s.KafkaBiFunctionConfiguration       : key: 9876543210, action: Action(id=186bb440, sms=Sms(body=string, sender=0000000000, receiver=9876543210, timestamp=2022-01-10T17:28:33), created=2022-01-10T17:28:33, updated=2022-01-10T17:28:33.529, status=PENDING, smsRule=null)
    2022-01-10 17:28:33.535  INFO 72784 --- [nio-9000-exec-4] c.p.s.producer.Producer                  : #~#: Producing (key: 0123456789), action -> Action(id=10b236b5, sms=Sms(body=string, sender=0000000000, receiver=0123456789, timestamp=2022-01-10T17:28:33.535), created=2022-01-10T17:28:33.535, updated=2022-01-10T17:28:33.535, status=CREATED, smsRule=null)
    2022-01-10 17:28:33.544  INFO 72784 --- [-StreamThread-1] c.p.s.KafkaBiFunctionConfiguration       : key: 0123456789, action: Action(id=10b236b5, sms=Sms(body=string, sender=0000000000, receiver=0123456789, timestamp=2022-01-10T17:28:33), created=2022-01-10T17:28:33, updated=2022-01-10T17:28:33.544, status=PENDING, smsRule=SmsRule(verb=BLOCK, allSenders=true, receiver=0123456789, timestamp=2022-01-10T17:28:23))
    2022-01-10 17:29:00.439  INFO 72784 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [bifunctionProcessor-applicationId-434e3933-b4b6-4a64-a52c-6dce3540ab7f-StreamThread-1] Processed 3 total records, ran 0 punctuators, and committed 1 total tasks since the last update
```

And on the consumer terminal:

```bash
  ...
  2022-01-10 17:27:09.674  INFO 72828 --- [-StreamThread-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumeService-applicationId-be80ec08-ee16-4c45-b2bf-11d246e0ca05-StreamThread-1-consumer, groupId=consumeService-applicationId] Adding newly assigned partitions: active.topic-0
  2022-01-10 17:27:09.674  INFO 72828 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-be80ec08-ee16-4c45-b2bf-11d246e0ca05-StreamThread-1] State transition from STARTING to PARTITIONS_ASSIGNED
  2022-01-10 17:27:09.683  INFO 72828 --- [-StreamThread-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumeService-applicationId-be80ec08-ee16-4c45-b2bf-11d246e0ca05-StreamThread-1-consumer, groupId=consumeService-applicationId] Found no committed offset for partition active.topic-0
  2022-01-10 17:27:09.700  INFO 72828 --- [-StreamThread-1] o.a.k.c.c.internals.SubscriptionState    : [Consumer clientId=consumeService-applicationId-be80ec08-ee16-4c45-b2bf-11d246e0ca05-StreamThread-1-consumer, groupId=consumeService-applicationId] Resetting offset for partition active.topic-0 to position FetchPosition{offset=0, offsetEpoch=Optional.empty, currentLeader=LeaderAndEpoch{leader=Optional[localhost:9092 (id: 1001 rack: null)], epoch=0}}.
  2022-01-10 17:27:09.752  INFO 72828 --- [-StreamThread-1] o.a.k.s.processor.internals.StreamTask   : stream-thread [consumeService-applicationId-be80ec08-ee16-4c45-b2bf-11d246e0ca05-StreamThread-1] task [0_0] Initialized
  2022-01-10 17:27:09.757  INFO 72828 --- [-StreamThread-1] o.a.k.c.c.internals.ConsumerCoordinator  : [Consumer clientId=consumeService-applicationId-be80ec08-ee16-4c45-b2bf-11d246e0ca05-StreamThread-1-consumer, groupId=consumeService-applicationId] Found no committed offset for partition active.topic-0
  2022-01-10 17:27:09.760  INFO 72828 --- [-StreamThread-1] o.a.k.s.processor.internals.StreamTask   : stream-thread [consumeService-applicationId-be80ec08-ee16-4c45-b2bf-11d246e0ca05-StreamThread-1] task [0_0] Restored and ready to run
  2022-01-10 17:27:09.760  INFO 72828 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-be80ec08-ee16-4c45-b2bf-11d246e0ca05-StreamThread-1] Restoration took 86 ms for all tasks [0_0]
  2022-01-10 17:27:09.760  INFO 72828 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-be80ec08-ee16-4c45-b2bf-11d246e0ca05-StreamThread-1] State transition from PARTITIONS_ASSIGNED to RUNNING
  2022-01-10 17:27:09.761  INFO 72828 --- [-StreamThread-1] org.apache.kafka.streams.KafkaStreams    : stream-client [consumeService-applicationId-be80ec08-ee16-4c45-b2bf-11d246e0ca05] State transition from REBALANCING to RUNNING
  2022-01-10 17:28:33.799  INFO 72828 --- [-StreamThread-1] c.p.s.KafkaConsumerConfiguration         : Action consumed [Action(id=186bb440, sms=Sms(body=string, sender=0000000000, receiver=9876543210, timestamp=2022-01-10T17:28:33), created=2022-01-10T17:28:33, updated=2022-01-10T17:28:33, status=DELIVERING, smsRule=null)] action.sms.body: [string]
  2022-01-10 17:29:09.737  INFO 72828 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-be80ec08-ee16-4c45-b2bf-11d246e0ca05-StreamThread-1] Processed 1 total records, ran 0 punctuators, and committed 1 total tasks since the last update
  2022-01-10 17:31:09.787  INFO 72828 --- [-StreamThread-1] o.a.k.s.p.internals.StreamThread         : stream-thread [consumeService-applicationId-be80ec08-ee16-4c45-b2bf-11d246e0ca05-StreamThread-1] Processed 0 total records, ran 0 punctuators, and committed 0 total tasks since the last update
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
that will be received are available from [swagger-ui](http://localhost:9000/swagger-ui.html) as long the server
(in producer's profile) is deployed.
For the case SMS rules, the web
[SmsRuleRequest](src/main/java/com/pilot/springbootkafkastreamsproducerbifunctionconsumer/web/SmsRuleRequest.java) is triggered when a
user sends a rule and web
[SmsRuleResponse](src/main/java/com/pilot/springbootkafkastreamsproducerbifunctionconsumer/web/SmsResponse.java) that will be received
are also available.

## 5. OpenAPI Definition

In onder to check this app's API calls, which are controlled by *producer* profile, you can check
[swagger-ui](http://localhost:9000/swagger-ui.html).

The [api-docs](http://localhost:9000/api-docs) are also available.

## 6. Read More
You might be interested to check the [Common HowTo's](docs/how-to.md).