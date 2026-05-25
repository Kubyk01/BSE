package bce.com.salonshub.application.service;

import bce.com.salonshub.domain.Salon;
import bce.com.salonshub.domain.SalonId;
import bce.com.salonshub.port.primary.SalonRetrievalPort;
import bce.com.salonshub.port.secondary.SalonStoragePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SalonRetrievalService implements SalonRetrievalPort {

    private final SalonStoragePort storagePort;

    @Override
    public Flux<Salon> findAllSalons() {
        return storagePort.findAll();
    }

    @Override
    public Mono<Salon> findSalonById(SalonId id) {
        return storagePort.findById(id);
    }

    @Override
    public Flux<Salon> findSalonsByFields(Map<String, String> fields) {
        return storagePort.findByFields(fields);
    }
}