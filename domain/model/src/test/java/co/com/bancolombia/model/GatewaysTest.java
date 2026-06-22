package co.com.bancolombia.model.wallet.gateways;

import co.com.bancolombia.model.wallet.Wallet;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WalletRepositoryTest {

    @Test
    void testWalletRepositoryInterface() {
        // The interface is defined, just verify it's properly structured
        assertThat(WalletRepository.class).isInterface();
    }

    @Test
    void testWalletRepositoryMethods() {
        // Verify that the interface has expected methods
        assertThat(WalletRepository.class.getMethods().length).isGreaterThan(0);
    }
}

class PocketRepositoryTest {

    @Test
    void testPocketRepositoryInterface() {
        assertThat(co.com.bancolombia.model.pocket.gateways.PocketRepository.class).isInterface();
    }

    @Test
    void testPocketRepositoryMethods() {
        assertThat(co.com.bancolombia.model.pocket.gateways.PocketRepository.class.getMethods().length).isGreaterThan(0);
    }
}

