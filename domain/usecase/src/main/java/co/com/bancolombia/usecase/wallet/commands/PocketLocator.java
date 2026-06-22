package co.com.bancolombia.usecase.wallet.commands;

/**
 * Identifica una sucursal específica dentro de una franquicia.
 * Reutilizado por todos los casos de uso que operan a nivel de pocket.
 */
public record PocketLocator(String walletId, String pocketId) {

    public PocketLocator {
        requireNonBlank(walletId, "walletId");
        requireNonBlank(pocketId, "pocketId");
    }

    static void requireNonBlank(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be null or blank");
        }
    }
}

