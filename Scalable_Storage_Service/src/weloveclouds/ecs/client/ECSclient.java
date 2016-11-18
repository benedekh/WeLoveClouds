package weloveclouds.ecs.client;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import weloveclouds.cli.models.ParsedUserInput;
import weloveclouds.cli.utils.UserInputReader;
import weloveclouds.cli.utils.UserOutputWriter;
import weloveclouds.ecs.models.commands.EcsCommandFactory;

/**
 * ECS client, lets the admin(user) interact with the actual ECS service.
 * Reads, validates and executes input from the admin.
 * @author hb
 *
 */
public class ECSclient {


    private InputStream inputStream;
    private EcsCommandFactory commandFactory;

    private Logger logger;
    
    /**
     * @param inputStream from which it receives command from the user
     * @param commandFactory that processes (validate and execute) the various commands
     */
    public void ESCclient(InputStream inputStream, EcsCommandFactory ecsCommandFactory){
        this.inputStream = inputStream;
        this.commandFactory = ecsCommandFactory;
        this.logger = Logger.getLogger(getClass());
    }
    
    /**
     * Reads commands with arguments from the user via the {@link #inputStream}. After, it forwards
     * the respective command to the {@link #commandFactory} that will validate and execute it.
     */
    public void run(){
        //TODO: write run method.
    }
}
