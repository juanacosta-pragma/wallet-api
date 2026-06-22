package co.com.bancolombia.api.wallet;

import co.com.bancolombia.api.dto.WalletRequest;
import co.com.bancolombia.api.handler.WalletHandler;
import co.com.bancolombia.api.mapper.WalletMapper;
import co.com.bancolombia.model.wallet.Wallet;
import co.com.bancolombia.usecase.wallet.WalletUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class WalletHandlerExtendedTest {

    @Mock
    private WalletUseCase useCase;

    @Mock
    private WalletMapper mapper;

    @InjectMocks
    private WalletHandler handler;

    private Wallet wallet;

    @BeforeEach
    void setUp() {
        wallet = Wallet.builder().id("w1").userId("user1").pocketes(new ArrayList<>()).build();
    }

    @Test
    void walletHandlerIsNotNull() {
        assertThat(handler).isNotNull();
    }

    @Test
    void walletUseCaseIsInjected() {
        assertThat(useCase).isNotNull();
    }

    @Test
    void walletMapperIsInjected() {
        assertThat(mapper).isNotNull();
    }

    @Test
    void walletCreation() {
        assertThat(wallet.getId()).isEqualTo("w1");
        assertThat(wallet.getUserId()).isEqualTo("user1");
        assertThat(wallet.getPocketes()).isEmpty();
    }

    @Test
    void walletRequestBuilding() {
        WalletRequest req = WalletRequest.builder()
                .userId("newUser")
                .build();

        assertThat(req.getUserId()).isEqualTo("newUser");
    }

    @Test
    void walletWithNullUserId() {
        Wallet w = Wallet.builder().id("w1").userId(null).pocketes(new ArrayList<>()).build();
        assertThat(w.getUserId()).isNull();
    }

    @Test
    void walletWithSpecialCharacters() {
        Wallet w = Wallet.builder()
                .id("wallet-@#$%")
                .userId("user@domain.com")
                .pocketes(new ArrayList<>())
                .build();

        assertThat(w.getId()).contains("@");
        assertThat(w.getUserId()).contains("@");
    }

    @Test
    void walletRequestWithEmptyUserId() {
        WalletRequest req = new WalletRequest("");
        assertThat(req.getUserId()).isEmpty();
    }

    @Test
    void walletEquivalence() {
        Wallet w1 = new Wallet("w1", "user1", new ArrayList<>());
        Wallet w2 = new Wallet("w1", "user1", new ArrayList<>());

        assertThat(w1.getId()).isEqualTo(w2.getId());
    }

    @Test
    void walletDifference() {
        Wallet w1 = new Wallet("w1", "user1", new ArrayList<>());
        Wallet w2 = new Wallet("w2", "user2", new ArrayList<>());

        assertThat(w1.getId()).isNotEqualTo(w2.getId());
    }

    @Test
    void walletBuilderWithToBuilder() {
        Wallet w1 = Wallet.builder().id("w1").userId("user1").build();
        Wallet w2 = w1.toBuilder().userId("user2").build();

        assertThat(w1.getUserId()).isEqualTo("user1");
        assertThat(w2.getUserId()).isEqualTo("user2");
        assertThat(w1.getId()).isEqualTo(w2.getId());
    }
}

