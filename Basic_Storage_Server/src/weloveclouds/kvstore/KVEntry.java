package weloveclouds.kvstore;

import java.io.Serializable;

public class KVEntry implements Serializable {

    private static final long serialVersionUID = 3871522756840647625L;

    private String key;
    private String value;

    public KVEntry(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean keyIsNull(){
        return key == null;
    }

}
