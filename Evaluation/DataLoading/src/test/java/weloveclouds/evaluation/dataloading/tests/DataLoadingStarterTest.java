package weloveclouds.evaluation.dataloading.tests;

import org.junit.Test;

import weloveclouds.evaluation.dataloading.application.DataLoadingApplication;

/**
 * The type Data loading starter test.
 */
public class DataLoadingStarterTest {

    /**
     * Start.
     */
    @SuppressWarnings("static-access")
    @Test
    public void start() {
        DataLoadingApplication application = new DataLoadingApplication();
        String csvRootFolderPath = "E:\\enron-dataset\\";
        application.main(new String[] {csvRootFolderPath});
    }

}
