package weloveclouds.evaluation.preparation.tests;

import org.junit.Test;

import weloveclouds.evaluation.preparation.application.DataPreparationApplication;

/**
 * The type Data transformation starter test.
 */
public class DataTransformationStarterTest {

    /**
     * Start.
     */
    @SuppressWarnings("static-access")
    @Test
    public void start() {
        DataPreparationApplication application = new DataPreparationApplication();
        String datasetRootFolderPath = "E:\\enron-dataset\\maildir\\";
        application.main(new String[] {datasetRootFolderPath});
    }

}
