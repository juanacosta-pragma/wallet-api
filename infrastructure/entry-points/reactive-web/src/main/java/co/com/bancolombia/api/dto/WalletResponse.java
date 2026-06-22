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
public class WalletResponse {
    @Schema(description = "Wallet unique identifier", example = "507f1f77bcf86cd799439011")
    private String id;
    @Schema(description = "UserID Wallet owner", example = "154674ds00Ls454sds564s4351")
    private String userId;
    @Schema(description = "List of Pockets")
    private List<PocketResponse> pocketes;
}

