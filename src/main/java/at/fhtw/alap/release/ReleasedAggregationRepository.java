package at.fhtw.alap.release;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ReleasedAggregationRepository extends JpaRepository<ReleasedAggregation, Long> {

    Optional<ReleasedAggregation> findByLocation_IdAndTimeBucketStart(Long locationId, Instant timeBucketStart);

    List<ReleasedAggregation> findByReleaseStatusOrderByTimeBucketStartDesc(ReleaseStatus releaseStatus);

    List<ReleasedAggregation> findByLocation_IdAndReleaseStatusOrderByTimeBucketStartAsc(
            Long locationId,
            ReleaseStatus releaseStatus
    );
}
