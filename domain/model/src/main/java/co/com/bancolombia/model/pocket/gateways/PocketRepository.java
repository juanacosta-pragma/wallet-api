package co.com.bancolombia.model.pocket.gateways;

import co.com.bancolombia.model.pocket.Pocket;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PocketRepository {
    Flux<Pocket> getAllPocketes();
    Mono<Void> deleteById(String id);
}

