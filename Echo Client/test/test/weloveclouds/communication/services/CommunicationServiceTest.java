package test.weloveclouds.communication.services;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import weloveclouds.communication.SocketFactory;
import weloveclouds.communication.services.CommunicationService;

/**
 * @author Benoit
 */
@RunWith(MockitoJUnitRunner.class)
public class CommunicationServiceTest {
    private CommunicationService communicationService;

    @Mock
    SocketFactory socketFactory;

    @Before
    public void setUp(){
        communicationService = new CommunicationService(socketFactory);
    }
}
