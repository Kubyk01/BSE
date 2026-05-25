package bce.com.salonshub.port.primary;

import bce.com.salonshub.domain.Salon;
import bce.com.salonshub.domain.SalonId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface SalonRetrievalPort {
    Flux<Salon> findAllSalons();
    Mono<Salon> findSalonById(SalonId id);
    Flux<Salon> findSalonsByFields(Map<String, String> fields);
}