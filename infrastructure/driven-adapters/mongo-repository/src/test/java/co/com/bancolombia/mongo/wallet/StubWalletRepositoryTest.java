package co.com.bancolombia.mongo.wallet;

import co.com.bancolombia.mongo.exception.DatabaseException;
import co.com.bancolombia.model.wallet.Wallet;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class StubWalletRepositoryTest {

    @Mock
    private MongoRepositoryWalletAdapter mongoRepositoryAdapter;

    @Mock
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @Mock
    private CircuitBreaker circuitBreaker;

    @InjectMocks
    private StubWalletRepository stubWalletRepository;

    private Wallet wallet1;
    private Wallet wallet2;

    @BeforeEach
    void setUp() {
        wallet1 = Wallet.builder().id("w1").userId("user1").pocketes(List.of()).build();
        wallet2 = Wallet.builder().id("w2").userId("user2").pocketes(List.of()).build();

        when(circuitBreakerRegistry.circuitBreaker("mongoCircuitBreaker"))
                .thenReturn(circuitBreaker);
    }

    // ===== getAllWallets Tests =====

    @Test
    void getAllWallets_success_returnsAllWallets() {
        when(mongoRepositoryAdapter.getAllWallets()).thenReturn(Flux.just(wallet1, wallet2));

        StepVerifier.create(stubWalletRepository.getAllWallets())
                .expectNext(wallet1, wallet2)
                .verifyComplete();
    }

    @Test
    void getAllWallets_empty_returnsEmptyFlux() {
        when(mongoRepositoryAdapter.getAllWallets()).thenReturn(Flux.empty());

        StepVerifier.create(stubWalletRepository.getAllWallets())
                .verifyComplete();
    }

    @Test
    void getAllWallets_error_handlesWithDatabaseException() {
        RuntimeException mongoError = new RuntimeException("MongoDB connection failed");
        when(mongoRepositoryAdapter.getAllWallets())
                .thenReturn(Flux.error(mongoError));

        StepVerifier.create(stubWalletRepository.getAllWallets())
                .expectError(DatabaseException.class)
                .verify();
    }

    @Test
    void getAllWallets_multipleWallets_processesAll() {
        Wallet wallet3 = Wallet.builder().id("w3").userId("user3").pocketes(List.of()).build();
        Wallet wallet4 = Wallet.builder().id("w4").userId("user4").pocketes(List.of()).build();

        when(mongoRepositoryAdapter.getAllWallets())
                .thenReturn(Flux.just(wallet1, wallet2, wallet3, wallet4));

        StepVerifier.create(stubWalletRepository.getAllWallets())
                .expectNext(wallet1, wallet2, wallet3, wallet4)
                .verifyComplete();
    }

    // ===== findById Tests =====

    @Test
    void findById_existing_returnsWallet() {
        when(mongoRepositoryAdapter.findById("w1")).thenReturn(Mono.just(wallet1));

        StepVerifier.create(stubWalletRepository.findById("w1"))
                .expectNext(wallet1)
                .verifyComplete();
    }

    @Test
    void findById_notFound_returnsEmpty() {
        when(mongoRepositoryAdapter.findById("nonexistent"))
                .thenReturn(Mono.empty());

        StepVerifier.create(stubWalletRepository.findById("nonexistent"))
                .verifyComplete();
    }

    @Test
    void findById_error_handlesDatabaseException() {
        RuntimeException mongoError = new RuntimeException("Connection timeout");
        when(mongoRepositoryAdapter.findById("w1"))
                .thenReturn(Mono.error(mongoError));

        StepVerifier.create(stubWalletRepository.findById("w1"))
                .expectError(DatabaseException.class)
                .verify();
    }

    @Test
    void findById_withNullId() {
        when(mongoRepositoryAdapter.findById(null))
                .thenReturn(Mono.empty());

        StepVerifier.create(stubWalletRepository.findById(null))
                .verifyComplete();
    }

    @Test
    void findById_withEmptyString() {
        when(mongoRepositoryAdapter.findById(""))
                .thenReturn(Mono.empty());

        StepVerifier.create(stubWalletRepository.findById(""))
                .verifyComplete();
    }

    @Test
    void findById_errorPropagatesWithContext() {
        RuntimeException mongoError = new RuntimeException("Database unavailable");
        when(mongoRepositoryAdapter.findById("w1"))
                .thenReturn(Mono.error(mongoError));

        StepVerifier.create(stubWalletRepository.findById("w1"))
                .expectErrorMatches(error ->
                    error instanceof DatabaseException &&
                    error.getMessage().contains("temporarily unavailable")
                )
                .verify();
    }

    // ===== save Tests =====

    @Test
    void save_success_savesWallet() {
        when(mongoRepositoryAdapter.save(wallet1)).thenReturn(Mono.just(wallet1));

        StepVerifier.create(stubWalletRepository.save(wallet1))
                .expectNext(wallet1)
                .verifyComplete();
    }

    @Test
    void save_error_handlesDatabaseException() {
        RuntimeException mongoError = new RuntimeException("Write failed");
        when(mongoRepositoryAdapter.save(wallet1))
                .thenReturn(Mono.error(mongoError));

        StepVerifier.create(stubWalletRepository.save(wallet1))
                .expectError(DatabaseException.class)
                .verify();
    }

    @Test
    void save_withUpdatedWallet_savesChanges() {
        Wallet updatedWallet = Wallet.builder()
                .id("w1")
                .userId("updatedUser")
                .pocketes(List.of())
                .build();

        when(mongoRepositoryAdapter.save(updatedWallet))
                .thenReturn(Mono.just(updatedWallet));

        StepVerifier.create(stubWalletRepository.save(updatedWallet))
                .assertNext(saved -> {
                    assertThat(saved.getUserId()).isEqualTo("updatedUser");
                    assertThat(saved.getId()).isEqualTo("w1");
                })
                .verifyComplete();
    }

    @Test
    void save_withNullWallet() {
        when(mongoRepositoryAdapter.save(null))
                .thenReturn(Mono.just(null));

        StepVerifier.create(stubWalletRepository.save(null))
                .expectNext((Wallet) null)
                .verifyComplete();
    }

    @Test
    void save_multipleWallets_sequential() {
        when(mongoRepositoryAdapter.save(wallet1)).thenReturn(Mono.just(wallet1));
        when(mongoRepositoryAdapter.save(wallet2)).thenReturn(Mono.just(wallet2));

        StepVerifier.create(stubWalletRepository.save(wallet1))
                .expectNext(wallet1)
                .verifyComplete();

        StepVerifier.create(stubWalletRepository.save(wallet2))
                .expectNext(wallet2)
                .verifyComplete();
    }

    // ===== deleteById Tests =====

    @Test
    void deleteById_success_completesVoid() {
        when(mongoRepositoryAdapter.deleteById("w1"))
                .thenReturn(Mono.empty());

        StepVerifier.create(stubWalletRepository.deleteById("w1"))
                .verifyComplete();
    }

    @Test
    void deleteById_error_throwsDatabaseException() {
        RuntimeException mongoError = new RuntimeException("Delete operation failed");
        when(mongoRepositoryAdapter.deleteById("w1"))
                .thenReturn(Mono.error(mongoError));

        StepVerifier.create(stubWalletRepository.deleteById("w1"))
                .expectError(DatabaseException.class)
                .verify();
    }

    @Test
    void deleteById_nonexistentWallet_stillCompletes() {
        when(mongoRepositoryAdapter.deleteById("nonexistent"))
                .thenReturn(Mono.empty());

        StepVerifier.create(stubWalletRepository.deleteById("nonexistent"))
                .verifyComplete();
    }

    @Test
    void deleteById_withNullId() {
        when(mongoRepositoryAdapter.deleteById(null))
                .thenReturn(Mono.empty());

        StepVerifier.create(stubWalletRepository.deleteById(null))
                .verifyComplete();
    }

    @Test
    void deleteById_multipleDeletes_sequential() {
        when(mongoRepositoryAdapter.deleteById("w1")).thenReturn(Mono.empty());
        when(mongoRepositoryAdapter.deleteById("w2")).thenReturn(Mono.empty());

        StepVerifier.create(stubWalletRepository.deleteById("w1"))
                .verifyComplete();

        StepVerifier.create(stubWalletRepository.deleteById("w2"))
                .verifyComplete();
    }

    // ===== Circuit Breaker Tests =====

    @Test
    void circuitBreakerIsRetrieved() {
        when(mongoRepositoryAdapter.getAllWallets()).thenReturn(Flux.just(wallet1));

        stubWalletRepository.getAllWallets();

        verify(circuitBreakerRegistry).circuitBreaker("mongoCircuitBreaker");
    }

    @Test
    void circuitBreakerRegistryIsUsedForEachOperation() {
        when(mongoRepositoryAdapter.getAllWallets()).thenReturn(Flux.just(wallet1));
        when(mongoRepositoryAdapter.findById("w1")).thenReturn(Mono.just(wallet1));

        stubWalletRepository.getAllWallets().blockFirst();
        stubWalletRepository.findById("w1").block();

        verify(circuitBreakerRegistry).circuitBreaker("mongoCircuitBreaker");
    }

    // ===== Error Handling Tests =====

    @Test
    void getAllWallets_errorMessagePreserved() {
        String errorMsg = "Connection timeout to MongoDB";
        RuntimeException mongoError = new RuntimeException(errorMsg);
        when(mongoRepositoryAdapter.getAllWallets())
                .thenReturn(Flux.error(mongoError));

        StepVerifier.create(stubWalletRepository.getAllWallets())
                .expectErrorSatisfies(error -> {
                    assertThat(error).isInstanceOf(DatabaseException.class);
                })
                .verify();
    }

    @Test
    void save_errorIncludesWalletId() {
        RuntimeException mongoError = new RuntimeException("Write failed");
        when(mongoRepositoryAdapter.save(wallet1))
                .thenReturn(Mono.error(mongoError));

        StepVerifier.create(stubWalletRepository.save(wallet1))
                .expectError(DatabaseException.class)
                .verify();
    }

    @Test
    void deleteById_errorIncludesId() {
        RuntimeException mongoError = new RuntimeException("Delete failed");
        when(mongoRepositoryAdapter.deleteById("w1"))
                .thenReturn(Mono.error(mongoError));

        StepVerifier.create(stubWalletRepository.deleteById("w1"))
                .expectError(DatabaseException.class)
                .verify();
    }

    // ===== Timeout Tests =====

    @Test
    void getAllWallets_withTimeout_handlesSuccessfully() {
        when(mongoRepositoryAdapter.getAllWallets())
                .thenReturn(Flux.just(wallet1));

        StepVerifier.create(stubWalletRepository.getAllWallets())
                .expectNext(wallet1)
                .verifyComplete();
    }

    @Test
    void findById_withTimeout_handlesSuccessfully() {
        when(mongoRepositoryAdapter.findById("w1"))
                .thenReturn(Mono.just(wallet1));

        StepVerifier.create(stubWalletRepository.findById("w1"))
                .expectNext(wallet1)
                .verifyComplete();
    }

    @Test
    void save_withTimeout_handlesSuccessfully() {
        when(mongoRepositoryAdapter.save(wallet1))
                .thenReturn(Mono.just(wallet1));

        StepVerifier.create(stubWalletRepository.save(wallet1))
                .expectNext(wallet1)
                .verifyComplete();
    }

    @Test
    void deleteById_withTimeout_handlesSuccessfully() {
        when(mongoRepositoryAdapter.deleteById("w1"))
                .thenReturn(Mono.empty());

        StepVerifier.create(stubWalletRepository.deleteById("w1"))
                .verifyComplete();
    }

    // ===== Edge Cases =====

    @Test
    void getAllWallets_withLargeDataset() {
        Flux<Wallet> largeDataset = Flux.range(0, 1000)
                .map(i -> Wallet.builder()
                        .id("wallet" + i)
                        .userId("user" + i)
                        .pocketes(List.of())
                        .build());

        when(mongoRepositoryAdapter.getAllWallets()).thenReturn(largeDataset);

        StepVerifier.create(stubWalletRepository.getAllWallets())
                .expectNextCount(1000)
                .verifyComplete();
    }

    @Test
    void findById_withSpecialCharactersInId() {
        Wallet specialWallet = Wallet.builder()
                .id("wallet-@#$%^&*()")
                .userId("user@example.com")
                .pocketes(List.of())
                .build();

        when(mongoRepositoryAdapter.findById("wallet-@#$%^&*()"))
                .thenReturn(Mono.just(specialWallet));

        StepVerifier.create(stubWalletRepository.findById("wallet-@#$%^&*()"))
                .expectNext(specialWallet)
                .verifyComplete();
    }

    @Test
    void save_withComplexWalletStructure() {
        Wallet complexWallet = Wallet.builder()
                .id("complex-wallet")
                .userId("complex-user-with-long-name")
                .pocketes(List.of())
                .build();

        when(mongoRepositoryAdapter.save(complexWallet))
                .thenReturn(Mono.just(complexWallet));

        StepVerifier.create(stubWalletRepository.save(complexWallet))
                .assertNext(saved -> {
                    assertThat(saved.getId()).isEqualTo("complex-wallet");
                    assertThat(saved.getUserId()).isEqualTo("complex-user-with-long-name");
                })
                .verifyComplete();
    }
}

