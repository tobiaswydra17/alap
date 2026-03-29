package at.fhtw.alap.ingestion.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public class LocationEventRequest {
    @NotBlank(message = "userHash must not be blank")
    private String userHash;

    @NotNull(message = "timestamp must not be null")
    private Instant timestamp;

    @NotNull(message = "latitude must not be null")
    @DecimalMin(value = "-90.0", message = "latitude must be >= -90")
    @DecimalMax(value = "90.0", message = "latitude must be <= 90")
    private Double latitude;

    @NotNull(message = "longitude must not be null")
    @DecimalMin(value = "-180.0", message = "longitude must be >= -180")
    @DecimalMax(value = "180.0", message = "longitude must be <= 180")
    private Double longitude;

    LocationEventRequest() {}

    public LocationEventRequest(String userHash, Instant timestamp, Double latitude, Double longitude) {
        this.userHash = userHash;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getUserHash() {
        return userHash;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
