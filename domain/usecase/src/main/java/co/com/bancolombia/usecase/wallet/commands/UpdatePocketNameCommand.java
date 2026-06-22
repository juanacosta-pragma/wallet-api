package co.com.bancolombia.usecase.wallet.commands;

import static co.com.bancolombia.usecase.wallet.commands.PocketLocator.requireNonBlank;

/**
 * Comando para renombrar una sucursal.
 */
public record UpdatePocketNameCommand(String walletId, String pocketId, String newName) {

    public UpdatePocketNameCommand {
        requireNonBlank(walletId, "walletId");
        requireNonBlank(pocketId, "pocketId");
        requireNonBlank(newName, "newName");
    }

    public PocketLocator pocketLocator() {
        return new PocketLocator(walletId, pocketId);
    }
}

