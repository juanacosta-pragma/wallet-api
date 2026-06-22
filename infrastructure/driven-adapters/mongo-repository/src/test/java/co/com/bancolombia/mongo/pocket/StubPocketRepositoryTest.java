package co.com.bancolombia.mongo.pocket;

import co.com.bancolombia.mongo.exception.DatabaseException;
import co.com.bancolombia.model.pocket.Pocket;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StubPocketRepositoryTest {

    @Mock
    private MongoRepositoryPocketAdapter mongoRepositoryAdapter;

    @Mock
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Mock
    private CircuitBreaker circuitBreaker;

    @InjectMocks
    private StubPocketRepository stubPocketRepository;

    private Pocket pocket1;
    private Pocket pocket2;

    @BeforeEach
    void setUp() {
        pocket1 = Pocket.builder().id("p1").name("Savings").transactions(new ArrayList<>()).build();
        pocket2 = Pocket.builder().id("p2").name("Investment").transactions(new ArrayList<>()).build();

        when(circuitBreakerRegistry.circuitBreaker("mongoCircuitBreaker"))
                .thenReturn(circuitBreaker);
    }

    // ===== getAllPocketes Tests =====

    @Test
    void getAllPocketes_success_returnsAllPockets() {
        when(mongoRepositoryAdapter.getAllPocketes()).thenReturn(Flux.just(pocket1, pocket2));

        StepVerifier.create(stubPocketRepository.getAllPocketes())
                .expectNext(pocket1, pocket2)
                .verifyComplete();
    }

    @Test
    void getAllPocketes_empty_returnsEmptyFlux() {
        when(mongoRepositoryAdapter.getAllPocketes()).thenReturn(Flux.empty());

        StepVerifier.create(stubPocketRepository.getAllPocketes())
                .verifyComplete();
    }

    @Test
    void getAllPocketes_error_handlesDatabaseException() {
        RuntimeException mongoError = new RuntimeException("MongoDB connection failed");
        when(mongoRepositoryAdapter.getAllPocketes())
                .thenReturn(Flux.error(mongoError));

        StepVerifier.create(stubPocketRepository.getAllPocketes())
                .expectError(DatabaseException.class)
                .verify();
    }

    @Test
    void getAllPocketes_multiplePockets_processesAll() {
        Pocket pocket3 = Pocket.builder().id("p3").name("Emergency").transactions(new ArrayList<>()).build();
        Pocket pocket4 = Pocket.builder().id("p4").name("Vacation").transactions(new ArrayList<>()).build();

        when(mongoRepositoryAdapter.getAllPocketes())
                .thenReturn(Flux.just(pocket1, pocket2, pocket3, pocket4));

        StepVerifier.create(stubPocketRepository.getAllPocketes())
                .expectNext(pocket1, pocket2, pocket3, pocket4)
                .verifyComplete();
    }

    @Test
    void getAllPocketes_errorMessagePreserved() {
        String errorMsg = "Connection timeout to MongoDB";
        RuntimeException mongoError = new RuntimeException(errorMsg);
        when(mongoRepositoryAdapter.getAllPocketes())
                .thenReturn(Flux.error(mongoError));

        StepVerifier.create(stubPocketRepository.getAllPocketes())
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(DatabaseException.class);
                    assertThat(error.getMessage()).contains("temporarily unavailable");
                })
                .verify();
    }

    @Test
    void getAllPocketes_withLargeDataset() {
        Flux<Pocket> largeDataset = Flux.range(0, 500)
                .map(i -> Pocket.builder()
                        .id("pocket" + i)
                        .name("Pocket Name " + i)
                        .transactions(new ArrayList<>())
                        .build());

        when(mongoRepositoryAdapter.getAllPocketes()).thenReturn(largeDataset);

        StepVerifier.create(stubPocketRepository.getAllPocketes())
                .expectNextCount(500)
                .verifyComplete();
    }

    // ===== deleteById Tests =====

    @Test
    void deleteById_success_completesVoid() {
        when(mongoRepositoryAdapter.deleteById("p1"))
                .thenReturn(Mono.empty());

        StepVerifier.create(stubPocketRepository.deleteById("p1"))
                .verifyComplete();
    }

    @Test
    void deleteById_error_throwsDatabaseException() {
        RuntimeException mongoError = new RuntimeException("Delete operation failed");
        when(mongoRepositoryAdapter.deleteById("p1"))
                .thenReturn(Mono.error(mongoError));

        StepVerifier.create(stubPocketRepository.deleteById("p1"))
                .expectError(DatabaseException.class)
                .verify();
    }

    @Test
    void deleteById_nonexistentPocket_stillCompletes() {
        when(mongoRepositoryAdapter.deleteById("nonexistent"))
                .thenReturn(Mono.empty());

        StepVerifier.create(stubPocketRepository.deleteById("nonexistent"))
                .verifyComplete();
    }

    @Test
    void deleteById_withNullId() {
        when(mongoRepositoryAdapter.deleteById(null))
                .thenReturn(Mono.empty());

        StepVerifier.create(stubPocketRepository.deleteById(null))
                .verifyComplete();
    }

    @Test
    void deleteById_withEmptyString() {
        when(mongoRepositoryAdapter.deleteById(""))
                .thenReturn(Mono.empty());

        StepVerifier.create(stubPocketRepository.deleteById(""))
                .verifyComplete();
    }

    @Test
    void deleteById_multipleDeletes_sequential() {
        when(mongoRepositoryAdapter.deleteById("p1")).thenReturn(Mono.empty());
        when(mongoRepositoryAdapter.deleteById("p2")).thenReturn(Mono.empty());

        StepVerifier.create(stubPocketRepository.deleteById("p1"))
                .verifyComplete();

        StepVerifier.create(stubPocketRepository.deleteById("p2"))
                .verifyComplete();
    }

    @Test
    void deleteById_errorIncludesId() {
        RuntimeException mongoError = new RuntimeException("Delete failed");
        when(mongoRepositoryAdapter.deleteById("p1"))
                .thenReturn(Mono.error(mongoError));

        StepVerifier.create(stubPocketRepository.deleteById("p1"))
                .expectError(DatabaseException.class)
                .verify();
    }

    @Test
    void deleteById_withSpecialCharactersInId() {
        String specialId = "pocket-@#$%^&*()";
        when(mongoRepositoryAdapter.deleteById(specialId))
                .thenReturn(Mono.empty());

        StepVerifier.create(stubPocketRepository.deleteById(specialId))
                .verifyComplete();
    }

    // ===== Circuit Breaker Tests =====

    @Test
    void circuitBreakerIsRetrieved() {
        when(mongoRepositoryAdapter.getAllPocketes()).thenReturn(Flux.just(pocket1));

        stubPocketRepository.getAllPocketes();

        verify(circuitBreakerRegistry).circuitBreaker("mongoCircuitBreaker");
    }

    @Test
    void circuitBreakerRegistryIsUsedForEachOperation() {
        when(mongoRepositoryAdapter.getAllPocketes()).thenReturn(Flux.just(pocket1));
        when(mongoRepositoryAdapter.deleteById("p1")).thenReturn(Mono.empty());

        stubPocketRepository.getAllPocketes().blockFirst();
        stubPocketRepository.deleteById("p1").block();

        verify(circuitBreakerRegistry).circuitBreaker("mongoCircuitBreaker");
    }

    // ===== Error Handling Tests =====

    @Test
    void getAllPocketes_errorDoesNotPropagateToFlux() {
        RuntimeException mongoError = new RuntimeException("Service unavailable");
        when(mongoRepositoryAdapter.getAllPocketes())
                .thenReturn(Flux.error(mongoError));

        StepVerifier.create(stubPocketRepository.getAllPocketes())
                .expectErrorMatches(error ->
                    error instanceof DatabaseException &&
                    "Database service temporarily unavailable".equals(error.getMessage())
                )
                .verify();
    }

    @Test
    void deleteById_errorDoesNotPropagateToMono() {
        RuntimeException mongoError = new RuntimeException("Write failed");
        when(mongoRepositoryAdapter.deleteById("p1"))
                .thenReturn(Mono.error(mongoError));

        StepVerifier.create(stubPocketRepository.deleteById("p1"))
                .expectErrorMatches(error ->
                    error instanceof DatabaseException &&
                    "Database service temporarily unavailable".equals(error.getMessage())
                )
                .verify();
    }

    // ===== Edge Cases =====

    @Test
    void getAllPocketes_withNull() {
        when(mongoRepositoryAdapter.getAllPocketes()).thenReturn(Flux.empty());

        StepVerifier.create(stubPocketRepository.getAllPocketes())
                .verifyComplete();
    }

    @Test
    void getAllPocketes_withComplexPocketStructure() {
        Pocket complexPocket = Pocket.builder()
                .id("complex-pocket-@123")
                .name("Pocket with special chars: áéíóú")
                .transactions(new ArrayList<>())
                .build();

        when(mongoRepositoryAdapter.getAllPocketes())
                .thenReturn(Flux.just(complexPocket));

        StepVerifier.create(stubPocketRepository.getAllPocketes())
                .assertNext(p -> {
                    assertThat(p.getId()).isEqualTo("complex-pocket-@123");
                    assertThat(p.getName()).contains("special chars");
                })
                .verifyComplete();
    }

    @Test
    void deleteById_calledMultipleTimesWithSameId() {
        when(mongoRepositoryAdapter.deleteById("p1"))
                .thenReturn(Mono.empty());

        for (int i = 0; i < 3; i++) {
            StepVerifier.create(stubPocketRepository.deleteById("p1"))
                    .verifyComplete();
        }

        verify(mongoRepositoryAdapter).deleteById("p1");
    }

    @Test
    void getAllPocketes_errorWithDifferentExceptionTypes() {
        // Test with different exception types
        when(mongoRepositoryAdapter.getAllPocketes())
                .thenReturn(Flux.error(new IllegalStateException("Invalid state")));

        StepVerifier.create(stubPocketRepository.getAllPocketes())
                .expectError(DatabaseException.class)
                .verify();
    }

    @Test
    void deleteById_timeoutScenario() {
        when(mongoRepositoryAdapter.deleteById("p1"))
                .thenReturn(Mono.empty());

        StepVerifier.create(stubPocketRepository.deleteById("p1"))
                .verifyComplete();
    }

    @Test
    void getAllPocketes_consecutiveCalls() {
        when(mongoRepositoryAdapter.getAllPocketes())
                .thenReturn(Flux.just(pocket1))
                .thenReturn(Flux.just(pocket2));

        StepVerifier.create(stubPocketRepository.getAllPocketes())
                .expectNext(pocket1)
                .verifyComplete();

        StepVerifier.create(stubPocketRepository.getAllPocketes())
                .expectNext(pocket2)
                .verifyComplete();
    }
}

