package weloveclouds.server.requests.kvserver.transaction.utils;

public interface ITransactionRestorationRequest {

    public void schedule();

    public void unschedule();

}
