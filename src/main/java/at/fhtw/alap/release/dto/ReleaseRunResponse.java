package at.fhtw.alap.release.dto;

public class ReleaseRunResponse {

    private int processedBuckets;
    private int releasedBuckets;
    private int suppressedBuckets;
    private int skippedOpenBuckets;

    public ReleaseRunResponse() {
    }

    public ReleaseRunResponse(int processedBuckets, int releasedBuckets, int suppressedBuckets, int skippedOpenBuckets) {
        this.processedBuckets = processedBuckets;
        this.releasedBuckets = releasedBuckets;
        this.suppressedBuckets = suppressedBuckets;
        this.skippedOpenBuckets = skippedOpenBuckets;
    }

    public int getProcessedBuckets() {
        return processedBuckets;
    }

    public void setProcessedBuckets(int processedBuckets) {
        this.processedBuckets = processedBuckets;
    }

    public int getReleasedBuckets() {
        return releasedBuckets;
    }

    public void setReleasedBuckets(int releasedBuckets) {
        this.releasedBuckets = releasedBuckets;
    }

    public int getSuppressedBuckets() {
        return suppressedBuckets;
    }

    public void setSuppressedBuckets(int suppressedBuckets) {
        this.suppressedBuckets = suppressedBuckets;
    }

    public int getSkippedOpenBuckets() {
        return skippedOpenBuckets;
    }

    public void setSkippedOpenBuckets(int skippedOpenBuckets) {
        this.skippedOpenBuckets = skippedOpenBuckets;
    }
}