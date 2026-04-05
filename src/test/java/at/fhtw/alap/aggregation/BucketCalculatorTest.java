package at.fhtw.alap.aggregation;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class BucketCalculatorTest {

    private final BucketCalculator bucketCalculator = new BucketCalculator();

    @Test
    void shouldRoundDownToBucketStart() {
        Instant timestamp = Instant.parse("2026-03-29T20:07:34Z");

        Instant bucketStart = bucketCalculator.calculateBucketStart(timestamp, 15);

        assertEquals(Instant.parse("2026-03-29T20:00:00Z"), bucketStart);
    }

    @Test
    void shouldReturnExactStartIfAlreadyOnBucketBoundary() {
        Instant timestamp = Instant.parse("2026-03-29T20:15:00Z");

        Instant bucketStart = bucketCalculator.calculateBucketStart(timestamp, 15);

        assertEquals(Instant.parse("2026-03-29T20:15:00Z"), bucketStart);
    }

    @Test
    void shouldThrowIfTimestampIsNull() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> bucketCalculator.calculateBucketStart(null, 15)
        );

        assertEquals("timestamp must not be null", ex.getMessage());
    }

    @Test
    void shouldThrowIfBucketMinutesIsInvalid() {
        Instant timestamp = Instant.parse("2026-03-29T20:07:34Z");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> bucketCalculator.calculateBucketStart(timestamp, 0)
        );

        assertEquals("timeBucketMinutes must be greater than 0", ex.getMessage());
    }
}