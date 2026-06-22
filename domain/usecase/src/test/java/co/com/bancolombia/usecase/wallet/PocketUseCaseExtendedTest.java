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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PocketUseCaseExtendedTest {

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
    void getAllPocketes_withMultiplePockets() {
        Pocket b1 = Pocket.builder().id("B1").name("X").build();
        Pocket b2 = Pocket.builder().id("B2").name("Y").build();
        when(pocketRepository.getAllPocketes()).thenReturn(Flux.just(b1, b2));

        StepVerifier.create(useCase.getAllPocketes())
                .expectNext(b1, b2)
                .verifyComplete();
    }

    @Test
    void createWallet_verifyMultiplePockets() {
        Pocket input = Pocket.builder().name("Center").build();
        Pocket oldPocket = Pocket.builder().id("OLD1").name("A").build();
        Pocket oldPocket2 = Pocket.builder().id("OLD2").name("B").build();
        Wallet existing = walletWith(oldPocket, oldPocket2);
        when(walletRepository.findById("F1")).thenReturn(Mono.just(existing));
        when(walletRepository.save(any(Wallet.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(useCase.createWallet(new CreatePocketCommand("F1", input)))
                .assertNext(saved -> assertThat(saved.getPocketes()).hasSize(3))
                .verifyComplete();
    }

    @Test
    void getWalletById_withEmptyPocketes() {
        Wallet wallet = Wallet.builder().id("F1").userId("JJ").pocketes(List.of()).build();
        when(walletRepository.findById("F1")).thenReturn(Mono.just(wallet));

        StepVerifier.create(useCase.getWalletById(new PocketLocator("F1", "B1")))
                .expectError(PocketNotFoundException.class)
                .verify();
    }

    @Test
    void updateWalletName_multiplePockets() {
        Pocket b1 = Pocket.builder().id("B1").name("Old1").build();
        Pocket b2 = Pocket.builder().id("B2").name("Other").build();
        Pocket b3 = Pocket.builder().id("B3").name("Third").build();
        Wallet wallet = walletWith(b1, b2, b3);
        when(walletRepository.findById("F1")).thenReturn(Mono.just(wallet));
        when(walletRepository.save(any(Wallet.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(useCase.updateWalletName(
                        new UpdatePocketNameCommand("F1", "B1", "New")))
                .assertNext(saved -> {
                    Pocket updated = saved.getPocketes().stream()
                            .filter(b -> "B1".equals(b.getId())).findFirst().orElseThrow();
                    assertThat(updated.getName()).isEqualTo("New");
                    assertThat(saved.getPocketes()).hasSize(3);
                })
                .verifyComplete();
    }

    @Test
    void deletePocket_callsRepository() {
        when(pocketRepository.deleteById("B1")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.deletePocket("B1"))
                .verifyComplete();

        verify(pocketRepository).deleteById("B1");
    }
}

