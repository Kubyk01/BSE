package bce.com.salonshub.adapter.secondary.salonstorage;

import bce.com.salonshub.adapter.secondary.salonstorage.mapper.SalonMapper;
import bce.com.salonshub.adapter.secondary.salonstorage.model.SalonDao;
import bce.com.salonshub.domain.Salon;
import bce.com.salonshub.domain.SalonId;
import bce.com.salonshub.port.secondary.SalonStoragePort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class SalonStorageAdapter implements SalonStoragePort {

    private final R2dbcEntityTemplate template;
    private final SalonRepository repository;
    private final MappingR2dbcConverter mappingR2dbcConverter;

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
    public Flux<Salon> findAll(Pageable pageable) {
        Query query = Query.query(Criteria.empty()).with(pageable);
        return template.select(SalonDao.class)
            .matching(query)
            .all()
            .map(SalonMapper::toDomain);
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
    public Flux<Salon> findByFields(Map<String, String> fields, Pageable pageable) {
        var sqlAndParams = SqlBuilder.buildFilterQuery(fields, pageable);
        String sql = sqlAndParams.getT1();
        List<Object> params = sqlAndParams.getT2();

        return template.getDatabaseClient()
            .sql(sql)
            .bindValues(params)
            .map((row, rowMetadata) -> mappingR2dbcConverter.read(SalonDao.class, row, rowMetadata))
            .all()
            .map(SalonMapper::toDomain);
    }

    @Override
    public Mono<Salon> update(SalonId id, Salon salon) {
        return repository.findById(id.id())
            .flatMap(existing -> {
                SalonDao dao = SalonMapper.toDao(salon);
                dao.setId(id.id());
                return repository.save(dao);
            })
            .map(SalonMapper::toDomain);
    }
}