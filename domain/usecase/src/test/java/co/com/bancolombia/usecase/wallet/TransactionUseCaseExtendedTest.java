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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
class TransactionUseCaseExtendedTest {

    @Mock
    private PocketRepository pocketRepository;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private TransactionUseCase useCase;

    private Transaction t1, t2, t3;
    private Pocket pocketWithTransactions;
    private Pocket otherPocket;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        t1 = Transaction.builder().id("P1").description("Apple").amount(BigDecimal.TEN).build();
        t2 = Transaction.builder().id("P2").description("Banana").amount(BigDecimal.valueOf(20)).build();
        t3 = Transaction.builder().id("P3").description("Cherry").amount(BigDecimal.valueOf(5)).build();
        pocketWithTransactions = Pocket.builder().id("B1").name("Centro")
                .transactions(List.of(t1, t2, t3)).build();
        otherPocket = Pocket.builder().id("B2").name("Norte").build();
        wallet = Wallet.builder().id("F1").userId("JJ")
                .pocketes(List.of(pocketWithTransactions, otherPocket)).build();
    }

    @Test
    void getAllPocketes_returnsMultiple() {
        when(pocketRepository.getAllPocketes()).thenReturn(Flux.just(pocketWithTransactions, otherPocket));

        StepVerifier.create(useCase.getAllPocketes())
                .expectNext(pocketWithTransactions, otherPocket)
                .verifyComplete();
    }

    @Test
    void create_appendsAndSaves() {
        when(walletRepository.findById("F1")).thenReturn(Mono.just(wallet));
        when(walletRepository.save(any(Wallet.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        Transaction newTransaction = Transaction.builder().description("Date").amount(BigDecimal.valueOf(15)).build();

        StepVerifier.create(useCase.create(new CreateTransactionCommand("F1", "B1", newTransaction)))
                .assertNext(saved -> {
                    Pocket updated = saved.getPocketes().get(0);
                    assertThat(updated.getTransactions()).hasSize(4);
                })
                .verifyComplete();
    }

    @Test
    void updateTransactionAmount_updatesCorrectly() {
        when(walletRepository.findById("F1")).thenReturn(Mono.just(wallet));
        when(walletRepository.save(any(Wallet.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(useCase.updateTransactionAmountUseCase(
                        new UpdateTransactionAmountCommand("F1", "B1", "P1", BigDecimal.valueOf(50))))
                .assertNext(saved -> {
                    Transaction updated = saved.getPocketes().get(0).getTransactions().get(0);
                    assertThat(updated.getAmount()).isEqualTo(BigDecimal.valueOf(50));
                })
                .verifyComplete();
    }

    @Test
    void updateTransactionName_changesName() {
        when(walletRepository.findById("F1")).thenReturn(Mono.just(wallet));
        when(walletRepository.save(any(Wallet.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(useCase.updateTransactionNameUseCase(
                        new UpdateTransactionNameCommand("F1", "B1", "P2", "Orange")))
                .assertNext(saved -> {
                    Transaction updated = saved.getPocketes().get(0).getTransactions().get(1);
                    assertThat(updated.getDescription()).isEqualTo("Orange");
                })
                .verifyComplete();
    }

    @Test
    void getHighestAmountTransaction_withThreeTransactions() {
        when(walletRepository.findById("F1")).thenReturn(Mono.just(wallet));

        StepVerifier.create(useCase.getHighestAmountTransactionByPocketUseCase(new PocketLocator("F1", "B1")))
                .expectNext(t2)
                .verifyComplete();
    }

    @Test
    void deleteTransaction_removesFromList() {
        when(walletRepository.findById("F1")).thenReturn(Mono.just(wallet));
        when(walletRepository.save(any(Wallet.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(useCase.deleteTransactionFromPocketUseCase(new TransactionLocator("F1", "B1", "P2")))
                .assertNext(saved -> {
                    Pocket updated = saved.getPocketes().get(0);
                    assertThat(updated.getTransactions()).hasSize(2);
                    assertThat(updated.getTransactions().get(0).getId()).isEqualTo("P1");
                    assertThat(updated.getTransactions().get(1).getId()).isEqualTo("P3");
                })
                .verifyComplete();
    }

    @Test
    void getHighestAmountWithSingleTransaction() {
        Pocket singlePocket = Pocket.builder().id("B3").name("Single")
                .transactions(List.of(t1))
                .build();
        Wallet walletSingle = Wallet.builder().id("F2").userId("User")
                .pocketes(List.of(singlePocket))
                .build();
        when(walletRepository.findById("F2")).thenReturn(Mono.just(walletSingle));

        StepVerifier.create(useCase.getHighestAmountTransactionByPocketUseCase(new PocketLocator("F2", "B3")))
                .expectNext(t1)
                .verifyComplete();
    }
}

