package at.fhtw.alap.ingestion;

import at.fhtw.alap.ingestion.dto.LocationEventRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class IngestionController {

    private final IngestionService ingestionService;

    public IngestionController(IngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping("/location")
    public ResponseEntity<Void> ingestLocationEvent(@Valid @RequestBody LocationEventRequest request) {
        ingestionService.ingest(request);
        return ResponseEntity.ok().build();
    }
}
