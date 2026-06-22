package co.com.bancolombia.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequest {
    @Schema(description = "Transaction description", example = "Paid Laptop")
    private String description;
    @Schema(description = "Transaction amount", example = "100.00")
    private BigDecimal amount;
}

