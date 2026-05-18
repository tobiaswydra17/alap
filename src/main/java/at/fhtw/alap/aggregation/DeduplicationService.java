package at.fhtw.alap.aggregation;

import at.fhtw.alap.ingestion.IngestionService;
import at.fhtw.alap.location.Location;
import at.fhtw.alap.policy.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class DeduplicationService {

    private static final Logger log = LoggerFactory.getLogger(DeduplicationService.class);

    private final BucketPresenceRepository bucketPresenceRepository;
    private final DeduplicationKeyService deduplicationKeyService;

    public DeduplicationService(BucketPresenceRepository bucketPresenceRepository,
                                DeduplicationKeyService deduplicationKeyService) {
        this.bucketPresenceRepository = bucketPresenceRepository;
        this.deduplicationKeyService = deduplicationKeyService;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean registerIfNotPresent(Location location, Instant timeBucketStart, String userHash, Policy policy) {
        String dedupKey = deduplicationKeyService.generateDedupKey(
                userHash,
                location.getId(),
                timeBucketStart
        );
        log.info("deduplication key: {}", dedupKey);

        boolean alreadyExists = bucketPresenceRepository.existsByDedupKey(dedupKey);
        if (alreadyExists) {
            return false;
        }

        Instant bucketEnd = timeBucketStart.plus(policy.getTimeBucketMinutes(), ChronoUnit.MINUTES);
        Instant expiresAt = bucketEnd.plus(5, ChronoUnit.MINUTES);

        BucketPresence bucketPresence = new BucketPresence(dedupKey, expiresAt);

        try {
            bucketPresenceRepository.save(bucketPresence);
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }
}