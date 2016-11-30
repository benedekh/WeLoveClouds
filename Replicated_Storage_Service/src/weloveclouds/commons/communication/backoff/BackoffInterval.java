package weloveclouds.commons.communication.backoff;

import static weloveclouds.client.utils.CustomStringJoiner.join;

public class BackoffInterval {

    private static final int SIXTY = 60;
    private static final int THOUSAND = 1000;

    private long intervalLengthInMillis;

    public BackoffInterval(long intervalLengthInMillis) {
        this.intervalLengthInMillis = intervalLengthInMillis;
    }

    public long getHours() {
        return getMinutes() / SIXTY;
    }

    public long getMinutes() {
        return getSeconds() / SIXTY;
    }

    public long getSeconds() {
        return intervalLengthInMillis / THOUSAND;
    }

    public long getMillis() {
        return intervalLengthInMillis;
    }

    public long getMicros() {
        return intervalLengthInMillis * THOUSAND;
    }

    public long getNano() {
        return getMicros() * THOUSAND;
    }

    @Override
    public String toString() {
        return join("", "Interval in milliseconds: ", String.valueOf(intervalLengthInMillis));
    }

}
