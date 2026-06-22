package co.com.bancolombia.usecase.wallet;

import co.com.bancolombia.model.wallet.Wallet;
import co.com.bancolombia.model.wallet.gateways.WalletRepository;
import co.com.bancolombia.usecase.wallet.exceptions.WalletNotFoundException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@RequiredArgsConstructor
public class WalletUseCase {
    private final WalletRepository walletRepository;

    public Flux<Wallet> getAllWallets() {
        return walletRepository.getAllWallets();
    }

    public Mono<Wallet> createWallet(Wallet wallet) {
        return walletRepository.save(wallet.withPocketes(List.of()));
    }

    public Mono<Wallet> getWalletById(String id) {
        return walletRepository.findById(id)
                .switchIfEmpty(Mono.error(new WalletNotFoundException("Wallet not found with id: " + id)));
    }

    public Mono<Wallet> updateWalletName(String id, String newName) {
        return walletRepository.findById(id)
                .switchIfEmpty(Mono.error(new WalletNotFoundException("Wallet not found with id: " + id)))
                .map(wallet -> wallet.withUserId(newName))
                .flatMap(walletRepository::save);
    }

    public Mono<Void> deleteWallet(String id) {
        return walletRepository.deleteById(id);
    }
}