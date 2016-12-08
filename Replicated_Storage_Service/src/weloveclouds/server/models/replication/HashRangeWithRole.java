package weloveclouds.server.models.replication;

import weloveclouds.client.utils.CustomStringJoiner;
import weloveclouds.hashing.models.HashRange;
import weloveclouds.kvstore.serialization.helper.ISerializer;

/**
 * Encapsulates a {@link HashRange} with a {@link Role} that represents the role of the node
 * regarding that hash range.
 * 
 * @author Benedek
 */
public class HashRangeWithRole {

    private HashRange hashRange;
    private Role role;

    protected HashRangeWithRole(Builder builder) {
        this.hashRange = builder.hashRange;
        this.role = builder.role;
    }

    public HashRange getHashRange() {
        return hashRange;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((hashRange == null) ? 0 : hashRange.hashCode());
        result = prime * result + ((role == null) ? 0 : role.hashCode());
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
        if (!(obj instanceof HashRangeWithRole)) {
            return false;
        }
        HashRangeWithRole other = (HashRangeWithRole) obj;
        if (hashRange == null) {
            if (other.hashRange != null) {
                return false;
            }
        } else if (!hashRange.equals(other.hashRange)) {
            return false;
        }
        if (role != other.role) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return CustomStringJoiner.join("", "{", "Range: ", hashRange.toString(), ", Role: ",
                role.toString(), "}");
    }

    /**
     * Converts the object to String.
     * 
     * @param delimiter separator character among the fields
     * @param rangeSerializer to convert the {@link HashRange} into a String representation
     */
    public String toStringWithDelimiter(String delimiter,
            ISerializer<String, HashRange> rangeSerializer) {
        return CustomStringJoiner.join(delimiter, rangeSerializer.serialize(hashRange),
                role.toString());
    }

    /**
     * Builder pattern to create a new {@link HashRangeWithRole} instance.
     * 
     * @author Benedek
     */
    public static class Builder {
        private HashRange hashRange;
        private Role role;

        public Builder hashRange(HashRange hashRange) {
            this.hashRange = hashRange;
            return this;
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public HashRangeWithRole build() {
            return new HashRangeWithRole(this);
        }

    }

}
