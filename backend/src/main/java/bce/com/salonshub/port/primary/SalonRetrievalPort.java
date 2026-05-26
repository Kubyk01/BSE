package bce.com.salonshub.port.primary;

import bce.com.salonshub.domain.Salon;
import bce.com.salonshub.domain.SalonId;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface SalonRetrievalPort {
    Mono<Salon> findSalonById(SalonId id);
    Flux<Salon> findAllSalons(Pageable pageable);
    Flux<Salon> findSalonsByFields(Map<String, String> fields, Pageable pageable);
}