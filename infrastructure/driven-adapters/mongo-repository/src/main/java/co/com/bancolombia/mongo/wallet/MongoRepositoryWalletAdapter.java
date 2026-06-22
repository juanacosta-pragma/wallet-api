package co.com.bancolombia.mongo.wallet;

import co.com.bancolombia.model.wallet.Wallet;
import co.com.bancolombia.model.wallet.gateways.WalletRepository;
import co.com.bancolombia.mongo.helper.AdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class MongoRepositoryWalletAdapter extends AdapterOperations<Wallet, Wallet, String, MongoDBWalletRepository>
        implements WalletRepository {

    public MongoRepositoryWalletAdapter(MongoDBWalletRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Wallet.class));
    }

    @Override
    public Flux<Wallet> getAllWallets() {
        return super.findAll();
    }

    @Override
    public Mono<Wallet> findById(String id) {
        return super.findById(id);
    }

    @Override
    public Mono<Wallet> save(Wallet wallet) {
        return super.save(wallet);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return super.deleteById(id);
    }
}

