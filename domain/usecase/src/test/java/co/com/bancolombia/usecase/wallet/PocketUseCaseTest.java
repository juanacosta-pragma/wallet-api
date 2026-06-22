package co.com.bancolombia.usecase.wallet;

import co.com.bancolombia.model.pocket.Pocket;
import co.com.bancolombia.model.pocket.gateways.PocketRepository;
import co.com.bancolombia.model.wallet.Wallet;
import co.com.bancolombia.model.wallet.gateways.WalletRepository;
import co.com.bancolombia.usecase.wallet.commands.PocketLocator;
import co.com.bancolombia.usecase.wallet.commands.CreatePocketCommand;
import co.com.bancolombia.usecase.wallet.commands.UpdatePocketNameCommand;
import co.com.bancolombia.usecase.wallet.exceptions.PocketNotFoundException;
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
class PocketUseCaseTest {

    @Mock
    private PocketRepository pocketRepository;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private PocketUseCase useCase;

    private Wallet walletWith(Pocket... pocketes) {
        return Wallet.builder().id("F1").userId("JJ")
                .pocketes(List.of(pocketes))
                .build();
    }

    @Test
    void getAllPocketes_delegates() {
        Pocket b = Pocket.builder().id("B1").name("X").build();
        when(pocketRepository.getAllPocketes()).thenReturn(Flux.just(b));

        StepVerifier.create(useCase.getAllPocketes())
                .expectNext(b)
                .verifyComplete();
    }

    @Test
    void createWallet_appendsPocketWithGeneratedId_andSaves() {
        Pocket input = Pocket.builder().name("Centro").build();
        Wallet existing = walletWith(Pocket.builder().id("OLD").name("A").build());
        when(walletRepository.findById("F1")).thenReturn(Mono.just(existing));
        when(walletRepository.save(any(Wallet.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(useCase.createWallet(new CreatePocketCommand("F1", input)))
                .assertNext(saved -> {
                    assertThat(saved.getPocketes()).hasSize(2);
                    Pocket newPocket = saved.getPocketes().get(1);
                    assertThat(newPocket.getId()).isNotNull();
                    assertThat(newPocket.getName()).isEqualTo("Centro");
                })
                .verifyComplete();

        ArgumentCaptor<Wallet> captor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletRepository).save(captor.capture());
        assertThat(captor.getValue().getPocketes()).hasSize(2);
    }

    @Test
    void createWallet_appendsToEmptyPocketes() {
        Pocket input = Pocket.builder().name("Centro").build();
        Wallet existing = Wallet.builder().id("F1").userId("JJ").build(); // pocketes default empty list
        when(walletRepository.findById("F1")).thenReturn(Mono.just(existing));
        when(walletRepository.save(any(Wallet.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(useCase.createWallet(new CreatePocketCommand("F1", input)))
                .assertNext(saved -> assertThat(saved.getPocketes()).hasSize(1))
                .verifyComplete();
    }

    @Test
    void createWallet_walletNotFound_emitsError() {
        when(walletRepository.findById("X")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.createWallet(
                        new CreatePocketCommand("X", Pocket.builder().name("N").build())))
                .expectError(WalletNotFoundException.class)
                .verify();
    }

    @Test
    void getWalletById_existing_returnsPocket() {
        Pocket target = Pocket.builder().id("B1").name("Centro").build();
        Wallet wallet = walletWith(target, Pocket.builder().id("B2").name("Norte").build());
        when(walletRepository.findById("F1")).thenReturn(Mono.just(wallet));

        StepVerifier.create(useCase.getWalletById(new PocketLocator("F1", "B1")))
                .expectNext(target)
                .verifyComplete();
    }

    @Test
    void getWalletById_pocketNotFound_emitsPocketNotFoundException() {
        Wallet wallet = walletWith(Pocket.builder().id("B2").name("Norte").build());
        when(walletRepository.findById("F1")).thenReturn(Mono.just(wallet));

        StepVerifier.create(useCase.getWalletById(new PocketLocator("F1", "B1")))
                .expectError(PocketNotFoundException.class)
                .verify();
    }

    @Test
    void getWalletById_walletNotFound_emitsWalletNotFoundException() {
        when(walletRepository.findById("F1")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.getWalletById(new PocketLocator("F1", "B1")))
                .expectError(WalletNotFoundException.class)
                .verify();
    }

    @Test
    void updateWalletName_existingPocket_updatesNameAndSaves() {
        Pocket b1 = Pocket.builder().id("B1").name("Old").build();
        Pocket b2 = Pocket.builder().id("B2").name("Other").build();
        Wallet wallet = walletWith(b1, b2);
        when(walletRepository.findById("F1")).thenReturn(Mono.just(wallet));
        when(walletRepository.save(any(Wallet.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(useCase.updateWalletName(
                        new UpdatePocketNameCommand("F1", "B1", "New")))
                .assertNext(saved -> {
                    Pocket updated = saved.getPocketes().stream()
                            .filter(b -> "B1".equals(b.getId())).findFirst().orElseThrow();
                    assertThat(updated.getName()).isEqualTo("New");
                    Pocket other = saved.getPocketes().stream()
                            .filter(b -> "B2".equals(b.getId())).findFirst().orElseThrow();
                    assertThat(other.getName()).isEqualTo("Other");
                })
                .verifyComplete();
    }

    @Test
    void updateWalletName_pocketNotFound_emitsPocketNotFoundException() {
        Wallet wallet = walletWith(Pocket.builder().id("B2").name("Other").build());
        when(walletRepository.findById("F1")).thenReturn(Mono.just(wallet));

        StepVerifier.create(useCase.updateWalletName(
                        new UpdatePocketNameCommand("F1", "B1", "New")))
                .expectError(PocketNotFoundException.class)
                .verify();
    }

    @Test
    void updateWalletName_walletNotFound_emitsError() {
        when(walletRepository.findById("F1")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateWalletName(
                        new UpdatePocketNameCommand("F1", "B1", "New")))
                .expectError(WalletNotFoundException.class)
                .verify();
    }

    @Test
    void deletePocket_delegates() {
        when(pocketRepository.deleteById("B1")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.deletePocket("B1"))
                .verifyComplete();

        verify(pocketRepository).deleteById("B1");
    }
}

