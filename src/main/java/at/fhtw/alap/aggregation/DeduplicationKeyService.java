package at.fhtw.alap.aggregation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HexFormat;

@Service
public class DeduplicationKeyService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final String secret;

    public DeduplicationKeyService(@Value("${alap.dedup.secret}") String secret) {
        this.secret = secret;
    }

    public String generateDedupKey(String userHash, Long locationId, Instant timeBucketStart) {
        try {
            String input = userHash + "|" + locationId + "|" + timeBucketStart.toString();

            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
            mac.init(secretKeySpec);

            byte[] hmacBytes = mac.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hmacBytes);

        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate deduplication key", e);
        }
    }
}