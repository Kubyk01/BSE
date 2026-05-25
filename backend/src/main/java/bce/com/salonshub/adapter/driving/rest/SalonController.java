package bce.com.salonshub.adapter.driving.rest;

import bce.com.salonshub.adapter.driving.rest.mapper.SalonMapper;
import bce.com.salonshub.adapter.driving.rest.model.dto.SalonDTO;
import bce.com.salonshub.adapter.driving.rest.model.view.SalonView;
import bce.com.salonshub.domain.Salon;
import bce.com.salonshub.domain.SalonId;
import bce.com.salonshub.port.primary.SalonIngestionPort;
import bce.com.salonshub.port.primary.SalonRetrievalPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/salons")
@RequiredArgsConstructor
public class SalonController {

    private final SalonRetrievalPort retrievalPort;
    private final SalonIngestionPort ingestionPort;

    @GetMapping
    public Flux<SalonView> getAllSalons() {
        return retrievalPort.findAllSalons()
            .map(SalonMapper::toView);
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<SalonView>> getSalonById(@PathVariable UUID id) {
        return retrievalPort.findSalonById(new SalonId(id))
            .map(SalonMapper::toView)
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<SalonView>> updateSalon(@PathVariable UUID id,
                                                       @Valid @RequestBody SalonDTO dto) {
        Salon domain = SalonMapper.toDomain(dto);
        domain.setId(new SalonId(id));
        return ingestionPort.updateSalon(new SalonId(id), domain)
            .map(SalonMapper::toView)
            .map(ResponseEntity::ok)
            .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    @PostMapping("/ingest")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> ingestSalon(@Valid @RequestBody SalonDTO salonDTO) {
        return ingestionPort.ingestSalon(SalonMapper.toDomain(salonDTO));
    }

    @PostMapping("/ingest/batch")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> ingestBatch(@Valid @RequestBody List<SalonDTO> salons) {
        List<Salon> domains = salons.stream()
            .map(SalonMapper::toDomain)
            .collect(Collectors.toList());
        return ingestionPort.ingestSalons(domains);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteById(@PathVariable UUID id) {
        return ingestionPort.deleteSalon(new SalonId(id));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteByFields(@RequestParam Map<String, String> fields) {
        return ingestionPort.deleteSalonsByFields(fields);
    }

    @GetMapping("/filter")
    public Flux<SalonView> dynamicFind(@RequestParam Map<String, String> fields) {
        return retrievalPort.findSalonsByFields(fields)
            .map(SalonMapper::toView);
    }
}