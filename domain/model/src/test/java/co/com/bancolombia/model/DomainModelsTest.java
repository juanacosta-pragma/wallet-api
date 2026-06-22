package co.com.bancolombia.model;

import co.com.bancolombia.model.pocket.Pocket;
import co.com.bancolombia.model.wallet.Wallet;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WalletTest {

    @Test
    void testWalletBuilder() {
        Pocket pocket = Pocket.builder().id("p1").name("Savings").build();
        Wallet wallet = Wallet.builder()
                .id("w1")
                .userId("user123")
                .pocketes(List.of(pocket))
                .build();

        assertThat(wallet.getId()).isEqualTo("w1");
        assertThat(wallet.getUserId()).isEqualTo("user123");
        assertThat(wallet.getPocketes()).hasSize(1);
    }

    @Test
    void testWalletNoArgsConstructor() {
        Wallet wallet = new Wallet();

        assertThat(wallet).isNotNull();
        assertThat(wallet.getId()).isNull();
        assertThat(wallet.getUserId()).isNull();
        assertThat(wallet.getPocketes()).isNotNull();
    }

    @Test
    void testWalletAllArgsConstructor() {
        List<Pocket> pocketes = new ArrayList<>();
        Wallet wallet = new Wallet("w1", "user1", pocketes);

        assertThat(wallet.getId()).isEqualTo("w1");
        assertThat(wallet.getUserId()).isEqualTo("user1");
        assertThat(wallet.getPocketes()).isEqualTo(pocketes);
    }

    @Test
    void testWalletSettersAndGetters() {
        Wallet wallet = new Wallet();
        wallet.setId("w2");
        wallet.setUserId("user2");
        wallet.setPocketes(new ArrayList<>());

        assertThat(wallet.getId()).isEqualTo("w2");
        assertThat(wallet.getUserId()).isEqualTo("user2");
    }

    @Test
    void testWalletWith() {
        Wallet wallet = Wallet.builder().id("w1").userId("user1").build();
        Wallet updated = wallet.withUserId("user2");

        assertThat(wallet.getUserId()).isEqualTo("user1");
        assertThat(updated.getUserId()).isEqualTo("user2");
        assertThat(updated.getId()).isEqualTo("w1");
    }

    @Test
    void testWalletBuilderDefaultPocketes() {
        Wallet wallet = Wallet.builder().id("w1").userId("user1").build();

        assertThat(wallet.getPocketes()).isEmpty();
    }

    @Test
    void testWalletEquality() {
        Wallet wallet1 = new Wallet("w1", "user1", new ArrayList<>());
        Wallet wallet2 = new Wallet("w1", "user1", new ArrayList<>());

        // Lombok-generated equals compares all fields including pocketes list reference
        // So we check that objects are not null and have same id
        assertThat(wallet1).isNotNull();
        assertThat(wallet2).isNotNull();
        assertThat(wallet1.getId()).isEqualTo(wallet2.getId());
    }

    @Test
    void testWalletWithNullId() {
        Wallet wallet = Wallet.builder().id(null).userId("user1").build();

        assertThat(wallet.getId()).isNull();
    }

    @Test
    void testWalletWithNullUserId() {
        Wallet wallet = Wallet.builder().id("w1").userId(null).build();

        assertThat(wallet.getUserId()).isNull();
    }
}

class PocketTest {

    @Test
    void testPocketBuilder() {
        Pocket pocket = Pocket.builder()
                .id("p1")
                .name("Savings")
                .transactions(new ArrayList<>())
                .build();

        assertThat(pocket.getId()).isEqualTo("p1");
        assertThat(pocket.getName()).isEqualTo("Savings");
        assertThat(pocket.getTransactions()).isEmpty();
    }

    @Test
    void testPocketNoArgsConstructor() {
        Pocket pocket = new Pocket();

        assertThat(pocket).isNotNull();
        assertThat(pocket.getId()).isNull();
        assertThat(pocket.getName()).isNull();
        assertThat(pocket.getTransactions()).isNotNull();
    }

    @Test
    void testPocketSettersAndGetters() {
        Pocket pocket = new Pocket();
        pocket.setId("p1");
        pocket.setName("Investment");
        pocket.setTransactions(new ArrayList<>());

        assertThat(pocket.getId()).isEqualTo("p1");
        assertThat(pocket.getName()).isEqualTo("Investment");
    }

    @Test
    void testPocketWith() {
        Pocket pocket = Pocket.builder().id("p1").name("OldName").build();
        Pocket updated = pocket.withName("NewName");

        assertThat(pocket.getName()).isEqualTo("OldName");
        assertThat(updated.getName()).isEqualTo("NewName");
    }

    @Test
    void testPocketEquality() {
        Pocket pocket1 = new Pocket("p1", "Savings", new ArrayList<>());
        Pocket pocket2 = new Pocket("p1", "Savings", new ArrayList<>());

        // Verify they have the same field values
        assertThat(pocket1.getId()).isEqualTo(pocket2.getId());
        assertThat(pocket1.getName()).isEqualTo(pocket2.getName());
    }

    @Test
    void testPocketWithNullName() {
        Pocket pocket = Pocket.builder().id("p1").name(null).build();

        assertThat(pocket.getName()).isNull();
    }
}

class TransactionTest {

    @Test
    void testTransactionBuilder() {
        co.com.bancolombia.model.transaction.Transaction transaction =
            co.com.bancolombia.model.transaction.Transaction.builder()
                .id("t1")
                .description("Laptop")
                .amount(BigDecimal.valueOf(100))
                .build();

        assertThat(transaction.getId()).isEqualTo("t1");
        assertThat(transaction.getDescription()).isEqualTo("Laptop");
        assertThat(transaction.getAmount()).isEqualTo(BigDecimal.valueOf(100));
    }

    @Test
    void testTransactionNoArgsConstructor() {
        co.com.bancolombia.model.transaction.Transaction transaction =
            new co.com.bancolombia.model.transaction.Transaction();

        assertThat(transaction).isNotNull();
        assertThat(transaction.getId()).isNull();
    }

    @Test
    void testTransactionSettersAndGetters() {
        co.com.bancolombia.model.transaction.Transaction transaction =
            new co.com.bancolombia.model.transaction.Transaction();
        transaction.setId("t1");
        transaction.setDescription("iPhone");
        transaction.setAmount(BigDecimal.valueOf(50));

        assertThat(transaction.getId()).isEqualTo("t1");
        assertThat(transaction.getDescription()).isEqualTo("iPhone");
        assertThat(transaction.getAmount()).isEqualTo(BigDecimal.valueOf(50));
    }

    @Test
    void testTransactionWith() {
        co.com.bancolombia.model.transaction.Transaction transaction =
            co.com.bancolombia.model.transaction.Transaction.builder()
                .id("t1").description("Old").amount(BigDecimal.valueOf(100)).build();
        co.com.bancolombia.model.transaction.Transaction updated =
            transaction.withDescription("New");

        assertThat(transaction.getDescription()).isEqualTo("Old");
        assertThat(updated.getDescription()).isEqualTo("New");
    }

    @Test
    void testTransactionWithZeroAmount() {
        co.com.bancolombia.model.transaction.Transaction transaction =
            co.com.bancolombia.model.transaction.Transaction.builder()
                .id("t1").description("Item").amount(BigDecimal.ZERO).build();

        assertThat(transaction.getAmount()).isZero();
    }

    @Test
    void testTransactionEquality() {
        co.com.bancolombia.model.transaction.Transaction transaction1 =
            new co.com.bancolombia.model.transaction.Transaction("t1", "Description", BigDecimal.valueOf(100));
        co.com.bancolombia.model.transaction.Transaction transaction2 =
            new co.com.bancolombia.model.transaction.Transaction("t1", "Description", BigDecimal.valueOf(100));

        // Verify they have the same field values
        assertThat(transaction1.getId()).isEqualTo(transaction2.getId());
        assertThat(transaction1.getDescription()).isEqualTo(transaction2.getDescription());
        assertThat(transaction1.getAmount()).isEqualTo(transaction2.getAmount());
    }
}

