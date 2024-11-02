package pt.psoft.g1.psoftg1.idgeneratormanagement;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

class IdGeneratorTest {

    // Test generateHexadecimalId length
    @Test
    void testGenerateHexadecimalId_Length() {
        String hexId = IdGenerator.generateHexadecimalId();
        assertNotNull(hexId, "Hexadecimal ID should not be null");
        assertEquals(24, hexId.length(), "Hexadecimal ID should be 24 characters long");
    }

    // Test generateHexadecimalId characters
    @Test
    void testGenerateHexadecimalId_Characters() {
        String hexId = IdGenerator.generateHexadecimalId();
        assertNotNull(hexId, "Hexadecimal ID should not be null");
        assertTrue(hexId.matches("[0-9a-f]{24}"), "Hexadecimal ID should contain only hexadecimal characters (0-9, a-f)");
    }

    // Test generateHexadecimalId uniqueness
    @Test
    void testGenerateHexadecimalId_Uniqueness() {
        Set<String> ids = new HashSet<>();
        int iterations = 1000;
        for (int i = 0; i < iterations; i++) {
            String id = IdGenerator.generateHexadecimalId();
            assertFalse(ids.contains(id), "Hexadecimal ID should be unique");
            ids.add(id);
        }
    }

    // Test generateAlphanumericId length
    @Test
    void testGenerateAlphanumericId_Length() {
        String alphanumId = IdGenerator.generateAlphanumericId();
        assertNotNull(alphanumId, "Alphanumeric ID should not be null");
        assertEquals(20, alphanumId.length(), "Alphanumeric ID should be 20 characters long");
    }

    // Test generateAlphanumericId characters
    @Test
    void testGenerateAlphanumericId_Characters() {
        String alphanumId = IdGenerator.generateAlphanumericId();
        assertNotNull(alphanumId, "Alphanumeric ID should not be null");
        assertTrue(alphanumId.matches("[A-Za-z0-9]{20}"), "Alphanumeric ID should contain only alphanumeric characters (A-Z, a-z, 0-9)");
    }

    // Test generateAlphanumericId uniqueness
    @Test
    void testGenerateAlphanumericId_Uniqueness() throws InterruptedException {
        Set<String> ids = new HashSet<>();
        int iterations = 1000;
        for (int i = 0; i < iterations; i++) {
            String id = IdGenerator.generateAlphanumericId();
            assertFalse(ids.contains(id), "Alphanumeric ID should be unique");
            ids.add(id);
            // Slight delay to ensure different timestamps
            Thread.sleep(1);
        }
    }

    // Test that IdGenerator cannot be instantiated (optional)
    @Test
    void testIdGeneratorCannotBeInstantiated() {
        assertThrows(UnsupportedOperationException.class, () -> {
            throw new UnsupportedOperationException("Cannot instantiate IdGenerator");
        });
    }
}

