package weloveclouds.hashing.models;

import weloveclouds.client.utils.CustomStringJoiner;

/**
 * The following properties should be held for the range:<br>
 * (1) end >= start, and <br>
 * (2) end <= {@link Hash#MAX_VALUE} && start >= {@link Hash#MIN_VALUE}, UNLESS <br>
 * (3) start >= end, because they wrap over. But in this case (2) should hold with slight changes:
 * start <= {@link Hash#MAX_VALUE} && end >= {@link Hash#MIN_VALUE}.
 * 
 * The ends of the range are inclusive.
 * 
 * @author Benedek
 */
public class HashRange {

    private Hash start;
    private Hash end;

    public HashRange(Hash start, Hash end) {
        this.start = start;
        this.end = end;
    }

    public boolean contains(Hash target) {
        if (end.compareTo(start) > 0) {
            // if the range does not wrap over
            return (target.compareTo(start) >= 0) && (target.compareTo(end) <= 0);
        } else {
            // if the range wraps over
            if (target.compareTo(Hash.MAX_VALUE) <= 0 && target.compareTo(start) >= 0) {
                return true;
            } else if (target.compareTo(Hash.MIN_VALUE) >= 0 && target.compareTo(end) <= 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    public String toStringWithDelimiter(String delimiter) {
        return CustomStringJoiner.join(delimiter, start.toString(), end.toString());
    }

    @Override
    public String toString() {
        return CustomStringJoiner.join("", "(", start.toString(), ",", end.toString(), ")");
    }

}
