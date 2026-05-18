package at.fhtw.alap.release;

import at.fhtw.alap.release.dto.ReleaseRunResponse;
import at.fhtw.alap.release.dto.ReleasedAggregationResponse;
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
    public List<ReleasedAggregationResponse> getReleasedAggregations() {
        return releaseService.getAllReleased();
    }

    @GetMapping("/released/location/{locationId}")
    public List<ReleasedAggregationResponse> getReleasedAggregationsByLocation(@PathVariable Long locationId) {
        return releaseService.getReleasedByLocation(locationId);
    }

    @GetMapping("/suppressed")
    public List<ReleasedAggregationResponse> getSuppressedAggregations() {
        return releaseService.getAllSuppressed();
    }
}