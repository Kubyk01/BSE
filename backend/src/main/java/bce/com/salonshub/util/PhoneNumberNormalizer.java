package bce.com.salonshub.util;

public final class PhoneNumberNormalizer {

    public static String normalize(String phone) {
        if (phone == null || phone.isBlank()) {
            return null;
        }
        String cleaned = phone.replaceAll("[^\\d+]", "");
        if (cleaned.isEmpty()) {
            return null;
        }
        if (cleaned.replaceAll("\\+", "").isEmpty()) {
            return null;
        }
        if (cleaned.contains("+")) {
            cleaned = "+" + cleaned.replaceAll("\\+", "");
        }
        return cleaned;
    }
}