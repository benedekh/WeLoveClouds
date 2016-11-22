import org.junit.Test;

import weloveclouds.evaluation.preparation.application.DataPreparationApplication;

public class DataTransformationStarterTest {

    @SuppressWarnings("static-access")
    @Test
    public void start() {
        DataPreparationApplication application = new DataPreparationApplication();
        String datasetRootFolderPath = "E:\\enron-dataset\\maildir\\";
        application.main(new String[] {datasetRootFolderPath});
    }

}
