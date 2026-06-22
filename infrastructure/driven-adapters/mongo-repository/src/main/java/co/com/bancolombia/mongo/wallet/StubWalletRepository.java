package co.com.bancolombia.mongo.wallet;

import co.com.bancolombia.mongo.exception.DatabaseException;
import co.com.bancolombia.model.wallet.Wallet;
import co.com.bancolombia.model.wallet.gateways.WalletRepository;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Repository
@Primary
@RequiredArgsConstructor
@Slf4j
public class StubWalletRepository implements WalletRepository {
    private final MongoRepositoryWalletAdapter mongoRepositoryAdapter;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @Override
    public Flux<Wallet> getAllWallets() {
        return mongoRepositoryAdapter.getAllWallets()
                .timeout(Duration.ofMillis(800))
                .transformDeferred(CircuitBreakerOperator.of(getCircuitBreaker()))
                .onErrorResume(this::handleFluxError);
    }

    @Override
    public Mono<Wallet> findById(String id) {
        return mongoRepositoryAdapter.findById(id)
                .timeout(Duration.ofMillis(800))
                .transformDeferred(CircuitBreakerOperator.of(getCircuitBreaker()))
                .onErrorResume(ex -> handleMonoError(ex, "Getting by ID: " + id));
    }

    @Override
    public Mono<Wallet> save(Wallet wallet) {
        return mongoRepositoryAdapter.save(wallet)
                .timeout(Duration.ofMillis(800))
                .transformDeferred(CircuitBreakerOperator.of(getCircuitBreaker()))
                .onErrorResume(ex -> handleMonoError(ex, "Save wallet: " + wallet.getId()));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return mongoRepositoryAdapter.deleteById(id)
                .timeout(Duration.ofMillis(800))
                .transformDeferred(CircuitBreakerOperator.of(getCircuitBreaker()))
                .onErrorResume(ex -> handleMonoVoidError(ex, "Deleted by ID: " + id));
    }

    private CircuitBreaker getCircuitBreaker() {
        return circuitBreakerRegistry.circuitBreaker("mongoCircuitBreaker");
    }

    private <T> Flux<T> handleFluxError(Throwable ex) {
        log.error("Wallet - MongoDB Service is unavailable. Detail: {}", ex.getMessage());
        return Flux.error(new DatabaseException("Database service temporarily unavailable", ex));
    }

    private Mono<Wallet> handleMonoError(Throwable ex, String context) {
        log.error("Wallet - {}. Detail: {}", context, ex.getMessage());
        return Mono.error(new DatabaseException("Database service temporarily unavailable", ex));
    }

    private Mono<Void> handleMonoVoidError(Throwable ex, String context) {
        log.error("Wallet - {}. Detail: {}", context, ex.getMessage());
        return Mono.error(new DatabaseException("Database service temporarily unavailable", ex));
    }
}


