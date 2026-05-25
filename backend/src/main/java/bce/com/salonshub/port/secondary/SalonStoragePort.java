package bce.com.salonshub.port.secondary;

import bce.com.salonshub.domain.Salon;
import bce.com.salonshub.domain.SalonId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface SalonStoragePort {
    Mono<Salon> save(Salon salon);
    Mono<Salon> findById(SalonId id);
    Flux<Salon> findAll();
    Mono<Void> deleteById(SalonId id);
    Mono<Void> deleteByFields(Map<String, String> fields);
    Flux<Salon> findByFields(Map<String, String> fields);
    Mono<Salon> update(SalonId id, Salon salon);
}