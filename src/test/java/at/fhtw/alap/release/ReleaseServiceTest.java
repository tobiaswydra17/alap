package at.fhtw.alap.release;

import at.fhtw.alap.aggregation.AggregationCounter;
import at.fhtw.alap.aggregation.AggregationCounterRepository;
import at.fhtw.alap.location.Location;
import at.fhtw.alap.policy.Policy;
import at.fhtw.alap.release.dto.ReleaseRunResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReleaseServiceTest {

    private AggregationCounterRepository aggregationCounterRepository;
    private ReleasedAggregationRepository releasedAggregationRepository;
    private ReleaseService releaseService;

    @BeforeEach
    void setUp() {
        aggregationCounterRepository = mock(AggregationCounterRepository.class);
        releasedAggregationRepository = mock(ReleasedAggregationRepository.class);
        releaseService = new ReleaseService(aggregationCounterRepository, releasedAggregationRepository);
    }

    @Test
    void shouldReleaseClosedBucketIfThresholdIsReached() {
        Location location = mock(Location.class);
        when(location.getId()).thenReturn(1L);
        when(location.getIsActive()).thenReturn(true);

        Policy policy = mock(Policy.class);
        when(policy.getTimeBucketMinutes()).thenReturn(15);
        when(policy.getKThreshold()).thenReturn(3);
        when(location.getPolicy()).thenReturn(policy);

        AggregationCounter counter = new AggregationCounter();
        counter.setLocation(location);
        counter.setTimeBucketStart(Instant.now().minus(2, ChronoUnit.HOURS));
        counter.setUniqueUserCount(3L);

        when(aggregationCounterRepository.findAllByOrderByTimeBucketStartAsc())
                .thenReturn(List.of(counter));

        when(releasedAggregationRepository.findByLocation_IdAndTimeBucketStart(anyLong(), any()))
                .thenReturn(Optional.empty());

        when(releasedAggregationRepository.save(any(ReleasedAggregation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReleaseRunResponse response = releaseService.runRelease();

        assertEquals(1, response.getProcessedBuckets());
        assertEquals(1, response.getReleasedBuckets());
        assertEquals(0, response.getSuppressedBuckets());
        assertEquals(0, response.getSkippedOpenBuckets());

        verify(releasedAggregationRepository, times(1)).save(any(ReleasedAggregation.class));
    }

    @Test
    void shouldSuppressClosedBucketIfThresholdIsNotReached() {
        Location location = mock(Location.class);
        when(location.getId()).thenReturn(1L);
        when(location.getIsActive()).thenReturn(true);

        Policy policy = mock(Policy.class);
        when(policy.getTimeBucketMinutes()).thenReturn(15);
        when(policy.getKThreshold()).thenReturn(3);
        when(location.getPolicy()).thenReturn(policy);

        AggregationCounter counter = new AggregationCounter();
        counter.setLocation(location);
        counter.setTimeBucketStart(Instant.now().minus(2, ChronoUnit.HOURS));
        counter.setUniqueUserCount(1L);

        when(aggregationCounterRepository.findAllByOrderByTimeBucketStartAsc())
                .thenReturn(List.of(counter));

        when(releasedAggregationRepository.findByLocation_IdAndTimeBucketStart(anyLong(), any()))
                .thenReturn(Optional.empty());

        when(releasedAggregationRepository.save(any(ReleasedAggregation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReleaseRunResponse response = releaseService.runRelease();

        assertEquals(1, response.getProcessedBuckets());
        assertEquals(0, response.getReleasedBuckets());
        assertEquals(1, response.getSuppressedBuckets());
        assertEquals(0, response.getSkippedOpenBuckets());

        verify(releasedAggregationRepository, times(1)).save(any(ReleasedAggregation.class));
    }

    @Test
    void shouldSkipOpenBuckets() {
        Location location = mock(Location.class);
        when(location.getId()).thenReturn(1L);
        when(location.getIsActive()).thenReturn(true);

        Policy policy = mock(Policy.class);
        when(policy.getTimeBucketMinutes()).thenReturn(15);
        when(policy.getKThreshold()).thenReturn(3);
        when(location.getPolicy()).thenReturn(policy);

        AggregationCounter counter = new AggregationCounter();
        counter.setLocation(location);
        counter.setTimeBucketStart(Instant.now().minus(5, ChronoUnit.MINUTES));
        counter.setUniqueUserCount(5L);

        when(aggregationCounterRepository.findAllByOrderByTimeBucketStartAsc())
                .thenReturn(List.of(counter));

        ReleaseRunResponse response = releaseService.runRelease();

        assertEquals(0, response.getProcessedBuckets());
        assertEquals(0, response.getReleasedBuckets());
        assertEquals(0, response.getSuppressedBuckets());
        assertEquals(1, response.getSkippedOpenBuckets());

        verify(releasedAggregationRepository, never()).save(any());
    }
}