package weloveclouds.loadbalancer.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Benoit on 2016-12-21.
 */
public class EcsNotification {
    private List<String> unrespondingNodesNames;

    protected EcsNotification(Builder builder) {
        this.unrespondingNodesNames = builder.unresponsiveNodesNames;
    }

    public static class Builder {
        private List<String> unresponsiveNodesNames;

        public Builder() {
            this.unresponsiveNodesNames = new ArrayList<>();
        }

        public Builder addUnrespondingNodeName(String nodeName) {
            this.unresponsiveNodesNames.add(nodeName);
            return this;
        }

        public EcsNotification build() {
            return new EcsNotification(this);
        }
    }
}
