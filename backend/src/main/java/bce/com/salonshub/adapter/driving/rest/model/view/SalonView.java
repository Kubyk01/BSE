package bce.com.salonshub.adapter.driving.rest.model.view;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalonView {
    private UUID id;
    private String name;
    private String address;
    private String district;
    private String phone;
    private Map<String, String> website;
    private List<String> services;
    private Double lowestPrice;
    private Double highestPrice;
    private Double rating;
    private Integer numberOfReviews;
}
