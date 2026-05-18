package at.fhtw.alap.aggregation;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "bucket_presences",
        uniqueConstraints = { @UniqueConstraint(name = "uk_bucket_presence_dedup_key", columnNames = "dedup_key")})
public class BucketPresence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dedup_key", nullable = false, unique = true, length = 128)
    private String dedupKey;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    public BucketPresence() {
    }

    public BucketPresence(String dedupKey, Instant expiresAt) {
        this.dedupKey = dedupKey;
        this.expiresAt = expiresAt;
    }

    public Long getId() {
        return id;
    }

    public String getDedupKey() {
        return dedupKey;
    }

    public void setDedupKey(String dedupKey) {
        this.dedupKey = dedupKey;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
}