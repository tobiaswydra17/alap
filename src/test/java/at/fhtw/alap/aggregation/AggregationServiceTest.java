package at.fhtw.alap.aggregation;

import at.fhtw.alap.location.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AggregationServiceTest {

    private AggregationCounterRepository aggregationCounterRepository;
    private AggregationService aggregationService;

    @BeforeEach
    void setUp() {
        aggregationCounterRepository = mock(AggregationCounterRepository.class);
        aggregationService = new AggregationService(aggregationCounterRepository);
    }

    @Test
    void shouldIncrementExistingCounter() {
        Location location = mock(Location.class);
        when(location.getId()).thenReturn(1L);

        Instant bucketStart = Instant.parse("2026-03-29T20:00:00Z");

        AggregationCounter existingCounter = new AggregationCounter();
        existingCounter.setLocation(location);
        existingCounter.setTimeBucketStart(bucketStart);
        existingCounter.setUniqueUserCount(2L);

        when(aggregationCounterRepository.findByLocation_IdAndTimeBucketStart(1L, bucketStart))
                .thenReturn(Optional.of(existingCounter));

        aggregationService.incrementCounter(location, bucketStart);

        verify(aggregationCounterRepository).save(existingCounter);
        assert existingCounter.getUniqueUserCount() == 3L;
    }

    @Test
    void shouldCreateNewCounterWithCountOneIfNoneExists() {
        Location location = mock(Location.class);
        when(location.getId()).thenReturn(1L);

        Instant bucketStart = Instant.parse("2026-03-29T20:00:00Z");

        when(aggregationCounterRepository.findByLocation_IdAndTimeBucketStart(1L, bucketStart))
                .thenReturn(Optional.empty());

        when(aggregationCounterRepository.save(any(AggregationCounter.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        aggregationService.incrementCounter(location, bucketStart);

        verify(aggregationCounterRepository).save(any(AggregationCounter.class));
    }
}