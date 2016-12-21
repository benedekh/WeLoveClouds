package weloveclouds.server.services.transaction.tasks;

import java.util.concurrent.ExecutionException;

public interface ITransactionTask {
    
    public void execute() throws InterruptedException, ExecutionException;

}
