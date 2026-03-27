package at.fhtw.alap.config;

import at.fhtw.alap.location.Location;
import at.fhtw.alap.location.LocationH3Cell;
import at.fhtw.alap.location.LocationH3CellRepository;
import at.fhtw.alap.location.LocationRepository;
import at.fhtw.alap.policy.Policy;
import at.fhtw.alap.policy.PolicyRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class DataSeeder implements CommandLineRunner {

    private final PolicyRepository policyRepository;
    private final LocationRepository locationRepository;
    private final LocationH3CellRepository locationH3CellRepository;

    public DataSeeder(
            PolicyRepository policyRepository,
            LocationRepository locationRepository,
            LocationH3CellRepository locationH3CellRepository
    ) {
        this.policyRepository = policyRepository;
        this.locationRepository = locationRepository;
        this.locationH3CellRepository = locationH3CellRepository;
    }

    @Override
    public void run(String... args) {
        if (policyRepository.count() > 0 || locationRepository.count() > 0 || locationH3CellRepository.count() > 0) {
            return;
        }

        Policy defaultPolicy = new Policy();
        defaultPolicy.setName("default_policy");
        defaultPolicy.setH3Resolution(9);
        defaultPolicy.setTimeBucketMinutes(15);
        defaultPolicy.setKThreshold(5);
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
        cell1.setH3CellId("891e39d75c3ffff");
        locationH3CellRepository.save(cell1);

        LocationH3Cell cell2 = new LocationH3Cell();
        cell2.setLocation(schwedenplatz);
        cell2.setH3CellId("891e39d7597ffff");
        locationH3CellRepository.save(cell2);

        LocationH3Cell cell3 = new LocationH3Cell();
        cell3.setLocation(stephansplatz);
        cell3.setH3CellId("891e39d74d3ffff");
        locationH3CellRepository.save(cell3);

        LocationH3Cell cell4 = new LocationH3Cell();
        cell4.setLocation(stephansplatz);
        cell4.setH3CellId("891e39d74dbffff");
        locationH3CellRepository.save(cell4);

        System.out.println("Seed data created.");
    }
}