package weloveclouds.commons.communication;

import java.io.IOException;

import weloveclouds.communication.api.ICommunicationApi;
import weloveclouds.ecs.models.tasks.Status;

public interface IPacketResendStrategy {

    void configure(int attemptNumber, ICommunicationApi communicationApi, byte[] packet);

    Status getExecutionStatus();

    IOException getException();

    byte[] getResponse();

    void tryAgain();

    void incrementNumberOfAttempts();
}
