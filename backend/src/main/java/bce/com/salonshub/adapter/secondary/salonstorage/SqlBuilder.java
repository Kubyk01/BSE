package bce.com.salonshub.adapter.secondary.salonstorage;

import java.util.HashMap;
import java.util.Map;

public final class SqlBuilder {

    private static String toSnakeCase(String camelCase) {
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

    public static WhereClause buildWhereClause(Map<String, String> fields) {
        StringBuilder whereClause = new StringBuilder();
        Map<String, Object> parameters = new HashMap<>();
        int index = 1;

        for (Map.Entry<String, String> entry : fields.entrySet()) {
            String logicalField = entry.getKey();
            String column = toSnakeCase(logicalField);
            String paramName = "p" + index;
            if (whereClause.isEmpty()) {
                whereClause.append(" WHERE ");
            } else {
                whereClause.append(" AND ");
            }
            whereClause.append(column).append(" = :").append(paramName);
            parameters.put(paramName, entry.getValue());
            index++;
        }

        return new WhereClause(whereClause.toString(), parameters);
    }

    public record WhereClause(String sql, Map<String, Object> parameters) {}
}