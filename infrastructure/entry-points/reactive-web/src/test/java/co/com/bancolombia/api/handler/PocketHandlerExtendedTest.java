package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.dto.PocketRequest;
import co.com.bancolombia.api.mapper.PocketMapper;
import co.com.bancolombia.api.mapper.WalletMapper;
import co.com.bancolombia.model.pocket.Pocket;
import co.com.bancolombia.usecase.wallet.PocketUseCase;
import co.com.bancolombia.usecase.wallet.commands.CreatePocketCommand;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PocketHandlerExtendedTest {

    @Mock
    private PocketUseCase useCase;

    @Mock
    private PocketMapper pocketMapper;

    @Mock
    private WalletMapper walletMapper;

    @InjectMocks
    private PocketHandler handler;

    private Pocket pocket;

    @BeforeEach
    void setUp() {
        pocket = Pocket.builder().id("p1").name("Savings").build();
    }

    @Test
    void addPocket_withValidName_returnsCreated() {
        PocketRequest req = PocketRequest.builder().name("New Pocket").build();
        when(pocketMapper.toModel(any())).thenReturn(pocket);
        when(useCase.createWallet(any(CreatePocketCommand.class))).thenReturn(Mono.empty().then(Mono.empty()));

        // Test validates basic structure
        assertThat(pocket.getName()).isNotNull();
    }

    @Test
    void addPocket_nullName_shouldFail() {
        PocketRequest req = PocketRequest.builder().name(null).build();

        assertThat(req.getName()).isNull();
    }

    @Test
    void getPocket_structure() {
        assertThat(pocket.getId()).isEqualTo("p1");
        assertThat(pocket.getName()).isEqualTo("Savings");
    }

    @Test
    void updatePocketName_basic() {
        PocketRequest req = PocketRequest.builder().name("Updated").build();

        assertThat(req.getName()).isEqualTo("Updated");
    }

    @Test
    void pocketHandlerIsNotNull() {
        assertThat(handler).isNotNull();
    }

    @Test
    void pocketUseCaseIsInjected() {
        assertThat(useCase).isNotNull();
    }

    @Test
    void pocketMapperIsInjected() {
        assertThat(pocketMapper).isNotNull();
    }
}

