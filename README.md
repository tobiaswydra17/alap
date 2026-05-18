# ALAP – Anonymous Location Aggregation Pipeline

ALAP (Anonymous Location Aggregation Pipeline) is a backend-focused prototype for the anonymous aggregation of location data.

The system is designed to process incoming location events while minimizing privacy risks through **privacy-by-design principles**. Instead of storing raw coordinates or persistent user tracking data, location events are immediately transformed into **aggregated space-time buckets**.

The project is developed as part of a bachelor's thesis focusing on the question:

> How can a backend system for anonymous location aggregation be designed and implemented in a way that avoids storing persistent personal location data while still enabling meaningful location analytics?

---

## Project Status

⚠️ **Current Status: Prototype / Testing Phase**

ALAP is currently in an experimental prototype stage and is primarily intended for:

- architecture validation
- privacy-by-design experimentation
- aggregation logic testing
- bachelor thesis evaluation

The system is **not production-ready** and currently focuses on validating the technical feasibility of anonymous location aggregation.

---

## Core Concept

Instead of storing exact GPS coordinates and movement histories, incoming events are:

1. received through an ingestion API
2. mapped to an H3 spatial cell
3. matched against predefined location cells
4. assigned to a predefined time bucket
5. deduplicated per user and bucket
6. aggregated anonymously
7. released only if a minimum threshold is reached

Only released aggregations are intended to be publicly accessible.

---

## Privacy-by-Design Principles

ALAP follows a strict **data minimization approach**.

### What is NOT stored

❌ Raw GPS coordinates  
❌ Persistent user identifiers  
❌ User movement trajectories  
❌ Individual visit histories

### What IS stored

✅ Aggregated visitor counts  
✅ Space-time bucket information  
✅ Temporary deduplication keys  
✅ Released aggregation results

Raw location data only exists **transiently during request processing** and is discarded immediately after aggregation.

---

## Architecture Overview

```text
Location Event
      ↓
Ingestion API
      ↓
H3 Cell Mapping
      ↓
Location Matching
      ↓
Time Bucket Calculation
      ↓
Deduplication
      ↓
Aggregation Counter
      ↓
k-Threshold Release Check
      ↓
ReleasedAggregation
```
## Technology Stack

- **Java 21**
- **Spring Boot 3.5**
- **PostgreSQL 16**
- **JPA / Hibernate**
- **Docker**
- **Uber H3 Spatial Index**

---

## Current Package Structure

```text
config/
location/
policy/
ingestion/
aggregation/
release/
api/
common/
```

---

## Data Model

### Policy

Defines aggregation behavior:

- H3 resolution
- time bucket size
- k-threshold
- multi-assignment behavior

### Location

Represents a predefined aggregation target (e.g., venue or hotspot).

### LocationH3Cell

Stores H3 cell IDs associated with locations.

### AggregationCounter

Tracks aggregated counts per:

```text
(location_id + time_bucket_start)
```

### BucketPresence

Temporary deduplication entity used to prevent double counting.

Stores:

```text
dedupKey
expiresAt
```

The deduplication key is derived from:

```text
user_hash + location_id + time_bucket_start
```

using an HMAC-based approach.

### ReleasedAggregation

Stores only finalized aggregation outputs.

Contains:

```text
location_id
time_bucket_start
time_bucket_end
unique_user_count
release_status
```

---

## Data Seeder

The project currently uses a **DataSeeder** for test and prototype purposes.

The `DataSeeder` is responsible for creating:

- predefined policies
- test locations
- associated H3 cells

This allows quick local testing without requiring manual configuration or admin interfaces.

At the current stage, locations and policies are intentionally managed through seeded data instead of a dedicated configuration API.

---

## Example Use Case

ALAP is evaluated using a **nightlife analytics scenario**.

Example:

A nightlife application wants to display anonymous crowd activity at partner locations such as:

- bars
- clubs
- venues

Instead of tracking individual users, the system only stores aggregated results like:

```text
14 users at Location A
between 20:00–20:30
```

This enables:

- heatmaps
- crowd estimation
- trend analysis
- activity distributions

without storing identifiable movement histories.

---

## Example Released Output

```json
{
  "location": "Kaktusbar",
  "timeBucketStart": "2026-05-10T20:00:00",
  "timeBucketEnd": "2026-05-10T20:30:00",
  "uniqueUserCount": 14,
  "status": "RELEASED"
}
```

---

## Limitations (Current Prototype)

Current limitations include:

- synchronous event processing
- no admin interface for policies or locations
- manually seeded test data
- no distributed processing
- limited scalability optimizations

These limitations are intentional and reflect the prototype nature of the system.

---

## Research Focus

The main research contribution of ALAP lies in the idea that:

> privacy risks can be reduced not only through anonymization techniques, but also through architectural decisions regarding what data is even stored in the first place.

The prototype investigates whether predefined spatial and temporal aggregation, combined with privacy-preserving release mechanisms, can retain analytical usefulness while significantly reducing re-identification risks.

---

## License

Educational / Research Prototype  
Bachelor Thesis Project – FH Technikum Wien