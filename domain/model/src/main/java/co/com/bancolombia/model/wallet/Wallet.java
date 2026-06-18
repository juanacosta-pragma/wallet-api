package co.com.bancolombia.model.wallet;
import co.com.bancolombia.model.pocket.Pocket;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Wallet {
    private String id;
    private String userId;
    private BigDecimal totalBalance;
    @Builder.Default
    private List<Pocket> pockets = new ArrayList<>();
}
