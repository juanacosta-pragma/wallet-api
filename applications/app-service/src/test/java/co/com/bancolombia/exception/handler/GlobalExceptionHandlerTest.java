package co.com.bancolombia.exception.handler;

import co.com.bancolombia.exception.dto.ErrorResponse;
import co.com.bancolombia.mongo.exception.DatabaseException;
import co.com.bancolombia.usecase.wallet.exceptions.PocketNotFoundException;
import co.com.bancolombia.usecase.wallet.exceptions.WalletNotFoundException;
import co.com.bancolombia.usecase.wallet.exceptions.TransactionNotFoundException;
import co.com.bancolombia.usecase.wallet.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;
    private ServerWebExchange exchange;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        MockServerHttpRequest req = MockServerHttpRequest
                .post("/api/v1/wallets/create")
                .build();
        exchange = MockServerWebExchange.from(req);
    }

    @Test
    void validationException_returnsBadRequestWithRealMessage() {
        ResponseEntity<ErrorResponse> resp = handler.handleValidationException(
                new ValidationException("Wallet name is required"), exchange);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse body = resp.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(400);
        assertThat(body.getError()).isEqualTo("Bad Request");
        assertThat(body.getMessage()).isEqualTo("Wallet name is required");
        assertThat(body.getErrorCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(body.getPath()).isEqualTo("/api/v1/wallets/create");
        assertThat(body.getRequestId()).isNotNull();
    }

    @Test
    void illegalArgumentException_alsoMapsToBadRequest() {
        ResponseEntity<ErrorResponse> resp = handler.handleValidationException(
                new IllegalArgumentException("Transaction name is required"), exchange);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().getMessage()).isEqualTo("Transaction name is required");
        assertThat(resp.getBody().getErrorCode()).isEqualTo("VALIDATION_ERROR");
    }

    @Test
    void walletNotFoundException_returns404() {
        ResponseEntity<ErrorResponse> resp = handler.handleWalletNotFoundException(
                new WalletNotFoundException("not found"), exchange);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resp.getBody().getErrorCode()).isEqualTo("WALLET_NOT_FOUND");
    }

    @Test
    void pocketNotFoundException_returns404() {
        ResponseEntity<ErrorResponse> resp = handler.handlePocketNotFoundException(
                new PocketNotFoundException("missing"), exchange);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resp.getBody().getErrorCode()).isEqualTo("POCKET_NOT_FOUND");
    }

    @Test
    void transactionNotFoundException_returns404() {
        ResponseEntity<ErrorResponse> resp = handler.handleTransactionNotFoundException(
                new TransactionNotFoundException("missing"), exchange);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resp.getBody().getErrorCode()).isEqualTo("TRANSACTION_NOT_FOUND");
    }

    @Test
    void databaseException_returns503() {
        ResponseEntity<ErrorResponse> resp = handler.handleDatabaseException(
                new DatabaseException("conn failed"), exchange);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(resp.getBody().getErrorCode()).isEqualTo("DATABASE_ERROR");
    }

    @Test
    void genericException_returns500WithGenericMessage() {
        ResponseEntity<ErrorResponse> resp = handler.handleGlobalException(
                new RuntimeException("boom"), exchange);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(resp.getBody().getErrorCode()).isEqualTo("INTERNAL_ERROR");
        // The generic 500 hides the real exception message from the client
        assertThat(resp.getBody().getMessage()).contains("inesperado");
    }
}

