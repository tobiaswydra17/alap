package at.fhtw.alap.location;

import jakarta.persistence.*;

import jakarta.persistence.Entity;

@Entity
@Table(name = "location_h3_cells")
public class LocationH3Cell {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "h3_cell_id", nullable = false, length = 32)
    private String h3CellId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    public LocationH3Cell() {}

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getH3CellId() {
        return h3CellId;
    }
    public void setH3CellId(String h3CellId) {
        this.h3CellId = h3CellId;
    }

    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        this.location = location;
    }
}
