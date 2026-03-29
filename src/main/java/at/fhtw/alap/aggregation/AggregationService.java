package at.fhtw.alap.aggregation;

import at.fhtw.alap.location.Location;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AggregationService {

    private final AggregationCounterRepository aggregationCounterRepository;

    public AggregationService(AggregationCounterRepository aggregationCounterRepository) {
        this.aggregationCounterRepository = aggregationCounterRepository;
    }

    @Transactional
    public void incrementCounter(Location location, Instant bucketStart) {
        AggregationCounter counter = aggregationCounterRepository
                .findByLocation_IdAndTimeBucketStart(location.getId(), bucketStart)
                .orElseGet(() -> {
                    AggregationCounter newCounter = new AggregationCounter();
                    newCounter.setLocation(location);
                    newCounter.setTimeBucketStart(bucketStart);
                    newCounter.setUniqueUserCount(0L);
                    return newCounter;
                });

        counter.setUniqueUserCount(counter.getUniqueUserCount() + 1);
        aggregationCounterRepository.save(counter);
    }
}