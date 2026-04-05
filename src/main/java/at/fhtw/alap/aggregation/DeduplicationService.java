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
        boolean alreadyExists = bucketPresenceRepository.existsByLocation_IdAndTimeBucketStartAndUserHash(
                location.getId(),
                bucketStart,
                userHash
        );

        if (alreadyExists) {
            return false;
        }

        BucketPresence bucketPresence = new BucketPresence();
        bucketPresence.setLocation(location);
        bucketPresence.setTimeBucketStart(bucketStart);
        bucketPresence.setUserHash(userHash);

        bucketPresenceRepository.save(bucketPresence);
        return true;
    }
}