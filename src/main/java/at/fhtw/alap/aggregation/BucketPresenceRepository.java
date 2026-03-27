package at.fhtw.alap.aggregation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface BucketPresenceRepository extends JpaRepository<BucketPresence, Long> {

    Optional<BucketPresence> findByLocation_IdAndTimeBucketStartAndUserHash(
            Long locationId,
            Instant timeBucketStart,
            String userHash
    );

    boolean existsByLocation_IdAndTimeBucketStartAndUserHash(
            Long locationId,
            Instant timeBucketStart,
            String userHash
    );
}