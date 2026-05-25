package bce.com.salonshub.adapter.secondary.salonstorage;

import bce.com.salonshub.adapter.secondary.salonstorage.model.SalonDao;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface SalonRepository extends ReactiveCrudRepository<SalonDao, UUID> {

}