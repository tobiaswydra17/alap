package at.fhtw.alap.release.dto;

import at.fhtw.alap.release.ReleaseStatus;

import java.time.Instant;

public class ReleasedAggregationResponse {

    private Long locationId;
    private String locationName;
    private Instant timeBucketStart;
    private Instant timeBucketEnd;
    private Long uniqueUserCount;
    private ReleaseStatus releaseStatus;

    public ReleasedAggregationResponse() {
    }

    public ReleasedAggregationResponse(
            Long locationId,
            String locationName,
            Instant timeBucketStart,
            Instant timeBucketEnd,
            Long uniqueUserCount,
            ReleaseStatus releaseStatus
    ) {
        this.locationId = locationId;
        this.locationName = locationName;
        this.timeBucketStart = timeBucketStart;
        this.timeBucketEnd = timeBucketEnd;
        this.uniqueUserCount = uniqueUserCount;
        this.releaseStatus = releaseStatus;
    }

    public Long getLocationId() {
        return locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public Instant getTimeBucketStart() {
        return timeBucketStart;
    }

    public Instant getTimeBucketEnd() {
        return timeBucketEnd;
    }

    public Long getUniqueUserCount() {
        return uniqueUserCount;
    }

    public ReleaseStatus getReleaseStatus() {
        return releaseStatus;
    }
}