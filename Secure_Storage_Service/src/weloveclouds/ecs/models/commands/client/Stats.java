package weloveclouds.ecs.models.commands.client;

import java.io.IOException;

import weloveclouds.commons.cli.utils.UserOutputWriter;
import weloveclouds.commons.exceptions.ClientSideException;
import weloveclouds.commons.utils.StringUtils;
import weloveclouds.ecs.api.IKVEcsApi;
import weloveclouds.ecs.models.commands.ICommand;
import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.models.stats.EcsStatistics;
import weloveclouds.ecs.utils.ArgumentsValidator;

/**
 * Created by Benoit on 2017-01-18.
 */
public class Stats extends AbstractEcsClientCommand {
    public Stats(IKVEcsApi externalCommunicationServiceApi, String[] arguments) {
        super(externalCommunicationServiceApi, arguments);
    }

    @Override
    public void execute() throws ClientSideException {

        try {
            EcsStatistics ecsStatistics = externalCommunicationServiceApi.getStats();
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("\n--------------------        ECS       --------------------\n");
            stringBuffer.append(" Status: " + ecsStatistics.getStatus() + "\n");
            stringBuffer.append("--------------------  LOAD  BALANCER " +
                    " --------------------\n");
            if (ecsStatistics.getLoadBalancer() != null) {
                stringBuffer.append(StringUtils.join(" ",
                        ecsStatistics.getLoadBalancer().toString(), "\n"));
            }
            stringBuffer.append("--------------------   RUNNING NODES   --------------------\n");
            for (StorageNode node : ecsStatistics.getRunningNodes()) {
                stringBuffer.append(" " + node.toString() + "\n");
            }
            stringBuffer.append("-------------------- INITIALIZED NODES --------------------\n");
            for (StorageNode node : ecsStatistics.getInitializedNodes()) {
                stringBuffer.append(" " + node.toString() + "\n");
            }
            stringBuffer.append("--------------------    IDLED NODES    --------------------\n");
            for (StorageNode node : ecsStatistics.getIdledNodes()) {
                stringBuffer.append(" " + node.toString() + "\n");
            }
            stringBuffer.append("--------------------    ERROR NODES    --------------------\n");
            for (StorageNode node : ecsStatistics.getErrorNodes()) {
                stringBuffer.append(" " + node.toString() + "\n");
            }
            UserOutputWriter.getInstance().writeLine(stringBuffer.toString());
        } catch (IOException ex) {
            //Log
        }
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public ICommand validate() throws IllegalArgumentException {
        ArgumentsValidator.validateStatsArguments(arguments);
        return this;
    }
}
