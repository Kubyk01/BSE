package bce.com.salonshub.adapter.secondary.salonstorage.mapper;

import bce.com.salonshub.adapter.secondary.salonstorage.model.SalonDao;
import bce.com.salonshub.domain.PriceRange;
import bce.com.salonshub.domain.Salon;
import bce.com.salonshub.domain.SalonId;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.codec.Json;
import lombok.SneakyThrows;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class SalonMapper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private SalonMapper() {}

    @SneakyThrows
    public static SalonDao toDao(Salon salon) {
        if (salon == null) return null;

        UUID id = Optional.ofNullable(salon.getId())
            .map(SalonId::id)
            .orElseGet(UUID::randomUUID);

        boolean isNew = salon.getId() == null || salon.getId().id() == null;

        Json websiteJson = Optional.ofNullable(salon.getWebsite())
            .map(SalonMapper::serializeWebsite)
            .orElse(null);

        Double lowest = Optional.ofNullable(salon.getPriceRange())
            .map(PriceRange::lowestPrice)
            .orElse(null);
        Double highest = Optional.ofNullable(salon.getPriceRange())
            .map(PriceRange::highestPrice)
            .orElse(null);

        return new SalonDao(
            id,
            salon.getName(),
            salon.getAddress(),
            salon.getDistrict(),
            salon.getPhone(),
            websiteJson,
            salon.getServices(),
            lowest,
            highest,
            salon.getRating(),
            salon.getNumberOfReviews(),
            isNew
        );
    }

    @SneakyThrows
    private static Json serializeWebsite(Map<String, String> website) {
        String json = OBJECT_MAPPER.writeValueAsString(website);
        return Json.of(json);
    }

    @SneakyThrows
    public static Salon toDomain(SalonDao dao) {
        if (dao == null) return null;
        Map<String, String> website = null;
        if (dao.getWebsite() != null) {
            website = OBJECT_MAPPER.readValue(dao.getWebsite().asString(),
                new TypeReference<>() {});
        }
        return Salon.builder()
            .id(dao.getId() != null ? new SalonId(dao.getId()) : null)
            .name(dao.getName())
            .address(dao.getAddress())
            .district(dao.getDistrict())
            .phone(dao.getPhone())
            .website(website)
            .services(dao.getServices())
            .priceRange(new PriceRange(
                dao.getLowestPrice() != null ? dao.getLowestPrice() : 0.0,
                dao.getHighestPrice() != null ? dao.getHighestPrice() : 0.0))
            .rating(dao.getRating())
            .numberOfReviews(dao.getNumberOfReviews())
            .build();
    }
}