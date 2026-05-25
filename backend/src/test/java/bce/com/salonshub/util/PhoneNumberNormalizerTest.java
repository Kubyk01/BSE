package bce.com.salonshub.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PhoneNumberNormalizerTest {

    @Test
    void normalize_shouldRemoveSpacesAndDashes() {
        assertEquals("+48734734734", PhoneNumberNormalizer.normalize("+48 734 734 734"));
        assertEquals("+48734734734", PhoneNumberNormalizer.normalize("+48-734-734-734"));
    }

    @Test
    void normalize_shouldKeepOnlyOneLeadingPlus() {
        assertEquals("+48734734734", PhoneNumberNormalizer.normalize("++48 734 734 734"));
    }

    @Test
    void normalize_shouldRemoveBrackets() {
        assertEquals("+48734734734", PhoneNumberNormalizer.normalize("+48 (734) 734 734"));
    }

    @Test
    void normalize_withoutPlus_shouldKeepDigitsOnly() {
        assertEquals("48734734734", PhoneNumberNormalizer.normalize("48 734 734 734"));
    }

    @Test
    void normalize_shouldReturnNullForNullOrEmpty() {
        assertNull(PhoneNumberNormalizer.normalize(null));
        assertNull(PhoneNumberNormalizer.normalize(""));
        assertNull(PhoneNumberNormalizer.normalize("   "));
    }

    @Test
    void normalize_shouldReturnNullWhenNoDigits() {
        assertNull(PhoneNumberNormalizer.normalize("abc + -"));
    }
}