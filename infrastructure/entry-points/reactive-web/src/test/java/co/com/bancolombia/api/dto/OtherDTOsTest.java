package co.com.bancolombia.api.dto;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PocketRequestTest {

    @Test
    void testPocketRequestCreation() {
        PocketRequest request = new PocketRequest("Savings");

        assertThat(request).isNotNull();
        assertThat(request.getName()).isEqualTo("Savings");
    }

    @Test
    void testPocketRequestBuilder() {
        PocketRequest request = PocketRequest.builder()
                .name("Investment Pocket")
                .build();

        assertThat(request.getName()).isEqualTo("Investment Pocket");
    }

    @Test
    void testPocketRequestNoArgsConstructor() {
        PocketRequest request = new PocketRequest();

        assertThat(request).isNotNull();
        assertThat(request.getName()).isNull();
    }

    @Test
    void testPocketRequestSettersAndGetters() {
        PocketRequest request = new PocketRequest();
        request.setName("NewPocket");

        assertThat(request.getName()).isEqualTo("NewPocket");
    }

    @Test
    void testPocketRequestWithEmptyString() {
        PocketRequest request = new PocketRequest("");

        assertThat(request.getName()).isEmpty();
    }

    @Test
    void testPocketRequestWithNull() {
        PocketRequest request = new PocketRequest(null);

        assertThat(request.getName()).isNull();
    }

    @Test
    void testPocketRequestEquality() {
        PocketRequest request1 = new PocketRequest("Pocket1");
        PocketRequest request2 = new PocketRequest("Pocket1");

        assertThat(request1).isEqualTo(request2);
    }

    @Test
    void testPocketRequestInequality() {
        PocketRequest request1 = new PocketRequest("Pocket1");
        PocketRequest request2 = new PocketRequest("Pocket2");

        assertThat(request1).isNotEqualTo(request2);
    }
}


