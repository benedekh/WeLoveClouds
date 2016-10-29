package weloveclouds.communication.api.kv;

public class KVMessage implements IKVMessage {

    private String key;
    private String value;
    private StatusType status;

    public KVMessage(String key, String value, StatusType status) {
        this.key = key;
        this.value = value;
        this.status = status;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public StatusType getStatus() {
        return status;
    }

}
