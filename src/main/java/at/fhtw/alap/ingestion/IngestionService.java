package at.fhtw.alap.ingestion;

import at.fhtw.alap.aggregation.AggregationService;
import at.fhtw.alap.aggregation.BucketCalculator;
import at.fhtw.alap.aggregation.DeduplicationService;
import at.fhtw.alap.ingestion.dto.LocationEventRequest;
import at.fhtw.alap.location.H3Service;
import at.fhtw.alap.location.Location;
import at.fhtw.alap.location.LocationH3Cell;
import at.fhtw.alap.location.LocationH3CellRepository;
import at.fhtw.alap.policy.Policy;
import at.fhtw.alap.policy.PolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class IngestionService {

    private static final Logger log = LoggerFactory.getLogger(IngestionService.class);

    private final PolicyRepository policyRepository;
    private final LocationH3CellRepository locationH3CellRepository;
    private final H3Service h3Service;
    private final BucketCalculator bucketCalculator;
    private final DeduplicationService deduplicationService;
    private final AggregationService aggregationService;

    public IngestionService(
            PolicyRepository policyRepository,
            LocationH3CellRepository locationH3CellRepository,
            H3Service h3Service,
            BucketCalculator bucketCalculator,
            DeduplicationService deduplicationService,
            AggregationService aggregationService
    ) {
        this.policyRepository = policyRepository;
        this.locationH3CellRepository = locationH3CellRepository;
        this.h3Service = h3Service;
        this.bucketCalculator = bucketCalculator;
        this.deduplicationService = deduplicationService;
        this.aggregationService = aggregationService;
    }

    @Transactional
    public void ingest(LocationEventRequest request) {
        Policy defaultPolicy = policyRepository.findByName("default_policy")
                .orElseThrow(() -> new IllegalStateException("Default policy not found"));

        String h3CellId = h3Service.latLngToCell(
                request.getLatitude(),
                request.getLongitude(),
                defaultPolicy.getH3Resolution()
        );

        log.info("Calculated H3 cell: {}", h3CellId);

        List<LocationH3Cell> matchedLocationCells = locationH3CellRepository.findByH3CellId(h3CellId);

        log.info("Matched location cells count: {}", matchedLocationCells.size());

        if (matchedLocationCells.isEmpty()) {
            log.info("No matching locations found for H3 cell {}", h3CellId);
            return;
        }

        Instant bucketStart = bucketCalculator.calculateBucketStart(
                request.getTimestamp(),
                defaultPolicy.getTimeBucketMinutes()
        );

        log.info("Calculated bucket start: {}", bucketStart);

        for (LocationH3Cell matchedLocationCell : matchedLocationCells) {
            Location location = matchedLocationCell.getLocation();

            if (location == null) {
                log.warn("Matched location cell has no location assigned");
                continue;
            }

            if (!Boolean.TRUE.equals(location.getIsActive())) {
                log.info("Skipping inactive location with id {}", location.getId());
                continue;
            }

            boolean isNewPresence = deduplicationService.registerIfNotPresent(
                    location,
                    bucketStart,
                    request.getUserHash()
            );

            log.info("Presence registration for location {} returned {}", location.getId(), isNewPresence);

            if (isNewPresence) {
                aggregationService.incrementCounter(location, bucketStart);
                log.info("Incremented aggregation counter for location {} and bucket {}", location.getId(), bucketStart);
            }
        }
    }
}
