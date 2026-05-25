package bce.com.salonshub.fixture;

import bce.com.salonshub.adapter.secondary.salonstorage.SalonStorageAdapter;
import bce.com.salonshub.application.service.SalonIngestionService;
import bce.com.salonshub.application.service.SalonRetrievalService;
import bce.com.salonshub.port.primary.SalonIngestionPort;
import bce.com.salonshub.port.primary.SalonRetrievalPort;
import bce.com.salonshub.port.secondary.SalonStoragePort;
import org.mockito.Mockito;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

public class PortFixture {

    private final RepositoryFixture repositoryFixture;
    private final SalonStoragePort salonStoragePort;
    private final SalonIngestionPort salonIngestionPort;
    private final SalonRetrievalPort salonRetrievalPort;

    public PortFixture() {
        this.repositoryFixture = new RepositoryFixture();
        R2dbcEntityTemplate template = Mockito.mock(R2dbcEntityTemplate.class);
        this.salonStoragePort = new SalonStorageAdapter(template, repositoryFixture.salonRepository());
        this.salonIngestionPort = new SalonIngestionService(salonStoragePort);
        this.salonRetrievalPort = new SalonRetrievalService(salonStoragePort);
    }

    public SalonStoragePort salonStoragePort() {
        return salonStoragePort;
    }

    public SalonIngestionPort salonIngestionPort() {
        return salonIngestionPort;
    }

    public SalonRetrievalPort salonRetrievalPort() {
        return salonRetrievalPort;
    }

    public static PortFixture withNewInMemoryDatabase() {
        return new PortFixture();
    }

}