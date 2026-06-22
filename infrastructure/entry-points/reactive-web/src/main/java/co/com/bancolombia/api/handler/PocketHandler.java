package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.dto.PocketRequest;
import co.com.bancolombia.api.mapper.PocketMapper;
import co.com.bancolombia.api.mapper.WalletMapper;
import co.com.bancolombia.usecase.wallet.PocketUseCase;
import co.com.bancolombia.usecase.wallet.commands.PocketLocator;
import co.com.bancolombia.usecase.wallet.commands.CreatePocketCommand;
import co.com.bancolombia.usecase.wallet.commands.UpdatePocketNameCommand;
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
public class PocketHandler {
    private final PocketUseCase pocketUseCase;
    private final PocketMapper pocketMapper;
    private final WalletMapper walletMapper;

    public Mono<ServerResponse> addPocket(ServerRequest request) {
        String walletId = request.pathVariable("walletId");
        return request.bodyToMono(PocketRequest.class)
                .doFirst(() -> log.info("Received request to add pocket to wallet: {}", walletId))
                .filter(pocketRequest -> pocketRequest.getName() != null && !pocketRequest.getName().isBlank())
                .switchIfEmpty(Mono.error(new ValidationException("Pocket name is required")))
                .map(pocketMapper::toModel)
                .flatMap(pocket -> pocketUseCase.createWallet(new CreatePocketCommand(walletId, pocket)))
                .flatMap(wallet -> ServerResponse.created(URI.create("/wallets/" + walletId + "/pocketes"))
                        .bodyValue(walletMapper.toResponse(wallet)))
                .doOnError(error -> log.error("Error adding pocket", error));
    }

    public Mono<ServerResponse> getPocket(ServerRequest request) {
        PocketLocator locator = new PocketLocator(
                request.pathVariable("walletId"),
                request.pathVariable("pocketId"));
        return pocketUseCase.getWalletById(locator)
                .doFirst(() -> log.info("Received request to get pocket {} from wallet {}",
                        locator.pocketId(), locator.walletId()))
                .flatMap(pocket -> ServerResponse.ok().bodyValue(pocketMapper.toResponse(pocket)))
                .doOnError(error -> log.error("Error getting pocket", error));
    }

    public Mono<ServerResponse> updatePocketName(ServerRequest request) {
        String walletId = request.pathVariable("walletId");
        String pocketId = request.pathVariable("pocketId");
        return request.bodyToMono(PocketRequest.class)
                .doFirst(() -> log.info("Received request to update pocket name for pocket {} in wallet {}", pocketId, walletId))
                .filter(pocketRequest -> pocketRequest.getName() != null && !pocketRequest.getName().isBlank())
                .switchIfEmpty(Mono.error(new ValidationException("Pocket name is required")))
                .flatMap(pocketRequest -> pocketUseCase.updateWalletName(
                        new UpdatePocketNameCommand(walletId, pocketId, pocketRequest.getName())))
                .flatMap(wallet -> ServerResponse.ok().bodyValue(walletMapper.toResponse(wallet)))
                .doOnError(error -> log.error("Error updating pocket", error));
    }
}
