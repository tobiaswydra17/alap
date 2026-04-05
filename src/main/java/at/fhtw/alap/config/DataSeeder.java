package at.fhtw.alap.config;

import at.fhtw.alap.location.Location;
import at.fhtw.alap.location.LocationH3Cell;
import at.fhtw.alap.location.LocationH3CellRepository;
import at.fhtw.alap.location.LocationRepository;
import at.fhtw.alap.policy.Policy;
import at.fhtw.alap.policy.PolicyRepository;
import at.fhtw.alap.aggregation.AggregationCounter;
import at.fhtw.alap.aggregation.AggregationCounterRepository;
import at.fhtw.alap.aggregation.BucketPresence;
import at.fhtw.alap.aggregation.BucketPresenceRepository;
import at.fhtw.alap.release.ReleasedAggregationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class DataSeeder implements CommandLineRunner {

    private final ReleasedAggregationRepository releasedAggregationRepository;
    private final AggregationCounterRepository aggregationCounterRepository;
    private final BucketPresenceRepository bucketPresenceRepository;
    private final LocationH3CellRepository locationH3CellRepository;
    private final LocationRepository locationRepository;
    private final PolicyRepository policyRepository;


    public DataSeeder(
            ReleasedAggregationRepository releasedAggregationRepository,
            PolicyRepository policyRepository,
            LocationRepository locationRepository,
            LocationH3CellRepository locationH3CellRepository,
            AggregationCounterRepository aggregationCounterRepository,
            BucketPresenceRepository bucketPresenceRepository
            ) {
        this.releasedAggregationRepository = releasedAggregationRepository;
        this.policyRepository = policyRepository;
        this.locationRepository = locationRepository;
        this.locationH3CellRepository = locationH3CellRepository;
        this.aggregationCounterRepository = aggregationCounterRepository;
        this.bucketPresenceRepository = bucketPresenceRepository;
    }

    @Override
    public void run(String... args) {
        releasedAggregationRepository.deleteAll();
        aggregationCounterRepository.deleteAll();
        bucketPresenceRepository.deleteAll();
        locationH3CellRepository.deleteAll();
        locationRepository.deleteAll();
        policyRepository.deleteAll();

        Policy defaultPolicy = new Policy();
        defaultPolicy.setName("default_policy");
        defaultPolicy.setH3Resolution(9);
        defaultPolicy.setTimeBucketMinutes(15);
        defaultPolicy.setKThreshold(3);
        defaultPolicy.setAllowMultiAssignment(false);
        defaultPolicy = policyRepository.save(defaultPolicy);

        Location schwedenplatz = new Location();
        schwedenplatz.setName("Schwedenplatz");
        schwedenplatz.setType("POI");
        schwedenplatz.setPolicy(defaultPolicy);
        schwedenplatz.setIsActive(true);
        schwedenplatz = locationRepository.save(schwedenplatz);

        Location stephansplatz = new Location();
        stephansplatz.setName("Stephansplatz");
        stephansplatz.setType("POI");
        stephansplatz.setPolicy(defaultPolicy);
        stephansplatz.setIsActive(true);
        stephansplatz = locationRepository.save(stephansplatz);

        LocationH3Cell cell1 = new LocationH3Cell();
        cell1.setLocation(schwedenplatz);
        cell1.setH3CellId("891e15b7127ffff");
        locationH3CellRepository.save(cell1);

        LocationH3Cell cell2 = new LocationH3Cell();
        cell2.setLocation(schwedenplatz);
        cell2.setH3CellId("891e15b7137ffff");
        locationH3CellRepository.save(cell2);

        LocationH3Cell cell3 = new LocationH3Cell();
        cell3.setLocation(schwedenplatz);
        cell3.setH3CellId("891e15b7133ffff");
        locationH3CellRepository.save(cell3);

        LocationH3Cell cell4 = new LocationH3Cell();
        cell4.setLocation(schwedenplatz);
        cell4.setH3CellId("891e15b7123ffff");
        locationH3CellRepository.save(cell4);

        LocationH3Cell cell5 = new LocationH3Cell();
        cell5.setLocation(stephansplatz);
        cell5.setH3CellId("891e15b712bffff");
        locationH3CellRepository.save(cell5);

        LocationH3Cell cell6 = new LocationH3Cell();
        cell6.setLocation(stephansplatz);
        cell6.setH3CellId("891e39d74dbffff");
        locationH3CellRepository.save(cell6);

        System.out.println("Seed data created.");
    }
}