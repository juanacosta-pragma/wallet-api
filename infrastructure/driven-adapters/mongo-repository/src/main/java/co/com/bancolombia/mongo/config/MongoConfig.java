package co.com.bancolombia.mongo.config;

import co.com.bancolombia.mongo.pocket.MongoDBPocketRepository;
import co.com.bancolombia.mongo.wallet.MongoDBWalletRepository;
import co.com.bancolombia.mongo.wallet.MongoRepositoryWalletAdapter;
import co.com.bancolombia.mongo.pocket.MongoRepositoryPocketAdapter;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.mongodb.autoconfigure.MongoConnectionDetails;
import org.springframework.boot.mongodb.autoconfigure.MongoProperties;
import org.springframework.boot.mongodb.autoconfigure.PropertiesMongoConnectionDetails;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    @Bean
    public MongoDBSecret dbSecret(@Value("${spring.data.mongodb.uri}") String uri) {
        return MongoDBSecret.builder()
                .uri(uri)
                .build();
    }

    @Bean
    public MongoConnectionDetails mongoProperties(MongoDBSecret secret, SslBundles sslBundles) {
        MongoProperties properties = new MongoProperties();
        properties.setUri(secret.getUri());
        return new PropertiesMongoConnectionDetails(properties, sslBundles);
    }

    @Bean
    @ConditionalOnProperty(name = "spring.data.mongodb.enabled", havingValue = "true")
    public MongoRepositoryWalletAdapter mongoRepositoryWalletAdapter(MongoDBWalletRepository repository, ObjectMapper mapper) {
        return new MongoRepositoryWalletAdapter(repository, mapper);
    }

    @Bean
    @ConditionalOnProperty(name = "spring.data.mongodb.enabled", havingValue = "true")
    public MongoRepositoryPocketAdapter mongoRepositoryPocketAdapter(MongoDBPocketRepository repository, ObjectMapper mapper) {
        return new MongoRepositoryPocketAdapter(repository, mapper);
    }
}

