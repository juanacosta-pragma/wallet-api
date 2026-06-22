package co.com.bancolombia.usecase.wallet.commands;

import co.com.bancolombia.model.pocket.Pocket;

import static co.com.bancolombia.usecase.wallet.commands.PocketLocator.requireNonBlank;

/**
 * Comando para añadir una nueva sucursal a una franquicia existente.
 */
public record CreatePocketCommand(String walletId, Pocket pocket) {

    public CreatePocketCommand {
        requireNonBlank(walletId, "walletId");
        if (pocket == null) {
            throw new IllegalArgumentException("pocket must not be null");
        }
    }
}

