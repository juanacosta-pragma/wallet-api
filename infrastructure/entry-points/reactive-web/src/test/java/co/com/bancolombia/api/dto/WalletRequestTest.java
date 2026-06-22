package co.com.bancolombia.api.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WalletRequestTest {

    @Test
    void testWalletRequestCreation() {
        WalletRequest request = new WalletRequest("user123");

        assertThat(request).isNotNull();
        assertThat(request.getUserId()).isEqualTo("user123");
    }

    @Test
    void testWalletRequestBuilder() {
        WalletRequest request = WalletRequest.builder()
                .userId("john_doe")
                .build();

        assertThat(request.getUserId()).isEqualTo("john_doe");
    }

    @Test
    void testWalletRequestNoArgsConstructor() {
        WalletRequest request = new WalletRequest();

        assertThat(request).isNotNull();
        assertThat(request.getUserId()).isNull();
    }

    @Test
    void testWalletRequestSettersAndGetters() {
        WalletRequest request = new WalletRequest();
        request.setUserId("newUser");

        assertThat(request.getUserId()).isEqualTo("newUser");
    }

    @Test
    void testWalletRequestWithEmptyString() {
        WalletRequest request = new WalletRequest("");

        assertThat(request.getUserId()).isEmpty();
    }

    @Test
    void testWalletRequestWithNull() {
        WalletRequest request = new WalletRequest(null);

        assertThat(request.getUserId()).isNull();
    }

    @Test
    void testWalletRequestEquality() {
        WalletRequest request1 = new WalletRequest("user123");
        WalletRequest request2 = new WalletRequest("user123");

        assertThat(request1).isEqualTo(request2);
    }

    @Test
    void testWalletRequestInequality() {
        WalletRequest request1 = new WalletRequest("user123");
        WalletRequest request2 = new WalletRequest("user456");

        assertThat(request1).isNotEqualTo(request2);
    }

    @Test
    void testWalletRequestHashCode() {
        WalletRequest request1 = new WalletRequest("user123");
        WalletRequest request2 = new WalletRequest("user123");

        assertThat(request1.hashCode()).isEqualTo(request2.hashCode());
    }

    @Test
    void testWalletRequestToString() {
        WalletRequest request = new WalletRequest("user123");

        assertThat(request.toString()).contains("user123");
    }
}

