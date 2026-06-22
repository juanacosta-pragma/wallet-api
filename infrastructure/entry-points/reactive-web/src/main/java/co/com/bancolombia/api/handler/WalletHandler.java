package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.dto.WalletRequest;
import co.com.bancolombia.api.dto.WalletResponse;
import co.com.bancolombia.api.mapper.WalletMapper;
import co.com.bancolombia.usecase.wallet.WalletUseCase;
import co.com.bancolombia.usecase.wallet.exceptions.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
@Slf4j
public class WalletHandler {

    private final WalletUseCase walletUseCase;
    private final WalletMapper walletMapper;

    public Mono<ServerResponse> createWallet(ServerRequest request) {
        return request.bodyToMono(WalletRequest.class)
                .doFirst(() -> log.info("Received request to create wallet"))
                .filter(walletRequest -> walletRequest.getUserId() != null && !walletRequest.getUserId().isBlank())
                .switchIfEmpty(Mono.error(new ValidationException("Wallet name is required")))
                .map(walletMapper::toModel)
                .flatMap(walletUseCase::createWallet)
                .flatMap(wallet -> ServerResponse.created(URI.create("/wallets/" + wallet.getId()))
                        .bodyValue(walletMapper.toResponse(wallet)))
                .doOnError(error -> log.error("Error creating wallet", error));
    }

    public Mono<ServerResponse> getWalletById(ServerRequest request) {
        String id = request.pathVariable("id");
        return walletUseCase.getWalletById(id)
                .doFirst(() -> log.info("Received request to get wallet with id: {}", id))
                .flatMap(wallet -> ServerResponse.ok().bodyValue(walletMapper.toResponse(wallet)))
                .doOnError(error -> log.error("Error getting wallet", error));
    }

    public Mono<ServerResponse> getAllWallets(ServerRequest request) {
        return ServerResponse.ok()
                .body(walletUseCase.getAllWallets().map(walletMapper::toResponse), WalletResponse.class)
                .doFirst(() -> log.info("Received request to get all wallets"))
                .doOnError(error -> log.error("Error getting wallets", error));
    }

    public Mono<ServerResponse> updateWalletName(ServerRequest request) {
        String id = request.pathVariable("id");
        return request.bodyToMono(WalletRequest.class)
                .doFirst(() -> log.info("Received request to update wallet with id: {}", id))
                .filter(walletRequest -> walletRequest.getUserId() != null && !walletRequest.getUserId().isBlank())
                .switchIfEmpty(Mono.error(new ValidationException("Wallet user ID is required")))
                .flatMap(walletRequest -> walletUseCase.updateWalletName(id, walletRequest.getUserId()))
                .flatMap(wallet -> ServerResponse.ok().bodyValue(walletMapper.toResponse(wallet)))
                .doOnError(error -> log.error("Error updating wallet", error));
    }

    public Mono<ServerResponse> deleteWallet(ServerRequest request) {
        String id = request.pathVariable("id");
        return walletUseCase.deleteWallet(id)
                .doFirst(() -> log.info("Received request to delete wallet with id: {}", id))
                .then(ServerResponse.noContent().build())
                .doOnError(error -> log.error("Error deleting wallet", error));
    }
}
