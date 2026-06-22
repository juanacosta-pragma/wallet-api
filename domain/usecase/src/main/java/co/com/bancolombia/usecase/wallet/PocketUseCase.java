package co.com.bancolombia.usecase.wallet;

import co.com.bancolombia.model.pocket.Pocket;
import co.com.bancolombia.model.pocket.gateways.PocketRepository;
import co.com.bancolombia.model.wallet.Wallet;
import co.com.bancolombia.model.wallet.gateways.WalletRepository;
import co.com.bancolombia.usecase.wallet.commands.PocketLocator;
import co.com.bancolombia.usecase.wallet.commands.CreatePocketCommand;
import co.com.bancolombia.usecase.wallet.commands.UpdatePocketNameCommand;
import co.com.bancolombia.usecase.wallet.exceptions.PocketNotFoundException;
import co.com.bancolombia.usecase.wallet.exceptions.WalletNotFoundException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;


@RequiredArgsConstructor
public class PocketUseCase {
    private final PocketRepository pocketRepository;
    private final WalletRepository walletRepository;

    public Flux<Pocket> getAllPocketes() {
        return pocketRepository.getAllPocketes();
    }

    public Mono<Wallet> createWallet(CreatePocketCommand command) {
        return findWallet(command.walletId())
                .flatMap(wallet -> Flux.fromIterable(wallet.getPocketes())
                        .concatWithValues(command.pocket().withId(UUID.randomUUID().toString()))
                        .collectList()
                        .map(wallet::withPocketes))
                .flatMap(walletRepository::save);
    }

    public Mono<Pocket> getWalletById(PocketLocator locator) {
        return findWallet(locator.walletId())
                .flatMapMany(wallet -> Flux.fromIterable(wallet.getPocketes()))
                .filter(b -> locator.pocketId().equals(b.getId()))
                .next()
                .switchIfEmpty(Mono.error(new PocketNotFoundException(
                        "Pocket not found with id: " + locator.pocketId())));
    }

    public Mono<Wallet> updateWalletName(UpdatePocketNameCommand command) {
        String pocketId = command.pocketId();
        return findWallet(command.walletId())
                .flatMap(wallet -> Flux.fromIterable(wallet.getPocketes())
                        .filter(b -> pocketId.equals(b.getId()))
                        .next()
                        .switchIfEmpty(Mono.error(new PocketNotFoundException(
                                "Pocket not found with id: " + pocketId)))
                        .thenMany(Flux.fromIterable(wallet.getPocketes()))
                        .map(b -> pocketId.equals(b.getId()) ? b.withName(command.newName()) : b)
                        .collectList()
                        .map(wallet::withPocketes))
                .flatMap(walletRepository::save);
    }

    public Mono<Void> deletePocket(String id) {
        return pocketRepository.deleteById(id);
    }

    private Mono<Wallet> findWallet(String walletId) {
        return walletRepository.findById(walletId)
                .switchIfEmpty(Mono.error(new WalletNotFoundException(
                        "Wallet not found with id: " + walletId)));
    }
}