package at.fhtw.alap.aggregation;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class BucketCalculator {

    public Instant calculateBucketStart(Instant timestamp, int timeBucketMinutes) {
        if (timestamp == null) {
            throw new IllegalArgumentException("timestamp must not be null");
        }

        if (timeBucketMinutes <= 0) {
            throw new IllegalArgumentException("timeBucketMinutes must be greater than 0");
        }

        long bucketSizeSeconds = timeBucketMinutes * 60L;
        long epochSeconds = timestamp.getEpochSecond();
        long bucketStartEpochSeconds = (epochSeconds / bucketSizeSeconds) * bucketSizeSeconds;

        return Instant.ofEpochSecond(bucketStartEpochSeconds);
    }
}
