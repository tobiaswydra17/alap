package at.fhtw.alap.location;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationH3CellRepository extends JpaRepository<LocationH3Cell, Long> {

    List<LocationH3Cell> findByH3CellId(String h3CellId);

    List<LocationH3Cell> findByLocation_Id(Long locationId);
}
