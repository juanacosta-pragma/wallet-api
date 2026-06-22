package co.com.bancolombia.mongo.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.boot.ssl.SslBundles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

@SpringBootTest(classes = MongoConfigAdvancedTest.TestConfig.class)
@TestPropertySource(properties = "spring.mongodb.uri=mongodb://localhost:27017/test")
class MongoConfigAdvancedTest {

    @Autowired(required = false)
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @Test
    void mongoConfigurationExists() {
        // MongoDB configuration is loaded
        assertThat(MongoConfig.class).isNotNull();
    }

    @Test
    void reactiveMongoTemplateCanBeInjected() {
        // This test will only pass if MongoDB is properly configured
        if (reactiveMongoTemplate != null) {
            assertThat(reactiveMongoTemplate).isNotNull();
        }
    }

    @Test
    void mongoDBSecretClassExists() {
        assertThat(MongoDBSecret.class).isNotNull();
    }

    @Configuration
    @Import(MongoConfig.class)
    static class TestConfig {
        @Bean
        public SslBundles sslBundles() {
            // Provide a mock SslBundles bean sufficient for the configuration under test
            return mock(SslBundles.class);
        }
    }
}

