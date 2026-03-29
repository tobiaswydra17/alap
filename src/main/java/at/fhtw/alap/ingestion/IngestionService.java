package at.fhtw.alap.ingestion;

import at.fhtw.alap.ingestion.dto.LocationEventRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IngestionService {

    @Transactional
    public void ingest(LocationEventRequest locationEventRequest) {

    }
}
