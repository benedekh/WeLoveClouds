package weloveclouds.server.services.transaction.tasks;

import java.util.concurrent.ExecutionException;

/**
 * A task that can be executed for a transaction.
 * 
 * @author Benedek
 */
public interface ITransactionTask {

    public void execute() throws InterruptedException, ExecutionException;

}
