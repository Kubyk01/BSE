package bce.com.salonshub.port.primary;

import bce.com.salonshub.domain.Salon;
import bce.com.salonshub.domain.SalonId;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface SalonIngestionPort {

    Mono<Void> ingestSalon(Salon salon);
    Mono<Void> ingestSalons(List<Salon> salons);
    Mono<Salon> updateSalon(SalonId id, Salon updatedSalon);
    Mono<Void> deleteSalon(SalonId id);
    Mono<Void> deleteSalonsByFields(Map<String, String> fields);
}