package co.com.bancolombia.mongo.pocket;

import co.com.bancolombia.mongo.exception.DatabaseException;
import co.com.bancolombia.model.pocket.Pocket;
import co.com.bancolombia.model.pocket.gateways.PocketRepository;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@Primary
@RequiredArgsConstructor
@Slf4j
public class StubPocketRepository implements PocketRepository {
    private final MongoRepositoryPocketAdapter mongoRepositoryAdapter;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @Override
    public Flux<Pocket> getAllPocketes() {
        return mongoRepositoryAdapter.getAllPocketes()
            .transformDeferred(CircuitBreakerOperator.of(getCircuitBreaker()))
            .onErrorResume(this::handleFluxError);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return mongoRepositoryAdapter.deleteById(id)
            .transformDeferred(CircuitBreakerOperator.of(getCircuitBreaker()))
            .onErrorResume(ex -> handleMonoVoidError(ex, "Deleted by ID: " + id));
    }

    private CircuitBreaker getCircuitBreaker() {
        return circuitBreakerRegistry.circuitBreaker("mongoCircuitBreaker");
    }

    private <T> Flux<T> handleFluxError(Throwable ex) {
        log.error("Pocket Error  - MongoDB is unavailable. Detail: {}", ex.getMessage());
        return Flux.error(new DatabaseException("Database service temporarily unavailable", ex));
    }

    private Mono<Pocket> handleMonoError(Throwable ex, String context) {
        log.error("Pocket Error - {}. Detail: {}", context, ex.getMessage());
        return Mono.error(new DatabaseException("Database service temporarily unavailable", ex));
    }

    private Mono<Void> handleMonoVoidError(Throwable ex, String context) {
        log.error("Pocket Error - {}. Detail: {}", context, ex.getMessage());
        return Mono.error(new DatabaseException("Database service temporarily unavailable", ex));
    }
}
