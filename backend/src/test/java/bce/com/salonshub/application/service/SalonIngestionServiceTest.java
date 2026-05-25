package bce.com.salonshub.application.service;

import bce.com.salonshub.domain.PriceRange;
import bce.com.salonshub.domain.Salon;
import bce.com.salonshub.fixture.PortFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SalonIngestionServiceTest {

    private PortFixture portFixture;
    private SalonIngestionService ingestionService;

    @BeforeEach
    void setUp() {
        portFixture = PortFixture.withNewInMemoryDatabase();
        ingestionService = (SalonIngestionService) portFixture.salonIngestionPort();
    }

    @Test
    void ingestSalon_shouldGenerateIdAndNormalizePhone() {
        Salon salon = Salon.builder()
            .name("Test Salon")
            .address("123 Main St")
            .district("Downtown")
            .phone("+48 734 734 734")
            .website(Map.of("facebook", "fb.com/test"))
            .services(java.util.List.of("Haircut"))
            .priceRange(new PriceRange(10.0, 50.0))
            .rating(4.5)
            .numberOfReviews(10)
            .build();

        StepVerifier.create(ingestionService.ingestSalon(salon))
            .verifyComplete();

        assertThat(salon.getId()).isNotNull();
        assertThat(salon.getPhone()).isEqualTo("+48734734734");
    }

    @Test
    void ingestSalons_shouldNormalizePhonesForAll() {
        Salon s1 = Salon.builder().name("S1").address("A").district("D").phone("+1 234 567 8900").priceRange(new PriceRange(0,0)).build();
        Salon s2 = Salon.builder().name("S2").address("A").district("D").phone("+44 20 7946 0958").priceRange(new PriceRange(0,0)).build();

        StepVerifier.create(ingestionService.ingestSalons(java.util.List.of(s1, s2)))
            .verifyComplete();

        assertThat(s1.getPhone()).isEqualTo("+12345678900");
        assertThat(s2.getPhone()).isEqualTo("+442079460958");
    }

    @Test
    void updateSalon_shouldUpdateExistingSalon() {
        Salon original = Salon.builder()
            .name("Original")
            .address("Addr")
            .district("Dist")
            .phone("+123")
            .priceRange(new PriceRange(1, 2))
            .build();

        StepVerifier.create(ingestionService.ingestSalon(original))
            .verifyComplete();

        Salon updated = Salon.builder()
            .name("Updated")
            .address("New Addr")
            .district("New Dist")
            .phone("+987")
            .priceRange(new PriceRange(5, 10))
            .build();

        StepVerifier.create(ingestionService.updateSalon(original.getId(), updated))
            .expectNextMatches(s -> s.getName().equals("Updated") && s.getPhone().equals("+987"))
            .verifyComplete();
    }

    @Test
    void deleteSalon_shouldRemoveSalon() {
        Salon salon = Salon.builder()
            .name("ToDelete")
            .address("Addr")
            .district("Dist")
            .priceRange(new PriceRange(0,0))
            .build();

        ingestionService.ingestSalon(salon).block();

        StepVerifier.create(ingestionService.deleteSalon(salon.getId()))
            .verifyComplete();

        StepVerifier.create(portFixture.salonRetrievalPort().findSalonById(salon.getId()))
            .verifyComplete(); // empty
    }
}