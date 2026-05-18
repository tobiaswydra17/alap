package at.fhtw.alap.aggregation;

import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class BucketPresenceCleanupService {

    private final BucketPresenceRepository bucketPresenceRepository;

    public BucketPresenceCleanupService(BucketPresenceRepository bucketPresenceRepository) {
        this.bucketPresenceRepository = bucketPresenceRepository;
    }

    public long cleanupExpired(Instant now) {
        return bucketPresenceRepository.deleteByExpiresAtLessThanEqual(now);
    }
}