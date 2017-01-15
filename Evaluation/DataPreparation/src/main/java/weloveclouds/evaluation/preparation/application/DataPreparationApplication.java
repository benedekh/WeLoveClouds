package weloveclouds.evaluation.preparation.application;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import weloveclouds.evaluation.preparation.transformer.DatasetTransformer;

/**
 * The type Data preparation application.
 */
public class DataPreparationApplication {

    private static final Logger LOGGER = LogManager.getLogger(DataPreparationApplication.class);

    private static final int NUMBER_OF_CLI_ARGUMENTS = 1;
    private static final int INPUT_DATASET_FOLDER_PATH_INDEX = 0;

    static {
        initializeRootLogger();
    }

    /**
     * Main.
     *
     * @param args the args
     */
    public static final void main(final String[] args) {
        if (args.length == NUMBER_OF_CLI_ARGUMENTS) {
            try {
                String datasetPathStr = args[INPUT_DATASET_FOLDER_PATH_INDEX];
                Path datasetFolderPath = Paths.get(datasetPathStr);

                if (!isInputDatasetPathValid(datasetFolderPath)
                        || !inputPathHasWritableParent(datasetFolderPath)) {
                    System.exit(0);
                } else {
                    new DatasetTransformer().transform(datasetFolderPath);
                }
            } catch (InvalidPathException ex) {
                LOGGER.error("A valid folder path is required: <input_dataset_folder_path>");
            }
        } else {
            LOGGER.error("Dataset folder path is required: <input_dataset_folder_path>");
        }
    }

    private static void initializeRootLogger() {
        try {
            LoggerContext context = (LoggerContext) LogManager.getContext(false);
            URI configXmlUri = DataPreparationApplication.class.getClassLoader()
                    .getResource("config/log4j2.xml").toURI();
            context.setConfigLocation(configXmlUri);
            LOGGER.info("Root logger initialized successfully.");
        } catch (URISyntaxException ex) {
            System.err.println("Root logger was not correctly initialized!");
        }
    }

    private static boolean isInputDatasetPathValid(final Path inputDatasetPath) {
        File referenceToFolderPath = inputDatasetPath.toAbsolutePath().toFile();
        if (!referenceToFolderPath.exists() || !referenceToFolderPath.isDirectory()) {
            LOGGER.error("Dataset folder path is not referring to an existing folder.");
            return false;
        }
        return true;
    }

    private static boolean inputPathHasWritableParent(final Path inputPath) {
        Path parentPath = inputPath.toAbsolutePath().getParent();
        if (parentPath == null) {
            LOGGER.error(
                    "Input path need to have a parent folder where the output CSVs will be saved.");
            return false;
        } else {
            try {
                File sample = new File(parentPath.toFile(), "empty.xyz");
                sample.createNewFile();
                sample.delete();
            } catch (IOException ex) {
                LOGGER.error(
                        "Input path's parent folder need to be writable, because the output CSVs will be saved there.");
                return false;
            }
        }
        return true;
    }


}
