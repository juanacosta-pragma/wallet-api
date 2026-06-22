package co.com.bancolombia.usecase.wallet.commands;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UpdateTransactionAmountCommandTest {

    @Test
    void testUpdateTransactionAmountCommandCreation() {
        co.com.bancolombia.usecase.wallet.commands.UpdateTransactionAmountCommand command =
            new co.com.bancolombia.usecase.wallet.commands.UpdateTransactionAmountCommand("w1", "p1", "t1", BigDecimal.TEN);

        assertThat(command.walletId()).isEqualTo("w1");
        assertThat(command.pocketId()).isEqualTo("p1");
        assertThat(command.transactionId()).isEqualTo("t1");
        assertThat(command.newAmount()).isEqualTo( BigDecimal.TEN);
    }

    @Test
    void testUpdateTransactionAmountCommandTransactionLocator() {
        co.com.bancolombia.usecase.wallet.commands.UpdateTransactionAmountCommand command =
            new co.com.bancolombia.usecase.wallet.commands.UpdateTransactionAmountCommand("w1", "p1", "t1",  BigDecimal.valueOf(50));

        TransactionLocator locator = command.transactionLocator();
        assertThat(locator.walletId()).isEqualTo("w1");
        assertThat(locator.pocketId()).isEqualTo("p1");
        assertThat(locator.transactionId()).isEqualTo("t1");
    }

    @Test
    void testUpdateTransactionAmountCommandWithNullWalletId() {
        assertThatThrownBy(() ->
            new co.com.bancolombia.usecase.wallet.commands.UpdateTransactionAmountCommand(null, "p1", "t1",  BigDecimal.valueOf(100)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testUpdateTransactionAmountCommandWithZeroAmount() {
        co.com.bancolombia.usecase.wallet.commands.UpdateTransactionAmountCommand command =
            new co.com.bancolombia.usecase.wallet.commands.UpdateTransactionAmountCommand("w1", "p1", "t1", BigDecimal.ZERO);

        assertThat(command.newAmount()).isZero();
    }
}

class UpdatePocketNameCommandTest {

    @Test
    void testUpdatePocketNameCommandCreation() {
        UpdatePocketNameCommand command = new UpdatePocketNameCommand("w1", "p1", "New Name");

        assertThat(command.walletId()).isEqualTo("w1");
        assertThat(command.pocketId()).isEqualTo("p1");
        assertThat(command.newName()).isEqualTo("New Name");
    }

    @Test
    void testUpdatePocketNameCommandWithNullWalletId() {
        assertThatThrownBy(() -> new UpdatePocketNameCommand(null, "p1", "New Name"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testUpdatePocketNameCommandWithBlankNewName() {
        assertThatThrownBy(() -> new UpdatePocketNameCommand("w1", "p1", ""))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

