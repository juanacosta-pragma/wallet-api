package co.com.bancolombia.model.wallet;
import co.com.bancolombia.model.pocket.Pocket;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@With
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Wallet {
    private String id;
    private String userId;
    @Builder.Default
    private List<Pocket> pocketes = new ArrayList<>();
}
