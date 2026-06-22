package co.com.bancolombia.usecase.wallet;

import co.com.bancolombia.model.pocket.Pocket;
import co.com.bancolombia.model.pocket.gateways.PocketRepository;
import co.com.bancolombia.model.wallet.Wallet;
import co.com.bancolombia.model.wallet.gateways.WalletRepository;
import co.com.bancolombia.model.transaction.Transaction;
import co.com.bancolombia.usecase.wallet.commands.*;
import co.com.bancolombia.usecase.wallet.exceptions.PocketNotFoundException;
import co.com.bancolombia.usecase.wallet.exceptions.WalletNotFoundException;
import co.com.bancolombia.usecase.wallet.exceptions.TransactionNotFoundException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.UnaryOperator;


@RequiredArgsConstructor
public class TransactionUseCase {
    private final PocketRepository pocketRepository;
    private final WalletRepository walletRepository;

    public Flux<Pocket> getAllPocketes() {
        return pocketRepository.getAllPocketes();
    }

    public Mono<Wallet> create(CreateTransactionCommand command) {
        return findWallet(command.walletId())
                .flatMap(wallet -> findPocket(wallet, command.pocketId())
                        .then(rebuildPocketes(wallet, command.pocketId(),
                                pocket -> appendTransaction(pocket, command.transaction()))))
                .flatMap(walletRepository::save);
    }

    public Mono<Transaction> getTransactionByIdUseCase(TransactionLocator locator) {
        return findWallet(locator.walletId())
                .flatMap(wallet -> findPocket(wallet, locator.pocketId()))
                .flatMap(pocket -> findTransaction(pocket, locator.transactionId()));
    }

    public Mono<Wallet> deleteTransactionFromPocketUseCase(TransactionLocator locator) {
        return findWallet(locator.walletId())
                .flatMap(wallet -> findPocket(wallet, locator.pocketId())
                        .flatMap(pocket -> findTransaction(pocket, locator.transactionId()))
                        .then(rebuildPocketes(wallet, locator.pocketId(),
                                pocket -> removeTransaction(pocket, locator.transactionId()))))
                .flatMap(walletRepository::save);
    }

    public Mono<Wallet> updateTransactionAmountUseCase(UpdateTransactionAmountCommand command) {
        return updateTransaction(command.transactionLocator(), p -> p.withAmount(command.newAmount()));
    }

    public Mono<Wallet> updateTransactionNameUseCase(UpdateTransactionNameCommand command) {
        return updateTransaction(command.transactionLocator(), p -> p.withDescription(command.newDescription()));
    }

    public Mono<Transaction> getHighestAmountTransactionByPocketUseCase(PocketLocator locator) {
        return findWallet(locator.walletId())
                .flatMap(wallet -> findPocket(wallet, locator.pocketId()))
                .flatMapMany(pocket -> Flux.fromIterable(pocket.getTransactions()))
                .reduce((a, b) -> a.getAmount().compareTo(b.getAmount()) >= 0 ? a : b)
                .switchIfEmpty(Mono.error(new TransactionNotFoundException("No transactions found in pocket")));
    }

    // --- private reactive helpers ---

    private Mono<Wallet> findWallet(String walletId) {
        return walletRepository.findById(walletId)
                .switchIfEmpty(Mono.error(new WalletNotFoundException("Wallet not found with id: " + walletId)));
    }

    private Mono<Pocket> findPocket(Wallet wallet, String pocketId) {
        return Flux.fromIterable(wallet.getPocketes())
                .filter(b -> pocketId.equals(b.getId()))
                .next()
                .switchIfEmpty(Mono.error(new PocketNotFoundException("Pocket not found with id: " + pocketId)));
    }

    private Mono<Transaction> findTransaction(Pocket pocket, String transactionId) {
        return Flux.fromIterable(pocket.getTransactions())
                .filter(p -> transactionId.equals(p.getId()))
                .next()
                .switchIfEmpty(Mono.error(new TransactionNotFoundException("Transaction not found with id: " + transactionId)));
    }

    private Mono<Wallet> updateTransaction(TransactionLocator locator, UnaryOperator<Transaction> transform) {
        return findWallet(locator.walletId())
                .flatMap(wallet -> findPocket(wallet, locator.pocketId())
                        .flatMap(pocket -> findTransaction(pocket, locator.transactionId()))
                        .then(rebuildPocketes(wallet, locator.pocketId(),
                                pocket -> updateTransactionInPocket(pocket, locator.transactionId(), transform))))
                .flatMap(walletRepository::save);
    }

    private Mono<Wallet> rebuildPocketes(Wallet wallet, String pocketId, Function<Pocket, Mono<Pocket>> pocketTransform) {
        return Flux.fromIterable(wallet.getPocketes())
                .concatMap(b -> pocketId.equals(b.getId()) ? pocketTransform.apply(b) : Mono.just(b))
                .collectList()
                .map(wallet::withPocketes);
    }

    private Mono<Pocket> appendTransaction(Pocket pocket, Transaction transaction) {
        Transaction newTransaction = transaction
                .withId(UUID.randomUUID().toString())
                .withCreatedAt(LocalDateTime.now());
        return Flux.fromIterable(pocket.getTransactions())
                .concatWithValues(newTransaction)
                .collectList()
                .map(pocket::withTransactions);
    }

    private Mono<Pocket> removeTransaction(Pocket pocket, String transactionId) {
        return Flux.fromIterable(pocket.getTransactions())
                .filter(p -> !transactionId.equals(p.getId()))
                .collectList()
                .map(pocket::withTransactions);
    }

    private Mono<Pocket> updateTransactionInPocket(Pocket pocket, String transactionId, UnaryOperator<Transaction> transform) {
        return Flux.fromIterable(pocket.getTransactions())
                .map(p -> transactionId.equals(p.getId()) ? transform.apply(p) : p)
                .collectList()
                .map(pocket::withTransactions);
    }
}