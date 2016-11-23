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
    private static final List<String> DUMMY_LIST = Arrays.asList(DUMMY_STR_ARG);
    private static final List<String> EMPTY_LIST = Arrays.asList();
    
    /**
     * This is the part where we test exception throwing.
     */
    public void testShouldThrowIfStartGivenArguments(){
        Exception exception = null;
        try{
            ArgumentsValidator.validateStartArguments(DUMMY_LIST);
        } catch(IllegalArgumentException e){
            exception = e;
        }finally{
            assertTrue(exception instanceof IllegalArgumentException);
        }
    }
    
    public void testShouldThrowIfStopGivenArguments(){
        Exception exception = null;
        try{
            ArgumentsValidator.validateStopArguments(DUMMY_LIST);
        } catch(IllegalArgumentException e){
            exception = e;
        }finally{
            assertTrue(exception instanceof IllegalArgumentException);
        }
    }
    
    public void testShouldThrowIfValidatingTooManyInitArguments(){
        List<String> testArgument = Arrays.asList(VALID_NUM_OF_NODES,
                                                  VALID_CACHE_SIZE,
                                                  VALID_DISPLACEMENT_STRAT,
                                                  DUMMY_STR_ARG);
        Exception exception = null;
        try{
            ArgumentsValidator.validateInitServiceArguments(testArgument);
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
            ArgumentsValidator.validateInitServiceArguments(testArgument);
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
            ArgumentsValidator.validateInitServiceArguments(testArgument);
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
            ArgumentsValidator.validateInitServiceArguments(testArgument);
        } catch(IllegalArgumentException e){
            exception = e;
        }finally{
            assertTrue(exception instanceof IllegalArgumentException);
        }
    }
    
    public void testShouldThrowIfRemoveNodeGivenArguments(){
        Exception exception = null;
        try{
            ArgumentsValidator.validateRemoveNodeArguments(DUMMY_LIST);
        }catch (IllegalArgumentException e){
            exception = e;
        }finally{
            assertTrue(exception instanceof IllegalArgumentException);
        }
    }
    
    public void testShouldThrowIfShutdownGivenArguments(){
        Exception exception = null;
        try{
            ArgumentsValidator.validateShutdownArguments(DUMMY_LIST);
        }catch (IllegalArgumentException e){
            exception = e;
        }finally{
            assertTrue(exception instanceof IllegalArgumentException);
        }
    }
    
    public void testShouldThrowIfValidatingTooManyAddNodeArguments(){
        List<String> testArgument = Arrays.asList(VALID_CACHE_SIZE,
                                                  VALID_DISPLACEMENT_STRAT,
                                                  DUMMY_STR_ARG);
        Exception exception = null;
        try{
            ArgumentsValidator.validateAddNodeArguments(testArgument);
        }catch (IllegalArgumentException e){
            exception = e;
        }finally{
            assertTrue(exception instanceof IllegalArgumentException);
        }
    }
    
    public void testShouldThrowIfValidatingInvalidCacheSizeAddNodeArgument(){
        List<String> testArgument = Arrays.asList(DUMMY_STR_ARG, VALID_DISPLACEMENT_STRAT);
        Exception exception = null;
        try{
            ArgumentsValidator.validateAddNodeArguments(testArgument);
        }catch (IllegalArgumentException e){
            exception = e;
        }finally{
            assertTrue(exception instanceof IllegalArgumentException);
        }
    }
    
    public void testShouldThrowIfValidatingInvalidDispStratAddNodeArgument(){
        List<String> testArgument = Arrays.asList(VALID_CACHE_SIZE, DUMMY_STR_ARG);
        Exception exception = null;
        try{
            ArgumentsValidator.validateAddNodeArguments(testArgument);
        }catch (IllegalArgumentException e){
            exception = e;
        }finally{
            assertTrue(exception instanceof IllegalArgumentException);
        }
    }    
    /**
     * testing valid input. No exceptions should be thrown tests are considered to succeed if they
     * fall through.
     */
    public void testShouldNotThrowIfValidatingCorrectInitArguments(){
        List<String> testArgument = Arrays.asList(VALID_NUM_OF_NODES,
                                                  VALID_CACHE_SIZE,
                                                  VALID_DISPLACEMENT_STRAT);

        ArgumentsValidator.validateInitServiceArguments(testArgument);
    }
    
    public void testShouldNotThrowIfValidatingValidAddNodeArguments(){
        List<String> testArgument = Arrays.asList(VALID_CACHE_SIZE, VALID_DISPLACEMENT_STRAT);
        ArgumentsValidator.validateAddNodeArguments(testArgument);
    }  
    
    /**
     * contains calls to validation functions that do not require arguments
     */
    public void testShouldNotThrowIfNoArguments(){
        ArgumentsValidator.validateRemoveNodeArguments(EMPTY_LIST);
        ArgumentsValidator.validateShutdownArguments(EMPTY_LIST);
        ArgumentsValidator.validateStartArguments(EMPTY_LIST);
        ArgumentsValidator.validateStopArguments(EMPTY_LIST);
    }
}
