package at.fhtw.alap.release;

import at.fhtw.alap.location.Location;

import java.time.Instant;

import jakarta.persistence.*;
import jakarta.persistence.Entity;

@Entity
@Table(name = "released_aggregations", uniqueConstraints = {@UniqueConstraint(columnNames = {"location_id", "time_bucket_start"})})
public class ReleasedAggregation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "time_bucket_start", nullable = false)
    private Instant timeBucketStart;

    @Column(name = "time_bucket_end", nullable = false)
    private Instant timeBucketEnd;

    @Column(name = "unique_user_count", nullable = false)
    private Long uniqueUserCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "release_status", nullable = false, length = 32)
    private ReleaseStatus releaseStatus;

    public ReleasedAggregation() {}

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

    public Instant getTimeBucketEnd() {
        return timeBucketEnd;
    }
    public void setTimeBucketEnd(Instant timeBucketEnd) {
        this.timeBucketEnd = timeBucketEnd;
    }

    public Long getUniqueUserCount() {
        return uniqueUserCount;
    }
    public void setUniqueUserCount(Long uniqueUserCount) {
        this.uniqueUserCount = uniqueUserCount;
    }

    public ReleaseStatus getReleaseStatus() {
        return releaseStatus;
    }
    public void setReleaseStatus(ReleaseStatus releaseStatus) {
        this.releaseStatus = releaseStatus;
    }
}
