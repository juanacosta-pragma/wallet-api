package co.com.bancolombia.usecase.wallet.commands;

import java.math.BigDecimal;

import static co.com.bancolombia.usecase.wallet.commands.PocketLocator.requireNonBlank;

/**
 * Comand for update amount  transaction.
 */
public record UpdateTransactionAmountCommand(
        String walletId,
        String pocketId,
        String transactionId,
        BigDecimal newAmount) {

    public UpdateTransactionAmountCommand {
        requireNonBlank(walletId, "walletId");
        requireNonBlank(pocketId, "pocketId");
        requireNonBlank(transactionId, "transactionId");
        if (newAmount == null || newAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("newAmount must be a non-negative number");
        }
    }

    public TransactionLocator transactionLocator() {
        return new TransactionLocator(walletId, pocketId, transactionId);
    }
}

