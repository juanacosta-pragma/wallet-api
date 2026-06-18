package co.com.bancolombia.model.pocket;
import co.com.bancolombia.model.transaction.Transaction;
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
public class Pocket {
    private String id;
    private String walletId;
    private String name;
    private BigDecimal balance;
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();
}
