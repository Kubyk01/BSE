package bce.com.salonshub.adapter.driving.rest;

import bce.com.salonshub.adapter.driving.rest.model.dto.SalonDTO;
import bce.com.salonshub.domain.PriceRange;
import bce.com.salonshub.domain.Salon;
import bce.com.salonshub.domain.SalonId;
import bce.com.salonshub.port.primary.SalonIngestionPort;
import bce.com.salonshub.port.primary.SalonRetrievalPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(SalonController.class)
class SalonControllerTest {

    @Autowired private WebTestClient webClient;
    @MockitoBean private SalonRetrievalPort retrievalPort;
    @MockitoBean private SalonIngestionPort ingestionPort;

    @Test
    void getAllSalons_shouldReturnOk() {
        Salon salon = createSalon(UUID.randomUUID());
        when(retrievalPort.findAllSalons(any(Pageable.class)))
            .thenReturn(Flux.just(salon));
        webClient.get().uri("/salons")
            .exchange()
            .expectStatus().isOk()
            .expectBody().jsonPath("$[0].name").isEqualTo("Test Salon");
    }

    @Test
    void getSalonById_found_shouldReturnOk() {
        UUID id = UUID.randomUUID();
        when(retrievalPort.findSalonById(new SalonId(id)))
            .thenReturn(Mono.just(createSalon(id)));
        webClient.get().uri("/salons/{id}", id)
            .exchange()
            .expectStatus().isOk()
            .expectBody().jsonPath("$.name").isEqualTo("Test Salon");
    }

    @Test
    void getSalonById_notFound_shouldReturnNotFound() {
        UUID id = UUID.randomUUID();
        when(retrievalPort.findSalonById(new SalonId(id)))
            .thenReturn(Mono.empty());
        webClient.get().uri("/salons/{id}", id)
            .exchange()
            .expectStatus().isNotFound();
    }

    @Test
    void updateSalon_shouldReturnOk() {
        UUID id = UUID.randomUUID();
        SalonDTO dto = new SalonDTO("New", "Addr", "Dist", "+48123456789", null, null, 10.0, 100.0, 4.5, 5);
        when(ingestionPort.updateSalon(any(), any()))
            .thenReturn(Mono.just(createSalon(id)));
        webClient.put().uri("/salons/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isOk();
    }

    @Test
    void ingestSalon_shouldAccept() {
        SalonDTO dto = new SalonDTO("New", "Addr", "Dist", "+48123456789", null, null, null, null, null, null);
        when(ingestionPort.ingestSalon(any()))
            .thenReturn(Mono.empty());
        webClient.post().uri("/salons/ingest")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(dto)
            .exchange()
            .expectStatus().isAccepted();
    }

    @Test
    void ingestBatch_shouldAccept() {
        List<SalonDTO> batch = List.of(new SalonDTO("A", "Addr1", "Dist1", "+48123456789", null, null, null, null, null, null));
        when(ingestionPort.ingestSalons(any()))
            .thenReturn(Mono.empty());
        webClient.post().uri("/salons/ingest/batch")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(batch)
            .exchange()
            .expectStatus().isAccepted();
    }

    @Test
    void deleteById_shouldReturnNoContent() {
        UUID id = UUID.randomUUID();
        when(ingestionPort.deleteSalon(new SalonId(id)))
            .thenReturn(Mono.empty());
        webClient.delete().uri("/salons/{id}", id)
            .exchange()
            .expectStatus().isNoContent();
    }

    @Test
    void deleteByFields_shouldReturnNoContent() {
        when(ingestionPort.deleteSalonsByFields(Map.of("district", "Center")))
            .thenReturn(Mono.empty());
        webClient.delete().uri("/salons?district=Center")
            .exchange()
            .expectStatus().isNoContent();
    }

    @Test
    void dynamicFind_shouldReturnFiltered() {
        Salon salon = createSalon(UUID.randomUUID());
        when(retrievalPort.findSalonsByFields(eq(Map.of("rating", "4.5")), any(Pageable.class)))
            .thenReturn(Flux.just(salon));
        webClient.get().uri("/salons/filter?rating=4.5")
            .exchange()
            .expectStatus().isOk()
            .expectBody().jsonPath("$[0].name").isEqualTo("Test Salon");
    }

    private Salon createSalon(UUID id) {
        return Salon.builder()
            .id(new SalonId(id))
            .name("Test Salon")
            .address("Addr")
            .district("Dist")
            .phone("+123")
            .priceRange(new PriceRange(0,0))
            .build();
    }
}