package co.com.bancolombia.model.wallet.gateways;

import co.com.bancolombia.model.wallet.Wallet;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface WalletRepository {
    Flux<Wallet> getAllWallets();
    Mono<Wallet> findById(String id);
    Mono<Wallet> save(Wallet wallet);
    Mono<Void> deleteById(String id);
}
