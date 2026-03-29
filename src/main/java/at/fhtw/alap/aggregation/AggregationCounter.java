package at.fhtw.alap.aggregation;

import at.fhtw.alap.location.Location;

import java.time.Instant;

import jakarta.persistence.*;
import jakarta.persistence.Entity;

@Entity
@Table(name = "aggregation_counter", uniqueConstraints = {@UniqueConstraint(columnNames = {"location_id", "time_bucket_start"})})
public class AggregationCounter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "time_bucket_start", nullable = false)
    private Instant timeBucketStart;

    @Column(name = "unique_user_count", nullable = false)
    private Long uniqueUserCount;

    public AggregationCounter() {}

    public Long getId() {
        return id;
    }

    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        this.location = location;
    }

    public Instant getTimeBucketStart() {
        return timeBucketStart;
    }
    public void setTimeBucketStart(Instant timeBucketStart) {
        this.timeBucketStart = timeBucketStart;
    }

    public Long getUniqueUserCount() {
        return uniqueUserCount;
    }
    public void setUniqueUserCount(Long uniqueUserCount) {
        this.uniqueUserCount = uniqueUserCount;
    }
}
