package at.fhtw.alap.release;

import at.fhtw.alap.release.dto.ReleaseRunResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/releases")
public class ReleaseController {

    private final ReleaseService releaseService;

    public ReleaseController(ReleaseService releaseService) {
        this.releaseService = releaseService;
    }

    @PostMapping("/run")
    public ReleaseRunResponse runRelease() {
        return releaseService.runRelease();
    }

    @GetMapping("/released")
    public List<ReleasedAggregation> getReleasedAggregations() {
        return releaseService.getAllReleased();
    }

    @GetMapping("/suppressed")
    public List<ReleasedAggregation> getSuppressedAggregations() {
        return releaseService.getAllSuppressed();
    }

    @GetMapping("/released/location/{locationId}")
    public List<ReleasedAggregation> getReleasedAggregationsByLocation(@PathVariable Long locationId) {
        return releaseService.getReleasedByLocation(locationId);
    }
}