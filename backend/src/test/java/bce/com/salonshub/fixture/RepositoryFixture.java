package bce.com.salonshub.fixture;

import bce.com.salonshub.adapter.secondary.salonstorage.SalonRepository;
import bce.com.salonshub.adapter.secondary.salonstorage.model.SalonDao;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RepositoryFixture {

    private final Map<UUID, SalonDao> salonStore = new ConcurrentHashMap<>();

    public SalonRepository salonRepository() {
        return new InMemorySalonRepository(salonStore);
    }

    static class InMemorySalonRepository implements SalonRepository {
        private final Map<UUID, SalonDao> store;

        InMemorySalonRepository(Map<UUID, SalonDao> store) {
            this.store = store;
        }

        @Override
        public <S extends SalonDao> Mono<S> save(S entity) {
            store.put(entity.getId(), entity);
            return Mono.just(entity);
        }

        @Override
        public <S extends SalonDao> Flux<S> saveAll(Iterable<S> entities) {
            return Flux.fromIterable(entities).flatMap(this::save);
        }

        @Override
        public <S extends SalonDao> Flux<S> saveAll(org.reactivestreams.Publisher<S> entityStream) {
            return Flux.from(entityStream).flatMap(this::save);
        }

        @Override
        public Mono<SalonDao> findById(UUID id) {
            return Mono.justOrEmpty(store.get(id));
        }

        @Override
        public Mono<SalonDao> findById(org.reactivestreams.Publisher<UUID> id) {
            return Mono.from(id).flatMap(this::findById);
        }

        @Override
        public Mono<Boolean> existsById(UUID id) {
            return Mono.just(store.containsKey(id));
        }

        @Override
        public Mono<Boolean> existsById(org.reactivestreams.Publisher<UUID> id) {
            return Mono.from(id).flatMap(this::existsById);
        }

        @Override
        public Flux<SalonDao> findAll() {
            return Flux.fromIterable(store.values());
        }

        @Override
        public Flux<SalonDao> findAllById(Iterable<UUID> ids) {
            return Flux.fromIterable(store.values())
                .filter(dao -> {
                    for (UUID id : ids) {
                        if (dao.getId().equals(id)) return true;
                    }
                    return false;
                });
        }

        @Override
        public Flux<SalonDao> findAllById(org.reactivestreams.Publisher<UUID> idStream) {
            return Flux.from(idStream).collectList().flatMapMany(this::findAllById);
        }

        @Override
        public Mono<Long> count() {
            return Mono.just((long) store.size());
        }

        @Override
        public Mono<Void> deleteById(UUID id) {
            store.remove(id);
            return Mono.empty();
        }

        @Override
        public Mono<Void> deleteById(org.reactivestreams.Publisher<UUID> id) {
            return Mono.from(id).doOnNext(this::deleteById).then();
        }

        @Override
        public Mono<Void> delete(SalonDao entity) {
            store.remove(entity.getId());
            return Mono.empty();
        }

        @Override
        public Mono<Void> deleteAllById(Iterable<? extends UUID> ids) {
            ids.forEach(store::remove);
            return Mono.empty();
        }

        @Override
        public Mono<Void> deleteAll(Iterable<? extends SalonDao> entities) {
            entities.forEach(dao -> store.remove(dao.getId()));
            return Mono.empty();
        }

        @Override
        public Mono<Void> deleteAll(org.reactivestreams.Publisher<? extends SalonDao> entityStream) {
            return Flux.from(entityStream).doOnNext(dao -> store.remove(dao.getId())).then();
        }

        @Override
        public Mono<Void> deleteAll() {
            store.clear();
            return Mono.empty();
        }
    }
}