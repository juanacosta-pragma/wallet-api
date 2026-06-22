package co.com.bancolombia.api.dto;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WalletResponseTest {

    @Test
    void testWalletResponseCreation() {
        WalletResponse response = new WalletResponse("w1", "user1", new ArrayList<>());

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo("w1");
        assertThat(response.getUserId()).isEqualTo("user1");
        assertThat(response.getPocketes()).isEmpty();
    }

    @Test
    void testWalletResponseBuilder() {
        List<PocketResponse> pockets = new ArrayList<>();
        WalletResponse response = WalletResponse.builder()
                .id("w1")
                .userId("user1")
                .pocketes(pockets)
                .build();

        assertThat(response.getId()).isEqualTo("w1");
        assertThat(response.getUserId()).isEqualTo("user1");
        assertThat(response.getPocketes()).isEqualTo(pockets);
    }

    @Test
    void testWalletResponseNoArgsConstructor() {
        WalletResponse response = new WalletResponse();

        assertThat(response).isNotNull();
        assertThat(response.getId()).isNull();
        assertThat(response.getUserId()).isNull();
        assertThat(response.getPocketes()).isNull();
    }

    @Test
    void testWalletResponseSettersAndGetters() {
        WalletResponse response = new WalletResponse();
        response.setId("new_id");
        response.setUserId("new_user");
        response.setPocketes(new ArrayList<>());

        assertThat(response.getId()).isEqualTo("new_id");
        assertThat(response.getUserId()).isEqualTo("new_user");
        assertThat(response.getPocketes()).isEmpty();
    }

    @Test
    void testWalletResponseWithNullId() {
        WalletResponse response = new WalletResponse(null, "user", new ArrayList<>());

        assertThat(response.getId()).isNull();
    }
}

