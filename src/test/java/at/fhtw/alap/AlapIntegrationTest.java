package at.fhtw.alap;

import at.fhtw.alap.location.H3Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AlapIntegrationTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private H3Service h3Service;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM released_aggregations");
        jdbcTemplate.execute("DELETE FROM aggregation_counter");
        jdbcTemplate.execute("DELETE FROM bucket_presences");
        jdbcTemplate.execute("DELETE FROM location_h3_cells");
        jdbcTemplate.execute("DELETE FROM location");
        jdbcTemplate.execute("DELETE FROM policy");

        jdbcTemplate.update("""
            INSERT INTO policy (id, allow_multi_assignment, h3_resolution, k_threshold, name, time_bucket_minutes)
            VALUES (?, ?, ?, ?, ?, ?)
        """, 1L, true, 9, 2, "default_policy", 15);

        jdbcTemplate.update("""
            INSERT INTO location (id, is_active, name, policy_id, type)
            VALUES (?, ?, ?, ?, ?)
        """, 1L, true, "Stephansplatz Test Location", 1L, "POI");

        String matchingH3Cell = h3Service.latLngToCell(48.2082, 16.3738, 9);

        jdbcTemplate.update("""
            INSERT INTO location_h3_cells (id, h3_cell_id, location_id)
            VALUES (?, ?, ?)
        """, 1L, matchingH3Cell, 1L);
    }

    @Test
    void shouldIngestDeduplicateAggregateAndReleaseBucketsCorrectly() throws Exception {
        Instant releasedBucketTimestamp1 = Instant.now()
                .minus(2, ChronoUnit.HOURS)
                .truncatedTo(ChronoUnit.HOURS)
                .plus(5, ChronoUnit.MINUTES);

        Instant releasedBucketTimestamp2 = Instant.now()
                .minus(3, ChronoUnit.HOURS)
                .truncatedTo(ChronoUnit.HOURS)
                .plus(5, ChronoUnit.MINUTES);

        Map<String, Object> event1 = Map.of(
                "userHash", "userA",
                "timestamp", releasedBucketTimestamp1.toString(),
                "latitude", 48.2082,
                "longitude", 16.3738
        );

        Map<String, Object> duplicateEvent1 = Map.of(
                "userHash", "userA",
                "timestamp", releasedBucketTimestamp1.toString(),
                "latitude", 48.2082,
                "longitude", 16.3738
        );

        Map<String, Object> event2 = Map.of(
                "userHash", "userB",
                "timestamp", releasedBucketTimestamp1.toString(),
                "latitude", 48.2082,
                "longitude", 16.3738
        );

        Map<String, Object> suppressedEvent = Map.of(
                "userHash", "userC",
                "timestamp", releasedBucketTimestamp2.toString(),
                "latitude", 48.2082,
                "longitude", 16.3738
        );

        mockMvc.perform(post("/api/events/location")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/events/location")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateEvent1)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/events/location")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(event2)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/events/location")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(suppressedEvent)))
                .andExpect(status().isOk());

        Integer bucketPresenceCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM bucket_presences",
                Integer.class
        );
        assertEquals(3, bucketPresenceCount);

        Integer releasedLikeCounterCount = jdbcTemplate.queryForObject("""
            SELECT COUNT(*) FROM aggregation_counter WHERE unique_user_count = 2
        """, Integer.class);

        Integer suppressedLikeCounterCount = jdbcTemplate.queryForObject("""
            SELECT COUNT(*) FROM aggregation_counter WHERE unique_user_count = 1
        """, Integer.class);

        assertNotNull(releasedLikeCounterCount);
        assertNotNull(suppressedLikeCounterCount);
        assertEquals(1, releasedLikeCounterCount);
        assertEquals(1, suppressedLikeCounterCount);

        mockMvc.perform(post("/api/releases/run"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processedBuckets", is(2)))
                .andExpect(jsonPath("$.releasedBuckets", is(1)))
                .andExpect(jsonPath("$.suppressedBuckets", is(1)))
                .andExpect(jsonPath("$.skippedOpenBuckets", is(0)));

        Integer releasedCount = jdbcTemplate.queryForObject("""
            SELECT COUNT(*) FROM released_aggregations WHERE release_status = 'RELEASED'
        """, Integer.class);

        Integer suppressedCount = jdbcTemplate.queryForObject("""
            SELECT COUNT(*) FROM released_aggregations WHERE release_status = 'SUPPRESSED'
        """, Integer.class);

        assertEquals(1, releasedCount);
        assertEquals(1, suppressedCount);

        mockMvc.perform(get("/api/releases/released"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/releases/suppressed"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/releases/released/location/1"))
                .andExpect(status().isOk());
    }
}