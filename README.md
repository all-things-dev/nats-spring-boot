# Spring Boot for NATS

Spring Boot for NATS enables declarative abstractions for implementing NATS in Spring Boot applications.

**TIP**: This project is inspired by [Spring Kafka](https://github.com/spring-projects/spring-kafka) and
[Spring Pulsar](https://github.com/spring-projects/spring-pulsar) and thus tries to closely match their implementation style.

## Getting Started

1. Add `nats-spring-core` dependency to your `pom.xml` file:

    ```xml
    <dependency>
	    <groupId>dev.all-things.boot</groupId>
	    <artifactId>nats-spring-core</artifactId>
	    <version>${nats-spring-boot.version}</version>
    </dependency>
    ```

2. Add `@EnableNats` on your Spring Boot application / configuration class.

    ```java
    @EnableNats
    @Configuration(proxyBeanMethods = false)
    public class NatsConfiguration
    {
        @Bean
        @ConditionalOnMissingBean
        public NatsConnectionConfiguration natsConnectionConfiguration()
        {
            NatsConnectionConfiguration configuration = new NatsConnectionConfiguration();
    
            configuration.setServers(List.of("nats://0.0.0.0:58811"));
            configuration.setUsername("local");
            configuration.setPassword("dH...yu");
    
            return configuration;
        }
    }
   ```

3. Create a listener class and annotate listener method with `@NatsListener` and the subject.

## Usage Examples

1. Simple listener

    ```java
    @Component
    public class MessageListener
    {
        @NatsListener(subject = "zion.neo")
        public void onMessage(String message)
        {
            logger.info("Received message: {}", message); // Hello, Neo
        }
    }
    ```
2. Listener with reply

    ```java
    @Component
    public class MessageListener
    {
        @NatsListener(subject = "zion.neo", replySubject = "zion.trinity")
        public String onMessage(String message)
        {
            logger.info("Received message: {}", message); // Hello, Neo
   
            return "Hello, Trinity"; // Sent to the subject 'zion.trinity'
        }
    }
    ```
   **TIP**: Listener methods support `io.nats.client.Message`, `java.lang.String` and `byte[]` as reply types.
   However, `replySubject` needs to be explicitly set for the reply type `io.nats.client.Message` and the value
   set via annotation will be ignored.

3. Listener with an unspecified reply type

    ```java
    @NatsListener(subject = "matrix.smith", replySubject = "matrix")
    public Object onMessageWithGenericReply(final String message)
    {
    	logger.info("Received message: {}", message);
    
    	return switch (new Random(System.nanoTime()).nextInt(3))
    	{
    		case 0 -> "Hello - From The Matrix";
    		case 1 -> UUID.randomUUID().toString();
    		default -> "Hello - From The Matrix".getBytes(StandardCharsets.UTF_8);
    	};
    }
    ```
   **TIP**: A special handler will be created for listener methods with an unspecified return type which may be slightly inefficient.
   Also, error related to unsupported return types will be thrown at runtime.
   Use this feature only when necessary.

## Attribute Injection

Certain attributes of the incoming message can be implicitly extracted and injected as parameters to the listener method.

1. `@Header` allows single message header to be injected to the annotated parameter.
   `@Headers` injects all message headers as `Map<String, String>`.

    ```java
    @Component
    public class MessageListener
    {
        @NatsListener(subject = "zion.trinity")
        public void onMessage(Message message, String messageContent, @Header("id") String messageId,
                              @Headers Map<String, String> headers)
        {
            logger.info("Received message '{}', content '{}', id '{}', headers '{}'",
                    message, messageContent, messageId, headers);
        }
    }
    ```
   **TIP**: Headers can also be injected in multi-value format i.e. `Map<String, List<String>`.
   `Map<String, String>` serves simple use-cases where headers are expected to have single value.

2. Unannotated parameters of types `io.nats.client.Message`, `java.lang.String` and/or `byte[]`
   are automatically treated as message content and injected accordingly.

## Differences from Spring Kafka

1. For better performance, `MethodHandle.invoke` is used (instead of `Method.invoke`) for invoking the listener methods.

## Software Requirements

1. JDK 17
2. Spring Boot 3.2

## License

This project is Open Source software released under the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0.html).
