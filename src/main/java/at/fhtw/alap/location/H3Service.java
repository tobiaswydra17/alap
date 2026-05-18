package at.fhtw.alap.location;

import com.uber.h3core.H3Core;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class H3Service {

    private final H3Core h3;

    public H3Service() {
        try {
            this.h3 = H3Core.newInstance();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to initialize H3", e);
        }
    }

    public String latLngToCell(double latitude, double longitude, int resolution) {
        validateResolution(resolution);
        return h3.latLngToCellAddress(latitude, longitude, resolution);
    }

    private void validateResolution(int resolution) {
        if (resolution < 0 || resolution > 15) {
            throw new IllegalArgumentException("H3 resolution must be between 0 and 15");
        }
    }
}
