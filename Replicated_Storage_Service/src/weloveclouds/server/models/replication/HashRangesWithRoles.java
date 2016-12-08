package weloveclouds.server.models.replication;

import java.util.Collections;
import java.util.Set;

/**
 * Encapsulates a {@link HashRangeWithRole} instances that represents the roles of the node
 * regarding those hash ranges.
 * 
 * @author Benedek
 */
public class HashRangesWithRoles {

    private Set<HashRangeWithRole> rangesWithRoles;

    public HashRangesWithRoles(Set<HashRangeWithRole> rangesWithRoles) {
        this.rangesWithRoles = rangesWithRoles;
    }

    public Set<HashRangeWithRole> getRangesWithRoles() {
        return Collections.unmodifiableSet(rangesWithRoles);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((rangesWithRoles == null) ? 0 : rangesWithRoles.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof HashRangesWithRoles)) {
            return false;
        }
        HashRangesWithRoles other = (HashRangesWithRoles) obj;
        if (rangesWithRoles == null) {
            if (other.rangesWithRoles != null) {
                return false;
            }
        } else if (!rangesWithRoles.equals(other.rangesWithRoles)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String delimiter = ", ";

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (HashRangeWithRole rangeWithRole : rangesWithRoles) {
            sb.append(rangeWithRole);
            sb.append(delimiter);
        }
        sb.setLength(sb.length() - delimiter.length());
        sb.append("}");

        return sb.toString();
    }
}
