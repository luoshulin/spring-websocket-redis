# Spring-websocket-redis

This is a demo project for testing spring-websocket and spring-data-redis functionalities.
##The task
1. Setup a running env. aligned with the technologies mentioned below.
2. Develop the application in Java language using Play framework / Spring boot / JHipster or a similar framework
3. A REST endpoint is taking a dummy json input, and the server puts the REST payload on a Redis pub/sub channels (or other messaging stack)
4. A Consumer is running the application and processes the freshly received message and persists the message in a NoSQL database (Redis or other)
5. A REST endpoint is implemented for getting all the messages persisted in json format
The message should also be pushed through Websockets for listening browser clients at the time the message was received on the REST endpoint
6. A simple html page is implemented to show the real time message delivery

###Requirements
- All tasks are solved in the solution
- The application has a solid commit history
- The application is build for scale
- The application is build for test

##Solution
The application basically a simple Spring-boot application which implements message broadcast functionalites. Messages can be posted to a REST resource *(/api/message)*, where after a successful post an event is published through a Redis channel *(newMessages)* about the new message. The application also subscribes this channel *(NewMessageEventListener.java, NewMessageBroadcaster.java)* and when a new event is triggered it persists the message received from the channel.

>NOTE: The task defines the websocket publishing step as a part of the REST layer but with this architecture the scaling of the Websocket connections can be achieved only with a use of a real message broker implementations like ActiveMQ. Now for the sake of simplicity only an embedded (in-memory) message broker is used inside the application and this prevents from correct scaling. This problem has been resolved with moving the server push step into a Redis message listener implementation because that is shared among the nodes.

###Used technologies:
  - JDK8
  - Spring-boot
  - Spring-websocket with STOMP
  - Redis for persistance
  - Redis for pub/sub messaging
  - YAML for Spring application configuration
  
###Description
####Layers
Although the application is built as one JAR the Web (Frontend) and Domain (backend) layers are separated through package structure for better decoupling.

#####Web
Web layer contains the REST controller *(MessageController.java)* and the static HTML resources *(index.html, etc...)* along with the Redis event publisher implementations *(RedisMessageEventPublisherImpl.java)*.

For supporting scaling this layer contains a Redis message listener also which pushes the message received on *newMessages* Redis channel towards the connected Websocket clients.
######REST resources
- POST /api/message - Save a message and broadcast it to connected websocket clients
```
example payload: {"content":"Hello World!", "type":"INFO"}
```
- DELETE /api/message/{id} - Delete a message with the given ID
- GET /api/message - Returns all the existing messages in ordered. First is the oldest. (in real life paging is a TODO)
 
#####Domain
Domain layer contains the domain model of the Messages *(Message.java)*, a service which handles responsible for message connected tasks (save, delete, findAll...) and a Redis message listener *(NewMessageEventListener.java)* for persisting incoming messages.
####Starting the application
You can start the application as any other Spring boot applications
With Maven:
```
$ mvn spring-boot:run
```
With Java:
```
$ java -jar spring-redis-websocket-0.0.1-SNAPSHOT.jar
```
####Configuration
The application is configured to search Redis instance on *localhost:6379*.
This can be overriden with the *spring.redis.host* variable. You can override this property through several ways. For example with a java runtime argument like this 
```
-Dspring.redis.host=192.168.59.103
```
#####Testing without Redis
For supporting easier development or testing without Redis an additional Spring profile is introduced: *embedded*.

With the activation of this profile the test implementations *(TestMessageServiceImpl.java)* of the *MessageService* and *MessageEventPublisher* interfaces are instantiated instead of the Redis backed ones *(RedisMessageServiceImpl.java, RedisMessageEventPublisherImpl.java)*.

The profle can be activated as usally in Spring application:
```
-Dspring.profiles.active=embedded
```


