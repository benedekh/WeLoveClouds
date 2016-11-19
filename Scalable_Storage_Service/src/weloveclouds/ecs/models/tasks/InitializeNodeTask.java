package weloveclouds.ecs.models.tasks;

import weloveclouds.ecs.exceptions.ClientSideException;
import weloveclouds.ecs.exceptions.task.RetryableException;
import weloveclouds.ecs.models.commands.ICommand;

/**
 * Created by Benoit on 2016-11-19.
 */
public class InitializeNodeTask extends AbstractRetryableTask {
    public InitializeNodeTask(int maxNumberOfRetries, ICommand command) {
        super(maxNumberOfRetries, command, null, null);
    }

    @Override
    public void run() throws ClientSideException, RetryableException {
        try {
            this.command.execute();
        } catch (ClientSideException e) {
            if (numberOfAttempt < maxNumberOfRetries) {
                numberOfAttempt++;
                throw new RetryableException("Retry attempt: " + numberOfAttempt + " on: " +
                        maxNumberOfRetries, e);
            } else {
                throw new ClientSideException("Maximum number of attempt reached, will not " +
                        "retry", e);
            }
        }
    }
}
