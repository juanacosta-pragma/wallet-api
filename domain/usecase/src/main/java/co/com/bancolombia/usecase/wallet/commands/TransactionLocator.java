package co.com.bancolombia.usecase.wallet.commands;

import static co.com.bancolombia.usecase.wallet.commands.PocketLocator.requireNonBlank;

/**
 * Identifica un transactiono específico dentro de la jerarquía
 * wallet -> pocket -> transaction. Reutilizado por get/delete y como base
 * de los comandos de actualización.
 */
public record TransactionLocator(String walletId, String pocketId, String transactionId) {

    public TransactionLocator {
        requireNonBlank(walletId, "walletId");
        requireNonBlank(pocketId, "pocketId");
        requireNonBlank(transactionId, "transactionId");
    }

    public PocketLocator pocketLocator() {
        return new PocketLocator(walletId, pocketId);
    }
}

