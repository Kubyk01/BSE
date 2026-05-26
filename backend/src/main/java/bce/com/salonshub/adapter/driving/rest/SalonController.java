package bce.com.salonshub.adapter.driving.rest;

import bce.com.salonshub.adapter.driving.rest.mapper.SalonMapper;
import bce.com.salonshub.adapter.driving.rest.model.dto.SalonDTO;
import bce.com.salonshub.adapter.driving.rest.model.view.SalonView;
import bce.com.salonshub.adapter.secondary.salonstorage.SqlBuilder;
import bce.com.salonshub.domain.Salon;
import bce.com.salonshub.domain.SalonId;
import bce.com.salonshub.port.primary.SalonIngestionPort;
import bce.com.salonshub.port.primary.SalonRetrievalPort;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
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
    public Flux<SalonView> getAllSalons(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(required = false) String sort) {
        Pageable pageable = buildPageable(page, size, sort);
        return retrievalPort.findAllSalons(pageable)
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
    public Flux<SalonView> dynamicFind(
        @RequestParam Map<String, String> allParams,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(required = false) String sort) {
        Map<String, String> fields = new HashMap<>(allParams);
        fields.remove("page");
        fields.remove("size");
        fields.remove("sort");

        Pageable pageable = buildPageable(page, size, sort);
        return retrievalPort.findSalonsByFields(fields, pageable)
            .map(SalonMapper::toView);
    }

    private Pageable buildPageable(int page, int size, String sortParam) {
        Sort sort = Sort.unsorted();
        if (sortParam != null && !sortParam.isBlank()) {
            String[] parts = sortParam.split(",");
            if (parts.length == 2) {
                String field = SqlBuilder.toSnakeCase(parts[0]);
                Sort.Direction direction = parts[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
                sort = Sort.by(direction, field);
            } else if (parts.length == 1) {
                String field = SqlBuilder.toSnakeCase(parts[0]);
                sort = Sort.by(field);
            }
        }
        return PageRequest.of(page, size, sort);
    }
}