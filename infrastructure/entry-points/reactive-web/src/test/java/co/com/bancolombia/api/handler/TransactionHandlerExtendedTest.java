package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.dto.TransactionRequest;
import co.com.bancolombia.api.dto.AmountUpdateRequest;
import co.com.bancolombia.api.mapper.TransactionMapper;
import co.com.bancolombia.api.mapper.WalletMapper;
import co.com.bancolombia.model.transaction.Transaction;
import co.com.bancolombia.usecase.wallet.TransactionUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class TransactionHandlerExtendedTest {

    @Mock
    private TransactionUseCase useCase;

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private WalletMapper walletMapper;

    @InjectMocks
    private TransactionHandler handler;

    private Transaction transaction;

    @BeforeEach
    void setUp() {
        transaction = Transaction.builder().id("t1").description("Apple").amount(BigDecimal.valueOf(10)).build();
    }

    @Test
    void transactionHandlerIsNotNull() {
        assertThat(handler).isNotNull();
    }

    @Test
    void transactionUseCaseIsInjected() {
        assertThat(useCase).isNotNull();
    }

    @Test
    void transactionMapperIsInjected() {
        assertThat(transactionMapper).isNotNull();
    }

    @Test
    void walletMapperIsInjected() {
        assertThat(walletMapper).isNotNull();
    }

    @Test
    void transactionCreation() {
        assertThat(transaction.getId()).isEqualTo("t1");
        assertThat(transaction.getDescription()).isEqualTo("Apple");
        assertThat(transaction.getAmount()).isEqualTo(BigDecimal.valueOf(10));
    }

    @Test
    void transactionRequestBuilding() {
        TransactionRequest req = TransactionRequest.builder()
                .description("Banana")
                .amount(BigDecimal.valueOf(20))
                .build();

        assertThat(req.getDescription()).isEqualTo("Banana");
        assertThat(req.getAmount()).isEqualTo(20L);
    }

    @Test
    void amountUpdateRequestBuilding() {
        AmountUpdateRequest req = AmountUpdateRequest.builder()
                .amount(BigDecimal.valueOf(50))
                .build();

        assertThat(req.getAmount()).isEqualTo(50L);
    }

    @Test
    void transactionWithNullName() {
        Transaction t = Transaction.builder().id("t1").description(null).amount(BigDecimal.valueOf(100)).build();
        assertThat(t.getDescription()).isNull();
    }

    @Test
    void transactionWithZeroAmount() {
        Transaction t = Transaction.builder().id("t1").description("Item").amount(BigDecimal.valueOf(0)).build();
        assertThat(t.getAmount()).isZero();
    }

    @Test
    void amountUpdateRequestWithNegativeAmount() {
        AmountUpdateRequest req = new AmountUpdateRequest(BigDecimal.valueOf(-10));
        assertThat(req.getAmount()).isNegative();
    }

    @Test
    void transactionRequestWithEmptyName() {
        TransactionRequest req = new TransactionRequest("", BigDecimal.valueOf(100));
        assertThat(req.getDescription()).isEmpty();
    }
}

