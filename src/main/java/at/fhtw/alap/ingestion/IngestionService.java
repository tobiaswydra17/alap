package at.fhtw.alap.ingestion;

import at.fhtw.alap.aggregation.BucketCalculator;
import at.fhtw.alap.ingestion.dto.LocationEventRequest;
import at.fhtw.alap.location.H3Service;
import at.fhtw.alap.policy.Policy;
import at.fhtw.alap.policy.PolicyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class IngestionService {

    private final PolicyRepository policyRepository;
    private final H3Service h3Service;
    private final BucketCalculator bucketCalculator;

    public IngestionService(
            PolicyRepository policyRepository,
            H3Service h3Service,
            BucketCalculator bucketCalculator
    ) {
        this.policyRepository = policyRepository;
        this.h3Service = h3Service;
        this.bucketCalculator = bucketCalculator;
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

        Instant bucketStart = bucketCalculator.calculateBucketStart(
                request.getTimestamp(),
                defaultPolicy.getTimeBucketMinutes()
        );

        // Später mit Deduplication usw weitermachen
    }
}
