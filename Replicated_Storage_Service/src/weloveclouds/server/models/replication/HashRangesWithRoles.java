package weloveclouds.server.models.replication;

import java.util.Set;

import weloveclouds.kvstore.serialization.helper.ISerializer;

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
        return rangesWithRoles;
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
        ISerializer<String, HashRangeWithRole> defaultSerializer =
                new ISerializer<String, HashRangeWithRole>() {
                    @Override
                    public String serialize(HashRangeWithRole target) {
                        return target.toString();
                    }
                };

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        String delimiter = ", ";
        sb.append(toStringWithDelimiter(delimiter, defaultSerializer));
        sb.append("}");

        return sb.toString();
    }

    /**
     * Converts the object to String.
     * 
     * @param delimiter separator character among the fields
     * @param rangeWithRoleSerializer to convert the {@link HashRangeWithRole} into a String
     *        representation
     */
    public String toStringWithDelimiter(String delimiter,
            ISerializer<String, HashRangeWithRole> rangeWithRoleSerializer) {
        StringBuilder sb = new StringBuilder();
        for (HashRangeWithRole rangeWithRole : rangesWithRoles) {
            sb.append(rangeWithRoleSerializer.serialize(rangeWithRole));
            sb.append(delimiter);
        }
        sb.setLength(sb.length() - delimiter.length());
        return sb.toString();
    }

}
