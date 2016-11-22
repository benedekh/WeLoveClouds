package weloveclouds.evalution.dataloading.application;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import weloveclouds.evalution.dataloading.csvtraverser.CSVFolderTraverser;

public class DataLoadingApplication {

    private static final Logger LOGGER = LogManager.getLogger(DataLoadingApplication.class);

    private static final int NUMBER_OF_CLI_ARGUMENTS = 1;
    private static final int INPUT_CSV_FOLDER_PATH_INDEX = 0;

    static {
        initializeRootLogger();
    }

    public static final void main(String[] args) {
        if (args.length == NUMBER_OF_CLI_ARGUMENTS) {
            try {
                String csvFolderPathStr = args[INPUT_CSV_FOLDER_PATH_INDEX];
                Path csvFolderPath = Paths.get(csvFolderPathStr);

                if (!isInputCSVFolderPathValid(csvFolderPath)) {
                    System.exit(0);
                } else {
                    new CSVFolderTraverser().traverseFolder(csvFolderPath);
                }
            } catch (InvalidPathException ex) {
                LOGGER.error("A valid folder path is required: <input_csv_folder_path>");
            }
        } else {
            LOGGER.error("CSV folder path is required: <input_csv_folder_path>");
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
