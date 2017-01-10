package weloveclouds.server.configuration.models;

import weloveclouds.server.core.KVServer;

/**
 * A configuration context which stores the port numbers on which the {@link KVServer} serves the
 * requests coming from the different 'clients' (e.g. KVClient, KVServer, KVECS).
 * 
 * @author Benedek
 */
public class KVServerPortContext {

    private int kvClientPort;
    private int kvServerPort;
    private int kvECSPort;

    public KVServerPortContext() {
        this.kvClientPort = 0;
        this.kvServerPort = 0;
        this.kvECSPort = 0;
    }

    protected KVServerPortContext(Builder builder) {
        this.kvClientPort = builder.kvClientPort;
        this.kvServerPort = builder.kvServerPort;
        this.kvECSPort = builder.kvECSPort;
    }

    public int getKVClientPort() {
        return kvClientPort;
    }

    public int getKVServerPort() {
        return kvServerPort;
    }

    public int getKVECSPort() {
        return kvECSPort;
    }

    public void setKvClientPort(int kvClientPort) {
        this.kvClientPort = kvClientPort;
    }

    public void setKvServerPort(int kvServerPort) {
        this.kvServerPort = kvServerPort;
    }

    public void setKvECSPort(int kvECSPort) {
        this.kvECSPort = kvECSPort;
    }

    /**
     * Builder pattern for creating a {@link KVServerPortContext} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private int kvClientPort;
        private int kvServerPort;
        private int kvECSPort;

        public Builder clientPort(int kvClientPort) {
            this.kvClientPort = kvClientPort;
            return this;
        }

        public Builder serverPort(int kvServerPort) {
            this.kvServerPort = kvServerPort;
            return this;
        }

        public Builder ecsPort(int kvECSPort) {
            this.kvECSPort = kvECSPort;
            return this;
        }

        public KVServerPortContext build() {
            return new KVServerPortContext(this);
        }
    }

}
