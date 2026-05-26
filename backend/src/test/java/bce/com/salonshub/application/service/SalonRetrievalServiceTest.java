package bce.com.salonshub.application.service;

import bce.com.salonshub.domain.PriceRange;
import bce.com.salonshub.domain.Salon;
import bce.com.salonshub.domain.SalonId;
import bce.com.salonshub.port.secondary.SalonStoragePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SalonRetrievalServiceTest {

    @Mock
    private SalonStoragePort storagePort;

    @InjectMocks
    private SalonRetrievalService retrievalService;

    private Salon salon;
    private SalonId salonId;

    @BeforeEach
    void setUp() {
        salonId = new SalonId(UUID.randomUUID());
        salon = Salon.builder()
            .id(salonId)
            .name("Test Salon")
            .address("Addr")
            .district("Dist")
            .phone("+123")
            .priceRange(new PriceRange(0, 0))
            .build();
    }

    @Test
    void findAllSalons_shouldReturnAllFromStorage() {
        Pageable pageable = PageRequest.of(0, 10);
        when(storagePort.findAll(pageable)).thenReturn(Flux.just(salon));

        StepVerifier.create(retrievalService.findAllSalons(pageable))
            .expectNext(salon)
            .verifyComplete();
    }

    @Test
    void findSalonById_shouldReturnSalonWhenExists() {
        when(storagePort.findById(salonId)).thenReturn(Mono.just(salon));

        StepVerifier.create(retrievalService.findSalonById(salonId))
            .expectNext(salon)
            .verifyComplete();
    }

    @Test
    void findSalonById_shouldReturnEmptyWhenNotFound() {
        when(storagePort.findById(any(SalonId.class))).thenReturn(Mono.empty());

        StepVerifier.create(retrievalService.findSalonById(new SalonId(UUID.randomUUID())))
            .verifyComplete();
    }

    @Test
    void findSalonsByFields_shouldDelegateToStorage() {
        Map<String, String> fields = Map.of("district", "Dist");
        Pageable pageable = Pageable.unpaged();
        when(storagePort.findByFields(fields, pageable)).thenReturn(Flux.just(salon));

        StepVerifier.create(retrievalService.findSalonsByFields(fields, pageable))
            .expectNext(salon)
            .verifyComplete();
    }
}