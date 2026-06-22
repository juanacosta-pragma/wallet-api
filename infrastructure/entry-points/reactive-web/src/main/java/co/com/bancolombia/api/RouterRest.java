package co.com.bancolombia.api;

import co.com.bancolombia.api.handler.PocketHandler;
import co.com.bancolombia.api.dto.PocketRequest;
import co.com.bancolombia.api.dto.PocketResponse;
import co.com.bancolombia.api.handler.WalletHandler;
import co.com.bancolombia.api.dto.WalletRequest;
import co.com.bancolombia.api.dto.WalletResponse;
import co.com.bancolombia.api.handler.TransactionHandler;
import co.com.bancolombia.api.dto.TransactionRequest;
import co.com.bancolombia.api.dto.TransactionResponse;
import co.com.bancolombia.api.dto.AmountUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;

@Configuration
public class RouterRest {

    // =====================================================================
    //  WALLET
    // =====================================================================
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/wallets/getAll",
                    method = RequestMethod.GET,
                    beanClass = WalletHandler.class,
                    beanMethod = "getAllWallets",
                    operation = @Operation(
                            operationId = "getAllWallets",
                            tags = {"Wallets"},
                            summary = "Get all wallets",
                            description = "Retrieves all registered wallets with their pocketes and transactions.",
                            responses = @ApiResponse(
                                    responseCode = "200",
                                    description = "Wallets retrieved successfully",
                                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                            array = @ArraySchema(
                                                    schema = @Schema(implementation = WalletResponse.class))))
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/wallets/create",
                    method = RequestMethod.POST,
                    beanClass = WalletHandler.class,
                    beanMethod = "createWallet",
                    operation = @Operation(
                            operationId = "createWallet",
                            tags = {"Wallets"},
                            summary = "Create a new wallet",
                            description = "Creates a new wallet with the provided name. Returns the created wallet.",
                            requestBody = @RequestBody(required = true,
                                    content = @Content(
                                            schema = @Schema(implementation = WalletRequest.class),
                                            examples = @ExampleObject(name = "Pizza Company",
                                                    value = "{\n  \"name\": \"Pizza Company\"\n}"))),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Wallet created successfully",
                                            content = @Content(schema = @Schema(implementation = WalletResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Validation error: name is blank or null")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/wallets/getById/{id}",
                    method = RequestMethod.GET,
                    beanClass = WalletHandler.class,
                    beanMethod = "getWalletById",
                    operation = @Operation(
                            operationId = "getWalletById",
                            tags = {"Wallets"},
                            summary = "Get wallet by ID",
                            description = "Retrieves a single wallet by its identifier.",
                            parameters = @Parameter(name = "id", description = "Wallet identifier", required = true,
                                    example = "507f1f77bcf86cd799439011"),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Wallet retrieved successfully",
                                            content = @Content(schema = @Schema(implementation = WalletResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Wallet not found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/wallets/update/{id}",
                    method = RequestMethod.PUT,
                    beanClass = WalletHandler.class,
                    beanMethod = "updateWalletName",
                    operation = @Operation(
                            operationId = "updateWalletName",
                            tags = {"Wallets"},
                            summary = "Update wallet name",
                            description = "Updates the name of an existing wallet.",
                            parameters = @Parameter(name = "id", description = "Wallet identifier", required = true),
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = WalletRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Wallet updated successfully",
                                            content = @Content(schema = @Schema(implementation = WalletResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Validation error: name is blank or null"),
                                    @ApiResponse(responseCode = "404", description = "Wallet not found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/wallets/delete/{id}",
                    method = RequestMethod.DELETE,
                    beanClass = WalletHandler.class,
                    beanMethod = "deleteWallet",
                    operation = @Operation(
                            operationId = "deleteWallet",
                            tags = {"Wallets"},
                            summary = "Delete wallet",
                            description = "Deletes a wallet by its identifier.",
                            parameters = @Parameter(name = "id", description = "Wallet identifier", required = true),
                            responses = {
                                    @ApiResponse(responseCode = "204", description = "Wallet deleted successfully"),
                                    @ApiResponse(responseCode = "404", description = "Wallet not found")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerWalletFunction(WalletHandler handler) {
        return RouterFunctions.route()
                .nest(RequestPredicates.path("/api/v1/wallets"), builder -> builder
                        .GET("/getAll", handler::getAllWallets)
                        .POST("/create", contentType(MediaType.APPLICATION_JSON), handler::createWallet)
                        .GET("/getById/{id}", handler::getWalletById)
                        .PUT("/update/{id}", contentType(MediaType.APPLICATION_JSON), handler::updateWalletName)
                        .DELETE("/delete/{id}", handler::deleteWallet))
                .build();
    }

    // =====================================================================
    //  POCKET
    // =====================================================================
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/pocketes/get/wallet/{walletId}/pocket/{pocketId}",
                    method = RequestMethod.GET,
                    beanClass = PocketHandler.class,
                    beanMethod = "getPocket",
                    operation = @Operation(
                            operationId = "getPocket",
                            tags = {"Pocketes"},
                            summary = "Get a pocket by id within a wallet",
                            parameters = {
                                    @Parameter(name = "walletId", description = "Wallet identifier", required = true),
                                    @Parameter(name = "pocketId", description = "Pocket identifier", required = true)
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Pocket retrieved successfully",
                                            content = @Content(schema = @Schema(implementation = PocketResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Wallet or Pocket not found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/pocketes/create/wallet/{walletId}",
                    method = RequestMethod.POST,
                    beanClass = PocketHandler.class,
                    beanMethod = "addPocket",
                    operation = @Operation(
                            operationId = "addPocket",
                            tags = {"Pocketes"},
                            summary = "Add a pocket to a wallet",
                            parameters = @Parameter(name = "walletId", description = "Wallet identifier", required = true),
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = PocketRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Pocket created successfully",
                                            content = @Content(schema = @Schema(implementation = WalletResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Validation error"),
                                    @ApiResponse(responseCode = "404", description = "Wallet not found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/pocketes/update/wallet/{walletId}/pocket/{pocketId}",
                    method = RequestMethod.PUT,
                    beanClass = PocketHandler.class,
                    beanMethod = "updatePocketName",
                    operation = @Operation(
                            operationId = "updatePocketName",
                            tags = {"Pocketes"},
                            summary = "Update pocket name",
                            parameters = {
                                    @Parameter(name = "walletId", description = "Wallet identifier", required = true),
                                    @Parameter(name = "pocketId", description = "Pocket identifier", required = true)
                            },
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = PocketRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Pocket updated successfully",
                                            content = @Content(schema = @Schema(implementation = WalletResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Validation error"),
                                    @ApiResponse(responseCode = "404", description = "Wallet or Pocket not found")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerPocketFunction(PocketHandler handler) {
        return RouterFunctions.route()
                .nest(RequestPredicates.path("/api/v1/pockets"), builder -> builder
                        .GET("/get/wallet/{walletId}/pocket/{pocketId}", handler::getPocket)
                        .POST("/create/wallet/{walletId}", contentType(MediaType.APPLICATION_JSON), handler::addPocket)
                        .PUT("/update/wallet/{walletId}/pocket/{pocketId}", contentType(MediaType.APPLICATION_JSON), handler::updatePocketName))
                .build();
    }

    // =====================================================================
    //  TRANSACTION
    // =====================================================================
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/transactions/create/wallet/{walletId}/pocket/{pocketId}",
                    method = RequestMethod.POST,
                    beanClass = TransactionHandler.class,
                    beanMethod = "addTransaction",
                    operation = @Operation(
                            operationId = "addTransaction",
                            tags = {"Transactions"},
                            summary = "Add a transaction to a pocket",
                            parameters = {
                                    @Parameter(name = "walletId", description = "Wallet identifier", required = true),
                                    @Parameter(name = "pocketId", description = "Pocket identifier", required = true)
                            },
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = TransactionRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Transaction created successfully",
                                            content = @Content(schema = @Schema(implementation = WalletResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Validation error"),
                                    @ApiResponse(responseCode = "404", description = "Wallet or Pocket not found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/transactions/get/wallet/{walletId}/pocket/{pocketId}/transaction/{transactionId}",
                    method = RequestMethod.GET,
                    beanClass = TransactionHandler.class,
                    beanMethod = "getTransaction",
                    operation = @Operation(
                            operationId = "getTransaction",
                            tags = {"Transactions"},
                            summary = "Get a transaction by id",
                            parameters = {
                                    @Parameter(name = "walletId", required = true),
                                    @Parameter(name = "pocketId", required = true),
                                    @Parameter(name = "transactionId", required = true)
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Transaction retrieved successfully",
                                            content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Wallet, Pocket or Transaction not found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/transactions/delete/wallet/{walletId}/pocket/{pocketId}/transaction/{transactionId}",
                    method = RequestMethod.DELETE,
                    beanClass = TransactionHandler.class,
                    beanMethod = "deleteTransaction",
                    operation = @Operation(
                            operationId = "deleteTransaction",
                            tags = {"Transactions"},
                            summary = "Delete a transaction from a pocket",
                            parameters = {
                                    @Parameter(name = "walletId", required = true),
                                    @Parameter(name = "pocketId", required = true),
                                    @Parameter(name = "transactionId", required = true)
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Transaction deleted successfully",
                                            content = @Content(schema = @Schema(implementation = WalletResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Wallet, Pocket or Transaction not found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/transactions/transactionAmount/wallet/{walletId}/pocket/{pocketId}/transaction/{transactionId}/amount",
                    method = RequestMethod.PUT,
                    beanClass = TransactionHandler.class,
                    beanMethod = "updateTransactionAmount",
                    operation = @Operation(
                            operationId = "updateTransactionAmount",
                            tags = {"Transactions"},
                            summary = "Update transaction amount",
                            parameters = {
                                    @Parameter(name = "walletId", required = true),
                                    @Parameter(name = "pocketId", required = true),
                                    @Parameter(name = "transactionId", required = true)
                            },
                            requestBody = @RequestBody(required = true,
                                    content = @Content(
                                            schema = @Schema(implementation = AmountUpdateRequest.class),
                                            examples = @ExampleObject(name = "New Amount",
                                                    value = "{\n  \"amount\": 150.000\n}"))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Amount updated successfully",
                                            content = @Content(schema = @Schema(implementation = WalletResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Wallet, Pocket or Transaction not found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/transactions/transactionName/wallet/{walletId}/pocket/{pocketId}/transaction/{transactionId}",
                    method = RequestMethod.PUT,
                    beanClass = TransactionHandler.class,
                    beanMethod = "updateTransactionName",
                    operation = @Operation(
                            operationId = "updateTransactionName",
                            tags = {"Transactions"},
                            summary = "Update transaction name",
                            parameters = {
                                    @Parameter(name = "walletId", required = true),
                                    @Parameter(name = "pocketId", required = true),
                                    @Parameter(name = "transactionId", required = true)
                            },
                            requestBody = @RequestBody(required = true,
                                    content = @Content(schema = @Schema(implementation = TransactionRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Name updated successfully",
                                            content = @Content(schema = @Schema(implementation = WalletResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Wallet, Pocket or Transaction not found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/transactions/getHighAmount/wallet/{walletId}/pocket/{pocketId}/transaction/",
                    method = RequestMethod.GET,
                    beanClass = TransactionHandler.class,
                    beanMethod = "getHighestAmountTransactionByPocket",
                    operation = @Operation(
                            operationId = "getHighestAmountTransactionByPocket",
                            tags = {"Transactions"},
                            summary = "Get the transaction with the highest amount in a pocket",
                            parameters = {
                                    @Parameter(name = "walletId", required = true),
                                    @Parameter(name = "pocketId", required = true)
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Transaction retrieved successfully",
                                            content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Pocket has no transactions or not found")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerTransactionFunction(TransactionHandler handler) {
        return RouterFunctions.route()
                .nest(RequestPredicates.path("/api/v1/transactions"), builder -> builder
                        .POST("/create/wallet/{walletId}/pocket/{pocketId}", contentType(MediaType.APPLICATION_JSON), handler::addTransaction)
                        .GET("/get/wallet/{walletId}/pocket/{pocketId}/transaction/{transactionId}", handler::getTransaction)
                        .DELETE("/delete/wallet/{walletId}/pocket/{pocketId}/transaction/{transactionId}", handler::deleteTransaction)
                        .PUT("/transactionAmount/wallet/{walletId}/pocket/{pocketId}/transaction/{transactionId}/amount",
                                contentType(MediaType.APPLICATION_JSON), handler::updateTransactionAmount)
                        .PUT("/transactionName/wallet/{walletId}/pocket/{pocketId}/transaction/{transactionId}",
                                contentType(MediaType.APPLICATION_JSON), handler::updateTransactionName)
                        .GET("/getHighAmount/wallet/{walletId}/pocket/{pocketId}/transaction/", handler::getHighestAmountTransactionByPocket))
                .build();
    }
}
