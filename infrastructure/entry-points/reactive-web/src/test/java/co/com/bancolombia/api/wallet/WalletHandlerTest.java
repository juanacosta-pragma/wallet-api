package co.com.bancolombia.api.wallet;

import co.com.bancolombia.api.handler.WalletHandler;
import co.com.bancolombia.api.dto.WalletRequest;
import co.com.bancolombia.api.dto.WalletResponse;
import co.com.bancolombia.api.mapper.WalletMapper;
import co.com.bancolombia.model.wallet.Wallet;
import co.com.bancolombia.usecase.wallet.WalletUseCase;
import co.com.bancolombia.usecase.wallet.exceptions.WalletNotFoundException;
import co.com.bancolombia.usecase.wallet.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import org.springframework.web.reactive.function.server.EntityResponse;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletHandlerTest {

    @Mock
    private WalletUseCase useCase;

    @Mock
    private WalletMapper mapper;

    @InjectMocks
    private WalletHandler handler;

    private Wallet wallet;
    private WalletResponse response;

    @BeforeEach
    void setUp() {
        wallet = Wallet.builder().id("1").userId("Pizza").build();
        response = WalletResponse.builder().id("1").userId("Pizza").build();
    }

    @Test
    void createWallet_validRequest_returnsCreated() {
        WalletRequest req = WalletRequest.builder().userId("Pizza").build();
        when(mapper.toModel(any(WalletRequest.class))).thenReturn(wallet);
        when(useCase.createWallet(any())).thenReturn(Mono.just(wallet));
        when(mapper.toResponse(any())).thenReturn(response);

        MockServerRequest request = MockServerRequest.builder().body(Mono.just(req));

        StepVerifier.create(handler.createWallet(request))
                .assertNext(r -> {
                    assertThat(r.statusCode()).isEqualTo(HttpStatus.CREATED);
                    assertThat(((EntityResponse<?>) r).entity()).isEqualTo(response);
                })
                .verifyComplete();
    }

    @Test
    void createWallet_blankName_emitsValidationException() {
        WalletRequest req = WalletRequest.builder().userId("  ").build();
        MockServerRequest request = MockServerRequest.builder().body(Mono.just(req));

        StepVerifier.create(handler.createWallet(request))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void createWallet_nullName_emitsValidationException() {
        WalletRequest req = WalletRequest.builder().userId(null).build();
        MockServerRequest request = MockServerRequest.builder().body(Mono.just(req));

        StepVerifier.create(handler.createWallet(request))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    void getWalletById_existing_returnsOk() {
        when(useCase.getWalletById("1")).thenReturn(Mono.just(wallet));
        when(mapper.toResponse(wallet)).thenReturn(response);

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "1").build();

        StepVerifier.create(handler.getWalletById(request))
                .assertNext(r -> {
                    assertThat(r.statusCode()).isEqualTo(HttpStatus.OK);
                    assertThat(((EntityResponse<?>) r).entity()).isEqualTo(response);
                })
                .verifyComplete();
    }

    @Test
    void getWalletById_notFound_propagatesError() {
        when(useCase.getWalletById("X"))
                .thenReturn(Mono.error(new WalletNotFoundException("not found")));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "X").build();

        StepVerifier.create(handler.getWalletById(request))
                .expectError(WalletNotFoundException.class)
                .verify();
    }

    @Test
    void getAllWallets_returnsOkFlux() {
        when(useCase.getAllWallets()).thenReturn(Flux.just(wallet));

        MockServerRequest request = MockServerRequest.builder().build();

        StepVerifier.create(handler.getAllWallets(request))
                .assertNext(r -> assertThat(r.statusCode()).isEqualTo(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void updateWalletName_returnsOk() {
        WalletRequest req = WalletRequest.builder().userId("Renamed").build();
        when(useCase.updateWalletName(anyString(), anyString())).thenReturn(Mono.just(wallet));
        when(mapper.toResponse(wallet)).thenReturn(response);

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "1")
                .body(Mono.just(req));

        StepVerifier.create(handler.updateWalletName(request))
                .assertNext(r -> assertThat(r.statusCode()).isEqualTo(HttpStatus.OK))
                .verifyComplete();
    }

    @Test
    void deleteWallet_returnsNoContent() {
        when(useCase.deleteWallet("1")).thenReturn(Mono.empty());

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "1").build();

        StepVerifier.create(handler.deleteWallet(request))
                .assertNext(r -> assertThat(r.statusCode()).isEqualTo(HttpStatus.NO_CONTENT))
                .verifyComplete();
    }

    @Test
    void deleteWallet_useCaseError_propagates() {
        when(useCase.deleteWallet("1")).thenReturn(Mono.error(new RuntimeException("boom")));

        MockServerRequest request = MockServerRequest.builder()
                .pathVariable("id", "1").build();

        StepVerifier.create(handler.deleteWallet(request))
                .expectErrorMessage("boom")
                .verify();
    }

    @SuppressWarnings("unused")
    private ServerResponse cast(ServerResponse r) { return r; } // keep import used
}

