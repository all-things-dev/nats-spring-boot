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
            final NatsConnectionConfiguration configuration = new NatsConnectionConfiguration();
    
            configuration.setServers(List.of("nats://0.0.0.0:58811"));
            configuration.setUsername("local");
            configuration.setPassword("dH...yu");
    
            return configuration;
        }
    }
   ```

3. Create a listener class and annotate corresponding method with `@NatsListener` annotation.

    ```java
    @Component
    public class MessageListener
    {
        @NatsListener(subject = "zion.neo")
        public void onMessage(final String message)
        {
            logger.info("Received message: {}", message); // Hello, Neo
        }
    }
    ```

## Differences from Spring Kafka

1. For better performance, `MethodHandle.invoke` is used (instead of `Method.invoke`) for invoking the listener methods.

## Software Requirements

1. JDK 17
2. Spring Boot 3.2 

## License

This project is Open Source software released under the [Apache 2.0 license](https://www.apache.org/licenses/LICENSE-2.0.html).
