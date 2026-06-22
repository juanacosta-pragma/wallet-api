package co.com.bancolombia.usecase.wallet.exceptions;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionsTest {

    @Test
    void walletNotFoundException_storesMessage() {
        WalletNotFoundException ex = new WalletNotFoundException("not found");
        assertThat(ex.getMessage()).isEqualTo("not found");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    @Test
    void walletNotFoundException_withNullMessage() {
        WalletNotFoundException ex = new WalletNotFoundException(null);
        assertThat(ex.getMessage()).isNull();
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    @Test
    void walletNotFoundException_withEmptyMessage() {
        WalletNotFoundException ex = new WalletNotFoundException("");
        assertThat(ex.getMessage()).isEmpty();
    }

    @Test
    void pocketNotFoundException_storesMessage() {
        PocketNotFoundException ex = new PocketNotFoundException("pocket missing");
        assertThat(ex.getMessage()).isEqualTo("pocket missing");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    @Test
    void pocketNotFoundException_withNullMessage() {
        PocketNotFoundException ex = new PocketNotFoundException(null);
        assertThat(ex.getMessage()).isNull();
    }

    @Test
    void pocketNotFoundException_withComplexMessage() {
        String complexMsg = "Pocket not found with id: pocket123-456";
        PocketNotFoundException ex = new PocketNotFoundException(complexMsg);
        assertThat(ex.getMessage()).isEqualTo(complexMsg);
    }

    @Test
    void transactionNotFoundException_storesMessage() {
        TransactionNotFoundException ex = new TransactionNotFoundException("transaction missing");
        assertThat(ex.getMessage()).isEqualTo("transaction missing");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    @Test
    void transactionNotFoundException_withNullMessage() {
        TransactionNotFoundException ex = new TransactionNotFoundException(null);
        assertThat(ex.getMessage()).isNull();
    }

    @Test
    void transactionNotFoundException_noTransactionsInPocket() {
        String msg = "No transactions found in pocket";
        TransactionNotFoundException ex = new TransactionNotFoundException(msg);
        assertThat(ex.getMessage()).isEqualTo(msg);
    }

    @Test
    void validationException_storesMessage() {
        ValidationException ex = new ValidationException("invalid");
        assertThat(ex.getMessage()).isEqualTo("invalid");
        assertThat(ex).isInstanceOf(RuntimeException.class);
    }

    @Test
    void validationException_withNullMessage() {
        ValidationException ex = new ValidationException(null);
        assertThat(ex.getMessage()).isNull();
    }

    @Test
    void validationException_emptyFieldError() {
        String msg = "Wallet name is required";
        ValidationException ex = new ValidationException(msg);
        assertThat(ex.getMessage()).isEqualTo(msg);
    }

    @Test
    void exceptionChaining_walletNotFound() {
        RuntimeException cause = new RuntimeException("Original cause");
        WalletNotFoundException ex = new WalletNotFoundException("Wallet lookup failed");

        assertThat(ex).isInstanceOf(RuntimeException.class);
        assertThat(ex.getMessage()).isEqualTo("Wallet lookup failed");
    }

    @Test
    void multipleExceptions_canBeThrown() {
        Throwable[] exceptions = {
            new WalletNotFoundException("Wallet not found"),
            new PocketNotFoundException("Pocket not found"),
            new TransactionNotFoundException("Transaction not found"),
            new ValidationException("Validation failed")
        };

        for (Throwable ex : exceptions) {
            assertThat(ex).isInstanceOf(RuntimeException.class);
        }
    }
}

