package co.com.bancolombia.mongo.pocket;

import co.com.bancolombia.model.pocket.Pocket;
import co.com.bancolombia.model.pocket.gateways.PocketRepository;
import co.com.bancolombia.mongo.helper.AdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MongoRepositoryPocketAdapter extends AdapterOperations<Pocket, Pocket, String, MongoDBPocketRepository>
        implements PocketRepository {

    public MongoRepositoryPocketAdapter(MongoDBPocketRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Pocket.class));
    }

    @Override
    public Flux<Pocket> getAllPocketes() {
        return super.findAll();
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return super.deleteById(id);
    }
}

