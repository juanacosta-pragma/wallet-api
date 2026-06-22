package co.com.bancolombia.mongo.pocket;

import co.com.bancolombia.model.pocket.Pocket;
import co.com.bancolombia.mongo.pocket.MongoDBPocketRepository;
import co.com.bancolombia.mongo.pocket.MongoRepositoryPocketAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class PocketRepositoryAdapterTest {

    @Mock
    private MongoDBPocketRepository repository;

    @Mock
    private ObjectMapper objectMapper;

    private MongoRepositoryPocketAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(objectMapper.map(any(), any())).thenAnswer(inv -> inv.getArgument(0));
        adapter = new MongoRepositoryPocketAdapter(repository, objectMapper);
    }

    @Test
    void getAllPocketes_delegatesToFindAll() {
        Pocket pocket = Pocket.builder().id("p1").name("Savings").build();
        when(repository.findAll()).thenReturn(Flux.just(pocket));

        StepVerifier.create(adapter.getAllPocketes())
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    void getAllPocketes_empty() {
        when(repository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(adapter.getAllPocketes())
                .verifyComplete();
    }

    @Test
    void findById_existing() {
        Pocket pocket = Pocket.builder().id("p1").name("Savings").build();
        when(repository.findById("p1")).thenReturn(Mono.just(pocket));

        StepVerifier.create(adapter.findById("p1"))
                .expectNext(pocket)
                .verifyComplete();
    }

    @Test
    void deletePocket() {
        when(repository.deleteById("p1")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.deleteById("p1"))
                .verifyComplete();
    }
}

