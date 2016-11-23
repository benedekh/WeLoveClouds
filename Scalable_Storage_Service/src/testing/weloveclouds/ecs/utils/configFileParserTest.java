package testing.weloveclouds.ecs.utils;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import weloveclouds.ecs.models.repository.StorageNode;
import weloveclouds.ecs.utils.ConfigurationFileParser;

/**
 * 
 * @author hb
 *  note that within the test files there are no invalid ip numbers
 *  such as 256.255.255.255 as the parser doesn't check the actual validity of the data, just
 *  that it is well formed.
 */
public class configFileParserTest extends TestCase{
    /*  We'll be able to tell if these tests were successful or not by inspecting the logfile.
        and by asserting the length of List<StorageNode> var */
    private static String MALFORMED_FILE = "../../../resources/malformed_data_test.config";
    private static String WELL_FORMED_FILE = "../../../resources/well_formed_data_test.config";
    private static int EXPECTED_LENGTH_WELL_FORMED = 4;
    private List<StorageNode> node_list = null;
    
    public void testShouldLogExceptionsOnInvalidFileParse(){
        File testFile = null;
        try {
            testFile = new File(this.getClass().getResource(MALFORMED_FILE).toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            fail();
        }    
        ConfigurationFileParser configFileParser = new ConfigurationFileParser();
        node_list = configFileParser.parse(testFile);
        System.out.println(node_list.size());
        //if it fails, the last element will be null.
    }
    
    public void testShouldNotLogExceptionsOnValidFileParse(){
        File testFile = null;
        try {
            testFile = new File(this.getClass().getResource(WELL_FORMED_FILE).toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            fail();
        }   
        ConfigurationFileParser configFileParser = new ConfigurationFileParser();
        List<StorageNode> node_list = configFileParser.parse(testFile);
        System.out.println(node_list.size());
        assertTrue(node_list.size() == EXPECTED_LENGTH_WELL_FORMED);
    }

}
