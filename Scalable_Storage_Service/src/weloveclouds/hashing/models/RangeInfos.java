package weloveclouds.hashing.models;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Stores <IP, port> and <hash-range> triplets, which defines respective server (denoted by its
 * <ip,port>) is responsible for which hash range.
 * 
 * @author Benedek
 */
public class RangeInfos {

    public static String FIELD_DELIMITER = "-\r-";

    private Set<RangeInfo> rangeInfos;

    public RangeInfos() {
        this.rangeInfos = new HashSet<>();
    }

    public RangeInfos(Set<RangeInfo> rangeInfos) {
        this.rangeInfos = rangeInfos;
    }

    public void addRangeInfo(RangeInfo info) {
        rangeInfos.add(info);
    }

    public void removeRangeInfo(RangeInfo info) {
        rangeInfos.remove(info);
    }

    /**
     * Get that server's details which handles the responsible hash value.
     * 
     * @return the range information (IP+port+range) of that server which handles the respective
     *         hash value
     */
    public RangeInfo findRangeInfoByHash(Hash hash) {
        for (RangeInfo rangeInfo : rangeInfos) {
            if (rangeInfo.rangeContains(hash)) {
                return rangeInfo;
            }
        }
        return null;
    }

    public Set<RangeInfo> getRangeInfos() {
        return Collections.unmodifiableSet(rangeInfos);
    }

    public String toStringWithDelimiter() {
        return toStringWithDelimiter(FIELD_DELIMITER);
    }

    private String toStringWithDelimiter(String delimiter) {
        StringBuilder sb = new StringBuilder();
        for (RangeInfo info : rangeInfos) {
            sb.append(info);
            sb.append(delimiter);
        }
        sb.setLength(sb.length() - delimiter.length());
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{Range infos: {");
        String delimiter = ", ";
        sb.append(toStringWithDelimiter(delimiter));
        sb.append("}}");
        return sb.toString();
    }

}
