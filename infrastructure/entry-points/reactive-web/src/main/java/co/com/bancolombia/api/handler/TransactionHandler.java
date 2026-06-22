package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.mapper.WalletMapper;
import co.com.bancolombia.api.dto.TransactionRequest;
import co.com.bancolombia.api.dto.AmountUpdateRequest;
import co.com.bancolombia.api.mapper.TransactionMapper;
import co.com.bancolombia.usecase.wallet.TransactionUseCase;
import co.com.bancolombia.usecase.wallet.commands.PocketLocator;
import co.com.bancolombia.usecase.wallet.commands.CreateTransactionCommand;
import co.com.bancolombia.usecase.wallet.commands.TransactionLocator;
import co.com.bancolombia.usecase.wallet.commands.UpdateTransactionNameCommand;
import co.com.bancolombia.usecase.wallet.commands.UpdateTransactionAmountCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.net.URI;

@Component
@RequiredArgsConstructor
@Slf4j
public class TransactionHandler {
    private final TransactionUseCase transactionUseCase;
    private final TransactionMapper transactionMapper;
    private final WalletMapper walletMapper;

    public Mono<ServerResponse> addTransaction(ServerRequest request) {
        String walletId = request.pathVariable("walletId");
        String pocketId = request.pathVariable("pocketId");
        return request.bodyToMono(TransactionRequest.class)
                .doFirst(() -> log.info("Received request to add transaction to pocket {} in wallet {}", pocketId, walletId))
                .filter(transactionRequest -> transactionRequest.getAmount() != null &&
                        transactionRequest.getAmount().compareTo(BigDecimal.ZERO) > 0)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Transaction amount is required")))
                .map(transactionMapper::toModel)
                .flatMap(transaction -> transactionUseCase.create(
                        new CreateTransactionCommand(walletId, pocketId, transaction)))
                .flatMap(wallet -> ServerResponse.created(
                                URI.create("/wallets/" + walletId + "/pocketes/" + pocketId + "/transactions"))
                        .bodyValue(walletMapper.toResponse(wallet)))
                .doOnError(error -> log.error("Error adding transaction", error));
    }

    public Mono<ServerResponse> getTransaction(ServerRequest request) {
        TransactionLocator locator = transactionLocatorFrom(request);
        return transactionUseCase.getTransactionByIdUseCase(locator)
                .doFirst(() -> log.info("Received request to get transaction {} from pocket {} in wallet {}",
                        locator.transactionId(), locator.pocketId(), locator.walletId()))
                .flatMap(transaction -> ServerResponse.ok().bodyValue(transactionMapper.toResponse(transaction)))
                .doOnError(error -> log.error("Error getting transaction", error));
    }

    public Mono<ServerResponse> deleteTransaction(ServerRequest request) {
        TransactionLocator locator = transactionLocatorFrom(request);
        return transactionUseCase.deleteTransactionFromPocketUseCase(locator)
                .doFirst(() -> log.info("Received request to delete transaction {} from pocket {} in wallet {}",
                        locator.transactionId(), locator.pocketId(), locator.walletId()))
                .flatMap(wallet -> ServerResponse.ok().bodyValue(walletMapper.toResponse(wallet)))
                .doOnError(error -> log.error("Error deleting transaction", error));
    }

    public Mono<ServerResponse> updateTransactionAmount(ServerRequest request) {
        String walletId = request.pathVariable("walletId");
        String pocketId = request.pathVariable("pocketId");
        String transactionId = request.pathVariable("transactionId");
        return request.bodyToMono(AmountUpdateRequest.class)
                .doFirst(() -> log.info("Received request to update transaction amount {} from pocket {} in wallet {}", transactionId, pocketId, walletId))
                .flatMap(amountUpdateRequest -> transactionUseCase.updateTransactionAmountUseCase(
                        new UpdateTransactionAmountCommand(walletId, pocketId, transactionId, amountUpdateRequest.getAmount())))
                .flatMap(wallet -> ServerResponse.ok().bodyValue(walletMapper.toResponse(wallet)))
                .doOnError(error -> log.error("Error updating transaction amount", error));
    }

    public Mono<ServerResponse> updateTransactionName(ServerRequest request) {
        String walletId = request.pathVariable("walletId");
        String pocketId = request.pathVariable("pocketId");
        String transactionId = request.pathVariable("transactionId");
        return request.bodyToMono(TransactionRequest.class)
                .doFirst(() -> log.info("Received request to update transaction name {} from pocket {} in wallet {}", transactionId, pocketId, walletId))
                .flatMap(transactionRequest -> transactionUseCase.updateTransactionNameUseCase(
                        new UpdateTransactionNameCommand(walletId, pocketId, transactionId, transactionRequest.getDescription())))
                .flatMap(wallet -> ServerResponse.ok().bodyValue(walletMapper.toResponse(wallet)))
                .doOnError(error -> log.error("Error updating transaction name", error));
    }

    public Mono<ServerResponse> getHighestAmountTransactionByPocket(ServerRequest request) {
        PocketLocator locator = new PocketLocator(
                request.pathVariable("walletId"),
                request.pathVariable("pocketId"));
        return transactionUseCase.getHighestAmountTransactionByPocketUseCase(locator)
                .doFirst(() -> log.info("Received request to get highest amount transaction from pocket {} in wallet {}",
                        locator.pocketId(), locator.walletId()))
                .flatMap(transaction -> ServerResponse.ok().bodyValue(transactionMapper.toResponse(transaction)))
                .doOnError(error -> log.error("Error getting highest amount transaction", error));
    }

    private static TransactionLocator transactionLocatorFrom(ServerRequest request) {
        return new TransactionLocator(
                request.pathVariable("walletId"),
                request.pathVariable("pocketId"),
                request.pathVariable("transactionId"));
    }
}
