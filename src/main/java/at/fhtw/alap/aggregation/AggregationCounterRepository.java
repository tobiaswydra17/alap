package at.fhtw.alap.aggregation;

import at.fhtw.alap.location.LocationH3Cell;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface AggregationCounterRepository extends JpaRepository<AggregationCounter, Long> {

    Optional<AggregationCounter> findByLocation_IdAndTimeBucketStart(Long locationId, Instant timeBucketStart);

    List<AggregationCounter> findByLocation_IdOrderByTimeBucketStartAsc(Long locationId);
}
