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

        String sql = where.sql();
        assertTrue(sql.contains("lowest_price = :p1") || sql.contains("lowest_price = :p2"));
        assertTrue(sql.contains("number_of_reviews = :p1") || sql.contains("number_of_reviews = :p2"));

        assertEquals(2, where.parameters().size());
        assertTrue(where.parameters().containsValue("50"));
        assertTrue(where.parameters().containsValue("100"));
    }

    @Test
    void buildWhereClause_shouldHandleSingleField() {
        SqlBuilder.WhereClause where = SqlBuilder.buildWhereClause(Map.of("name", "SalonA"));
        assertEquals(" WHERE name = :p1", where.sql());
    }
}