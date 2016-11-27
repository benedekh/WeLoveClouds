package weloveclouds.server.models.conf;

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

    private KVServerPortContext(Builder builder) {
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
