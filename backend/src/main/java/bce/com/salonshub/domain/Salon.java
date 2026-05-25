package bce.com.salonshub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Salon {
    private SalonId id;
    private String name;
    private String address;
    private String district;
    private String phone;
    private Map<String, String> website; //"name of social link,ex "facebook":"link"
    private List<String> services;
    private PriceRange priceRange;
    private Double rating;
    private Integer numberOfReviews;
}