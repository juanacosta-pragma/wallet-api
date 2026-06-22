package co.com.bancolombia.api;

import co.com.bancolombia.api.handler.PocketHandler;
import co.com.bancolombia.api.handler.TransactionHandler;
import co.com.bancolombia.api.handler.WalletHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class RouterRestTest {

    @Mock
    private WalletHandler walletHandler;

    @Mock
    private PocketHandler pocketHandler;

    @Mock
    private TransactionHandler transactionHandler;

    private final RouterRest router = new RouterRest();

    @Test
    void routerWalletFunction_isNotNull() {
        RouterFunction<ServerResponse> rf = router.routerWalletFunction(walletHandler);
        assertThat(rf).isNotNull();
    }

    @Test
    void routerPocketFunction_isNotNull() {
        RouterFunction<ServerResponse> rf = router.routerPocketFunction(pocketHandler);
        assertThat(rf).isNotNull();
    }

    @Test
    void routerTransactionFunction_isNotNull() {
        RouterFunction<ServerResponse> rf = router.routerTransactionFunction(transactionHandler);
        assertThat(rf).isNotNull();
    }
}

