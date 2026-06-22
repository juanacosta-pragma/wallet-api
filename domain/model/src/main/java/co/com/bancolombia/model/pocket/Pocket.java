package co.com.bancolombia.model.pocket;
import co.com.bancolombia.model.transaction.Transaction;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@With
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Pocket {

    private String id;
    private String name;
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();
}
