package co.com.bancolombia.api.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class AmountUpdateRequestTest {

    @Test
    void testAmountUpdateRequestCreation() {
        AmountUpdateRequest request = new AmountUpdateRequest( BigDecimal.valueOf(100));

        assertThat(request).isNotNull();
        assertThat(request.getAmount()).isEqualTo(BigDecimal.valueOf(100));
    }

    @Test
    void testAmountUpdateRequestBuilder() {
        AmountUpdateRequest request = AmountUpdateRequest.builder()
                .amount(BigDecimal.valueOf(250))
                .build();

        assertThat(request.getAmount()).isEqualTo(BigDecimal.valueOf(250));
    }

    @Test
    void testAmountUpdateRequestNoArgsConstructor() {
        AmountUpdateRequest request = new AmountUpdateRequest();

        assertThat(request).isNotNull();
        assertThat(request.getAmount()).isNull();
    }

    @Test
    void testAmountUpdateRequestWithZeroAmount() {
        AmountUpdateRequest request = new AmountUpdateRequest(BigDecimal.ZERO);

        assertThat(request.getAmount()).isZero();
    }

    @Test
    void testAmountUpdateRequestWithNegativeAmount() {
        AmountUpdateRequest request = new AmountUpdateRequest(BigDecimal.valueOf(-50));

        assertThat(request.getAmount()).isNegative();
    }

    @Test
    void testAmountUpdateRequestWithNullAmount() {
        AmountUpdateRequest request = new AmountUpdateRequest(null);

        assertThat(request.getAmount()).isNull();
    }

    @Test
    void testAmountUpdateRequestEquality() {
        AmountUpdateRequest request1 = new AmountUpdateRequest(BigDecimal.valueOf(100));
        AmountUpdateRequest request2 = new AmountUpdateRequest(BigDecimal.valueOf(100));

        assertThat(request1).isEqualTo(request2);
    }

    @Test
    void testAmountUpdateRequestSettersAndGetters() {
        AmountUpdateRequest request = new AmountUpdateRequest();
        request.setAmount(BigDecimal.valueOf(500));

        assertThat(request.getAmount()).isEqualTo(BigDecimal.valueOf(500));
    }
}

class TransactionResponseTest {

    @Test
    void testTransactionResponseCreation() {
        TransactionResponse response = new TransactionResponse("t1", "Laptop", BigDecimal.valueOf(100));

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("t1");
        assertThat(response.getDescription()).isEqualTo("Laptop");
        assertThat(response.getAmount()).isEqualTo(BigDecimal.valueOf(100));
    }

    @Test
    void testTransactionResponseBuilder() {
        TransactionResponse response = TransactionResponse.builder()
                .id("t2")
                .description("Phone")
                .amount(BigDecimal.valueOf(50))
                .build();

        assertThat(response.getId()).isEqualTo("t2");
        assertThat(response.getDescription()).isEqualTo("Phone");
        assertThat(response.getAmount()).isEqualTo(50L);
    }

    @Test
    void testTransactionResponseNoArgsConstructor() {
        TransactionResponse response = new TransactionResponse();

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNull();
        assertThat(response.getDescription()).isNull();
        assertThat(response.getAmount()).isNull();
    }

    @Test
    void testTransactionResponseWithNullAmount() {
        TransactionResponse response = new TransactionResponse("t1", "Item", null);

        assertThat(response.getAmount()).isNull();
    }

    @Test
    void testTransactionResponseEquality() {
        TransactionResponse response1 = new TransactionResponse("t1", "Item", BigDecimal.valueOf(100));
        TransactionResponse response2 = new TransactionResponse("t1", "Item", BigDecimal.valueOf(100));

        assertThat(response1).isEqualTo(response2);
    }

    @Test
    void testTransactionResponseSettersAndGetters() {
        TransactionResponse response = new TransactionResponse();
        response.setId("id1");
        response.setDescription("Updated Name");
        response.setAmount(BigDecimal.valueOf(999));

        assertThat(response.getId()).isEqualTo("id1");
        assertThat(response.getDescription()).isEqualTo("Updated Name");
        assertThat(response.getAmount()).isEqualTo(BigDecimal.valueOf(999));
    }
}

