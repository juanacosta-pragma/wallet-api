package co.com.bancolombia.mongo.wallet;

import co.com.bancolombia.model.wallet.Wallet;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface MongoDBWalletRepository extends ReactiveMongoRepository<Wallet, String>, ReactiveQueryByExampleExecutor<Wallet>, ReactiveCrudRepository<Wallet, String> {
}
