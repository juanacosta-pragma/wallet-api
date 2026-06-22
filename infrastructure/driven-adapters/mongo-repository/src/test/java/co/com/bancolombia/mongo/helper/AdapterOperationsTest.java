package co.com.bancolombia.mongo.helper;

import co.com.bancolombia.model.wallet.Wallet;
import co.com.bancolombia.mongo.wallet.MongoDBWalletRepository;
import co.com.bancolombia.mongo.wallet.MongoRepositoryWalletAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class AdapterOperationsTest {

    @Mock
    private MongoDBWalletRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    private MongoRepositoryWalletAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(objectMapper.map(any(), any())).thenAnswer(inv -> inv.getArgument(0));
        adapter = new MongoRepositoryWalletAdapter(repository, objectMapper);
    }

    @Test
    void getAllWallets_delegatesToFindAll() {
        Wallet f = Wallet.builder().id("1").userId("A").build();
        when(repository.findAll()).thenReturn(Flux.just(f));

        StepVerifier.create(adapter.getAllWallets())
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getAllWallets_empty() {
        when(repository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(adapter.getAllWallets())
                .verifyComplete();
    }

    @Test
    void getAllWallets_multipleWallets() {
        Wallet f1 = Wallet.builder().id("1").userId("A").build();
        Wallet f2 = Wallet.builder().id("2").userId("B").build();
        Wallet f3 = Wallet.builder().id("3").userId("C").build();
        when(repository.findAll()).thenReturn(Flux.just(f1, f2, f3));

        StepVerifier.create(adapter.getAllWallets())
                .expectNextCount(3)
                .verifyComplete();
    }

    @Test
    void findById_existing() {
        Wallet wallet = Wallet.builder().id("1").userId("A").build();
        when(repository.findById("1")).thenReturn(Mono.just(wallet));

        StepVerifier.create(adapter.findById("1"))
                .expectNext(wallet)
                .verifyComplete();
    }

    @Test
    void findById_notFound() {
        when(repository.findById("nonexistent")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById("nonexistent"))
                .verifyComplete();
    }

    @Test
    void save_successful() {
        Wallet wallet = Wallet.builder().id("1").userId("A").build();
        when(repository.save(wallet)).thenReturn(Mono.just(wallet));

        StepVerifier.create(adapter.save(wallet))
                .expectNext(wallet)
                .verifyComplete();
    }

    @Test
    void save_error() {
        Wallet wallet = Wallet.builder().id("1").userId("A").build();
        when(repository.save(wallet)).thenReturn(Mono.error(new RuntimeException("DB error")));

        StepVerifier.create(adapter.save(wallet))
                .expectErrorMessage("DB error")
                .verify();
    }

    @Test
    void deleteById_successful() {
        when(repository.deleteById("1")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.deleteById("1"))
                .verifyComplete();
    }

    @Test
    void deleteById_delegates() {
        when(repository.deleteById("key")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.deleteById("key"))
                .verifyComplete();
    }

    @Test
    void deleteById_error() {
        when(repository.deleteById("1")).thenReturn(Mono.error(new RuntimeException("Delete failed")));

        StepVerifier.create(adapter.deleteById("1"))
                .expectErrorMessage("Delete failed")
                .verify();
    }
}
