package co.com.bancolombia.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PocketResponse {
    @Schema(description = "Pocket unique identifier", example = "507f1f77bcf86cd799439011")
    private String id;
    @Schema(description = "Pocket name", example = "Central Pocket")
    private String name;
    @Schema(description = "List of transactions in the pocket")
    private List<TransactionResponse> transactions;
}

