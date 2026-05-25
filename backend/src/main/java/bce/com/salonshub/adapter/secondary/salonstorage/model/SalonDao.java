/* path: src/main/java/bce/com/salonshub/adapter/secondary/salonstorage/model/SalonDao.java */
package bce.com.salonshub.adapter.secondary.salonstorage.model;

import io.r2dbc.postgresql.codec.Json;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("Salon")
public class SalonDao implements Persistable<UUID> {
    @Id
    private UUID id;

    @Column("name")
    private String name;

    @Column("address")
    private String address;

    @Column("district")
    private String district;

    @Column("phone")
    private String phone;

    @Column("website")
    private Json website;

    @Column("services")
    private List<String> services;

    @Column("lowest_price")
    private Double lowestPrice;

    @Column("highest_price")
    private Double highestPrice;

    private Double rating;

    @Column("number_of_reviews")
    private Integer numberOfReviews;

    @Transient
    private boolean isNew;

    @Override
    public boolean isNew() {
        return isNew;
    }
}