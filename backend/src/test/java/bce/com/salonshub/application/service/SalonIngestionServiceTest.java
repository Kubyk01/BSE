package bce.com.salonshub.application.service;

import bce.com.salonshub.domain.PriceRange;
import bce.com.salonshub.domain.Salon;
import bce.com.salonshub.domain.SalonId;
import bce.com.salonshub.port.secondary.SalonStoragePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SalonIngestionServiceTest {

    @Mock
    private SalonStoragePort storagePort;

    @InjectMocks
    private SalonIngestionService ingestionService;

    private Salon salon;

    @BeforeEach
    void setUp() {
        salon = Salon.builder()
            .name("Test Salon")
            .address("123 Main St")
            .district("Downtown")
            .phone("+48 734 734 734")
            .website(Map.of("facebook", "fb.com/test"))
            .services(List.of("Haircut"))
            .priceRange(new PriceRange(10.0, 50.0))
            .rating(4.5)
            .numberOfReviews(10)
            .build();
    }

    @Test
    void ingestSalon_shouldNormalizePhoneAndCallSave() {
        when(storagePort.save(any(Salon.class)))
            .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(ingestionService.ingestSalon(salon))
            .verifyComplete();

        ArgumentCaptor<Salon> captor = ArgumentCaptor.forClass(Salon.class);
        verify(storagePort).save(captor.capture());

        Salon saved = captor.getValue();
        assertThat(saved.getPhone()).isEqualTo("+48734734734");
    }

    @Test
    void ingestSalons_shouldNormalizePhonesForAll() {
        Salon s1 = Salon.builder().name("S1").address("A").district("D")
            .phone("+1 234 567 8900").priceRange(new PriceRange(0,0)).build();
        Salon s2 = Salon.builder().name("S2").address("A").district("D")
            .phone("+44 20 7946 0958").priceRange(new PriceRange(0,0)).build();

        when(storagePort.save(any(Salon.class)))
            .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(ingestionService.ingestSalons(List.of(s1, s2)))
            .verifyComplete();

        ArgumentCaptor<Salon> captor = ArgumentCaptor.forClass(Salon.class);
        verify(storagePort, times(2)).save(captor.capture());

        List<Salon> captured = captor.getAllValues();
        assertThat(captured).hasSize(2);
        assertThat(captured.get(0).getPhone()).isEqualTo("+12345678900");
        assertThat(captured.get(1).getPhone()).isEqualTo("+442079460958");
    }

    @Test
    void updateSalon_shouldNormalizePhoneAndCallUpdate() {
        SalonId id = new SalonId(UUID.randomUUID());
        Salon updatedSalon = Salon.builder()
            .name("Updated")
            .address("New Addr")
            .district("New Dist")
            .phone("+987 654 321")
            .priceRange(new PriceRange(5, 10))
            .build();

        when(storagePort.update(any(SalonId.class), any(Salon.class)))
            .thenAnswer(inv -> Mono.just(inv.getArgument(1)));

        StepVerifier.create(ingestionService.updateSalon(id, updatedSalon))
            .expectNextMatches(s -> s.getPhone().equals("+987654321"))
            .verifyComplete();

        ArgumentCaptor<Salon> captor = ArgumentCaptor.forClass(Salon.class);
        verify(storagePort).update(any(SalonId.class), captor.capture());
        assertThat(captor.getValue().getPhone()).isEqualTo("+987654321");
    }

    @Test
    void deleteSalon_shouldCallDeleteById() {
        SalonId id = new SalonId(UUID.randomUUID());
        when(storagePort.deleteById(any(SalonId.class))).thenReturn(Mono.empty());

        StepVerifier.create(ingestionService.deleteSalon(id))
            .verifyComplete();

        verify(storagePort).deleteById(id);
    }

    @Test
    void deleteSalonsByFields_shouldCallDeleteByFields() {
        Map<String, String> fields = Map.of("district", "Downtown");
        when(storagePort.deleteByFields(any())).thenReturn(Mono.empty());

        StepVerifier.create(ingestionService.deleteSalonsByFields(fields))
            .verifyComplete();

        verify(storagePort).deleteByFields(fields);
    }
}