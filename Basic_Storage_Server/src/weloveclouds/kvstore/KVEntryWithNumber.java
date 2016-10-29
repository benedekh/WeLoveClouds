package weloveclouds.kvstore;

public class KVEntryWithNumber implements Comparable<KVEntryWithNumber> {

    private KVEntry entry;
    private long number;

    public KVEntryWithNumber(KVEntry entry, long number) {
        this.entry = entry;
        this.number = number;
    }

    public KVEntry getEntry() {
        return entry;
    }

    public long getNumber() {
        return number;
    }

    public void increaseNumber() {
        number += 1;
    }

    public void decreaseNumber() {
        number -= 1;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((entry == null) ? 0 : entry.hashCode());
        result = prime * result + (int) (number ^ (number >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        KVEntryWithNumber other = (KVEntryWithNumber) obj;
        if (entry == null) {
            if (other.entry != null)
                return false;
        } else if (!entry.equals(other.entry))
            return false;
        if (number != other.number)
            return false;
        return true;
    }

    @Override
    public int compareTo(KVEntryWithNumber other) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        // sort only by the number
        if (number < other.number) {
            return BEFORE;
        } else if (number == other.number) {
            return EQUAL;
        } else {
            return AFTER;
        }
    }



}
