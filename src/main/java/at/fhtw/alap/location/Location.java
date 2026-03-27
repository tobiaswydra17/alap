package at.fhtw.alap.location;

import at.fhtw.alap.policy.Policy;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.persistence.Entity;

@Entity
@Table(name = "location")
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String type;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    @Column(name = "is_active")
    private Boolean isActive;

    @OneToMany(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<LocationH3Cell> h3Cells = new ArrayList<>();

    public Location() {}

    public Long getId()  {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public Policy getPolicyId() {
        return policy;
    }
    public void setPolicyId(Policy policyId) {
        this.policy = policyId;
    }

    public Boolean getIsActive() {
        return isActive;
    }
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public List<LocationH3Cell> getH3Cells() {
        return h3Cells;
    }
    public void setH3Cells(List<LocationH3Cell> h3Cells) {
        this.h3Cells = h3Cells;
    }
}
