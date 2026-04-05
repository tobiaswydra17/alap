package at.fhtw.alap.aggregation;

import at.fhtw.alap.location.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeduplicationServiceTest {

    private BucketPresenceRepository bucketPresenceRepository;
    private DeduplicationService deduplicationService;

    @BeforeEach
    void setUp() {
        bucketPresenceRepository = mock(BucketPresenceRepository.class);
        deduplicationService = new DeduplicationService(bucketPresenceRepository);
    }

    @Test
    void shouldReturnTrueWhenPresenceIsNew() {
        Location location = mock(Location.class);
        Instant bucketStart = Instant.parse("2026-03-29T20:00:00Z");

        when(bucketPresenceRepository.saveAndFlush(any(BucketPresence.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        boolean result = deduplicationService.registerIfNotPresent(location, bucketStart, "userA");

        assertTrue(result);
        verify(bucketPresenceRepository, times(1)).saveAndFlush(any(BucketPresence.class));
    }

    @Test
    void shouldReturnFalseWhenDuplicatePresenceCausesConstraintViolation() {
        Location location = mock(Location.class);
        Instant bucketStart = Instant.parse("2026-03-29T20:00:00Z");

        when(bucketPresenceRepository.saveAndFlush(any(BucketPresence.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate"));

        boolean result = deduplicationService.registerIfNotPresent(location, bucketStart, "userA");

        assertFalse(result);
        verify(bucketPresenceRepository, times(1)).saveAndFlush(any(BucketPresence.class));
    }
}