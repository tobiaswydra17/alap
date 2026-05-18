package at.fhtw.alap.aggregation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface BucketPresenceRepository extends JpaRepository<BucketPresence, Long> {

    boolean existsByDedupKey(String dedupKey);

    long deleteByExpiresAtLessThanEqual(Instant cutoff);
}