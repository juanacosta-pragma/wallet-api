package co.com.bancolombia.model.transaction;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@With
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Transaction {
    private String id;
    private String description;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}
