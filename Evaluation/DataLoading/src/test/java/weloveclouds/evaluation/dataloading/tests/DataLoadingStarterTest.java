package weloveclouds.evaluation.dataloading.tests;

import org.junit.Test;

import weloveclouds.evaluation.dataloading.application.DataLoadingApplication;

public class DataLoadingStarterTest {

    @SuppressWarnings("static-access")
    @Test
    public void start() {
        DataLoadingApplication application = new DataLoadingApplication();
        String csvRootFolderPath = "E:\\enron-dataset\\";
        application.main(new String[] {csvRootFolderPath});
    }

}
