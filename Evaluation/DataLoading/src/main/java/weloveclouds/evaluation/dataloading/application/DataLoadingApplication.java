package weloveclouds.evaluation.dataloading.application;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import weloveclouds.commons.context.ExecutionContext;
import weloveclouds.evaluation.dataloading.connection.ClientConnection;
import weloveclouds.evaluation.dataloading.connection.ClientConnectionFactory;
import weloveclouds.evaluation.dataloading.csvtraverser.CSVFolderTraverser;
import weloveclouds.evaluation.dataloading.util.Operation;

/**
 * The type Data loading application.
 * 
 * @author Benedek
 */
public class DataLoadingApplication {

    private static final Logger LOGGER = LogManager.getLogger(DataLoadingApplication.class);

    private static final int NUMBER_OF_CLI_ARGUMENTS = 4;
    private static final int INPUT_CSV_FOLDER_PATH_INDEX = 0;
    private static final int SERVER_IP_ADDRESS_INDEX = 1;
    private static final int SERVER_PORT_INDEX = 2;
    private static final int OPERATION_INDEX = 3;

    static {
        initializeRootLogger();
    }

    /**
     * Main.
     *
     * @param args the args
     */
    public static final void main(String[] args) {
        if (args.length == NUMBER_OF_CLI_ARGUMENTS) {
            try {
                String csvFolderPathStr = args[INPUT_CSV_FOLDER_PATH_INDEX];
                Path csvFolderPath = Paths.get(csvFolderPathStr);

                if (!isInputCSVFolderPathValid(csvFolderPath)) {
                    System.exit(0);
                } else {
                    String serverIp = args[SERVER_IP_ADDRESS_INDEX];
                    int serverPort = Integer.valueOf(args[SERVER_PORT_INDEX]);
                    Operation operation = Operation.valueOf(args[OPERATION_INDEX]);
                    
                    ClientConnection client =
                            ClientConnectionFactory.createDefaultClient(serverIp, serverPort);
                    CSVFolderTraverser traverser = new CSVFolderTraverser(client);

                    ExecutionContext.getExecutionEnvironmentFromArgs(args);
                    traverser.traverseFolder(csvFolderPath, operation);
                }
            } catch (InvalidPathException ex) {
                LOGGER.error("A valid folder path is required: <input_csv_folder_path>");
            }
        } else {
            LOGGER.error(
                    "Required arguments: <input_csv_folder_path> <server_ip_address> <server_port>");
        }
    }

    private static void initializeRootLogger() {
        try {
            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            URI configXmlUri = DataLoadingApplication.class.getClassLoader()
                    .getResource("config/log4j2.xml").toURI();
            context.setConfigLocation(configXmlUri);
            LOGGER.info("Root logger initialized successfully.");
        } catch (URISyntaxException ex) {
            System.err.println("Root logger was not correctly initialized!");
        }
    }

    private static boolean isInputCSVFolderPathValid(final Path inputCSVFolderPath) {
        File referenceToFolderPath = inputCSVFolderPath.toAbsolutePath().toFile();
        if (!referenceToFolderPath.exists() || !referenceToFolderPath.isDirectory()) {
            LOGGER.error("CSV folder path is not referring to an existing folder.");
            return false;
        }
        return true;
    }

}
