package co.com.bancolombia.usecase.wallet.commands;

import static co.com.bancolombia.usecase.wallet.commands.PocketLocator.requireNonBlank;


public record UpdateTransactionNameCommand(
        String walletId,
        String pocketId,
        String transactionId,
        String newDescription) {

    public UpdateTransactionNameCommand {
        requireNonBlank(walletId, "walletId");
        requireNonBlank(pocketId, "pocketId");
        requireNonBlank(transactionId, "transactionId");
        requireNonBlank(newDescription, "newName");
    }

    public TransactionLocator transactionLocator() {
        return new TransactionLocator(walletId, pocketId, transactionId);
    }
}

