package co.com.bancolombia.mongo.pocket;

import co.com.bancolombia.model.pocket.Pocket;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MongoRepositoryPocketAdapterTest {

    @Mock
    private MongoDBPocketRepository mongoDBRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private MongoRepositoryPocketAdapter adapter;

    @Test
    void getAllPocketes_success() {
        Pocket pocket = Pocket.builder().id("p1").name("Savings").transactions(new ArrayList<>()).build();
        when(mongoDBRepository.findAll()).thenReturn(Flux.just(pocket));
        when(objectMapper.map(any(), any())).thenReturn(pocket);

        StepVerifier.create(adapter.getAllPocketes())
                .expectNext(pocket)
                .verifyComplete();
    }

    @Test
    void getAllPocketes_empty() {
        when(mongoDBRepository.findAll()).thenReturn(Flux.empty());

        StepVerifier.create(adapter.getAllPocketes())
                .verifyComplete();
    }

    @Test
    void getAllPocketes_multiplePockets() {
        Pocket p1 = Pocket.builder().id("p1").name("S1").transactions(new ArrayList<>()).build();
        Pocket p2 = Pocket.builder().id("p2").name("S2").transactions(new ArrayList<>()).build();

        when(mongoDBRepository.findAll()).thenReturn(Flux.just(p1, p2));
        when(objectMapper.map(any(), any())).thenReturn(p1).thenReturn(p2);

        StepVerifier.create(adapter.getAllPocketes())
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void deleteById_success() {
        when(mongoDBRepository.deleteById("p1")).thenReturn(Mono.empty());

        StepVerifier.create(adapter.deleteById("p1"))
                .verifyComplete();
    }

    @Test
    void findById_existing() {
        Pocket pocket = Pocket.builder().id("p1").name("Savings").transactions(new ArrayList<>()).build();
        when(mongoDBRepository.findById("p1")).thenReturn(Mono.just(pocket));
        when(objectMapper.map(any(), any())).thenReturn(pocket);

        StepVerifier.create(adapter.findById("p1"))
                .expectNext(pocket)
                .verifyComplete();
    }

    @Test
    void save_success() {
        Pocket pocket = Pocket.builder().id("p1").name("Savings").transactions(new ArrayList<>()).build();
        when(objectMapper.map(any(), any())).thenReturn(pocket);
        when(mongoDBRepository.save(any())).thenReturn(Mono.just(pocket));

        StepVerifier.create(adapter.save(pocket))
                .expectNext(pocket)
                .verifyComplete();
    }

    @Test
    void saveAll_success() {
        Pocket p1 = Pocket.builder().id("p1").name("S1").transactions(new ArrayList<>()).build();
        Pocket p2 = Pocket.builder().id("p2").name("S2").transactions(new ArrayList<>()).build();

        when(objectMapper.map(any(), any())).thenReturn(p1).thenReturn(p2);
        when(mongoDBRepository.saveAll(Collections.singleton(any()))).thenReturn(Flux.just(p1, p2));

        StepVerifier.create(adapter.saveAll(Flux.just(p1, p2)))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void findByExample_success() {
        Pocket example = Pocket.builder().id("p1").name("Savings").transactions(new ArrayList<>()).build();
        when(objectMapper.map(any(), any())).thenReturn(example);
        when(mongoDBRepository.findAll((Example<Pocket>) any())).thenReturn(Flux.just(example));

        StepVerifier.create(adapter.findByExample(example))
                .expectNext(example)
                .verifyComplete();
    }
}

