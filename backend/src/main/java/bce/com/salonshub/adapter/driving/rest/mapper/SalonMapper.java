package bce.com.salonshub.adapter.driving.rest.mapper;

import bce.com.salonshub.adapter.driving.rest.model.dto.SalonDTO;
import bce.com.salonshub.adapter.driving.rest.model.view.SalonView;
import bce.com.salonshub.domain.PriceRange;
import bce.com.salonshub.domain.Salon;

public final class SalonMapper {

    public static Salon toDomain(SalonDTO dto) {
        if (dto == null) return null;
        return Salon.builder()
            .name(dto.getName())
            .address(dto.getAddress())
            .district(dto.getDistrict())
            .phone(dto.getPhone())
            .website(dto.getWebsite())
            .services(dto.getServices())
            .priceRange(new PriceRange(
                dto.getLowestPrice() != null ? dto.getLowestPrice() : 0.0,
                dto.getHighestPrice() != null ? dto.getHighestPrice() : 0.0))
            .rating(dto.getRating())
            .numberOfReviews(dto.getNumberOfReviews())
            .build();
    }

    public static SalonDTO toDTO(Salon salon) {
        if (salon == null) return null;
        return SalonDTO.builder()
            .name(salon.getName())
            .address(salon.getAddress())
            .district(salon.getDistrict())
            .phone(salon.getPhone())
            .website(salon.getWebsite())
            .services(salon.getServices())
            .lowestPrice(salon.getPriceRange() != null ? salon.getPriceRange().lowestPrice() : null)
            .highestPrice(salon.getPriceRange() != null ? salon.getPriceRange().highestPrice() : null)
            .rating(salon.getRating())
            .numberOfReviews(salon.getNumberOfReviews())
            .build();
    }

    public static SalonView toView(Salon salon) {
        if (salon == null) return null;
        return SalonView.builder()
            .name(salon.getName())
            .address(salon.getAddress())
            .district(salon.getDistrict())
            .phone(salon.getPhone())
            .website(salon.getWebsite())
            .services(salon.getServices())
            .lowestPrice(salon.getPriceRange() != null ? salon.getPriceRange().lowestPrice() : null)
            .highestPrice(salon.getPriceRange() != null ? salon.getPriceRange().highestPrice() : null)
            .rating(salon.getRating())
            .numberOfReviews(salon.getNumberOfReviews())
            .build();
    }
}