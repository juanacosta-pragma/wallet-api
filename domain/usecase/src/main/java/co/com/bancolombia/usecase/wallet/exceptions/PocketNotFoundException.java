package co.com.bancolombia.usecase.wallet.exceptions;

public class PocketNotFoundException extends RuntimeException {
    public PocketNotFoundException(String message) {
        super(message);
    }
}

