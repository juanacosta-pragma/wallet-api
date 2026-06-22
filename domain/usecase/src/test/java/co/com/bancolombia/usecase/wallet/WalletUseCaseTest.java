package co.com.bancolombia.usecase.wallet;

import co.com.bancolombia.model.wallet.Wallet;
import co.com.bancolombia.model.wallet.gateways.WalletRepository;
import co.com.bancolombia.usecase.wallet.exceptions.WalletNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletUseCaseTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletUseCase useCase;

    @Test
    void getAllWallets_returnsRepositoryFlux() {
        Wallet f1 = Wallet.builder().id("1").userId("A").build();
        Wallet f2 = Wallet.builder().id("2").userId("B").build();
        when(walletRepository.getAllWallets()).thenReturn(Flux.just(f1, f2));

        StepVerifier.create(useCase.getAllWallets())
                .expectNext(f1, f2)
                .verifyComplete();

        verify(walletRepository).getAllWallets();
    }

    @Test
    void getAllWallets_empty_completesEmpty() {
        when(walletRepository.getAllWallets()).thenReturn(Flux.empty());

        StepVerifier.create(useCase.getAllWallets())
                .verifyComplete();
    }

    @Test
    void createWallet_clearsPocketes_andSaves() {
        Wallet input = Wallet.builder().userId("New").build();
        Wallet saved = Wallet.builder().id("1").userId("New").pocketes(List.of()).build();
        when(walletRepository.save(any(Wallet.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(useCase.createWallet(input))
                .expectNext(saved)
                .verifyComplete();

        ArgumentCaptor<Wallet> captor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletRepository).save(captor.capture());
        assertThat(captor.getValue().getPocketes()).isEmpty();
        assertThat(captor.getValue().getUserId()).isEqualTo("New");
    }

    @Test
    void createWallet_propagatesRepositoryError() {
        Wallet input = Wallet.builder().userId("Bad").build();
        when(walletRepository.save(any(Wallet.class)))
                .thenReturn(Mono.error(new RuntimeException("db down")));

        StepVerifier.create(useCase.createWallet(input))
                .expectErrorMessage("db down")
                .verify();
    }

    @Test
    void getWalletById_existing_returnsWallet() {
        Wallet f = Wallet.builder().id("1").userId("A").build();
        when(walletRepository.findById("1")).thenReturn(Mono.just(f));

        StepVerifier.create(useCase.getWalletById("1"))
                .expectNext(f)
                .verifyComplete();
    }

    @Test
    void getWalletById_notFound_emitsWalletNotFoundException() {
        when(walletRepository.findById("X")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.getWalletById("X"))
                .expectErrorSatisfies(err -> {
                    assertThat(err).isInstanceOf(WalletNotFoundException.class);
                    assertThat(err.getMessage()).contains("X");
                })
                .verify();
    }

    @Test
    void updateWalletName_existing_updatesAndSaves() {
        Wallet existing = Wallet.builder().id("1").userId("Old").build();
        Wallet updatedSaved = Wallet.builder().id("1").userId("New").build();
        when(walletRepository.findById("1")).thenReturn(Mono.just(existing));
        when(walletRepository.save(any(Wallet.class))).thenReturn(Mono.just(updatedSaved));

        StepVerifier.create(useCase.updateWalletName("1", "New"))
                .expectNext(updatedSaved)
                .verifyComplete();

        ArgumentCaptor<Wallet> captor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletRepository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo("New");
    }

    @Test
    void updateWalletName_notFound_emitsError() {
        when(walletRepository.findById("X")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateWalletName("X", "New"))
                .expectError(WalletNotFoundException.class)
                .verify();
    }

    @Test
    void deleteWallet_delegatesToRepository() {
        when(walletRepository.deleteById("1")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.deleteWallet("1"))
                .verifyComplete();

        verify(walletRepository).deleteById("1");
    }

    @Test
    void deleteWallet_propagatesError() {
        when(walletRepository.deleteById("X"))
                .thenReturn(Mono.error(new RuntimeException("fail")));

        StepVerifier.create(useCase.deleteWallet("X"))
                .expectErrorMessage("fail")
                .verify();
    }

    @Test
    void createWallet_withNullPocketes() {
        Wallet input = Wallet.builder().userId("Test").pocketes(null).build();
        Wallet saved = Wallet.builder().id("1").userId("Test").pocketes(List.of()).build();
        when(walletRepository.save(any(Wallet.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(useCase.createWallet(input))
                .expectNext(saved)
                .verifyComplete();
    }

    @Test
    void updateWalletName_saveThrowsError() {
        Wallet existing = Wallet.builder().id("1").userId("Old").build();
        when(walletRepository.findById("1")).thenReturn(Mono.just(existing));
        when(walletRepository.save(any(Wallet.class)))
                .thenReturn(Mono.error(new RuntimeException("Save failed")));

        StepVerifier.create(useCase.updateWalletName("1", "New"))
                .expectErrorMessage("Save failed")
                .verify();
    }

    @Test
    void getWalletById_withEmptyUserId() {
        Wallet f = Wallet.builder().id("1").userId("").build();
        when(walletRepository.findById("1")).thenReturn(Mono.just(f));

        StepVerifier.create(useCase.getWalletById("1"))
                .expectNext(f)
                .verifyComplete();
    }

    @Test
    void createWallet_multipleWallets() {
        Wallet input1 = Wallet.builder().userId("User1").build();
        Wallet input2 = Wallet.builder().userId("User2").build();
        Wallet saved1 = Wallet.builder().id("1").userId("User1").pocketes(List.of()).build();
        Wallet saved2 = Wallet.builder().id("2").userId("User2").pocketes(List.of()).build();
        when(walletRepository.save(any(Wallet.class)))
                .thenReturn(Mono.just(saved1), Mono.just(saved2));

        StepVerifier.create(useCase.createWallet(input1))
                .expectNext(saved1)
                .verifyComplete();

        StepVerifier.create(useCase.createWallet(input2))
                .expectNext(saved2)
                .verifyComplete();
    }

    @Test
    void updateWalletName_withNullNewName() {
        Wallet existing = Wallet.builder().id("1").userId("Old").build();
        Wallet updated = Wallet.builder().id("1").userId(null).build();
        when(walletRepository.findById("1")).thenReturn(Mono.just(existing));
        when(walletRepository.save(any(Wallet.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(useCase.updateWalletName("1", null))
                .expectNext(updated)
                .verifyComplete();
    }
}

