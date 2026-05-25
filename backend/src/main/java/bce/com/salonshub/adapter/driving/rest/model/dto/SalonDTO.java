package bce.com.salonshub.adapter.driving.rest.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
public class SalonDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String address;
    @NotBlank
    private String district;
    @Pattern(regexp = "^\\+?[0-9\\-\\s]{7,20}$",
        message = "Phone number must be valid (7-20 digits, may start with +, may contain spaces or hyphens)")
    private String phone;
    private Map<String, String> website;
    private List<String> services;
    private Double lowestPrice;
    private Double highestPrice;
    private Double rating;
    private Integer numberOfReviews;
}