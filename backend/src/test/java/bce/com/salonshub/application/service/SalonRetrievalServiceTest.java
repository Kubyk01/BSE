package bce.com.salonshub.application.service;

import bce.com.salonshub.domain.PriceRange;
import bce.com.salonshub.domain.Salon;
import bce.com.salonshub.domain.SalonId;
import bce.com.salonshub.fixture.PortFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SalonRetrievalServiceTest {

    private SalonRetrievalService retrievalService;
    private SalonIngestionService ingestionService;

    @BeforeEach
    void setUp() {
        PortFixture portFixture = PortFixture.withNewInMemoryDatabase();
        retrievalService = (SalonRetrievalService) portFixture.salonRetrievalPort();
        ingestionService = (SalonIngestionService) portFixture.salonIngestionPort();
    }

    @Test
    void findAllSalons_shouldReturnAllStored() {
        Salon s1 = Salon.builder().name("Salon A").address("Addr1").district("Dist1").priceRange(new PriceRange(0,0)).build();
        Salon s2 = Salon.builder().name("Salon B").address("Addr2").district("Dist2").priceRange(new PriceRange(0,0)).build();

        ingestionService.ingestSalon(s1).block();
        ingestionService.ingestSalon(s2).block();

        StepVerifier.create(retrievalService.findAllSalons().collectList())
            .assertNext(list -> assertThat(list).hasSize(2))
            .verifyComplete();
    }

    @Test
    void findSalonById_shouldReturnCorrect() {
        Salon salon = Salon.builder()
            .name("Unique")
            .address("Addr")
            .district("Dist")
            .priceRange(new PriceRange(0,0))
            .build();
        ingestionService.ingestSalon(salon).block();

        StepVerifier.create(retrievalService.findSalonById(salon.getId()))
            .expectNextMatches(s -> s.getName().equals("Unique"))
            .verifyComplete();
    }

    @Test
    void findSalonById_notFound_shouldReturnEmpty() {
        StepVerifier.create(retrievalService.findSalonById(new SalonId(UUID.randomUUID())))
            .verifyComplete();
    }
}