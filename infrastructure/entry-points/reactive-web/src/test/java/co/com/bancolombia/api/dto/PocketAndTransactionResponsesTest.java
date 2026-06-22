package co.com.bancolombia.api.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PocketResponseTest {

    @Test
    void testPocketResponseBuilderWithTransactions() {
        List<TransactionResponse> transactions = new ArrayList<>();
        TransactionResponse t1 = TransactionResponse.builder().id("t1").description("Item 1").amount(BigDecimal.valueOf(10)).build();
        transactions.add(t1);

        PocketResponse response = PocketResponse.builder()
                .id("p1")
                .name("Pocket 1")
                .transactions(transactions)
                .build();

        assertThat(response.getTransactions()).hasSize(1);
        assertThat(response.getId()).isEqualTo("p1");
    }

    @Test
    void testPocketResponseWithEmptyTransactions() {
        PocketResponse response = new PocketResponse("p1", "Pocket", new ArrayList<>());
        assertThat(response.getTransactions()).isEmpty();
    }

    @Test
    void testPocketResponseSettersGetters() {
        PocketResponse response = new PocketResponse();
        response.setId("p1");
        response.setName("Name");
        response.setTransactions(new ArrayList<>());

        assertThat(response.getId()).isEqualTo("p1");
        assertThat(response.getName()).isEqualTo("Name");
    }

    @Test
    void testPocketResponseEquality() {
        PocketResponse r1 = new PocketResponse("p1", "Name", new ArrayList<>());
        PocketResponse r2 = new PocketResponse("p1", "Name", new ArrayList<>());

        assertThat(r1.getId()).isEqualTo(r2.getId());
    }

    @Test
    void testTransactionResponseBuilder() {
        TransactionResponse response = TransactionResponse.builder()
                .id("t1")
                .description("Transaction")
                .amount(BigDecimal.valueOf(100))
                .build();

        assertThat(response.getId()).isEqualTo("t1");
        assertThat(response.getDescription()).isEqualTo("Transaction");
        assertThat(response.getAmount()).isEqualTo(BigDecimal.valueOf(100));
    }

    @Test
    void testTransactionResponseWithZeroAmount() {
        TransactionResponse response = new TransactionResponse("t1", "Item", BigDecimal.ZERO);
        assertThat(response.getAmount()).isZero();
    }

    @Test
    void testTransactionResponseWithNegativeAmount() {
        TransactionResponse response = new TransactionResponse("t1", "Item", BigDecimal.valueOf(-5));
        assertThat(response.getAmount()).isNegative();
    }

    @Test
    void testTransactionResponseEquality() {
        TransactionResponse r1 = new TransactionResponse("t1", "Item", BigDecimal.valueOf(10));
        TransactionResponse r2 = new TransactionResponse("t1", "Item", BigDecimal.valueOf(10));

        assertThat(r1.getId()).isEqualTo(r2.getId());
        assertThat(r1.getAmount()).isEqualTo(r2.getAmount());
    }
}

