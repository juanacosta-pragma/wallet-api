package co.com.bancolombia.api.transaction;

import co.com.bancolombia.api.handler.TransactionHandler;
import co.com.bancolombia.api.dto.WalletResponse;
import co.com.bancolombia.api.mapper.WalletMapper;
import co.com.bancolombia.api.dto.TransactionRequest;
import co.com.bancolombia.api.dto.TransactionResponse;
import co.com.bancolombia.api.dto.AmountUpdateRequest;
import co.com.bancolombia.api.mapper.TransactionMapper;
import co.com.bancolombia.model.wallet.Wallet;
import co.com.bancolombia.model.transaction.Transaction;
import co.com.bancolombia.usecase.wallet.TransactionUseCase;
import co.com.bancolombia.usecase.wallet.commands.PocketLocator;
import co.com.bancolombia.usecase.wallet.commands.CreateTransactionCommand;
import co.com.bancolombia.usecase.wallet.commands.TransactionLocator;
import co.com.bancolombia.usecase.wallet.commands.UpdateTransactionNameCommand;
import co.com.bancolombia.usecase.wallet.commands.UpdateTransactionAmountCommand;
import co.com.bancolombia.usecase.wallet.exceptions.TransactionNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionHandlerTest {

    @Mock
    private TransactionUseCase useCase;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private WalletMapper walletMapper;

    @InjectMocks
    private TransactionHandler handler;

    private Transaction transaction;
    private TransactionResponse transactionResponse;
    private Wallet wallet;
    private WalletResponse walletResponse;

    @BeforeEach
    void setUp() {
        transaction = Transaction.builder().id("P1").description("Apple").amount(BigDecimal.valueOf(10)).build();
        transactionResponse = TransactionResponse.builder().id("P1").description("Apple").amount(BigDecimal.valueOf(10)).build();
        wallet = Wallet.builder().id("F1").userId("JJ").build();
        walletResponse = WalletResponse.builder().id("F1").userId("JJ").build();
    }

    @Test
    void addTransaction_validRequest_returnsCreated() {
        TransactionRequest req = TransactionRequest.builder().description("Apple").amount(BigDecimal.valueOf(10)).build();
        when(transactionMapper.toModel(any(TransactionRequest.class))).thenReturn(transaction);
        when(useCase.create(any(CreateTransactionCommand.class)))
                .thenReturn(Mono.just(wallet));
        when(walletMapper.toResponse(wallet)).thenReturn(walletResponse);

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("walletId", "F1")
                .pathVariable("pocketId", "B1")
                .body(Mono.just(req));

        StepVerifier.create(handler.addTransaction(request))
                .assertNext(r -> assertThat(r.statusCode()).isEqualTo(HttpStatus.CREATED))
                .verifyComplete();
    }

    @Test
    void addTransaction_blankName_emitsIllegalArgument() {
        TransactionRequest req = TransactionRequest.builder().description("  ").amount(BigDecimal.valueOf(1)).build();
        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("walletId", "F1")
                .pathVariable("pocketId", "B1")
                .body(Mono.just(req));

        StepVerifier.create(handler.addTransaction(request))
                .expectError(IllegalArgumentException.class)
                .verify();
    }

    @Test
    void getTransaction_existing_returnsOk() {
        when(useCase.getTransactionByIdUseCase(eq(new TransactionLocator("F1", "B1", "P1"))))
                .thenReturn(Mono.just(transaction));
        when(transactionMapper.toResponse(transaction)).thenReturn(transactionResponse);

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("walletId", "F1")
                .pathVariable("pocketId", "B1")
                .pathVariable("transactionId", "P1")
                .build();

        StepVerifier.create(handler.getTransaction(request))
                .assertNext(r -> assertThat(r.statusCode()).isEqualTo(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void getTransaction_notFound_propagatesError() {
        when(useCase.getTransactionByIdUseCase(eq(new TransactionLocator("F1", "B1", "P1"))))
                .thenReturn(Mono.error(new TransactionNotFoundException("missing")));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("walletId", "F1")
                .pathVariable("pocketId", "B1")
                .pathVariable("transactionId", "P1")
                .build();

        StepVerifier.create(handler.getTransaction(request))
                .expectError(TransactionNotFoundException.class)
                .verify();
    }

    @Test
    void deleteTransaction_returnsOk() {
        when(useCase.deleteTransactionFromPocketUseCase(any(TransactionLocator.class)))
                .thenReturn(Mono.just(wallet));
        when(walletMapper.toResponse(wallet)).thenReturn(walletResponse);

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("walletId", "F1")
                .pathVariable("pocketId", "B1")
                .pathVariable("transactionId", "P1")
                .build();

        StepVerifier.create(handler.deleteTransaction(request))
                .assertNext(r -> assertThat(r.statusCode()).isEqualTo(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void updateTransactionAmount_returnsOk() {
        AmountUpdateRequest amountReq = AmountUpdateRequest.builder().amount(BigDecimal.valueOf(99)).build();
        when(useCase.updateTransactionAmountUseCase(any(UpdateTransactionAmountCommand.class)))
                .thenReturn(Mono.just(wallet));
        when(walletMapper.toResponse(wallet)).thenReturn(walletResponse);

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("walletId", "F1")
                .pathVariable("pocketId", "B1")
                .pathVariable("transactionId", "P1")
                .body(Mono.just(amountReq));

        StepVerifier.create(handler.updateTransactionAmount(request))
                .assertNext(r -> assertThat(r.statusCode()).isEqualTo(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void updateTransactionName_returnsOk() {
        TransactionRequest nameReq = TransactionRequest.builder().description("New").build();
        when(useCase.updateTransactionNameUseCase(any(UpdateTransactionNameCommand.class)))
                .thenReturn(Mono.just(wallet));
        when(walletMapper.toResponse(wallet)).thenReturn(walletResponse);

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("walletId", "F1")
                .pathVariable("pocketId", "B1")
                .pathVariable("transactionId", "P1")
                .body(Mono.just(nameReq));

        StepVerifier.create(handler.updateTransactionName(request))
                .assertNext(r -> assertThat(r.statusCode()).isEqualTo(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void getHighestAmountTransaction_returnsOk() {
        when(useCase.getHighestAmountTransactionByPocketUseCase(eq(new PocketLocator("F1", "B1"))))
                .thenReturn(Mono.just(transaction));
        when(transactionMapper.toResponse(transaction)).thenReturn(transactionResponse);

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("walletId", "F1")
                .pathVariable("pocketId", "B1")
                .build();

        StepVerifier.create(handler.getHighestAmountTransactionByPocket(request))
                .assertNext(r -> assertThat(r.statusCode()).isEqualTo(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void getHighestAmountTransaction_noTransactions_propagatesError() {
        when(useCase.getHighestAmountTransactionByPocketUseCase(eq(new PocketLocator("F1", "B1"))))
                .thenReturn(Mono.error(new TransactionNotFoundException("none")));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("walletId", "F1")
                .pathVariable("pocketId", "B1")
                .build();

        StepVerifier.create(handler.getHighestAmountTransactionByPocket(request))
                .expectError(TransactionNotFoundException.class)
                .verify();
    }
}

