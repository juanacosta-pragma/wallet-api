package co.com.bancolombia.usecase.wallet.commands;

import co.com.bancolombia.model.transaction.Transaction;

import static co.com.bancolombia.usecase.wallet.commands.PocketLocator.requireNonBlank;

/**
 * Comando para añadir un transactiono nuevo a una sucursal.
 */
public record CreateTransactionCommand(String walletId, String pocketId, Transaction transaction) {

    public CreateTransactionCommand {
        requireNonBlank(walletId, "walletId");
        requireNonBlank(pocketId, "pocketId");
        if (transaction == null) {
            throw new IllegalArgumentException("transaction must not be null");
        }
    }

    public PocketLocator pocketLocator() {
        return new PocketLocator(walletId, pocketId);
    }
}

