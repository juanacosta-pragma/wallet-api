package co.com.bancolombia.usecase.wallet;

import co.com.bancolombia.model.pocket.Pocket;
import co.com.bancolombia.model.pocket.gateways.PocketRepository;
import co.com.bancolombia.model.wallet.Wallet;
import co.com.bancolombia.model.wallet.gateways.WalletRepository;
import co.com.bancolombia.model.transaction.Transaction;
import co.com.bancolombia.usecase.wallet.commands.PocketLocator;
import co.com.bancolombia.usecase.wallet.commands.CreateTransactionCommand;
import co.com.bancolombia.usecase.wallet.commands.TransactionLocator;
import co.com.bancolombia.usecase.wallet.commands.UpdateTransactionNameCommand;
import co.com.bancolombia.usecase.wallet.commands.UpdateTransactionAmountCommand;
import co.com.bancolombia.usecase.wallet.exceptions.PocketNotFoundException;
import co.com.bancolombia.usecase.wallet.exceptions.WalletNotFoundException;
import co.com.bancolombia.usecase.wallet.exceptions.TransactionNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionUseCaseTest {

    @Mock
    private PocketRepository pocketRepository;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private TransactionUseCase useCase;

    private Transaction p1;
    private Transaction p2;
    private Pocket pocketWithTransactions;
    private Pocket otherPocket;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        p1 = Transaction.builder().id("P1").description("Apple").amount(BigDecimal.valueOf(10L)).build();
        p2 = Transaction.builder().id("P2").description("Banana").amount(BigDecimal.valueOf(20L)).build();
        pocketWithTransactions = Pocket.builder().id("B1").name("Centro")
                .transactions(List.of(p1, p2)).build();
        otherPocket = Pocket.builder().id("B2").name("Norte").build();
        wallet = Wallet.builder().id("F1").userId("JJ")
                .pocketes(List.of(pocketWithTransactions, otherPocket)).build();
    }

    private void mockFindAndSave() {
        when(walletRepository.findById("F1")).thenReturn(Mono.just(wallet));
        when(walletRepository.save(any(Wallet.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));
    }

    // -------- getAllPocketes --------

    @Test
    void getAllPocketes_delegates() {
        when(pocketRepository.getAllPocketes()).thenReturn(Flux.just(pocketWithTransactions));

        StepVerifier.create(useCase.getAllPocketes())
                .expectNext(pocketWithTransactions)
                .verifyComplete();
    }

    // -------- create --------

    @Test
    void create_appendsTransactionWithGeneratedId() {
        mockFindAndSave();
        Transaction newTransaction = Transaction.builder().description("Cherry").amount(BigDecimal.valueOf(5L)).build();

        StepVerifier.create(useCase.create(new CreateTransactionCommand("F1", "B1", newTransaction)))
                .assertNext(saved -> {
                    Pocket updatedB1 = saved.getPocketes().stream()
                            .filter(b -> "B1".equals(b.getId())).findFirst().orElseThrow();
                    assertThat(updatedB1.getTransactions()).hasSize(3);
                    Transaction appended = updatedB1.getTransactions().get(2);
                    assertThat(appended.getId()).isNotNull();
                    assertThat(appended.getDescription()).isEqualTo("Cherry");
                })
                .verifyComplete();
    }

    @Test
    void create_pocketNotFound_emitsError() {
        when(walletRepository.findById("F1")).thenReturn(Mono.just(wallet));

        StepVerifier.create(useCase.create(new CreateTransactionCommand("F1", "MISSING",
                        Transaction.builder().description("X").amount(BigDecimal.valueOf(1L)).build())))
                .expectError(PocketNotFoundException.class)
                .verify();
    }

    @Test
    void create_walletNotFound_emitsError() {
        when(walletRepository.findById("F1")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.create(new CreateTransactionCommand("F1", "B1",
                        Transaction.builder().description("X").amount(BigDecimal.valueOf(1L)).build())))
                .expectError(WalletNotFoundException.class)
                .verify();
    }

    // -------- getTransactionByIdUseCase --------

    @Test
    void getTransactionByIdUseCase_existing_returns() {
        when(walletRepository.findById("F1")).thenReturn(Mono.just(wallet));

        StepVerifier.create(useCase.getTransactionByIdUseCase(new TransactionLocator("F1", "B1", "P1")))
                .expectNext(p1)
                .verifyComplete();
    }

    @Test
    void getTransactionByIdUseCase_transactionNotFound_emitsError() {
        when(walletRepository.findById("F1")).thenReturn(Mono.just(wallet));

        StepVerifier.create(useCase.getTransactionByIdUseCase(new TransactionLocator("F1", "B1", "MISSING")))
                .expectError(TransactionNotFoundException.class)
                .verify();
    }

    @Test
    void getTransactionByIdUseCase_pocketNotFound_emitsError() {
        when(walletRepository.findById("F1")).thenReturn(Mono.just(wallet));

        StepVerifier.create(useCase.getTransactionByIdUseCase(new TransactionLocator("F1", "MISSING", "P1")))
                .expectError(PocketNotFoundException.class)
                .verify();
    }

    @Test
    void getTransactionByIdUseCase_walletNotFound_emitsError() {
        when(walletRepository.findById("F1")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.getTransactionByIdUseCase(new TransactionLocator("F1", "B1", "P1")))
                .expectError(WalletNotFoundException.class)
                .verify();
    }

    // -------- deleteTransactionFromPocketUseCase --------

    @Test
    void deleteTransaction_existing_removesAndSaves() {
        mockFindAndSave();

        StepVerifier.create(useCase.deleteTransactionFromPocketUseCase(new TransactionLocator("F1", "B1", "P1")))
                .assertNext(saved -> {
                    Pocket updated = saved.getPocketes().stream()
                            .filter(b -> "B1".equals(b.getId())).findFirst().orElseThrow();
                    assertThat(updated.getTransactions()).hasSize(1);
                    assertThat(updated.getTransactions().get(0).getId()).isEqualTo("P2");
                })
                .verifyComplete();
    }

    @Test
    void deleteTransaction_transactionNotFound_emitsError() {
        when(walletRepository.findById("F1")).thenReturn(Mono.just(wallet));

        StepVerifier.create(useCase.deleteTransactionFromPocketUseCase(new TransactionLocator("F1", "B1", "MISSING")))
                .expectError(TransactionNotFoundException.class)
                .verify();
    }

    @Test
    void deleteTransaction_pocketNotFound_emitsError() {
        when(walletRepository.findById("F1")).thenReturn(Mono.just(wallet));

        StepVerifier.create(useCase.deleteTransactionFromPocketUseCase(new TransactionLocator("F1", "MISSING", "P1")))
                .expectError(PocketNotFoundException.class)
                .verify();
    }

    @Test
    void deleteTransaction_walletNotFound_emitsError() {
        when(walletRepository.findById("F1")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.deleteTransactionFromPocketUseCase(new TransactionLocator("F1", "B1", "P1")))
                .expectError(WalletNotFoundException.class)
                .verify();
    }

    // -------- updateTransactionAmountUseCase --------

    @Test
    void updateTransactionAmount_existing_updates() {
        mockFindAndSave();

        StepVerifier.create(useCase.updateTransactionAmountUseCase(
                        new UpdateTransactionAmountCommand("F1", "B1", "P1", BigDecimal.valueOf(99))))
                .assertNext(saved -> {
                    Transaction updated = saved.getPocketes().get(0).getTransactions().stream()
                            .filter(p -> "P1".equals(p.getId())).findFirst().orElseThrow();
                    assertThat(updated.getAmount()).isEqualTo(BigDecimal.valueOf(99));
                })
                .verifyComplete();
    }

    @Test
    void updateTransactionAmount_transactionNotFound_emitsError() {
        when(walletRepository.findById("F1")).thenReturn(Mono.just(wallet));

        StepVerifier.create(useCase.updateTransactionAmountUseCase(
                        new UpdateTransactionAmountCommand("F1", "B1", "MISSING", BigDecimal.valueOf(99))))
                .expectError(TransactionNotFoundException.class)
                .verify();
    }

    // -------- updateTransactionNameUseCase --------

    @Test
    void updateTransactionName_existing_updates() {
        mockFindAndSave();

        StepVerifier.create(useCase.updateTransactionNameUseCase(
                        new UpdateTransactionNameCommand("F1", "B1", "P1", "Renamed")))
                .assertNext(saved -> {
                    Transaction updated = saved.getPocketes().get(0).getTransactions().stream()
                            .filter(p -> "P1".equals(p.getId())).findFirst().orElseThrow();
                    assertThat(updated.getDescription()).isEqualTo("Renamed");
                })
                .verifyComplete();
    }

    @Test
    void updateTransactionName_transactionNotFound_emitsError() {
        when(walletRepository.findById("F1")).thenReturn(Mono.just(wallet));

        StepVerifier.create(useCase.updateTransactionNameUseCase(
                        new UpdateTransactionNameCommand("F1", "B1", "MISSING", "X")))
                .expectError(TransactionNotFoundException.class)
                .verify();
    }

    @Test
    void updateTransactionName_pocketNotFound_emitsError() {
        when(walletRepository.findById("F1")).thenReturn(Mono.just(wallet));

        StepVerifier.create(useCase.updateTransactionNameUseCase(
                        new UpdateTransactionNameCommand("F1", "MISSING", "P1", "X")))
                .expectError(PocketNotFoundException.class)
                .verify();
    }

    // -------- getHighestAmountTransactionByPocketUseCase --------

    @Test
    void getHighestAmountTransaction_returnsMax() {
        when(walletRepository.findById("F1")).thenReturn(Mono.just(wallet));

        StepVerifier.create(useCase.getHighestAmountTransactionByPocketUseCase(new PocketLocator("F1", "B1")))
                .expectNext(p2)
                .verifyComplete();
    }

    @Test
    void getHighestAmountTransaction_emptyPocket_emitsTransactionNotFound() {
        when(walletRepository.findById("F1")).thenReturn(Mono.just(wallet));

        StepVerifier.create(useCase.getHighestAmountTransactionByPocketUseCase(new PocketLocator("F1", "B2")))
                .expectError(TransactionNotFoundException.class)
                .verify();
    }

    @Test
    void getHighestAmountTransaction_pocketNotFound_emitsError() {
        when(walletRepository.findById("F1")).thenReturn(Mono.just(wallet));

        StepVerifier.create(useCase.getHighestAmountTransactionByPocketUseCase(new PocketLocator("F1", "MISSING")))
                .expectError(PocketNotFoundException.class)
                .verify();
    }

    @Test
    void getHighestAmountTransaction_walletNotFound_emitsError() {
        when(walletRepository.findById("F1")).thenReturn(Mono.empty());

        StepVerifier.create(useCase.getHighestAmountTransactionByPocketUseCase(new PocketLocator("F1", "B1")))
                .expectError(WalletNotFoundException.class)
                .verify();
    }

    @Test
    void create_capturesSavedWallet() {
        mockFindAndSave();
        useCase.create(new CreateTransactionCommand("F1", "B1",
                Transaction.builder().description("X").amount(BigDecimal.valueOf(1)).build())).block();

        ArgumentCaptor<Wallet> captor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletRepository).save(captor.capture());
        assertThat(captor.getValue().getPocketes().get(0).getTransactions()).hasSize(3);
    }
}

