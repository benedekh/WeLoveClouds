package weloveclouds.server.services.replication;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import weloveclouds.commons.kvstore.models.KVEntry;
import weloveclouds.communication.models.ServerConnectionInfo;
import weloveclouds.server.services.replication.request.AbstractReplicationRequest;
import weloveclouds.server.services.replication.request.ReplicationRequestFactory;
import weloveclouds.server.services.transaction.ITransactionSenderService;

public class ReplicationService implements IReplicationService, Runnable {

    private static final int MAX_NUMBER_OF_WAITING_REQUESTS = 20;
    private static final Seconds MAX_WAITING_TIME = Seconds.seconds(2);

    private Queue<AbstractReplicationRequest<?, ?>> awaitingRequests;
    private ITransactionSenderService transactionSenderService;
    private ReplicationRequestFactory replicationFactory;

    private Set<ServerConnectionInfo> replicaConnectionInfos;
    private Set<ServerConnectionInfo> latestReplicaConnectionInfos;

    private Thread runner;
    private volatile boolean halted;

    public ReplicationService(Builder builder) {
        this.transactionSenderService = builder.transactionSenderService;
        this.replicationFactory = builder.replicationFactory;

        this.awaitingRequests = new LinkedBlockingQueue<>();
        this.replicaConnectionInfos = new HashSet<>();
        this.latestReplicaConnectionInfos = new HashSet<>();
    }

    @Override
    public void start() {
        if (!halted) {
            runner = new Thread(this);
            runner.start();
        }
    }

    @Override
    public void halt() {
        if (isRunning()) {
            runner.interrupt();
            halted = true;
        }
        awaitingRequests.clear();
        this.replicaConnectionInfos.clear();
    }

    @Override
    public boolean isRunning() {
        if (runner == null) {
            return false;
        }
        return runner.isAlive();
    }

    @Override
    public void putEntryOnReplicas(KVEntry entry) {
        startIfNotRunning();
        awaitingRequests.add(replicationFactory.createPutReplicationRequest(entry));
    }

    @Override
    public void removeEntryOnReplicas(String key) {
        startIfNotRunning();
        awaitingRequests.add(replicationFactory.createDeleteReplicationRequest(key));
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted() && !halted) {
            waitUntilThereAreEnoughRequests();
            AbstractReplicationRequest<?, ?> request = awaitingRequests.poll();
            transactionSenderService.executeTransactionsFor(request, replicaConnectionInfos);
            if (awaitingRequests.isEmpty()) {
                updateReplicaConnectionInfos();
            }
        }
    }

    @Override
    public void updateReplicaConnectionInfos(Set<ServerConnectionInfo> replicaConnectionInfos) {
        if (replicaConnectionInfos == null) {
            halt();
        } else {
            if (isRunning()) {
                synchronized (latestReplicaConnectionInfos) {
                    latestReplicaConnectionInfos.addAll(replicaConnectionInfos);
                }
            } else {
                this.replicaConnectionInfos.addAll(replicaConnectionInfos);
            }
        }
    }

    private void startIfNotRunning() {
        if (!isRunning()) {
            start();
        }
    }

    private void waitUntilThereAreEnoughRequests() {
        do {
            DateTime start = DateTime.now();
            while (!enoughTimeElapsed(start) && !hasEnoughWaitingRequests());
        } while (awaitingRequests.isEmpty());
    }

    private boolean enoughTimeElapsed(DateTime sinceStart) {
        Seconds elapsedTime = Seconds.secondsBetween(sinceStart, DateTime.now());
        return elapsedTime.equals(MAX_WAITING_TIME) || elapsedTime.isGreaterThan(MAX_WAITING_TIME);
    }

    private boolean hasEnoughWaitingRequests() {
        return awaitingRequests.size() >= MAX_NUMBER_OF_WAITING_REQUESTS;
    }

    private void updateReplicaConnectionInfos() {
        synchronized (latestReplicaConnectionInfos) {
            if (!latestReplicaConnectionInfos.isEmpty()) {
                replicaConnectionInfos.clear();
                replicaConnectionInfos.addAll(latestReplicaConnectionInfos);
                latestReplicaConnectionInfos.clear();
            }
        }
    }

    /**
     * Builder pattern for creating a {@link ReplicationService} instance.
     *
     * @author Benedek
     */
    public static class Builder {
        private ITransactionSenderService transactionSenderService;
        private ReplicationRequestFactory replicationFactory;

        public Builder transactionSenderService(
                ITransactionSenderService transactionSenderService) {
            this.transactionSenderService = transactionSenderService;
            return this;
        }

        public Builder statefulReplicationFactory(ReplicationRequestFactory replicationFactory) {
            this.replicationFactory = replicationFactory;
            return this;
        }

        public ReplicationService build() {
            return new ReplicationService(this);
        }
    }


}
