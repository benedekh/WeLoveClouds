package weloveclouds.ecs.models.repository;

import java.util.concurrent.locks.ReentrantReadWriteLock;

import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.loadbalancer.models.NodeHealthInfos;

/**
 * Created by Benoit on 2016-12-09.
 */
public abstract class AbstractNode {
    private final ReentrantReadWriteLock healthInfoReadWriteLock = new ReentrantReadWriteLock();
    protected String name;
    protected NodeStatus status;
    protected ServerConnectionInfo serverConnectionInfo;
    protected ServerConnectionInfo ecsChannelConnectionInfo;
    protected NodeHealthInfos healthInfos;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NodeStatus getStatus() {
        return status;
    }

    public void setStatus(NodeStatus status) {
        this.status = status;
    }

    public ServerConnectionInfo getServerConnectionInfo() {
        return serverConnectionInfo;
    }

    public ServerConnectionInfo getEcsChannelConnectionInfo() {
        return ecsChannelConnectionInfo;
    }

    public NodeHealthInfos getHealthInfos() {
        try {
            healthInfoReadWriteLock.readLock().lock();
            return healthInfos;
        } finally {
            healthInfoReadWriteLock.readLock().unlock();
        }
    }

    public void updateHealthInfos(NodeHealthInfos healthInfos) {
        if (healthInfos != null) {
            try {
                healthInfoReadWriteLock.writeLock().lock();
                this.healthInfos = healthInfos;
            } finally {
                healthInfoReadWriteLock.writeLock().unlock();
            }
        }
    }

    public String getIpAddress() {
        return serverConnectionInfo.getIpAddress().toString().replace("/", "");
    }

    public int getPort() {
        return serverConnectionInfo.getPort();
    }
}
