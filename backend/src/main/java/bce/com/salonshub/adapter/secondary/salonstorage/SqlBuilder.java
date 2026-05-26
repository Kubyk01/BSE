package bce.com.salonshub.adapter.secondary.salonstorage;

import bce.com.salonshub.adapter.secondary.salonstorage.model.SalonDao;
import org.springframework.data.domain.Pageable;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class SqlBuilder {

    private static final Map<String, String> COLUMN_TO_SQL_TYPE = new ConcurrentHashMap<>();

    static {
        for (Field field : SalonDao.class.getDeclaredFields()) {
            String columnName = toSnakeCase(field.getName());
            Class<?> type = field.getType();
            if (type == Double.class || type == double.class) {
                COLUMN_TO_SQL_TYPE.put(columnName, "DOUBLE PRECISION");
            } else if (type == Integer.class || type == int.class) {
                COLUMN_TO_SQL_TYPE.put(columnName, "INTEGER");
            }
        }
    }

    public static String toSnakeCase(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) return camelCase;

        StringBuilder result = new StringBuilder();
        result.append(Character.toLowerCase(camelCase.charAt(0)));

        for (int i = 1; i < camelCase.length(); i++) {
            char c = camelCase.charAt(i);
            if (Character.isUpperCase(c)) {
                result.append('_').append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    public static Tuple2<String, List<Object>> buildFilterQuery(
        Map<String, String> fields,
        Pageable pageable
    ) {
        StringBuilder sql = new StringBuilder("SELECT * FROM salon");
        List<Object> params = new ArrayList<>();

        boolean first = true;
        int paramIndex = 1;

        if (fields != null && !fields.isEmpty()) {
            sql.append(" WHERE ");

            for (Map.Entry<String, String> entry : fields.entrySet()) {
                String column = toSnakeCase(entry.getKey());
                String value = entry.getValue();

                if ("services".equals(column)) {
                    String[] items = value.split(",");
                    for (String item : items) {
                        if (!first) {
                            sql.append(" AND ");
                        }
                        first = false;
                        sql.append(column)
                            .append(" @> ARRAY[$")
                            .append(paramIndex)
                            .append("]::varchar[]");
                        params.add(item.trim());
                        paramIndex++;
                    }
                } else {
                    if (!first) {
                        sql.append(" AND ");
                    }
                    first = false;
                    sql.append(column).append(" = ");
                    String sqlType = COLUMN_TO_SQL_TYPE.get(column);
                    if (sqlType != null) {
                        sql.append("CAST($").append(paramIndex)
                            .append(" AS ").append(sqlType).append(")");
                    } else {
                        sql.append("$").append(paramIndex);
                    }
                    params.add(value);
                    paramIndex++;
                }
            }
        }

        sql.append(" ORDER BY id");

        if (pageable != null && pageable.isPaged()) {
            sql.append(" LIMIT $").append(paramIndex);
            params.add(pageable.getPageSize());
            paramIndex++;
            if (pageable.getOffset() > 0) {
                sql.append(" OFFSET $").append(paramIndex);
                params.add(pageable.getOffset());
            }
        }

        return Tuples.of(sql.toString(), params);
    }

    public static WhereClause buildWhereClause(Map<String, String> fields) {
        StringBuilder where = new StringBuilder();
        Map<String, Object> params = new HashMap<>();

        boolean first = true;
        int index = 1;

        if (fields != null && !fields.isEmpty()) {
            for (Map.Entry<String, String> entry : fields.entrySet()) {
                String column = toSnakeCase(entry.getKey());
                String paramName = "p" + index;

                if (first) {
                    where.append(" WHERE ");
                    first = false;
                } else {
                    where.append(" AND ");
                }

                if ("services".equals(column)) {
                    where.append(column)
                        .append(" @> ARRAY[:")
                        .append(paramName)
                        .append("]::varchar[]");
                    params.put(paramName, new String[]{entry.getValue()});
                } else {
                    where.append(column).append(" = ");
                    String sqlType = COLUMN_TO_SQL_TYPE.get(column);
                    if (sqlType != null) {
                        where.append("CAST(:").append(paramName)
                            .append(" AS ").append(sqlType).append(")");
                    } else {
                        where.append(":").append(paramName);
                    }
                    params.put(paramName, entry.getValue());
                }
                index++;
            }
        }

        return new WhereClause(where.toString(), params);
    }

    public record WhereClause(String sql, Map<String, Object> parameters) {}
}