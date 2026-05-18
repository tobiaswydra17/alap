package at.fhtw.alap.release;

import at.fhtw.alap.aggregation.AggregationCounter;
import at.fhtw.alap.aggregation.AggregationCounterRepository;
import at.fhtw.alap.aggregation.BucketPresenceCleanupService;
import at.fhtw.alap.location.Location;
import at.fhtw.alap.policy.Policy;
import at.fhtw.alap.release.dto.ReleaseRunResponse;
import at.fhtw.alap.release.dto.ReleasedAggregationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class ReleaseService {

    private static final Logger log = LoggerFactory.getLogger(ReleaseService.class);

    private final AggregationCounterRepository aggregationCounterRepository;
    private final ReleasedAggregationRepository releasedAggregationRepository;
    private final BucketPresenceCleanupService bucketPresenceCleanupService;

    public ReleaseService(
            AggregationCounterRepository aggregationCounterRepository,
            ReleasedAggregationRepository releasedAggregationRepository,
            BucketPresenceCleanupService bucketPresenceCleanupService
    ) {
        this.aggregationCounterRepository = aggregationCounterRepository;
        this.releasedAggregationRepository = releasedAggregationRepository;
        this.bucketPresenceCleanupService = bucketPresenceCleanupService;
    }

    @Transactional
    public ReleaseRunResponse runRelease() {
        Instant now = Instant.now();

        log.info("Starting release run at {}", now);

        List<AggregationCounter> counters = aggregationCounterRepository.findAllByOrderByTimeBucketStartAsc();

        log.info("Total aggregation counters found: {}", counters.size());

        int processedBuckets = 0;
        int releasedBuckets = 0;
        int suppressedBuckets = 0;
        int skippedOpenBuckets = 0;

        for (AggregationCounter counter : counters) {
            Location location = counter.getLocation();

            if (location == null || !Boolean.TRUE.equals(location.getIsActive())) {
                continue;
            }

            Policy policy = location.getPolicy();
            if (policy == null) {
                throw new IllegalStateException("Location " + location.getId() + " has no policy assigned");
            }

            Instant bucketStart = counter.getTimeBucketStart();
            Instant bucketEnd = bucketStart.plusSeconds(policy.getTimeBucketMinutes() * 60L);

            if (bucketEnd.isAfter(now)) {
                log.info("Skipping OPEN bucket -> location: {}, bucketEnd: {}", location.getId(), bucketEnd);
                skippedOpenBuckets++;
                continue;
            }

            ReleaseStatus status = determineReleaseStatus(counter, policy);

            log.info("Bucket decision -> location: {}, count: {}, kThreshold: {}, result: {}",
                    location.getId(),
                    counter.getUniqueUserCount(),
                    policy.getKThreshold(),
                    status
            );

            ReleasedAggregation releasedAggregation = releasedAggregationRepository
                    .findByLocation_IdAndTimeBucketStart(location.getId(), bucketStart)
                    .orElseGet(() -> {
                        ReleasedAggregation newReleasedAggregation = new ReleasedAggregation();
                        newReleasedAggregation.setLocation(location);
                        newReleasedAggregation.setTimeBucketStart(bucketStart);
                        return newReleasedAggregation;
                    });

            releasedAggregation.setTimeBucketEnd(bucketEnd);
            releasedAggregation.setUniqueUserCount(counter.getUniqueUserCount());
            releasedAggregation.setReleaseStatus(status);

            releasedAggregationRepository.save(releasedAggregation);

            log.info("Saved ReleasedAggregation -> location: {}, bucket: {}, status: {}",
                    location.getId(),
                    bucketStart,
                    status
            );

            processedBuckets++;

            if (status == ReleaseStatus.RELEASED) {
                releasedBuckets++;
            } else {
                suppressedBuckets++;
            }
        }

        long deletedBucketPresences = bucketPresenceCleanupService.cleanupExpired(Instant.now());
        log.info("Deleted {} expired bucket presence entries", deletedBucketPresences);

        log.info("Release run finished -> processed: {}, released: {}, suppressed: {}, skipped(open): {}",
                processedBuckets,
                releasedBuckets,
                suppressedBuckets,
                skippedOpenBuckets
        );

        return new ReleaseRunResponse(
                processedBuckets,
                releasedBuckets,
                suppressedBuckets,
                skippedOpenBuckets
        );
    }

    private ReleaseStatus determineReleaseStatus(AggregationCounter counter, Policy policy) {
        Long uniqueUserCount = counter.getUniqueUserCount();
        Integer kThreshold = policy.getKThreshold();

        if (uniqueUserCount >= kThreshold) {
            return ReleaseStatus.RELEASED;
        }

        return ReleaseStatus.SUPPRESSED;
    }

    @Transactional(readOnly = true)
    public List<ReleasedAggregationResponse> getAllReleased() {
        return releasedAggregationRepository
                .findByReleaseStatusOrderByTimeBucketStartDesc(ReleaseStatus.RELEASED)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReleasedAggregationResponse> getReleasedByLocation(Long locationId) {
        return releasedAggregationRepository
                .findByLocation_IdAndReleaseStatusOrderByTimeBucketStartAsc(locationId, ReleaseStatus.RELEASED)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ReleasedAggregationResponse> getAllSuppressed() {
        return releasedAggregationRepository
                .findByReleaseStatusOrderByTimeBucketStartDesc(ReleaseStatus.SUPPRESSED)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ReleasedAggregationResponse toResponse(ReleasedAggregation releasedAggregation) {
        Location location = releasedAggregation.getLocation();

        return new ReleasedAggregationResponse(
                location.getId(),
                location.getName(),
                releasedAggregation.getTimeBucketStart(),
                releasedAggregation.getTimeBucketEnd(),
                releasedAggregation.getUniqueUserCount(),
                releasedAggregation.getReleaseStatus()
        );
    }
}