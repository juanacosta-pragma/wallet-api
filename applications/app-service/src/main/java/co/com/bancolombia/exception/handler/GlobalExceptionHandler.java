package co.com.bancolombia.exception.handler;

import co.com.bancolombia.mongo.exception.DatabaseException;
import co.com.bancolombia.exception.dto.ErrorResponse;
import co.com.bancolombia.usecase.wallet.exceptions.WalletNotFoundException;
import co.com.bancolombia.usecase.wallet.exceptions.PocketNotFoundException;
import co.com.bancolombia.usecase.wallet.exceptions.TransactionNotFoundException;
import co.com.bancolombia.usecase.wallet.exceptions.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

/**
 * Global exception handler for the entire application
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles wallet not found exceptions
     */
    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWalletNotFoundException(
            WalletNotFoundException ex,
            ServerWebExchange exchange) {

        log.warn("Wallet Not Found: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message("Wallet not found")
                .errorCode("WALLET_NOT_FOUND")
                .path(exchange.getRequest().getPath().value())
                .requestId(exchange.getRequest().getId())
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    /**
     * Handles pocket not found exceptions
     */
    @ExceptionHandler(PocketNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePocketNotFoundException(
            PocketNotFoundException ex,
            ServerWebExchange exchange) {

        log.warn("Pocket Not Found: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message("Pocket not found")
                .errorCode("POCKET_NOT_FOUND")
                .path(exchange.getRequest().getPath().value())
                .requestId(exchange.getRequest().getId())
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    /**
     * Handles transaction not found exceptions
     */
    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTransactionNotFoundException(
            TransactionNotFoundException ex,
            ServerWebExchange exchange) {

        log.warn("Transaction Not Found: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("Not Found")
                .message("Transaction not found")
                .errorCode("TRANSACTION_NOT_FOUND")
                .path(exchange.getRequest().getPath().value())
                .requestId(exchange.getRequest().getId())
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    /**
     * Handles validation errors raised in handlers (blank/null fields, invalid input).
     */
    @ExceptionHandler({ValidationException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponse> handleValidationException(
            RuntimeException ex,
            ServerWebExchange exchange) {

        log.warn("Validation error: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ex.getMessage())
                .errorCode("VALIDATION_ERROR")
                .path(exchange.getRequest().getPath().value())
                .requestId(exchange.getRequest().getId())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    /**
     * Handles database exceptions
     */
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseException(
            DatabaseException ex,
            ServerWebExchange exchange) {

        log.error("Database Error: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.SERVICE_UNAVAILABLE.value())
                .error("Service Unavailable")
                .message("Error conectado a base de datos")
                .errorCode(ex.getErrorCode())
                .path(exchange.getRequest().getPath().value())
                .requestId(exchange.getRequest().getId())
                .build();

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(errorResponse);
    }

    /**
     * Handles generic exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            ServerWebExchange exchange) {

        log.error("Unexpected Error: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("Ocurrió un error inesperado procesando la solicitud")
                .errorCode("INTERNAL_ERROR")
                .path(exchange.getRequest().getPath().value())
                .requestId(exchange.getRequest().getId())
                .build();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }
}
