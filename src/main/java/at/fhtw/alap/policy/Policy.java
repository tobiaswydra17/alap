package at.fhtw.alap.policy;

import jakarta.persistence.*;
import jakarta.persistence.Entity;

@Entity
@Table(name = "policy")
public class Policy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "h3_resolution", nullable = false)
    private Integer h3Resolution;

    @Column(name = "time_bucket_minutes", nullable = false)
    private Integer timeBucketMinutes;

    @Column(name = "k_treshold", nullable = false)
    private Integer kThreshold;

    @Column(name = "allow_multi_assignment", nullable = false)
    private Boolean allowMultiAssignment;

    public Policy() {}

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Integer getH3Resolution() {
        return h3Resolution;
    }
    public void setH3Resolution(Integer h3Resolution) {
        this.h3Resolution = h3Resolution;
    }

    public Integer getTimeBucketMinutes() {
        return timeBucketMinutes;
    }
    public void setTimeBucketMinutes(Integer timeBucketMinutes) {
        this.timeBucketMinutes = timeBucketMinutes;
    }

    public Integer getKThreshold() {
        return kThreshold;
    }
    public void setKThreshold(Integer kThreshold) {
        this.kThreshold = kThreshold;
    }

    public Boolean getAllowMultiAssignment() {
        return allowMultiAssignment;
    }
    public void setAllowMultiAssignment(Boolean allowMultiAssignment) {
        this.allowMultiAssignment = allowMultiAssignment;
    }
}
