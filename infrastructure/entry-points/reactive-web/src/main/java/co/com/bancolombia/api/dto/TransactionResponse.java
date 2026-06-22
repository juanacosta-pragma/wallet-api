package co.com.bancolombia.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponse {
    @Schema(description = "Transaction unique identifier", example = "507f1f77bcf86cd799439011")
    private String id;
    @Schema(description = "Transaction description", example = "Paid Laptop")
    private String description;
    @Schema(description = "Transaction amount", example = "100.00")
    private BigDecimal amount;
    @Schema(description = "Transaction creation date", example = "2024-06-22T10:30:00")
    private LocalDateTime createdAt;
}

