package bce.com.salonshub.adapter.secondary.salonstorage;

import org.junit.jupiter.api.Test;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlBuilderTest {

    @Test
    void buildWhereClause_shouldReturnEmptyWhenFieldsEmpty() {
        SqlBuilder.WhereClause where = SqlBuilder.buildWhereClause(Map.of());
        assertEquals("", where.sql());
        assertTrue(where.parameters().isEmpty());
    }

    @Test
    void buildWhereClause_shouldConvertCamelCaseToSnakeCase() {
        Map<String, String> fields = Map.of("lowestPrice", "50", "numberOfReviews", "100");
        SqlBuilder.WhereClause where = SqlBuilder.buildWhereClause(fields);
        assertEquals(" WHERE lowest_price = :p1 AND number_of_reviews = :p2", where.sql());
        assertEquals(Map.of("p1", "50", "p2", "100"), where.parameters());
    }

    @Test
    void buildWhereClause_shouldHandleSingleField() {
        SqlBuilder.WhereClause where = SqlBuilder.buildWhereClause(Map.of("name", "SalonA"));
        assertEquals(" WHERE name = :p1", where.sql());
    }
}