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

public final class SalonMapper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private SalonMapper() {}

    @SneakyThrows
    public static SalonDao toDao(Salon salon) {
        if (salon == null) return null;
        SalonDao dao = new SalonDao();
        dao.setId(salon.getId() != null ? salon.getId().id() : null);
        dao.setName(salon.getName());
        dao.setAddress(salon.getAddress());
        dao.setDistrict(salon.getDistrict());
        dao.setPhone(salon.getPhone());
        if (salon.getWebsite() != null) {
            String json = OBJECT_MAPPER.writeValueAsString(salon.getWebsite());
            dao.setWebsite(Json.of(json));
        }
        dao.setServices(salon.getServices());
        if (salon.getPriceRange() != null) {
            dao.setLowestPrice(salon.getPriceRange().lowestPrice());
            dao.setHighestPrice(salon.getPriceRange().highestPrice());
        }
        dao.setRating(salon.getRating());
        dao.setNumberOfReviews(salon.getNumberOfReviews());
        dao.setNew(salon.getId() == null || salon.getId().id() == null);
        return dao;
    }

    @SneakyThrows
    public static Salon toDomain(SalonDao dao) {
        if (dao == null) return null;
        Map<String, String> website = null;
        if (dao.getWebsite() != null) {
            website = OBJECT_MAPPER.readValue(dao.getWebsite().asString(),
                new TypeReference<>() {
                });
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