package co.com.bancolombia.exception.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseTest {

    @Test
    void testErrorResponseCreation() {
        ErrorResponse response = new ErrorResponse();
        response.setStatus(400);
        response.setError("Bad Request");
        response.setMessage("Invalid input");
        response.setErrorCode("VALIDATION_ERROR");

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getError()).isEqualTo("Bad Request");
        assertThat(response.getMessage()).isEqualTo("Invalid input");
        assertThat(response.getErrorCode()).isEqualTo("VALIDATION_ERROR");
    }

    @Test
    void testErrorResponseBuilder() {
        ErrorResponse response = ErrorResponse.builder()
                .status(404)
                .error("Not Found")
                .message("Resource not found")
                .errorCode("NOT_FOUND")
                .path("/api/wallets/1")
                .requestId("req-123")
                .build();

        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getError()).isEqualTo("Not Found");
        assertThat(response.getMessage()).isEqualTo("Resource not found");
        assertThat(response.getErrorCode()).isEqualTo("NOT_FOUND");
        assertThat(response.getPath()).isEqualTo("/api/wallets/1");
        assertThat(response.getRequestId()).isEqualTo("req-123");
    }

    @Test
    void testErrorResponseOfSimpleFactory() {
        ErrorResponse response = ErrorResponse.of(400, "Validation error", "VALIDATION_ERROR");

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getMessage()).isEqualTo("Validation error");
        assertThat(response.getErrorCode()).isEqualTo("VALIDATION_ERROR");
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    void testErrorResponseOfCompleteFactory() {
        ErrorResponse response = ErrorResponse.of(500, "Internal Server Error", "Server error occurred", "INTERNAL_ERROR", "/api/test", "req-456");

        assertThat(response.getStatus()).isEqualTo(500);
        assertThat(response.getError()).isEqualTo("Internal Server Error");
        assertThat(response.getMessage()).isEqualTo("Server error occurred");
        assertThat(response.getErrorCode()).isEqualTo("INTERNAL_ERROR");
        assertThat(response.getPath()).isEqualTo("/api/test");
        assertThat(response.getRequestId()).isEqualTo("req-456");
        assertThat(response.getTimestamp()).isNotNull();
    }

    @Test
    void testErrorResponseWithNullValues() {
        ErrorResponse response = new ErrorResponse(
                null,  // timestamp
                400,
                null,  // error
                null,  // message
                null,  // errorCode
                null,  // path
                null   // requestId
        );

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getError()).isNull();
        assertThat(response.getMessage()).isNull();
    }

    @Test
    void testErrorResponseNoArgsConstructor() {
        ErrorResponse response = new ErrorResponse();

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(0);
        assertThat(response.getError()).isNull();
    }

    @Test
    void testErrorResponseEquality() {
        ErrorResponse response1 = ErrorResponse.of(400, "Bad Request", "VALIDATION_ERROR");
        ErrorResponse response2 = ErrorResponse.of(400, "Bad Request", "VALIDATION_ERROR");

        // They may not be equal due to different timestamps, but status and error code should match
        assertThat(response1.getStatus()).isEqualTo(response2.getStatus());
        assertThat(response1.getErrorCode()).isEqualTo(response2.getErrorCode());
    }

    @Test
    void testErrorResponseForDifferentStatuses() {
        ErrorResponse response400 = ErrorResponse.of(400, "Bad Request", "BAD_REQUEST");
        ErrorResponse response404 = ErrorResponse.of(404, "Not Found", "NOT_FOUND");
        ErrorResponse response500 = ErrorResponse.of(500, "Internal Error", "INTERNAL_ERROR");

        assertThat(response400.getStatus()).isEqualTo(400);
        assertThat(response404.getStatus()).isEqualTo(404);
        assertThat(response500.getStatus()).isEqualTo(500);
    }

    @Test
    void testErrorResponseAllFieldsPopulated() {
        String timestamp = "2026-06-22T10:30:00Z";
        int status = 503;
        String error = "Service Unavailable";
        String message = "Database connection failed";
        String errorCode = "DATABASE_ERROR";
        String path = "/api/wallets";
        String requestId = "req-789";

        ErrorResponse response = new ErrorResponse(timestamp, status, error, message, errorCode, path, requestId);

        assertThat(response.getTimestamp()).isEqualTo(timestamp);
        assertThat(response.getStatus()).isEqualTo(status);
        assertThat(response.getError()).isEqualTo(error);
        assertThat(response.getMessage()).isEqualTo(message);
        assertThat(response.getErrorCode()).isEqualTo(errorCode);
        assertThat(response.getPath()).isEqualTo(path);
        assertThat(response.getRequestId()).isEqualTo(requestId);
    }
}

