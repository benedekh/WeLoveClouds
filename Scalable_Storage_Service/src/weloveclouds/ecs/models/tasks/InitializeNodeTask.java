package weloveclouds.ecs.models.tasks;

import weloveclouds.ecs.exceptions.ClientSideException;
import weloveclouds.ecs.exceptions.task.RetryableException;
import weloveclouds.ecs.models.commands.ICommand;

/**
 * Created by Benoit on 2016-11-19.
 */
public class InitializeNodeTask extends AbstractRetryableTask {
    public InitializeNodeTask(Builder taskBuilder) {
        super(taskBuilder.maxNumberOfRetries, taskBuilder.command, taskBuilder.successCommand,
                taskBuilder.failCommand);
    }

    @Override
    public void run() throws ClientSideException, RetryableException {
        try {
            this.command.execute();
        } catch (ClientSideException e) {
            if (numberOfAttempt <= maxNumberOfRetries) {
                numberOfAttempt++;
                throw new RetryableException("Retry attempt: " + numberOfAttempt + " on: " +
                        maxNumberOfRetries, e);
            } else {
                throw e;
            }
        }
    }

    public static class Builder {
        private int maxNumberOfRetries;
        private ICommand command;
        private ICommand successCommand;
        private ICommand failCommand;

        public Builder maxNumberOfRetries(int maxNumberOfRetries) {
            this.maxNumberOfRetries = maxNumberOfRetries;
            return this;
        }

        public Builder remoteCommandExeccution(ICommand command) {
            this.command = command;
            return this;
        }

        public Builder successCommand(ICommand successCommand) {
            this.successCommand = successCommand;
            return this;
        }

        public Builder failCommand(ICommand failCommand) {
            this.failCommand = failCommand;
            return this;
        }

        public InitializeNodeTask build() {
            return new InitializeNodeTask(this);
        }
    }
}
