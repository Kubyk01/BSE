package bce.com.salonshub.adapter.secondary.salonstorage;

import bce.com.salonshub.adapter.secondary.salonstorage.mapper.SalonMapper;
import bce.com.salonshub.adapter.secondary.salonstorage.model.SalonDao;
import bce.com.salonshub.domain.Salon;
import bce.com.salonshub.domain.SalonId;
import bce.com.salonshub.port.secondary.SalonStoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class SalonStorageAdapter implements SalonStoragePort {

    private final R2dbcEntityTemplate template;
    private final SalonRepository repository;

    @Override
    public Mono<Salon> save(Salon salon) {
        SalonDao dao = SalonMapper.toDao(salon);
        return repository.save(dao).map(SalonMapper::toDomain);
    }

    @Override
    public Mono<Salon> findById(SalonId id) {
        return repository.findById(id.id()).map(SalonMapper::toDomain);
    }

    @Override
    public Flux<Salon> findAll() {
        return repository.findAll().map(SalonMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(SalonId id) {
        return repository.deleteById(id.id());
    }

    @Override
    public Mono<Void> deleteByFields(Map<String, String> fields) {
        SqlBuilder.WhereClause where = SqlBuilder.buildWhereClause(fields);
        String sql = "DELETE FROM Salon" + where.sql();
        return template.getDatabaseClient()
            .sql(sql)
            .bindValues(where.parameters())
            .then();
    }

    @Override
    public Flux<Salon> findByFields(Map<String, String> fields) {
        SqlBuilder.WhereClause where = SqlBuilder.buildWhereClause(fields);
        String sql = "SELECT * FROM Salon" + where.sql();
        return template.getDatabaseClient()
            .sql(sql)
            .bindValues(where.parameters())
            .map((row, metadata) -> template.getConverter().read(SalonDao.class, row, metadata))
            .all()
            .map(SalonMapper::toDomain);
    }

    @Override
    public Mono<Salon> update(SalonId id, Salon salon) {
        return repository.findById(id.id())
            .flatMap(_ -> {
                SalonDao dao = SalonMapper.toDao(salon);
                dao.setId(id.id());
                return repository.save(dao);
            })
            .map(SalonMapper::toDomain);
    }
}