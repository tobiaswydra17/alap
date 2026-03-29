package at.fhtw.alap.aggregation;

import at.fhtw.alap.location.Location;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class DeduplicationService {

    private final BucketPresenceRepository bucketPresenceRepository;

    public DeduplicationService(BucketPresenceRepository bucketPresenceRepository) {
        this.bucketPresenceRepository = bucketPresenceRepository;
    }

    @Transactional
    public boolean registerIfNotPresent(Location location, Instant bucketStart, String userHash) {
        BucketPresence bucketPresence = new BucketPresence();
        bucketPresence.setLocation(location);
        bucketPresence.setTimeBucketStart(bucketStart);
        bucketPresence.setUserHash(userHash);

        try {
            bucketPresenceRepository.saveAndFlush(bucketPresence);
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }
}