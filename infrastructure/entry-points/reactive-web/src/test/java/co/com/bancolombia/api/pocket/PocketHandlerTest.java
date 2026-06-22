package co.com.bancolombia.api.pocket;

import co.com.bancolombia.api.handler.PocketHandler;
import co.com.bancolombia.api.dto.PocketRequest;
import co.com.bancolombia.api.dto.PocketResponse;
import co.com.bancolombia.api.mapper.PocketMapper;
import co.com.bancolombia.api.dto.WalletResponse;
import co.com.bancolombia.api.mapper.WalletMapper;
import co.com.bancolombia.model.pocket.Pocket;
import co.com.bancolombia.model.wallet.Wallet;
import co.com.bancolombia.usecase.wallet.PocketUseCase;
import co.com.bancolombia.usecase.wallet.commands.PocketLocator;
import co.com.bancolombia.usecase.wallet.commands.CreatePocketCommand;
import co.com.bancolombia.usecase.wallet.commands.UpdatePocketNameCommand;
import co.com.bancolombia.usecase.wallet.exceptions.PocketNotFoundException;
import co.com.bancolombia.usecase.wallet.exceptions.ValidationException;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PocketHandlerTest {

    @Mock
    private PocketUseCase useCase;

    @Mock
    private PocketMapper pocketMapper;

    @Mock
    private WalletMapper walletMapper;

    @InjectMocks
    private PocketHandler handler;

    private Pocket pocket;
    private Wallet wallet;
    private WalletResponse walletResponse;

    @BeforeEach
    void setUp() {
        pocket = Pocket.builder().id("B1").name("Centro").build();
        wallet = Wallet.builder().id("F1").userId("JJ").build();
        walletResponse = WalletResponse.builder().id("F1").userId("JJ").build();
    }

    @Test
    void addPocket_validRequest_returnsCreated() {
        PocketRequest req = PocketRequest.builder().name("Centro").build();
        when(pocketMapper.toModel(any(PocketRequest.class))).thenReturn(pocket);
        when(useCase.createWallet(any(CreatePocketCommand.class))).thenReturn(Mono.just(wallet));
        when(walletMapper.toResponse(any(Wallet.class))).thenReturn(walletResponse);

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("walletId", "F1")
                .body(Mono.just(req));

        StepVerifier.create(handler.addPocket(request))
                .assertNext(r -> assertThat(r.statusCode()).isEqualTo(HttpStatus.CREATED))
                .verifyComplete();
    }

    @Test
    void addPocket_blankName_emitsValidationException() {
        PocketRequest req = PocketRequest.builder().name("").build();
        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("walletId", "F1")
                .body(Mono.just(req));

        StepVerifier.create(handler.addPocket(request))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void getPocket_existing_returnsOk() {
        when(useCase.getWalletById(eq(new PocketLocator("F1", "B1")))).thenReturn(Mono.just(pocket));
        when(pocketMapper.toResponse(pocket))
                .thenReturn(PocketResponse.builder().id("B1").name("Centro").build());

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("walletId", "F1")
                .pathVariable("pocketId", "B1")
                .build();

        StepVerifier.create(handler.getPocket(request))
                .assertNext(r -> assertThat(r.statusCode()).isEqualTo(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void getPocket_notFound_propagatesError() {
        when(useCase.getWalletById(eq(new PocketLocator("F1", "B1"))))
                .thenReturn(Mono.error(new PocketNotFoundException("missing")));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("walletId", "F1")
                .pathVariable("pocketId", "B1")
                .build();

        StepVerifier.create(handler.getPocket(request))
                .expectError(PocketNotFoundException.class)
                .verify();
    }

    @Test
    void updatePocketName_validRequest_returnsOk() {
        PocketRequest req = PocketRequest.builder().name("Renamed").build();
        when(useCase.updateWalletName(any(UpdatePocketNameCommand.class)))
                .thenReturn(Mono.just(wallet));
        when(walletMapper.toResponse(wallet)).thenReturn(walletResponse);

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("walletId", "F1")
                .pathVariable("pocketId", "B1")
                .body(Mono.just(req));

        StepVerifier.create(handler.updatePocketName(request))
                .assertNext(r -> assertThat(r.statusCode()).isEqualTo(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void updatePocketName_blankName_emitsValidationException() {
        PocketRequest req = PocketRequest.builder().name("   ").build();
        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("walletId", "F1")
                .pathVariable("pocketId", "B1")
                .body(Mono.just(req));

        StepVerifier.create(handler.updatePocketName(request))
                .expectError(ValidationException.class)
                .verify();
    }
}

