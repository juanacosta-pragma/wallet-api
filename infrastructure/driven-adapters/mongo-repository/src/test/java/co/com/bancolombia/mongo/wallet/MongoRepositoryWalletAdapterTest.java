package co.com.bancolombia.mongo.wallet;

import co.com.bancolombia.model.wallet.Wallet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MongoRepositoryWalletAdapterTest {

    @Mock
    private MongoDBWalletRepository mongoDBRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private MongoRepositoryWalletAdapter adapter;

    @Test
    void getAllWallets_success() {
        Wallet wallet = Wallet.builder().id("1").userId("user1").build();
        when(mongoDBRepository.findAll()).thenReturn(Flux.just(wallet));
        when(objectMapper.map(any(), any())).thenReturn(wallet);

        StepVerifier.create(adapter.getAllWallets())
                .expectNext(wallet)
                .verifyComplete();
    }

    @Test
    void getAllWallets_empty() {
        when(mongoDBRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(adapter.getAllWallets())
                .verifyComplete();
    }

    @Test
    void findById_existing() {
        Wallet wallet = Wallet.builder().id("1").userId("user1").build();
        when(mongoDBRepository.findById("1")).thenReturn(Mono.just(wallet));
        when(objectMapper.map(any(), any())).thenReturn(wallet);

        StepVerifier.create(adapter.findById("1"))
                .expectNext(wallet)
                .verifyComplete();
    }

    @Test
    void findById_notFound() {
        when(mongoDBRepository.findById("nonexistent")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.findById("nonexistent"))
                .verifyComplete();
    }

    @Test
    void save_success() {
        Wallet wallet = Wallet.builder().id("1").userId("user1").build();
        when(objectMapper.map(any(), any())).thenReturn(wallet);
        when(mongoDBRepository.save(any())).thenReturn(Mono.just(wallet));

        StepVerifier.create(adapter.save(wallet))
                .expectNext(wallet)
                .verifyComplete();
    }

    @Test
    void deleteById_success() {
        when(mongoDBRepository.deleteById("1")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.deleteById("1"))
                .verifyComplete();
    }


}

