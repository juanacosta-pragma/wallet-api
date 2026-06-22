package co.com.bancolombia.usecase.wallet.commands;

import co.com.bancolombia.model.pocket.Pocket;
import co.com.bancolombia.model.transaction.Transaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreatePocketCommandTest {

    @Test
    void testCreatePocketCommandCreation() {
        Pocket pocket = Pocket.builder().name("Savings").build();
        CreatePocketCommand command = new CreatePocketCommand("wallet1", pocket);

        assertThat(command).isNotNull();
        assertThat(command.walletId()).isEqualTo("wallet1");
        assertThat(command.pocket()).isEqualTo(pocket);
    }

    @Test
    void testCreatePocketCommandWithNullWalletId() {
        Pocket pocket = Pocket.builder().name("Savings").build();

        assertThatThrownBy(() -> new CreatePocketCommand(null, pocket))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("walletId");
    }

    @Test
    void testCreatePocketCommandWithBlankWalletId() {
        Pocket pocket = Pocket.builder().name("Savings").build();

        assertThatThrownBy(() -> new CreatePocketCommand("  ", pocket))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("walletId");
    }

    @Test
    void testCreatePocketCommandWithNullPocket() {
        assertThatThrownBy(() -> new CreatePocketCommand("wallet1", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("pocket must not be null");
    }
}

class CreateTransactionCommandTest {

    @Test
    void testCreateTransactionCommandCreation() {
        Transaction transaction = Transaction.builder().description("Apple").amount(BigDecimal.valueOf(10)).build();
        CreateTransactionCommand command = new CreateTransactionCommand("wallet1", "pocket1", transaction);

        assertThat(command).isNotNull();
        assertThat(command.walletId()).isEqualTo("wallet1");
        assertThat(command.pocketId()).isEqualTo("pocket1");
        assertThat(command.transaction()).isEqualTo(transaction);
    }

    @Test
    void testCreateTransactionCommandPocketLocator() {
        Transaction transaction = Transaction.builder().description("Apple").amount(BigDecimal.valueOf(10)).build();
        CreateTransactionCommand command = new CreateTransactionCommand("wallet1", "pocket1", transaction);

        PocketLocator locator = command.pocketLocator();
        assertThat(locator.walletId()).isEqualTo("wallet1");
        assertThat(locator.pocketId()).isEqualTo("pocket1");
    }

    @Test
    void testCreateTransactionCommandWithNullWalletId() {
        Transaction transaction = Transaction.builder().description("Apple").amount(BigDecimal.valueOf(10)).build();

        assertThatThrownBy(() -> new CreateTransactionCommand(null, "pocket1", transaction))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("walletId");
    }

    @Test
    void testCreateTransactionCommandWithBlankPocketId() {
        Transaction transaction = Transaction.builder().description("Apple").amount(BigDecimal.valueOf(10)).build();

        assertThatThrownBy(() -> new CreateTransactionCommand("wallet1", "", transaction))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("pocketId");
    }

    @Test
    void testCreateTransactionCommandWithNullTransaction() {
        assertThatThrownBy(() -> new CreateTransactionCommand("wallet1", "pocket1", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("transaction must not be null");
    }
}

class UpdateTransactionNameCommandTest {

    @Test
    void testUpdateTransactionNameCommandCreation() {
        UpdateTransactionNameCommand command = new UpdateTransactionNameCommand("wallet1", "pocket1", "transaction1", "NewName");

        assertThat(command).isNotNull();
        assertThat(command.walletId()).isEqualTo("wallet1");
        assertThat(command.pocketId()).isEqualTo("pocket1");
        assertThat(command.transactionId()).isEqualTo("transaction1");
        assertThat(command.newDescription()).isEqualTo("NewName");
    }

    @Test
    void testUpdateTransactionNameCommandTransactionLocator() {
        UpdateTransactionNameCommand command = new UpdateTransactionNameCommand("wallet1", "pocket1", "transaction1", "NewName");

        TransactionLocator locator = command.transactionLocator();
        assertThat(locator.walletId()).isEqualTo("wallet1");
        assertThat(locator.pocketId()).isEqualTo("pocket1");
        assertThat(locator.transactionId()).isEqualTo("transaction1");
    }

    @Test
    void testUpdateTransactionNameCommandWithNullNewName() {
        assertThatThrownBy(() -> new UpdateTransactionNameCommand("wallet1", "pocket1", "transaction1", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("newName");
    }

    @Test
    void testUpdateTransactionNameCommandWithBlankWalletId() {
        assertThatThrownBy(() -> new UpdateTransactionNameCommand("", "pocket1", "transaction1", "NewName"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("walletId");
    }
}

class PocketLocatorTest {

    @Test
    void testPocketLocatorCreation() {
        PocketLocator locator = new PocketLocator("wallet1", "pocket1");

        assertThat(locator).isNotNull();
        assertThat(locator.walletId()).isEqualTo("wallet1");
        assertThat(locator.pocketId()).isEqualTo("pocket1");
    }

    @Test
    void testPocketLocatorWithNullWalletId() {
        assertThatThrownBy(() -> new PocketLocator(null, "pocket1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("walletId");
    }

    @Test
    void testPocketLocatorWithBlankWalletId() {
        assertThatThrownBy(() -> new PocketLocator("  ", "pocket1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("walletId");
    }

    @Test
    void testPocketLocatorWithNullPocketId() {
        assertThatThrownBy(() -> new PocketLocator("wallet1", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("pocketId");
    }

    @Test
    void testPocketLocatorWithBlankPocketId() {
        assertThatThrownBy(() -> new PocketLocator("wallet1", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("pocketId");
    }

    @Test
    void testPocketLocatorEquality() {
        PocketLocator locator1 = new PocketLocator("wallet1", "pocket1");
        PocketLocator locator2 = new PocketLocator("wallet1", "pocket1");

        assertThat(locator1).isEqualTo(locator2);
    }
}

class TransactionLocatorTest {

    @Test
    void testTransactionLocatorCreation() {
        TransactionLocator locator = new TransactionLocator("wallet1", "pocket1", "transaction1");

        assertThat(locator).isNotNull();
        assertThat(locator.walletId()).isEqualTo("wallet1");
        assertThat(locator.pocketId()).isEqualTo("pocket1");
        assertThat(locator.transactionId()).isEqualTo("transaction1");
    }

    @Test
    void testTransactionLocatorWithNullWalletId() {
        assertThatThrownBy(() -> new TransactionLocator(null, "pocket1", "transaction1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("walletId");
    }

    @Test
    void testTransactionLocatorWithBlankTransactionId() {
        assertThatThrownBy(() -> new TransactionLocator("wallet1", "pocket1", "  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("transactionId");
    }

    @Test
    void testTransactionLocatorEquality() {
        TransactionLocator locator1 = new TransactionLocator("wallet1", "pocket1", "transaction1");
        TransactionLocator locator2 = new TransactionLocator("wallet1", "pocket1", "transaction1");

        assertThat(locator1).isEqualTo(locator2);
    }
}

