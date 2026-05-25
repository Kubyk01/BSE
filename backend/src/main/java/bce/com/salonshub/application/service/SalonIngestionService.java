package bce.com.salonshub.application.service;

import bce.com.salonshub.domain.Salon;
import bce.com.salonshub.domain.SalonId;
import bce.com.salonshub.port.primary.SalonIngestionPort;
import bce.com.salonshub.port.secondary.SalonStoragePort;
import bce.com.salonshub.util.PhoneNumberNormalizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalonIngestionService implements SalonIngestionPort {

    private final SalonStoragePort storagePort;

    @Override
    public Mono<Void> ingestSalon(Salon salon) {
        if (salon.getId() == null) {
            salon.setId(new SalonId(UUID.randomUUID()));
        }
        normalizePhone(salon);
        return storagePort.save(salon).then();
    }

    @Override
    public Mono<Void> ingestSalons(List<Salon> salons) {
        return Flux.fromIterable(salons)
            .flatMap(this::ingestSalon)
            .then();
    }

    @Override
    public Mono<Salon> updateSalon(SalonId id, Salon updatedSalon) {
        normalizePhone(updatedSalon);
        return storagePort.update(id, updatedSalon);
    }

    @Override
    public Mono<Void> deleteSalon(SalonId id) {
        return storagePort.deleteById(id);
    }

    @Override
    public Mono<Void> deleteSalonsByFields(Map<String, String> fields) {
        return storagePort.deleteByFields(fields);
    }

    private void normalizePhone(Salon salon) {
        if (salon.getPhone() != null) {
            String normalized = PhoneNumberNormalizer.normalize(salon.getPhone());
            salon.setPhone(normalized);
        }
    }
}