package co.com.bancolombia.mongo.pocket;

import co.com.bancolombia.model.pocket.Pocket;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface MongoDBPocketRepository extends ReactiveMongoRepository<Pocket, String>, ReactiveQueryByExampleExecutor<Pocket>, ReactiveCrudRepository<Pocket, String> {
}
