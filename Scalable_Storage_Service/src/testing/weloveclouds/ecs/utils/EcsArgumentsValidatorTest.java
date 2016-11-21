package testing.weloveclouds.ecs.utils;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;


import weloveclouds.ecs.utils.ArgumentsValidator;
/**
 * 
 * @author hb
 *
 */
public class EcsArgumentsValidatorTest extends TestCase {
  //used for tests where the arguments presence matters more than its content.
    private static final String DUMMY_STR_ARG = "dummy"; 
    private static final String INVALID_NUM_OF_NODES = "1.5";
    private static final String VALID_NUM_OF_NODES = "10";
    private static final String VALID_CACHE_SIZE = "5";
    private static final String VALID_DISPLACEMENT_STRAT = "LRU";
    
    /**
     * This is the part where we test exception throwing.
     */
    public void testShouldThrowIfStartArgumentsGiven(){
        List<String> testArgument = Arrays.asList(DUMMY_STR_ARG);
        Exception exception = null;
        try{
            ArgumentsValidator.validateStartArguments(testArgument);
        } catch(IllegalArgumentException e){
            exception = e;
        }finally{
            assertTrue(exception instanceof IllegalArgumentException);
        }
    }
    
    public void testShouldThrowIfStopArgumentsGiven(){
        List<String> testArgument = Arrays.asList(DUMMY_STR_ARG);
        Exception exception = null;
        try{
            ArgumentsValidator.validateStopArguments(testArgument);
        } catch(IllegalArgumentException e){
            exception = e;
        }finally{
            assertTrue(exception instanceof IllegalArgumentException);
        }
    }
    
    public void testShouldThrowIfValidatingTooManyInitArguments(){
        List<String> testArgument = Arrays.asList(DUMMY_STR_ARG,
                                                  VALID_NUM_OF_NODES,
                                                  VALID_CACHE_SIZE,
                                                  VALID_DISPLACEMENT_STRAT);
        Exception exception = null;
        try{
            ArgumentsValidator.validateStartArguments(testArgument);
        } catch(IllegalArgumentException e){
            exception = e;
        }finally{
            assertTrue(exception instanceof IllegalArgumentException);
        }
    }
    
    public void testShouldThrowIfValidatingIncorrectNodeNumberInitArgument(){
        List<String> testArgument = Arrays.asList(DUMMY_STR_ARG,
                                                  VALID_CACHE_SIZE,
                                                  VALID_DISPLACEMENT_STRAT);
        Exception exception = null;
        try{
            ArgumentsValidator.validateStartArguments(testArgument);
        } catch(IllegalArgumentException e){
            exception = e;
        }finally{
            assertTrue(exception instanceof IllegalArgumentException);
        }
    }
    
    public void testShouldThrowIfValidatingIncorrectCacheSizeInitArgument(){
        List<String> testArgument = Arrays.asList(VALID_NUM_OF_NODES,
                                                  DUMMY_STR_ARG,
                                                  VALID_DISPLACEMENT_STRAT);
        Exception exception = null;
        try{
            ArgumentsValidator.validateStartArguments(testArgument);
        } catch(IllegalArgumentException e){
            exception = e;
        }finally{
            assertTrue(exception instanceof IllegalArgumentException);
        }
    }
    
    public void testShouldThrowIfValidatingIncorrectDispStratInitArgument(){
        List<String> testArgument = Arrays.asList(VALID_NUM_OF_NODES,
                                                  VALID_CACHE_SIZE,
                                                  DUMMY_STR_ARG);
        Exception exception = null;
        try{
            ArgumentsValidator.validateStartArguments(testArgument);
        } catch(IllegalArgumentException e){
            exception = e;
        }finally{
            assertTrue(exception instanceof IllegalArgumentException);
        }
    }    
    /**
     * testing valid input. No exceptions should be thrown
     */
    public void testShouldNotThrowIfValidatingCorrectInitArguments(){
        List<String> testArgument = Arrays.asList(VALID_NUM_OF_NODES,
                                                  VALID_CACHE_SIZE,
                                                  VALID_DISPLACEMENT_STRAT);

        ArgumentsValidator.validateInitServiceArguments(testArgument);
    }
}
