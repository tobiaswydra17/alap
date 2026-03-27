package at.fhtw.alap.aggregation;

import jakarta.persistence.*;
import at.fhtw.alap.location.Location;

import java.time.Instant;

@Entity
@Table (name = "bucket_presences", uniqueConstraints = {@UniqueConstraint(columnNames = {"location_id", "time_bucket_start", "user_hash"})})
public class BucketPresence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "time_bucket_start", nullable = false)
    private Instant timeBucketStart;

    @Column(name = "user_hash", nullable = false, length = 255)
    private String userHash;

    @Column(name = "first_seen_at", nullable = false)
    private Instant firstSeenAt;

    public BucketPresence() {}

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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

    public String getUserHash() {
        return userHash;
    }
    public void setUserHash(String userHash) {
        this.userHash = userHash;
    }

    public Instant getFirstSeenAt() {
        return firstSeenAt;
    }
    public void setFirstSeenAt(Instant firstSeenAt) {
        this.firstSeenAt = firstSeenAt;
    }
}
